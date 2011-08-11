package org.destecs.ide.debug.launching.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class FileExtensionFilter extends ViewerFilter
{
	final private List<String> filter = new Vector<String>();
	private boolean allowFolders = false;

	public FileExtensionFilter(String... filter)
	{
		this.filter.addAll(Arrays.asList(filter));
	}

	public FileExtensionFilter(boolean allowFolders,String... filter)
	{
		this.filter.addAll(Arrays.asList(filter));
		this.allowFolders = allowFolders;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		return element instanceof IFile
				&& filter.contains(((IFile) element).getFullPath().getFileExtension())
				|| (allowFolders && element instanceof IFolder);
	}
};
