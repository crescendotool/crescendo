package org.destecs.ide.debug.launching.ui.aca;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public abstract class AbstractAcaTab extends AbstractLaunchConfigurationTab implements
		ILaunchConfigurationTab {

	
	public IProject getActiveProject()
	{
		IProject project = null;
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof DseMainTab)
			{
				DseMainTab dseLaunchTab = (DseMainTab) tab;
				project = dseLaunchTab.getProject();
			}
		}
		return project;
	}
	
	public ILaunchConfiguration getBaseConfiguration()
	{
		ILaunchConfiguration configuration = null;
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof DseMainTab)
			{
				DseMainTab dseLaunchTab = (DseMainTab) tab;
				configuration = dseLaunchTab.getBaseLaunchConfig();
			}
		}
		return configuration;
	}
	
	public static File getFileFromPath(IProject project, String path)
	{

		IResource r = project.findMember(new Path(path));

		if (r != null && !r.equals(project))
		{
			return r.getLocation().toFile();
		}
		return null;
	}

}
