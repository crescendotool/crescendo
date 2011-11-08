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
