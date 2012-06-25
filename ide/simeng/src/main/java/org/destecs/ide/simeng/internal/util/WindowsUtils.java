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
package org.destecs.ide.simeng.internal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @see http://techmonks.net/windowsxp-using-the-command-prompt-to-see-and-kill-
 *      processes/
 * @author kela
 * 
 */
public class WindowsUtils {
	public static class ProcessInfo {
		public final String imageName;
		public final int pid;
		public final String sessionName;
		public final int sessionNumber;
		public final String memUsage;

		public ProcessInfo(String imageName, int pid, String sessionName,
				int sessionNumber, String memUsage) {
			this.imageName = imageName;
			this.pid = pid;
			this.sessionName = sessionName;
			this.sessionNumber = sessionNumber;
			this.memUsage = memUsage;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof String) {
				return this.imageName.equals(obj);
			}
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return imageName + " " + pid + " " + sessionName + " "
					+ sessionNumber + " " + memUsage;
		}
	}

	/**
	 * Get all running windows processes
	 * 
	 * @return a list with process info as shown in the windows taskmanager
	 * @throws IOException
	 */
	public static List<ProcessInfo> getRunningProcesses() throws IOException {
		List<ProcessInfo> processes = new ArrayList<ProcessInfo>();

		String line;
		Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
		BufferedReader input = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		while ((line = input.readLine()) != null) {
			String[] items = line.split("\",");

			for (int i = 0; i < items.length; i++) {
				items[i] = items[i].replaceAll("\"", "");
			}

			if (items.length >= 5) {
				ProcessInfo info = new ProcessInfo(items[0],
						Integer.valueOf(items[1]), items[2],
						Integer.valueOf(items[3]), items[4]);
				processes.add(info);
			}
		}
		input.close();

		return processes;
	}

	public static boolean kill(Integer pid) {
		try {
			Runtime.getRuntime().exec("tskill.exe "+pid);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean isProcessRunning(String imageName) throws IOException {
		for (ProcessInfo process : getRunningProcesses()) {
			if (process.imageName.endsWith(imageName)) {
				return true;
			}
		}
		return false;
	}
	
	public static ProcessInfo getProcessInfo(String imageName) throws IOException {
		for (ProcessInfo process : getRunningProcesses()) {
			if (process.imageName.endsWith(imageName)) {
				return process;
			}
		}
		return null;
	}
}
