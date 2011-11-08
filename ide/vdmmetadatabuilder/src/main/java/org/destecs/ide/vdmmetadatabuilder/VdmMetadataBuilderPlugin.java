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
package org.destecs.ide.vdmmetadatabuilder;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class VdmMetadataBuilderPlugin extends Plugin {

	public static final String PLUGIN_ID = IVdmMetadataBuilder.PLUGIN_ID;
	public static final boolean DEBUG = true;
	private static VdmMetadataBuilderPlugin plugin;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// ResourcesPlugin.getWorkspace().addResourceChangeListener(ResourceManager.getInstance());
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static VdmMetadataBuilderPlugin getDefault() {
		return plugin;
	}

	public static void log(Throwable exception) {

		if (DEBUG) {
			getDefault().getLog().log(
					new Status(IStatus.ERROR, PLUGIN_ID,VdmMetadataBuilderPlugin.class.getName(), exception));
		}
	}

	public static void log(String message, Throwable exception) {
		if (DEBUG) {
			getDefault().getLog().log(
					new Status(IStatus.ERROR, PLUGIN_ID, message, exception));
		}
	}

}
