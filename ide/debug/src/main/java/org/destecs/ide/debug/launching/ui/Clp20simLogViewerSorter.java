package org.destecs.ide.debug.launching.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class Clp20simLogViewerSorter extends  ViewerSorter {

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		
		if(e1 instanceof String && e2 instanceof String)
		{
			return ((String) e1).compareTo((String)e2);
		}
		return super.compare(viewer, e1, e2);
	}

}
