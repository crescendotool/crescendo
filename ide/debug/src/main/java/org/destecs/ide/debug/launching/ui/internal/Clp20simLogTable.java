package org.destecs.ide.debug.launching.ui.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.launching.ui.IUpdatableTab;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class Clp20simLogTable implements ICheckStateProvider, ICheckStateListener
{
	private IUpdatableTab tab;
	private Set<LogItem> logItems = null;
	private CheckboxTreeViewer logTreeViewer;
	private LogItemTree logItemTree = null;
	
	
	public Clp20simLogTable(Set<LogItem> logItems)
	{
		this.logItems = logItems;
	}

	public void createLogTree(Composite comp, IUpdatableTab argTab)
	{
		this.tab = argTab;
		Group group = new Group(comp, SWT.NONE);
		group.setText("Log");
		group.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		gd.minimumHeight = 100;
		group.setLayoutData(gd);
		
		logTreeViewer = new CheckboxTreeViewer(group, SWT.FILL);
		logTreeViewer.setContentProvider(new LogTreeContentProvider());
		logTreeViewer.setLabelProvider(new LogTreeLabelProvider());
		
		logTreeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		logTreeViewer.setCheckStateProvider(this);
		logTreeViewer.addCheckStateListener(this);		
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
				}

			}
			logItemTree = LogItemTree.buildTreeFromItems(logItems, null, tab,true);
			refresh();
			
		} catch (CoreException e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	public void refresh()
	{
		if (logTreeViewer != null)
		{
			logTreeViewer.setInput(logItemTree);
			logTreeViewer.refresh();			
			logTreeViewer.expandAll();	
			logTreeViewer.collapseAll();
		}
	}


	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES, getConfigValue());
	}


	private String getConfigValue()
	{
		StringBuilder sb = new StringBuilder();
		Object[] checkedElements = logTreeViewer.getCheckedElements();
		Object[] grayedElements = logTreeViewer.getGrayedElements();
		
		Set<Object> elements = new HashSet<Object>(Arrays.asList(checkedElements));
		elements.removeAll(Arrays.asList(grayedElements));
		
		if(elements.size() > 0)
		{
			for (Object object : elements)
			{
				if(object instanceof LogItemTree)
				{
					sb.append(((LogItemTree) object).getKey());
					sb.append(",");
				}
			}
			
			sb.append("time");
			System.out.println("Writting config log: " + sb.toString());
		}
		return sb.toString();
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_LOG_VARIABLES, "");		
	}

	public void populateControl(Set<LogItem> logItems, IUpdatableTab tab)
	{
		logItemTree = LogItemTree.buildTreeFromItems(logItems,logItemTree,tab,false);		
	}

	public boolean isChecked(Object element)
	{
		if(element instanceof LogItemTree)
		{
			LogItemTree item = (LogItemTree) element;
			return item.isVirtual() || item.isChecked();
		}
		
		return false;
	}

	public boolean isGrayed(Object element)
	{
		if(element instanceof LogItemTree)
		{
			LogItemTree item = (LogItemTree) element;
			return item.isVirtual();
		}
		
		return false;
	}

	@Override
	public void checkStateChanged(CheckStateChangedEvent event)
	{
		tab.updateTab();
		
	}
	

}
