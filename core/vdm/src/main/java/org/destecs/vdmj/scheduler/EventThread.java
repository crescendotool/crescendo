package org.destecs.vdmj.scheduler;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
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
		//this.tid =BasicSchedulableThread.nextThreadID();
		this.thread = t;
	}
	public void duration(long pause, Context ctxt, LexLocation location)
	{
		// TODO Auto-generated method stub
		
	}

	public CPUResource getCPUResource()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public long getDurationEnd()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public long getId()
	{
		return 4;//tid;
	}

	public String getName()
	{
		return "Event thread - "+ getId();
	}

	public ObjectValue getObject()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public RunState getRunState()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public long getSwapInBy()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public Thread getThread()
	{
		return this.thread;
	}

	public long getTimestep()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public void inOuterTimestep(boolean b)
	{
		// TODO Auto-generated method stub
		
	}

	public boolean inOuterTimestep()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isActive()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAlive()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPeriodic()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isVirtual()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void locking(Context ctxt, LexLocation location)
	{
		// TODO Auto-generated method stub
		
	}

	public void run()
	{
		// TODO Auto-generated method stub
		
	}

	public void runslice(long slice)
	{
		// TODO Auto-generated method stub
		
	}

	public void setName(String name)
	{
		// TODO Auto-generated method stub
		
	}

	public void setSignal(Signal sig)
	{
		// TODO Auto-generated method stub
		
	}

	public void setState(RunState newstate)
	{
		// TODO Auto-generated method stub
		
	}

	public void setSwapInBy(long swapInBy)
	{
		// TODO Auto-generated method stub
		
	}

	public void setTimestep(long step)
	{
		// TODO Auto-generated method stub
		
	}

	public void start()
	{
		// TODO Auto-generated method stub
		
	}

	public void step(Context ctxt, LexLocation location)
	{
		// TODO Auto-generated method stub
		
	}

	public void suspendOthers()
	{
		// TODO Auto-generated method stub
		
	}

	public void waiting(Context ctxt, LexLocation location)
	{
		// TODO Auto-generated method stub
		
	}

}
