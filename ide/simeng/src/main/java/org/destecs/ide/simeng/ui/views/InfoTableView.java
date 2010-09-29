package org.destecs.ide.simeng.ui.views;

import java.util.List;
import java.util.Vector;

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
	// private Action actionSetProvedFilter;

	
	public int elementCount=200;

	class ViewContentProvider implements IStructuredContentProvider
	{
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}

		public void dispose()
		{
		}

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof List)
			{
				List list = (List) inputElement;
				return list.toArray();
			}
			return new Object[0];
		}

	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider
	{

		@SuppressWarnings("unchecked")
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof List)
			{
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

		// public Image getColumnImage(Object obj, int index)
		// {
		// if (index == 3)
		// {
		// return getImage(obj);
		// }
		// return null;
		// }

		// @Override
		// public Image getImage(Object obj)
		// {
		// ProofObligation data = (ProofObligation) obj;
		//
		// String imgPath = "icons/cview16/unproved.png";
		//
		// if (data.status == POStatus.PROVED)
		// imgPath = "icons/cview16/proved.png";
		// else if (data.status == POStatus.TRIVIAL)
		// imgPath = "icons/cview16/trivial.png";
		//
		// return Activator.getImageDescriptor(imgPath).createImage();
		// }

	}

	// class IdSorter extends ViewerSorter
	// {
	// }

	/**
	 * The constructor.
	 */
	public InfoTableView()
	{
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
	}
	
	public void addColumn(String name)
	{
		for (TableColumn tc : viewer.getTable().getColumns())
		{
			if(tc.getText().equals(name))
			{
				return;
			}
		}
		((TableLayout)viewer.getTable().getLayout()).addColumnData(new ColumnWeightData(60, false));
		
		TableColumn column2 = new TableColumn(viewer.getTable(), SWT.LEFT);
		column2.setText(name);
		column2.setToolTipText("Show "+name);
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
		if(isUpdating)
		{
			return;
		}
		synchronized (isUpdating)
		{
			isUpdating = true;
		}
		
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				viewer.refresh();
				viewer.getTable().select(viewer.getTable().getItemCount()-1);
				viewer.getTable().showSelection();
				synchronized (isUpdating)
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
		while(dataSource.size()>elementCount)
		{
			dataSource.remove(0);
		}
		dataSource.add(data);
		//refreshList();
	}

	public void refreshPackTable()
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
		refreshList();
	}

	public synchronized void resetBuffer()
	{
		dataSource.clear();
		//refreshList();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		

	}
}
