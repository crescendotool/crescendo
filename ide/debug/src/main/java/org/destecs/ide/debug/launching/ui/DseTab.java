package org.destecs.ide.debug.launching.ui;

import java.util.List;
import java.util.Vector;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
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
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class DseTab extends AbstractLaunchConfigurationTab
{
	class WidgetListener implements ModifyListener, SelectionListener
	{

		public void modifyText(ModifyEvent e)
		{
			// if(!suspended)
			{
				// validatePage();
				updateLaunchConfigurationDialog();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			// if(!suspended)
			{
				/* do nothing */
			}
		}

		public void widgetSelected(SelectionEvent e)
		{
			// if(!suspended)
			{
				// fOperationText.setEnabled(!fdebugInConsole.getSelection());

				updateLaunchConfigurationDialog();
			}
		}
	}

	
	private final WidgetListener fListener = new WidgetListener();
	private Text fArchitecturePathText;
	private Button selectArchitecturePathButton;
	
	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_COMMON_TAB);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		
		
		Group group = new Group(comp, comp.getStyle());
		group.setText("Controller Architecture");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		group.setLayout(layout);
		
//		Label label = new Label(group, SWT.MIN);
//		label.setText("DE Replace pattern (A/B):");
//		gd = new GridData(GridData.BEGINNING);
//		label.setLayoutData(gd);
//
//		replacePattern = new Text(group, SWT.SINGLE | SWT.BORDER);
//
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		replacePattern.setLayoutData(gd);
//		replacePattern.addModifyListener(fListener);
		

		Label label = new Label(group, SWT.MIN);
		label.setText("Architecture:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fArchitecturePathText = new Text(group, SWT.SINGLE | SWT.BORDER|SWT.READ_ONLY);

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
				class ScenarioContentProvider extends
						BaseWorkbenchContentProvider
				{
					@Override
					public boolean hasChildren(Object element)
					{
						if (element instanceof IProject)
						{
							return super.hasChildren(element);
						} else
						{
							return super.hasChildren(element);
						}
					}

					@SuppressWarnings("unchecked")
					@Override
					public Object[] getElements(Object element)
					{
						List elements = new Vector();
						Object[] arr = super.getElements(element);
						if (arr != null)
						{
							for (Object object : arr)
							{
								if (object instanceof IFile)
								{
									IFile f = (IFile) object;
									if (f.getFullPath().getFileExtension().equals("arch"))
									{
										elements.add(f);
									}
								}
							}
							return elements.toArray();
						}
						return null;
					}

				}
				;
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new ScenarioContentProvider());
				dialog.setTitle("Architecture Selection");
				dialog.setMessage("Select an architecture:");
				dialog.setComparator(new ViewerComparator());
				for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
				{
					if (tab instanceof CoSimLaunchConfigurationTab)
					{
						CoSimLaunchConfigurationTab cosimLaunchTab = (CoSimLaunchConfigurationTab) tab;
						IProject project = cosimLaunchTab.getProject();
						if (project != null)
						{
				dialog.setInput(project.getFolder("dse"));
						}
					}
				}
				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null
					// && dialog.getFirstResult() instanceof IProject
					// && ((IProject) dialog.getFirstResult()).getAdapter(IVdmProject.class) != null)
					)
					{
						fArchitecturePathText.setText(((IFile) dialog.getFirstResult()).getProjectRelativePath().toString());
						
					}

				}
			}
		});

		
//		label = new Label(group, SWT.MIN);
//		label.setText("CT Simulator URL:");
//		gd = new GridData(GridData.BEGINNING);
//		label.setLayoutData(gd);
//
//		ctUrl = new Text(group, SWT.SINGLE | SWT.BORDER);
//
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		ctUrl.setLayoutData(gd);
//		ctUrl.addModifyListener(fListener);
	}

	public String getName()
	{
		return "DSE";
	}
	
	@Override
	public String getId()
	{
		return "org.destecs.ide.debug.launching.ui.DseTab";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{	
			fArchitecturePathText.setText( configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, ""));
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Error fetching dse from launch configuration", e);
		}
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, fArchitecturePathText.getText());
//		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, ctUrl.getText());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE,"");
//		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, IDebugConstants.DEFAULT_CT_ENDPOINT);
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);
//		try
//		{
//			if(deUrl.getText().length()<=0)
//			{
//				setErrorMessage("DE URL not set");
//				return false;
//			}
//			new URL(deUrl.getText());
//		} catch (Exception e)
//		{
//			setErrorMessage("DE URL not valid");
//		}
//		
//		try
//		{
//			if(ctUrl.getText().length()<=0)
//			{
//				setErrorMessage("CT URL not set");
//				return false;
//			}
//			new URL(ctUrl.getText());
//		} catch (Exception e)
//		{
//			setErrorMessage("CT URL not valid");
//		}
//		
//		
		return super.isValid(launchConfig);
	}

}
