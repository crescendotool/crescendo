/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.ide.debug.launching.ui;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.destecs.core.parsers.IError;
import org.destecs.core.parsers.SubsParserWrapper;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.launching.ui.internal.TreeNodeContainer;
import org.destecs.ide.debug.launching.ui.internal.VdmLogTreeContentProvider;
import org.destecs.ide.debug.launching.ui.internal.VdmLogTreeLabelProvider;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;

public class VdmTab extends AbstractLaunchConfigurationTab implements ICheckStateProvider
{

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		public void widgetSelected(SelectionEvent e)
		{
			updateLaunchConfigurationDialog();
		}
	}

	protected WidgetListener fListener = new WidgetListener();
	private Button checkBoxUsePostChecks = null;
	private Button checkBoxUsePreChecks = null;
	private Button checkBoxInvChecks = null;
	private Button checkBoxDynamicTypeChecks = null;
	private Button checkBoxUseMeasure = null;
	private Button checkBoxUseCoverage = null;
	private Button checkBoxUseLogRt = null;
	private Button checkBoxTimingInvariants = null;


	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		createInterperterGroupCheckGroup(comp);
		createLogTreeGroup(comp);

		createFaultField(comp);
		
		createArchitectureGroup(comp);
	}
	
	private Text fArchitecturePathText;
	private Button selectArchitecturePathButton;
	private Button removeArchitectureButton;
	private Button createFolderButton;
	private Label warningLabel;
	
	private void createArchitectureGroup(Composite comp)
	{
		
		Group group = new Group(comp, comp.getStyle());
		group.setText("Controller Architecture");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		group.setLayout(layout);

		createArchitectureFolderGroup(group);
		
		
		Label label = new Label(group, SWT.MIN);
		label.setText("Architecture:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fArchitecturePathText = new Text(group, SWT.SINGLE | SWT.BORDER
				| SWT.READ_ONLY);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fArchitecturePathText.setLayoutData(gd);
		fArchitecturePathText.addModifyListener(fListener);

		selectArchitecturePathButton = createPushButton(group, "Browse...", null);
		selectArchitecturePathButton.setEnabled(true);
		selectArchitecturePathButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
				dialog.setTitle("Architecture Selection");
				dialog.setMessage("Select an architecture:");
				dialog.setComparator(new ViewerComparator());
				dialog.addFilter(new FileExtensionFilter(true,"arch"));
				dialog.setValidator(new ISelectionStatusValidator()
				{
					
					public IStatus validate(Object[] selection)
					{
						if (selection.length == 1 && selection[0] instanceof IFile)
						{
							return Status.OK_STATUS;
						}
						else return new Status(IStatus.ERROR,DestecsDebugPlugin.PLUGIN_ID,"Invalid selection. Selection must be a file.");
							
					}
				});

				for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
				{
					if (tab instanceof CoSimLaunchConfigurationTab)
					{
						CoSimLaunchConfigurationTab cosimLaunchTab = (CoSimLaunchConfigurationTab) tab;
						IProject project = cosimLaunchTab.getProject();
						if (project != null)
						{
							dialog.setInput(project.getFolder("model_de/architectures"));
						}
					}
				}
				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null)
					{
						fArchitecturePathText.setText(((IFile) dialog.getFirstResult()).getProjectRelativePath().toString());

					}

				}
			}
		});

		removeArchitectureButton = createPushButton(group, "Remove", null);

		removeArchitectureButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fArchitecturePathText.setText("");
			}
		});
		
	}
	
	private void createArchitectureFolderGroup(Composite comp) {
		Group group = new Group(comp, comp.getStyle());
		group.setText("Architectures folder (\"model_de/architectures\")");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan =2 ;
		group.setLayoutData(gd);
		group.setLayout(new GridLayout(2, false));
		
		
		createFolderButton = createPushButton(group, "Create Architectures Folder", null);
		createFolderButton.setEnabled(false);
		createFolderButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				IProject project = getProject();
				if(project == null)
				{
					//this should not happen
					System.out.println("this should not happen");
				}
				else
				{
					boolean folderCreated = false;
					IPath architecturesPath = project.getLocation().append("model_de/architectures");
					File architecturesFile = architecturesPath.toFile();
					if(true)
					{
						folderCreated = architecturesFile.mkdirs();
						try {
							project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
						} catch (CoreException e1) {
							DestecsDebugPlugin.logError("Failed to refresh project", e1);
						}
					}
					
					if(folderCreated)
					{
						createFolderButton.setEnabled(false);
						warningLabel.setText("Folder present");	
					}
					
				}
				
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println("widgetDefaultSelected");
				
			}
		});
		
		warningLabel = new Label(group, SWT.MIN);
		
		
	}
	
	private Text replacePattern = null;
	private CheckboxTreeViewer logTreeViewer;
	private List<String> selectedLogVariables;
	
	public void createFaultField(Composite comp)
	{
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(comp.getFont());

		Group group = new Group(comp, comp.getStyle());
		group.setText("Faults");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		group.setLayout(layout);

		Label label = new Label(group, SWT.MIN);
		label.setText("DE Replace pattern (A/B):");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		replacePattern = new Text(group, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		replacePattern.setLayoutData(gd);
		replacePattern.addModifyListener(fListener);

	}
	
	private void createLogTreeGroup(Composite comp)
	{
		Group group = new Group(comp, SWT.NONE);
		group.setText("Log");
		GridData gd = new GridData(GridData.FILL_BOTH);

		group.setLayoutData(gd);
		group.setLayout(new GridLayout(1, true));
		
		logTreeViewer = new CheckboxTreeViewer(group, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data = new GridData(GridData.FILL_BOTH);
		logTreeViewer.getControl().setLayoutData(data);
		
		logTreeViewer.setContentProvider(new VdmLogTreeContentProvider());
		logTreeViewer.setLabelProvider(new VdmLogTreeLabelProvider());
		logTreeViewer.setCheckStateProvider(this);
		
	}
	

	

	void createInterperterGroupCheckGroup(Composite controlGroup)
	{
		Group interperterGroup = new Group(controlGroup, SWT.NONE);
		interperterGroup.setText("Interpreting");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		interperterGroup.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		interperterGroup.setLayout(layout);

		checkBoxDynamicTypeChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxDynamicTypeChecks.setText("Dynamic type checks");
		checkBoxDynamicTypeChecks.addSelectionListener(fListener);

		checkBoxInvChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxInvChecks.setText("Invariants checks");
		checkBoxInvChecks.addSelectionListener(fListener);

		checkBoxUsePreChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxUsePreChecks.setText("Pre condition checks");
		checkBoxUsePreChecks.addSelectionListener(fListener);

		checkBoxUsePostChecks = new Button(interperterGroup, SWT.CHECK);
		checkBoxUsePostChecks.setText("Post condition checks");
		checkBoxUsePostChecks.addSelectionListener(fListener);

		checkBoxUseMeasure = new Button(interperterGroup, SWT.CHECK);
		checkBoxUseMeasure.setText("Measure Run-Time checks");
		checkBoxUseMeasure.addSelectionListener(fListener);

		checkBoxUseCoverage = new Button(interperterGroup, SWT.CHECK);
		checkBoxUseCoverage.setText("Generate Coverage");
		checkBoxUseCoverage.addSelectionListener(fListener);

		checkBoxUseLogRt = new Button(interperterGroup, SWT.CHECK);
		checkBoxUseLogRt.setText("Log Real-Time Events");
		checkBoxUseLogRt.addSelectionListener(fListener);
		
		checkBoxTimingInvariants = new Button(interperterGroup, SWT.CHECK);
		checkBoxTimingInvariants.setText("Check timing invariants");
		checkBoxTimingInvariants.addSelectionListener(fListener);

	}

	public String getName()
	{
		return "VDM";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			checkBoxDynamicTypeChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, true));
			checkBoxInvChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, true));
			checkBoxUsePostChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, true));
			checkBoxUsePreChecks.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, true));
			checkBoxUseMeasure.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, true));
			checkBoxUseCoverage.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_GENERATE_COVERAGE, true));
			checkBoxUseLogRt.setSelection(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_LOG_RT, true));
			checkBoxTimingInvariants.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_RT_VALIDATION, false));
			parseConfigValue(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_VDM_LOG_VARIABLES, ""));
			fArchitecturePathText.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, ""));
			removeArchitectureButton.setEnabled(!fArchitecturePathText.getText().isEmpty());
			String url = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_REPLACE, "");
			replacePattern.setText(url);
		} catch (CoreException e)
		{
			if (DestecsDebugPlugin.DEBUG)
			{
				DestecsDebugPlugin.log(new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID, "Error in vdmruntimechecks launch configuration tab", e));
			}
		}

		IProject project = getProject();
		if (project == null)
		{
			return;
		}
		IVdmProject p = (IVdmProject) project.getAdapter(IVdmProject.class);

		if (p != null)
		{
			IVdmModel model = p.getModel();
			logTreeViewer.setInput(model);
			logTreeViewer.expandAll();
			logTreeViewer.collapseAll();
		}
		
		IResource architecturesDirectory = project.findMember("model_de/architectures");
		if(architecturesDirectory == null)
		{
			warningLabel.setText("Folder not found");
			createFolderButton.setEnabled(true);
		}
		else
		{
			warningLabel.setText("Folder present");
			createFolderButton.setEnabled(false);
			
		}
		
	}

	private void parseConfigValue(String attribute)
	{
		List<String> result = new Vector<String>();
		String[] splitAttribute = attribute.split(",");
		
		for (String string : splitAttribute)
		{
			result.add(string);
		}
		
		this.selectedLogVariables = result;
	}

	public IProject getProject()
	{
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof CoSimLaunchConfigurationTab)
			{
				return ((CoSimLaunchConfigurationTab) tab).getProject();
			}
		}
		return null;
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, checkBoxDynamicTypeChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, checkBoxInvChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, checkBoxUsePostChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, checkBoxUsePreChecks.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, checkBoxUseMeasure.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_GENERATE_COVERAGE, checkBoxUseCoverage.getSelection());
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_LOG_RT, checkBoxUseLogRt.getSelection());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_RT_VALIDATION, checkBoxTimingInvariants.getSelection());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_VDM_LOG_VARIABLES, getConfigString(getConfigValues()));
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, fArchitecturePathText.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_REPLACE, replacePattern.getText());
	}

	private List<String> getConfigValues()
	{
		
		Object[] checkedElements = logTreeViewer.getCheckedElements();
		Object[] grayedElements = logTreeViewer.getGrayedElements();
		
		Set<Object> elements = new HashSet<Object>(Arrays.asList(checkedElements));
		elements.removeAll(Arrays.asList(grayedElements));
		
		List<String> result = new Vector<String>();

		for (Object object : elements)
		{
			result.add(object.toString());
		}
		return result;
	}
	
	private String getConfigString(List<String> values)
	{
		StringBuilder sb = new StringBuilder();
		if (values.size() > 0)
		{
			for (Object object : values)
			{
				sb.append(object.toString());
				sb.append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DTC_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_INV_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_POST_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PRE_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_MEASURE_CHECKS, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_GENERATE_COVERAGE, true);
		configuration.setAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_LOG_RT, true);
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_RT_VALIDATION, false);
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, "");
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_REPLACE, "");
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);
		if (replacePattern.getText().trim().length() > 0)
		{
			try
			{
				SubsParserWrapper parser = new SubsParserWrapper();
				parser.parse(new File("argument"), replacePattern.getText());
				for (IError errorMessage : parser.getErrors())
				{
					setErrorMessage(errorMessage.toString());
					break;
				}
			} catch (Exception e)
			{
				DestecsDebugPlugin.logError("Error while running subs parser to validate replace pattern", e);
			}
		}

		return super.isValid(launchConfig);
	}

	public boolean isChecked(Object element)
	{
		if(element instanceof TreeNodeContainer)
		{
			TreeNodeContainer tNode = (TreeNodeContainer) element;
			return selectedLogVariables.contains(tNode.toString()) || tNode.isVirtual;
		}
		return false;
	}

	public boolean isGrayed(Object element)
	{
		if(element instanceof TreeNodeContainer)
		{
			TreeNodeContainer tNode = (TreeNodeContainer) element;
			return tNode.isVirtual;
		}
		return false;
	}
}
