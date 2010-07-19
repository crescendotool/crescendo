package org.destecs.tools.jprotocolgenerator.ast;

public class RpcMethodType implements ITypeNode
{

	@Override
	public String getName()
	{
		return  "org.destecs.core.xmlrpc.extensions.RpcMethod";
	}

	@Override
	public String toSource()
	{
		return "RpcMethod";
	}

}
