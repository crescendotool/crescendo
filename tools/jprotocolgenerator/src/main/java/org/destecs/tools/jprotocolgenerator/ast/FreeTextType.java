package org.destecs.tools.jprotocolgenerator.ast;

public class FreeTextType implements ITypeNode
{
	String name;
	
	public FreeTextType(String name)
	{
	this.name = name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toSource()
	{
		return name;
	}
	
	@Override
	public String toString()
	{
	return name;
	}

}
