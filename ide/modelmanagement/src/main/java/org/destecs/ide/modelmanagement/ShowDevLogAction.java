package org.destecs.ide.modelmanagement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.team.core.TeamException;
import org.eclipse.team.ui.history.IHistoryView;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
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
 * Show history for selected local resource
 */
public class ShowDevLogAction extends TeamAction implements IWorkbenchWindowActionDelegate  {

	ISVNClientAdapter svnClient;
	//IResource[] resources;
//	public ShowDevLogAction showLog = new ShowDevLogAction();
	IResource[] selectedResources;
	IResource selectedResource;
	
	public void run(IAction action) {
		if(action != null){
			System.out.println("ShowDevLogAction is triggered.");
			
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
			
			NotifyListener listener = new ShowDevLogAction.NotifyListener();
			svnClient.addNotifyListener(listener);
			
			try{
				//get selected resources
				IResource[] resources = getSelectedResources();
				System.out.println("Resources length: "+resources.length);
				System.out.println("Resources locationURI: "+resources[0].getLocationURI());
				System.out.println("Resources name: "+resources[0].getName()+"\n");
				

				//show log msg in console
				//need to extend ISVNLogMessage with more items later
				ISVNLogMessage[] logMsg = svnClient.getLogMessages(new SVNUrl("https://chessforge.chess-it.com/svn/destecs/trunk/WP2AndWP3Planning/WP2AndWP3SprintPlan.tex"), new SVNRevision.Number(1), SVNRevision.HEAD);
				System.out.println("Total revisions of this file: "+logMsg.length);
				int i;
				for (i=0; i < logMsg.length; i++){
					System.out.println("Revision number: "+logMsg[i].getRevision());
					System.out.println("Date: "+logMsg[i].getDate());
					System.out.println("Author: "+logMsg[i].getAuthor());
					System.out.println("Message: "+logMsg[i].getMessage()+"\n");
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
	
//    public IResource[] getSelectedResources() {
//		if (selectedResource != null) {
//			IResource[] selectedResources  = { selectedResource };
//			return selectedResources;
//		}
//		return this.getSelectedResources();
//    }

	
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
	        } catch (SVNClientException e) {
	            // can register this factory
	        }
//	        try {
//	            CmdLineClientAdapterFactory.setup();
//	        } catch (SVNClientException e1) {
//	            // can't register this factory
//	        }	
//	        try {
//	            SvnKitClientAdapterFactory.setup();
//	        } catch (SVNClientException e1) {
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

	@Override
	protected boolean isEnabled() throws TeamException {
		// TODO Auto-generated method stub
		return false;
	}
	
}

