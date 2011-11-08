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
package org.destecs.vdm;

import java.io.IOException;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.CPUResource;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.scheduler.RunState;
import org.overturetool.vdmj.scheduler.Signal;
import org.overturetool.vdmj.util.NotSupportedError;
import org.overturetool.vdmj.values.ObjectValue;

public class DBGPThread extends Thread
{
	final DBGPReaderCoSim dbgpReader;

	public DBGPThread(String host, int port, String ideKey,
			Interpreter interpreter, String expression)
	{
		new DBGPVdmThread(this);
		dbgpReader = new DBGPReaderCoSim(host, port, ideKey, interpreter, expression, null);
	}

	@Override
	public void run()
	{
		try
		{
			dbgpReader.startup(null);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static class DBGPVdmThread implements ISchedulableThread
	{
		private Thread thread;
		private long tid = 0;

		public DBGPVdmThread(Thread t)
		{
			this.tid = BasicSchedulableThread.nextThreadID();
			this.thread = t;
		}

		public void duration(long pause, Context ctxt, LexLocation location)
		{
			throw new NotSupportedError();
		}

		public CPUResource getCPUResource()
		{
			throw new NotSupportedError();
		}

		public long getDurationEnd()
		{
			throw new NotSupportedError();
		}

		public long getId()
		{
			return tid;
		}

		public String getName()
		{
			return this.thread.getName();
		}

		public ObjectValue getObject()
		{
			throw new NotSupportedError();
		}

		public RunState getRunState()
		{
			return RunState.RUNNING;
		}

		public long getSwapInBy()
		{
			throw new NotSupportedError();
		}

		public Thread getThread()
		{
			return this.thread;
		}

		public long getTimestep()
		{
			throw new NotSupportedError();
		}

		public void inOuterTimestep(boolean b)
		{
			throw new NotSupportedError();
		}

		public boolean inOuterTimestep()
		{
			throw new NotSupportedError();
		}

		public boolean isActive()
		{
			throw new NotSupportedError();
		}

		public boolean isAlive()
		{
			throw new NotSupportedError();
		}

		public boolean isPeriodic()
		{
			return false;
		}

		public boolean isVirtual()
		{
			throw new NotSupportedError();
		}

		public void locking(Context ctxt, LexLocation location)
		{
			throw new NotSupportedError();
		}

		public void run()
		{
			throw new NotSupportedError();
		}

		public void runslice(long slice)
		{
			throw new NotSupportedError();
		}

		public void setName(String name)
		{
			throw new NotSupportedError();
		}

		public void setSignal(Signal sig)
		{
			throw new NotSupportedError();
		}

		public void setState(RunState newstate)
		{
			throw new NotSupportedError();
		}

		public void setSwapInBy(long swapInBy)
		{
			throw new NotSupportedError();
		}

		public void setTimestep(long step)
		{
			throw new NotSupportedError();
		}

		public void start()
		{
			throw new NotSupportedError();
		}

		public void step(Context ctxt, LexLocation location)
		{
			throw new NotSupportedError();
		}

		public void suspendOthers()
		{
			throw new NotSupportedError();
		}

		public void waiting(Context ctxt, LexLocation location)
		{
			throw new NotSupportedError();
		}

		public void alarming(long expected)
		{
			//Not used
		}
		public long getAlarmWakeTime()
		{
			//Not used
			return 0;
		}
		public void clearAlarm()
		{
			//Not used
		}

	}
}
