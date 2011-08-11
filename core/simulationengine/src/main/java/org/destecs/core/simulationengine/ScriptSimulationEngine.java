package org.destecs.core.simulationengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Iterator;

//import org.destecs.core.scenario.Action;
//import org.destecs.core.scenario.Scenario;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.destecs.core.contract.Contract;
import org.destecs.core.dcl.*;
//import org.destecs.core.parsers.dcl.ScriptLexer;
//import org.destecs.core.parsers.dcl.ScriptParser;
import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.core.simulationengine.exceptions.SimulationException;

import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepinputsStructParam;

public class ScriptSimulationEngine extends SimulationEngine
{
	Script script;//list of actions
	public Queue<Action> actions;
	boolean flag = false;	
//	public StepStruct result;
	
	public ScriptSimulationEngine(File contractFile, Script script)
	{
		super(contractFile);
		this.script = script;

		actions = new LinkedList<Action>();
		actions.addAll(this.script.actions);
		
	}
	
	@Override
	public StepStruct step(Simulator simulator, ProxyICoSimProtocol dtProxy,
			ProxyICoSimProtocol ctProxy, Double outputTime,
			List<StepinputsStructParam> inputs, Boolean singleStep,
			List<String> events) throws SimulationException
	{
		beforeStep(simulator, outputTime, dtProxy, ctProxy);
		messageInfo(simulator, outputTime, "step");
		StepStruct result = null;
		try
		{
			if (simulator == Simulator.CT)
			{
				result = ctProxy.step(outputTime, inputs, singleStep, events);
			} else if (simulator == Simulator.DE)
			{
				result = dtProxy.step(outputTime, inputs, singleStep, events);
			}
		} catch (Exception e)
		{
			abort(simulator, "step failed(time = " + outputTime + " inputs=["
					+ inputs.toString().replaceAll("\n", ", ")
					+ "], singleStep = " + singleStep + ", events = " + events
					+ ")", e);
		}
		simulationInfo(simulator, result);
		System.out.println("--- step ---");
		System.out.println("result outputs size: " + result.outputs.size());// added
		System.out.println(result.outputs);
			
//		this.result = result;		
//		System.out.println("result outputs size: " + this.result.outputs.size());// added
//		System.out.println(this.result.outputs);
		
		
//		if(result.outputs.get(0).name.matches("valve")&& flag==true){
//			System.out.println("**value before**" + result.outputs.get(0).value); 
//			result.outputs.get(0).value = 1.0;
////			result.outputs.get(0).toMap().put("value", double (1.0));
//			System.out.println("**value after**" + result.outputs.get(0).value); 
//			flag = false;
//		}
//		
//		if (result.outputs.get(0).name.matches("level")){
//			System.out.println("inside the loop"); 
//			Double internal = result.outputs.get(0).value;
//			System.out.println("current level value is:" + internal);
//			if (internal > 2.0){
//				flag = true;				
//			}			
//		}		
		
		int counter = 0;
		Action middle = null;
		//----------non-time triggered--------------
		if( actions.peek()!= null && actions.peek().time == 0.0 ){
			
			System.out.println("---Counter:  " + counter);
			System.out.println(actions.toString());			
			Action expression = actions.peek();
			Iterator<Action> it = actions.iterator();
						
			if (result.outputs.get(0).name.matches(expression.variableName)){
				System.out.println("inside the loop"); 
				Double internal = result.outputs.get(0).value;
				System.out.println("current level value is:" + internal);
				if (internal > expression.variableValue){
					flag = true;	
					it.next();// the first one of the queue
					middle = it.next(); // the next one
					System.out.println("next action is :" + middle.toString());
					actions.poll();
				}					
			}
			
			if (flag == true){
				System.out.println("flag");				
				System.out.println("next action variableName is :" + actions.peek().variableName);
				if(result.outputs.get(0).name.matches(actions.peek().variableName) ){
					System.out.println("**value before**" + result.outputs.get(0).value); 
					result.outputs.get(0).value = actions.peek().variableValue;
		//			result.outputs.get(0).toMap().put("value", double (1.0));
					System.out.println("**value after**" + result.outputs.get(0).value); 
					flag = false;
				}
			}
			
			counter++;
			
			
			
		}
		
		return result;
		
	}
	
	
	
	
	
