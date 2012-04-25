package org.destecs.ide.debug.launching.ui.internal;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class LogTreeContentProvider implements ITreeContentProvider
{

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		// TODO Auto-generated method stub

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

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if(parentElement instanceof LogItemTree)
		{
			return ((LogItemTree) parentElement).getChildren();
		}
		
		return new Object[0];
	}

	@Override
	public Object getParent(Object element)
	{
		return null;
	}

	@Override
	public boolean hasChildren(Object element)
	{
		if(element instanceof LogItemTree)
		{
			return ((LogItemTree) element).getChildren().length > 0;
		}
		
		return false;
	}

}
