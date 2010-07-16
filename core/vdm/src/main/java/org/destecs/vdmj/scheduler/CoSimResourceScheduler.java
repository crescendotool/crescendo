package org.destecs.vdmj.scheduler;

import org.destecs.vdm.SimulationManager;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.scheduler.MainThread;
import org.overturetool.vdmj.scheduler.Resource;
import org.overturetool.vdmj.scheduler.ResourceScheduler;
import org.overturetool.vdmj.scheduler.RunState;
import org.overturetool.vdmj.scheduler.SchedulableThread;
import org.overturetool.vdmj.scheduler.Signal;
import org.overturetool.vdmj.scheduler.SystemClock;
import org.overturetool.vdmj.values.BUSValue;

public class CoSimResourceScheduler extends ResourceScheduler
{
	public CoSimResourceScheduler()
	{
		SimulationManager.getInstance().register(this);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long nextSimulationStop = new Long(0);
	
	private boolean forceStop = false;
	
	@Override
	public void start(MainThread main)
	{
		mainThread = main;
		SimulationManager.getInstance().setMainContext(mainThread.ctxt);
		BUSValue.start();	// Start BUS threads first...

		boolean idle = true;
		stopping = false;

		do
		{
			long minstep = Long.MAX_VALUE;
			idle = true;
			
			if(forceStop)
			{
				SchedulableThread.signalAll(Signal.TERMINATE);
				return;
			}

			for (Resource resource: resources)
			{
				if (resource.reschedule())
				{
					idle = false;
				}
				else
				{
					long d = resource.getMinimumTimestep();

					if (d < minstep)
					{
						minstep = d;
					}
				}
			}

			if (idle && minstep >= 0 && minstep < Long.MAX_VALUE)
			{
				if(canAdvanceSimulationTime(minstep))
				{
					SystemClock.advance(minstep);
	
					for (Resource resource: resources)
					{
						resource.advance();
					}
				}

				idle = false;
			}
		}
		while (!idle && main.getRunState() != RunState.COMPLETE);

		stopping = true;

		if (main.getRunState() != RunState.COMPLETE)
		{
    		for (Resource resource: resources)
    		{
    			if (resource.hasActive())
    			{
   					Console.err.println("DEADLOCK detected");
					SchedulableThread.signalAll(Signal.DEADLOCKED);

					while (main.isAlive())
					{
						try
                        {
	                        Thread.sleep(500);
                        }
                        catch (InterruptedException e)
                        {
	                        // ?
                        }
					}

    				break;
    			}
    		}
		}

		SchedulableThread.signalAll(Signal.TERMINATE);
	}

	private boolean canAdvanceSimulationTime(long minstep)
	{
		if(SystemClock.getWallTime()+minstep <= nextSimulationStop)
		{
			return true; // OK, just proceed there is still simulation time left
		}else
		{
			//We have reached the allowed simulation time
			synchronized (nextSimulationStop)
			{
				nextSimulationStop = SimulationManager.getInstance().waitForStep(minstep);
				if(SystemClock.getWallTime()+minstep <= nextSimulationStop)
				{
					return true;
				}else
				{
					return false;
				}
			}
			
		}
		
	}

	public void stop()
	{
		this.forceStop = true;
	}

}
