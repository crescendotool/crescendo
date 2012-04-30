package org.destecs.ide.debug.launching.ui.internal;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class LogTreeContentProvider implements ITreeContentProvider
{

	public void dispose()
	{

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{

	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		if(inputElement instanceof LogItemTree)
		{
			return ((LogItemTree) inputElement).getChildren();
		}
		return null;
	}

	public Object[] getChildren(Object parentElement)
	{
		if(parentElement instanceof LogItemTree)
		{
			return ((LogItemTree) parentElement).getChildren();
		}
		
		return new Object[0];
	}

	public Object getParent(Object element)
	{
		return null;
	}

	public boolean hasChildren(Object element)
	{
		if(element instanceof LogItemTree)
		{
			return ((LogItemTree) element).getChildren().length > 0;
		}
		
		return false;
	}

}
