package org.destecs.ide.simeng.internal.core;

import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.IEngineListener;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.ide.simeng.ui.views.InfoTableView;

public class EngineListener implements IEngineListener
{
	private InfoTableView view;

	public EngineListener(InfoTableView view)
	{
		this.view = view;
		this.view.resetBuffer();
	}

	public void info(Simulator simulator, String message)
	{
		List<String> l = new Vector<String>();
		l.add(simulator.toString());
		l.add(message.replace('\n', ' '));
		view.setDataList(l);
	}
}
