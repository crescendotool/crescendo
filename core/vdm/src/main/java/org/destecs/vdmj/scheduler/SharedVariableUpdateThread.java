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
package org.destecs.vdmj.scheduler;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.ISchedulableThread;
import org.overture.interpreter.scheduler.RunState;
import org.overture.interpreter.scheduler.Signal;
import org.overture.interpreter.values.ObjectValue;


public class SharedVariableUpdateThread implements ISchedulableThread
{
	private Thread thread;
	private long tid = 0;
	public SharedVariableUpdateThread(Thread t)
	{
		this.tid =BasicSchedulableThread.nextThreadID();
		this.thread = t;
	}
	public void duration(long pause, Context ctxt, ILexLocation location)
	{
		//Not used
	}

	public CPUResource getCPUResource()
	{
		//Not used
		return null;
	}

	public long getDurationEnd()
	{
		//Not used
		return 0;
	}

	public long getId()
	{
		return tid;
	}

	public String getName()
	{
		return "Shared Variable Update thread - "+ getId();
	}

	public ObjectValue getObject()
	{
		//Not used
		return null;
	}

	public RunState getRunState()
	{
		//Not used
		return null;
	}

	public long getSwapInBy()
	{
		//Not used
		return 0;
	}

	public Thread getThread()
	{
		return this.thread;
	}

	public long getTimestep()
	{
		//Not used
		return 0;
	}

	public void inOuterTimestep(boolean b)
	{
		//Not used
	}

	public boolean inOuterTimestep()
	{
		//Not used
		return false;
	}

	public boolean isActive()
	{
		//Not used
		return false;
	}

	public boolean isAlive()
	{
		//Not used
		return false;
	}

	public boolean isPeriodic()
	{
		//Not used
		return false;
	}

	public boolean isVirtual()
	{
		//Not used
		return false;
	}

	public void locking(Context ctxt, ILexLocation location)
	{
		//Not used
	}

	public void run()
	{
		//Not used
	}

	public void runslice(long slice)
	{
		//Not used
	}

	public void setName(String name)
	{
		//Not used
	}

	public void setSignal(Signal sig)
	{
		//Not used
	}

	public void setState(RunState newstate)
	{
		//Not used
	}

	public void setSwapInBy(long swapInBy)
	{
		//Not used
	}

	public void setTimestep(long step)
	{
		//Not used
	}

	public void start()
	{
		//Not used
	}

	public void step(Context ctxt, ILexLocation location)
	{
		//Not used
	}

	public void suspendOthers()
	{
		//Not used
	}

	public void waiting(Context ctxt, ILexLocation location)
	{
		//Not used
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
	public boolean stopThread()
	{
		return false;
	}
	public void reschedule(Context ctxt, ILexLocation location)
	{
		//Not used
	}
}
