package org.destecs.ide.simeng.internal.core;

import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.IMessageListener;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.ide.simeng.ui.views.InfoTableView;

public class MessageListener extends BaseListener implements IMessageListener
{
	
	public MessageListener(InfoTableView view)
	{
		super(view);
		view.addColumn("Time");
	}

	
	public void from(Simulator simulator,Double time, String messageName)
	{
		List<String> l = new Vector<String>();
		l.add(simulator.toString());
		l.add(messageName);
		l.add(time.toString());
		insetData(l);
	}

}
