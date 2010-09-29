package org.destecs.vdm;

public class SimulationException extends Exception
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
