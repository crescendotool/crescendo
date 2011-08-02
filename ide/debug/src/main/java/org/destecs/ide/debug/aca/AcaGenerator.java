package org.destecs.ide.debug.aca;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;

public class AcaGenerator
{

	private final ILaunchConfiguration configuration;
	private final ILaunchConfiguration baseConfig;
	private final Set<IAcaGeneratorPlugin> generators = new HashSet<IAcaGeneratorPlugin>();
	private final IProgressMonitor monitor;
	private final int maxProgress;

	public AcaGenerator(ILaunchConfiguration configuration,
			ILaunchConfiguration baseConfig, IProgressMonitor monitor, int maxProgress)
	{
		this.configuration = configuration;
		this.baseConfig = baseConfig;
		this.monitor = monitor;
		this.maxProgress = maxProgress;
	}
	
	public void addGenerator(IAcaGeneratorPlugin generator)
	{
		this.generators.add(generator);
	}

	public Set<ILaunchConfiguration> generate()
	{
		monitor.subTask("Generating ACA permutations");
		final Set<ILaunchConfiguration> congifurations = new HashSet<ILaunchConfiguration>();
		
		int step = maxProgress/generators.size();
		for (IAcaGeneratorPlugin g : generators)
		{
			monitor.worked(step);
			congifurations.addAll(g.generate(configuration, baseConfig, congifurations));
		}
		monitor.worked(maxProgress);
		return congifurations;
	}

}
