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
package org.destecs.ide.ui.editor.core;

import org.destecs.ide.ui.editor.syntax.DestecsColorProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


public abstract class DestecsBaseSourceViewerConfiguration extends
SourceViewerConfiguration
{
	private String[] commentingPrefix = new String[] { "--","//" };
	public final static String SINGLELINE_COMMENT = "__contract_singleline_comment";

	private ITokenScanner vdmCodeScanner = null;
	PresentationReconciler reconciler = null;
	
	
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
		MonoReconciler reconciler = new MonoReconciler(getReconcilingStrategy(), false);
		reconciler.install(sourceViewer);

		return reconciler;
	}
	
	protected abstract IReconcilingStrategy getReconcilingStrategy();
	
	protected ITokenScanner getCodeScanner()
	{
		return getCodeScaner(new DestecsColorProvider());
	}
	
	protected abstract ITokenScanner getCodeScaner(DestecsColorProvider colorProvider);
	
	/*
	 * @see SourceViewerConfiguration#getAnnotationHover(ISourceViewer)
	 */
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer)
	{

		return new DestecsAnnotationHover(false);
	}
}
