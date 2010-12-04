package org.destecs.ide.debug.launching.ui;

import java.net.URL;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SimulatorConnectionTab extends AbstractLaunchConfigurationTab
{
	class WidgetListener implements ModifyListener, SelectionListener
	{

		public void modifyText(ModifyEvent e)
		{
			// if(!suspended)
			{
				// validatePage();
				updateLaunchConfigurationDialog();
			}
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
			// if(!suspended)
			{
				/* do nothing */
			}
		}

		public void widgetSelected(SelectionEvent e)
		{
			// if(!suspended)
			{
				// fOperationText.setEnabled(!fdebugInConsole.getSelection());

				updateLaunchConfigurationDialog();
			}
		}
	}

	private Text ctUrl = null;
	private Text deUrl = null;
	private final WidgetListener fListener = new WidgetListener();
	
	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
		// IDebugHelpContextIds.LAUNCH_CONFIGURATION_DIALOG_COMMON_TAB);
		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());
		
		
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
		return "Connection";
	}
	
	@Override
	public String getId()
	{
		return "org.destecs.ide.debug.launching.ui.SimulatorConnectionTab";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		try
		{
			String url = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, "");
			if(url.length()==0)
			{
				url = IDebugConstants.DEFAULT_DE_ENDPOINT;
			}
			deUrl.setText(url);
			
			 url = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, "");
			if(url.length()==0)
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
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, deUrl.getText());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, ctUrl.getText());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ENDPOINT, IDebugConstants.DEFAULT_DE_ENDPOINT);
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, IDebugConstants.DEFAULT_CT_ENDPOINT);
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);
		try
		{
			if(deUrl.getText().length()<=0)
			{
				setErrorMessage("DE URL not set");
				return false;
			}
			new URL(deUrl.getText());
		} catch (Exception e)
		{
			setErrorMessage("DE URL not valid");
		}
		
		try
		{
			if(ctUrl.getText().length()<=0)
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
