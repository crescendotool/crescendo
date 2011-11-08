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
package org.destecs.ide.simeng.listener;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class CachedLogFileWriter
{
	private List<String> events = new LinkedList<String>();
	private PrintWriter logfile = null;

	public synchronized void log(String event)
	{
		doLog(event);
	}

	private void doLog(String event)
	{
		if (logfile == null)
		{
			System.out.println(event);
		} else
		{
			events.add(event);

			if (events.size() > 1000)
			{
				dump(false);
			}
		}
	}

	public void setLogfile(PrintWriter out)
	{
		dump(true); // Write out and close previous
		setInternalLogFile(out);
		
	}
	
	private synchronized void setInternalLogFile(PrintWriter out)
	{
		logfile = out;	
	}

	public int getLogSize()
	{
		return events.size();
	}

	public synchronized void dump(boolean close)
	{
		if (logfile != null)
		{
			for (String event : events)
			{
				logfile.println(event);
			}

			logfile.flush();
			events.clear();

			if (close)
			{
				logfile.close();
			}
		}
	}
}
