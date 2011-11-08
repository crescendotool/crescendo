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
