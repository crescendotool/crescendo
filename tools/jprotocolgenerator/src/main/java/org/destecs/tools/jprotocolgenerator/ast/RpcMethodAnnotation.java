package org.destecs.tools.jprotocolgenerator.ast;



public class RpcMethodAnnotation implements IAnnotation
{
	public String methodName =null;

	public RpcMethodAnnotation(String name)
	{
		this.methodName = name;
	}

	@Override
	public String toSource()
	{
		if(methodName!=null)
		{
			return "@RpcMethod(methodName = \""+methodName+"\")";
		}
		return "";
	}

}
