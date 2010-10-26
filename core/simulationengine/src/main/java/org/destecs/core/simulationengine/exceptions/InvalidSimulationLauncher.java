package org.destecs.core.simulationengine.exceptions;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;

public class InvalidSimulationLauncher extends SimulationException
{

	public InvalidSimulationLauncher(Simulator simulator,String message)
	{
		super(simulator,message);
	}
	
	public InvalidSimulationLauncher(Simulator simulator,String message,Throwable exception)
	{
		super(simulator,message,exception);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
