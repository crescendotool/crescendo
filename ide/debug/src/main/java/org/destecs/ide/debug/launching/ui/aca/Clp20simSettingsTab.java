package org.destecs.ide.debug.launching.ui.aca;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.launching.ui.Clp20simTab;
import org.destecs.ide.debug.launching.ui.Clp20simTab.SettingItem;
import org.destecs.ide.debug.launching.ui.IUpdatableTab;
import org.destecs.ide.debug.launching.ui.Launch20simUtility;
import org.destecs.ide.debug.launching.ui.SettingTreeNode;
import org.destecs.ide.debug.launching.ui.SettingsTreeContentProvider;
import org.destecs.ide.debug.launching.ui.SettingsTreeLabelProvider;
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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
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
				
				/*
				 * Querying 20sim settings
				 */
				settingItems.clear();
				/*
				 * Querying multiple implementations - transforming to settings
				 */
				List<Map<String, Object>> implementations = protocol.queryImplementations();
				for (Map<String, Object> map : implementations) {
					
					String name = (String) map.get("name");
					String value = (String) map.get("implementation");
					
					Object[] enumerations = (Object[]) map.get("implementations");
					List<String> enumerationsVector = new Vector<String>();
						
					for (Object object : enumerations) {
						if(object instanceof String)
						{
							enumerationsVector.add((String) object);
						}
					}
					
					
					SettingItem settingItem = new SettingItem(IDebugConstants.IMPLEMENTATION_PREFIX + name, value, enumerationsVector, "string", new HashMap<String, String>());
					settingItems.add(settingItem);
				}
				
				List<Map<String, Object>> getst = protocol.querySettings(new Vector<String>(Arrays.asList(new String[] {})));
				for (Map<String, Object> elem : getst)
				{
					Object[] enumerations = (Object[]) elem.get("enumerations");
					List<String> enumerationsVector = new Vector<String>();
						
					for (Object object : enumerations) {
						if(object instanceof String)
						{
							enumerationsVector.add((String) object);
						}
					}
					
					Object[] properties = (Object[]) elem.get("properties");
					
					HashMap<String, String> propertiesMap = new HashMap<String, String>();
					
					for (Object object : properties) {
						if(object instanceof HashMap)
						{
							@SuppressWarnings("unchecked")
							HashMap<String, Object> singlePropertyMap = (HashMap<String, Object>) object;
							String key = (String) singlePropertyMap.get("key");
							String value = (String) singlePropertyMap.get("value");
							propertiesMap.put(key, value);
						}
					}
					
					SettingItem item = new SettingItem(elem.get("key").toString(), elem.get("value").toString(), enumerationsVector  , elem.get("type").toString(),propertiesMap);
					settingItems.add(item);
				}

				settingsRootNode = SettingTreeNode.createSettingsTree(settingItems,settingsRootNode,tab);

			

				final UIJob refreshTables = new UIJob("Refresh Tables Job")
				{

					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						if (optionsGroup != null)
						{
							for (Control map : optionsGroup.getChildren())
							{
								map.dispose();
							}
							optionsGroup.layout();
						}

						if (settingsTreeViewer != null)
						{
							settingsTreeViewer.setInput(settingsRootNode);
							settingsTreeViewer.refresh();
							settingsTreeViewer.expandToLevel(2);
						}
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
	
	private final Set<SettingItem> settingItems = new HashSet<Clp20simTab.SettingItem>();
	private SettingTreeNode settingsRootNode;
	private IUpdatableTab tab;
	private Group optionsGroup = null;
	private TreeViewer settingsTreeViewer;
	private Button populateButton;
	
	
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
		Group group = new Group(comp, SWT.NONE);
//		group.setText("Settings");
		group.setLayout(new GridLayout(2, true));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		group.setLayoutData(gd);
		
		Group settingsGroup = new Group(group, SWT.NONE);
		settingsGroup.setText("Settings");
		settingsGroup.setLayout(new GridLayout(1, true));
		settingsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		settingsTreeViewer = new TreeViewer(settingsGroup, SWT.BORDER);
		settingsTreeViewer.setContentProvider(new SettingsTreeContentProvider());
		settingsTreeViewer.setLabelProvider(new SettingsTreeLabelProvider());
		settingsTreeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		optionsGroup = new Group(group, SWT.NONE);
		optionsGroup.setText("Options");
		optionsGroup.setLayout(new GridLayout(1, true));
		optionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		settingsTreeViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{

				if (event.getSelection().isEmpty())
				{
					return;
				}

				if (event.getSelection() instanceof IStructuredSelection)
				{
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					Object selected = selection.getFirstElement();
					if (selected instanceof SettingTreeNode)
					{
						SettingTreeNode node = (SettingTreeNode) selected;
						node.drawInAca(optionsGroup);
					}
				}
			}
		});

	}
	
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IDebugConstants.DESTECS_ACA_20SIM_IMPLEMENTATIONS, "");
		
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String acaImplementations = configuration.getAttribute(IDebugConstants.DESTECS_ACA_20SIM_IMPLEMENTATIONS,"");
			
			HashSet<String[]> settingsSet = new HashSet<String[]>();
			String[] splitSettings = acaImplementations.split(";");
			
			for (String setting : splitSettings) {
				String[] splitSetting = setting.split("=");
				if(splitSetting.length == 2)
				{
					splitSetting[0] = IDebugConstants.IMPLEMENTATION_PREFIX + splitSetting[0];
					settingsSet.add(splitSetting);
				}
			}
			
			settingsRootNode = SettingTreeNode.createAcaSettingsTreeFromConfiguration(settingsSet);
			settingsTreeViewer.setInput(settingsRootNode);
			settingsTreeViewer.refresh();
			settingsTreeViewer.expandToLevel(2);
		} catch (CoreException e) {
			DestecsDebugPlugin.logError("Failed to initialize Clp20sim settings tab", e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IDebugConstants.DESTECS_ACA_20SIM_IMPLEMENTATIONS, settingsRootNode.toImplementationAcaString());
		
	}

	public String getName() {
		return "CT Settings";
	}

	public void updateTab() {
		this.updateLaunchConfigurationDialog();
		
	}

	
}
