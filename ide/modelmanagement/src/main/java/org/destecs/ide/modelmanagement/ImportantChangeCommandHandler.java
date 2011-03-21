package org.destecs.ide.modelmanagement;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/*
 * Get resource, set important flag to true, store info in model base.
 * */

public class ImportantChangeCommandHandler extends AbstractHandler {

	boolean isImportantChange;
	private String decisionMsg;
	
	public ImportantChangeCommandHandler()
	{}
	
	public ImportantChangeCommandHandler(boolean isImportantChange, String decisionMsg){
		this.isImportantChange = isImportantChange;
		this.decisionMsg = decisionMsg;
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		//mark as important change	
		//TODO get resource, query model base, set isImportanctchange flag=true
		System.out.println("ImportantChangeCommandHandler");
		ImportantChangeCommandHandler newImportantChange = new ImportantChangeCommandHandler(isImportantChange, decisionMsg);

		if(newImportantChange.getImportantChangeFlag()==false){
			newImportantChange.setBaseEnabled(true);
			newImportantChange.setMsg("important change comments");
			System.out.println("This change is marked as an important change.");
		}else{
			
			System.out.println("error msg: This change is already marked as an important change.");
		}
			

//		if (newImportantChange.getImportantChangeFlag() == false){
//			 //call modified subversion tag, invoke setBaselineFlag to the DB
//			 newImportantChange.setImportantChangeFlag(true);
//			 System.out.println("This change is marked as an important change.");
//			 System.out.println(newImportantChange.getImportantChangeFlag());
//		   }else{
//			 //call warning message window, pass this.error to it
//			 System.out.println("error msg: already marked.");
//		   }
		
		return null;
	}
	
	//write those to model base table	
	public void setImportantChangeFlag(boolean isImportantChange){
		this.isImportantChange = isImportantChange;
	}
	
	public Boolean getImportantChangeFlag(){				
		return isImportantChange;		
	}
	
	public void setMsg(String decisionMsg){
		this.decisionMsg = decisionMsg;		
	}
	
	public String getMsg(){
		return decisionMsg;
	}

}
