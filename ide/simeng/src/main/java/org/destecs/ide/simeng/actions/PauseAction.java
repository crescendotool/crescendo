package org.destecs.ide.simeng.actions;

import org.destecs.ide.simeng.Activator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.resource.ImageDescriptor;

public class PauseAction extends BaseSimulationControlAction
{

	@Override
	public String getText()
	{
		return "Pause All";
	}

	public ImageDescriptor getImageDescriptor()
	{
		return getImageDescriptor("suspend_co.gif");
	}
	
	@Override
	public void run()
	{
		try
		{
			IDebugTarget target = getRunningTarget();

			if (target != null)
			{

				target.suspend();

			}
		} catch (DebugException e)
		{
			Activator.log(e);
		}
		catch (CoreException e)
		{
			Activator.log(e);
		}
	}


	@Override
	protected void doTerminate()
	{
		setEnabled(false);
	}

	@Override
	protected void doResume()
	{
		setEnabled(true);
	}

	@Override
	protected void doSuspend()
	{
		setEnabled(false);
	}
}
