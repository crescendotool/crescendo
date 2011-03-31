package org.destecs.ide.ui.wizards;
import org.eclipse.core.resources.IProject;
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

	@Override
	protected String getFileTemplate(String fileName)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("-- Shared Design Parameters \n");
		sb.append("-- sdp real MAXLEVEL;");
		sb.append("\n\n");
		sb.append("-- Monitored variables\n");
		sb.append("-- monitored real level := 0.0;");
		sb.append("\n\n");
		sb.append("-- Controlled variables\n");
		sb.append("-- controlled real valve := 0.0;");
		sb.append("\n\n");
		sb.append("-- Events\n");
		sb.append("-- event HIGH;");
		sb.append("\n\n");
		return sb.toString();
	}

	@Override
	protected String getInitialFileName(IProject project)
	{
		return "contract";
	}
}
