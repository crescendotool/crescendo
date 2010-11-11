package org.destecs.ide.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.UIPlugin;

public abstract class AbstractNewFileWizard extends Wizard implements INewWizard {

	private String fPageName;
	private IStructuredSelection fStructuredSelection;
	private DestecsWizardPageCreation _pageOne;
	
	public AbstractNewFileWizard() {
		setWindowTitle(getName() + " Wizard");
		
	}

	public abstract String getName();

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		fStructuredSelection = selection;
		

	}

	@SuppressWarnings("restriction")
	@Override
	public boolean performFinish()
	{
		String pName = _pageOne.getProjectName();
		IWorkspaceRoot ws= ResourcesPlugin.getWorkspace().getRoot();
		IProject p = ws.getProject(pName);
		File root = p.getLocation().toFile();
		File dfile = new File(root,getLocation());
	
		if(!dfile.exists()){
			dfile.mkdirs();
		}
		
		
		
		File nfile =  new File(dfile,p.getName()+"."+getFileExtension());
		IFile[] files = ws.findFilesForLocationURI(nfile.toURI());
		if(files.length == 0)
			return false;
		
		IFile file = files[0];
		
		if (file.exists())
		{
			String fileName = file.getName();
			if(fileName.contains("."))
			{
				fileName = fileName.substring(0,fileName.indexOf("."));
			}
//			String fileTemplate = getFileTemplate(fileName);
//			if (fileTemplate != null)
//			{
//				applyTemplate(file, fileTemplate);
//			}
			
		}
		try
		{
			IDE.openEditor(UIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(),file, true);
			file.touch(null);
			file.refreshLocal(IResource.DEPTH_ONE, null);
		} catch (CoreException e)
		{
			
		}
		return true;
	}

	public abstract String getLocation();

	public abstract String getFileExtension();

	private void applyTemplate(IFile file, String fileTemplate)
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
	public void addPages() {
		// TODO Auto-generated method stub
		super.addPages();
		
		_pageOne = new DestecsWizardPageCreation("Project Selection",
				this.fStructuredSelection,"csc",getName());
		_pageOne.setTitle("Project Selection");
		_pageOne.setDescription("Chose the project in which you want to create the " + getName());
		

		addPage(_pageOne);
	}
	
	
}
