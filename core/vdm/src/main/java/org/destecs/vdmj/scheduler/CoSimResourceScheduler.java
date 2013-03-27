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

import org.destecs.vdm.SimulationManager;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.MainThread;
import org.overture.interpreter.scheduler.Resource;
import org.overture.interpreter.scheduler.ResourceScheduler;
import org.overture.interpreter.scheduler.RunState;
import org.overture.interpreter.scheduler.SharedStateListner;
import org.overture.interpreter.scheduler.Signal;
import org.overture.interpreter.scheduler.SystemClock;
import org.overture.interpreter.values.BUSValue;

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

	private Long nextSimulationStop = Long.valueOf(0);

	private boolean forceStop = false;

	@Override
	public void start(MainThread main)
	{
		mainThread = main;
		SimulationManager.getInstance().setMainContext(mainThread.ctxt);
		BUSValue.start(); // Start BUS threads first...

		boolean idle = true;
		stopping = false;

		do
		{
			long minstep = Long.MAX_VALUE;
			idle = true;

			if (forceStop)
			{
				BasicSchedulableThread.signalAll(Signal.TERMINATE);
				return;
			}

			for (Resource resource : resources)
			{
				if (resource.reschedule())
				{
					idle = false;
				} else
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
				// System.out.println("Min step "+ minstep + " - Time: "+SystemClock.getWallTime());
				if (canAdvanceSimulationTime(minstep))
				{
					SystemClock.advance(minstep);

					for (Resource resource : resources)
					{
						resource.advance();
					}
				}

				idle = false;
			}

		} while (/*!idle &&*/ main.getRunState() != RunState.COMPLETE);

		stopping = true;
		/*
		if (main.getRunState() != RunState.COMPLETE)
		{
			for (Resource resource : resources)
			{
				if (resource.hasActive())
				{
					Console.err.println("DEADLOCK detected");
					BasicSchedulableThread.signalAll(Signal.DEADLOCKED);

					while (main.isAlive())
					{
						try
						{
							Thread.sleep(500);
						} catch (InterruptedException e)
						{
							// ?
						}
					}

					break;
				}
			}
		}*/

		BasicSchedulableThread.signalAll(Signal.TERMINATE);
	}

	private synchronized boolean canAdvanceSimulationTime(long minstep)
	{
		long newTime = SystemClock.getWallTime() + minstep;
		SimulationManager manager = SimulationManager.getInstance();
		if (newTime <= nextSimulationStop || ( manager.isOptimizationEnabled() && SharedStateListner.isAutoIncrementTime() && newTime <manager.getFinishTime()))
		{
			return true; // OK, just proceed there is still simulation time left
		} else
		{
			// We have reached the allowed simulation time
			nextSimulationStop = manager.waitForStep(minstep);
			if (newTime <= nextSimulationStop)
			{
				return true;
			} else
			{
				return false;
			}

		}

	}

	public void stop()
	{
		this.forceStop = true;
	}

}
