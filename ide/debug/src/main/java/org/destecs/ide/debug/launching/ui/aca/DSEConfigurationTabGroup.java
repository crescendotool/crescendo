package org.destecs.ide.debug.launching.ui.aca;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class DSEConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public DSEConfigurationTabGroup() {
		super();
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new DseMainTab(),
				new DseArchitectureTab(),
				new CommonTab() };
		setTabs(tabs);

	}

}
