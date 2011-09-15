package org.destecs.ide.simeng.ui.views;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.internal.views.ViewsPlugin;

public class TerminationAction extends Action
{
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
			ViewsPlugin plugin = ViewsPlugin.getDefault();
			URL installURL = plugin.getDescriptor().getInstallURL();
			URL url = new URL(installURL, iconPath + relativePath);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e)
		{
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	ILaunch launch;

	public void setLaunch(ILaunch launch)
	{
		this.launch = launch;
	}

	@Override
	public void run()
	{
		if (this.launch != null)
		{
			try
			{
				this.launch.terminate();
			} catch (DebugException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
