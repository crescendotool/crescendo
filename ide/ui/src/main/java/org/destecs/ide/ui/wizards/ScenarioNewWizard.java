package org.destecs.ide.ui.wizards;

import org.eclipse.ui.INewWizard;

public class ScenarioNewWizard extends AbstractNewFileWizard implements
		INewWizard {

	public ScenarioNewWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "Scenario";
	}

	@Override
	public String getLocation() {
		return "scenarios";
	}

	@Override
	public String getFileExtension() {
		return "script";
	}

}
