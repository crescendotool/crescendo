package org.destecs.vdm;

import org.apache.xmlrpc.common.XmlRpcController;
import org.apache.xmlrpc.common.XmlRpcWorker;
import org.apache.xmlrpc.common.XmlRpcWorkerFactory;

public class XmlRpcWorkerFactoryDestecs extends XmlRpcWorkerFactory
{

	public XmlRpcWorkerFactoryDestecs(XmlRpcController pController)
	{
		super(pController);
	}

	@Override
	protected XmlRpcWorker newWorker()
	{
		return new XmlRpcServerWorkerDestecs(this);
	}

}
