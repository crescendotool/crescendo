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
package org.destecs.ide.simeng.internal.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.destecs.core.simulationengine.launcher.ISimulatorLauncher;
import org.destecs.ide.simeng.ISimengConstants;
import org.destecs.ide.simeng.internal.util.WindowsUtils;

public class Clp20SimProgramLauncher implements ISimulatorLauncher
{
	static final String processName = "20sim.exe";
	private static final int RETRIES = 10;
	private Process p;
	private File model;

	public Clp20SimProgramLauncher(File model)
	{
		this.model = model;
	}

	public void kill()
	{

	}

	public Process launch()
	{
		if (!isWindowsPlatform())
		{
			return null; // not supported
		}
		
		try
		{
			if (WindowsUtils.isProcessRunning(processName))
			{
				return null; // 20-sim is already running
			} else
			{
				String path = null;
				try
				{
					path = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,ISimengConstants.CLP_20_SIM_REGKEY,ISimengConstants.CLP_20_SIM_PATH_REGKEY );
					if(path == null)
					{
						path = WinRegistry.readString(WinRegistry.HKEY_LOCAL_MACHINE,ISimengConstants.CLP_20_SIM_REGKEY_x64,ISimengConstants.CLP_20_SIM_PATH_REGKEY );
						if(path == null)
						{
							return null;
						}
					}
				} catch (IllegalArgumentException e)
				{
					return null;
				} catch (IllegalAccessException e)
				{
					return null;
				} catch (InvocationTargetException e)
				{
					return null;
				}
				
				List<String> commandList = new ArrayList<String>();
				if(path !=null)
				{
					commandList.add(path);
					p = Runtime.getRuntime().exec(getArgumentString(commandList), null, model.getParentFile());
	
					new ProcessConsolePrinter(p.getInputStream()).start();
					new ProcessConsolePrinter(p.getErrorStream()).start();
	
					sleep(5000);
					return p;
				}
			}

		} catch (IOException e)
		{
			//ignore it
		}

		for (int i = 0; i < RETRIES; i++)
		{
			try
			{
				if (WindowsUtils.isProcessRunning(processName))
				{
					return null;
				}else
				{
					sleep(2000);
				}
			} catch (IOException e)
			{
				//ignore it
			}
		}
		return null;
	}

	private void sleep(int millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch (InterruptedException e)
		{
			// ignore it
		}
	}

	private String getArgumentString(List<String> args)
	{
		StringBuffer executeString = new StringBuffer();
		for (String string : args)
		{
			executeString.append(string);
			executeString.append(" ");
		}
		return executeString.toString().trim();

	}

	public static boolean isWindowsPlatform()
	{
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	protected static String toPlatformPath(String path)
	{
		if (isWindowsPlatform())
		{
			return "\"" + path + "\"";
		} else
		{
			return path.replace(" ", "\\ ");
		}
	}

	public boolean isRunning()
	{
		return false;// We don't know
	}

	public String getName()
	{
		return processName;
	}

}
