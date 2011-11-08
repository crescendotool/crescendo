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
package org.destecs.core.simulationengine.xmlrpc.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransport;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.destecs.core.simulationengine.log.CustomLoggingUtils;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * @see http://sujitpal.blogspot.com/2010/05/debugging-xml-with-apache-xmlrpc.html
 * @author kela
 */
public class CustomSAXParserTransportFactory extends
		XmlRpcSun15HttpTransportFactory
{
	public static final Logger LogFactory = null;
	public static SAXParserFactory spf = null;

	public CustomSAXParserTransportFactory(XmlRpcClient pClient)
	{
		super(pClient);
	}

	@Override
	public XmlRpcTransport getTransport()
	{
		return new XmlRpcSun15HttpTransportCustomSAXReader(this.getClient());
	}

	public static class XmlRpcSun15HttpTransportCustomSAXReader extends
			XmlRpcSun15HttpTransport
	{
		private final Logger logger = Logger.getLogger(getClass());

		public XmlRpcSun15HttpTransportCustomSAXReader(XmlRpcClient pClient)
		{
			super(pClient);

		}

		@Override
		protected XMLReader newXMLReader() throws XmlRpcException
		{
			XMLReader parser = null;
			try
			{
				System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
				parser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
				// Turn on validation
				parser.setFeature("http://xml.org/sax/features/validation", false);
				parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

			} catch (SAXException e)
			{
				// Ignore it
			}

			return parser;

		}

		/**
		 * Logs the request content in addition to the actual work.
		 */
		@Override
		protected void writeRequest(ReqWriter pWriter) throws IOException,
				XmlRpcException, SAXException
		{
			super.writeRequest(pWriter);
			if (logger.isDebugEnabled())
			{
				ByteArrayOutputStream sos = new ByteArrayOutputStream();

				pWriter.write(sos);
				CustomLoggingUtils.logRequest(logger, getClientUrl(), sos.toString());
			}
		}

		/**
		 * Logs the response from the server, and returns the contents of the response as a ByteArrayInputStream.
		 */
		@Override
		protected InputStream getInputStream() throws XmlRpcException
		{
			InputStream istream = super.getInputStream();
			if (logger.isDebugEnabled())
			{
				return new ByteArrayInputStream(CustomLoggingUtils.logResponse(logger, getClientUrl(), istream).getBytes());
			} else
			{
				return istream;
			}
		}

		private String getClientUrl()
		{
			String source = "unknown";
			try
			{
				source = ((XmlRpcClientConfigImpl) getClient().getConfig()).getServerURL().toString();
			} catch (Exception e)
			{
				// ignore
			}
			return source;
		}
	}

}
