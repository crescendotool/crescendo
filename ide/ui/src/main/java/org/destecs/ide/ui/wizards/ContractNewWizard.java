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

	@Override
	protected String getFileTemplate(String fileName)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("contract ");
		sb.append(fileName);
		sb.append("\n\n");
		sb.append("end ");
		sb.append(fileName);
		return sb.toString();
	}
}
