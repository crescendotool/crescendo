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
	private Button checkBoxEnableLogging = null;
	private Button checkBoxShowDebugIngo = null;
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

	// private void createSyncScheme(Composite parent)
	// {
	// Group group = new Group(parent, parent.getStyle());
	// group.setText("Synchronization Scheme");
	// GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	//
	// group.setLayoutData(gd);
	//
	// GridLayout layout = new GridLayout();
	// layout.makeColumnsEqualWidth = false;
	// layout.numColumns = 3;
	// group.setLayout(layout);
	//
	// Label label = new Label(group, SWT.MIN);
	// label.setText("Sync Scheme:");
	// gd = new GridData(GridData.BEGINNING);
	// label.setLayoutData(gd);
	//
	// syncSchemeDropDown = new Combo(group, SWT.SINGLE | SWT.BORDER
	// | SWT.READ_ONLY);
	//
	// gd = new GridData(GridData.FILL_HORIZONTAL);
	// syncSchemeDropDown.setLayoutData(gd);
	// syncSchemeDropDown.addModifyListener(fListener);
	//
	// for (SynchronizationScheme scheme : SimulationEngine.SynchronizationScheme.values())
	// {
	// syncSchemeDropDown.add(scheme.toString());
	//
	// }
	//
	// for (int i = 0; i < syncSchemeDropDown.getItemCount(); i++)
	// {
	// if (syncSchemeDropDown.getItem(i).equals(SynchronizationScheme.Default.toString()))
	// {
	// syncSchemeDropDown.select(i);
	// break;
	// }
	//
	// }
	//
	// }

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
			checkBoxEnableLogging.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_ENABLE_LOGGING, false));
			checkBoxShowDebugIngo.setSelection(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_DEBUG_INFO, false));
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

		// syncscheme
		// try
		// {
		// String scheme = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SYNC_SCHEME, "DEFAULT");
		// for (int i = 0; i < syncSchemeDropDown.getItemCount(); i++)
		// {
		// if (syncSchemeDropDown.getItem(i).equals(scheme))
		// {
		// syncSchemeDropDown.select(i);
		// break;
		// }
		//
		// }
		// } catch (Exception e)
		// {
		//
		// }
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		// develop
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_REMOTE_DEBUG, checkBoxRemoteDebug.getSelection());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_ENABLE_LOGGING, checkBoxEnableLogging.getSelection());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_DEBUG_INFO, checkBoxShowDebugIngo.getSelection());
		// connection
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, deUrl.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, ctUrl.getText());
		// syncscheme
		// configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SYNC_SCHEME, syncSchemeDropDown.getText());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// develop
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_REMOTE_DEBUG, false);
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_ENABLE_LOGGING, false);
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_DEBUG_INFO, false);
		// connection
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, "");
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, IDebugConstants.DEFAULT_CT_ENDPOINT);
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

}
