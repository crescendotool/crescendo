package org.destecs.vdmj.scheduler;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.scheduler.CPUResource;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.scheduler.RunState;
import org.overturetool.vdmj.scheduler.Signal;
import org.overturetool.vdmj.values.ObjectValue;

public class EventThread implements ISchedulableThread
{
	private Thread thread;
	private long tid = 0;
	public EventThread(Thread t)
	{
		//Not used
		this.thread = t;
	}
	public void duration(long pause, Context ctxt, LexLocation location)
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
		return "Event thread - "+ getId();
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

	public void locking(Context ctxt, LexLocation location)
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

	public void step(Context ctxt, LexLocation location)
	{
		//Not used
	}

	public void suspendOthers()
	{
		//Not used
	}

	public void waiting(Context ctxt, LexLocation location)
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

}
