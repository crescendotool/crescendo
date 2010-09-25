package org.destecs.ide.simeng.ui;

import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.ISimulationListener;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;

public class SimulationListener implements ISimulationListener
{
	private InfoTableView view;

	public SimulationListener(InfoTableView view)
	{
		this.view = view;
	}

	
	
	public void stepInfo(Simulator simulator, StepStruct result)
	{
		List<String> l = new Vector<String>();
		l.add(simulator.toString());
		
		StringBuilder sb = new StringBuilder();
		sb.append(simulator + ": ");
		for (StepStructoutputsStruct o : result.outputs)
		{
			sb.append(o.name + "=" + o.value);
		}
		
		l.add(sb.toString());
		view.setDataList(l);
	}

}
