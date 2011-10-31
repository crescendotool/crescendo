package org.destecs.ide.ui.editor.impl;

import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.parsers.ParserWrapper;
import org.destecs.ide.ui.editor.core.BaseCodeScanner;
import org.destecs.ide.ui.editor.core.BaseReconcilingStrategy;
import org.destecs.ide.ui.editor.core.DestecsBaseSourceViewerConfiguration;
import org.destecs.ide.ui.editor.core.DestecsDocumentProvider;
import org.destecs.ide.ui.editor.syntax.DestecsColorProvider;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.ui.editors.text.TextEditor;

public class ContractEditor extends TextEditor
{
	public static class DestecsContractSourceViewerConfiguration extends
			DestecsBaseSourceViewerConfiguration
	{

		@Override
		protected ITokenScanner getCodeScaner(DestecsColorProvider colorProvider)
		{
			return new DestecsContractCodeScanner(colorProvider);
		}

		@Override
		protected IReconcilingStrategy getReconcilingStrategy()
		{
			return new ContractReconcilingStrategy();
		}

	}
	
	public static class ContractReconcilingStrategy extends BaseReconcilingStrategy
	{
		@SuppressWarnings("unchecked")
		@Override
		protected ParserWrapper getParser()
		{
			return new ContractParserWrapper();
		}
	}

	public static class DestecsContractCodeScanner extends BaseCodeScanner
	{

		public DestecsContractCodeScanner(DestecsColorProvider provider)
		{
			super(provider);
		}

		@Override
		protected String[] getCommentWords()
		{
			return new String[] {  };
		}

		@Override
		protected String[] getKeywords()
		{
			return new String[] { "contract", "end", "sdp","shared_design_parameter",
					"real", "monitored", "controlled", "event", "bool",
					"false", "true", "matrix" };
		}

		@Override
		protected String[] getTypeWords()
		{
			return new String[] { "true", "false", "real", "bool" };
		}

	}

	public ContractEditor()
	{
		super();
		setDocumentProvider(new DestecsDocumentProvider());
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		setSourceViewerConfiguration(getContractSourceViewerConfiguration());
	}

	public DestecsContractSourceViewerConfiguration getContractSourceViewerConfiguration()
	{
		return new DestecsContractSourceViewerConfiguration();
	}

}
