package org.destecs.core.simulationengine.model;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ModelConfig
{
	public final Map<String, String> arguments = new Hashtable<String, String>();

	public abstract boolean isValid();
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("Model(");
		for (Entry<String, String> entry : arguments.entrySet())
		{
			sb.append(entry.getKey()+"="+entry.getValue());
		}
		sb.append(")");
		return sb.toString();
	}
}
