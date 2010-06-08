package org.destecs.tools.jprotocolgenerator.ast;

import java.util.HashMap;
import java.util.Map;


public class MapType implements ITypeNode
{
	
	public ITypeNode keyType;
	public ITypeNode valueType;
	public Map<String,ITypeNode> possibleEntries = new HashMap<String, ITypeNode>();

	public MapType(Type type, ITypeNode rangeType)
	{
		this.keyType = type;
		this.valueType = rangeType;
	}

	public MapType()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName()
	{
		return "Map<"+ keyType.toSource()+","+valueType.toSource()+">";
	}

	@Override
	public String toSource()
	{
		return "Map<"+ keyType.toSource()+","+valueType.toSource()+">";
	}
	
	@Override
	public String toString()
	{
		return toSource();
	}

}
