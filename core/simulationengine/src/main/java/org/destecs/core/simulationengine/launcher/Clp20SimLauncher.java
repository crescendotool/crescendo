package org.destecs.core.simulationengine.launcher;

import org.destecs.core.simulationengine.ISimulatorLauncher;

public class Clp20SimLauncher implements ISimulatorLauncher
{

	public void kill()
	{

	}

	public Process launch()
	{
		System.out.println("Please launch CLP co-sim now with the model loaded... waiting for 5 seconds");
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
		}
		return null;
	}

	/**
	 * 
	 */
	public boolean isRunning()
	{
		return false;
	}

	public String getName()
	{
		return "20-Sim";
	}

}
