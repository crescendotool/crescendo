package org.destecs.ide.core.internal.builder;

import org.destecs.ide.core.DestecsCorePlugin;
import org.destecs.ide.core.resources.IDestecsProject;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

public class SafeBuilder extends Thread
{
	final IDestecsProject currentProject;

	final IProgressMonitor monitor;

	public SafeBuilder(final IDestecsProject currentProject,
			final IProgressMonitor monitor)
	{
		this.currentProject = currentProject;

		this.monitor = monitor;
		this.setName("DESTECS Safe Builder");
		this.setDaemon(true);
	}

	@Override
	public void run()
	{
		try
		{
			ISafeRunnable runnable = new ISafeRunnable()
			{

				public void handleException(Throwable e)
				{
					DestecsCorePlugin.log("SafeBuilder", e);
				}

				public void run() throws Exception
				{
					clearProblemMarkers((IProject) currentProject.getAdapter(IProject.class));
					DestecsBuilder.build(currentProject);
				}
			};
			SafeRunner.run(runnable);
		} catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			DestecsCorePlugin.log(ex);
		}
	}
	
	/***
	 * This method removed all problem markers and its sub-types from the project. It is called before an instance of
	 * the AbstractBuilder is created
	 * 
	 * @param project
	 *            The project which should be build.
	 */
	public static void clearProblemMarkers(IProject project)
	{
		try
		{
			project.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);

		} catch (CoreException e)
		{
			DestecsCorePlugin.log("DestecsCorePluginBuilder:clearProblemMarkers", e);
		}

	}
}
