package org.destecs.ide.debug.launching.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class FileExtensionFilter extends ViewerFilter
{
	private String filter;
	private boolean allowFolders = false;

	public FileExtensionFilter(String filter)
	{
		this.filter = filter;
	}

	public FileExtensionFilter(String filter, boolean allowFolders)
	{
		this.filter = filter;
		this.allowFolders = allowFolders;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		return element instanceof IFile
				&& ((IFile) element).getFullPath().getFileExtension().equals(filter)
				|| (allowFolders && element instanceof IFolder);
	}
};
