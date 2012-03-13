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
package org.destecs.ide.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class DestecsUIPlugin extends AbstractUIPlugin {

	private static DestecsUIPlugin plugin;

	public DestecsUIPlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(BundleContext context) throws Exception {		
		super.start(context);
		plugin = this;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static DestecsUIPlugin getDefault() {
		return plugin;
	}
	
	public static void log(Exception e) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IDestecsUiConstants.PLUGIN_ID,"DestecsUIPlugin",e));
		
	}

	public static void logErrorMessage(String message) {
		getDefault().getLog().log(new Status(IStatus.ERROR,IDestecsUiConstants.PLUGIN_ID,message));

	}
	
	public static void log(String message, Throwable exception) {
			getDefault().getLog().log(
					new Status(IStatus.ERROR, IDestecsUiConstants.PLUGIN_ID, message, exception));
	}

}
