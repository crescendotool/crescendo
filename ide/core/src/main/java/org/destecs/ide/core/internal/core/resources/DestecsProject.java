package org.destecs.ide.core.internal.core.resources;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.destecs.ide.core.DestecsCorePlugin;
import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.resources.IDestecsProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class DestecsProject implements
		org.destecs.ide.core.resources.IDestecsProject
{

	private static ArrayList<IProject> loadedProjects = new ArrayList<IProject>();

	public final IProject project;

	public DestecsProject(IProject project)
	{
		this.project = project;createStructure();
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	public static boolean isDestecsProject(IProject project)
	{

		try
		{
			if (project!=null && project.isAccessible() && project.isOpen() && project.hasNature(IDestecsCoreConstants.NATURE))
			{
				return true;
			}
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	public synchronized static Object createProject(String projectName,
			URI location)
	{
		Assert.isNotNull(projectName);
		Assert.isTrue(projectName.trim().length() > 0);

		IProject project = createBaseProject(projectName, location);
		try
		{
			addNature(project, IDestecsCoreConstants.NATURE);

			//String[] paths = { };//"parent/child1-1/child2", "parent/child1-2/child2/child3" }; //$NON-NLS-1$ //$NON-NLS-2$
			// addToProjectStructure(project, paths);
		} catch (CoreException e)
		{
			// VdmCore.log("VdmProject createProject", e);
			project = null;
		}

		return new DestecsProject(project);
	}

	public static void addNature(IProject project, String nature)
			throws CoreException
	{
		if (!project.hasNature(nature))
		{
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = nature;
			description.setNatureIds(newNatures);

			IProgressMonitor monitor = null;
			project.setDescription(description, monitor);
		}
	}

	/**
	 * Just do the basics: create a basic project.
	 * 
	 * @param location
	 * @param projectName
	 */
	private static IProject createBaseProject(String projectName, URI location)
	{
		// it is acceptable to use the ResourcesPlugin class
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

		if (!newProject.exists())
		{
			URI projectLocation = location;
			IProjectDescription desc = newProject.getWorkspace().newProjectDescription(newProject.getName());

			if (location != null
					&& ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location))
			{
				projectLocation = null;
			}

			desc.setLocationURI(projectLocation);
			try
			{
				newProject.create(desc, null);
				if (!newProject.isOpen())
				{
					newProject.open(null);
				}
			} catch (CoreException e)
			{
				DestecsCorePlugin.log("DestecsModelManager createBaseProject", e);
			}
		}

		return newProject;
	}

	private static IProject checkLoaded(IProject project)
	{
		for (IProject p : loadedProjects)
		{
			if (p.getName().equals(project.getName()))
			{
				return p;
			}
		}
		return null;
	}

	public synchronized static IDestecsProject createProject(IProject project)
	{

		IProject c = checkLoaded(project);

		if (c != null)
		{
			return (IDestecsProject) c;
		} else
		{
			IDestecsProject destecsProject = new DestecsProject(project);
			return destecsProject;
		}

		// if (ResourceManager.getInstance().hasProject(project))
		// return ResourceManager.getInstance().getProject(project);
		// else
		// {
		// try
		// {
		// IDestecsProject destecsProject = new DestecsProject(project);
		// return ResourceManager.getInstance().addProject(destecsProject);
		// } catch (Exception e)
		// {
		//				
		// DestecsCorePlugin.log("VdmModelManager createProject", e);
		//				
		// return null;
		// }
		// }
	}

	public IFolder getOutputFolder()
	{
		return this.project.getFolder("output");
	}

	public List<IFile> getScenarioFiles() throws CoreException
	{
		List<IFile> files = new Vector<IFile>();
		for (IResource resource : getScenarioFolder().members())
		{
			if(resource instanceof IFile)
			{
				files.add((IFile) resource);
			}
		}
		return files;
	}

	public IFolder getScenarioFolder()
	{
		return this.project.getFolder("scenarios");
	}

	public IFile getSharedDesignParameterFile()
	{
		return this.project.getFile("configuration/debug.sdp");
	}

	public IFile getVdmLinkFile()
	{
		return this.project.getFile("configuration/vdm.link");
	}
	
	public IFolder getVdmModelFolder()
	{
		return this.project.getFolder("model");
	}

	public IFile getContractFile()
	{
		return this.project.getFile("configuration/"+this.project.getName()+".csc");
	}

	public void createStructure()
	{
		File root = this.project.getLocation().toFile();

		new File(root, "model").mkdirs();
		new File(root, "output").mkdirs();
		new File(root, "scenarios").mkdirs();
		new File(root, "configuration").mkdirs();

		try
		{
			this.project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e)
		{
			// Ignore it
		}
	}

	

}
