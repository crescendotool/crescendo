package org.destecs.ide.debug.aca.plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.destecs.ide.debug.aca.IAcaGeneratorPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfiguration;

public class IncludeBaseConfigAcaPlugin implements IAcaGeneratorPlugin
{

	public Collection<? extends ILaunchConfiguration> generate(
			ILaunchConfiguration configuration,
			ILaunchConfiguration baseConfig,
			Set<ILaunchConfiguration> congifurations, IProject project)
	{
		final Set<ILaunchConfiguration> configs = new HashSet<ILaunchConfiguration>();
		configs.add(baseConfig);
		return configs;
	}

}
