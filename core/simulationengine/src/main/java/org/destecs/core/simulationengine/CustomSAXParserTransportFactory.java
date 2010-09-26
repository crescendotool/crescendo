package org.destecs.core.simulationengine;

import javax.xml.parsers.SAXParserFactory;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransport;
import org.apache.xmlrpc.client.XmlRpcSun15HttpTransportFactory;
import org.apache.xmlrpc.client.XmlRpcTransport;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class CustomSAXParserTransportFactory extends
		XmlRpcSun15HttpTransportFactory
{
	public static SAXParserFactory spf = null;

	public CustomSAXParserTransportFactory(XmlRpcClient pClient)
	{
		super(pClient);
	}

	@Override
	public XmlRpcTransport getTransport()
	{
		// return super.getTransport();
		return new XmlRpcSun15HttpTransportCustomSAXReader(this.getClient());
	}

	public class XmlRpcSun15HttpTransportCustomSAXReader extends
			XmlRpcSun15HttpTransport
	{

		public XmlRpcSun15HttpTransportCustomSAXReader(XmlRpcClient pClient)
		{
			super(pClient);

		}

		@Override
		protected XMLReader newXMLReader() throws XmlRpcException
		{
			XMLReader parser=null;
			try
			{
				System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
				parser = org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
				// Turn on validation
				parser.setFeature("http://xml.org/sax/features/validation", false);
				parser.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
				
				// spf.setNamespaceAware(true);
				// spf.setValidating(false);
				// try {
				// spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
				// } catch (javax.xml.parsers.ParserConfigurationException e) {
				// // Ignore it
				// } catch (org.xml.sax.SAXException e) {
				// // Ignore it
				// }
				// try {
				// spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
				// } catch (javax.xml.parsers.ParserConfigurationException e) {
				// // Ignore it
				// } catch (org.xml.sax.SAXException e) {
				// // Ignore it
				// }

			} catch (SAXException e)
			{
				// Ignore it
			}

			return parser;

		}
	}

}
