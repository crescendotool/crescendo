package org.destecs.tools.jprotocolgenerator.ast;

public class RpcMethodType implements ITypeNode
{

	@Override
	public String getName()
	{
		return "org.destetcs.core.xmlrpc.extensions.RpcMethod";
	}

	@Override
	public String toSource()
	{
		return "RpcMethod";
	}

}
