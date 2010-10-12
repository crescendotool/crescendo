package org.destecs.ide.debug.launching.ui;

import java.util.List;
import java.util.Vector;


import org.destecs.ide.debug.IDebugConstants;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;




public class coSimLaunchConfigurationTab extends AbstractLaunchConfigurationTab implements ILaunchConfigurationTab {

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			// validatePage();
			updateLaunchConfigurationDialog();
			searchModels();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			/* do nothing */
		}

		public void widgetSelected(SelectionEvent e)
		{
			// fOperationText.setEnabled(!fdebugInConsole.getSelection());

			updateLaunchConfigurationDialog();
		}
	}

	
	private Text fProjectText;
	
	private Text ctPath = null;
	private Text dtPath = null;
	private Text contractPath = null;
	private Text scenarioPath = null;
	private Text sharedDesignParamPath = null;
	private double totalSimulationTime = 5;
	Button selectScenarioButton;

	private IProject project = null;
	private Text simulationTimeText = null;
	
	
	final List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();
	
	
	private WidgetListener fListener = new WidgetListener();

	private Text fScenarioText;
	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_COMMON_TAB);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		createProjectSelection(comp);
		createPathsSelection(comp);
		createSimConfig(comp);
	}
	
	
	private void createSimConfig(Composite parent){
		Group group = new Group(parent, parent.getStyle());
		group.setText("Simulation Configuration");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		// editParent = group;

		Label label = new Label(group, SWT.MIN);
		label.setText("Scenario:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fScenarioText = new Text(group, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fScenarioText.setLayoutData(gd);
		fScenarioText.addModifyListener(fListener);

		selectScenarioButton = createPushButton(group, "Browse...", null);
		selectScenarioButton.setEnabled(false);
		selectScenarioButton.addSelectionListener(
				new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// ListSelectionDialog dlg = new ListSelectionDialog(getShell(),
				// ResourcesPlugin.getWorkspace().getRoot(), new
				// BaseWorkbenchContentProvider(), new
				// WorkbenchLabelProvider(), "Select the Project:");
				// dlg.setTitle("Project Selection");
				// dlg.open();
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
								if(object instanceof IFile){
									IFile f = (IFile) object;
									if(f.getFullPath().getFileExtension().equals("script"))
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
				dialog.setTitle("Scenario Selection");
				dialog.setMessage("Select a scenario:");
				dialog.setComparator(new ViewerComparator());
				
				
				
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot().getProject(fProjectText.getText()).getFolder("scenarios"));

				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null
							//&& dialog.getFirstResult() instanceof IProject
							//&& ((IProject) dialog.getFirstResult()).getAdapter(IVdmProject.class) != null)
							)
					{
						fScenarioText.setText(((IFile)dialog.getFirstResult()).getLocationURI().getPath());
					}

				}
			}
		});
		
		// Total simulation time Line
		Label simulationTimeLabel = new Label(group, SWT.NONE);
		simulationTimeLabel.setText("Total simulation time:");
		simulationTimeText = new Text(group, SWT.BORDER);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		simulationTimeText.setLayoutData(gd);
		simulationTimeText.setText("5");

		

		// warningLabel = new Label(parent, SWT.NONE);
		simulationTimeText.addListener(SWT.Modify, new Listener()
		{
			public void handleEvent(Event event)
			{
				try
				{
					totalSimulationTime = new Double(simulationTimeText.getText());
					
				} catch (Exception e)
				{
					//runButton.setEnabled(false);
				}
			}
		});
	}
	
	private void createPathsSelection(Composite parent){
		Group group = new Group(parent, parent.getStyle());
		group.setText("Simulation Model Paths");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 1;
		group.setLayout(layout);

		parent.setLayout(new GridLayout(1, false));
		
		// DT Line
		Label dtLabel = new Label(group, SWT.NONE);
		dtLabel.setText("DT Path:");
		dtPath = new Text(group, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		dtPath.setLayoutData(gridData);
		dtPath.setText("Insert DT model path here");
		dtPath.setEditable(false);
		
		// CT Line
		Label ctLabel = new Label(group, SWT.NONE);
		ctLabel.setText("CT Path:");
		ctPath = new Text(group, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		ctPath.setLayoutData(gridData);
		ctPath.setText("Insert CT model path here");
		ctPath.setEditable(false);

		// Contract Line
		Label contractLabel = new Label(group, SWT.NONE);
		contractLabel.setText("Contract Path:");
		contractPath = new Text(group, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		contractPath.setLayoutData(gridData);
		contractPath.setText("Insert Contract path here");
		contractPath.setEditable(false);
		
		// Shared Design Parameters Line
		Label sharedDesignParamLabel = new Label(group, SWT.NONE);
		sharedDesignParamLabel.setText("Shared Design Parameters Path:");
		sharedDesignParamPath = new Text(group, SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		sharedDesignParamPath.setLayoutData(gridData);
		//sharedDesignParamPath.setText("Insert Shared Design Parameters path here");
		sharedDesignParamPath.setEditable(false);
		//sharedDesignParamPath.setEnabled(false);

//		// Scenario Line
//		Label scenarioLabel = new Label(group, SWT.NONE);
//		scenarioLabel.setText("Scenario Path:");
//		scenarioPath = new Text(group, SWT.BORDER);
//		gridData = new GridData();
//		gridData.horizontalAlignment = SWT.FILL;
//		gridData.grabExcessHorizontalSpace = true;
//		scenarioPath.setLayoutData(gridData);
//		scenarioPath.setText("Insert Scenario path here");

	

		
	}
	
	
	
	private void createProjectSelection(Composite parent)
	{
		Group group = new Group(parent, parent.getStyle());
		group.setText("Project");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		// editParent = group;

		Label label = new Label(group, SWT.MIN);
		label.setText("Project:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fProjectText = new Text(group, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjectText.setLayoutData(gd);
		fProjectText.addModifyListener(fListener);

		Button selectProjectButton = createPushButton(group, "Browse...", null);

		selectProjectButton.addSelectionListener(
				new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// ListSelectionDialog dlg = new ListSelectionDialog(getShell(),
				// ResourcesPlugin.getWorkspace().getRoot(), new
				// BaseWorkbenchContentProvider(), new
				// WorkbenchLabelProvider(), "Select the Project:");
				// dlg.setTitle("Project Selection");
				// dlg.open();
				class ProjectContentProvider extends
						BaseWorkbenchContentProvider
				{
					@Override
					public boolean hasChildren(Object element)
					{
						if (element instanceof IProject)
						{
							return false;
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
								if (object instanceof IProject)
										//&& (((IProject) object).getAdapter(IVdmProject.class) != null)
										//&& isSupported((IProject) object))
								{
									elements.add(object);
								}
							}
							return elements.toArray();
						}
						return null;
					}

				}
				;
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new ProjectContentProvider());
				dialog.setTitle("Project Selection");
				dialog.setMessage("Select a project:");
				dialog.setComparator(new ViewerComparator());

				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null
							&& dialog.getFirstResult() instanceof IProject
							//&& ((IProject) dialog.getFirstResult()).getAdapter(IVdmProject.class) != null)
							)
					{
						fProjectText.setText(((IProject) dialog.getFirstResult()).getName());
						selectScenarioButton.setEnabled(true);
					}

				}
			}
		});
	}
	
	
	private void searchModels()
	{
		if(fProjectText.getText().equals(""))
		{
			return;
		}
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = wsRoot.getProject(fProjectText.getText());
		if (project == null)
		{
			// Show error
			return;
		}

		try
		{
			IResource[] projectMembers = project.members();
			for (IResource iResource : projectMembers)
			{
				if (iResource instanceof IFolder)
				{
					IFolder folder = (IFolder) iResource;
					String fName = folder.getName();
					if (fName.equals("model"))
					{
						dtPath.setText(folder.getLocationURI().getPath());
					} 
//					else if (fName.equals("scenarios"))
//					{
//						for (IResource scenarioSub : folder.members())
//						{
//							if (scenarioSub instanceof IFile)
//							{
//								IFile file = (IFile) scenarioSub;
//								if (file.getFileExtension().equals("script"))
//								{
//									scenarioPath.setText(file.getLocationURI().getPath());
//								}
//							}
//						}
//					}
				} else if (iResource instanceof IFile)
				{
					IFile file = (IFile) iResource;
					if (file.getFileExtension().equals("csc"))
					{
						contractPath.setText(file.getLocationURI().getPath());
					} else if (file.getFileExtension().equals("emx"))
					{
						ctPath.setText(file.getLocationURI().getPath());
					} else if (file.getFileExtension().equals("sdp"))
					{
						sharedDesignParamPath.setText(file.getLocationURI().getPath());
					}
				}

			}

		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
		
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			fProjectText.setText(configuration.getAttribute(IDebugConstants.PROJECT_NAME,""));
			if(fProjectText.getText().equals(""))
			{

				return;
			}
			
			ctPath.setText(configuration.getAttribute(IDebugConstants.CT_MODEL_PATH,"No Path Selected"));
			dtPath.setText(configuration.getAttribute(IDebugConstants.DE_MODEL_PATH,"No Path Selected"));
			contractPath.setText(configuration.getAttribute(IDebugConstants.CONTRACT_PATH,"No Path Selected"));
			simulationTimeText.setText(configuration.getAttribute(IDebugConstants.SIMULATION_TIME,"0"));
			fScenarioText.setText(configuration.getAttribute(IDebugConstants.SCENARIO_PATH,"No Scenario Selected"));
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IDebugConstants.PROJECT_NAME, fProjectText.getText());
		configuration.setAttribute(IDebugConstants.CT_MODEL_PATH, ctPath.getText());
		configuration.setAttribute(IDebugConstants.DE_MODEL_PATH, dtPath.getText());
		configuration.setAttribute(IDebugConstants.CONTRACT_PATH,contractPath.getText() );
		configuration.setAttribute(IDebugConstants.SIMULATION_TIME,simulationTimeText.getText() );
		configuration.setAttribute(IDebugConstants.SCENARIO_PATH,fScenarioText.getText());
		configuration.setAttribute(IDebugConstants.SHARED_DESIGN_PARAM_PATH,sharedDesignParamPath.getText());
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if(fProjectText.equals("")){
			return false;
		}
		
		if(fScenarioText.equals(""))
		{
			return false;
		}
		
		try{
			Double.parseDouble(simulationTimeText.getText());
		}catch(NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}

	
	public String getName() {
		return "Co-Sim Launcher";
	}

	
	

}
