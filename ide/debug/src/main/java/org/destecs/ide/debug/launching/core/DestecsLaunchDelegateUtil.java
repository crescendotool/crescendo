package org.destecs.ide.debug.launching.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;

public class DestecsLaunchDelegateUtil
{
	public static String getOutputPreFix(ILaunchConfiguration configuration)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		return dateFormat.format(new Date()) + "_" + configuration.getName();
	}

	public static File getFileFromPath(IProject project, String path)
			throws IOException
	{
		if (path == null || path.isEmpty())
		{
			return null;
		}

		IResource r = project.findMember(new Path(path));

		if (r != null && !r.equals(project))
		{
			return r.getLocation().toFile();
		}
		throw new IOException("Faild to find file: " + path);
	}

	public static IFile filterPlots(File ctFile, File outputFile)
	{

		BufferedReader br = null;
		PrintWriter pw = null;

		try
		{
			br = new BufferedReader(new FileReader(ctFile));
			pw = new PrintWriter(new FileWriter(outputFile));

			String line;
			boolean skip = false;
			while ((line = br.readLine()) != null)
			{
				if (!skip || line.trim().equals("</PlotSpecs>"))
				{
					pw.println(line);
					skip = false;
				}
				if (line.trim().equals("<PlotSpecs>"))
				{
					skip = true;
				}

			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (IOException e)
			{
			}
			pw.close();
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(outputFile.getAbsolutePath());
		IFile file = workspace.getRoot().getFileForLocation(location);

		return file;
	}
}
