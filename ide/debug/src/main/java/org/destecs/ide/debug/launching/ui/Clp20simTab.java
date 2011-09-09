package org.destecs.ide.debug.launching.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.destecs.core.simulationengine.Clp20SimUtility;
import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.simeng.internal.core.Clp20SimProgramLauncher;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;

public class Clp20simTab extends AbstractLaunchConfigurationTab {
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

	private final Set<SettingItem> settingItems = new HashSet<Clp20simTab.SettingItem>();
	private TableViewer viewer;

	public void createControl(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);

		setControl(comp);
		
		
		comp.setLayout(new GridLayout());
		comp.setFont(parent.getFont());

		createTable(comp);
		createPopulateButton(comp);

	}

	public void createTable(Composite comp) {

		viewer = new TableViewer(comp,SWT.FULL_SELECTION | SWT.VIRTUAL|SWT.FILL);// SWT.FULL_SELECTION | SWT.FILL	| SWT.H_SCROLL | SWT.V_SCROLL);

		final Table table = viewer.getTable();
		
		table.setHeaderVisible(true);
		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText("Settings");
		column.setWidth(600);

		column = new TableColumn(table, SWT.NONE);
		column.setText("Value");
		column.setWidth(100);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
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

		viewer.setContentProvider(new IStructuredContentProvider() {

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

		viewer.setLabelProvider(new ITableLabelProvider() {

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

		viewer.setInput(settingItems);
	}

	private File getFileFromPath(IProject project, String path) {

		IResource r = project.findMember(new Path(path));

		if (r != null && !r.equals(project)) {
			return r.getLocation().toFile();
		}
		return null;
	}

	private void createPopulateButton(Composite comp) {
		final Button b = createPushButton(comp, "Populate...", null);

		b.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {

				b.setEnabled(false);

				IProject project = getProject();
				if (project == null) {
					return;// /new Status(IStatus.ERROR,
							// DestecsDebugPlugin.PLUGIN_ID,
							// "Project is not set");

				}

				final File ctFile = getFileFromPath(project, getCtPath());
				final String ctUrl = getCtEndpoint();
				Job populator = new Job("20-sim table Populator") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {

						try {
							Clp20SimProgramLauncher clp20sim = new Clp20SimProgramLauncher(
									ctFile);
							Process p = clp20sim.launch();

							ProxyICoSimProtocol protocol = Clp20SimUtility
									.connect(new URL(ctUrl));
							List<Map<String, Object>> getst = protocol
									.queryToolSettings();

							for (Map<String, Object> elem : getst) {
								SettingItem item = new SettingItem(elem.get(
										"key").toString(), elem.get("value")
										.toString(), new Vector<String>());
								settingItems.add(item);
							}

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

						UIJob changeButton = new UIJob("Enable populate button") {

							@Override
							public IStatus runInUIThread(
									IProgressMonitor monitor) {
								b.setEnabled(true);
								viewer.refresh();
								return new Status(IStatus.OK,
										DestecsDebugPlugin.PLUGIN_ID,
										"Enabled populate button");
							}
						};
						changeButton.schedule();
						return new Status(IStatus.OK,
								DestecsDebugPlugin.PLUGIN_ID, "Populated ok");
					}

				};
				populator.schedule();

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

	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub

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
