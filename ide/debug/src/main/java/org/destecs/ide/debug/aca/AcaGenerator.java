/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.ide.debug.aca;

import java.util.Collection;
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

	public Set<ILaunchConfiguration> generate() throws Exception
	{
		monitor.subTask("Generating ACA permutations");
		final Set<ILaunchConfiguration> configurations = new HashSet<ILaunchConfiguration>();
		configurations.add(baseConfig);
		
		int step = maxProgress/generators.size();
		for (IAcaGeneratorPlugin g : generators)
		{
			monitor.worked(step);
			Collection<? extends ILaunchConfiguration> temp = g.generate(configuration, baseConfig, configurations,project,outputPreFix);
			if(!temp.isEmpty())
			{
				configurations.clear();
				configurations.addAll(temp);
			}
			
		}
		monitor.worked(maxProgress);
		return configurations;
	}
	

	
	

}
