package org.destecs.cosim;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;
import org.destecs.protocol.ICoSimProtocol;

public class CoSimClient
{

	/**
	 * @param args
	 * @throws MalformedURLException
	 */
	public static void main(String[] args) throws MalformedURLException
	{
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL("http://127.0.0.1:8080/xmlrpc"));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		ClientFactory factory = new ClientFactory(client);
		
		ICoSimProtocol dt = (ICoSimProtocol) factory.newInstance(ICoSimProtocol.class);
		
		
		System.out.println("GetVersion: "+dt.GetVersion());
		
	Map<String, List<Map<String, Object>>> interfaceDefinition = 	dt.QueryInterface();
		System.out.println("QueryInterface: "+interfaceDefinition);

	}

}
