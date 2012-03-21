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
package org.destecs.ide.debug.launching.ui.aca;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.destecs.core.parsers.SdpParserWrapper;
import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class SharedDesignParameterAcaTab extends AbstractLaunchConfigurationTab
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

	private Table incrementTable;
	private Table valueSetTable;

	private WidgetListener fListener = new WidgetListener();
	private final Set<String> sdps = new HashSet<String>();

	

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		createIncrementTable(comp);
		createValueSetTable(comp);
		
		createClearButton(comp);
		
	}

	private void createValueSetTable(Composite comp) {
		Group group = new Group(comp, SWT.FILL);
		group.setText("Value Set Sweep");
		group.setLayout(new GridLayout());
		
		valueSetTable = new Table(group, SWT.FULL_SELECTION | SWT.VIRTUAL);
		
		valueSetTable.setLinesVisible(true);
		valueSetTable.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		valueSetTable.setLayoutData(data);

		String[] titles = { "Name", "Values" };
		for (int i = 0; i < titles.length; i++)
		{
			TableColumn column = new TableColumn(valueSetTable, SWT.NONE);
			column.setText(titles[i]);
			column.setWidth(200);

		}

		
		addEditorToValueSetTable();
	}

	private void createClearButton(Composite comp) {
		Button defaultsButton = createPushButton(comp, "Clear All", null);
		defaultsButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				incrementTable.removeAll();
				valueSetTable.removeAll();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{

			}
		});
		
	}

	private void createIncrementTable(Composite comp) {
		
		Group group = new Group(comp, SWT.FILL);
		group.setText("Incremental Sweep");
		group.setLayout(new GridLayout());
		
		
		incrementTable = new Table(group, SWT.FULL_SELECTION | SWT.VIRTUAL);// SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);

		incrementTable.setLinesVisible(true);
		incrementTable.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		incrementTable.setLayoutData(data);

		String[] titles = { "Name", "From", "To", "Increment by" };
		for (int i = 0; i < titles.length; i++)
		{
			TableColumn column = new TableColumn(incrementTable, SWT.NONE);
			column.setText(titles[i]);
			column.setWidth(100);

		}

		addEditorToIncrementTable();

		
		
	}

	private void addEditorToIncrementTable()
	{
		// Create an editor object to use for text editing
		final TableEditor editor = new TableEditor(incrementTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Use a mouse listener, not a selection listener, since we're interested
		// in the selected column as well as row
		incrementTable.addMouseListener(new MouseAdapter()
		{
			public void mouseDown(MouseEvent event)
			{
				// Dispose any existing editor
				Control old = editor.getEditor();
				if (old != null)
					old.dispose();

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = incrementTable.getItem(pt);
				if (item != null)
				{
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = incrementTable.getColumnCount(); i < n; i++)
					{
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt))
						{
							// This is the selected column
							column = i;
							break;
						}
					}

					// Column 2 holds dropdowns
					if (column == 0)
					{
						// Create the dropdown and add data to it
						final CCombo combo = new CCombo(incrementTable, SWT.READ_ONLY);
						// for (int i = 0, n = sdps.keySet().size(); i < n; i++)
						// {
						// combo.add(sdps.keySet().toArray()[i].toString());
						// }
						for (String key : sdps)
						{
							if (getItemIfPresent(key) == null
									|| item.getText(column).equals(key))
							{
								combo.add(key);
							}
						}

						// Select the previously selected item from the cell
						combo.select(combo.indexOf(item.getText(column)));

						// Compute the width for the editor
						// Also, compute the column width, so that the dropdown fits
						editor.minimumWidth = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
						if (incrementTable.getColumn(column).getWidth() < editor.minimumWidth)
						{
							incrementTable.getColumn(column).setWidth(editor.minimumWidth);
						}
						// Set the focus on the dropdown and set into the editor
						combo.setFocus();
						editor.setEditor(combo, item, column);

						combo.addModifyListener(fListener);

						// Add a listener to set the selected item back into the cell
						final int col = column;
						combo.addSelectionListener(new SelectionAdapter()
						{
							public void widgetSelected(SelectionEvent event)
							{
								item.setText(col, combo.getText());
								// setDirty(true);
								// updateLaunchConfigurationDialog();

								// They selected an item; end the editing session
								combo.dispose();
							}
						});
					} else if (column > 0)
					{
						// Create the Text object for our editor
						final Text text = new Text(incrementTable, SWT.NONE);
						text.setForeground(item.getForeground());

						// Transfer any text from the cell to the Text control,
						// set the color to match this row, select the text,
						// and set focus to the control
						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();

						// Recalculate the minimum width for the editor
						editor.minimumWidth = text.getBounds().width;

						// Set the control into the editor
						editor.setEditor(text, item, column);

//						text.addModifyListener(fListener);

						// Add a handler to transfer the text back to the cell
						// any time it's modified
						final int col = column;
						text.addModifyListener(new ModifyListener()
						{
							public void modifyText(ModifyEvent event)
							{
								// Set the text of the editor's control back into the cell
								try
								{
									Double.parseDouble(text.getText());
									item.setText(col, text.getText());
									updateLaunchConfigurationDialog();
								} catch (Exception e)
								{
									setMessage("Specified value is not a double");
									item.setText(col, "0");
								}

								// setDirty(true);
								// updateLaunchConfigurationDialog();
							}
						});
					}
				} else if (incrementTable.getItemCount() < sdps.size())
				{
					// allow new items to be created until we reach the total sdp count
					new TableItem(incrementTable, SWT.NONE);

				}
				{

				}
			}
		});

	}

	private void addEditorToValueSetTable()
	{
		// Create an editor object to use for text editing
		final TableEditor editor = new TableEditor(valueSetTable);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Use a mouse listener, not a selection listener, since we're interested
		// in the selected column as well as row
		valueSetTable.addMouseListener(new MouseAdapter()
		{
			public void mouseDown(MouseEvent event)
			{
				// Dispose any existing editor
				Control old = editor.getEditor();
				if (old != null)
					old.dispose();

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = valueSetTable.getItem(pt);
				if (item != null)
				{
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = valueSetTable.getColumnCount(); i < n; i++)
					{
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt))
						{
							// This is the selected column
							column = i;
							break;
						}
					}

					// Column 2 holds dropdowns
					if (column == 0)
					{
						// Create the dropdown and add data to it
						final CCombo combo = new CCombo(valueSetTable, SWT.READ_ONLY);
						// for (int i = 0, n = sdps.keySet().size(); i < n; i++)
						// {
						// combo.add(sdps.keySet().toArray()[i].toString());
						// }
						for (String key : sdps)
						{
							if (getItemIfPresent(key) == null
									|| item.getText(column).equals(key))
							{
								combo.add(key);
							}
						}

						// Select the previously selected item from the cell
						combo.select(combo.indexOf(item.getText(column)));

						// Compute the width for the editor
						// Also, compute the column width, so that the dropdown fits
						editor.minimumWidth = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
						if (valueSetTable.getColumn(column).getWidth() < editor.minimumWidth)
						{
							valueSetTable.getColumn(column).setWidth(editor.minimumWidth);
						}
						// Set the focus on the dropdown and set into the editor
						combo.setFocus();
						editor.setEditor(combo, item, column);

//						combo.addModifyListener(fListener);

						// Add a listener to set the selected item back into the cell
						final int col = column;
						combo.addSelectionListener(new SelectionAdapter()
						{
							public void widgetSelected(SelectionEvent event)
							{
								item.setText(col, combo.getText());
								// setDirty(true);
								// updateLaunchConfigurationDialog();
								updateLaunchConfigurationDialog();
								// They selected an item; end the editing session
								combo.dispose();
							}
						});
					} else if (column > 0)
					{
						// Create the Text object for our editor
						final Text text = new Text(valueSetTable, SWT.NONE);
						text.setForeground(item.getForeground());

						// Transfer any text from the cell to the Text control,
						// set the color to match this row, select the text,
						// and set focus to the control
						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();

						// Recalculate the minimum width for the editor
						editor.minimumWidth = text.getBounds().width;

						// Set the control into the editor
						editor.setEditor(text, item, column);

//						text.addModifyListener(fListener);

						// Add a handler to transfer the text back to the cell
						// any time it's modified
						final int col = column;
						text.addModifyListener(new ModifyListener()
						{
							public void modifyText(ModifyEvent event)
							{
								// Set the text of the editor's control back into the cell
								
									
									boolean p = parseInputText(text.getText());
									
									System.out.println("parsing text:" + text.getText() + " parsed? " + p);
									if(!p)
									{
										setMessage("Specified value is not are not double numbers separated by semi-colon (;)");
										item.setText(col, "0");
									}
									else
									{
										setMessage(null);
										item.setText(col, text.getText());
									}
									
								

								// setDirty(true);
								// updateLaunchConfigurationDialog();
									updateLaunchConfigurationDialog();
							}

							private boolean parseInputText(String text) {
								
								String[] values = text.split(";");
								if(values.length == 0)
								{
									try
									{
										Double.parseDouble(text);
										return true;
									} catch (Exception e)
									{
										
										return false;
									}
								}
									
								for (String string : values) {
									try
									{
										Double.parseDouble(string);
									} catch (Exception e)
									{
										
										return false;
									}
								}
								
								return true;
							}
						});
					}
				} else if (valueSetTable.getItemCount() < sdps.size())
				{
					// allow new items to be created until we reach the total sdp count
					new TableItem(valueSetTable, SWT.NONE);

				}
				{

				}
			}
		});

	}
	
	
	public String getName()
	{
		return "SDPs Sweep";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		populateSdps();
		incrementTable.removeAll();
		valueSetTable.removeAll();
		try
		{
			String data = configuration.getAttribute(IDebugConstants.DESTECS_ACA_INCREMENTAL_SDPS, "");
			if (data != null && !data.isEmpty())
			{
				String[] items = data.split(",");
				for (String item : items)
				{
					String[] colls = item.split("\\|");
					if (colls.length == incrementTable.getColumnCount())
					{
						TableItem tableItem = new TableItem(incrementTable, SWT.NONE);
						tableItem.setText(colls);
					}
				}
			}
			
			data = configuration.getAttribute(IDebugConstants.DESTECS_ACA_VALUESET_SDPS, "");
			if (data != null && !data.isEmpty())
			{
				String[] items = data.split(",");
				for (String item : items)
				{
					String[] colls = item.split("\\|");
					if (colls.length == valueSetTable.getColumnCount())
					{
						TableItem tableItem = new TableItem(valueSetTable, SWT.NONE);
						tableItem.setText(colls);
					}
				}
			}
			
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Error in initialization of shared design parameter tab", e);
		}
		
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_ACA_INCREMENTAL_SDPS, getSdpsSyntax());
		configuration.setAttribute(IDebugConstants.DESTECS_ACA_VALUESET_SDPS, getValueSetSdps());
	}

	private String getValueSetSdps() 
	{
		StringBuilder sb = new StringBuilder();
		
		for (TableItem  item : valueSetTable.getItems()) 
		{
			sb.append(item.getText(0));
			sb.append("|");
			sb.append(item.getText(1));
			sb.append(",");
		}
		
		if (sb.toString().endsWith(","))
		{
			return sb.toString().substring(0, sb.toString().length() - 1);
		}
		else
		{
			return sb.toString();
		}
	}

	private String getSdpsSyntax()
	{
		StringBuilder sb = new StringBuilder();
		for (TableItem item : incrementTable.getItems())
		{
			for (int i = 0; i < incrementTable.getColumnCount(); i++)
			{
				sb.append(item.getText(i));
				if (i < incrementTable.getColumnCount())
				{
					sb.append("|");
				}
			}
			sb.append(",");
		}

		if (sb.toString().endsWith(","))
		{
			return sb.toString().substring(0, sb.toString().length() - 1);
		}
		return sb.toString();
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		// table.clearAll();
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);
		for (TableItem item : incrementTable.getItems())
		{
			for (int i = 0; i < incrementTable.getColumnCount(); i++)
			{
				if (i > 0 && !item.getText(0).isEmpty())
				{
					try
					{
						Double.parseDouble(item.getText(i));
					} catch (Exception e)
					{
						setErrorMessage("Value is not a double: "
								+ item.getText(i));
						return false;

					}
				}
			}
		}
		return true;
	}

	private void populateSdps()
	{

		for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
		{
			if (tab instanceof DseMainTab)
			{
				DseMainTab cosimLaunchTab = (DseMainTab) tab;
				ILaunchConfiguration baseConfig = cosimLaunchTab.getSelectedBaseConfig();
				if (baseConfig != null)
				{
					String data;
					try
					{
						data = baseConfig.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, "");
						SdpParserWrapper parser = new SdpParserWrapper();

						if (!data.isEmpty())
						{
							HashMap<String, Object> result = parser.parse(new File("memory"), data);
							if (result != null)
							{
								sdps.clear();
								sdps.addAll(result.keySet());
								return;
							}
						}
					} catch (CoreException e)
					{
						DestecsDebugPlugin.logError("Error while populating shared design parameter tab", e);
					} catch (IOException e)
					{
						DestecsDebugPlugin.logError("IO error while populating shared design parameter tab", e);
					}
				}
			}
		}
	}

	private HashMap<String, List<String>> getItemIfPresent(String name)
	{
		for (TableItem item : incrementTable.getItems())
		{
			if (item.getText(0).trim().equalsIgnoreCase(name.trim()))
			{
				HashMap<String, List<String>> result = new HashMap<String, List<String>>();
				result.put(item.getText(0), Arrays.asList(new String[] {
						item.getText(1), item.getText(2), item.getText(3) }));
				return result;
			}
		}
		
		for (TableItem item : valueSetTable.getItems())
		{
			if (item.getText(0).trim().equalsIgnoreCase(name.trim()))
			{
				HashMap<String, List<String>> result = new HashMap<String, List<String>>();
				result.put(item.getText(0), Arrays.asList(new String[] {
						item.getText(1) }));
				return result;
			}
		}

		return null;
	}

}
