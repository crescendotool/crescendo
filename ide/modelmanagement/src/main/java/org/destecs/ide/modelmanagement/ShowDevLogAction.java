package org.destecs.ide.modelmanagement;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ShowDevLogAction implements IWorkbenchWindowActionDelegate {

	public void run(IAction action) {
		// TODO Auto-generated method stub
		if(action != null){
			System.out.println("show development overview Action is triggered.");		
//			ISVNRemoteResource[] resources = getSelectedRemoteResources();
//			IHistoryView view = (IHistoryView)showView(ISVNUIConstants.HISTORY_VIEW_ID);
//			if (view != null) {
//				view.showHistoryFor(resources[0]);
//			}
		}
		else {
			System.out.println("show development overview Action is not triggered.");			
		}
	}


//	private IHistoryView showView(String historyViewId) {
//		// TODO Auto-generated method stub
//		System.out.println("showView method is triggered.");
//		return null;
//	}


//	@SuppressWarnings("null")
//	private ISVNRemoteResource[] getSelectedRemoteResources() {
//		// TODO Auto-generated method stub	
//		
//		ISVNRemoteResource remoteResource = null;  
//		IStructuredSelection fSelection = null;
//		
//		if (fSelection.getFirstElement() instanceof LogEntryChangePath) {
//			  try {
//				remoteResource = ((LogEntryChangePath)fSelection.getFirstElement()).getRemoteResource();
//			} catch (Exception e) {}
//		  }
//		  else if (fSelection.getFirstElement() instanceof HistoryFolder) {
//			  HistoryFolder historyFolder = (HistoryFolder)fSelection.getFirstElement();
//			  Object[] children = historyFolder.getChildren();
//			  if (children != null && children.length > 0 && children[0] instanceof LogEntryChangePath) {
//				  LogEntryChangePath changePath = (LogEntryChangePath)children[0];
//				  try {
//					  ISVNRemoteResource changePathResource = changePath.getRemoteResource();
//					  ISVNRemoteResource remoteFolder = changePathResource.getRepository().getRemoteFolder(historyFolder.getPath());					  
//					  remoteResource = new RemoteFolder(null, changePathResource.getRepository(), remoteFolder.getUrl(), changePathResource.getRevision(), (SVNRevision.Number)changePathResource.getRevision(), null, null); 
//				} catch (Exception e) {}
//			  }
//		  }
//		  if (remoteResource != null) {
//			   ISVNRemoteResource[] selectedResource = { remoteResource };
//			  return selectedResource;
//		  }		  
//		  return new ISVNRemoteResource[0];
//		//return null;
//	}


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



