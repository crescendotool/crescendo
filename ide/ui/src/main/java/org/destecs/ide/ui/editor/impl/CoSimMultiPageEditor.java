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

import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.destecs.core.contract.Contract;
import org.destecs.core.contract.IVariable;
import org.destecs.core.vdmlink.LinkInfo;
import org.destecs.core.vdmlink.Links;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.core.utility.ParserUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;


/**
 * An example showing how to create a multi-page editor. This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
@SuppressWarnings("deprecation")
public class CoSimMultiPageEditor extends MultiPageEditorPart implements
		IResourceChangeListener
{

	/** The text editor used in page 0. */
	private TextEditor editor;

	/** The font chosen in page 1. */

	/** The text widget used in page 2. */
	private StyledText text;

	/** The text widget used in page 3. */
	private StyledText text3;

	final Map<IFile, Integer> handingFiles = new Hashtable<IFile, Integer>();

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
		if (handingFiles.containsKey(file))
		{
			setActivePage(handingFiles.get(file));
		}
	}

	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	void createPage0(IEditorInput input)
	{
		try
		{
			editor = new ContractEditor();
			int index = addPage(editor, input);
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

	void createPage3()
	{
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		text3 = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL|SWT.READ_ONLY);
		text3.setEditable(false);

		int index = addPage(composite);
		setPageText(index, "Overview");
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme currentTheme = themeManager.getCurrentTheme();

		FontRegistry fontRegistry = currentTheme.getFontRegistry();
		text3.setFont( fontRegistry.get(JFaceResources.TEXT_FONT));
		text3.addFocusListener(new FocusListener()
		{

			public void focusLost(FocusEvent e)
			{
			}

			public void focusGained(FocusEvent e)
			{
				text3.setText(getOverviewDescription());
			}
		});
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages()
	{
		if (getProject() == null || getProject().getContractFile() == null)
		{
			return;
		}
		IFile contractFile = getProject().getContractFile();
		handingFiles.put(contractFile, 0);
		createPage0(createEditorInput(contractFile));

		IFile vdmLinkFile = getProject().getVdmLinkFile();
		handingFiles.put(vdmLinkFile, 1);
		
		if(!vdmLinkFile.exists())
		{
			createPage1(createEditorInput(vdmLinkFile));
			createVdmLinkInitialTemplate(vdmLinkFile);
		}
		else
		{
			createPage1(createEditorInput(vdmLinkFile));
		}
		
		createPage2();

		createPage3();

		IFile inputFile = (IFile) getEditorInput().getAdapter(IFile.class);

		if (inputFile.equals(getProject().getContractFile()))
		{
			setActivePage(0);
		}
		if (inputFile.equals(getProject().getVdmLinkFile()))
		{
			setActivePage(1);
		}
	}
	
	
	protected void createVdmLinkInitialTemplate(IFile vdmLinkFile)
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("-- Linking of Shared Design Parameters\n");
		sb.append("--sdp maxlevel=Controller.maxLevel;\n");
		sb.append("--sdp minlevel=Controller.minLevel;\n\n");
		
		sb.append("-- Linking of Monitored Variables\n");
		sb.append("--input level=System.levelSensor.level;\n\n");
		
		sb.append("-- Linking of Controlled Variables\n");
		sb.append("--output valve=System.valveActuator.valveState;\n\n");
		
		sb.append("-- Linking of Events\n");
		sb.append("--event HIGH=System.eventHandler.high;\n\n");
				
		sb.append("-- other linked names used in scenarios\n");
		sb.append("--model fault=levelSensor.fault\n");
				
				
		try
		{
			vdmLinkFile.setContents(new StringBufferInputStream(sb.toString()), true, true, new NullProgressMonitor());
		} catch (CoreException e)
		{
			//DO nothing
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
			if (getEditor(i) != null && getEditor(i).isDirty())
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

	@Override
	public String getTitle()
	{
		return "Configuration Editor";
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

	public String getOverviewDescription()
	{
		IDestecsProject p = getProject();
		StringBuilder sb = new StringBuilder();
		try
		{
			if (p != null)
			{
				Contract contract = ParserUtil.getContract(p, null);
				Links vdmLinks = ParserUtil.getVdmLinks(p, null);

				sb.append("--- \n");
				sb.append("--- Shared Design Parameters ---\n");
				sb.append("--- \n");
				for (IVariable sdp : contract.getSharedDesignParameters())
				{
					LinkInfo info = vdmLinks.getBoundVariableInfo(sdp.getName());
					String id = "?";
					if (info != null)
					{
						id = info.getQualifiedNameString();
					}
					sb.append(id + " <-> " + sdp.getName() + " <-> "
							+ sdp.getName() + " not checked\n");
				}

				sb.append("\n\n");
				sb.append("--- \n");
				sb.append("--- Monitored Variables ---\n");
				sb.append("--- \n");
				for (IVariable sdp : contract.getMonitoredVariables())
				{
					LinkInfo info = vdmLinks.getBoundVariableInfo(sdp.getName());
					String id = "?";
					if (info != null)
					{
						id = info.getQualifiedNameString();
					}
					sb.append(id + " <-> " + sdp.getName() + " <-> "
							+ sdp.getName() + " not checked\n");
				}
				sb.append("\n\n");
				sb.append("--- \n");
				sb.append("--- Controlled Variables ---\n");
				sb.append("--- \n");
				for (IVariable sdp : contract.getControlledVariables())
				{
					LinkInfo info = vdmLinks.getBoundVariableInfo(sdp.getName());
					String id = "?";
					if (info != null)
					{
						id = info.getQualifiedNameString();
					}
					sb.append(id + " <-> " + sdp.getName() + " <-> "
							+ sdp.getName() + " not checked\n");
				}
				sb.append("\n\n");
				sb.append("--- \n");
				sb.append("--- Events ---\n");
				sb.append("--- \n");
				for (String event : contract.getEvents())
				{
					LinkInfo info = vdmLinks.getBoundVariableInfo(event);
					String id = "?";
					if (info != null)
					{
						id = info.getQualifiedNameString();
					}
					sb.append(id + " <-> " + event + " <-> not supported\n");
				}
			}
		} catch (Exception e)
		{
			sb.append("No info avaliable");
		}
		return sb.toString();
	}

}
