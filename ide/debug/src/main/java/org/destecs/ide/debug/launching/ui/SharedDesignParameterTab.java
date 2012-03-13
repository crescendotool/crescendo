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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.destecs.core.contract.ArrayVariable;
import org.destecs.core.contract.Contract;
import org.destecs.core.contract.IVariable;
import org.destecs.core.contract.MatrixVariable;
import org.destecs.core.contract.Variable.DataType;
import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.parsers.SdpParserWrapper;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.debug.DestecsDebugPlugin;
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

public class SharedDesignParameterTab extends AbstractLaunchConfigurationTab
{
	class WidgetListener implements ModifyListener, SelectionListener
	{

		public void modifyText(ModifyEvent e)
		{
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e)
		{
		}

		public void widgetSelected(SelectionEvent e)
		{
			updateLaunchConfigurationDialog();
		}
	}

	private Table table;

	private WidgetListener fListener = new WidgetListener();
	private HashMap<String, Object> sdps;
	private HashMap<String, List<Integer>> dimensions = new HashMap<String, List<Integer>>();

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		Button defaultsButton = createPushButton(comp, "Synchronize with contract", null);
		defaultsButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				synchronizeDefaults();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{

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
		for (int i = 0; i < titles.length; i++)
		{
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

		table.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
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
				newEditor.addModifyListener(new ModifyListener()
				{
					public void modifyText(ModifyEvent me)
					{
						Text text = (Text) editor.getEditor();
						String s = text.getText();
						editor.getItem().setText(EDITABLECOLUMN, s);
						if (!isParseCorrect(s))
						{

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
	}

	private boolean isParseCorrect(String s)
	{
		return true;
	}

	public String getName()
	{
		return "Shared Design Parameters";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		dimensions.clear();
		if (sdps != null)
		{
			sdps.clear();
		}
		SdpParserWrapper parser = new SdpParserWrapper();

		String data;
		try
		{
			data = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, "");

			if (data != null)
			{
				sdps = parser.parse(new File("memory"), data);

				if (sdps == null)
				{
					sdps = new HashMap<String, Object>();
				}
			}

			try
			{
				table.removeAll();
				table.redraw();
			} catch (Exception e)
			{

			}
			synchronizeDefaults();

		} catch (Exception e)
		{
			DestecsDebugPlugin.logError("Faild to initialize debug configuration with SDP", e);
		}

	}

	@Override
	public String getId()
	{
		return "org.destecs.ide.debug.launching.ui.SharedDesignParameterTab";
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{

		HashMap<String, Object> values = preProcessResult();

		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, getSdpsSyntax(values));
	}

	private String getSdpsSyntax(HashMap<String, Object> values)
	{
		StringBuilder sb = new StringBuilder();

		for (Entry<String, Object> entry : values.entrySet())
		{
			sb.append(entry.getKey().replace(" ", ""));
			sb.append(":=");
			sb.append(entry.getValue());
			sb.append(";");
		}

		return sb.toString();
	}

	private HashMap<String, Object> preProcessResult()
	{
		HashMap<String, Object> out = new HashMap<String, Object>();

		for (TableItem tItem : table.getItems())
		{
			String name = tItem.getText(0);
			if (name.contains("["))
			{
				String strippedName = name.substring(0, name.indexOf("["));

				if (dimensions.containsKey(strippedName))
				{
					strippedName = strippedName
							+ dimensions.get(strippedName).toString();
				}

				if (out.containsKey(strippedName))
				{
					@SuppressWarnings({ "unchecked", "rawtypes" })
					List<String> variable = (List) out.get(strippedName);
					variable.add(tItem.getText(1));

				} else
				{
					List<Object> variable = new ArrayList<Object>();
					variable.add(tItem.getText(1));
					out.put(strippedName, variable);
				}

			} else
			{
				out.put(name, tItem.getText(1));
			}
		}

		return out;
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, "");
	}

	private void synchronizeDefaults()
	{
		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof CoSimLaunchConfigurationTab)
			{
				CoSimLaunchConfigurationTab cosimLaunchTab = (CoSimLaunchConfigurationTab) tab;
				IProject project = cosimLaunchTab.getProject();
				if (project != null)
				{
					ContractParserWrapper parser = new ContractParserWrapper();
					IDestecsProject dproject = (IDestecsProject) project.getAdapter(IDestecsProject.class);

					Contract contract;
					try
					{
						if (dproject == null)
						{
							return;
						}
						File file = dproject.getContractFile().getLocation().toFile();
						if (!file.exists())
						{
							return;
						}
						contract = parser.parse(file);

						if (contract == null)
						{
							return;
						}

						fillSdpTable(contract.getSharedDesignParameters());
						return;
					} catch (IOException e)
					{
						DestecsDebugPlugin.logError("Faild to synchronize SDPs with contract", e);
					}
				}
			}
		}
	}

	private void fillSdpTable(List<IVariable> sharedDesignParameters)
	{
		dimensions.clear();
		table.removeAll();

		for (IVariable iVariable : sharedDesignParameters)
		{
			putOnUITable(iVariable);
			if (iVariable.getDataType() == DataType.array
					|| iVariable.getDataType() == DataType.matrix)
			{
				dimensions.put(iVariable.getName(), iVariable.getDimensions());
			}
		}
	}

	private void putOnUITable(IVariable v)
	{
		if (v instanceof ArrayVariable)
		{
			ArrayVariable arrayVariable = (ArrayVariable) v;
			List<Integer> dimensions = arrayVariable.getDimensions();
			if (dimensions.size() > 0)
			{
				if (dimensions.get(0) == 1)
				{
					if (dimensions.size() > 1)
					{
						for (int j = 1; j < dimensions.size(); j++)
						{
							for (int k = 0; k < dimensions.get(j); k++)
							{
								TableItem item = new TableItem(table, SWT.NONE);
								String itemName = arrayVariable.getName() + "["
										+ (k + 1) + "]";
								item.setText(0, itemName);
								String existing = getValueIfPresent(itemName);

								if (existing == null)
								{
									item.setText(1, "0.0");
								} else
								{
									item.setText(1, existing);
								}
								item.setData(arrayVariable);
							}
						}
					}
				}
			}
		} else if (v instanceof MatrixVariable)
		{
			MatrixVariable matrixVariable = (MatrixVariable) v;

			List<Integer> dimensions = matrixVariable.getDimensions();

			List<String> results = buildMatrixIndexes(dimensions);
			for (String res : results)
			{
				TableItem item = new TableItem(table, SWT.NONE);
				String itemName = matrixVariable.getName() + "[" + res + "]";
				item.setText(0, itemName);
				String existing = getValueIfPresent(itemName);

				if (existing == null)
				{
					item.setText(1, "0.0");
				} else
				{
					item.setText(1, existing);
				}
				item.setData(matrixVariable);

			}

		} else
		{
			TableItem item = new TableItem(table, SWT.NONE);
			item.setData(v);
			item.setText(0, v.getName());
			String existing = getValueIfPresent(v.getName());

			if (existing == null)
			{
				item.setText(1, "0.0");
			} else
			{
				item.setText(1, existing);
			}
		}

	}

	private List<String> buildMatrixIndexes(List<Integer> dimensions)
	{
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < dimensions.get(0); i++)
		{
			result.add(Integer.toString(i + 1));
		}

		for (int i = 1; i < dimensions.size(); i++)
		{
			List<String> tempResult = new ArrayList<String>();
			for (int j = 0; j < dimensions.get(i); j++)
			{
				for (String string : result)
				{
					tempResult.add(string + "," + (j + 1));
				}
			}
			result = tempResult;
		}

		Collections.sort(result);
		return result;
	}

