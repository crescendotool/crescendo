package org.destecs.tools.jprotocolgenerator.ast;

public class VoidType extends Type
{
	@Override
	public String toSource()
	{
		return "";
	}

	@Override
	public String getName()
	{
		return "void";
	}
}
