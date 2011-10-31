package org.destecs.core.simulationengine;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import org.destecs.core.scenario.Action;
import org.destecs.core.scenario.Scenario;
import org.destecs.core.simulationengine.exceptions.SimulationException;
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
			throws SimulationException
	{
		super.beforeStep(nextStepEngine, nextTime, dtProxy, ctProxy);

		while (!actions.isEmpty() && actions.peek().time <= nextTime)
		{
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
						//TODO:
						//ctProxy.setParameter(action.variableName, action.variableValue);
					} catch (Exception e)
					{
						abort(Simulator.CT, "setParameter("+action.variableName+"="+action.variableValue+") faild", e);
					}
					break;
				case DE:
					try
					{
						engineInfo(Simulator.DE, "Setting parameter (Next time="
								+ nextTime + "): " + action);
						messageInfo(Simulator.DE, nextTime, "setParameter");
						//TODO:
						//dtProxy.setParameter(action.variableName, action.variableValue);
					} catch (Exception e)
					{
						abort(Simulator.DE, "setParameter("+action.variableName+"="+action.variableValue+") faild", e);
					}
					break;

			}
		}
	}
}
