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
package org.destecs.vdmj.log;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.scheduler.SystemClock;
import org.overturetool.vdmj.scheduler.SystemClock.TimeUnit;

public class SimulationLogger
{
	private static boolean enabled = false;
	private static List<SimulationMessage> events = new LinkedList<SimulationMessage>();
	private static PrintWriter logfile = null;
	private static SimulationMessage cached = null;
	
	private static PrintWriter logfileCsv = null;
	private static boolean isCsvInitialized = false;
	private final static Map<String, SimulationMessage> lastRow = new Hashtable<String, SimulationMessage>();
	
	private static class CsvRow
	{
		public CsvRow(SimulationMessage event,
				Collection<SimulationMessage> values)
		{
			this.trigger = event;
			messages.addAll(values);
			Collections.sort(messages, new Comparator<SimulationMessage>()
			{

				public int compare(SimulationMessage o1, SimulationMessage o2)
				{
					return o1.name.compareTo(o2.name);
				}
			});
		}
		public final SimulationMessage trigger;
		public final List<SimulationMessage> messages = new Vector<SimulationMessage>();
		public String getMessage()
		{
			Iterator<SimulationMessage> itr = messages.iterator();
			StringBuffer buf = new StringBuffer();
			buf.append(SystemClock.internalToTime(TimeUnit.seconds, trigger.timestamp)+",");
			while (itr.hasNext())
			{
				buf.append(itr.next().value);
				if(itr.hasNext())
				{
					buf.append(",");
				}
				
			}
			return buf.toString();
		}
		
		public String getHeader()
		{
			Iterator<SimulationMessage> itr = messages.iterator();
			StringBuffer buf = new StringBuffer();
			buf.append("time_,");
			while (itr.hasNext())
			{
				buf.append(itr.next().name);
				if(itr.hasNext())
				{
					buf.append(",");
				}
				
			}
			return buf.toString();
		}
	}
	private static List<CsvRow> eventsCsv = new LinkedList<CsvRow>();

	public static synchronized void enable(boolean on)
	{
		if (!on)
		{
			dump(true);
			cached = null;
		}

		enabled = on;
	}

	
	public static synchronized void log(SimulationMessage message)
	{
		if (!enabled)
		{
			return;
		}
		
			doLog(message);
		

	}

	
	
	private static synchronized void doLog(SimulationMessage message)
	{
		SimulationMessage event = message;

		
			if (cached != null)
			{
				doInternalLog(cached);
			}

			cached = event;
			return;
		

	}

	
	private static void doInternalLog(SimulationMessage event)
	{
		if (logfile == null)
		{
			Console.out.println(event);
		} else
		{
			events.add(event);
			
			lastRow.put(event.name, event);
			eventsCsv.add(new CsvRow(event,lastRow.values()));
			

			if (events.size() > 1000)
			{
				dump(false);
				dumpCsv(false);
			}
		}
	}




	public static void setLogfile(PrintWriter out)
	{
		enabled = true;
		dump(true); // Write out and close previous
		logfile = out;
		cached = null;
	}
	
	public static void setLogfileCsv(PrintWriter out)
	{
		enabled = true;
		dumpCsv(true); // Write out and close previous
		logfileCsv = out;
		cached = null;
		isCsvInitialized = false;
		prepared = false;
	}

	public static int getLogSize()
	{
		return events.size();
	}

	public static synchronized void dump(boolean close)
	{
		if (logfile != null)
		{
			for (SimulationMessage event : events)
			{
				logfile.println(event.getMessage());
			}

			logfile.flush();
			events.clear();

			if (close)
			{
				logfile.close();
			}
		}
		
		if(close)
		{
			dumpCsv(close);
		}
	}
	
	public static void prepareCsv()
	{
		try{
		for (CsvRow row : eventsCsv)
		{
			for (SimulationMessage event : row.messages)
			{
				lastRow.put(event.name, event);	
			}
		}
		eventsCsv.clear();
//		logfileCsv.println(eventsCsv.get(0).getHeader());
		prepared = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	private static boolean prepared = false;
	
	public static synchronized void dumpCsv(boolean close)
	{
		if (logfileCsv != null && prepared)
		{
			if(!isCsvInitialized&& !eventsCsv.isEmpty())
			{
				logfileCsv.println(eventsCsv.get(0).getHeader());
				isCsvInitialized = true;
			}
			for (CsvRow event : eventsCsv)
			{
				logfileCsv.println(event.getMessage());
			}

			logfileCsv.flush();
			eventsCsv.clear();

			if (close)
			{
				logfileCsv.close();
			}
		}
	}
	
}
