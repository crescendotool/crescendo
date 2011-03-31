package org.destecs.ide.ui.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.INewWizard;

public class ScenarioNewWizard extends AbstractNewFileWizard implements
		INewWizard
{

	@Override
	public String getName()
	{
		return "Scenario";
	}

	@Override
	public String getLocation()
	{
		return "scenarios";
	}

	@Override
	public String getFileExtension()
	{
		return "script";
	}

	@Override
	protected boolean isFileNameEditable()
	{
		return true;
	}
	
	@Override
	protected String getFileTemplate(String fileName)
	{
		return "// Time  [DE/CT].variable := value;\n// 0.2    DE.enableFault1   :=  1.0;\n";
	}

	@Override
	protected String getInitialFileName(IProject project)
	{
		return project.getName();
	}
}
