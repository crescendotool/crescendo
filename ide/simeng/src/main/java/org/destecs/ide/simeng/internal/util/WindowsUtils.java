package org.destecs.ide.simeng.internal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WindowsUtils
{
	public static class ProcessInfo
	{
		public final String imageName;
		public final int pid;
		public final String sessionName;
		public final int sessionNumber;
		public final String memUsage;

		public ProcessInfo(String imageName, int pid, String sessionName,
				int sessionNumber, String memUsage)
		{
			this.imageName = imageName;
			this.pid = pid;
			this.sessionName = sessionName;
			this.sessionNumber = sessionNumber;
			this.memUsage = memUsage;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof String)
			{
				return this.imageName.equals(obj);
			}
			return super.equals(obj);
		}
		
		@Override
		public String toString()
		{
			return imageName+" "+pid+" "+sessionName+ " "+ sessionNumber+ " "+memUsage;
		}
	}

	/**
	 * Get all running windows processes
	 * @return a list with process info as shown in the windows taskmanager
	 * @throws IOException
	 */
	public static List<ProcessInfo> getRunningProcesses() throws IOException
	{
		List<ProcessInfo> processes = new ArrayList<ProcessInfo>();

		String line;
		Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((line = input.readLine()) != null)
		{
			String[] items = line.split(",");

			for (int i = 0; i < items.length; i++)
			{
				items[i] = items[i].replaceAll("\"", "");
			}

			if (items.length == 5)
			{
				ProcessInfo info = new ProcessInfo(items[0], Integer.valueOf(items[1]), items[2], Integer.valueOf(items[3]), items[4]);
				processes.add(info);
			}
		}
		input.close();

		return processes;
	}
	
	public static boolean isProcessRunning(String imageName) throws IOException
	{
		for (ProcessInfo process : getRunningProcesses())
		{
			if(process.imageName.endsWith(imageName))
			{
				return true;
			}
		}
		return false;
	}
}
