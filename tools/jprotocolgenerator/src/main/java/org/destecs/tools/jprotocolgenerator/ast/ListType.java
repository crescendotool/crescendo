package org.destecs.tools.jprotocolgenerator.ast;

public class ListType implements ITypeNode
{

	public ITypeNode type;
	public ListType(ITypeNode rangeType)
	{
		this.type = rangeType;
	}

	@Override
	public String getName()
	{
		return "List<"+type.getName()+">";
	}

	@Override
	public String toSource()
	{
		return "List<"+type.toSource()+">";
	}

	@Override
	public String toString()
	{
		return toSource();
	}
}
