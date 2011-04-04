package org.destecs.core.simulationengine.launcher;


public class VdmRtLauncher implements ISimulatorLauncher
{
	private int timeOut = 5000;
	public VdmRtLauncher()
	{
		
	}	
	
	public VdmRtLauncher(int timeOut)
	{
		this.timeOut = timeOut;
	}	


	public void kill()
	{

	}

	public Process launch()
	{
		System.out.println("Please launch VDM-RT co-sim now... waiting for 5 seconds");
		try
		{
			Thread.sleep(timeOut);
			System.out.println("VDM-RT co-sim times up if simulator is not started the simulation will fail");
		} catch (InterruptedException e)
		{
		}
		return null;
	}

	public boolean isRunning()
	{
		return false;
	}
	
	public String getName()
	{
		return "VDMJ-CoSimulation";
	}

}
