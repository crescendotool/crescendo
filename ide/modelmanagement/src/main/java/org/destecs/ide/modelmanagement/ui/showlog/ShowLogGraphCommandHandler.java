package org.destecs.ide.modelmanagement.ui.showlog;

import java.awt.Window;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

public class ShowLogGraphCommandHandler extends AbstractHandler {
//implements ISelectionListener
	
	//private IWorkbenchWindow window;
	//private IStructuredSelection selection;
	
	public ShowLogGraphCommandHandler(){
		
	}
	
//	public ShowLogGraphCommandHandler(IWorkbenchWindow window){
//		this.window = window;
//		setText("&Log Graph");
//		window.getSelectionService().addSelectionListener(this);
//	}
	
	
	
//	private void setText(String string) {
//		// TODO Auto-generated method stub
//		
//	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		//String path = openFileDialog();
//		if (path!=null){
//			IEditorInput input = new LogGraphEditorInput(new Path(path));
//			IWorkbenchPage page = window.getActivePage();
//			try{
//				page.openEditor(input, LogGraphEditor.ID, true);
//			}catch(PartInitException e){				
//			}			
//		}
		System.out.println("See result by selecting: Window->Show View->Other->Development Log Graph");
		return null;
	}

//	private String openFileDialog() {
//		FileDialog dialog = new FileDialog(window.getShell(),SWT.OPEN);
//		dialog.setText("Graph");
//		dialog.setFilterExtensions(new String[] {".jpg"});
//		return dialog.open();
//	}

//	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
//		// TODO Auto-generated method stub		
//	}


}
