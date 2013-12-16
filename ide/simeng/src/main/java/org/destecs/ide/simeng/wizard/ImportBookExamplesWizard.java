package org.destecs.ide.simeng.wizard;

import java.io.IOException;

import org.destecs.ide.simeng.Activator;
import org.destecs.ide.simeng.ISimengConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.overture.ide.ui.wizard.pages.WizardProjectsImportPageProxy;


public class ImportBookExamplesWizard extends Wizard implements IImportWizard
{
	private WizardProjectsImportPageProxy importPageProxy = new WizardProjectsImportPageProxy();

	public ImportBookExamplesWizard()
	{

	}


	@Override
	public void createPageControls(Composite pageContainer)
	{
		super.createPageControls(pageContainer);
		try
		{
			this.importPageProxy.setBundleRelativeInputPath(ISimengConstants.PLUGIN_ID, "examples/book-examples.zip");
		} catch (IOException e)
		{
			Activator.log("Failed to get path for embedded exmaples.zip",e);
		}
		this.importPageProxy.createPageControlsPostconfig();

	}

	@Override
	public void addPages()
	{
		super.addPages();
		addPage(this.importPageProxy.getPage());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection)
	{

	}

	@Override
	public boolean performFinish()
	{
		this.importPageProxy.performFinish();
		return true;
	}

	@Override
	public boolean performCancel()
	{
		this.importPageProxy.performCancel();
		return true;
	}

}
