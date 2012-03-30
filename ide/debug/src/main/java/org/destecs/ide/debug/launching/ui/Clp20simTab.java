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

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.launching.ui.internal.Clp20simLogTable;
import org.destecs.ide.debug.launching.ui.internal.Clp20simSettingsControl;
import org.destecs.ide.debug.launching.ui.internal.LogItem;
import org.destecs.ide.debug.launching.ui.internal.SettingItem;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.progress.UIJob;

public class Clp20simTab extends AbstractLaunchConfigurationTab implements IUpdatableTab
{

	public class PopulatorJob extends Job
	{

		private boolean remote;
		private String ctFile;
		private String ctUrl;

		public PopulatorJob(String ctFile, String ctUrl,boolean remote)
		{
			super("20-sim table Populator");
			this.ctFile = ctFile;
			this.ctUrl = ctUrl;
			this.remote = remote;

		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			try
			{
				ProxyICoSimProtocol protocol = Launch20simUtility.launch20sim(ctFile, ctUrl,remote);
				
				SettingItem.readSettingsFromProtocol(protocol,settingItems);
				settingsControl.populateControl(settingItems,tab);

				
				LogItem.readLogItemsFromProtocol(protocol,logItems);

				final UIJob refreshTables = new UIJob("Refresh Tables Job")
				{

					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{												
						settingsControl.refreshInputAndExpand();
						logTableControl.refreshAndReselectInput();
						
						return new Status(IStatus.OK, DestecsDebugPlugin.PLUGIN_ID, "Refreshed Tables Job");
					}
				};
				refreshTables.schedule();

			} catch (MalformedURLException e)
			{
				DestecsDebugPlugin.logWarning("Failed to resolve url for log variable and settings retrival", e);
			} catch (SimulationException e)
			{
				DestecsDebugPlugin.logWarning("Failed to retrieve log variable and settings", e);
			} catch (Exception e)
			{
				DestecsDebugPlugin.logWarning("Failure with log variable and settings retrival", e);
			}

			return new Status(IStatus.OK, DestecsDebugPlugin.PLUGIN_ID, "Populated ok");
		}

	}

	


	
	private Button populateButton;

	private final Set<SettingItem> settingItems = new HashSet<SettingItem>();
	private final Set<LogItem> logItems = new HashSet<LogItem>();

	
	private Clp20simTab tab;
	private Clp20simSettingsControl settingsControl = null;
	private Clp20simLogTable logTableControl = null;

	public void createControl(Composite parent)
	{
		tab = this;
	
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout gl = new GridLayout(1, true);

		comp.setLayout(gl);
		comp.setFont(parent.getFont());

		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		createLogTable(comp);
		createSettingsTable(comp);
		createPopulateButton(comp);

	}

	private void createSettingsTable(Composite comp)
	{
		settingsControl = new Clp20simSettingsControl(false);
		settingsControl.createSettingsTable(comp);		
	}

	private void createLogTable(Composite comp)
	{
		logTableControl = new Clp20simLogTable(logItems);
		logTableControl.createLogTable(comp,tab);
	}

	

	private File getFileFromPath(IProject project, String path)
	{

		IResource r = project.findMember(new Path(path));

		if (r != null && !r.equals(project))
		{
			return r.getLocation().toFile();
		}
		return null;
	}

	private void populateButtonPress()
	{
		populateButton.setEnabled(false);
		IProject project = getProject();

		if (project == null)
		{
			return; // new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID,
					// "Project is not set");

		}

		final String ctFile =  getCtPath();
		final String ctUrl = getCtEndpoint();

		PopulatorJob populator = new PopulatorJob(ctFile, ctUrl,getUseRemoteCtSimulator());

		final UIJob changeButton = new UIJob("Enable populate button")
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				populateButton.setEnabled(true);
				return new Status(IStatus.OK, DestecsDebugPlugin.PLUGIN_ID, "Enabled populate button");
			}
		};

		populator.addJobChangeListener(new JobChangeAdapter()
		{
			public void done(IJobChangeEvent event)
			{
				changeButton.schedule();

			}
		});

		populator.schedule();
	}

	private void createPopulateButton(Composite comp)
	{
		populateButton = createPushButton(comp, "Populate...", null);

		populateButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				populateButtonPress();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{

			}
		});
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		settingsControl.setDefaults(configuration);
		logTableControl.setDefaults(configuration);
	}
	

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		settingsControl.initializeFrom(configuration);
		logTableControl.initializeFrom(configuration);
	}


	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		settingsControl.performApply(configuration);
		logTableControl.performApply(configuration);
	}


	public String getName()
	{
		return "20-sim";
	}

	public IProject getProject()
	{
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof CoSimLaunchConfigurationTab)
			{
				return ((CoSimLaunchConfigurationTab) tab).getProject();
			}
		}
		return null;
	}

	private String getCtPath()
	{
		String base = null;
		String path = null;
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof CoSimLaunchConfigurationTab)
			{
				path =  ((CoSimLaunchConfigurationTab) tab).getCtPath();
			}else if (tab instanceof DevelopLaunchConfigurationTab)
			{
				base = ((DevelopLaunchConfigurationTab) tab).getRemoteProjectBase();
			}
		}
		
		if(getUseRemoteCtSimulator())
		{
			return base+"\\"+path;
		}else
		{
			return getFileFromPath(getProject(), path).getAbsolutePath();
		}
		
	}

	private String getCtEndpoint()
	{
		if(!getUseRemoteCtSimulator())
		{
			return IDebugConstants.DEFAULT_CT_ENDPOINT;
		}
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof DevelopLaunchConfigurationTab)
			{
				return ((DevelopLaunchConfigurationTab) tab).getCtUrl();
			}
		}
		return null;
	}
	
	private boolean getUseRemoteCtSimulator()
	{
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof DevelopLaunchConfigurationTab)
			{
				return ((DevelopLaunchConfigurationTab) tab).useRemoteCtSimulator();
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.destecs.ide.debug.launching.ui.IUpdatableTab#updateTab()
	 */
	public void updateTab() {
		updateLaunchConfigurationDialog();
		
	}

}
