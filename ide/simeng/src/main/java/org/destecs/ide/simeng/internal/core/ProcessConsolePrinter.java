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
package org.destecs.ide.simeng.internal.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



public class ProcessConsolePrinter extends Thread
{
	
	InputStream stream = null;

	public ProcessConsolePrinter( InputStream inputStream) {
		setDaemon(true);
		this.stream = inputStream;
	}

	@Override
	public void run()
	{

		String line = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(stream));
		try
		{
			while ((line = input.readLine()) != null)
			{
				System.out.println(line);
			}
		} catch (IOException e)
		{
			//ignore
		}
	}
}
