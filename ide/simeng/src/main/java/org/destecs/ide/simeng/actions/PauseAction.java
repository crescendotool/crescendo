package org.destecs.ide.simeng.actions;

import java.util.List;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;

public class PauseAction extends BaseSimulationControlAction
{
	private ResumeAction reumeAction;

	@Override
	public String getText()
	{
		return "Pause";
	}

	public ImageDescriptor getImageDescriptor()
	{
		return getImageDescriptor("suspend_co.gif");
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
					p.pause();
				} catch (Exception e)
				{
					// Ignore
				}
			}
		}
		if(reumeAction!=null)
		{
			reumeAction.setEnabled(true);
			setEnabled(false);
		}
	}

	public void setResume(ResumeAction resumeAction)
	{
		this.reumeAction = resumeAction;
	}
}
