package org.destecs.tools.jprotocolgenerator.ast;

public class Parameter implements IAstNode
{
	public String name;
	public ITypeNode type;

	public Parameter(ITypeNode type2)
	{
		this.type = type2;
	}

	public Parameter(ITypeNode type2, String name2)
	{
		this.type = type2;
		this.name = name2;
	}

	@Override
	public String toSource()
	{
		return type.toSource() + " " + name;
	}
}
