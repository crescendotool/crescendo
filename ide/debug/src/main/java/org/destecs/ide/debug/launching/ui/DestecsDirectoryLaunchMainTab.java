package org.destecs.ide.debug.launching.ui;

import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.launching.ui.internal.FolderFilter;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class DestecsDirectoryLaunchMainTab extends AbstractLaunchConfigurationTab implements ILaunchConfigurationTab
{

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public boolean suspended = false;

		public void modifyText(ModifyEvent e)
		{
			if (!suspended)
			{
				if(fProjectText.getText().trim().isEmpty())
				{
					selectPathToConfigsButton.setEnabled(false);
				}
				else
				{
					selectPathToConfigsButton.setEnabled(true);
				}
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
	private WidgetListener fListener = new WidgetListener();
	private Text fPathToConfigsText;
	private Button selectPathToConfigsButton;
	
	@Override
	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		createProjectSelection(comp);
		createPathToConfigsGroup(comp);
	}

	private void createProjectSelection(Composite parent)
	{
		Group group = new Group(parent, parent.getStyle());
		group.setText("Project Selection");
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

						fProjectText.setText(project.getName());
					}

				}
			}
		});
	}
	
	private void createPathToConfigsGroup(Composite comp)
	{
		Group group = new Group(comp, comp.getStyle());
		group.setText("Directory selection");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		group.setLayout(layout);

		Label label = new Label(group, SWT.MIN);
		label.setText("Directory:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		fPathToConfigsText = new Text(group, SWT.SINGLE | SWT.BORDER
				| SWT.READ_ONLY);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		fPathToConfigsText.setLayoutData(gd);
		fPathToConfigsText.addModifyListener(fListener);

		selectPathToConfigsButton = createPushButton(group, "Browse...", null);
		selectPathToConfigsButton.setEnabled(true);
		selectPathToConfigsButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
				dialog.setTitle("Directory Selection");
				dialog.setMessage("Select an Directory:");
				dialog.setComparator(new ViewerComparator());
				dialog.addFilter(new FolderFilter());
				dialog.setValidator(new ISelectionStatusValidator()
				{
					
					public IStatus validate(Object[] selection)
					{
						if (selection.length == 1 && selection[0] instanceof IFolder)
						{
							return Status.OK_STATUS;
						}
						else return new Status(IStatus.ERROR,DestecsDebugPlugin.PLUGIN_ID,"Invalid selection. Selection must be a file.");
							
					}
				});

				if(fProjectText.getText() != null && !fProjectText.getText().trim().isEmpty() )
				{
					IProject project = getProject();
					dialog.setInput(project);
				}
					
				
				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null)
					{
						fPathToConfigsText.setText(((IFolder) dialog.getFirstResult()).getProjectRelativePath().toString());

					}

				}
			}
		});
		
	}
	
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, "");
		configuration.setAttribute(IDebugConstants.DESTECS_DIRECTORY_LAUNCH_FOLDER, "");
		
		if(fProjectText != null)
		{
			fProjectText.setText("");
		}
		
		if(fPathToConfigsText != null)
		{
			fPathToConfigsText.setText("");
		}
		
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			String projectText = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, "");
			String pathToConfigs = configuration.getAttribute(IDebugConstants.DESTECS_DIRECTORY_LAUNCH_FOLDER, "");;
			
			if(fProjectText != null)
			{
				fProjectText.setText(projectText);
			}
			
			if(fPathToConfigsText != null)
			{
				fPathToConfigsText.setText(pathToConfigs);
			}
			
		} catch (CoreException e)
		{
			DestecsDebugPlugin.log(e);
		}
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, fProjectText.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_DIRECTORY_LAUNCH_FOLDER, fPathToConfigsText.getText());
		
	}

	public String getName()
	{
		return "Main";
	}

	public IProject getProject()
	{
		if (fProjectText != null && fProjectText.getText().length() > 0)
		{
			IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(fProjectText.getText());
			if (p.isAccessible())
			{
				return p;
			} else
			{
				setErrorMessage("Project not accessible");
				return null;
			}
		} else
		{
			setErrorMessage("Project not set");
			return null;
		}

	}

}
