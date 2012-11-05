package org.destecs.vdm.utility;


import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.Value;

public class ValueInfo
{
	public final Value value;
	public final CPUValue cpu;
	public final ClassDefinition classDef;
	public final LexNameToken name;

	protected ValueInfo(LexNameToken name, ClassDefinition classDef, Value value,
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
