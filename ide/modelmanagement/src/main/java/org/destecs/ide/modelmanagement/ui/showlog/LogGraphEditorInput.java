package org.destecs.ide.modelmanagement.ui.showlog;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;

public class LogGraphEditorInput implements IPathEditorInput{

	private IPath path;
	
	public LogGraphEditorInput(IPath path){
		this.path = path;
	}
	
	public boolean exists() {
		return path.toFile().exists();
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		return path.toString();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return path.toString();
	}

	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	public IPath getPath() {
		return path;
	}

	
}
