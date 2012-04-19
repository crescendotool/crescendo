package org.destecs.ide.debug.launching.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class DestecsDirectoryLaunchTabGroup extends
		AbstractLaunchConfigurationTabGroup
{

	public DestecsDirectoryLaunchTabGroup()
	{
		super();
	}

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode)
	{
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new DestecsDirectoryLaunchMainTab(),
				new CommonTab() };
		setTabs(tabs);
	}

}
