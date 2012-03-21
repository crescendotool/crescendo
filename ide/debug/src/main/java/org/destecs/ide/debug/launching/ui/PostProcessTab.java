package org.destecs.ide.debug.launching.ui;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class PostProcessTab extends AbstractLaunchConfigurationTab {

	private Button checkBoxOctavePlot;
	private WidgetListener fListener = new WidgetListener();
	
	
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		
		createOctaveGroup(comp);
		
	}

	private void createOctaveGroup(Composite comp) {
		Group group = new Group(comp, comp.getStyle());
		group.setText("Octave Options");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		
		checkBoxOctavePlot = new Button(group, SWT.CHECK);
		checkBoxOctavePlot.setText("Show plot automatically when the script runs");
		checkBoxOctavePlot.addSelectionListener(fListener);
		
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS, false);
		
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			checkBoxOctavePlot.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS, false));
		} catch (CoreException e) {
			DestecsDebugPlugin.logError("Error initializing post process tab", e);
		}
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS, checkBoxOctavePlot.getSelection());
		
	}

	public String getName() {		
		return "Post-Processing";
	}

	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		public void widgetSelected(SelectionEvent e)
		{
			updateLaunchConfigurationDialog();
		}
	}

}
