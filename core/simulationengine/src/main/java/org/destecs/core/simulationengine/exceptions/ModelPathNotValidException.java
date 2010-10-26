package org.destecs.core.simulationengine.exceptions;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;


public class ModelPathNotValidException extends SimulationException
{

	public ModelPathNotValidException(Simulator simulator,String model)
	{
		super(simulator, model);
	}
	
	public ModelPathNotValidException(Simulator simulator,String message,Throwable exception)
	{
		super(simulator, message,exception);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1801621338317239126L;

}
