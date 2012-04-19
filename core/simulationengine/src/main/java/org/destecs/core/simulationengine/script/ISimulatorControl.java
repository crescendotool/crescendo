package org.destecs.core.simulationengine.script;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;

public interface ISimulatorControl
{

	void setVariable(Simulator simulator, String name, boolean value);

	void setVariable(Simulator simulator, String name, double value);
	
	Object getVariableValue(Simulator simulator, String name);

	double getSystemTime();

	void showMessage(String type, String message);

	void quit();

	void scriptError(String string);

}
