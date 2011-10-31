package org.destecs.core.contract;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.destecs.core.contract.Variable.VariableType;

public class Contract
{
//	public final String name;
	
	private final List<IVariable> variables;
	private final List<String> events;
	
	public Contract( List<IVariable> variables, List<String> events) {
//		this.name = name;
		this.variables = variables;
		this.events = events;
	}
	
	private List<IVariable> filterVariables(VariableType... filter)
	{
		List<VariableType> filters = Arrays.asList(filter);
		List<IVariable> list =  new Vector<IVariable>();
		for (IVariable variable : variables)
		{
			if(filters.contains(variable.getType()))
			{
				list.add(variable);
			}
		}
		return list;
	}
	
	public List<IVariable> getVariables()
	{
		return filterVariables(VariableType.Controlled,VariableType.Monitored);
	}
	
	public List<IVariable> getControlledVariables()
	{
		return filterVariables(VariableType.Controlled);
	}
	public List<IVariable> getMonitoredVariables()
	{
		return filterVariables(VariableType.Monitored);
	}
	
	public List<IVariable> getSharedDesignParameters()
	{
		return filterVariables(VariableType.SharedDesignParameter);
	}
	
	public List<String> getEvents()
	{
		return events;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
//		sb.append("contract "+name+"\n");
		
		for (String event : events)
		{
			sb.append("event "+event+";\n");
		}
		
		for (IVariable var : variables)
		{
			sb.append(var.getType().syntaxName+" "+ var.getDataType()+ " "+ var.getName()+ " := "+ var.getValue()+";\n");
		}
		
//		sb.append("end "+name+"\n");
		return sb.toString();
	}
}

// contract watertank
//
// design_parameters
//
// design_parameter real maxlevel := 3.0;
// design_parameter real minlevel := 2.0;
//
// variables
//
// monitored real level := 0.0;
//
// controlled bool valve := false;
//
//
// events
//
// event high;
// event low;
//
// end watertank