package org.destecs.ide.ui.wizards;

import org.eclipse.ui.INewWizard;

public class NewContractWizard extends AbstractDestecsWizard implements
		INewWizard {

	public NewContractWizard() {
		super();
	}

	@Override
	protected String getPageName() {
		return "Contract Wizard";
	}

	@Override
	protected String getPageTitle() {
		return "Contract file creation wizard";
	}

	@Override
	protected String getPageDescription() {
		return "Chose a name for the contract file.";
	}

	@Override
	protected String getFileExtension() {
		return "csc";
	}

	@Override
	protected String getWizardName() {
		return "DESTECS New Contract Wizard";
	}

	
	
}
