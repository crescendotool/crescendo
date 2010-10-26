package org.destecs.core.simulationengine.exceptions;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;

public class SimulationException extends Exception
{
	Simulator simulator;
	public SimulationException(Simulator simulator,String message)
	{
		super(message);
		this.simulator = simulator;
	}
	
	public SimulationException(Simulator simulator,String message,Throwable exception)
	{
		super(message,exception);
		this.simulator = simulator;
	}
	
	
	@Override
	public String getMessage()
	{
		return "[Source: +"+simulator.toString()+"] "+ super.getMessage();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
