package org.destecs.vdm;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.common.XmlRpcController;
import org.apache.xmlrpc.common.XmlRpcWorker;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;

public class XmlRpcServerWorkerDestecs implements XmlRpcWorker
{
	private final XmlRpcWorkerFactoryDestecs factory;

	/** Creates a new instance.
	 * @param pFactory The factory creating the worker.
	 */
	public XmlRpcServerWorkerDestecs(XmlRpcWorkerFactoryDestecs pFactory) {
		factory = pFactory;
	}

	public XmlRpcController getController() { return factory.getController(); }

	public Object execute(XmlRpcRequest pRequest) throws XmlRpcException {
		XmlRpcServer server = (XmlRpcServer) getController();
		XmlRpcHandlerMapping mapping = server.getHandlerMapping();
		XmlRpcHandler handler = mapping.getHandler(pRequest.getMethodName());
		return handler.execute(pRequest);
	}
}