package org.destecs.core.simulationengine;

import java.util.List;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.protocol.structs.StepinputsStructParam;

public interface ISimulationListener
{
	void stepInfo(Simulator simulator, float timestamp, float desiredTime,
			List<StepinputsStructParam> variables);
}
