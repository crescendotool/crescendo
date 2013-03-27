package org.destecs.vdm.utility;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexNameToken;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.SeqValue;


public class SeqValueInfo extends ValueInfo
{
	public final SeqValue value;
	public SeqValueInfo(LexNameToken name, SClassDefinition classDef,
			SeqValue value, CPUValue cpu)
	{
		super(name, classDef, value, cpu);
		this.value = value;
	}

}
