package org.destecs.ide.debug.launching.ui;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class CoSimLaunchConfigurationTab extends AbstractLaunchConfigurationTab
		implements ILaunchConfigurationTab
{

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public boolean suspended = false;

		public void modifyText(ModifyEvent e)
		{
			if (!suspended)
			{
				// validatePage();
				updateLaunchConfigurationDialog();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			if (!suspended)
			{
				/* do nothing */
			}
		}

		public void widgetSelected(SelectionEvent e)
		{
			if (!suspended)
			{
				// fOperationText.setEnabled(!fdebugInConsole.getSelection());

				updateLaunchConfigurationDialog();
			}
		}
	}

	private Text fProjectText;
	private Text ctPath = null;
	private Text dtPath = null;
//	private Text contractPath = null;
	// private Text scenarioPath = null;
//	private Text fSharedDesignParamPath = null;
	// private double totalSimulationTime = 5;
	Button selectScenarioButton;

	// private IProject project = null;
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

	private void createSimConfig(Composite parent)
	{
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

		fScenarioText = new Text(group, SWT.SINGLE | SWT.BORDER|SWT.READ_ONLY);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fScenarioText.setLayoutData(gd);
		fScenarioText.addModifyListener(fListener);

		selectScenarioButton = createPushButton(group, "Browse...", null);
		selectScenarioButton.setEnabled(false);
		selectScenarioButton.addSelectionListener(new SelectionAdapter()
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
									if (f.getFullPath().getFileExtension().equals("script"))
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
					// && dialog.getFirstResult() instanceof IProject
					// && ((IProject) dialog.getFirstResult()).getAdapter(IVdmProject.class) != null)
					)
					{
						fScenarioText.setText(((IFile) dialog.getFirstResult()).getLocationURI().getPath());
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
		simulationTimeText.addModifyListener(fListener);
		simulationTimeText.addListener(SWT.Modify, new Listener()
		{
			public void handleEvent(Event event)
			{
				try
				{
					new Double(simulationTimeText.getText());
				} catch (Exception e)
				{
					setErrorMessage("Simulation time is not valid: "
							+ simulationTimeText.getText());
				}
			}
		});
	}

	private void createPathsSelection(Composite parent)
	{
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
		// Label contractLabel = new Label(group, SWT.NONE);
		// contractLabel.setText("Contract Path:");
		// contractPath = new Text(group, SWT.BORDER);
		// gridData = new GridData();
		// gridData.horizontalAlignment = SWT.FILL;
		// gridData.grabExcessHorizontalSpace = true;
		// contractPath.setLayoutData(gridData);
		// contractPath.setText("Insert Contract path here");
		// contractPath.setEditable(false);

		// Shared Design Parameters Line
		// Label sharedDesignParamLabel = new Label(group, SWT.NONE);
		// sharedDesignParamLabel.setText("Shared Design Parameters Path:");
		// fSharedDesignParamPath = new Text(group, SWT.BORDER);
		// gridData = new GridData();
		// gridData.horizontalAlignment = SWT.FILL;
		// gridData.grabExcessHorizontalSpace = true;
		// fSharedDesignParamPath.setLayoutData(gridData);
		// // sharedDesignParamPath.setText("Insert Shared Design Parameters path here");
		// fSharedDesignParamPath.setEditable(false);

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

		fProjectText = new Text(group, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fProjectText.setLayoutData(gd);
		fProjectText.addModifyListener(fListener);

		Button selectProjectButton = createPushButton(group, "Browse...", null);

		selectProjectButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
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
								try
								{
									if (object instanceof IProject && ((IProject)object).hasNature(IDestecsCoreConstants.NATURE))
									{
										elements.add(object);
									}
								} catch (CoreException e)
								{
									//Ignore it
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

					)
					{
						IProject project = ((IProject) dialog.getFirstResult());
						setProjectAndsearchModels(project);

						// selectScenarioButton.setEnabled(true);

					}

				}
			}
		});
	}

	private void setProjectAndsearchModels(IProject project)
	{
		if (project == null)
		{
			// Show error
			return;
		}

		try
		{
			List<String> emxFiles = new Vector<String>();
			fListener.suspended = true;
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
				} else if (iResource instanceof IFile)
				{
					IFile file = (IFile) iResource;
					if (file.getFileExtension().equals("csc"))
					{
						// contractPath.setText(file.getLocationURI().getPath());
					}
					if (file.getFileExtension().equals("emx"))
					{
						emxFiles.add(file.getLocationURI().getPath());
					}
					if (file.getFileExtension().equals("sdp"))
					{
//						fSharedDesignParamPath.setText(file.getLocationURI().getPath());
					}
				}

			}

			if (emxFiles.size() == 1)
			{
				ctPath.setText(emxFiles.get(0));
			}

		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			fListener.suspended = false;
		}
		fProjectText.setText(project.getName());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{

	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			fProjectText.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, ""));

			ctPath.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH, "No Path Selected"));
			dtPath.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_MODEL_PATH, "No Path Selected"));
			// contractPath.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CONTRACT_PATH,
			// "No Path Selected"));
			simulationTimeText.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SIMULATION_TIME, "0"));
			fScenarioText.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SCENARIO_PATH, ""));

			// if (getProject() == null)
			// {
			// selectScenarioButton.setEnabled(true);
			// }

		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		if (getProject() != null )
		{
			IDestecsProject p = (IDestecsProject) getProject().getAdapter(IDestecsProject.class);
			configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CONTRACT_PATH, p.getContractFile().getLocationURI().getPath());
		}
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, fProjectText.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH, ctPath.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_MODEL_PATH, dtPath.getText());

		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SIMULATION_TIME, simulationTimeText.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SCENARIO_PATH, fScenarioText.getText());

	}

	public IProject getProject()
	{
		if (fProjectText != null && fProjectText.getText().length() > 0)
		{
			return ResourcesPlugin.getWorkspace().getRoot().getProject(fProjectText.getText());
		} else
		{
			setErrorMessage("Project not set");
			return null;
		}

	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);

		IProject project = getProject();
		if (project == null)
		{
			selectScenarioButton.setEnabled(false);
			return false;
		}

		if (!new File(ctPath.getText()).exists())
		{
			setErrorMessage("CT model path not valid");
		}
		if (!new File(dtPath.getText()).exists())
		{
			setErrorMessage("DT model path not valid");
		}
//		if (!new File(contractPath.getText()).exists())
//		{
//			setErrorMessage("Contract path not valid");
//		}
//
//		if (!new File(fSharedDesignParamPath.getText()).exists())
//		{
//			setErrorMessage("Shared design parameters path not valid");
//		}

		if (fScenarioText.getText().length() > 0
				&& !new File(fScenarioText.getText()).exists())
		{
			setErrorMessage("Scenario path not valid");
		}

		try
		{
			if (Double.parseDouble(simulationTimeText.getText()) <= 0)
			{
				setErrorMessage("Simulation time not set");
			}
		} catch (NumberFormatException e)
		{
			setErrorMessage("Simulation time is not a number");
			return false;
		}

		selectScenarioButton.setEnabled(true);

		return true;
	}

	public String getName()
	{
		return "Main";
	}

}
