package org.destecs.ide.debug.launching.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class coSimLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public coSimLaunchConfigurationTabGroup() {
		super();
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { 
				new CommonTab()
		};
		setTabs(tabs);
	}

}
