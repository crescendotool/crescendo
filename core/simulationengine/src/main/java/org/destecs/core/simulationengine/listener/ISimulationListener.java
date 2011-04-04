package org.destecs.core.simulationengine.listener;

import org.destecs.core.simulationengine.SimulationEngine;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.protocol.structs.StepStruct;

public interface ISimulationListener
{
	void stepInfo(Simulator simulator, StepStruct result);
}
