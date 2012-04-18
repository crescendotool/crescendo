package org.destecs.ide.simeng.actions;

import org.destecs.ide.simeng.Activator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.resource.ImageDescriptor;

public class TerminationAction extends BaseSimulationControlAction 
{
	@Override
	public String getText()
	{
		return "Terminate All";
	}

	public ImageDescriptor getImageDescriptor()
	{
		return getImageDescriptor("terminatedlaunch_obj.gif");
	}
	
	@Override
	public void run()
	{
		try
		{
			IDebugTarget target = getRunningTarget();

			if (target != null)
			{

				target.terminate();

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
		System.out.println("DISenabling Terminate Button");
		
	}

	@Override
	protected void doResume()
	{
		setEnabled(true);
		System.out.println("Enabling Terminate Button");
	}

	@Override
	protected void doSuspend()
	{
		setEnabled(true);
		System.out.println("Enabling Terminate Button");
	}
}
