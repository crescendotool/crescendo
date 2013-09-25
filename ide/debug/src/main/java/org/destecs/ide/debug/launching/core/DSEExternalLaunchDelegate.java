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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.core.model.internal.AcaSimulationManager;
import org.destecs.ide.debug.core.model.internal.DestecsAcaDebugTarget;
import org.destecs.ide.debug.core.model.internal.ExternalAcaSimulationManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class DSEExternalLaunchDelegate extends DSELaunchDelegate implements
		ILaunchConfigurationDelegate
{

	public void launch(ILaunchConfiguration configuration, final String mode,
			final ILaunch launch, IProgressMonitor monitor)
			throws CoreException
	{
		monitor.beginTask("External ACA execution", IProgressMonitor.UNKNOWN);
		String baseLaunchName = launch.getLaunchConfiguration().getAttribute(IDebugConstants.DESTECS_ACA_BASE_CONFIG, "");
		System.out.println("External ACA launch with base: " + baseLaunchName);
		ILaunchConfiguration baseConfig = null;

		for (ILaunchConfiguration tmp : DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations())
		{
			if (tmp.getName().equals(baseLaunchName))
			{
				baseConfig = tmp;
			}
		}

		IProject project = getProject(baseConfig);

		String outputPreFix = DestecsLaunchDelegateUtil.getOutputPreFix(configuration);

		ILaunchConfigurationWorkingCopy baseConfigWorkingCopy = baseConfig.getWorkingCopy();
		baseConfigWorkingCopy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_LEAVE_DIRTY_FOR_INSPECTION, false);
		baseConfigWorkingCopy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_MODE, IDebugConstants.DESTECS_LAUNCH_MODE_ACA);

		if (configuration.getAttribute(IDebugConstants.DESTECS_ACA_DATA_INTENSE, false))
		{
			baseConfigWorkingCopy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_FILTER_OUTPUT, true);
		}

		Set<ILaunchConfiguration> configurations = new HashSet<ILaunchConfiguration>();
		configurations.add(baseConfigWorkingCopy);

		IDestecsProject dProject = (IDestecsProject) project.getAdapter(IDestecsProject.class);
		File base = dProject.getOutputFolder().getLocation().toFile();

		File acaOutput = new File(base, outputPreFix);
		acaOutput.mkdirs();

		if (configuration.getAttribute(IDebugConstants.DESTECS_EXTERNAL_ACA_FILTER_ANNIMATIONS, false))
		{

			try
			{
				String ctFilePathRelative = baseConfig.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH, "");
				File ctFile = DestecsLaunchDelegateUtil.getFileFromPath(project, ctFilePathRelative);

				IFile ctIFile = DestecsLaunchDelegateUtil.filterPlots(ctFile, new File(acaOutput, ctFile.getName()));
				baseConfigWorkingCopy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH, ctIFile.getProjectRelativePath().toString());

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		DestecsAcaDebugTarget acaTarget = new DestecsAcaDebugTarget(launch, project, acaOutput, configurations);
		launch.addDebugTarget(acaTarget);

		String host = configuration.getAttribute(IDebugConstants.DESTECS_EXTERNAL_ACA_HOST, "localhost");
		AcaSimulationManager manager = new ExternalAcaSimulationManager(acaTarget, monitor, host);
		acaTarget.setAcaSimulationManager(manager);

		createListeners(launch);

		manager.run();
		monitor.done();
	}

}
