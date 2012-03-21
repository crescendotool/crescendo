package org.destecs.core.contract;

import java.util.List;

import org.destecs.core.contract.Variable.DataType;
import org.destecs.core.contract.Variable.VariableType;

public class Event implements IVariable
{

	private String name = null;
	private int lineNumber = -1;
	
	
	public Event(String name, int lineNumber)
	{
		this.name = name;
		this.lineNumber = lineNumber;
	}
	
	public String getName()
	{
		return name;
	}

	public VariableType getType()
	{
		return VariableType.Event;
	}

	public Object getValue()
	{
		return null;
	}

	public DataType getDataType()
	{
		return null;
	}

	public SharedVariableType getSharedVariableType()
	{
		return null;
	}

	public List<Integer> getDimensions()
	{
		return null;
	}

	public int getLine()
	{
		return lineNumber;
	}

}
