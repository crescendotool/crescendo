package org.destecs.ide.contracteditor.editor.core;

import org.destecs.ide.contracteditor.editor.syntax.ContractColorProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
//import org.overture.ide.ui.editor.core.VdmReconcilingStrategy;
//import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;
//
//import org.overture.ide.ui.editor.partitioning.VdmPartitionScanner;
//import org.overture.ide.ui.editor.syntax.VdmColorProvider;

public class DestecsContractSourceViewerConfiguration extends
SourceViewerConfiguration
{
	private String[] commentingPrefix = new String[] { "--","//" };
	public final static String SINGLELINE_COMMENT = "__contract_singleline_comment";

	private ITokenScanner vdmCodeScanner = null;
	PresentationReconciler reconciler = null;
	
	

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer)
	{
		return null;
	}

	@Override
	public String[] getDefaultPrefixes(ISourceViewer sourceViewer,
			String contentType)
	{
		if (contentType.equals(IDocument.DEFAULT_CONTENT_TYPE))
		{
			return commentingPrefix;
		}
		if (contentType.equals(SINGLELINE_COMMENT))
		{
			return commentingPrefix;
		}

		return super.getDefaultPrefixes(sourceViewer, contentType);
	}
	
	public final static String MULTILINE_COMMENT = "__vdm_multiline_comment";
	
	public final static String STRING = "__vdm_string";
	public final static String LATEX = "__vdm_latex";

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer)
	{
		if (reconciler == null)
		{
			reconciler = new PresentationReconciler();

			if (vdmCodeScanner == null)
			{
				vdmCodeScanner = getCodeScanner();
			}

			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(vdmCodeScanner);
			reconciler.setDamager(dr, SINGLELINE_COMMENT);
			reconciler.setRepairer(dr, SINGLELINE_COMMENT);

			dr = new DefaultDamagerRepairer(getCodeScanner());
			reconciler.setDamager(dr, MULTILINE_COMMENT);
			reconciler.setRepairer(dr, MULTILINE_COMMENT);

			dr = new DefaultDamagerRepairer(getCodeScanner());
			reconciler.setDamager(dr, STRING);
			reconciler.setRepairer(dr, STRING);

			dr = new DefaultDamagerRepairer(getCodeScanner());
			reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		}
		return reconciler;
	}
	
	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer)
	{
		MonoReconciler reconciler = new MonoReconciler(new ContractReconcilingStrategy(), false);
		reconciler.install(sourceViewer);

		return reconciler;
	}
	
	protected ITokenScanner getCodeScanner()
	{
		return new DestecsContractCodeScanner(new ContractColorProvider());
	}
}
