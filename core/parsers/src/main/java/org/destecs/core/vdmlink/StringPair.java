package org.destecs.core.vdmLink;

public class StringPair
{
	public final String instanceName;
	public final String variableName;

	public StringPair(String instance, String variable)
	{
		this.instanceName = instance;
		this.variableName = variable;
	}

	@Override
	public String toString()
	{
		return instanceName + "." + variableName;
	}
}