package org.destecs.tools.jprotocolgenerator.ast;

import java.util.UUID;


public class Field implements ITypeNode
{
	private String name;
	
	public ITypeNode type;
	
	public void setName(String name)
	{
		if(name== null || name.length() == 0)
		{
			this.name ="A"+UUID.randomUUID().toString().replace("-", "");
		}
		else
		{
			this.name = name;
		}
	}

	@Override
	public String toSource()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("public "+type.toSource() + " "+name);
		
		if(type instanceof ListType || type instanceof MapType)
		{
			sb.append(" = new "+type.toSource().replace("List", "Vector").replace("Map", "Hashtable")+"()");
		}
		
		sb.append(";");
		
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
	return toSource();
	}

	public String getName()
	{
		return name;
	}

}
