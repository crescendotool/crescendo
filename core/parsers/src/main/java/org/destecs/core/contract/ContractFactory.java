package org.destecs.core.contract;

import java.util.ArrayList;
import java.util.List;

import org.destecs.core.contract.Variable.VariableType;

public class ContractFactory {

//	private String name = null;
	private List<IVariable> variables = null;
	private List<String> events = null;
	
	public ContractFactory() {
		this.variables = new ArrayList<IVariable>();
		this.events = new ArrayList<String>();
	}

//	public void setName(String name) {
//		this.name = name;
//	}	
	
	public void addVariable(IVariable var ){
		this.variables.add(var);
	}
	
	public void addEvent(String event){
		this.events.add(event);
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
			if(var.getType() == VariableType.SharedDesignParameter){
				sb.append(var.getType().syntaxName+" "+ var.getDataType()+ " "+ var.getName() + ";\n");
			}
			else{
				sb.append(var.getType().syntaxName+" "+ var.getDataType()+ " "+ var.getName()+ " := "+ var.getValue()+";\n");	
			}
			
		}
		
//		sb.append("end "+name+"\n");
		return sb.toString();
	}
	
	public Contract getContract(){
		Contract contract = new Contract(this.variables,this.events);
		return contract;	
	}


}
