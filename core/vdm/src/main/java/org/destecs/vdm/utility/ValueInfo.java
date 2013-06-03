package org.destecs.vdm.utility;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.Value;



public class ValueInfo
{
	public final Value value;
	public final CPUValue cpu;
	public final SClassDefinition classDef;
	public final ILexNameToken name;

	protected ValueInfo(ILexNameToken name, SClassDefinition classDef, Value value,
			CPUValue cpu)
	{
		this.value = value;
		this.cpu = cpu;
		this.classDef = classDef;
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return "Name: "+ name.getName()+ " CPU:"+ cpu.getName()+  " Class:"+ classDef.getName()+ " Value:"+value;
	}

}
