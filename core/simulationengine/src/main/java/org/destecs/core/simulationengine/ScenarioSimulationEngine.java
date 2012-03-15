/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.core.simulationengine;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import org.destecs.core.scenario.Action;
import org.destecs.core.scenario.Scenario;
import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.core.simulationengine.model.DeModelConfig;
import org.destecs.core.simulationengine.model.ModelConfig;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.structs.SetParametersparametersStructParam;
import org.destecs.protocol.structs.StepinputsStructParam;

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
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy, List<StepinputsStructParam> inputs, Boolean singleStep, List<String> events)
			throws SimulationException
	{
		super.beforeStep(nextStepEngine, nextTime, dtProxy, ctProxy, inputs, singleStep,events);

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
						SetParametersparametersStructParam parm = new SetParametersparametersStructParam(action.variableName, new Vector<Double>(Arrays.asList(new Double[] { action.variableValue })), new Vector<Integer>(Arrays.asList(new Integer[] { 1 })));
						List<SetParametersparametersStructParam> list = new Vector<SetParametersparametersStructParam>();
						list.add(parm);
						ctProxy.setParameters(list);
					} catch (Exception e)
					{
						abort(Simulator.CT, "setParameter("
								+ action.variableName + "="
								+ action.variableValue + ") faild", e);
					}
					break;
				case DE:
					try
					{
						engineInfo(Simulator.DE, "Setting parameter (Next time="
								+ nextTime + "): " + action);
						messageInfo(Simulator.DE, nextTime, "setParameter");
						SetParametersparametersStructParam parm = new SetParametersparametersStructParam(action.variableName, new Vector<Double>(Arrays.asList(new Double[] { action.variableValue })), new Vector<Integer>(Arrays.asList(new Integer[] { 1 })));
						List<SetParametersparametersStructParam> list = new Vector<SetParametersparametersStructParam>();
						list.add(parm);
						dtProxy.setParameters(list);
					} catch (Exception e)
					{
						abort(Simulator.DE, "setParameter("
								+ action.variableName + "="
								+ action.variableValue + ") faild", e);
					}
					break;

			}
		}
	}
	
	@Override
	protected boolean loadModel(Simulator simulator, ProxyICoSimProtocol proxy,
			ModelConfig model) throws SimulationException
	{
		if(simulator==Simulator.DE && model instanceof DeModelConfig)
		{
			model.arguments.put(DeModelConfig.LOAD_SETTING_DISABLE_OPTIMIZATION, "true");
		}
		return super.loadModel(simulator, proxy, model);
	}
}
