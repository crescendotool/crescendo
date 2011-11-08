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

	public static class CustomOut extends OutputStream
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
