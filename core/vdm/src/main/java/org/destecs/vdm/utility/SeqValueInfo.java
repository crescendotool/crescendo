package org.destecs.vdm.utility;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.Value;


public class SeqValueInfo extends ValueInfo
{
	public final SeqValue value;
	public final Value source;
	public SeqValueInfo(ILexNameToken name, SClassDefinition classDef,
			SeqValue value, Value sourceValue,CPUValue cpu)
	{
		super(name, classDef, value, cpu);
		this.value = value;
		this.source = sourceValue;
	}

}
