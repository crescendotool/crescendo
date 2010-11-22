package org.destecs.ide.ui.wizards;

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
		return "// Time  [DT/CT].variable = value\n// 0.2    DT.enableFault1   =  1.0;\n";
	}
}
