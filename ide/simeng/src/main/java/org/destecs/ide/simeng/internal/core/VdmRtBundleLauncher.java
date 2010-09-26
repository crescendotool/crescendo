package org.destecs.ide.simeng.internal.core;

import java.io.File;
import java.io.IOException;
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

	public VdmRtBundleLauncher(File dir)
	{
		this.dir = dir;
	}

	public void kill()
	{
		if (p != null)
		{
			p.destroy();
		}

	}

	public void launch()
	{
		List<String> commandList = new ArrayList<String>();
		commandList.add(0, "java");

		try
		{
			commandList.addAll(1, getClassPath());

			commandList.add(3, ISimengConstants.VDM_ENGINE_CLASS);
			// commandList.addAll(1, getVmArguments(preferences));

			p = Runtime.getRuntime().exec(getArgumentString(commandList), null, dir);
			
			new ProcessConsolePrinter(p.getInputStream()).start();
			new ProcessConsolePrinter(p.getErrorStream()).start();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private String getArgumentString(List<String> args)
	{
		String executeString = "";
		for (String string : args)
		{
			executeString += string + " ";
		}
		return executeString.trim();

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
			String classPath = " ";
			for (String cp : entries)
			{
				if (cp.toLowerCase().replace("\"", "").trim().endsWith(".jar"))
				{
					classPath += toPlatformPath(cp) + getCpSeperator();
				}
			}
			classPath = classPath.substring(0, classPath.length() - 1);
			commandList.add(classPath.trim());

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

}
