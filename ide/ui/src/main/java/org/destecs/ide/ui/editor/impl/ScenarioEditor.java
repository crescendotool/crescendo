package org.destecs.ide.ui.editor.impl;

import org.destecs.core.parsers.ParserWrapper;
import org.destecs.core.parsers.ScenarioParserWrapper;
import org.destecs.ide.ui.editor.core.BaseCodeScanner;
import org.destecs.ide.ui.editor.core.BaseReconcilingStrategy;
import org.destecs.ide.ui.editor.core.DestecsBaseSourceViewerConfiguration;
import org.destecs.ide.ui.editor.core.DestecsDocumentProvider;
import org.destecs.ide.ui.editor.syntax.DestecsColorProvider;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.ui.editors.text.TextEditor;

public class ScenarioEditor extends TextEditor
{
	public class ScenarioSourceViewerConfiguration extends
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
	
	public class ScenarioReconcilingStrategy extends BaseReconcilingStrategy
	{
		@SuppressWarnings("unchecked")
		@Override
		protected ParserWrapper getParser()
		{
			return new ScenarioParserWrapper();
		}
	}

	public class ScenarioCodeScanner extends BaseCodeScanner
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
			return new String[] {  };
		}

		@Override
		protected String[] getTypeWords()
		{
			return new String[] { "true", "false"};
		}

	}

	public ScenarioEditor()
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
