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

import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.SimulationEngine;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.simeng.listener.ListenerToLog;
import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;

public class CoSimulationThread extends Thread
{
	final SimulationEngine engine;
	final ListenerToLog log;
	final List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters;
	final double totalSimulationTime;

	final DestecsDebugTarget target;

	final List<Throwable> exceptions = new Vector<Throwable>();
	final List<InfoTableView> views;

	public CoSimulationThread(
			SimulationEngine engine,
			ListenerToLog log,
			List<SetDesignParametersdesignParametersStructParam> shareadDesignParameters,
			double totalSimulationTime, DestecsDebugTarget target,
			List<InfoTableView> views)
	{
		this.engine = engine;
		this.log = log;
		this.shareadDesignParameters = shareadDesignParameters;
		this.totalSimulationTime = totalSimulationTime;
		this.target = target;
		this.views = views;
		setDaemon(true);
		setName("CoSimulationThread Engine");
	}

	@Override
	public void run()
	{
		try
		{
			engine.simulate(shareadDesignParameters, totalSimulationTime);
		} catch (Throwable e)
		{
			exceptions.add(e);
			DestecsDebugPlugin.log(e);
			if (log != null)
			{
				log.flush();
			}
		}

		for (InfoTableView view : views)
		{
			view.refreshPackTable();
		}

		if (log != null)
		{
			log.close();
		}

		try
		{
			target.terminate();
		} catch (DebugException e)
		{
			DestecsDebugPlugin.logError("Failed to terminate destecs target", e);
		}
		refreshProject();

//		engine.shutdownSimulators();
	}

	private void refreshProject()
	{
		try
		{
			target.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e)
		{
			// Ignore it
		}
	}

	public void stopSimulation()
	{
		engine.forceSimulationStop();
		if (log != null)
		{
			log.flush();
		}
	}

	public List<Throwable> getExceptions()
	{
		return this.exceptions;
	}
}
