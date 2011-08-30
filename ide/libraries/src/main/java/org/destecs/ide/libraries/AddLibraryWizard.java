package org.destecs.ide.libraries;

import org.destecs.ide.libraries.util.LibraryUtil;
import org.destecs.ide.libraries.wizard.LibraryIncludePage;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;


public class AddLibraryWizard extends Wizard implements IWorkbenchWizard
{
	private static final String WIZARD_NAME = "Add Library Wizard";
	private IProject project = null;
	private LibraryIncludePage _pageTwo;

	public AddLibraryWizard() {
		setWindowTitle(WIZARD_NAME);
	}

	@Override
	public boolean performFinish()
	{
		try
		{
			LibraryUtil.createSelectedLibraries(project,_pageTwo.getLibrarySelection());
		} catch (CoreException e)
		{
//			if (VdmUIPlugin.DEBUG)
			{
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		
		if(selection.getFirstElement() instanceof IProject ){
			IProject project = (IProject) selection.getFirstElement();
			this.project = project;//(IVdmProject) project.getAdapter(IVdmProject.class);
		}
//		if (selection.getFirstElement() instanceof IVdmProject)
//		{					
//			this.project = (IVdmProject) selection.getFirstElement();
//		}else if(selection.getFirstElement() instanceof IFolder)
//		{
//			IProject project = ((IFolder)selection.getFirstElement()).getProject();
//			this.project = (IVdmProject) project.getAdapter(IVdmProject.class);
//			
//			if(this.project == null)
//			{
//				MessageDialog.openError(getShell(), "Project type error", "Project is not a VDM project");
//			}
//		}

	}

	@Override
	public void addPages()
	{
		_pageTwo = new LibraryIncludePage("Add Library");
		addPage(_pageTwo);
	}
	

//@Override
//public boolean canFinish()
//{
//	
//	return super.canFinish()&&_pageTwo.getLibrarySelection().getSelectedLibs().size()>0;
//}

}
