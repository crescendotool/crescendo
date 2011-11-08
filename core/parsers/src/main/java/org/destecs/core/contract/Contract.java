/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
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