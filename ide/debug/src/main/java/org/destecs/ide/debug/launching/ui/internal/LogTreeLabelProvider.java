package org.destecs.ide.debug.launching.ui.internal;

import org.eclipse.jface.viewers.LabelProvider;

public class LogTreeLabelProvider extends LabelProvider
{
	
	@Override
	public String getText(Object element)
	{
		if(element instanceof LogItemTree)
		{
			return ((LogItemTree) element).getName();
		}
		
		return super.getText(element);
	}	

}
