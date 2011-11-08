/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
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
