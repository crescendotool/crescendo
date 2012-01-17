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
import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.core.simulationengine.script.ISimulatorControl;
import org.destecs.core.simulationengine.script.ScriptEvaluator;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;
import org.destecs.script.ast.node.INode;

public class ScriptSimulationEngine extends SimulationEngine
{
	public class SimulationInterpreter implements ISimulatorControl
	{

		public void setVariable(Simulator simulator, String name, boolean value)
		{
			boolean found = false;
			if (inAfter && result != null)
			{
				for (StepStructoutputsStruct out : result.outputs)
				{
					if (out.name.equals(name))
					{
						out.value.clear();
						out.value.add(value == true ? 1.0 : 0.0);
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
							// ctProxy.setParameter(name, new Vector<Double>(Arrays.asList(new Double[] { value ==true ?
							// 1.0:0.0 })), new Vector<Integer>(Arrays.asList(new Integer[] { 1 })));
							break;
						case DE:

							deProxy.setParameter(name, new Vector<Double>(Arrays.asList(new Double[] { value == true ? 1.0
									: 0.0 })), new Vector<Integer>(Arrays.asList(new Integer[] { 1 })));
							break;

					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		public void setVariable(Simulator simulator, String name, double value)
		{
			boolean found = false;
			if (inAfter && result != null)
			{
				for (StepStructoutputsStruct out : result.outputs)
				{
					if (out.name.equals(name))
					{
						out.value.clear();
						out.value.add(value);
						found = true;
					}
				}
			}

			if (!found)
			{
				try
				{
					switch (simulator)
					{
						case CT:
							// ctProxy.setParameter(name, new Vector<Double>(Arrays.asList(new Double[] { value ==true ?
							// 1.0:0.0 })), new Vector<Integer>(Arrays.asList(new Integer[] { 1 })));
							break;
						case DE:
							deProxy.setParameter(name, new Vector<Double>(Arrays.asList(new Double[] { value })), new Vector<Integer>(Arrays.asList(new Integer[] { 1 })));
							break;

					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}

		public double getSystemTime()
		{
			if (result != null)
			{
				return result.time;
			}
			return 0;
		}

		public void showMessage(String type, String message)
		{
			engineInfo(Simulator.ALL, type + " " + message);
		}

		public void quite()
		{
			engineInfo(Simulator.ALL, "Quite by script");
			forceSimulationStop();
		}

		public Object getVariableValue(Simulator simulator, String name)
		{
			if (inAfter && result != null)
			{
				for (StepStructoutputsStruct out : result.outputs)
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
						// ctProxy.setParameter(name, new Vector<Double>(Arrays.asList(new Double[] { value ==true ?
						// 1.0:0.0 })), new Vector<Integer>(Arrays.asList(new Integer[] { 1 })));
						break;
					case DE:

						return deProxy.getParameter(name);

				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public void scriptError(String string)
		{
			engineInfo(Simulator.ALL, "Error in script: "+string);
			quite();
		}

	}

	final public List<INode> script = new Vector<INode>();
	boolean inAfter = false;

	public StepStruct result;
	ScriptEvaluator eval = new ScriptEvaluator(new SimulationInterpreter());
	ProxyICoSimProtocol dtProxy;
	ProxyICoSimProtocol ctProxy;

	public ScriptSimulationEngine(File contractFile, List<INode> script)
	{
		super(contractFile);
		this.script.clear();
		this.script.addAll(script);

	}

	@Override
	protected StepStruct afterStep(Simulator simulator, StepStruct result)
	{
		super.afterStep(simulator, result);
		this.result = result;
		this.inAfter = true;
		for (INode stm : script)
		{
			stm.apply(eval);
		}
		return result;
	}

	@Override
	protected void beforeStep(Simulator nextStepEngine, Double nextTime,
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy)
			throws SimulationException
	{
		super.beforeStep(nextStepEngine, nextTime, dtProxy, ctProxy);
		this.inAfter = false;
		this.ctProxy = ctProxy;
		this.deProxy = dtProxy;
		for (INode stm : script)
		{
			stm.apply(eval);
		}
	}

}
