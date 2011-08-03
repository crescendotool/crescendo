package org.destecs.ide.debug.launching.ui;

import java.util.List;
import java.util.Vector;

import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
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
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

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
				updateLaunchConfigurationDialog();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		public void widgetSelected(SelectionEvent e)
		{
			if (!suspended)
			{
				updateLaunchConfigurationDialog();
			}
		}
	}

	private Text fProjectText;
	private Text ctPath = null;
	private Text dePath = null;
	private Button selectScenarioButton;
	private Text simulationTimeText = null;
	private WidgetListener fListener = new WidgetListener();
	private Text fScenarioText;
	private Button selectCtPathButton;
	private Button removeScenarioButton;

	final List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
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
		layout.numColumns = 4;
		group.setLayout(layout);

		Label label = new Label(group, SWT.MIN);
		label.setText("Scenario:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fScenarioText = new Text(group, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fScenarioText.setLayoutData(gd);
		fScenarioText.addModifyListener(fListener);

		removeScenarioButton = createPushButton(group, "Remove", null);

		removeScenarioButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fScenarioText.setText("");
			}
		});

		selectScenarioButton = createPushButton(group, "Browse...", null);
		selectScenarioButton.setEnabled(false);
		selectScenarioButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
				dialog.setTitle("Scenario Selection");
				dialog.setMessage("Select a scenario:");
				dialog.setComparator(new ViewerComparator());
				dialog.addFilter(new FileExtensionFilter("script"));
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot().getProject(fProjectText.getText()).getFolder("scenarios"));

				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null)
					{
						fScenarioText.setText(((IFile) dialog.getFirstResult()).getProjectRelativePath().toString());
						removeScenarioButton.setEnabled(true);
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
		layout.numColumns = 3;
		group.setLayout(layout);

		// DE Line
		Label deLabel = new Label(group, SWT.NONE);
		deLabel.setText("DE Path:");
		dePath = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		dePath.setLayoutData(gridData);
		dePath.setText("Insert DE model path here");
		createPushButton(group, "Browse...", null).setEnabled(false);

		// CT Line
		Label ctLabel = new Label(group, SWT.MIN);
		ctLabel.setText("CT Path:");
		gd = new GridData(GridData.BEGINNING);
		ctLabel.setLayoutData(gd);

		ctPath = new Text(group, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		ctPath.setLayoutData(gd);
		ctPath.addModifyListener(fListener);

		selectCtPathButton = createPushButton(group, "Browse...", null);
		selectCtPathButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
				dialog.setTitle("20-Sim Model Selection");
				dialog.setMessage("Select a 20-Sim Model:");
				dialog.setComparator(new ViewerComparator());
				dialog.addFilter(new FileExtensionFilter("emx"));
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot().getProject(fProjectText.getText()).getFolder("model_ct"));

				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null)
					{
						ctPath.setText(((IFile) dialog.getFirstResult()).getProjectRelativePath().toString());

					}
				}
			}
		});

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
				}
				;
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new ProjectContentProvider());
				dialog.setTitle("Project Selection");
				dialog.setMessage("Select a project:");
				dialog.setComparator(new ViewerComparator());
				dialog.addFilter(new ViewerFilter()
				{
					@Override
					public boolean select(Viewer viewer, Object parentElement,
							Object element)
					{
						try
						{
							return element instanceof IProject
									&& ((IProject) element).hasNature(IDestecsCoreConstants.NATURE);
						} catch (CoreException e)
						{
							return false;
						}
					}
				});

				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null
							&& dialog.getFirstResult() instanceof IProject

					)
					{
						IProject project = ((IProject) dialog.getFirstResult());
						if (project == null)
						{
							// Show error
							return;
						}

						IDestecsProject dproject = (IDestecsProject) project.getAdapter(IDestecsProject.class);
						dePath.setText(dproject.getVdmModelFolder().getProjectRelativePath().toString());
						fProjectText.setText(project.getName());
					}

				}
			}
		});
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
			dePath.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_MODEL_PATH, "No Path Selected"));
			simulationTimeText.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SIMULATION_TIME, "0"));
			fScenarioText.setText(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SCENARIO_PATH, ""));

			removeScenarioButton.setEnabled(!fScenarioText.getText().isEmpty());

		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Faild to initialize from launch configuration", e);
		}

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		if (getProject() != null)
		{
			IDestecsProject p = (IDestecsProject) getProject().getAdapter(IDestecsProject.class);
			configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CONTRACT_PATH, p.getContractFile().getProjectRelativePath().toString());
		}
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, fProjectText.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH, ctPath.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_MODEL_PATH, dePath.getText());

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

		IVdmProject vdmProject = (IVdmProject) getProject().getAdapter(IVdmProject.class);
		IVdmModel model = vdmProject.getModel();
		if (!model.isTypeCorrect())
		{
			if (!VdmTypeCheckerUi.typeCheck(getShell(), vdmProject))
			{
				setErrorMessage("Type errors in Model");
				return false;
			}
		}

		if (project.findMember(new Path(ctPath.getText())) == null)
		{
			setErrorMessage("CT model path not valid");
		}
		if (project.findMember(new Path(dePath.getText())) == null)
		{
			setErrorMessage("DE model path not valid");
		}
		if (fScenarioText.getText().length() > 0
				&& project.findMember(new Path(fScenarioText.getText())) == null)
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
