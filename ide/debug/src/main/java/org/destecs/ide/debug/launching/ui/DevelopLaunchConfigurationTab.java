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
package org.destecs.ide.debug.launching.ui;

import java.net.URL;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DevelopLaunchConfigurationTab extends
		AbstractLaunchConfigurationTab
{
	class WidgetListener implements ModifyListener, SelectionListener
	{
		public void modifyText(ModifyEvent e)
		{
			// validatePage();
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			/* do nothing */
		}

		public void widgetSelected(SelectionEvent e)
		{
			// fOperationText.setEnabled(!fdebugInConsole.getSelection());

			updateLaunchConfigurationDialog();
		}
	}

	private Button checkBoxRemoteDebug = null;
	private Button checkBoxDebug = null;
	private Button checkBoxEnableLogging = null;
	private Button checkBoxShowDebugIngo = null;
	private Button checkBoxShowOctavePlot = null;
	private Text ctUrl = null;
	private Text deUrl = null;
	// private Combo syncSchemeDropDown;
	private WidgetListener fListener = new WidgetListener();

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		createDevelopGroup(comp);
		createConnectionGroup(comp);
		// createSyncScheme(comp);
	}

	public void createDevelopGroup(Composite comp)
	{
		Group group = new Group(comp, SWT.NONE);
		group.setText("Development options");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 3;
		group.setLayout(layout);

		checkBoxRemoteDebug = new Button(group, SWT.CHECK);
		checkBoxRemoteDebug.setText("Remote debug");
		checkBoxRemoteDebug.setSelection(false);
		checkBoxRemoteDebug.addSelectionListener(fListener);

		checkBoxEnableLogging = new Button(group, SWT.CHECK);
		checkBoxEnableLogging.setText("Enable logging");
		checkBoxEnableLogging.setSelection(false);
		checkBoxEnableLogging.addSelectionListener(fListener);

		checkBoxShowDebugIngo = new Button(group, SWT.CHECK);
		checkBoxShowDebugIngo.setText("Enable runtime debug info");
		checkBoxShowDebugIngo.setSelection(false);
		checkBoxShowDebugIngo.addSelectionListener(fListener);
		
		checkBoxDebug = new Button(group, SWT.CHECK);
		checkBoxDebug.setText("Enable General Debug");
		checkBoxDebug.setSelection(false);
		checkBoxDebug.addSelectionListener(fListener);
		
		checkBoxShowOctavePlot = new Button(group, SWT.CHECK);
		checkBoxShowOctavePlot.setText("Show Octave Plots From Script");
		checkBoxShowOctavePlot.setSelection(false);
		checkBoxShowOctavePlot.addSelectionListener(fListener);
		
	}

	private void createConnectionGroup(Composite comp)
	{
		Group group = new Group(comp, comp.getStyle());
		group.setText("Simulation Connection");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		group.setLayout(layout);

		Label label = new Label(group, SWT.MIN);
		label.setText("DE Simulator URL:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		deUrl = new Text(group, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		deUrl.setLayoutData(gd);
		deUrl.addModifyListener(fListener);

		label = new Label(group, SWT.MIN);
		label.setText("CT Simulator URL:");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		ctUrl = new Text(group, SWT.SINGLE | SWT.BORDER);

		gd = new GridData(GridData.FILL_HORIZONTAL);
		ctUrl.setLayoutData(gd);
		ctUrl.addModifyListener(fListener);
	}

	public String getName()
	{
		return "Advanced";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{

		// develop
		try
		{
			checkBoxRemoteDebug.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_REMOTE_DEBUG, false));
			checkBoxDebug.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DEBUG, false));
			checkBoxEnableLogging.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_ENABLE_LOGGING, false));
			checkBoxShowDebugIngo.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_DEBUG_INFO, false));
			checkBoxShowOctavePlot.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS, false));
		} catch (CoreException e)
		{
			if (DestecsDebugPlugin.DEBUG)
			{
				DestecsDebugPlugin.log(new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID, "Error in develop launch configuration tab", e));
			}
		}
		// connection
		try
		{
			String url = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, "");
			deUrl.setText(url);
			url = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, "");
			if (url.length() == 0)
			{
				url = IDebugConstants.DEFAULT_CT_ENDPOINT;
			}
			ctUrl.setText(url);
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Error fetching connections from launch configuration", e);
		}

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		// develop
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_REMOTE_DEBUG, checkBoxRemoteDebug.getSelection());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DEBUG, checkBoxDebug.getSelection());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_ENABLE_LOGGING, checkBoxEnableLogging.getSelection());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_DEBUG_INFO, checkBoxShowDebugIngo.getSelection());
		// connection
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, deUrl.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, ctUrl.getText());
		// syncscheme
		// configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SYNC_SCHEME, syncSchemeDropDown.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS, checkBoxShowOctavePlot.getSelection());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// develop
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_REMOTE_DEBUG, false);
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DEBUG, false);
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_ENABLE_LOGGING, false);
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_DEBUG_INFO, false);
		// connection
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, "");
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, IDebugConstants.DEFAULT_CT_ENDPOINT);
		
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS, false);
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);
		try
		{
			if (ctUrl.getText().length() <= 0)
			{
				setErrorMessage("CT URL not set");
				return false;
			}
			new URL(ctUrl.getText());
		} catch (Exception e)
		{
			setErrorMessage("CT URL not valid");
		}
		return super.isValid(launchConfig);
	}

	public String getCtUrl() {
		return ctUrl.getText();
	}

}
