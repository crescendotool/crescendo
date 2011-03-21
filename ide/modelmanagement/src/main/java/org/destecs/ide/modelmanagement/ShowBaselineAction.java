package org.destecs.ide.modelmanagement;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ShowBaselineAction implements IWorkbenchWindowActionDelegate {

	public void run(IAction action) {
		// TODO Auto-generated method stub
		if(action != null){
			System.out.println("show baseline Action is triggered.");
		} else{
			System.out.println("show baseline Action is not triggered.");
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}
