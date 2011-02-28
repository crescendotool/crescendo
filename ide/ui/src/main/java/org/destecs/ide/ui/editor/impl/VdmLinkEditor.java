package org.destecs.ide.ui.editor.impl;

import org.destecs.core.parsers.ParserWrapper;
import org.destecs.core.parsers.VdmLinkParserWrapper;
import org.destecs.core.vdmlink.Links;
import org.destecs.ide.ui.editor.core.BaseCodeScanner;
import org.destecs.ide.ui.editor.core.BaseReconcilingStrategy;
import org.destecs.ide.ui.editor.core.DestecsBaseSourceViewerConfiguration;
import org.destecs.ide.ui.editor.core.DestecsDocumentProvider;
import org.destecs.ide.ui.editor.syntax.DestecsColorProvider;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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
//			assistant.setContentAssistProcessor(new HtmlContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);

			// assistant.setContentAssistProcessor(new JavaDocCompletionProcessor(), JavaPartitionScanner.JAVA_DOC);

			// assistant.enableAutoActivation(true);
			// assistant.setAutoActivationDelay(0);
			// assistant.enablePrefixCompletion(true);
			// assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
			// assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
			// assistant.setContextInformationPopupBackground(JavaEditorEnvironment.getJavaColorProvider().getColor(new
			// RGB(150, 150, 0)));
			// assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

			// Set factory for information controller
			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
			// Allow automatic activation after 500 msec
			assistant.enableAutoActivation(true);
			assistant.setAutoActivationDelay(500);

			return assistant;
		}

		// The presenter instance for the information window
		// private static final DefaultInformationControl.IInformationPresenter presenter =
		// new DefaultInformationControl.IInformationPresenter() {
		// public String updatePresentation(
		// Display display,
		// String infoText,
		// TextPresentation presentation,
		// int maxWidth,
		// int maxHeight) {
		// int start = -1;
		// // Loop over all characters of information text
		// for (int i = 0; i < infoText.length(); i++) {
		// switch (infoText.charAt(i)) {
		// case '<' :
		// // Remember start of tag
		// start = i;
		// break;
		// case '>' :
		// if (start >= 0) {
		// // We have found a tag and create a new style range
		// StyleRange range =
		// new StyleRange(
		// start,
		// i - start + 1,
		// null,
		// null,
		// SWT.BOLD);
		// // Add this style range to the presentation
		// presentation.addStyleRange(range);
		// // Reset tag start indicator
		// start = -1;
		// }
		// break;
		// }
		// }
		// // Return the information text
		// return infoText;
		// }
		// };

		/*
		 * (non-Javadoc)
		 * @see
		 * org.eclipse.jface.text.source.SourceViewerConfiguration#getInformationControlCreator(org.eclipse.jface.text
		 * .source.ISourceViewer)
		 */
		// public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		// return new IInformationControlCreator() {
		// public IInformationControl createInformationControl(Shell parent) {
		// return new DefaultInformationControl(parent, presenter);
		// }
		// };
		// }

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
