package org.destecs.ide.simeng.actions;


import org.destecs.ide.simeng.Activator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.resource.ImageDescriptor;

public class ResumeAction extends BaseSimulationControlAction 
{

	@Override
	public String getText()
	{
		return "Resume All";
	}

	public ImageDescriptor getImageDescriptor()
	{
		return getImageDescriptor("resume_co.gif");
	}
	
	@Override
	public void run()
	{
		try
		{
			IDebugTarget target = getRunningTarget();

			if (target != null)
			{

				target.resume();

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
		System.out.println("DISenabling Resume Button");
		
	}

	@Override
	protected void doResume()
	{
		setEnabled(false);
		System.out.println("DISenabling Resume Button");
		
	}

	@Override
	protected void doSuspend()
	{
		System.out.println("Enabling Resume Button");
		setEnabled(true);
		
	}
}
