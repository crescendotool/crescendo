package org.destecs.ide.ui.editor.impl;

import org.destecs.core.parsers.ParserWrapper;
import org.destecs.core.parsers.VdmLinkParserWrapper;
import org.destecs.ide.ui.editor.core.BaseCodeScanner;
import org.destecs.ide.ui.editor.core.BaseReconcilingStrategy;
import org.destecs.ide.ui.editor.core.DestecsBaseSourceViewerConfiguration;
import org.destecs.ide.ui.editor.core.DestecsDocumentProvider;
import org.destecs.ide.ui.editor.syntax.DestecsColorProvider;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.ui.editors.text.TextEditor;

public class VdmLinkEditor extends TextEditor
{
	public class VdmLinkSourceViewerConfiguration extends
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

	}
	
	public class VdmLinkReconcilingStrategy extends BaseReconcilingStrategy
	{
		@SuppressWarnings("unchecked")
		@Override
		protected ParserWrapper getParser()
		{
			return new VdmLinkParserWrapper();
		}
	}

	public class VdmLinkCodeScanner extends BaseCodeScanner
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
			return new String[] { "vdm_outputs","vdm_inputs","vdm_events","vdm_sdp" };
		}

		@Override
		protected String[] getTypeWords()
		{
			return new String[] { };
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
