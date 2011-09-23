package org.destecs.ide.ui.editor.impl;

import org.destecs.core.parsers.ParserWrapper;
import org.destecs.core.parsers.VdmLinkParserWrapper;
import org.destecs.ide.ui.editor.core.BaseCodeScanner;
import org.destecs.ide.ui.editor.core.BaseReconcilingStrategy;
import org.destecs.ide.ui.editor.core.DestecsBaseSourceViewerConfiguration;
import org.destecs.ide.ui.editor.core.DestecsDocumentProvider;
import org.destecs.ide.ui.editor.syntax.DestecsColorProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextEditor;

public class VdmLinkEditor extends TextEditor
{
	public static class VdmLinkSourceViewerConfiguration extends
			DestecsBaseSourceViewerConfiguration
	{

		@Override
		protected ITokenScanner getCodeScaner(DestecsColorProvider colorProvider)
		{
			return new VdmLinkCodeScanner(colorProvider);
		}

		@Override
		protected IReconcilingStrategy getReconcilingStrategy()
		{
			return new VdmLinkReconcilingStrategy();
		}

		@Override
		public IContentAssistant getContentAssistant(ISourceViewer sourceViewer)
		{
			ContentAssistant assistant = new ContentAssistant();
			 assistant.setContentAssistProcessor(new VdmLinkCompletionProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
			// Allow automatic activation after 500 msec
			assistant.enableAutoActivation(true);
			assistant.setAutoActivationDelay(500);

			return assistant;
		}

	}

	public static class VdmLinkReconcilingStrategy extends
			BaseReconcilingStrategy
	{
		@SuppressWarnings("unchecked")
		@Override
		protected ParserWrapper getParser()
		{
			return new VdmLinkParserWrapper();
		}
	}

	public static class VdmLinkCodeScanner extends BaseCodeScanner
	{

		public VdmLinkCodeScanner(DestecsColorProvider provider)
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
			return new String[] { "input", "output", "event",
					"sdp","model" };
		}

		@Override
		protected String[] getTypeWords()
		{
			return new String[] {};
		}

	}

	public VdmLinkEditor()
	{
		super();
		setDocumentProvider(new DestecsDocumentProvider());
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		setSourceViewerConfiguration(getVdmLinkSourceViewerConfiguration());
	}

	public VdmLinkSourceViewerConfiguration getVdmLinkSourceViewerConfiguration()
	{
		return new VdmLinkSourceViewerConfiguration();
	}

}
