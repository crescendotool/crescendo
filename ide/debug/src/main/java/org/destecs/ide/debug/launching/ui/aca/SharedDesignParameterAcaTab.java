package org.destecs.ide.debug.launching.ui.aca;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.destecs.core.parsers.SdpParserWrapper;
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

	private Table table;

	private WidgetListener fListener = new WidgetListener();
	private final Set<String> sdps = new HashSet<String>();

	public void createControl(Composite parent)
	{
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		comp.setLayout(new GridLayout(1, true));
		comp.setFont(parent.getFont());

		table = new Table(comp, SWT.FULL_SELECTION | SWT.VIRTUAL);// SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);

		String[] titles = { "Name", "From", "To", "Incremet by" };
		for (int i = 0; i < titles.length; i++)
		{
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(titles[i]);
			column.setWidth(100);

		}

		addEditorToTable();

		Button defaultsButton = createPushButton(comp, "Clear", null);
		defaultsButton.addSelectionListener(new SelectionListener()
		{

			public void widgetSelected(SelectionEvent e)
			{
				table.removeAll();
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{

			}
		});
	}

	private void addEditorToTable()
	{
		// Create an editor object to use for text editing
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Use a mouse listener, not a selection listener, since we're interested
		// in the selected column as well as row
		table.addMouseListener(new MouseAdapter()
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
				final TableItem item = table.getItem(pt);
				if (item != null)
				{
					// Determine which column was selected
					int column = -1;
					for (int i = 0, n = table.getColumnCount(); i < n; i++)
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
						final CCombo combo = new CCombo(table, SWT.READ_ONLY);
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
						if (table.getColumn(column).getWidth() < editor.minimumWidth)
						{
							table.getColumn(column).setWidth(editor.minimumWidth);
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
						final Text text = new Text(table, SWT.NONE);
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

						text.addModifyListener(fListener);

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
				} else if (table.getItemCount() < sdps.size())
				{
					// allow new items to be created until we reach the total sdp count
					new TableItem(table, SWT.NONE);

				}
				{

				}
			}
		});

	}

	public String getName()
	{
		return "Shared Design Parameters";
	}

	public void initializeFrom(ILaunchConfiguration configuration)
	{
		populateSdps();
		table.removeAll();
		try
		{
			String data = configuration.getAttribute(IDebugConstants.DESTECS_ACA_SHARED_DESIGN_PARAMETERS, "");
			if (data != null && !data.isEmpty())
			{
				String[] items = data.split(",");
				for (String item : items)
				{
					String[] colls = item.split("\\|");
					if (colls.length == table.getColumnCount())
					{
						TableItem tableItem = new TableItem(table, SWT.NONE);
						tableItem.setText(colls);
					}
				}
			}
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_ACA_SHARED_DESIGN_PARAMETERS, getSdpsSyntax());
	}

	private String getSdpsSyntax()
	{
		StringBuilder sb = new StringBuilder();
		for (TableItem item : table.getItems())
		{
			for (int i = 0; i < table.getColumnCount(); i++)
			{
				sb.append(item.getText(i));
				if (i < table.getColumnCount())
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
		for (TableItem item : table.getItems())
		{
			for (int i = 0; i < table.getColumnCount(); i++)
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private HashMap<String, List<String>> getItemIfPresent(String name)
	{
		for (TableItem item : table.getItems())
		{
			if (item.getText(0).trim().equalsIgnoreCase(name.trim()))
			{
				HashMap<String, List<String>> result = new HashMap<String, List<String>>();
				result.put(item.getText(0), Arrays.asList(new String[] {
						item.getText(1), item.getText(2), item.getText(3) }));
				return result;
			}
		}

		return null;
	}

}
