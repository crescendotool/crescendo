package org.destecs.ide.simeng.actions;

import java.util.List;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;

public class ResumeAction extends BaseSimulationControlAction
{
	private PauseAction pauseAction;

	@Override
	public String getText()
	{
		return "Terminate";
	}

	public ImageDescriptor getImageDescriptor()
	{
		return getImageDescriptor("resume_co.gif");
	}
	
	@Override
	public void run()
	{
		List<ISimulationControlProxy> proxies = new Vector<ISimulationControlProxy>(proxy);
		for (ISimulationControlProxy p : proxies)
		{
			if (p != null)
			{
				try
				{
					p.resume();
				} catch (Exception e)
				{
					// Ignore
				}
			}
		}
		if(pauseAction!=null)
		{
			pauseAction.setEnabled(true);
			setEnabled(false);
		}
	}

	public void setPause(PauseAction pauseAction)
	{
		this.pauseAction = pauseAction;
	}
}
