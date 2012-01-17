package org.destecs.core.simulationengine.script.values;

public class Value
{

	public static Value valueOf(Object val)
	{
		if (val instanceof Boolean)
		{
			return new BooleanValue((Boolean) val);
		} else if (val instanceof Double)
		{
			return new DoubleValue((Double) val);
		}
		return null;
	}

}
