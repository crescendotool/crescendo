package org.destecs.core.dcl;

import java.util.ArrayList;
import java.util.List;

public class ScriptFactory {

	private List<Action> actions = null;
	
	public ScriptFactory(){
		actions = new ArrayList<Action>();
		
	}
	
	public void addAction(Action a){
		actions.add(a);
	}
	
	public Script getScript(){
		return new Script(this.actions);		
	}
	
}