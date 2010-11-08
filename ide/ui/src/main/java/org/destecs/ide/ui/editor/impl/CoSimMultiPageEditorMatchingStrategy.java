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
