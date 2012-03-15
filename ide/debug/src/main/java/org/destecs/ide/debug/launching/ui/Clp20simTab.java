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
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.destecs.core.simulationengine.Clp20SimUtility;
import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.simeng.internal.core.Clp20SimProgramLauncher;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.progress.UIJob;

public class Clp20simTab extends AbstractLaunchConfigurationTab
{
	static class ClpSettingsLabelProvider extends LabelProvider implements
			ITableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof SettingItem)
			{
				if (columnIndex == 0)
				{
					return ((SettingItem) element).getShortName();
				} else if (columnIndex == 1)
				{
					return ((SettingItem) element).value;
				}
			}
			return null;
		}

	}

	public class PopulatorJob extends Job
	{

		
		private File ctFile;
		private String ctUrl;

		public PopulatorJob(File ctFile, String ctUrl)
		{
			super("20-sim table Populator");
			this.ctFile = ctFile;
			this.ctUrl = ctUrl;

		}

		@Override
		protected IStatus run(IProgressMonitor monitor)
		{

			try
			{
				/*
				 * Connecting to 20sim
				 */
				Clp20SimProgramLauncher clp20sim = new Clp20SimProgramLauncher(ctFile);
				clp20sim.launch();
				ProxyICoSimProtocol protocol = Clp20SimUtility.connect(new URL(ctUrl));
				protocol.load(ctFile.getAbsolutePath());
				
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
					
					
					SettingItem settingItem = new SettingItem(IMPLEMENTATION_PREFIX + name, value, enumerationsVector, "string", new HashMap<String, String>());
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

				logItems.clear();
				List<Map<String, Object>> getLog = protocol.queryVariables();
				for (Map<String, Object> elem : getLog)
				{
					LogItem item = new LogItem(elem.get("name").toString());
					
					logItems.add(item);
				}

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

						if (settingsViewer != null)
						{
							settingsViewer.refresh();
						}

						if (logViewer != null)
						{
							logViewer.refresh();
						}

						reSelectVariables();
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

	public static class LogVariablesSelectionManager
	{

		Set<String> selectedVariables = new HashSet<String>();

		public void selectionChanged(String name)
		{
			if (selectedVariables.contains(name))
			{
				selectedVariables.remove(name);
			} else
			{
				selectedVariables.add(name);
			}
		}

		public String getConfigValue()
		{
			StringBuilder sb = new StringBuilder();

			for (String elem : selectedVariables)
			{
				sb.append(elem);
				sb.append(",");
			}

			sb.append("time");
			
			return sb.toString();
		}
		
		
		public void addSelectedVariable(String name)
		{
			selectedVariables.add(name);
		}
	}

	public static class LogItem
	{

		public String name;
		public List<Integer> size;

		public LogItem(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	public static class SettingItem
	{
		enum ValueType
		{
			Bool, Real, RealPositive, Enum, String, Unknown, Double
		}

		public final String key;
		public String value;
		public final List<String> enumerations = new Vector<String>();
		public final ValueType type;
		public final HashMap<String, String> propertiesMap;

		public SettingItem(String key, String value, List<String> values,
				String type, HashMap<String, String> propertiesMap)
		{
			this.key = key;
			this.value = value;
			this.enumerations.addAll(values);
			this.type = convertType(type);
			this.propertiesMap = propertiesMap;

		}

		private ValueType convertType(String type)
		{
			if (type.equals("string"))
			{
				return ValueType.String;
			}
			if (type.equals("boolean"))
			{
				return ValueType.Bool;
			}
			if (type.equals("double"))
			{
				return ValueType.Double;
			}

			return ValueType.Unknown;
		}

		@Override
		public String toString()
		{
			return getShortName() + " = " + this.value + " possible values: "
					+ enumerations;
		}

		public String getShortName()
		{
			return key.substring(16);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof SettingItem)
			{
				return this.key.equals(((SettingItem) obj).key);
			}
			return super.equals(obj);
		}
	}

	private LogVariablesSelectionManager logManager = new LogVariablesSelectionManager();
	private Button populateButton;

	private final Set<SettingItem> settingItems = new HashSet<Clp20simTab.SettingItem>();
	private TableViewer settingsViewer;

	private final Set<LogItem> logItems = new HashSet<LogItem>();
	private TableViewer logViewer;
	private TreeViewer settingsTreeViewer = null;
	//private SettingsTreeContent settingsContent = new SettingsTreeContent();
	private SettingTreeNode settingsRootNode;
	private Group optionsGroup;
	private Clp20simTab tab;
	
	static final String IMPLEMENTATION_PREFIX = "model.implementations.";

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

	private void createLogTable(Composite comp)
	{
		Group group = new Group(comp, SWT.NONE);
		group.setText("Log");
		group.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		gd.minimumHeight = 100;
		group.setLayoutData(gd);

		logViewer = new TableViewer(group, SWT.FULL_SELECTION | SWT.FILL
				| SWT.CHECK);

		// GridData gd = new GridData(GridData.FILL_BOTH);
		// gd.heightHint = 100;
		// gd.minimumHeight = 100;
		logViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		final Table table = logViewer.getTable();

		table.setHeaderVisible(true);
		logViewer.setSorter(new Clp20simLogViewerSorter());

		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText("Variable Name");
		column.setWidth(500);

		GridData data = new GridData(GridData.FILL_BOTH);
		// data.heightHint = 10;
		table.setLayoutData(data);
		logViewer.setContentProvider(new ArrayContentProvider());
		logViewer.setLabelProvider(new LabelProvider());

		logViewer.getTable().addListener(SWT.Selection, new Listener()
		{

			public void handleEvent(Event event)
			{
				try
				{
					if (event.detail == SWT.CHECK)
					{
						if (event.item.getData() instanceof LogItem)
						{
							LogItem item = (LogItem) event.item.getData();
							logManager.selectionChanged(item.name);
							updateLaunchConfigurationDialog();
						}

					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		logViewer.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if(element instanceof LogItem)
				{
					LogItem logItem = (LogItem) element;
					if(logItem.name.equals("time"))
					{
						return false;
					}
				}
				return true;
			}
		});
		
		logViewer.setInput(logItems);
	}

	public void createSettingsTable(Composite comp)
	{
		Group group = new Group(comp, SWT.NONE);
		group.setText("Settings");
		group.setLayout(new GridLayout(2, true));
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		group.setLayoutData(gd);

		settingsTreeViewer = new TreeViewer(group, SWT.BORDER);
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
						node.drawIn(optionsGroup);
					}
				}
			}
		});

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

		final File ctFile = getFileFromPath(project, getCtPath());
		final String ctUrl = getCtEndpoint();

		PopulatorJob populator = new PopulatorJob(ctFile, ctUrl);

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
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES, "");
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS, "");
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_IMPLEMENTATIONS, "");
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		
		/* 
		 * Initializing the variables to log table
		 */
		try
		{
			String logVariables = configuration.getAttribute(
					IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES,
					"");
			String[] variables = logVariables.split(",");

			logItems.clear();
			
			for (String name : variables)
			{
				if (!name.equals(""))
				{
					logItems.add(new LogItem(name));
					logManager.addSelectedVariable(name);
				}

			}
			logViewer.refresh();
			reSelectVariables();
			
		} catch (CoreException e)
		{
			e.printStackTrace();
		}

		/*
		 * Initializing the settings
		 */
		try {
			
			/*
			 * getting the settings
			 */
			String settings = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS, "");
			String[] splitSettings = settings.split(";");
			
			Set<String[]> settingsSet = new HashSet<String[]>();
			
			for (String setting : splitSettings) {
				String[] splitSetting = setting.split("=");
				if(splitSetting.length == 2)
				{
					settingsSet.add(splitSetting);
				}
			}
			
			/*
			 * 	getting the implementations
			 */
			
			settings = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_IMPLEMENTATIONS, "");
			splitSettings = settings.split(";");
			
			for (String setting : splitSettings) {
				String[] splitSetting = setting.split("=");
				if(splitSetting.length == 2)
				{
					splitSetting[0] = IMPLEMENTATION_PREFIX + splitSetting[0];
					settingsSet.add(splitSetting);
				}
			}
			
			settingsRootNode = SettingTreeNode.createSettingsTreeFromConfiguration(settingsSet);
			settingsTreeViewer.setInput(settingsRootNode);
			settingsTreeViewer.refresh();
			settingsTreeViewer.expandToLevel(2);
			
		} catch (CoreException e) {
			DestecsDebugPlugin.logWarning("Faild to initialize Clp20SimTab with log variables", e);
		}
		
		
	}

	private void reSelectVariables()
	{
		for (TableItem elem : logViewer.getTable().getItems())
		{

			if (elem.getData() instanceof LogItem)
			{
				LogItem logItem = (LogItem) elem.getData();
				if (logManager.selectedVariables.contains(logItem.name))
				{
					elem.setChecked(true);
				}
			}

		}

		deleteUnexistentVariables();

	}

	private void deleteUnexistentVariables()
	{
		Set<String> varsToRemove = new HashSet<String>();

		for (String var : logManager.selectedVariables)
		{
			boolean exists = false;

			for (TableItem elem : logViewer.getTable().getItems())
			{
				if (elem.getData() instanceof LogItem)
				{
					LogItem logItem = (LogItem) elem.getData();
					if (logItem.name.equals(var))
					{
						exists = true;
						break;
					}
				}
			}

			if (!exists)
			{
				varsToRemove.add(var);
			}
		}

		logManager.selectedVariables.removeAll(varsToRemove);
		if (!varsToRemove.isEmpty())
		{
			updateLaunchConfigurationDialog();
		}

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES, logManager.getConfigValue());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS, getSettingsString());
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_IMPLEMENTATIONS, getImplementationsString());
		
	}

	private String getImplementationsString() {

		Object rootSettingsNode = settingsTreeViewer.getInput();
		
		if(rootSettingsNode instanceof SettingTreeNode)
		{
			String result = ((SettingTreeNode) rootSettingsNode).toImplementationString();
			return result;
		}
		
		return "";
	}

	private String getSettingsString() {
		
		Object rootSettingsNode = settingsTreeViewer.getInput();
		
		if(rootSettingsNode instanceof SettingTreeNode)
		{
			String result = ((SettingTreeNode) rootSettingsNode).toSettingsString();
			return result;
		}
		
		return "";
		
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
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof CoSimLaunchConfigurationTab)
			{
				return ((CoSimLaunchConfigurationTab) tab).getCtPath();
			}
		}
		return null;
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

	public void updateTab() {
		updateLaunchConfigurationDialog();
		
	}

}
