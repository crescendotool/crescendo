package org.destecs.core.simulationengine.launcher;

import java.io.IOException;

public class DummyLauncher implements ISimulatorLauncher
{
	String name="";
	
	public DummyLauncher(String name)
	{
		this.name = name;
	}

	public Process launch() throws IOException
	{
		return null;
	}

	public void kill()
	{
		
	}

	public boolean isRunning()
	{
		return false;
	}

	public String getName()
	{
		return "Dummy "+name;
	}

}
