package org.destecs.ide.debug.aca;

import java.util.Collection;
import java.util.Set;

import org.eclipse.debug.core.ILaunchConfiguration;

public interface IAcaGeneratorPlugin
{

	Collection<? extends ILaunchConfiguration> generate(
			ILaunchConfiguration configuration, ILaunchConfiguration baseConfig, Set<ILaunchConfiguration> congifurations);

}
