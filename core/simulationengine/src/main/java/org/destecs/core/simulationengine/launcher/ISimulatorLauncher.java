package org.destecs.core.simulationengine.launcher;

import java.io.IOException;

public interface ISimulatorLauncher
{
	/**
	 * Launches the process 
	 * @return returns the launched process or null if not available
	 * @exception throws IOException if launch fails
	 */
	public Process launch() throws IOException;

	public void kill();
	
	public boolean isRunning();
	
	public String getName();
}
