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
package org.destecs.ide.simeng.actions;

import java.util.List;
import java.util.Vector;

import org.destecs.ide.simeng.ISimengConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public abstract class BaseSimulationControlAction extends Action implements IDebugEventSetListener
{
	public final String DESTECS_LAUNCH_ID = "org.destecs.ide.debug.launchConfigurationType";
	public BaseSimulationControlAction()
	{
		DebugPlugin.getDefault().addDebugEventListener(this);
		setEnabled(false);
	}

	public BaseSimulationControlAction(ISimulationControlProxy proxy)
	{
		this.proxy.add(proxy);
	}

	public abstract ImageDescriptor getImageDescriptor();

	/**
	 * Returns the image descriptor with the given relative path.
	 */
	protected ImageDescriptor getImageDescriptor(String relativePath)
	{
		return ImageDescriptor.createFromURL(FileLocator.find(Platform.getBundle(ISimengConstants.PLUGIN_ID), new Path("icons/"
				+ relativePath), null));
	}

	protected final List<ISimulationControlProxy> proxy = new Vector<ISimulationControlProxy>();

	public synchronized void addSimulationControlProxy(
			ISimulationControlProxy proxy)
	{
		this.proxy.add(proxy);
	}

	public synchronized void removeSimulationControlProxy(
			ISimulationControlProxy proxy)
	{
		this.proxy.remove(proxy);
	}

	protected IDebugTarget getRunningTarget() throws CoreException
	{
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType(DESTECS_LAUNCH_ID);
		IDebugTarget target = null;
		for (ILaunch iLaunch : launchManager.getLaunches())
		{
			
				if(iLaunch.getLaunchConfiguration().getType() == configType)
				{
					target = iLaunch.getDebugTarget();
					
				}
					
		}
		return target;
	}
	
	
	public void handleDebugEvents(DebugEvent[] events)
	{
		for (DebugEvent debugEvent : events)
		{
			if(debugEvent.getSource() instanceof IDebugTarget)
			{
				ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
				ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType(DESTECS_LAUNCH_ID);
				IDebugTarget target = (IDebugTarget) debugEvent.getSource();
				try
				{
					if(target.getLaunch().getLaunchConfiguration().getType() == configType)
					{
						switch(debugEvent.getKind())
						{
							case(DebugEvent.CREATE):
								doResume();
								break;
							case(DebugEvent.SUSPEND):
								doSuspend();								
								break;
							case(DebugEvent.RESUME):
								doResume();								
								break;
							case(DebugEvent.TERMINATE):
								doTerminate();
								break;
							default:
								break;
						}
						
						
					}
				} catch (CoreException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	
	abstract protected void doTerminate();

	abstract protected void doResume();
	
	abstract protected void doSuspend();
	
}
