package org.destecs.ide.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.undo.CreateFileOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.internal.UIPlugin;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

public abstract class AbstractNewFileWizard extends Wizard implements
		INewWizard
{

	// private String fPageName;
	private IStructuredSelection fStructuredSelection;
	private DestecsWizardPageCreation _pageOne;

	public AbstractNewFileWizard()
	{
		setWindowTitle(getName() + " Wizard");

	}

	public abstract String getName();

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		fStructuredSelection = selection;

	}

	@SuppressWarnings("restriction")
	@Override
	public boolean performFinish()
	{
		String pName = _pageOne.getProjectName();
		IWorkspaceRoot ws = ResourcesPlugin.getWorkspace().getRoot();
		IProject p = ws.getProject(pName);
		File root = p.getLocation().toFile();
		File dfile = new File(root, getLocation());

		if (!dfile.exists())
		{
			dfile.mkdirs();
		}

		IPath path = p.getFullPath().append("/" + getLocation());
		path = path.append("/" + _pageOne.getFileName());
		path = path.addFileExtension(getFileExtension());
		IFile file = createFileHandle(path);
		
		if (!file.exists())
		{
			createFile(file, getInitialContents());
		}
		try
		{
			IDE.openEditor(UIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
			file.touch(null);
			file.refreshLocal(IResource.DEPTH_ONE, null);
		} catch (CoreException e)
		{

		}
		return true;
	}

	/**
	 * Creates a file resource handle for the file with the given workspace path. This method does not create the file
	 * resource; this is the responsibility of <code>createFile</code>.
	 * 
	 * @param filePath
	 *            the path of the file resource to create a handle for
	 * @return the new file resource handle
	 * @see #createFile
	 */
	protected IFile createFileHandle(IPath filePath)
	{
		return IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(filePath);
	}
	
	/**
	 * Returns a stream containing the initial contents to be given to new file
	 * resource instances. <b>Subclasses</b> may wish to override. This default
	 * implementation provides no initial contents.
	 * 
	 * @return initial contents to be given to new file resource instances
	 */
	protected InputStream getInitialContents() {
		String fileTemplate = getFileTemplate(_pageOne.getProjectName());
		InputStream stream = null;
		if (fileTemplate != null && fileTemplate.length() > 0)
		{
			stream = new ByteArrayInputStream(fileTemplate.getBytes());
		}
		return stream;
	}

	private void createFile(final IFile newFileHandle,
			final InputStream initialContents)
	{
		IRunnableWithProgress op = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor)
			{
				URI linkTargetPath = null;

				CreateFileOperation op = new CreateFileOperation(newFileHandle, linkTargetPath, initialContents, IDEWorkbenchMessages.WizardNewFileCreationPage_title);
				try
				{
					// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
					// directly execute the operation so that the undo state is
					// not preserved. Making this undoable resulted in too many
					// accidental file deletions.
					op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(getShell()));
				} catch (final ExecutionException e)
				{
					getContainer().getShell().getDisplay().syncExec(new Runnable()
					{
						public void run()
						{
							if (e.getCause() instanceof CoreException)
							{
								ErrorDialog.openError(getContainer().getShell(), // Was
								// Utilities.getFocusShell()
								IDEWorkbenchMessages.WizardNewFileCreationPage_errorTitle, null, // no special
								// message
								((CoreException) e.getCause()).getStatus());
							} else
							{
								IDEWorkbenchPlugin.log(getClass(), "createNewFile()", e.getCause()); //$NON-NLS-1$
								MessageDialog.openError(getContainer().getShell(), IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorTitle, NLS.bind(IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorMessage, e.getCause().getMessage()));
							}
						}
					});
				}
			}
		};
		try
		{
			getContainer().run(true, true, op);
		} catch (InterruptedException e)
		{
			// return null;
		} catch (InvocationTargetException e)
		{
			// Execution Exceptions are handled above but we may still get
			// unexpected runtime errors.
			IDEWorkbenchPlugin.log(getClass(), "createNewFile()", e.getTargetException()); //$NON-NLS-1$
			MessageDialog.open(MessageDialog.ERROR, getContainer().getShell(), IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorTitle, NLS.bind(IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorMessage, e.getTargetException().getMessage()), SWT.SHEET);

			// return null;
		}

	}

	protected String getFileTemplate(String fileName)
	{
		return null;
	}

	public abstract String getLocation();

	public abstract String getFileExtension();

	protected void applyTemplate(IFile file, String fileTemplate)
	{
		InputStream stream;
		try
		{
			stream = new ByteArrayInputStream(fileTemplate.getBytes());
			file.setContents(stream, IFile.FORCE, null);
		} catch (CoreException e)
		{

		}

	}

	@Override
	public boolean canFinish()
	{
		return super.canFinish() && _pageOne.getErrorMessage() == null;
	}

	@Override
	public void addPages()
	{
		super.addPages();

		IProject project = getProject();
		_pageOne = new DestecsWizardPageCreation("Project Selection", this.fStructuredSelection, getFileExtension(), getName(),isFileNameEditable(),project.getName());
		_pageOne.setTitle("Project Selection");
		_pageOne.setDescription("Chose the project in which you want to create the "
				+ getName());

		addPage(_pageOne);
	}
	
	protected IProject getProject()
	{
		if(fStructuredSelection.getFirstElement() instanceof IProject)
		{
			return (IProject) fStructuredSelection.getFirstElement();
		}
		return null;
	}
	
	protected boolean isFileNameEditable()
	{
		return false;
	}

}
