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
package org.destecs.core.simulationengine;

import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.core.simulationengine.xmlrpc.client.CustomSAXParserTransportFactory;
import org.destecs.core.xmlrpc.extensions.AnnotationClientFactory;
import org.destecs.protocol.ICoSimProtocol;
import org.destecs.protocol.ProxyICoSimProtocol;

public class Clp20SimUtility {
	static public ProxyICoSimProtocol connect(URL url)
			throws SimulationException {
		ProxyICoSimProtocol protocolProxy = null;
		try {
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(url);
			// config.setReplyTimeout(5000);// 5 sec time out

			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);

			if (SimulationEngine.eclipseEnvironment) {
				client.setTransportFactory(new CustomSAXParserTransportFactory(
						client));
			}

			// clients.add(client);
			// add factory for annotations for generated protocol
			AnnotationClientFactory factory = new AnnotationClientFactory(
					client);

			ICoSimProtocol protocol = (ICoSimProtocol) factory
					.newInstance(ICoSimProtocol.class);

			protocolProxy = new ProxyICoSimProtocol(protocol);
		} catch (Exception e) {

		}
		return protocolProxy;
	}

}
