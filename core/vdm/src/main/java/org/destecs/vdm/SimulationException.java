package org.destecs.vdm;

import java.io.Serializable;

public class SimulationException extends Exception implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SimulationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SimulationException(String message)
	{
		super(message);
	}

}
