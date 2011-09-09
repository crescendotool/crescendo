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
