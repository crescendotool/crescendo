package org.destecs.core.scenario;

import java.util.ArrayList;
import java.util.List;

public class ScenarioFactory {

	private List<Action> actions = null;
	
	public ScenarioFactory(){
		actions = new ArrayList<Action>();
		
	}
	
	public void addAction(Action a){
		actions.add(a);
	}
	
	public Scenario getScenario(){
		return new Scenario(this.actions);		
	}
	
}
