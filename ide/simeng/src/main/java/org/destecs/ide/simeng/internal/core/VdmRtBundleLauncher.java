package org.destecs.ide.simeng.internal.core;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.ISimulatorLauncher;
import org.destecs.ide.simeng.ISimengConstants;
import org.eclipse.core.runtime.CoreException;

public class VdmRtBundleLauncher implements ISimulatorLauncher
{
	private Process p;
	private File dir;
	private final Integer port;

	public VdmRtBundleLauncher(File dir, Integer port)
	{
		this.dir = dir;
		this.port = port;
	}

	public void kill()
	{
		if (p != null)
		{
			p.destroy();
		}

	}

	public boolean launch()
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
			return true;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	private String getArgumentString(List<String> args)
	{
		StringBuffer executeString = new StringBuffer();
		for (String string : args)
		{
			executeString.append(string);
			executeString.append( " ");
		}
		return executeString.toString().trim();

	}

	private List<String> getClassPath() throws CoreException
	{
		List<String> commandList = new Vector<String>();
		List<String> entries = new Vector<String>();
		// get the bundled class path of the debugger
		ClasspathUtils.collectClasspath(new String[] { ISimengConstants.VDM_ENGINE_BUNDLE_ID }, entries);
		// get the class path for all jars in the project lib folder
		// File lib = new File(project.getLocation().toFile(), "lib");
		// if (lib.exists() && lib.isDirectory())
		// {
		// for (File f : getAllFiles(lib))
		// {
		// if (f.getName().toLowerCase().endsWith(".jar"))
		// {
		// entries.add(toPlatformPath(f.getAbsolutePath()));
		// }
		// }
		// }

		if (entries.size() > 0)
		{
			commandList.add("-cp");
			StringBuffer classPath = new StringBuffer(" ");
			for (String cp : entries)
			{
				if (cp.toLowerCase().replace("\"", "").trim().endsWith(".jar"))
				{
					classPath.append( toPlatformPath(cp));
					classPath.append( getCpSeperator());
				}
			}
			classPath.deleteCharAt( classPath.length() - 1);
			commandList.add(classPath.toString().trim());

		}
		return commandList;
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

}
