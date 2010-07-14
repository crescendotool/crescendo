package org.destecs.tools.jprotocolgenerator.ast;

public class OverrideAnnotation implements IAnnotation
{

	@Override
	public String toSource()
	{
		return "@Override";
	}

}
