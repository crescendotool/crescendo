package org.destecs.ide.debug.launching.ui.internal;

import java.util.HashSet;
import java.util.Set;

import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.launching.ui.Clp20simLogViewerSorter;
import org.destecs.ide.debug.launching.ui.IUpdatableTab;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Clp20simLogTable
{

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

	
	
	private TableViewer logViewer;
	private IUpdatableTab tab;
	private LogVariablesSelectionManager logManager = new LogVariablesSelectionManager();
	private Set<LogItem> logItems = null;
	
	
	public Clp20simLogTable(Set<LogItem> logItems)
	{
		this.logItems = logItems;
	}


	public void createLogTable(Composite comp, IUpdatableTab argTab)
	{
		this.tab = argTab;
		Group group = new Group(comp, SWT.NONE);
		group.setText("Log");
		group.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		gd.minimumHeight = 100;
		group.setLayoutData(gd);

		logViewer = new TableViewer(group, SWT.FULL_SELECTION | SWT.FILL
				| SWT.CHECK);

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
							tab.updateTab();
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


	public void initializeFrom(ILaunchConfiguration configuration)
	{
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
			tab.updateTab();
		}

	}


	public void refreshAndReselectInput()
	{
		if (logViewer != null)
		{
			logViewer.refresh();
		}

		reSelectVariables();
	}


	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES, logManager.getConfigValue());
	}


	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES, "");
	}


	

}
