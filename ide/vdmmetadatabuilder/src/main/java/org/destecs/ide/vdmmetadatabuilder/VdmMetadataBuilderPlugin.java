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
