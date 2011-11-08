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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;

public class DestecsDocumentProvider extends FileDocumentProvider
{
	@Override
	protected IDocument createDocument(Object element) throws CoreException
	{

		IDocument document = super.createDocument(element);

		if (element instanceof FileEditorInput)
		{
			if (document instanceof DestecsDocument)
			{
				IFile file = ((FileEditorInput) element).getFile();
				((DestecsDocument)document).setFile(file);
//
//				IVdmProject vdmProject = (IVdmProject) file.getProject().getAdapter(IVdmProject.class);
//
//				Assert.isNotNull(vdmProject, "Project of file: "
//						+ file.getName() + " is not VDM");
//
//				if (vdmProject != null)
//				{
//
//					IVdmSourceUnit source = vdmProject.findSourceUnit(file);
//					((VdmDocument) document).setSourceUnit(source);
//				}
//
			}
		}

//		if (document instanceof IDocumentExtension3)
//		{
//			IDocumentExtension3 extension3 = (IDocumentExtension3) document;
//			IDocumentPartitioner partitioner = new VdmDocumentPartitioner(VdmUIPlugin.getDefault().getPartitionScanner(), VdmPartitionScanner.PARTITION_TYPES);
//			extension3.setDocumentPartitioner(VdmUIPlugin.VDM_PARTITIONING, partitioner);
//			partitioner.connect(document);
//		}

		return document;
	}

	@Override
	protected IDocument createEmptyDocument()
	{
		return new DestecsDocument();
	}
}
