package org.destecs.ide.simeng.internal.core;

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
