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
package org.destecs.ide.ui.editor.impl;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;

public class CoSimMultiPageEditorMatchingStrategy implements
		IEditorMatchingStrategy
{

	public boolean matches(IEditorReference editorRef, IEditorInput input)
	{
		IEditorPart editor = editorRef.getEditor(true);
		if(editor instanceof CoSimMultiPageEditor)
		{
			CoSimMultiPageEditor coSimEditor = (CoSimMultiPageEditor) editor;
			
			IFile file=(IFile) input.getAdapter(IFile.class);
			if( coSimEditor.getHandingFiles().contains(file))
			{
				coSimEditor.setActivePage(file);
				return true;
			}
		}
		return false;
	}

}
