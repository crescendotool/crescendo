package org.destecs.cosim;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.destecs.protocol.ICoSimProtocol;
import org.destetcs.core.xmlrpc.extensions.AnnotationClientFactory;


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
		AnnotationClientFactory factory = new AnnotationClientFactory(client);
		
		ICoSimProtocol dt = (ICoSimProtocol) factory.newInstance(ICoSimProtocol.class);
//		ICoSimProtocol dt = (ICoSimProtocol) factory.newInstance(Thread.currentThread().getContextClassLoader(),ICoSimProtocol.class,"PREFIX");
		
		
//		System.out.println("GetVersion: "+dt.GetVersion());
//		
//	Map<String, List<Map<String, Object>>> interfaceDefinition = 	dt.QueryInterface();
//		System.out.println("QueryInterface: "+interfaceDefinition);

		System.out.println(dt.getVersion());
		
	}

}
