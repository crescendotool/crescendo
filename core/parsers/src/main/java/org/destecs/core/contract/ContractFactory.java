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

import java.util.ArrayList;
import java.util.List;

import org.destecs.core.contract.Variable.VariableType;

public class ContractFactory {

//	private String name = null;
	private List<IVariable> variables = null;
	//private List<String> events = null;
	
	public ContractFactory() {
		this.variables = new ArrayList<IVariable>();
	//	this.events = new ArrayList<String>();
	}

//	public void setName(String name) {
//		this.name = name;
//	}	
	
	public void addVariable(IVariable var ){
		this.variables.add(var);
	}
	
//	public void addEvent(String event){
//		this.events.add(event);
//	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for (IVariable var : variables)
		{
			
			if(var.getType() == VariableType.SharedDesignParameter){
				sb.append(var.getType().syntaxName+" "+ var.getDataType()+ " "+ var.getName() + ";\n");
			}
			else if(var.getType() == VariableType.Event)
			{
				sb.append(var.getType().syntaxName+" "+ var.getName() + ";\n");
			}
			else{
				sb.append(var.getType().syntaxName+" "+ var.getDataType()+ " "+ var.getName()+";\n");	
			}
			
		}
		
		return sb.toString();
	}
	
	public Contract getContract(){
		Contract contract = new Contract(this.variables);
		return contract;	
	}


}
