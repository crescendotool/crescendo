package org.destecs.ide.simeng.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.destecs.ide.simeng.Activator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public class TerminationAction extends Action
{

	public TerminationAction()
	{
	}

	public TerminationAction(ITerminationProxy proxy)
	{
		this.proxy = proxy;
	}

	@Override
	public String getText()
	{
		return "Terminate";
	}

	public ImageDescriptor getImageDescriptor()
	{
		return getImageDescriptor("terminatedlaunch_obj.gif");
	}

	/**
	 * Returns the image descriptor with the given relative path.
	 */
	private ImageDescriptor getImageDescriptor(String relativePath)
	{
		String iconPath = "icons/";
		try
		{
			Activator plugin = Activator.getDefault();
			URL installURL = plugin.getDescriptor().getInstallURL();
			URL url = new URL(installURL, iconPath + relativePath);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e)
		{
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	ITerminationProxy proxy;

	public void setTerminationProxy(ITerminationProxy proxy)
	{
		this.proxy = proxy;
	}

	@Override
	public void run()
	{
		if (this.proxy != null)
		{
			this.proxy.terminate();
		}
	}
}
