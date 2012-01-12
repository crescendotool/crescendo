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
package org.destecs.ide.simeng.ui.views;

import java.util.List;
import java.util.Vector;

import org.destecs.ide.simeng.actions.BaseSimulationControlAction;
import org.destecs.ide.simeng.actions.PauseAction;
import org.destecs.ide.simeng.actions.ResumeAction;
import org.destecs.ide.simeng.actions.TerminationAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class InfoTableView extends ViewPart implements ISelectionListener
{

	private TableViewer viewer;
	// private Action doubleClickAction;
	final Display display = Display.getCurrent();
	final List<List<String>> dataSource = new Vector<List<String>>();

	public static String SIMULATION_VIEW_ID = "org.destecs.ide.simeng.ui.views.SimulationView";
	public static String SIMULATION_ENGINE_VIEW_ID = "org.destecs.ide.simeng.ui.views.SimulationEngineView";
	public static String SIMULATION_MESSAGES_VIEW_ID = "org.destecs.ide.simeng.ui.views.SimulationMessagesView";

	private final Object lock = new Object();
	// private Action actionSetProvedFilter;

	public int elementCount = 200;
	private BaseSimulationControlAction terminationAction;
	private PauseAction pauseAction;
	private ResumeAction resumeAction;

	static class ViewContentProvider implements IStructuredContentProvider
	{
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof List)
			{
				@SuppressWarnings("rawtypes")
				List list = (List) inputElement;
				return list.toArray();
			}
			return new Object[0];
		}

	}

	static class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider
	{

		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof List)
			{
				@SuppressWarnings("rawtypes")
				List list = (List) element;
				if (list.size() > columnIndex)
				{
					return list.get(columnIndex).toString();
				}
				return "---";
			}
			return "---";

		}

		public Image getColumnImage(Object element, int columnIndex)
		{

			return null;
		}

	}

	/**
	 * The constructor.
	 */
	public InfoTableView()
	{
		// createMenu();
		this.terminationAction = new TerminationAction();
		this.pauseAction = new PauseAction();
		this.resumeAction = new ResumeAction();
		this.resumeAction.setEnabled(false);
		
		this.pauseAction.setResume(this.resumeAction);
		this.resumeAction.setPause(this.pauseAction);
	}

	
	public BaseSimulationControlAction getTerminationAction()
	{
		return terminationAction;
	}
	
	public PauseAction getPauseAction()
	{
		return pauseAction;
	}
	
	public ResumeAction getResumeAction()
	{
		return resumeAction;
	}

	/**
	 * Create toolbar.
	 */
	private void createToolbar()
	{
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		// mgr.add(addItemAction);
		// mgr.add(deleteItemAction);
		mgr.add(resumeAction);
		mgr.add(pauseAction);
		mgr.add(terminationAction);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL);
		// test setup columns...
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(20, true));

		layout.addColumnData(new ColumnWeightData(100, true));

		// layout.addColumnData(new ColumnWeightData(60, false));
		// layout.addColumnData(new ColumnWeightData(20, false));
		viewer.getTable().setLayout(layout);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setSortDirection(SWT.NONE);
		viewer.setSorter(null);

		TableColumn column01 = new TableColumn(viewer.getTable(), SWT.LEFT);
		column01.setText("Source");
		column01.setToolTipText("Source");

		TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("Message");
		column.setToolTipText("Message");

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());

		viewer.setInput(dataSource);
		if (getSite().getId() != null
				&& getSite().getId().equals(SIMULATION_ENGINE_VIEW_ID))
		{
			createToolbar();
		}
	}

	public void addColumn(String name)
	{
		for (TableColumn tc : viewer.getTable().getColumns())
		{
			if (tc.getText().equals(name))
			{
				return;
			}
		}
		((TableLayout) viewer.getTable().getLayout()).addColumnData(new ColumnWeightData(60, false));

		TableColumn column2 = new TableColumn(viewer.getTable(), SWT.LEFT);
		column2.setText(name);
		column2.setToolTipText("Show " + name);
		refreshPackTable();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	Boolean isUpdating = false;

	public void refreshList()
	{
		if (isUpdating)
		{
			return;
		}
		synchronized (lock)
		{
			isUpdating = true;
		}

		display.asyncExec(new Runnable()
		{

			public void run()
			{
				viewer.refresh();
				viewer.getTable().select(viewer.getTable().getItemCount() - 1);
				viewer.getTable().showSelection();
				synchronized (lock)
				{
					isUpdating = false;
				}
			}

		});

	}

	public void packColumns()
	{
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				for (TableColumn col : viewer.getTable().getColumns())
				{
					col.pack();
				}
			}

		});
	}

	public synchronized void setDataList(final List<String> data)
	{
		while (dataSource.size() > elementCount)
		{
			dataSource.remove(0);
		}
		dataSource.add(data);
		// refreshList();
	}

	public void refreshPackTable()
	{
		if (display.isDisposed())
		{
			return;
		}
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				for (TableColumn col : viewer.getTable().getColumns())
				{
					col.pack();
				}
			}

		});
		refreshList();
	}

	public synchronized void resetBuffer()
	{
		dataSource.clear();
		// refreshList();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{

	}
}
