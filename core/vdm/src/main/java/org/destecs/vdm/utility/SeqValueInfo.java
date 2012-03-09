package org.destecs.vdm.utility;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.values.CPUValue;
import org.overturetool.vdmj.values.SeqValue;

public class SeqValueInfo extends ValueInfo
{
	public final SeqValue value;
	public SeqValueInfo(LexNameToken name, ClassDefinition classDef,
			SeqValue value, CPUValue cpu)
	{
		super(name, classDef, value, cpu);
		this.value = value;
	}

}
