package org.destecs.ide.debug.aca.plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.aca.IAcaGeneratorPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class ScenarioAcaPlugin implements IAcaGeneratorPlugin {

	public Collection<? extends ILaunchConfiguration> generate(
			ILaunchConfiguration configuration,
			ILaunchConfiguration baseConfig,
			Set<ILaunchConfiguration> configurations, IProject project,
			String outputPreFix) {
		
		
		final Set<ILaunchConfiguration> results = new HashSet<ILaunchConfiguration>();

		String unparsedString;
		try
		{
			unparsedString = configuration.getAttribute(IDebugConstants.DESTECS_ACA_SCENARIOS, "");
			
			if(unparsedString.trim().length() == 0)
			{
				return new HashSet<ILaunchConfiguration>(configurations);
			}
			
			String[] architectures = unparsedString.split(";");
			List<String> architecturesList = Arrays.asList(architectures);
			
			
			IResource folder = project.findMember(new Path("scenarios"));
			

			if (folder != null && folder.exists() && folder instanceof IFolder)
			{
				
				for (IResource iResource : ((IFolder)folder).members()) {
					if(iResource instanceof IFile)
					{
						if(architecturesList.contains(((IFile)iResource).getName()))
						{
							for (ILaunchConfiguration iLaunchConf : configurations)
							{
								ILaunchConfigurationWorkingCopy copy = iLaunchConf.getWorkingCopy().copy(iLaunchConf.getName());
								copy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SCENARIO_PATH, iResource.getProjectRelativePath().toString());
								copy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, outputPreFix);
								results.add(copy);
							}
						}
					}
				}
				
			}

		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Error scenario ACA plugin", e);
		}
		return results;
	}
	

}
