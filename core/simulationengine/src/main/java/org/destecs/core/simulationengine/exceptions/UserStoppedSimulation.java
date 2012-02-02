package org.destecs.core.simulationengine.exceptions;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;

public class UserStoppedSimulation extends SimulationException
{

	public UserStoppedSimulation(Simulator simulator)
	{
		super(simulator, "User stopped simulation");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
