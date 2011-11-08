/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
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
