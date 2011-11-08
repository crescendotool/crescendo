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
		@SuppressWarnings("rawtypes")
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
