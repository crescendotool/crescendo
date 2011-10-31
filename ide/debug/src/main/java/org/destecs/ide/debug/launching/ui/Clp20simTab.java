package org.destecs.ide.debug.launching.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.LogManager;

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
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.progress.UIJob;

public class Clp20simTab extends AbstractLaunchConfigurationTab {
	
	public class PopulatorJob extends Job{

		private File ctFile;
		private String ctUrl;
		
		public PopulatorJob(File ctFile,String ctUrl) {
			super("20-sim table Populator");
			this.ctFile = ctFile;
			this.ctUrl = ctUrl;
			
			
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {

			try {
				System.out.println("Running Populator");
				Clp20SimProgramLauncher clp20sim = new Clp20SimProgramLauncher(
						ctFile);
				Process p = clp20sim.launch();

				ProxyICoSimProtocol protocol = Clp20SimUtility
						.connect(new URL(ctUrl));
				
				protocol.load(ctFile.getAbsolutePath());
				List<Map<String, Object>> getst = protocol
						.queryToolSettings();
				settingItems.clear();
				for (Map<String, Object> elem : getst) {
					SettingItem item = new SettingItem(elem.get(
							"key").toString(), elem.get("value")
							.toString(), new Vector<String>());
					settingItems.add(item);
				}
				
				logItems.clear();
				List<Map<String, Object>> getLog = protocol.queryVariables();
				for (Map<String, Object> elem : getLog) {
					LogItem item = new LogItem(elem.get("name").toString());
					logItems.add(item);
				}
				
				
				
				final UIJob refreshTables = new UIJob("Refresh Tables Job") {

					@Override
					public IStatus runInUIThread(
							IProgressMonitor monitor) {
						//b.setEnabled(true);
						
						settingsViewer.refresh();
						logViewer.refresh();
						
						reSelectVariables();
						logViewer.getTable().redraw();
						
						return new Status(IStatus.OK,
								DestecsDebugPlugin.PLUGIN_ID,
								"Refreshed Tables Job");
					}
				};
				refreshTables.schedule();

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SimulationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			return new Status(IStatus.OK,
					DestecsDebugPlugin.PLUGIN_ID, "Populated ok");
		}
		
	}
	
	
	public static class LogVariablesSelectionManager{
		
		Set<String> selectedVariables = new HashSet<String>();
		
		public void selectionChanged(String name){
			if(selectedVariables.contains(name))
			{
				selectedVariables.remove(name);
			}
			else
			{
				selectedVariables.add(name);
			}
		}
		
		public String getConfigValue()
		{
			StringBuilder sb = new StringBuilder();
			
			
			
			for (String elem : selectedVariables) {
				sb.append(elem);
				sb.append(",");
			}
			
			return sb.length() > 0 ? sb.substring(0, sb.length()-1) : "";
		}

		public void parseConfigValue(String attribute) {
			String[] variables = attribute.split(",");
			
			for (String name : variables) {
				if(!name.equals(""))
				{
					selectedVariables.add(name);
				}
				
			}
			
		}
	}
	
	
	public static class LogItem {
		
		public String name;
		public List<Integer> size;
		
		public LogItem(String name) {
			this.name = name;
		}
	
		@Override
		public String toString() {
			return name;
		}
	}
	
	public static class SettingItem {
		enum ValueType {
			Bool, Real, RealPositive, Enum
		}

		public final String key;
		public String value;
		public final List<String> values = new Vector<String>();
		public final ValueType type;

		public SettingItem(String key, String value, List<String> values) {
			this.key = key;
			this.value = value;
			this.values.addAll(values);
			// calculateType();
			this.type = ValueType.Bool;
		}

		@Override
		public String toString() {
			return getShortName() + " = " + this.value + " possible values: "
					+ values;
		}

		public String getShortName() {
			return key.substring(16);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SettingItem) {
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
	
	
	
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		comp.setLayout(new GridLayout());
		comp.setFont(parent.getFont());
		
		createTable(comp);
		createLogTable(comp);
		createPopulateButton(comp);
			
	}

	private void createLogTable(Composite comp) {
		Group group = new Group(comp, SWT.NONE);
		group.setText("Log");
		GridData gd = new GridData(GridData.FILL_BOTH);

		group.setLayoutData(gd);
		group.setLayout(new GridLayout(1, true));
		
		logViewer = new TableViewer(group,SWT.FULL_SELECTION |SWT.FILL | SWT.CHECK| SWT.BORDER);
		
		final Table table = logViewer.getTable();
		
		table.setHeaderVisible(true);
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText("Variable Name");
		column.setWidth(500);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
//		data.heightHint = 200;
		table.setLayoutData(data);
		
		
		logViewer.setContentProvider(new IStructuredContentProvider() {

			public void dispose() {
				// TODO Auto-generated method stub

			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// TODO Auto-generated method stub

			}

			public Object[] getElements(Object inputElement) {
				return logItems.toArray();
			}

		});
		
		logViewer.setLabelProvider(new ITableLabelProvider() {
			
			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}
			
			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}
			
			public void dispose() {
				// TODO Auto-generated method stub
				
			}
			
			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}
			
			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof LogItem) {
					if (columnIndex == 0) {
						return ((LogItem) element).toString();
					} 
				}
				return null;
			}
			
			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		logViewer.getTable().addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK)
				{
					if(event.item.getData() instanceof LogItem)
					{
						LogItem item = (LogItem) event.item.getData();
						logManager.selectionChanged(item.name);
						updateLaunchConfigurationDialog();
					}
					
				}
				
			}
		});
		
		logViewer.setInput(logItems);
	}

	public void createTable(Composite comp) {

		Group group = new Group(comp, SWT.NONE);
		group.setText("Settings");
		GridData gd = new GridData(GridData.FILL_BOTH);

		group.setLayoutData(gd);
		group.setLayout(new GridLayout(1, true));
		
		settingsViewer = new TableViewer(group,SWT.FULL_SELECTION | SWT.FILL);// SWT.FULL_SELECTION | SWT.FILL	| SWT.H_SCROLL | SWT.V_SCROLL);

		final Table table = settingsViewer.getTable();
		
		table.setHeaderVisible(true);
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText("Settings");
		column.setWidth(600);

		column = new TableColumn(table, SWT.NONE);
		column.setText("Value");
		column.setWidth(100);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
//		data.heightHint = 200;
		table.setLayoutData(data);

		final TableEditor editor = new TableEditor(table);
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();

				// Identify the selected row
				TableItem item = (TableItem) e.item;
				if (item == null)
					return;

				// The control that will be the editor must be a child of the
				// Table
				Text newEditor = new Text(table, SWT.NONE);
				newEditor.setText(item.getText(1));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						Text text = (Text) editor.getEditor();
						editor.getItem().setText(1, text.getText());
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, 1);
			}
		});

		settingsViewer.setContentProvider(new IStructuredContentProvider() {

			public void dispose() {
				// TODO Auto-generated method stub

			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				// TODO Auto-generated method stub

			}

			public Object[] getElements(Object inputElement) {
				return settingItems.toArray();
			}

		});

		settingsViewer.setLabelProvider(new ITableLabelProvider() {

			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			public void dispose() {
				// TODO Auto-generated method stub

			}

			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub

			}

			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (element instanceof SettingItem) {
					if (columnIndex == 0) {
						return ((SettingItem) element).getShortName();
					} else if (columnIndex == 1) {
						return ((SettingItem) element).value;
					}
				}
				return null;
			}

		});

		settingsViewer.setInput(settingItems);
	}

	private File getFileFromPath(IProject project, String path) {

		IResource r = project.findMember(new Path(path));

		if (r != null && !r.equals(project)) {
			return r.getLocation().toFile();
		}
		return null;
	}

	private void populateButtonPress(){
		populateButton.setEnabled(false);
		IProject project = getProject();
		
		if (project == null) {
			return; //new Status(IStatus.ERROR, DestecsDebugPlugin.PLUGIN_ID,  "Project is not set");

		}

		final File ctFile = getFileFromPath(project, getCtPath());
		final String ctUrl = getCtEndpoint();
		
		PopulatorJob populator = new PopulatorJob(ctFile,ctUrl);
		
		final UIJob changeButton = new UIJob("Enable populate button") {

			@Override
			public IStatus runInUIThread(
					IProgressMonitor monitor) {
				populateButton.setEnabled(true);
				return new Status(IStatus.OK,
						DestecsDebugPlugin.PLUGIN_ID,
						"Enabled populate button");
			}
		};
		
		populator.addJobChangeListener(new IJobChangeListener() {
			
			public void sleeping(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			public void scheduled(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			public void running(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			public void done(IJobChangeEvent event) {
				changeButton.schedule();
				
			}
			
			public void awake(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}
			
			public void aboutToRun(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		populator.schedule();
	}
	
	
	private void createPopulateButton(Composite comp) {
		populateButton = createPushButton(comp, "Populate...", null);

		populateButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				populateButtonPress();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub

	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		
		try {
			logManager.parseConfigValue(configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES, ""));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		UIJob uij = new UIJob("initialize") {
//			
//			@Override
//			public IStatus runInUIThread(IProgressMonitor monitor) {
//				populateButtonPress();
//				//reSelectVariables();
//	
//				return new Status(IStatus.OK, DestecsDebugPlugin.PLUGIN_ID, "ok");
//			}
//		};
//		uij.schedule();
		
		
		
	}

	private void reSelectVariables() {
		for (TableItem elem : logViewer.getTable().getItems()) {
			
			if(elem.getData() instanceof LogItem){
				LogItem logItem = (LogItem) elem.getData();
				if(logManager.selectedVariables.contains(logItem.name)){
					elem.setChecked(true);
				}	
			}
			
		}
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		System.out.println("Performing apply");
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES, logManager.getConfigValue());

	}

	public String getName() {

		return "20-sim options";
	}

	public IProject getProject() {
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog()
				.getTabs()) {
			if (tab instanceof CoSimLaunchConfigurationTab) {
				return ((CoSimLaunchConfigurationTab) tab).getProject();
			}
		}
		return null;
	}

	private String getCtPath() {
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog()
				.getTabs()) {
			if (tab instanceof CoSimLaunchConfigurationTab) {
				return ((CoSimLaunchConfigurationTab) tab).getCtPath();
			}
		}
		return null;
	}

	private String getCtEndpoint() {
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog()
				.getTabs()) {
			if (tab instanceof DevelopLaunchConfigurationTab) {
				return ((DevelopLaunchConfigurationTab) tab).getCtUrl();
			}
		}
		return null;
	}
	
	
	
}
