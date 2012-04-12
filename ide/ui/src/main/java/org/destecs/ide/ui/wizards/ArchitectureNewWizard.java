package org.destecs.ide.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.INewWizard;

public class ArchitectureNewWizard extends AbstractNewFileWizard implements INewWizard {

	@Override
	public String getName() {
		return "Architecture";
	}

	@Override
	public String getLocation() {
		return "model_de/architectures";
	}

	@Override
	public String getFileExtension() {
		return "arch";
	}

	@Override
	protected String getInitialFileName(IProject project) {
		return "newArch";
	}

	@Override
	protected boolean isFileNameEditable()
	{
		return true;
	}
	
	@Override
	protected String getFileTemplate(String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append("-- ## Architecture ## -- \n");
		sb.append("//Add CPUs and bus topology here \n\n");
		sb.append("-- ## Deployment ## --\n");
		sb.append("//Add deployment of instances here");
		
		return sb.toString();
	}
	

}
