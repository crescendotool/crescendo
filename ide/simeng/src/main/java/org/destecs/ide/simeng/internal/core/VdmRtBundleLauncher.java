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
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.launcher.ISimulatorLauncher;
import org.destecs.ide.simeng.Activator;
import org.destecs.ide.simeng.ISimengConstants;
import org.eclipse.core.runtime.CoreException;

public class VdmRtBundleLauncher implements ISimulatorLauncher
{
	private Process p;
	private File dir;
	private final Integer port;
	private File libSearchRoot;

	public VdmRtBundleLauncher(File dir, Integer port, File libSearchRoot)
	{
		this.dir = dir;
		this.port = port;
		this.libSearchRoot = libSearchRoot;
	}

	public void kill()
	{
		if (p != null)
		{
			p.destroy();
		}

	}

	public Process launch()
	{
		List<String> commandList = new ArrayList<String>();
		commandList.add(0, "java");

		try
		{
			commandList.addAll(1, getClassPath());

			commandList.add(3, ISimengConstants.VDM_ENGINE_CLASS);
			// commandList.addAll(1, getVmArguments(preferences));
			commandList.add("-p");
			commandList.add(port.toString());

			p = Runtime.getRuntime().exec(getArgumentString(commandList), null, dir);

			new ProcessConsolePrinter(p.getInputStream()).start();
			new ProcessConsolePrinter(p.getErrorStream()).start();
			return p;
		} catch (IOException e)
		{
			Activator.log("IO Error when running bundle launcher for VDM-RT", e);
		} catch (CoreException e1)
		{
			Activator.log("Unknown error in bundle luncher for VDM-RT", e1);
		}
		return null;
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

	private List<String> getClassPath() throws CoreException
	{
		List<String> commandList = new Vector<String>();
		List<String> entries = new Vector<String>();
		// get the bundled class path of the debugger
		ClasspathUtils.collectClasspath( ISimengConstants.VDM_ENGINE_BUNDLE_IDS , entries);
		// get the class path for all jars in the project lib folder
		File lib = libSearchRoot;// new File(project.getLocation().toFile(), "lib");
		if (lib.exists() && lib.isDirectory())
		{
			for (File f : getAllFiles(lib))
			{
				if (f.getName().toLowerCase().endsWith(".jar"))
				{
					entries.add(f.getAbsolutePath());
				}
			}
		}

		if (entries.size() > 0)
		{
			commandList.add("-cp");
			StringBuffer classPath = new StringBuffer(" ");
			for (String cp : entries)
			{
				if (cp.toLowerCase().replace("\"", "").trim().endsWith(".jar"))
				{
					classPath.append(toPlatformPath(cp));
					classPath.append(getCpSeperator());
				}
			}
			classPath.deleteCharAt(classPath.length() - 1);
			commandList.add(classPath.toString().trim());

		}
		return commandList;
	}

	private static List<File> getAllFiles(File file)
	{
		List<File> files = new Vector<File>();
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				files.addAll(getAllFiles(f));
			}

		} else
		{
			files.add(file);
		}
		return files;
	}

	private String getCpSeperator()
	{
		if (isWindowsPlatform())
			return ";";
		else
			return ":";
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

	/**
	 * Returns a free port number on localhost, or -1 if unable to find a free port.
	 * 
	 * @return a free port number on localhost, or -1 if unable to find a free port
	 */
	public static Integer getFreePort()
	{
		ServerSocket socket = null;
		try
		{
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e)
		{
		} finally
		{
			if (socket != null)
			{
				try
				{
					socket.close();
				} catch (IOException e)
				{
				}
			}
		}
		return -1;
	}

	public boolean isRunning()
	{
		try
		{
			if(p!=null)
			{
				p.exitValue();
			}
			return false;
		} catch (IllegalThreadStateException e)
		{
			return true;
		}
	}

	public String getName()
	{
		return ISimengConstants.VDM_ENGINE_CLASS;
	}

}
