package org.destecs.vdmj.log;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.overturetool.vdmj.messages.Console;

public class SimulationLogger
{
	private static boolean enabled = false;
	private static List<SimulationMessage> events = new LinkedList<SimulationMessage>();
	private static PrintWriter logfile = null;
	private static SimulationMessage cached = null;

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

			if (events.size() > 1000)
			{
				dump(false);
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
	}
}
