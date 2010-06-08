package org.destecs.protocol.xmlrpc.serializer;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.common.XmlRpcStreamConfig;
import org.apache.xmlrpc.serializer.DefaultXMLWriterFactory;
import org.xml.sax.ContentHandler;

public class ObservableXMLWriterFactory extends DefaultXMLWriterFactory
{
	@Override
	public ContentHandler getXmlWriter(XmlRpcStreamConfig pConfig,
			OutputStream pStream) throws XmlRpcException
	{
		return super.getXmlWriter(pConfig, new CustomOut(pStream));	
	}

	public class CustomOut extends OutputStream
	{
		OutputStream out;

		public CustomOut(OutputStream out)
		{
			this.out = out;
		}

		@Override
		public void write(byte[] b) throws IOException
		{
			out.write(b);
		}

		@Override
		public synchronized void write(byte[] b, int off, int len)
				throws IOException
		{
			System.out.println(new String(b).trim());
			out.write(b, off, len);
		}

		@Override
		public synchronized void write(int b) throws IOException
		{
			out.write(b);
		}
	}
}
