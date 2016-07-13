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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.core.simulationengine.model.DeModelConfig;
import org.destecs.core.simulationengine.model.ModelConfig;
import org.destecs.core.simulationengine.script.ISimulatorControl;
import org.destecs.core.simulationengine.script.ScriptEvaluator;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.structs.GetParametersStruct;
import org.destecs.protocol.structs.GetVariablesStruct;
import org.destecs.protocol.structs.GetVariablesStructvariablesStruct;
import org.destecs.protocol.structs.SetParametersparametersStructParam;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destecs.script.ast.node.INode;

/**
 * Custom simulation engine that evaluates a script as past of the stepping
 * 
 * @author kela
 */
public class ScriptSimulationEngine extends SimulationEngine
{
	public class SimulationInterpreter implements ISimulatorControl
	{

		public void setVariable(Simulator simulator, String name, boolean value)
		{
			setVariable(simulator, name, value == true ? 1.0 : 0.0);
		}

		public void setVariable(Simulator simulator, String name, double value)
		{
			boolean found = false;
			if (inAfter && resultAfter != null)
			{
				for (StepStructoutputsStruct out : resultAfter.outputs)
				{
					if (out.name.equals(name))
					{
						out.value.clear();
						out.value.add(value);
						engineInfo(simulator, "Replacing output (Next time="
								+ getSystemTime() + "): " + name + " = "
								+ value);
						found = true;
					}
				}
			}

			if (!inAfter && inputsBefore != null)
			{
				for (StepinputsStructParam out : inputsBefore)
				{
					if (out.name.equals(name))
					{
						out.value.clear();
						out.value.add(value);
						engineInfo(simulator, "Replacing output (Next time="
								+ getSystemTime() + "): " + name + " = "
								+ value);
						found = true;
					}
				}
			}

			if (!found)
			{
				try
				{
					engineInfo(simulator, "Setting parameter (Next time="
							+ getSystemTime() + "): " + name + " = " + value);
					messageInfo(simulator, getSystemTime(), "setParameter");
					switch (simulator)
					{
						case CT:
						{
							SetParametersparametersStructParam parm = new SetParametersparametersStructParam(name, new Vector<Integer>(Arrays.asList(new Integer[] { 1 })), new Vector<Double>(Arrays.asList(new Double[] { value })));
							List<SetParametersparametersStructParam> list = new Vector<SetParametersparametersStructParam>();
							list.add(parm);
							ctProxy.setParameters(list);
							found = true;
						}
							break;
						case DE:
						{
							SetParametersparametersStructParam parm = new SetParametersparametersStructParam(name, new Vector<Integer>(Arrays.asList(new Integer[] { 1 })), new Vector<Double>(Arrays.asList(new Double[] { value })));
							List<SetParametersparametersStructParam> list = new Vector<SetParametersparametersStructParam>();
							list.add(parm);
							deProxy.setParameters(list);
							found = true;
						}
							break;
					}
				} catch (Exception e)
				{
					scriptError("Faild to set variable: "+name+" = "+value);
				}
			}
			if(!found)
			{
				scriptError("Failure in setVariable. Not able to find \""+name+"\" as a shared variable or in the models");
			}

		}

		public double getSystemTime()
		{
			if (resultAfter != null)
			{
				return resultAfter.time;
			}
			return 0;
		}

		public void showMessage(String type, String message)
		{
			engineInfo(Simulator.ALL, type + " " + message);
			messageInfo(Simulator.ALL, getSystemTime(), type + " " + message);
		}

		public void quit()
		{
			engineInfo(Simulator.ALL, "Terminating simulation from script");
			forceSimulationStop();
		}

		public Object getVariableValue(Simulator simulator, String name)
		{
			if (inAfter && resultAfter != null)
			{
				for (StepStructoutputsStruct out : resultAfter.outputs)
				{
					if (out.name.equals(name))
					{
						return out.value.get(0);
					}
				}
			}

			if (!inAfter && inputsBefore != null)
			{
				for (StepinputsStructParam out : inputsBefore)
				{
					if (out.name.equals(name))
					{
						return out.value.get(0);
					}
				}
			}

			try
			{
				switch (simulator)
				{
					case CT:
					{
						GetVariablesStruct data = ctProxy.getVariables(Arrays.asList(new String[] { name }));

						if (!data.variables.isEmpty())
						{
							return data.variables.get(0).value.get(0);
						}
						
						//If data is empty maybe the variable is a parameter
						GetParametersStruct parameterData = ctProxy.getParameters(Arrays.asList(new String[] { name }));
						
						if (!parameterData.parameters.isEmpty())
						{
							return parameterData.parameters.get(0).value.get(0);
						}
						
					}
						break;
					case DE:
					{
						GetVariablesStruct data = deProxy.getVariables(Arrays.asList(new String[] { name }));

						if (!data.variables.isEmpty())
						{
							for (GetVariablesStructvariablesStruct p : data.variables)
							{
								if(p.name.equals(name))
								{
									return p.value.get(0);
								}
							}
						}
						scriptError("No apropiate parameter is returned from the simulator");
					}

				}
			} catch (Exception e)
			{
				scriptError("Failure in getVariableValue with "+name, e);
			}
			scriptError("Failure in getVariableValue, could not get value for: "+name);
			return null;
		}

		public void scriptError(String string)
		{
			engineInfo(Simulator.ALL, "Error in script: " + string);
			quit();
		}
		
		public void scriptError(String string, Exception e)
		{
			engineInfo(Simulator.ALL, "Error in script: " + string+" Exception: "+getStackTrace(e));
			quit();
		}
		
		private String getStackTrace(Throwable aThrowable) {
		    final Writer result = new StringWriter();
		    final PrintWriter printWriter = new PrintWriter(result);
		    aThrowable.printStackTrace(printWriter);
		    return result.toString();
		  }

	}

	final public List<INode> script = new Vector<INode>();
	boolean inAfter = false;

	public StepStruct resultAfter;
	ScriptEvaluator eval = new ScriptEvaluator(new SimulationInterpreter());
	ProxyICoSimProtocol dtProxy;
	ProxyICoSimProtocol ctProxy;
	private List<StepinputsStructParam> inputsBefore;

	public ScriptSimulationEngine(File contractFile, List<INode> script)
	{
		super(contractFile);
		this.script.clear();
		this.script.addAll(script);

	}

	@Override
	protected StepStruct afterStep(Simulator simulator, StepStruct result) throws SimulationException
	{
		if(simulator == Simulator.CT)
			return result;
		
		super.afterStep(simulator, result);
		this.resultAfter = result;
		this.inAfter = true;
		for (INode stm : script)
		{
			try {
				stm.apply(eval);
			} catch (Throwable e) {
				abort(simulator, "Internal Evaluation Error", e);
			}
		}
		return result;
	}

	@Override
	protected void beforeStep(Simulator nextStepEngine, Double nextTime,
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy,
			List<StepinputsStructParam> inputs, Boolean singleStep,
			List<String> events) throws SimulationException
	{
		
		super.beforeStep(nextStepEngine, nextTime, dtProxy, ctProxy, inputs, singleStep, events);
		this.inAfter = false;
		this.ctProxy = ctProxy;
		this.deProxy = dtProxy;
		this.inputsBefore = inputs;
		if(nextStepEngine == Simulator.CT || nextStepEngine == Simulator.DE || nextStepEngine == Simulator.ALL)
		{
			return ;
		}
		for (INode stm : script)
		{
			try {
				stm.apply(eval);
			} catch (Throwable e) {
				abort(nextStepEngine, "Internal Evaluation Error", e);
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
