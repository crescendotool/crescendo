package org.destecs.tools.jprotocolgenerator.ast;

public class Type implements ITypeNode
{
	@SuppressWarnings("unchecked")
	public Class type;
	private final static String unknown = "_ERROR_UNKNOWN_TYPE_";

	@SuppressWarnings("unchecked")
	public Type(Class class1)
	{
		this.type = class1;
	}
	
	public Type()
	{
		this.type = null;
	}
	
	public Type(ITypeNode node)
	{
		if(node instanceof Type)
		{
			this.type = ((Type)node).type;
		}
	}

	@Override
	public String toSource()
	{
		if(type !=null)
		{
			return type.getSimpleName();
		}else
		{
			return unknown;
		}
		
	}

	@Override
	public String getName()
	{
		if(type !=null)
		{
			return type.getName();
		}else
		{
			return unknown;
		}
	}
	
	@Override
	public String toString()
	{
		return toSource();
	}

}
