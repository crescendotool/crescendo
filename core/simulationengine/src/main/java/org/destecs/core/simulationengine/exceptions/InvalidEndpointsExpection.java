package org.destecs.core.simulationengine.exceptions;

import java.net.URL;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;

public class InvalidEndpointsExpection extends SimulationException
{

	public InvalidEndpointsExpection(Simulator simulator,URL url)
	{
		super(simulator, url==null?"":url.toString());
	}
	
	public InvalidEndpointsExpection(Simulator simulator,String message,Throwable exception)
	{
		super(simulator, message,exception);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
