package org.destecs.vdm;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;
import org.destecs.core.xmlrpc.extensions.AnnotedPropertyHandlerMapping;
import org.destecs.protocol.ICoSimProtocol;

/**
 * @author kela
 * @see http://ws.apache.org/xmlrpc/server.html
 */
public class CoSim
{
	private static final int port = 8080;
	private static final boolean DEBUG = false;

	/**
	 * @param args
	 * @throws ServletException
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	public static void main(String[] args) throws ServletException,
			IOException, XmlRpcException
	{
		// XmlRpcServlet servlet = new XmlRpcServlet();
		//
		// ServletWebServer webServer = new ServletWebServer(servlet, port);
		//
		// webServer.start();

		WebServer webServer = new WebServer(port);

		// webServer.getXmlRpcServer().setXMLWriterFactory(new ObservableXMLWriterFactory());
		if (!DEBUG)
		{
			webServer.getXmlRpcServer().setWorkerFactory(new XmlRpcWorkerFactoryDestecs(webServer.getXmlRpcServer()));
		}
		XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

		PropertyHandlerMapping phm = new AnnotedPropertyHandlerMapping(); // new PropertyHandlerMapping();

		phm.addHandler(ICoSimProtocol.class.getName(), CoSimImpl.class);

		xmlRpcServer.setHandlerMapping(phm);

		XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
		serverConfig.setEnabledForExtensions(true);
		serverConfig.setContentLengthOptional(false);

		webServer.start();
	}
}
