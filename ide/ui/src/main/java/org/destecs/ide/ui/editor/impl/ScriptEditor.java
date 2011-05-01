package org.destecs.ide.ui.editor.impl;

import org.destecs.core.parsers.ParserWrapper;
import org.destecs.core.parsers.ScenarioParserWrapper;
import org.destecs.core.parsers.ScriptParserWrapper;
import org.destecs.ide.ui.editor.core.BaseCodeScanner;
import org.destecs.ide.ui.editor.core.BaseReconcilingStrategy;
import org.destecs.ide.ui.editor.core.DestecsBaseSourceViewerConfiguration;
import org.destecs.ide.ui.editor.core.DestecsDocumentProvider;
import org.destecs.ide.ui.editor.syntax.DestecsColorProvider;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.ui.editors.text.TextEditor;

public class ScriptEditor extends TextEditor
{
	public static class ScenarioSourceViewerConfiguration extends
			DestecsBaseSourceViewerConfiguration
	{

		@Override
		protected ITokenScanner getCodeScaner(DestecsColorProvider colorProvider)
		{
			return new ScenarioCodeScanner(colorProvider);
		}

		@Override
		protected IReconcilingStrategy getReconcilingStrategy()
		{
			return new ScenarioReconcilingStrategy();
		}

	}
	
	public static class ScenarioReconcilingStrategy extends BaseReconcilingStrategy
	{
		@SuppressWarnings("unchecked")
		@Override
		protected ParserWrapper getParser()
		{
			return new ScriptParserWrapper();
		}
	}

	public static class ScenarioCodeScanner extends BaseCodeScanner
	{

		public ScenarioCodeScanner(DestecsColorProvider provider)
		{
			super(provider);
		}

		@Override
		protected String[] getCommentWords()
		{
			return new String[] {};
		}

		@Override
		protected String[] getKeywords()
		{
			return new String[] { "DE","CT","de","ct","when","for","include","time","do","print","after","quit","revert","error","warn","boolean","real","int" };
		}

		@Override
		protected String[] getTypeWords()
		{
			return new String[] { "true", "false"};
		}

	}

	public ScriptEditor()
	{
		super();
		setDocumentProvider(new DestecsDocumentProvider());
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		setSourceViewerConfiguration(getScenarioSourceViewerConfiguration());
	}

	public ScenarioSourceViewerConfiguration getScenarioSourceViewerConfiguration()
	{
		return new ScenarioSourceViewerConfiguration();
	}

}
