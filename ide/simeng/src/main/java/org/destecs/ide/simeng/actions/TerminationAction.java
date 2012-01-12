package org.destecs.ide.simeng.actions;

import java.util.List;
import java.util.Vector;

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
		List<ISimulationControlProxy> proxies = new Vector<ISimulationControlProxy>(proxy);
		for (ISimulationControlProxy p : proxies)
		{
			if (p != null)
			{
				try
				{
					p.terminate();
				} catch (Exception e)
				{
					// Ignore
				}
				removeSimulationControlProxy(p);
			}
		}
	}
}
