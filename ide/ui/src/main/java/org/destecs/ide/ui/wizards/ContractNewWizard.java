package org.destecs.ide.ui.wizards;
import org.eclipse.ui.INewWizard;


public class ContractNewWizard extends AbstractNewFileWizard implements INewWizard {

	

	@Override
	public String getFileExtension() {
		return "csc";
	}

	@Override
	public String getName() {		
		return "Contract";
	}

	@Override
	public String getLocation() {
		return "configuration";
	}

}
