package org.destecs.ide.ui.wizards;

import java.util.List;
import java.util.Vector;

import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.resources.IDestecsProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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

public class DestecsWizardPageCreation extends WizardPage
{

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public boolean suspended = false;

		public void modifyText(ModifyEvent e)
		{
			if (!suspended)
			{
				// updatePage();
				// updateLaunchConfigurationDialog();
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

				// updateLaunchConfigurationDialog();
			}
		}
	}

	// private IStructuredSelection fStructuredSelection = null;
	// private IProject project = null;
	// private boolean validSelection = false;
	private Text fProjectText;
	private String fProjectName = null;
	private WidgetListener fListener = new WidgetListener();
	private String fType;
	private String fName;
	private Text fFileText;
	private IStructuredSelection fStructuredSelection;
	private boolean fFileNameEditable;
	private String initialFileName;

	protected DestecsWizardPageCreation(String pageName)
	{
		super(pageName);

	}

	public DestecsWizardPageCreation(String string,
			IStructuredSelection fStructuredSelection, String type,
			String name, boolean fileNameEditable, String initialFileName)
	{
		super(string);
		// this.fStructuredSelection = fStructuredSelection;
		this.fType = type;
		this.fName = name;
		this.fStructuredSelection = fStructuredSelection;
		this.fFileNameEditable = fileNameEditable;
		this.initialFileName = initialFileName;
		
		if(fStructuredSelection.getFirstElement() instanceof IContainer)
		{
			this.fProjectName = ((IContainer)fStructuredSelection.getFirstElement()).getProject().getName();
		}

	}

	public String getProjectName()
	{
		return this.fProjectName;
	}

	public String getFileName()
	{
		return this.fFileText.getText();
	}

	private void updatePage()
	{
		if (fProjectName == null)
		{
			return;
		}

		IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();
		IProject p = ws.getProject(fProjectName);
		IFolder f = p.getFolder("configuration");

		if (f != null)
		{
			IFile file = f.getFile(p.getName() + "." + this.fType);
			if (file.exists())
			{
				setErrorMessage(fName + " already exists");
			} else
			{
				setErrorMessage(null);
				setMessage(fName + " is going to be created in configuration/"
						+ p.getName() + "." + fType);
			}
		} else
		{
			setErrorMessage(null);

			setMessage(fName + "is going to be created in configuration/"
					+ p.getName() + "." + fType);
		}

		if (fFileText.getText().trim().length() == 0)
		{
			setErrorMessage("A file name must be specified.");
		}

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

		if (this.fProjectName != null)
		{
			fProjectText.setText(this.fProjectName);
		}
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
									if (object instanceof IProject
											&& ((IProject) object).hasNature(IDestecsCoreConstants.NATURE))
									{
										elements.add(object);
									}
								} catch (CoreException e)
								{
									// Ignore it
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
						setProject(project);

						// setProjectAndsearchModels(project);

						// selectScenarioButton.setEnabled(true);

					}

				}
			}

		});

		Group groupFile = new Group(parent, parent.getStyle());
		groupFile.setText(fName);
		GridData gdFile = new GridData(GridData.FILL_HORIZONTAL);

		groupFile.setLayoutData(gdFile);

		GridLayout layoutFile = new GridLayout();
		layoutFile.makeColumnsEqualWidth = false;
		layoutFile.numColumns = 2;
		groupFile.setLayout(layoutFile);

		// editParent = group;

		Label labelFile = new Label(groupFile, SWT.MIN);
		labelFile.setText("Name:");
		gdFile = new GridData(GridData.BEGINNING);
		labelFile.setLayoutData(gdFile);

		fFileText = new Text(groupFile, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		fFileText.setEditable(fFileNameEditable);
		if (initialFileName != null)
		{
			fFileText.setText(initialFileName);
		}
		gdFile = new GridData(GridData.FILL_HORIZONTAL);
		fFileText.setLayoutData(gdFile);
		fFileText.addModifyListener(fListener);

		Object o = fStructuredSelection.getFirstElement();

		if (o != null && o instanceof IProject)
		{
			IProject p = (IProject) o;
			IDestecsProject dp = (IDestecsProject) p.getAdapter(IDestecsProject.class);

			if (dp != null)
			{
				// this.project = p;
				this.fProjectName = p.getName();
				// this.validSelection = true;
				updatePage();
			}
		}
	}

	private void setProject(IProject project)
	{
		this.fProjectName = project.getName();
		this.fProjectText.setText(fProjectName);
		updatePage();

	}

	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout(1, true));
		composite.setFont(parent.getFont());
		createProjectSelection(composite);
		setControl(composite);
	}

	@Override
	public boolean isPageComplete()
	{
		System.out.println("Is page complete run...");
		return super.isPageComplete();
	}

	// private static Button createPushButton(Composite parent, String label, String tooltip, Image image) {
	// Button button = createPushButton(parent, label, image);
	// button.setToolTipText(tooltip);
	// return button;
	// }

	private static Button createPushButton(Composite parent, String label,
			Image image)
	{
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		if (image != null)
		{
			button.setImage(image);
		}
		if (label != null)
		{
			button.setText(label);
		}
		GridData gd = new GridData();
		button.setLayoutData(gd);
		setButtonDimensionHint(button);
		return button;
	}

	private static void setButtonDimensionHint(Button button)
	{
		Assert.isNotNull(button);
		Object gd = button.getLayoutData();
		if (gd instanceof GridData)
		{
			((GridData) gd).widthHint = getButtonWidthHint(button);
			((GridData) gd).horizontalAlignment = GridData.FILL;
		}
	}

	private static int getButtonWidthHint(Button button)
	{
		/* button.setFont(JFaceResources.getDialogFont()); */
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	public void setType(String type)
	{
		this.fType = type;

	}

}
