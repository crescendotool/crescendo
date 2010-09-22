package org.destecs.core.simulationengine;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;

public interface IEngineListener
{
	void info(Simulator simulator,String message);
}
