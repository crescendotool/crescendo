package org.destecs.vdmj.log;

public class SimulationMessage
{
	final String name;
	final long timestamp;
	final String value;

	public SimulationMessage(String name, long timestamp, String value)
	{
		this.name = name;
		this.timestamp = timestamp;
		this.value = value;
	}

	public String getMessage()
	{
		return this.timestamp + "\t,\t" + name + "\t,\t" + this.value;
	}

}
