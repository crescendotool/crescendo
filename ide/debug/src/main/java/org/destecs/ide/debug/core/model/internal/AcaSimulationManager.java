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
package org.destecs.ide.debug.core.model.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

public class AcaSimulationManager
{
	final protected DestecsAcaDebugTarget target;
	ILaunch activeLaunch;
	boolean shouldStop = false;
	final List<DestecsDebugTarget> completedTargets = new Vector<DestecsDebugTarget>();
	final protected IProgressMonitor monitor;
	protected File lastUsedOutputFolder=null;

	public AcaSimulationManager(DestecsAcaDebugTarget target, IProgressMonitor monitor)
	{
		this.target = target;
		this.monitor = monitor;
	}

	public void run()
	{
		List<ILaunchConfiguration> configurations = new Vector<ILaunchConfiguration>();
		configurations.addAll(target.getAcaConfigs());
		int totalCount = configurations.size();
		Integer currentIndex = 0;
		boolean filterOutput = false;
		
		for (final ILaunchConfiguration configOriginal : configurations)
		{
			monitor.subTask("Running ("+(++currentIndex)+"/"+totalCount+") "+configOriginal.getName());
			filterOutput = totalCount > 500;
			if(!launchSingleLaunchConfig(filterOutput, currentIndex, configOriginal))
			{
				break;
			}
		}

		try
		{
			target.terminate();
		} catch (DebugException e)
		{
			DestecsDebugPlugin.logError("Failed to terminate destecs target", e);
		}
		monitor.done();
		refreshProject();
	}

	protected boolean launchSingleLaunchConfig(boolean filterOutput, Integer currentIndex,
			final ILaunchConfiguration configOriginal)
	{
		if (shouldStop)
		{
			return false;
		}
		try
		{
			if (target.getLaunch().isTerminated())
			{
				return false;
			}
			
			ILaunchConfigurationWorkingCopy config = configOriginal.getWorkingCopy();
			config.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_NAME_POSTFIX, currentIndex.toString());
			if(filterOutput)
			{//protect eclipse 
				config.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_FILTER_OUTPUT, true);
			}
			
			//group runs
			String outputPreFix = config.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, "");
			//modify
			// total count
			//
			config.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, outputPreFix);
			
			
			// monitor.worked(step);
			System.out.println("Running ACA with: " + config.getName());
			
			
			ILaunch acaRunLaunch = launch(config, ILaunchManager.DEBUG_MODE);
			setActiveLaunch(acaRunLaunch);
			IDebugTarget target = acaRunLaunch.getDebugTarget();
			completedTargets.add((DestecsDebugTarget) target);
			
			if(target==null)
			{
				lastUsedOutputFolder=null;
			}else
			{
				lastUsedOutputFolder = ((DestecsDebugTarget) target).getOutputFolder();
			}
			

			while (!acaRunLaunch.isTerminated())
			{
				internalSleep(100);
			}

			if (acaRunLaunch != null && !acaRunLaunch.isTerminated())
			{
				acaRunLaunch.terminate();
			}

			setActiveLaunch(null);

			@SuppressWarnings("unchecked")
			Map<Object, Object> attributes = config.getAttributes();
			String data = "** launch summery for ACA: " + config.getName();
			for (Map.Entry<Object, Object> entry : attributes.entrySet())
			{
				data += entry.getKey() + " = " + entry.getValue() + "\n";
			}

			data += "\n\n----------------------- MEMENTO -------------------------------\n\n";
			data += config.getMemento();

			if (lastUsedOutputFolder != null)
			{
				try
				{
					FileWriter outFile = new FileWriter(new File(lastUsedOutputFolder, "launch"));
					PrintWriter out = new PrintWriter(outFile);
					out.println(data);
					out.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			internalSleep(1000);// just let the tools calm down.
		} catch (Exception e)
		{
			DestecsDebugPlugin.logError("Error in AcaSimlation manager", e);
		}
		return true;
	}

	protected void refreshProject()
	{
		try
		{
			target.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e)
		{
			// Ignore it
		}
	}

	private synchronized void setActiveLaunch(ILaunch launch)
	{
		this.activeLaunch = launch;
	}

	private synchronized ILaunch getActiveLaunch()
	{
		return this.activeLaunch;
	}

	public void internalSleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch (InterruptedException e)
		{
		}
	}

	public abstract class UILaunchJob extends UIJob
	{
		public boolean isFinished = false;

		public UILaunchJob(Display jobDisplay, String name)
		{
			super(jobDisplay, name);
		}

	}

	private ILaunch launch(ILaunchConfiguration config, String mode)
	{
		if (config != null)
		{
			try
			{
				ILaunch launch = DebugUITools.buildAndLaunch(config, mode, new NullProgressMonitor());
				return launch;
			} catch (CoreException e)
			{
				DestecsDebugPlugin.logError("Failed to launch co simulation as part of ACA.", e);
			}
		}
		return null;
	}

	public void stopSimulation()
	{
		try
		{
			ILaunch l = getActiveLaunch();
			if(l != null)
			{
				l.terminate();
			}
			this.shouldStop=true;
		} catch (Exception e)
		{
			DestecsDebugPlugin.logError("Failed to stop ACA Manager.", e);
		}
	}

	public List<DestecsDebugTarget> getCompletedTargets()
	{
		return this.completedTargets;
	}
}
