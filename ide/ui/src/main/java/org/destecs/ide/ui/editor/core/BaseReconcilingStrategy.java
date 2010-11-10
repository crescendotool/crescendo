package org.destecs.ide.ui.editor.core;

import java.io.IOException;

import org.destecs.core.parsers.IError;
import org.destecs.core.parsers.ParserWrapper;
import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.utility.FileUtility;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;

public abstract class BaseReconcilingStrategy implements IReconcilingStrategy
{
	private DestecsDocument currentDocument;

	@SuppressWarnings("unchecked")
	public void reconcile(IRegion partition)
	{
		if (currentDocument != null)
		{
			ParserWrapper parser = getParser();
			FileUtility.deleteMarker(currentDocument.getFile(), IMarker.PROBLEM, IDestecsCoreConstants.PLUGIN_ID);
			try
			{
				parser.parse(currentDocument.getFile().getLocation().toFile(), currentDocument.get());

				if (parser.hasErrors())
				{
					for (Object err : parser.getErrors())
					{
						IError e = (IError) err;
						System.out.println(e);
						FileUtility.addMarker(currentDocument.getFile(), e.getMessage(), e.getLine(), e.getCharPositionInLine(), IMarker.SEVERITY_ERROR, currentDocument.get());
					}
				}

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected abstract ParserWrapper getParser();

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{

	}

	public void setDocument(IDocument document)
	{
		if (document instanceof DestecsDocument)
		{
			currentDocument = (DestecsDocument) document;
		}
	}

}
