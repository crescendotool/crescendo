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
//	private Action doubleClickAction;
	final Display display = Display.getCurrent();
	final List<List<String>> dataSource = new Vector<List<String>>();
	
	
//	private Action actionSetProvedFilter;

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
			if(element instanceof List)
			{
				List list = (List) element;
				if(list.size()>columnIndex)
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

//		public Image getColumnImage(Object obj, int index)
//		{
//			if (index == 3)
//			{
//				return getImage(obj);
//			}
//			return null;
//		}

//		@Override
//		public Image getImage(Object obj)
//		{
//			ProofObligation data = (ProofObligation) obj;
//
//			String imgPath = "icons/cview16/unproved.png";
//
//			if (data.status == POStatus.PROVED)
//				imgPath = "icons/cview16/proved.png";
//			else if (data.status == POStatus.TRIVIAL)
//				imgPath = "icons/cview16/trivial.png";
//
//			return Activator.getImageDescriptor(imgPath).createImage();
//		}

	}

//	class IdSorter extends ViewerSorter
//	{
//	}

	/**
	 * The constructor.
	 */
	public InfoTableView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL);
		// test setup columns...
		TableLayout layout = new TableLayout();
		layout.addColumnData(new ColumnWeightData(20,  true));
		layout.addColumnData(new ColumnWeightData(100,  true));
//		layout.addColumnData(new ColumnWeightData(60,  false));
//		layout.addColumnData(new ColumnWeightData(20,  false));
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

//		TableColumn column2 = new TableColumn(viewer.getTable(), SWT.LEFT);
//		column2.setText("Type");
//		column2.setToolTipText("Show Type");
//
//		TableColumn column3 = new TableColumn(viewer.getTable(), SWT.CENTER);
//		column3.setText("Status");
//		column3.setToolTipText("Show status");

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());

//		makeActions();
//		contributeToActionBars();
//		hookDoubleClickAction();

//		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
//
//			public void selectionChanged(SelectionChangedEvent event)
//			{
//
//				Object first = ((IStructuredSelection) event.getSelection()).getFirstElement();
//				if (first instanceof ProofObligation)
//				{
//					try
//					{
//						IViewPart v = getSite().getPage()
//								.showView(IPoviewerConstants.PoTableViewId);
//
//						if (v instanceof PoTableView)
//							((PoTableView) v).setDataList(project,
//									(ProofObligation) first);
//					} catch (PartInitException e)
//					{
//
//						e.printStackTrace();
//					}
//				}
//
//			}
//		});
		viewer.setInput(dataSource);
	}
	
//	private void contributeToActionBars() {
//		IActionBars bars = getViewSite().getActionBars();
//		
//		fillLocalToolBar(bars.getToolBarManager());
//	}
	
//	private void fillLocalToolBar(IToolBarManager manager) {
//
//		manager.add(actionSetProvedFilter);
//		
//		//drillDownAdapter.addNavigationActions(manager);
//	}

//	private void makeActions()
//	{
////		doubleClickAction = new Action() {
////			@Override
////			public void run()
////			{
////				ISelection selection = viewer.getSelection();
////				Object obj = ((IStructuredSelection) selection).getFirstElement();
////				if (obj instanceof ProofObligation)
////				{
////					gotoDefinition((ProofObligation) obj);
////					// showMessage(((ProofObligation) obj).toString());
////				}
////			}
////
////			private void gotoDefinition(ProofObligation po)
////			{
////				EditorUtility.gotoLocation(project.findIFile(po.location.file), po.location, po.name);
////			}
////		};
//		
////		actionSetProvedFilter = new Action("Filter proved",Action.AS_CHECK_BOX) {
////			@Override
////			public void run() {
////				ViewerFilter[] filters = viewer.getFilters();
////				boolean isSet = false;
////				for (ViewerFilter viewerFilter : filters) {
////					if (viewerFilter.equals(provedFilter))
////						isSet = true;
////				}
////				if (isSet) {
////					viewer.removeFilter(provedFilter);
////					
////				} else {
////					viewer.addFilter(provedFilter);
////					
////				}
////				if (viewer.getLabelProvider() instanceof ViewLabelProvider)
////					((ViewLabelProvider) viewer.getLabelProvider()).resetCounter(); // this
////																					// is
////																					// needed
////																					// to
////																					// reset
////																					// the
////				// numbering
////				viewer.refresh();
////			}
////
////		};
//	
//	}

//	private void hookDoubleClickAction()
//	{
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event)
//			{
//				doubleClickAction.run();
//			}
//		});
//	}

	// private void showMessage(String message)
	// {
	// MessageDialog.openInformation(
	// viewer.getControl().getShell(),
	// "PO Test",
	// message);
	// }

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

//	public void selectionChanged(IWorkbenchPart part, ISelection selection)
//	{
//
//		if (selection instanceof IStructuredSelection
//				&& part instanceof InfoTableView)
//		{
//			Object first = ((IStructuredSelection) selection).getFirstElement();
//			if (first instanceof ProofObligation)
//			{
//				try
//				{
//					IViewPart v = part.getSite()
//							.getPage()
//							.showView("org.overture.ide.plugins.poviewer.views.PoTableView");
//
//					if (v instanceof PoTableView)
//						((PoTableView) v).setDataList(project,
//								(ProofObligation) first);
//				} catch (PartInitException e)
//				{
//
//					e.printStackTrace();
//				}
//			}
//		}
//
//	}

	public void refreshList()
	{
		display.asyncExec(new Runnable() {

			public void run()
			{
				viewer.refresh();
			}

		});
	}

	public synchronized void setDataList(
			final List<String> data)
	{
		dataSource.add(data);
		refreshList();
	}

	private void refreshPackTable()
	{
		display.asyncExec(new Runnable() {

			public void run()
			{
				
				viewer.refresh();
//				if (viewer.getLabelProvider() instanceof ViewLabelProvider)
//					((ViewLabelProvider) viewer.getLabelProvider()).resetCounter(); // this
//																					// is
//																					// needed
//																					// to
//																					// reset
//																					// the
//				// numbering
//
//				viewer.setInput(data);
				
				for (TableColumn col : viewer.getTable().getColumns())
				{
					col.pack();
				}
			}

		});
	}
	
	public synchronized void resetBuffer()
	{
		dataSource.clear();
		refreshList();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		// TODO Auto-generated method stub
		
	}
}
