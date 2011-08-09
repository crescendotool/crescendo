package org.destecs.ide.debug.aca;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;

public class AcaGenerator
{

	private final ILaunchConfiguration configuration;
	private final ILaunchConfiguration baseConfig;
	private final Set<IAcaGeneratorPlugin> generators = new HashSet<IAcaGeneratorPlugin>();
	private final IProgressMonitor monitor;
	private final int maxProgress;
	private final IProject project;
	private final String outputPreFix;

	public AcaGenerator(ILaunchConfiguration configuration,
			ILaunchConfiguration baseConfig, IProgressMonitor monitor, int maxProgress, IProject project, String outputPreFix)
	{
		this.configuration = configuration;
		this.baseConfig = baseConfig;
		this.monitor = monitor;
		this.maxProgress = maxProgress;
		this.project = project;
		this.outputPreFix = outputPreFix;
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
			congifurations.addAll(g.generate(configuration, baseConfig, congifurations,project,outputPreFix));
		}
		monitor.worked(maxProgress);
		return congifurations;
	}

}
