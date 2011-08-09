package org.destecs.ide.debug.aca.plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.aca.IAcaGeneratorPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class IncludeBaseConfigAcaPlugin implements IAcaGeneratorPlugin
{

	public Collection<? extends ILaunchConfiguration> generate(
			ILaunchConfiguration configuration,
			ILaunchConfiguration baseConfig,
			Set<ILaunchConfiguration> congifurations, IProject project, String outputPreFix)
	{
		final Set<ILaunchConfiguration> configs = new HashSet<ILaunchConfiguration>();
		ILaunchConfigurationWorkingCopy copy;
		try
		{
			copy = baseConfig.getWorkingCopy();
			copy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, outputPreFix);
			configs.add(copy);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return configs;
	}

}
