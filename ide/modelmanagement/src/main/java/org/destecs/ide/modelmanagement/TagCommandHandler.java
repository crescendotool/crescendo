package org.destecs.ide.modelmanagement;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class TagCommandHandler extends AbstractHandler {
	
	private boolean isBaseline;	
	private String baselineName;
	private String relativePath;
	private String decisionMsg;
	
	public TagCommandHandler()
	{}
	
	//will put this constructor(with lots arguments) to a super class, and call it from here, should not in Handler
	public TagCommandHandler(boolean isBaseline, String baselineName, String relativePath, String decisionMsg){
		this.isBaseline = isBaseline;
		this.baselineName = baselineName;
		this.relativePath = relativePath;
		this.decisionMsg = decisionMsg;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		//tag as Baseline, will invoke subversion tag
		//TODO get resource, query model base
		
		System.out.println("TagCommandHandler");
	
		TagCommandHandler newBaseline = new TagCommandHandler(isBaseline, baselineName, relativePath, decisionMsg);
		
		if(newBaseline.getBaselineFlag()==false){
			newBaseline.setBaselineFlag(true);
			newBaseline.setName("baseline name");
			newBaseline.setPath("baseline path");
			newBaseline.setMsg("baseline comments");
			System.out.println("This project is tagged as co-sim baseline.");
		}else{
			System.out.println("error msg: this project is already tagged as co-sim baseline.");
		}
		
		
		return null;
	}

	//write those to model base table	
	public void setBaselineFlag(boolean isBaseline){
		this.isBaseline = isBaseline;
	}
	
	public Boolean getBaselineFlag(){				
		return isBaseline;		
	}
	
	public void setName(String baselineName){
		this.baselineName = baselineName;
	}
	
	public String getName(){
		return baselineName;
	}
	
	public void setPath(String relativePath){
		this.relativePath = relativePath;
	}
	
	public String getPath(){
		return relativePath;
	}
	
	public void setMsg(String decisionMsg){
		this.decisionMsg = decisionMsg;		
	}
	
	public String getMsg(){
		return decisionMsg;
	}

}


