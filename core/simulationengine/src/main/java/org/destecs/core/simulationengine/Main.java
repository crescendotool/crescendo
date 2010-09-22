package org.destecs.core.simulationengine;

import java.io.File;
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
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.destecs.protocol.structs.StepStruct;

public class Main
{

	/**
	 * @param args
	 * @throws ModelPathNotValidException 
	 * @throws MalformedURLException 
	 * @throws InvalidSimulationLauncher 
	 * @throws InvalidEndpointsExpection 
	 */
	public static void main(String[] args) throws ModelPathNotValidException, MalformedURLException, InvalidEndpointsExpection, InvalidSimulationLauncher
	{
		SimulationEngine engine = new SimulationEngine();
		
		engine.setDtSimulationLauncher(new VdmRtLauncher());
		engine.setDtModel(new File("C:\\destecs\\workspace\\watertank_new\\model"));
		engine.setDtEndpoint(new URL("http://127.0.0.1:8080/xmlrpc"));
		
		engine.setCtSimulationLauncher(new Clp20SimLauncher());
		engine.setCtModel(new File("C:\\destecs\\workspace\\watertank_new\\WaterTank.emx"));
		engine.setCtEndpoint(new URL("http://localhost:1580"));

		
		List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters = new Vector<SetDesignParametersdesignParametersStructParam>();
		shareadDesignParameters
				.add(new SetDesignParametersdesignParametersStructParam(
						"minLevel", 1.0));
		shareadDesignParameters
				.add(new SetDesignParametersdesignParametersStructParam(
						"maxLevel", 2.0));
		
		
		Listener listener = new Listener();
		engine.engineListeners.add(listener);
		engine.messageListeners.add(listener);
		engine.simulationListeners.add(listener);
		
		engine.simulate(shareadDesignParameters, 5);
	}
	
	private static class Listener implements IEngineListener, IMessageListener, ISimulationListener
	{

		public void info(Simulator simulator, String message)
		{
			System.out.println(simulator+": "+message);
			
		}

		public void from(Simulator simulator, String messageName)
		{
			System.out.println(simulator+": "+messageName);
		}

		public void stepInfo(Simulator simulator, StepStruct result)
		{
			System.out.println(simulator+": "+result);
		}
		
	}

}
