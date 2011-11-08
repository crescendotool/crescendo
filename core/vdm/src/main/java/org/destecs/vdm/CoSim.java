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

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
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
	private static int port = 8080;
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
		if (args.length > 1 && args[0].equals("-p"))
		{
			try
			{
				port = Integer.parseInt(args[1]);
			} catch (Exception e)
			{
				System.err.println("Parse error of port number");
			}
		}
		WebServer webServer = new WebServer(port);
		
		Logger log = Logger.getLogger("org.apache.xmlrpc.server.XmlRpcStreamServer");
		log.addAppender(new ConsoleAppender(new SimpleLayout()));
		log.setLevel(Level.OFF);
		

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
		serverConfig.setEnabledForExceptions(true);

		webServer.start();
	}
}
