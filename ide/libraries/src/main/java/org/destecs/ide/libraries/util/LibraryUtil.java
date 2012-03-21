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
package org.destecs.ide.libraries.util;

import java.io.File;
import java.io.IOException;

import org.destecs.ide.libraries.DestecsLibraryPlugin;
import org.destecs.ide.libraries.ILibrariesConstants;
import org.destecs.ide.libraries.store.Library;
import org.destecs.ide.libraries.wizard.LibrarySelection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;

@SuppressWarnings("restriction")
public class LibraryUtil
{
	public static void createSelectedLibraries(IProject prj,
			LibrarySelection selection) throws CoreException
	{
		IProject project = (IProject) prj.getAdapter(IProject.class);
		Assert.isNotNull(project, "Project could not be adapted");

		File projectRoot = project.getLocation().toFile();
		// File libFolder = new File(projectRoot,"lib");
		// if (!libFolder.exists())
		// libFolder.mkdirs();

		// copyFile(libFolder, "includes/lib/sl/IO.vdmsl", "IO."
		// + extension);

		for (Library lib : selection.getSelectedLibs())
		{
			try
			{
				for (String file : lib.deFiles)
				{
					if (selection.useLinkedLibs())
					{
						createLink(project, lib, file, "model_de");
					} else
					{
						copyFile(new File(projectRoot, "model_de"), lib.pathToFileRoot
								+ "/" + file, file);
					}

				}
				for (String file : lib.ctFiles)
				{
					if (selection.useLinkedLibs())
					{
						createLink(project, lib, file, "model_ct");
					} else
					{
						copyFile(new File(projectRoot, "model_de"), lib.pathToFileRoot
								+ "/" + file, file);
					}
				}
			} catch (IOException e)
			{
				DestecsLibraryPlugin.log("Failed to create library: "+ lib.name, e);
			}
		}

		project.refreshLocal(IResource.DEPTH_INFINITE, null);

	}

	public static void updateLink(IResource resource, Library lib, String file,
			String outputFolder,boolean refresh) throws CoreException
	{
		resource.delete(true, new NullProgressMonitor());
		IProject project = resource.getProject();
		createLink(project, lib, file, outputFolder);
		if(refresh)
		{
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
	}

	@SuppressWarnings("deprecation")
	private static void createLink(IProject project, Library lib, String file,
			String outputFolder) throws CoreException
	{
		IPath path = project.getFullPath().append("/" + outputFolder);
		path = path.append("/" + file.substring(0, file.indexOf('.')));
		path = path.addFileExtension(file.substring(file.indexOf('.') + 1));
		IFile newFile = createFileHandle(path);
		Bundle b = Platform.getBundle(ILibrariesConstants.PLUGIN_ID);
		String location = b.getLocation();
		// System.out.println("Bundle location: " + location);
		IPath systemfilePath = null;
		if (!location.startsWith("reference:file:/"))
		{
			// release mode
			systemfilePath = new Path("${ECLIPSE_HOME}/"
					+ location.substring(15));
			systemfilePath = systemfilePath.append("/");
			systemfilePath = systemfilePath.append(lib.pathToFileRoot);
			systemfilePath = systemfilePath.append("/");
			systemfilePath = systemfilePath.append(file);
		} else
		{
			// debug: The plug-in is not packed into the relase structure yet.
			systemfilePath = new Path(location.substring(16));
			systemfilePath = systemfilePath.append("/");
			systemfilePath = systemfilePath.append(lib.pathToFileRoot);
			systemfilePath = systemfilePath.append("/");
			systemfilePath = systemfilePath.append(file);
		}
		newFile.createLink(systemfilePath, IResource.REPLACE, null);
		newFile.setReadOnly(true);
	}

	private static void copyFile(File libFolder, String sourceLocation,
			String newName) throws IOException
	{
		String io = PluginFolderInclude.readFile(ILibrariesConstants.PLUGIN_ID, sourceLocation);
		PluginFolderInclude.writeFile(libFolder, newName, io);

	}

	protected static IFile createFileHandle(IPath filePath)
	{
		return IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(filePath);
	}
}
