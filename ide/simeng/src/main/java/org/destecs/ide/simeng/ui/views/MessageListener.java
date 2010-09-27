package org.destecs.ide.simeng.ui.views;

import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.IMessageListener;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;

public class MessageListener implements IMessageListener
{
	private InfoTableView view;

	public MessageListener(InfoTableView view)
	{
		this.view = view;
	}

	
	public void from(Simulator simulator, String messageName)
	{
		List<String> l = new Vector<String>();
		l.add(simulator.toString());
		l.add(messageName);
		view.setDataList(l);
	}

}
