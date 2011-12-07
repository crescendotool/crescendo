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

	public void reconcile(IRegion partition)
	{
		if (currentDocument != null)
		{
			@SuppressWarnings("rawtypes")
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
//						FileUtility.addMarker(currentDocument.getFile(), e.getMessage(), e.getLine()+1, e.getCharPositionInLine(), IMarker.SEVERITY_ERROR, currentDocument.get());
						FileUtility.addMarker(currentDocument.getFile(),  e.getMessage(), e.getLine()+1, IMarker.SEVERITY_ERROR);
					}
				}

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes")
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
