// package org.destecs.ide.debug.launching.ui;
//
// import java.util.List;
// import java.util.Vector;
//
// import org.destecs.ide.debug.launching.ui.internal.LinesProvider;
// import org.eclipse.core.resources.IFile;
// import org.eclipse.core.resources.IProject;
// import org.eclipse.debug.core.ILaunchConfiguration;
// import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
// import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
// import org.eclipse.debug.ui.ILaunchConfigurationTab;
// import org.eclipse.jface.viewers.ArrayContentProvider;
// import org.eclipse.jface.viewers.ColumnLabelProvider;
// import org.eclipse.jface.viewers.ISelection;
// import org.eclipse.jface.viewers.ISelectionChangedListener;
// import org.eclipse.jface.viewers.SelectionChangedEvent;
// import org.eclipse.jface.viewers.TableViewer;
// import org.eclipse.jface.viewers.TableViewerColumn;
// import org.eclipse.jface.viewers.ViewerComparator;
// import org.eclipse.jface.window.Window;
// import org.eclipse.swt.SWT;
// import org.eclipse.swt.events.SelectionAdapter;
// import org.eclipse.swt.events.SelectionEvent;
// import org.eclipse.swt.layout.GridData;
// import org.eclipse.swt.layout.GridLayout;
// import org.eclipse.swt.widgets.Button;
// import org.eclipse.swt.widgets.Composite;
// import org.eclipse.swt.widgets.Table;
// import org.eclipse.swt.widgets.TableColumn;
// import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
// import org.eclipse.ui.model.BaseWorkbenchContentProvider;
// import org.eclipse.ui.model.WorkbenchLabelProvider;
//
// public class DseArchitectureLaunchTab extends AbstractLaunchConfigurationTab {
//
// int projNumber = 1;
//
// class TableSelectionListener implements ISelectionChangedListener
// {
//
// public void selectionChanged(SelectionChangedEvent event) {
// ISelection s = event.getSelection();
// System.out.println(event.getSelection());
// }
//
// }
//
// TableViewer viewer = null;
// private Button selectArchitecturePathButton;
// private Button deleteButton;
//
// public void createControl(Composite parent) {
// Composite comp = new Composite(parent, SWT.NONE);
// setControl(comp);
//
// comp.setLayout(new GridLayout(2, true));
// comp.setFont(parent.getFont());
//
// createTableViewer(comp);
// createBrowseButton(comp);
// createDeleteButton(comp);
// }
//
//
//
//
// private void createDeleteButton(Composite parent)
// {
// deleteButton = createPushButton(parent, "Delete", null);
//
// }
//
// private void createTableViewer(Composite parent) {
// viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
// | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
// createColumns(parent, viewer);
// final Table table = viewer.getTable();
// table.setHeaderVisible(true);
// table.setLinesVisible(true);
//
// viewer.setContentProvider(new ArrayContentProvider());
// // Get the content for the viewer, setInput will call getElements in the
// // contentProvider
// viewer.setInput(LinesProvider.getInstance().getLines());
// viewer.addSelectionChangedListener(new TableSelectionListener());
//
//
// // Layout the viewer
// GridData gridData = new GridData();
// gridData.verticalAlignment = GridData.FILL;
// gridData.horizontalSpan = 2;
// gridData.grabExcessHorizontalSpace = true;
// gridData.grabExcessVerticalSpace = true;
// gridData.horizontalAlignment = GridData.FILL;
// viewer.getControl().setLayoutData(gridData);
// }
//
// private void createColumns(Composite parent, TableViewer viewer2) {
//
// // First column is for the first name
// TableViewerColumn col = createTableViewerColumn("Architecture Definitions", 500);
// col.setLabelProvider(new ColumnLabelProvider() {
// @Override
// public String getText(Object element) {
//
// return (String) element;
// }
// });
//
// }
// private TableViewerColumn createTableViewerColumn(String title, int bound) {
// final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
// SWT.NONE);
// final TableColumn column = viewerColumn.getColumn();
// column.setText(title);
// column.setWidth(bound);
// column.setResizable(true);
// column.setMoveable(true);
// return viewerColumn;
//
// }
//
// public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
// // TODO Auto-generated method stub
//
// }
//
// public void initializeFrom(ILaunchConfiguration configuration) {
// // TODO Auto-generated method stub
//
// }
//
// public void performApply(ILaunchConfigurationWorkingCopy configuration) {
// // TODO Auto-generated method stub
//
// }
//
// public String getName() {
// // TODO Auto-generated method stub
// return "DE Architectures";
// }
//
// private void createBrowseButton(Composite group){
// selectArchitecturePathButton = createPushButton(group, "Add...", null);
// selectArchitecturePathButton.setEnabled(true);
// selectArchitecturePathButton.addSelectionListener(new SelectionAdapter()
// {
// @Override
// public void widgetSelected(SelectionEvent e)
// {
// class ScenarioContentProvider extends
// BaseWorkbenchContentProvider
// {
// @Override
// public boolean hasChildren(Object element)
// {
// if (element instanceof IProject)
// {
// return super.hasChildren(element);
// } else
// {
// return super.hasChildren(element);
// }
// }
//
// @SuppressWarnings("unchecked")
// @Override
// public Object[] getElements(Object element)
// {
// List elements = new Vector();
// Object[] arr = super.getElements(element);
// if (arr != null)
// {
// for (Object object : arr)
// {
// if (object instanceof IFile)
// {
// IFile f = (IFile) object;
// if (f.getFullPath().getFileExtension().equals("arch"))
// {
// elements.add(f);
// }
// }
// }
// return elements.toArray();
// }
// return null;
// }
//
// }
// ;
// ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new
// ScenarioContentProvider());
// dialog.setTitle("Architecture Selection");
// dialog.setMessage("Select an architecture:");
// dialog.setComparator(new ViewerComparator());
// for (ILaunchConfigurationTab tab : getLaunchConfigurationDialog().getTabs())
// {
// if (tab instanceof CoSimLaunchConfigurationTab)
// {
// CoSimLaunchConfigurationTab cosimLaunchTab = (CoSimLaunchConfigurationTab) tab;
// IProject project = cosimLaunchTab.getProject();
// if (project != null)
// {
// dialog.setInput(project.getFolder("dse"));
// }
// }
// }
// if (dialog.open() == Window.OK)
// {
// if (dialog.getFirstResult() != null
// // && dialog.getFirstResult() instanceof IProject
// // && ((IProject) dialog.getFirstResult()).getAdapter(IVdmProject.class) != null)
// )
// {
// //fArchitecturePathText.setText(((IFile) dialog.getFirstResult()).getProjectRelativePath().toString());
//
// }
//
// }
// }
// });
// }
//
// }
