//package org.destecs.ide.ui.wizards;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//
//import org.destecs.ide.ui.DestecsUIPlugin;
//import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.jface.wizard.Wizard;
//import org.eclipse.ui.INewWizard;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
//import org.eclipse.ui.ide.IDE;
//import org.eclipse.ui.internal.UIPlugin;
//
//
//public abstract class AbstractDestecsWizard extends Wizard implements INewWizard {
//
//	
//	
//	private WizardNewFileCreationPage _pageOne;
//	private String fPageName;
//	private String fPageTitle;
//	private String fPageDescription;
//	private IStructuredSelection fStructuredSelection;
//	
//	
//	public AbstractDestecsWizard() {
//		setWindowTitle(getWizardName());
//		this.fPageName = getPageName();
//		this.fPageTitle = getPageTitle();
//		this.fPageDescription = getPageDescription();
//	}
//
//	public void init(IWorkbench workbench, IStructuredSelection selection) {
//		this.fStructuredSelection = selection;
//		
//		
//	}
//
//	@Override
//	public void addPages()
//	{
//		super.addPages();
//		_pageOne = new WizardNewFileCreationPage(this.fPageName,
//				this.fStructuredSelection);
//		_pageOne.setTitle(this.fPageTitle);
//		_pageOne.setDescription(this.fPageDescription);
//		
//
//		addPage(_pageOne);
//
//	}
//	
//	/*
//	 * Gets the main page name
//	 */
//	protected abstract String getPageName();
//
//	/*
//	 * Gets the main page title to be displayed
//	 */
//	protected abstract String getPageTitle();
//
//	/*
//	 * Gets the main page description
//	 */
//	protected abstract String getPageDescription();
//
//	/*
//	 * Gets the file extension of the file to create
//	 */
//	protected abstract String getFileExtension();
//	
//	protected abstract String getWizardName();
//	
//	@Override
//	public boolean canFinish()
//	{	
//		return super.canFinish() && _pageOne.getErrorMessage() == null;
//	}
//
//	@SuppressWarnings("restriction")
//	@Override
//	public boolean performFinish()
//	{
//		_pageOne.setFileExtension(getFileExtension());
//		IFile file = _pageOne.createNewFile();
//		if (file.exists())
//		{
//			String fileName = file.getName();
//			if(fileName.contains("."))
//			{
//				fileName = fileName.substring(0,fileName.indexOf("."));
//			}
//			String fileTemplate = null;//getFileTemplate(fileName);
//			if (fileTemplate != null)
//			{
//				applyTemplate(file, fileTemplate);
//			}
//			
//		}
//		try
//		{
//			IDE.openEditor(UIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage(), file, true);
//			file.touch(null);
//			file.refreshLocal(IResource.DEPTH_ONE, null);
//		} catch (CoreException e)
//		{
//			DestecsUIPlugin.log(e);
//			
//		}
//		return true;
//	}
//
//	private void applyTemplate(IFile file, String fileTemplate)
//	{
//		InputStream stream;
//		try
//		{
//			stream = new ByteArrayInputStream(fileTemplate.getBytes());
//			file.setContents(stream, IFile.FORCE, null);
//		} catch (CoreException e)
//		{
//			DestecsUIPlugin.log(e);
//
//		}
//
//	}
//	
//}
