package org.destecs.core.simulationengine.listener;

import org.destecs.core.simulationengine.SimulationEngine;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;

public interface IMessageListener
{
	void from(Simulator simulator,Double time,String messageName);
}
