package org.destecs.core.simulationengine;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;

public interface IMessageListener
{
	void from(Simulator simulator,String messageName);
}
