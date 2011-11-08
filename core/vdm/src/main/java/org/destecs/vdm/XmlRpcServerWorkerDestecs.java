/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
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