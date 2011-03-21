package org.destecs.ide.modelmanagement;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/*
 * Mark current project as exploration point;
 * Invoke branch action;
 * Set successor;
 * Store in model base.
 * */
public class ExploreCommandHandler extends AbstractHandler {
	
	boolean isExplorationPoint;
	String successorName;
	String successorPath;
	String decisionMsg;
	
	public ExploreCommandHandler()
	{}
	
	public ExploreCommandHandler(boolean isExplorationPoint, String successorName, String successorPath, String decisionMsg){
		this.isExplorationPoint = isExplorationPoint;
		this.successorName = successorName;
		this.successorPath = successorPath;
		this.decisionMsg = decisionMsg;
	}
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("ExploreCommandHandler");
		ExploreCommandHandler newExplorationPoint = new ExploreCommandHandler(isExplorationPoint, successorName, successorPath, decisionMsg);
		if(newExplorationPoint.getExploreFlag()==false){
			newExplorationPoint.setExploreFlag(true);
			newExplorationPoint.setName("successor name");
			newExplorationPoint.setPath("successor path");
			newExplorationPoint.setMsg("exploration comments");
			System.out.println("Marked as exploration point, new successor is: " + newExplorationPoint.getName());
		}else {
			System.out.println("Do you want to add another design alternative to this design?");
			//
		}
		
//		try{
//			 IStructuredSelection  selection  =  (IStructuredSelection)  HandlerUtil.
//			 getCurrentSelectionChecked(event);
//			 final  IContainer  c  =  (IContainer)  selection.getFirstElement();
//			 System.out.println("ExploreCommandHandler + "+c);
//		}catch(Exception e)
//		{e.printStackTrace();}
				
		return null;
	}

	//write those to model base table	
	public void setExploreFlag(boolean isExplorationPoint){
		this.isExplorationPoint = isExplorationPoint;
	}
	
	public Boolean getExploreFlag(){				
		return isExplorationPoint;		
	}
	
	public void setName(String baselineName){
		this.successorName = baselineName;
	}
	
	public String getName(){
		return successorName;
	}
	
	public void setPath(String successorPath){
		this.successorPath = successorPath;
	}
	
	public String getPath(){
		return successorPath;
	}
	
	public void setMsg(String decisionMsg){
		this.decisionMsg = decisionMsg;		
	}
	
	public String getMsg(){
		return decisionMsg;
	}


}
