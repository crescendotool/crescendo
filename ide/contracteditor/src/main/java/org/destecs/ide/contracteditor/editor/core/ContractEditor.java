package org.destecs.ide.contracteditor.editor.core;

import org.eclipse.ui.editors.text.TextEditor;

public class ContractEditor extends TextEditor
{

	public ContractEditor()
	{
		super();
		setDocumentProvider(new ContractDocumentProvider());
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		setSourceViewerConfiguration(getContractSourceViewerConfiguration());
	}

	// @Override
	// public VdmSourceViewerConfiguration getVdmSourceViewerConfiguration() {
	// return new DestecsContractSourceViewerConfiguration();
	// }

	public DestecsContractSourceViewerConfiguration getContractSourceViewerConfiguration()
	{
		return new DestecsContractSourceViewerConfiguration();
	}

}
