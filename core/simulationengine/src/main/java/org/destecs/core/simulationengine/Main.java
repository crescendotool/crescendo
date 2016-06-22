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
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.destecs.core.parsers.ScenarioParserWrapper;
import org.destecs.core.scenario.Scenario;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.core.simulationengine.exceptions.ModelPathNotValidException;
import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.core.simulationengine.launcher.Clp20SimLauncher;
import org.destecs.core.simulationengine.launcher.VdmRtLauncher;
import org.destecs.core.simulationengine.listener.IEngineListener;
import org.destecs.core.simulationengine.listener.IMessageListener;
import org.destecs.core.simulationengine.listener.ISimulationListener;
import org.destecs.core.simulationengine.model.CtModelConfig;
import org.destecs.core.simulationengine.model.DeModelConfig;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;

public class Main
{
	static boolean useScenario = false;

	static String modelName = "WatertankPeriodic";
	private static String ctModelName = "Watertank.emx";

	static String ws = "C:\\overture\\runtime-destecs.product 8\\";
	static File base = new File(ws + modelName);

	public static SimulationEngine getEngine()
			throws ModelPathNotValidException, IOException
	{
		SimulationEngine engine = null;
		if (useScenario)
		{
			Scenario scenario = new ScenarioParserWrapper().parse(new File(base.getAbsolutePath()
					+ "\\scenarios\\scenario1.script"));
			engine = new ScenarioSimulationEngine(new File(base, "configuration\\contract.csc"), scenario);
		} else
		{
			engine = new SimulationEngine(new File(base, "configuration\\contract.csc"));
		}

		engine.setDeSimulationLauncher(new VdmRtLauncher());
		engine.setDeModel(new DeModelConfig(new File(base, "model_de")));
		engine.setDeEndpoint(new URL("http://127.0.0.1:8080/xmlrpc"));

		engine.setCtSimulationLauncher(new Clp20SimLauncher());
		engine.setCtModel(new CtModelConfig(new File(new File(base, "model_ct"), ctModelName)));
		engine.setCtEndpoint(new URL("http://localhost:1580"));

		Listener listener = new Listener();
		engine.engineListeners.add(listener);
		engine.messageListeners.add(listener);
		engine.simulationListeners.add(listener);

		return engine;
	}

	/**
	 * @param args
	 * @throws SimulationException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SimulationException,
			IOException
	{
		SimulationEngine engine = getEngine();

		File sharedDesignParamFile = new File(base, modelName + ".sdp");
		final List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = loadSharedDesignParameters(sharedDesignParamFile);

		engine.simulate(shareadDesignParameters, 30);
	}

	private static List<SetDesignParametersdesignParametersStructParam> loadSharedDesignParameters(
			File sharedDesignParamFile)
	{
		List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();
		
		List<Double> minLevel = new Vector<Double>();
		minLevel.add(1.0);
		
		List<Double> maxLevel = new Vector<Double>();
		maxLevel.add(2.0);
		
		List<Integer> size = new Vector<Integer>();
		size.add(1);
		
		shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("minlevel",size, minLevel));
		shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("maxlevel",size, maxLevel));
		return shareadDesignParameters;
	}

	private static class Listener implements IEngineListener, IMessageListener,
			ISimulationListener
	{
		private String pad(String text, int count)
		{
			StringBuffer buf = new StringBuffer(text);
			while (buf.length() < count)
			{
				buf.append(" ");
			}
			return buf.toString();
		}

		public void info(Simulator simulator, String message)
		{
			System.out.println(pad(simulator + ": ", 20) + message);

		}

		public void from(Simulator simulator, Double time, String messageName)
		{
			// System.out.println(simulator+": "+messageName);
		}

		public void stepInfo(Simulator simulator, StepStruct result)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(simulator + ": ");
			for (StepStructoutputsStruct o : result.outputs)
			{
				sb.append(o.name + "=" + o.value + " ");
			}
			System.out.println(sb.toString() + "\t\t\t" + result.time);
		}

	}

}
