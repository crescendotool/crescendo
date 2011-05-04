package org.destecs.ide.modelmanagement;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.tigris.subversion.subclipse.ui.actions.CommitAction;

/**
 * This class extends Subversion CommitAction for checking in files to repo
 * Includes all model mgmt system required development information
 * Such as: isImportantchange, isBaseline, hasAlternatives.
 */
public class commit2mgmt extends CommitAction implements IActionDelegate{
	
	protected boolean isImportantchange;
	protected boolean isBaseline;
	protected boolean hasAlternatives;
	
	public commit2mgmt(){	
	}
	
	public void execute(IAction action) throws InvocationTargetException, InterruptedException {
		if(action != null){
			System.out.println("commit2mgmt is triggered.");	
			
			ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
			IResource[] resources = getSelectedResources(selection);
			
			System.out.println("this class selected resource length: "+resources.length);
			System.out.println("this class selected resource locatiion: "+resources[0].getLocation().toString());
			System.out.println("super class selected resource length: "+super.getSelectedResources().length);
			
			//TODO override super.execute, call SvnWizardCommitPage
			//TODO call mgmtCommitOperation, then user can specify the extra dev info
			super.setSelectedResources(resources);
			super.execute(action);

		}else{
			System.out.println("commit2mgmt is not triggered.");
		}
	}

	/**
	  * Returns the selected resources.
	  *
	  * @return the selected resources
	  */
	 protected IResource[] getSelectedResources(ISelection selection) {
	  ArrayList<Object> resources = null;
	  if (!selection.isEmpty()) {
	   resources = new ArrayList<Object>();
	   @SuppressWarnings("unchecked")
	Iterator<Object> elements = ((IStructuredSelection) selection).iterator();
	   while (elements.hasNext()) {
	    Object next = elements.next();
	    if (next instanceof IResource) {
	     resources.add(next);
	     continue;
	    }
	    if (next instanceof IAdaptable) {
	     IAdaptable a = (IAdaptable) next;
	     Object adapter = a.getAdapter(IResource.class);
	     if (adapter instanceof IResource) {
	      resources.add(adapter);
	      continue;
	     }
	    }
	   }
	  }
	  if (resources != null && !resources.isEmpty()) {
	   IResource[] result = new IResource[resources.size()];
	   resources.toArray(result);
	   return result;
	  }
	  return new IResource[0];
	 }
	
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub		
	}

	
}
