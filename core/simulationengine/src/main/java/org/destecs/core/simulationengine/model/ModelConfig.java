package org.destecs.core.simulationengine.model;

import java.util.Hashtable;
import java.util.Map;

public abstract class ModelConfig
{
	public final Map<String, String> arguments = new Hashtable<String, String>();

	public abstract boolean isValid();
}
