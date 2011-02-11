package org.destecs.core.contract;

import java.util.ArrayList;
import java.util.List;

import org.destecs.core.contract.Variable.VariableType;

public class ContractFactory {

//	private String name = null;
	private List<Variable> variables = null;
	private List<String> events = null;
	
	public ContractFactory() {
		this.variables = new ArrayList<Variable>();
		this.events = new ArrayList<String>();
	}

//	public void setName(String name) {
//		this.name = name;
//	}	
	
	public void addVariable(Variable var ){
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
		
		for (Variable var : variables)
		{
			if(var.type == VariableType.SharedDesignParameter){
				sb.append(var.type.syntaxName+" "+ var.dataType+ " "+ var.name + ";\n");
			}
			else{
				sb.append(var.type.syntaxName+" "+ var.dataType+ " "+ var.name+ " := "+ var.value+";\n");	
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
