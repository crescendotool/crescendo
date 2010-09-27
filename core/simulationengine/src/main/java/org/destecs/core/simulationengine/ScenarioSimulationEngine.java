package org.destecs.core.simulationengine;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import org.destecs.core.simulationengine.senario.Action;
import org.destecs.core.simulationengine.senario.Scenario;
import org.destecs.protocol.ProxyICoSimProtocol;

public class ScenarioSimulationEngine extends SimulationEngine
{
	Scenario scenario;
	Queue<Action> actions;

	public ScenarioSimulationEngine(File contractFile, Scenario scenario)
	{
		super(contractFile);
		this.scenario = scenario;

		actions = new LinkedList<Action>();
		actions.addAll(this.scenario.actions);
	}

	@Override
	protected void beforeStep(Simulator nextStepEngine, Double nextTime,
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy)
	{
		super.beforeStep(nextStepEngine, nextTime, dtProxy, ctProxy);

		while (!actions.isEmpty() && actions.peek().time >= nextTime)
		{
			Action action = actions.poll();
			switch (action.targetSimulator)
			{
				case ALL:
					break;
				case CT:
					engineInfo(Simulator.CT, "Setting parameter (Next time="+nextTime+"): "+action);
					messageInfo(Simulator.CT, "setParameter");
					ctProxy.setParameter(action.variableName, action.variableValue);
					break;
				case DT:
					engineInfo(Simulator.DT, "Setting parameter (Next time="+nextTime+"): "+action);
					messageInfo(Simulator.DT, "setParameter");
					dtProxy.setParameter(action.variableName, action.variableValue);
					break;

			}
		}
	}
}
