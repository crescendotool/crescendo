/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
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
