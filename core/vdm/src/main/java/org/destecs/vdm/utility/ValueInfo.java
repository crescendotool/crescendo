package org.destecs.vdm.utility;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexNameToken;
import org.overture.interpreter.values.*;



public class ValueInfo
{
	public final Value value;
	public final CPUValue cpu;
	public final SClassDefinition classDef;
	public final LexNameToken name;

	protected ValueInfo(LexNameToken name, SClassDefinition classDef, Value value,
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
