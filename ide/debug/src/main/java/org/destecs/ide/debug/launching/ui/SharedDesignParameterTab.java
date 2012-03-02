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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.destecs.core.contract.ArrayVariable;
import org.destecs.core.contract.Contract;
import org.destecs.core.contract.IVariable;
import org.destecs.core.contract.MatrixVariable;
import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.parsers.SdpParserWrapper;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class SharedDesignParameterTab extends AbstractLaunchConfigurationTab {
	class WidgetListener implements ModifyListener, SelectionListener {

		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			updateLaunchConfigurationDialog();
		}
	}

	private Table table;

	private WidgetListener fListener = new WidgetListener();
	private HashMap<String, Object> sdps;

	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		Button defaultsButton = createPushButton(comp,
				"Synchronize with contract", null);
		defaultsButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				synchronizeDefaults();
			}

			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		table = new Table(comp, SWT.FULL_SELECTION | SWT.VIRTUAL);// SWT.MULTI |
																	// SWT.BORDER
																	// |
																	// SWT.FULL_SELECTION);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);

		String[] titles = { "Name", "Value" };
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
			column.setWidth(100);
		}

		final TableEditor editor = new TableEditor(table);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		// editing the second column
		final int EDITABLECOLUMN = 1;

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
				newEditor.setText(item.getText(EDITABLECOLUMN));
				newEditor.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent me) {
						Text text = (Text) editor.getEditor();
						String s = text.getText();
						editor.getItem().setText(EDITABLECOLUMN, s);
						if (!isParseCorrect(s)) {

						}

						setDirty(true);
						updateLaunchConfigurationDialog();
					}

				});
				newEditor.addModifyListener(fListener);

				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, EDITABLECOLUMN);

			}
		});

		populate();
	}

	private boolean isParseCorrect(String s) {
		return true;
	}

	private void populate() {
		table.removeAll();
		if (sdps != null) {
			for (String p : sdps.keySet()) {
				TableItem item = new TableItem(table, SWT.NONE);
				item.setText(0, p);
				item.setText(1, sdps.get(p).toString());
			}
		}
		// table.redraw();

	}

	public String getName() {
		return "Shared Design Parameters";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		SdpParserWrapper parser = new SdpParserWrapper();

		String data;
		try {
			data = configuration.getAttribute(
					IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM,
					"");

			if (data != null) {
				sdps = parser.parse(new File("memory"), data);

				if (table != null) {
					populate();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String getId() {
		return "org.destecs.ide.debug.launching.ui.SharedDesignParameterTab";
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(
				IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM,
				getSdpsSyntax());
	}

	private String getSdpsSyntax() {
		StringBuilder sb = new StringBuilder();
		for (TableItem item : table.getItems()) {
			if (item.getText(0).trim().length() > 0
					&& item.getText(1).trim().length() > 0) {
				sb.append(item.getText(0) + ":=" + item.getText(1) + ";");
			}
		}
		return sb.toString();
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	public void setDefaults() {
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog()
				.getTabs()) {
			if (tab instanceof CoSimLaunchConfigurationTab) {
				CoSimLaunchConfigurationTab cosimLaunchTab = (CoSimLaunchConfigurationTab) tab;
				IProject project = cosimLaunchTab.getProject();
				if (project != null) {
					ContractParserWrapper parser = new ContractParserWrapper();
					IDestecsProject dproject = (IDestecsProject) project
							.getAdapter(IDestecsProject.class);

					Contract contract;
					try {
						File file = dproject.getContractFile().getLocation()
								.toFile();
						if (!file.exists()) {
							return;
						}
						contract = parser.parse(file);
						sdps = new HashMap<String, Object>();
						for (IVariable var : contract
								.getSharedDesignParameters()) {
							sdps.put(var.getName(), "0.0");
						}
						if (table != null) {
							populate();
						}
						return;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

	private void synchronizeDefaults() {
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog()
				.getTabs()) {
			if (tab instanceof CoSimLaunchConfigurationTab) {
				CoSimLaunchConfigurationTab cosimLaunchTab = (CoSimLaunchConfigurationTab) tab;
				IProject project = cosimLaunchTab.getProject();
				if (project != null) {
					ContractParserWrapper parser = new ContractParserWrapper();
					IDestecsProject dproject = (IDestecsProject) project
							.getAdapter(IDestecsProject.class);

					Contract contract;
					try {
						File file = dproject.getContractFile().getLocation()
								.toFile();
						if (!file.exists()) {
							return;
						}
						contract = parser.parse(file);

						sdps = new HashMap<String, Object>();
						if (contract == null) {
							return;
						}

						fillSdpTable(contract.getSharedDesignParameters());
						// for (IVariable var :
						// contract.getSharedDesignParameters())
						// {
						// String[] existing = getItemIfPresent(var.getName());
						// if (existing == null)
						// {
						// sdps.put(var.getName(), "0.0");
						// } else if (existing.length >= 2)
						// {
						// sdps.put(existing[0], existing[1]);
						// }
						// }
						// if (table != null)
						// {
						// populate();
						// }
						return;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void fillSdpTable(List<IVariable> sharedDesignParameters) {

		for (IVariable variable : sharedDesignParameters) {
			sdps.put(variable.getName(), variable);
		}

		populateNew();

	}

	private void populateNew() {
		table.removeAll();

		if (sdps == null) {
			return;
		}

		for (String sdpName : sdps.keySet()) {
			IVariable v = (IVariable) sdps.get(sdpName);
			putOnUITable(v);
			System.out.println(v);
		}

	}

	private void putOnUITable(IVariable v) {

		if (v instanceof ArrayVariable) {
			ArrayVariable arrayVariable = (ArrayVariable) v;
			List<Integer> dimensions = arrayVariable.getDimensions();
			if (dimensions.size() > 0) {
				if (dimensions.get(0) == 1) {
					if (dimensions.size() > 1) {

						for (int j = 1; j < dimensions.size(); j++) {
							for (int k = 0; k < dimensions.get(j); k++) {
								TableItem item = new TableItem(table, SWT.NONE);
								item.setText(arrayVariable.getName() + "["
										+ (k + 1) + "]");
							}
						}
					}
				}
			}
		} else if (v instanceof MatrixVariable) {

		} else {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setData(v);
			item.setText(0, v.getName());
			item.setText(1, "none");
		}

	}

	private String[] getItemIfPresent(String name) {
		for (TableItem item : table.getItems()) {
			if (item.getText(0).trim().equalsIgnoreCase(name.trim())) {
				return new String[] { item.getText(0), item.getText(1) };
			}
		}

		return null;
	}

}
