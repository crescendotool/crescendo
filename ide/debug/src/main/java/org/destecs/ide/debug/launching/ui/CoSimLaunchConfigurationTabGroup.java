package org.destecs.ide.debug.launching.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class CoSimLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup
{

	public CoSimLaunchConfigurationTabGroup()
	{
		super();
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode)
	{
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new CommonTab(), new SharedDesignParameterTab() };
		setTabs(tabs);
	}

}