	@SuppressWarnings("rawtypes")
	private String getValueIfPresent(String name)
	{
		if (name.contains("["))
		{
			String strippedName = name.substring(0, name.indexOf("["));

			if (containsLooseKey(sdps, strippedName))
			{

				String looseKey = getLooseKey(sdps, strippedName);

				String dimentions = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
				String[] splitDimentions = dimentions.split(",");

				if (splitDimentions.length == 1)
				{
					List o = (List) sdps.get(looseKey);
					return o.get(Integer.parseInt(splitDimentions[0]) - 1).toString();

				} else if (splitDimentions.length == 2)
				{

					String looseDimentions = looseKey.substring(looseKey.indexOf("[") + 1, looseKey.indexOf("]"));
					String[] splitLooseDimentions = looseDimentions.split(",");

					List o = (List) sdps.get(looseKey);
					int k = (Integer.parseInt(splitDimentions[0]) - 1)
							* (Integer.parseInt(splitLooseDimentions[1]))
							+ Integer.parseInt(splitDimentions[1]);

					if (k < o.size())
					{
						return o.get(k - 1).toString();
					} else
					{
						return null;
					}

				} else
				{
					return null;
				}

			} else
			{
				return null;
			}

		}

		if (sdps.containsKey(name))
		{
			return sdps.get(name).toString();
		}

		return null;
	}

	private boolean containsLooseKey(HashMap<String, Object> sdpsMap,
			String strippedName)
	{
		for (String key : sdpsMap.keySet())
		{
			if (key.contains("["))
			{
				if (key.substring(0, key.indexOf("[")).equals(strippedName))
				{
					return true;
				}

			}
		}
		return false;
	}

	private String getLooseKey(HashMap<String, Object> sdpsMap,
			String strippedName)
	{

		for (String key : sdpsMap.keySet())
		{
			if (key.contains("["))
			{
				if (key.substring(0, key.indexOf("[")).equals(strippedName))
				{
					return key;
				}

			}
		}
		return null;
	}

}
