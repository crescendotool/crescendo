package org.destecs.ide.debug.aca.plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.aca.IAcaGeneratorPlugin;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class ArchitectureAcaPlugin implements IAcaGeneratorPlugin
{

	public Collection<? extends ILaunchConfiguration> generate(
			ILaunchConfiguration configuration,
			final ILaunchConfiguration baseConfig,
			Set<ILaunchConfiguration> congifurations, IProject project,final String outputPreFix)
	{
		final Set<ILaunchConfiguration> results = new HashSet<ILaunchConfiguration>();

		String folderName;
		try
		{
			folderName = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, "");
			
			if(folderName.isEmpty())
			{
				return results;
			}
			IResource folder = project.findMember(new Path(folderName));
			

			if (folder.exists() && folder instanceof IFolder)
			{
				folder.accept(new IResourceVisitor()
				{
					
					public boolean visit(IResource resource) throws CoreException
					{
						if (resource.getFileExtension()!=null && resource.getFileExtension().equals("arch"))
						{
							ILaunchConfigurationWorkingCopy copy = baseConfig.getWorkingCopy();
							copy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, resource.getProjectRelativePath().toString());
							copy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, outputPreFix);
							results.add(copy);
							return false;
						}
						return true;
					}
				});
			}

		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}
	
	
	

}
