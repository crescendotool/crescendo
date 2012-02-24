package org.destecs.ide.ui.editor.impl;

import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.editors.text.TextEditor;

public class AbstractDestecsEditor extends TextEditor {

	protected SourceViewerConfiguration configuration;
	
	 public SourceViewerConfiguration getAbstractSourceViewerConfiguration()
	 {
		 return configuration;
	 }
	
}
