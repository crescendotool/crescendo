package org.destecs.ide.debug.launching.ui.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.destecs.protocol.ProxyICoSimProtocol;

public class LogItem implements Comparable<LogItem>
{
	public String name;
	public List<Integer> size;

	public LogItem(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static void readLogItemsFromProtocol(ProxyICoSimProtocol protocol,
			Set<LogItem> logItems) throws Exception
	{
		logItems.clear();
		List<Map<String, Object>> getLog = protocol.queryVariables();
		for (Map<String, Object> elem : getLog)
		{
			LogItem item = new LogItem(elem.get("name").toString());
			
			logItems.add(item);
		}
		
	}

	@Override
	public int compareTo(LogItem o)
	{
		return name.compareTo(o.name);
	}
}
