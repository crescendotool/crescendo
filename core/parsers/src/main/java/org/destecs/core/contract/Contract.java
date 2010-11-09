package org.destecs.core.contract;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.destecs.core.contract.Variable.VariableType;

public class Contract
{
	public final String name;
	//public final List<Variable> sharedDesignParameters = new Vector<Variable>();
	private final List<Variable> variables;
	private final List<String> events;
	
	public Contract(String name, List<Variable> variables, List<String> events) {
		this.name = name;
		this.variables = variables;
		this.events = events;
	}
	
	private List<Variable> filterVariables(VariableType... filter)
	{
		List<VariableType> filters = Arrays.asList(filter);
		List<Variable> list =  new Vector<Variable>();
		for (Variable variable : variables)
		{
			if(filters.contains(variable.type))
			{
				list.add(variable);
			}
		}
		return list;
	}
	
	public List<Variable> getVariables()
	{
		return filterVariables(VariableType.Controlled,VariableType.Monitored);
	}
	
	public List<Variable> getControlledVariables()
	{
		return filterVariables(VariableType.Controlled);
	}
	public List<Variable> getMonitoredVariables()
	{
		return filterVariables(VariableType.Monitored);
	}
	
	public List<Variable> getSharedDesignParameters()
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
		
		sb.append("contract "+name+"\n");
		
		for (String event : events)
		{
			sb.append("event "+event+";\n");
		}
		
		for (Variable var : variables)
		{
			sb.append(var.type.syntaxName+" "+ var.dataType+ " "+ var.name+ " := "+ var.value+";\n");
		}
		
		sb.append("end "+name+"\n");
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