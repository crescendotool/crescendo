package org.destecs.ide.contracteditor.editor.core;

import java.io.IOException;

import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.parsers.IError;
import org.destecs.ide.contracteditor.Activator;
import org.destecs.ide.core.DestecsCorePlugin;
import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.utility.FileUtility;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
//import org.overture.ide.core.VdmCore;
//
//import org.overture.ide.core.parser.ISourceParser;
//import org.overture.ide.core.parser.SourceParserManager;
//import org.overture.ide.core.resources.IVdmProject;
//import org.overture.ide.core.utility.FileUtility;

public class ContractReconcilingStrategy implements IReconcilingStrategy
{

	private ContractDocument currentDocument;

	// private ContentOutline outline =null;

	public ContractReconcilingStrategy()
	{

		// IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench()
		// .getActiveWorkbenchWindow();

		// if (activeWorkbenchWindow != null)
		// {
		// IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		// if (activePage != null)
		// {
		// IViewPart outlineCandidate =
		// activePage.findView(IPageLayout.ID_OUTLINE);
		// if (outlineCandidate instanceof ContentOutline)
		// {
		// outline = (ContentOutline) outlineCandidate;
		// }
		// }
		// }
	}

	public void reconcile(IRegion partition)
	{
		if (DestecsCorePlugin.DEBUG)
		{
			// System.out.println("reconcile(IRegion partition)");
			// System.out.println("File: "
			// + (currentDocument).getFile().toString());
			// if(outline != null)
			// {
			// VdmContentOutlinePage page = (VdmContentOutlinePage)
			// outline.getCurrentPage();
			//				
			// }
		}

		if (currentDocument != null)
		{
			ContractParserWrapper parser = new ContractParserWrapper();
			FileUtility.deleteMarker(currentDocument.getFile(), IMarker.PROBLEM, IDestecsCoreConstants.PLUGIN_ID);
			try
			{
				parser.parse(currentDocument.getFile().getLocation().toFile(), currentDocument.get());

				if (parser.hasErrors())
				{
					for (IError e : parser.getErrors())
					{
						FileUtility.addMarker(currentDocument.getFile(), e.getMessage(), e.getLine()+1, e.getCharPositionInLine(), IMarker.SEVERITY_ERROR);
					}
				}

			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// try
		// {
		//
		// IVdmProject vdmProject = (IVdmProject) currentDocument.getProject()
		// .getAdapter(IVdmProject.class);
		//
		// if (currentDocument.getSourceUnit() != null && vdmProject != null)
		// {
		//
		// ISourceParser parser = SourceParserManager.getInstance()
		// .getSourceParser(vdmProject);
		//
		// if (parser != null)
		// {
		// parser.parse(currentDocument.getSourceUnit(),
		// currentDocument.get());
		// }
		// //Setting type checked to false after some alteration
		// vdmProject.getModel().setChecked(false);
		// }
		//
		// } catch (CoreException e)
		// {
		// if (VdmCore.DEBUG)
		// e.printStackTrace();
		// }
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
	{

	}

	public void setDocument(IDocument document)
	{
		if (document instanceof ContractDocument)
		{
			currentDocument = (ContractDocument) document;
		}
	}

}
