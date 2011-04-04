package org.destecs.ide.debug.core.model.internal;

import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.SimulationEngine;
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
			e.printStackTrace();
			exceptions.add(e);
			log.flush();
		}

		for (InfoTableView view : views)
		{
			view.refreshPackTable();
		}

		log.close();

		refreshProject();

		try
		{
			target.terminate();
		} catch (DebugException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		engine.shutdownSimulators();
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
		log.flush();
//		interrupt();
	}

	public List<Throwable> getExceptions()
	{
		return this.exceptions;
	}
}
