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
package org.destecs.ide.debug.launching.core;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.aca.AcaGenerator;
import org.destecs.ide.debug.aca.plugin.ArchitecturesAcaPlugin;
import org.destecs.ide.debug.aca.plugin.IncludeBaseConfigAcaPlugin;
import org.destecs.ide.debug.aca.plugin.ScenarioAcaPlugin;
import org.destecs.ide.debug.aca.plugin.SharedDesignParameterAcaPlugin;
import org.destecs.ide.debug.core.model.internal.AcaSimulationManager;
import org.destecs.ide.debug.core.model.internal.DestecsAcaDebugTarget;
import org.destecs.ide.simeng.actions.ISimulationControlProxy;
import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.ui.progress.UIJob;

public class DSELaunchDelegate implements ILaunchConfigurationDelegate
{

	public void launch(ILaunchConfiguration configuration, final String mode,
			final ILaunch launch, IProgressMonitor monitor)
			throws CoreException
	{
		monitor.beginTask("ACA execution", 100);
		String baseLaunchName = launch.getLaunchConfiguration().getAttribute(IDebugConstants.DESTECS_ACA_BASE_CONFIG, "");
		System.out.println("ACA launch with base: " + baseLaunchName);
		ILaunchConfiguration baseConfig = null;

		for (ILaunchConfiguration tmp : DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations())
		{
			if (tmp.getName().equals(baseLaunchName))
			{
				baseConfig = tmp;
			}
		}

		IProject project = getProject(baseConfig);

		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String outputPreFix = dateFormat.format(new Date()) + "_"
				+ configuration.getName();

		AcaGenerator generator = new AcaGenerator(configuration, baseConfig, monitor, 10, project, outputPreFix);
		generator.addGenerator(new IncludeBaseConfigAcaPlugin());
		//generator.addGenerator(new ArchitectureAcaPlugin());
		generator.addGenerator(new ArchitecturesAcaPlugin());
		generator.addGenerator(new SharedDesignParameterAcaPlugin());
		generator.addGenerator(new ScenarioAcaPlugin());

		monitor.worked(10);
		final Set<ILaunchConfiguration> configurations = generator.generate();

		IDestecsProject dProject = (IDestecsProject) project.getAdapter(IDestecsProject.class);
		File base = dProject.getOutputFolder().getLocation().toFile();

		DestecsAcaDebugTarget acaTarget = new DestecsAcaDebugTarget(launch, project, new File(base, outputPreFix), configurations);
		launch.addDebugTarget(acaTarget);

		AcaSimulationManager manager = new AcaSimulationManager(acaTarget);
		manager.start();
		acaTarget.setAcaSimulationManager(manager);

		UIJob listeners = new UIJob("Set Listeners")
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				final String engineViewId = IDebugConstants.ENGINE_VIEW_ID;
				final InfoTableView engineView = CoSimLaunchConfigurationDelegate.getInfoTableView(engineViewId);

				ISimulationControlProxy simulationControl = new ISimulationControlProxy()
				{

					public void terminate()
					{
						try
						{
							launch.terminate();
						} catch (DebugException e)
						{
							DestecsDebugPlugin.logError("Failed to terminate launch", e);
						}
					}

					public void pause()
					{
						// not supported
					}

					public void resume()
					{
						// not supported
					}
				};

				engineView.getTerminationAction().addSimulationControlProxy(simulationControl);
				engineView.getPauseAction().addSimulationControlProxy(simulationControl);
				engineView.getResumeAction().addSimulationControlProxy(simulationControl);

				return new Status(IStatus.OK, IDebugConstants.PLUGIN_ID, "Listeners OK");
			}
		};
		listeners.schedule();

		monitor.done();
	}

	private IProject getProject(ILaunchConfiguration configuration)
	{
		try
		{
			if (configuration != null)
			{
				String projectName = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_PROJECT_NAME, "");
				return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			}
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Failed to get project from config for ACA launch", e);
		}
		return null;
	}

}
