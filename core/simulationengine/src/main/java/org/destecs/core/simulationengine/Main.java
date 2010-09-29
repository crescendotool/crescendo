package org.destecs.core.simulationengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.core.simulationengine.exceptions.InvalidEndpointsExpection;
import org.destecs.core.simulationengine.exceptions.InvalidSimulationLauncher;
import org.destecs.core.simulationengine.exceptions.ModelPathNotValidException;
import org.destecs.core.simulationengine.launcher.Clp20SimLauncher;
import org.destecs.core.simulationengine.launcher.VdmRtLauncher;
import org.destecs.core.simulationengine.senario.Scenario;
import org.destecs.core.simulationengine.senario.ScenarioParser;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;

public class Main
{
	static boolean useScenario = true;

	public static SimulationEngine getEngine() throws ModelPathNotValidException,
			MalformedURLException
	{
		SimulationEngine engine = null;
		if (useScenario)
		{
			Scenario scenario = new ScenarioParser(new File("C:\\destecs\\workspace\\watertank_new\\scenarios\\scenario1.script")).parse();
			engine = new ScenarioSimulationEngine(new File("C:\\destecs\\workspace\\watertank_new\\watertank.csc"), scenario );
		} else
		{
			engine = new SimulationEngine(new File("C:\\destecs\\workspace\\watertank_new\\watertank.csc"));
		}

		engine.setDtSimulationLauncher(new VdmRtLauncher());
		engine.setDtModel(new File("C:\\destecs\\workspace\\watertank_new\\model"));
		engine.setDtEndpoint(new URL("http://127.0.0.1:8080/xmlrpc"));

		engine.setCtSimulationLauncher(new Clp20SimLauncher());
		engine.setCtModel(new File("C:\\destecs\\workspace\\watertank_new\\WaterTank.emx"));
		engine.setCtEndpoint(new URL("http://localhost:1580"));

//		List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();
//		shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("minlevel", 1.0));
//		shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("maxlevel", 2.0));

		Listener listener = new Listener();
		engine.engineListeners.add(listener);
		engine.messageListeners.add(listener);
		engine.simulationListeners.add(listener);

		// engine.simulate(shareadDesignParameters, 5);
		return engine;
	}

	/**
	 * @param args
	 * @throws ModelPathNotValidException
	 * @throws MalformedURLException
	 * @throws InvalidSimulationLauncher
	 * @throws InvalidEndpointsExpection
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws ModelPathNotValidException,
			MalformedURLException, InvalidEndpointsExpection,
			InvalidSimulationLauncher, FileNotFoundException
	{
		SimulationEngine engine = getEngine();
//			new SimulationEngine(new File("C:\\destecs\\workspace\\watertank_new\\watertank.csc"));
//
//		engine.setDtSimulationLauncher(new VdmRtLauncher());
//		engine.setDtModel(new File("C:\\destecs\\workspace\\watertank_new\\model"));
//		engine.setDtEndpoint(new URL("http://127.0.0.1:8080/xmlrpc"));
//
//		engine.setCtSimulationLauncher(new Clp20SimLauncher());
//		engine.setCtModel(new File("C:\\destecs\\workspace\\watertank_new\\WaterTank.emx"));
//		engine.setCtEndpoint(new URL("http://localhost:1580"));
//
		List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();
		shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("minlevel", 1.0));
		shareadDesignParameters.add(new SetDesignParametersdesignParametersStructParam("maxlevel", 2.0));
//
//		Listener listener = new Listener();
//		engine.engineListeners.add(listener);
//		engine.messageListeners.add(listener);
//		engine.simulationListeners.add(listener);

		engine.simulate(shareadDesignParameters, 5);
	}

	private static class Listener implements IEngineListener, IMessageListener,
			ISimulationListener
	{
		private String pad(String text, int count)
		{
			while (text.length() < count)
			{
				text += " ";
			}
			return text;
		}

		public void info(Simulator simulator, String message)
		{
			System.out.println(pad(simulator + ": ", 20) + message);

		}

		public void from(Simulator simulator,Double time, String messageName)
		{
			// System.out.println(simulator+": "+messageName);
		}

		public void stepInfo(Simulator simulator, StepStruct result)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(simulator + ": ");
			for (StepStructoutputsStruct o : result.outputs)
			{
				sb.append(o.name + "=" + o.value);
			}
			System.out.println(sb.toString()+"\t\t"+result.time);
		}

	}

}
