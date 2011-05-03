package org.destecs.ide.modelmanagement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.tigris.subversion.subclipse.ui.ISVNUIConstants;
import org.tigris.subversion.subclipse.ui.internal.TeamAction;
import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.ISVNLogMessage;
import org.tigris.subversion.svnclientadapter.ISVNNotifyListener;
import org.tigris.subversion.svnclientadapter.SVNClientAdapterFactory;
import org.tigris.subversion.svnclientadapter.SVNClientException;
import org.tigris.subversion.svnclientadapter.SVNNodeKind;
import org.tigris.subversion.svnclientadapter.SVNRevision;
import org.tigris.subversion.svnclientadapter.SVNUrl;
import org.tigris.subversion.svnclientadapter.javahl.JhlClientAdapterFactory;

/**
 * This class is used to generate co-model development history in HistoryView.
 * */
public class showlogaction extends TeamAction implements IActionDelegate  {
	
	ISVNClientAdapter svnClient;
	ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
	
	public showlogaction() {
		// TODO Auto-generated constructor stub
	}
	
	public void run(IAction action) {
		if(action != null){
			System.out.println("showlogaction is triggered.");
			
			//create client type (javahl is recommended)
			setup();
			
			try{
				String clientType = SVNClientAdapterFactory.getPreferredSVNClientType();
				svnClient = SVNClientAdapterFactory.createSVNClient(clientType);				
				System.out.println("using "+clientType+" factory.\n");
			}catch(SVNClientException e){
				System.out.println(e.getMessage());
				return;
			}
			
			NotifyListener listener = new showlogaction.NotifyListener();
			svnClient.addNotifyListener(listener);
			
			try{
				//get selected resources (or get ISVNResource? then have svnUrl, check later)
				IResource[] resources = getSelectedResources(selection);				
				if (resources != null){					
					System.out.println("Selection toString: "+selection.toString());
					System.out.println("Resources length: "+resources.length);
					System.out.println("Resources name: "+resources[0].getName());
					System.out.println("Resources location: "+resources[0].getLocation().toFile()+"\n");
				} else {
					System.out.println("Did not select resource yet.");
				}
				
				//show log msg in console
				//need to extend ISVNLogMessage with more items later
				@SuppressWarnings("unused")
				ISVNLogMessage[] logMsg = svnClient.getLogMessages(new SVNUrl("https://chessforge.chess-it.com/svn/destecs/trunk/WP2AndWP3Planning/WP2AndWP3SprintPlan.tex"), new SVNRevision.Number(1), SVNRevision.HEAD);
				ISVNLogMessage[] logMsg2 = svnClient.getLogMessages(resources[0].getLocation().toFile(), new SVNRevision.Number(1), SVNRevision.HEAD);
				System.out.println("Total revisions of this file: "+logMsg2.length);
				int i;
				for (i=0; i < logMsg2.length; i++){
					System.out.println("Revision number: "+logMsg2[i].getRevision());
					System.out.println("Date: "+logMsg2[i].getDate());
					System.out.println("Author: "+logMsg2[i].getAuthor());
					System.out.println("Message: "+logMsg2[i].getMessage()+"\n");
				}
				
				//show log msg in history view
				IHistoryView view = (IHistoryView) showView(ISVNUIConstants.HISTORY_VIEW_ID);
				if (view != null) {
					view.showHistoryFor(resources[0]);
				}

				//fetch file content
//				InputStream is = svnClient.getContent(new SVNUrl("https://chessforge.chess-it.com/svn/destecs/trunk/WP2AndWP3Planning/WP2AndWP3SprintPlan.tex"), SVNRevision.HEAD);
//				System.out.println("The beginning of the file is :");
//				byte[] bytes = new byte[100]; //create a buffer
//				System.out.println("is.read(bytes) is: "+is.read(bytes));
//				System.out.println("String(bytes) is: "+ new String(bytes));
			}
			catch(IOException e){
				System.out.println("An exception occured while getting remote file.");
			} 
			catch (SVNClientException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("ShowDevLogAction is not triggered.");
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
	 
	public static class NotifyListener implements ISVNNotifyListener{
		public void setCommand(int cmd){
			
		}

		public void logCommandLine(String commandLine) {
			System.out.println(commandLine);
		}

		public void logMessage(String message) {
			System.out.println(message);
		}

		public void logError(String message) {
			System.out.println("error :" +message);
		}

		public void logRevision(long revision, String path) {
			System.out.println("revision :" +revision);
		}

		public void logCompleted(String message) {
			System.out.println(message);
		}

		public void onNotify(File path, SVNNodeKind kind) {
			System.out.println("Status of "+path.toString()+" has changed");
		}			
	}
	
	public void setup() {
		 try{
	            JhlClientAdapterFactory.setup();
	        }catch (SVNClientException e) {
	            // can register this factory
	        }
//	        try {
//	            CmdLineClientAdapterFactory.setup();
//	        }catch (SVNClientException e1) {
//	            // can't register this factory
//	        }	
//	        try {
//	            SvnKitClientAdapterFactory.setup();
//	        }catch (SVNClientException e1) {
//	            // can't register this factory
//	        }
	}

	public InputStream getHistoryContent(SVNUrl url, SVNRevision revision)throws SVNClientException{
		return this.svnClient.getContent(url, revision);		
	}
	
	public ISVNLogMessage[] getHistory(SVNUrl url)throws SVNClientException{
        return this.svnClient.getLogMessages(url, SVNRevision.START, SVNRevision.HEAD);
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

	protected boolean isEnabled() throws TeamException {
		// TODO Auto-generated method stub
		return false;
	}

}
