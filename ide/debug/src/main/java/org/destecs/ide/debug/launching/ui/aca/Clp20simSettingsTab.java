package org.destecs.ide.debug.launching.ui.aca;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.launching.ui.IUpdatableTab;
import org.destecs.ide.debug.launching.ui.Launch20simUtility;
import org.destecs.ide.debug.launching.ui.internal.Clp20simSettingsControl;
import org.destecs.ide.debug.launching.ui.internal.SettingItem;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.progress.UIJob;

public class Clp20simSettingsTab extends AbstractAcaTab implements IUpdatableTab{

	
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
				settingsControl.populateControl(settingItems, tab);
			

				final UIJob refreshTables = new UIJob("Refresh Tables Job")
				{

					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						settingsControl.refreshInputAndExpand();
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
	
	private final Set<SettingItem> settingItems = new HashSet<SettingItem>();
	//private SettingTreeNode settingsRootNode;
	private IUpdatableTab tab;
	//private Group optionsGroup = null;
	//private TreeViewer settingsTreeViewer;
	private Button populateButton;
	private Clp20simSettingsControl settingsControl = null;
	
	
	public void createControl(Composite parent) {
		this.tab = this;
		
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout gl = new GridLayout(1, true);

		comp.setLayout(gl);
		comp.setFont(parent.getFont());

		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createSettingsTable(comp);
		createPopulateButton(comp);
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
	
	private void populateButtonPress()
	{
		populateButton.setEnabled(false);
		IProject project = getActiveProject();
		ILaunchConfiguration baseConfig = getBaseConfiguration();
		String ctPath = null;
		String remoteBase = null;
		boolean useRemote = false;
		String ctUrlFromConfig = null;
		try {
			ctPath = baseConfig.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_MODEL_PATH, "");
			ctUrlFromConfig = baseConfig.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_CT_ENDPOINT, "");
			useRemote = baseConfig.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_USE_REMOTE_CT_SIMULATOR, false);
			remoteBase = baseConfig.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_REMOTE_PROJECT_BASE, "");
			if(ctUrlFromConfig != null && ctUrlFromConfig.equals(""))
			{
				ctUrlFromConfig = IDebugConstants.DEFAULT_CT_ENDPOINT;
			}

			String ctbase = null;
			if (project == null)
			{
				return; // new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID,
						// "Project is not set");

			}
			
			
			if(useRemote)
			{
				ctbase = remoteBase+"\\"+ctPath;
			}else
			{
				ctbase = getFileFromPath(project, ctPath).getAbsolutePath();
			}

			final String ctFile = ctbase;
			final String ctUrl = ctUrlFromConfig;
			PopulatorJob populator = new PopulatorJob(ctFile, ctUrl,useRemote);

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
			
		} catch (CoreException e) {
			populateButton.setEnabled(true);
			e.printStackTrace();
		}
		
		
		
	}

	public void createSettingsTable(Composite comp)
	{
		settingsControl = new Clp20simSettingsControl(true);
		settingsControl.createSettingsTable(comp);
		
		
	}
	
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		settingsControl.setDefaults(configuration);		
	}
	

	public void initializeFrom(ILaunchConfiguration configuration) {
		
		settingsControl.initializeFrom(configuration);
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		settingsControl.performApply(configuration);
	}

	public String getName() {
		return "CT Settings";
	}

	public void updateTab() {
		this.updateLaunchConfigurationDialog();
		
	}

	
}
