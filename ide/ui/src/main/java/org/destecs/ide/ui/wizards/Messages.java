package org.destecs.ide.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
	private static final String BUNDLE_NAME = "org.destecs.ide.ui.wizards.messages"; //$NON-NLS-1$
	public static String DestecsNewWizard_0;
	public static String DestecsNewWizard_4;
	static
	{
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages()
	{
	}
}
