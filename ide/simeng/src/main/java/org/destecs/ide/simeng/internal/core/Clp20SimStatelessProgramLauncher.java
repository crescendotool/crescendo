package org.destecs.ide.simeng.internal.core;

import java.io.File;
import java.io.IOException;

import org.destecs.ide.simeng.internal.util.WindowsUtils;
import org.destecs.ide.simeng.internal.util.WindowsUtils.ProcessInfo;

public class Clp20SimStatelessProgramLauncher extends Clp20SimProgramLauncher
{

	private Process p;
	private ProcessInfo info;
	boolean leaveProcessRunning;

	public Clp20SimStatelessProgramLauncher(File model,
			boolean leaveProcessRunning)
	{
		super(model);
		this.leaveProcessRunning = leaveProcessRunning;
	}

	@Override
	public Process launch() throws IOException
	{

		boolean isRunning = false;
		if (WindowsUtils.isProcessRunning(processName))
		{
			isRunning = true;
		}

		if (isRunning)
		{
			throw new IOException("The process "+processName+" is running but the model might be dirty, please close 20-sim and try again.");
		}

		p = super.launch();
		info = WindowsUtils.getProcessInfo(processName);
		return p;
	}

	public void kill()
	{
		if (leaveProcessRunning)
		{
			return;
		}

		if (p != null)
		{
			p.destroy();
		}
		// Just to be sure it actually is killed
		if (info != null)
		{
			WindowsUtils.kill(info.pid);
		}
	}

	public boolean isRunning()
	{
		try
		{
			if (p != null)
			{
				p.exitValue();
			}
			return false;
		} catch (IllegalThreadStateException e)
		{
			return true;
		}
	}
}
