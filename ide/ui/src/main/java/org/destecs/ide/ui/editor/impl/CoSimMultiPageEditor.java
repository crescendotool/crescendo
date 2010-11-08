package org.destecs.ide.ui.editor.impl;

import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.destecs.ide.core.resources.IDestecsProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * An example showing how to create a multi-page editor. This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class CoSimMultiPageEditor extends MultiPageEditorPart implements
		IResourceChangeListener
{

	/** The text editor used in page 0. */
	private TextEditor editor;

	/** The font chosen in page 1. */
	

	/** The text widget used in page 2. */
	private StyledText text;

	
	final Map<IFile,Integer> handingFiles = new Hashtable<IFile,Integer>();
	/**
	 * Creates a multi-page editor example.
	 */
	public CoSimMultiPageEditor()
	{
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	public Set<IFile> getHandingFiles()
	{
		return this.handingFiles.keySet();
	}
	
	public void setActivePage(IFile file)
	{
		if(handingFiles.containsKey(file))
		{
			setActivePage(handingFiles.get(file));
		}
	}
	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	@SuppressWarnings("deprecation")
	void createPage0(IEditorInput input)
	{
		try
		{
			editor = new ContractEditor();
			int index = addPage(editor,input);
			// setPageText(index, editor.getTitle());
			setPageText(index, "Contract");
			setTitle(editor.getTitle());
		} catch (PartInitException e)
		{
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Creates page 1 of the multi-page editor, which allows you to change the font used in page 2.
	 */
	void createPage1(IEditorInput input)
	{
		try
		{
			VdmLinkEditor vdmLinkEditor = new VdmLinkEditor();
			int index = addPage(vdmLinkEditor, input);
			// setPageText(index, editor.getTitle());
			setPageText(index, "VDM Link");

		} catch (PartInitException e)
		{
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Creates page 2 of the multi-page editor, which shows the sorted text.
	 */
	void createPage2()
	{
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		text = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(false);

		int index = addPage(composite);
		setPageText(index, "20-Sim Link");
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages()
	{
		IFile contractFile=getProject().getContractFile();
		handingFiles.put(contractFile,0);
		createPage0(createEditorInput(contractFile));
		
		IFile vdmLinkFile=getProject().getVdmLinkFile();
		handingFiles.put(vdmLinkFile,1);
		createPage1(createEditorInput(vdmLinkFile));
		
		createPage2();
		
		IFile inputFile =(IFile) getEditorInput().getAdapter(IFile.class);
		
		if(inputFile.equals(getProject().getContractFile()))
		{
			setActivePage(0);
		}
		if(inputFile.equals(getProject().getVdmLinkFile()))
		{
			setActivePage(1);
		}
	}
	protected IEditorInput createEditorInput(IFile file)
	{
		if (!file.exists())
		{
			try
			{
				file.create(new ByteArrayInputStream("".getBytes()), true, null);
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new FileEditorInput(file);
	}
	
	protected IDestecsProject getProject()
	{
		IProject project = ((IFile) getEditorInput().getAdapter(IFile.class)).getProject();

		IDestecsProject dp = (IDestecsProject) project.getAdapter(IDestecsProject.class);
		return dp;
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this <code>IWorkbenchPart</code> method disposes all
	 * nested editors. Subclasses may extend.
	 */
	public void dispose()
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor)
	{
		for (int i = 0; i < 3; i++)
		{
			if (getEditor(i) != null)
			{
				getEditor(i).doSave(monitor);
			}
		}
		// getEditor(0).doSave(monitor);
	}
	
	@Override
	public boolean isDirty()
	{
		for (int i = 0; i < 3; i++)
		{
			if (getEditor(i) != null &&getEditor(i).isDirty())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the text for page 0's tab, and updates this
	 * multi-page editor's input to correspond to the nested editor's.
	 */
	public void doSaveAs()
	{
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker)
	{
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method checks that the input is an instance of
	 * <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException
	{
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);

	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex)
	{
		super.pageChange(newPageIndex);
		// if (newPageIndex == 2)
		// {
		// sortWords();
		// }
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event)
	{
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++)
					{
						if (((FileEditorInput) editor.getEditorInput()).getFile().getProject().equals(event.getResource()))
						{
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}




}
