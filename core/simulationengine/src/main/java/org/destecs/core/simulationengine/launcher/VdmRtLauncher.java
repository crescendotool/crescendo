package org.destecs.core.simulationengine.launcher;

import org.destecs.core.simulationengine.ISimulatorLauncher;

public class VdmRtLauncher implements ISimulatorLauncher
{

	public void kill()
	{
		
	}

	public void launch()
	{
		System.out.println("Please launch VDM-RT co-sim now... waiting for 5 seconds");
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
		}
		
	}

}