	@Override
	protected void beforeStep(Simulator nextStepEngine, Double nextTime,
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy)
			throws SimulationException
	{
		super.beforeStep(nextStepEngine, nextTime, dtProxy, ctProxy);
		System.out.println("---before step 0---");
		System.out.println("Time:" + nextTime);
//		Iterator it = actions.iterator();
	    System.out.println("Initial Size of Queue :" + actions.size());
	    System.out.println(actions.toString());
	    // not working?!
//	    while(it.hasNext())
//        {
//            String iteratorValue = (String)it.next();
//            System.out.println("Queue Next Value :" + iteratorValue);
//        }		

		// *******time-triggered*******
		if( actions.peek().time > 0.0){
			
		while (!actions.isEmpty() && actions.peek().time <= nextTime)
		{
			System.out.println("---before step 1---");
			Action action = actions.poll();
			switch (action.targetSimulator)
			{
				case ALL:
					break;
				case CT:
					try
					{
						engineInfo(Simulator.CT, "Setting parameter (Next time="
								+ nextTime + "): " + action);
						messageInfo(Simulator.CT, nextTime, "setParameter");
//						ctProxy.setParameter(action.variableName, action.variableValue);						
						
//						System.out.println("result outputs size: " + this.result.outputs.size());// added
//						System.out.println(this.result.outputs);
//						
//						if(this.result.outputs.get(0).name.matches(action.variableName)&& flag==true){
//							System.out.println("**value before**" + this.result.outputs.get(0).value); 
//							this.result.outputs.get(0).value = action.variableValue;
//	//						result.outputs.get(0).toMap().put("value", double (1.0));
//							System.out.println("**value after**" + this.result.outputs.get(0).value); 
//							flag = false;
//						}
//					
//						if (this.result.outputs.get(0).name.matches(action.variableName)){
//							System.out.println("inside the loop"); 
//							Double internal = this.result.outputs.get(0).value;
//							System.out.println("current level value is:" + internal);
//							if (internal > action.variableValue){
//								flag = true;		
//								System.out.println("action.variableValue" + action.variableValue);
//							}			
//						}
											
						
					} catch (Exception e)
					{
						abort(Simulator.CT, "setParameter(" + action.variableName + "=" + action.variableValue + ") failed", e);
					}
					break;
				case DE:
					try
					{
						engineInfo(Simulator.DE, "Setting parameter (Next time="
								+ nextTime + "): " + action);
						messageInfo(Simulator.DE, nextTime, "setParameter");
						dtProxy.setParameter(action.variableName, action.variableValue);
					} catch (Exception e)
					{
						abort(Simulator.DE, "setParameter("+action.variableName+"="+action.variableValue+") failed", e);
					}
					break;			
		
			}	
		}
			
//			switch (action.condition)
//			{
//				case WHEN:
//				try
//				{
//					System.out.println("---when condition--1-");
////					engineInfo(Simulator.DE, "Setting parameter (Next time="
////		 					+ nextTime + "): " + action);
////					engineInfo(Simulator.DE, "Setting parameter ( " + action.variableName
////		 					+ "): " + action);
//					System.out.println("---when condition--2-");// why not here?
//					messageInfo(Simulator.CT, nextTime, "setParameter");	
//					System.out.println("---when condition--3-");// why not here?
////					System.out.println("dtProxy parameters "+ dtProxy.getParameters());// check what kind of parameters that exist in the list
//					System.out.println("variable value: "+ action.variableValue);
////					System.out.println("ctProxy parameter value: "+ ctProxy.getParameter(action.variableName).value);
//
////					ctProxy.getParameter(action.variableName);
////					dtProxy.getParameter(action.variableName);
//					
//					System.out.println(dtProxy.getParameter(action.variableName).value);
//					dtProxy.setParameter("valve2", 1.0);
//				    
////					if(  dtProxy.getParameter(action.variableName).value== action.variableValue)//hard coding here
////					{
////						System.out.println("dtProxy.getParameter");
////						ctProxy.setParameter(action.variableName, action.variableValue);
////					}					
//					
//				} catch (Exception e)
//				{
//					abort(Simulator.DE, "getParameter("+action.variableName+"="+action.variableValue+") failed", e);
//				}
//				break;					
//			}
//			
		}	
	}	
	
}
