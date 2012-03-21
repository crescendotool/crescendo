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
package org.destecs.ide.libraries;

import org.destecs.ide.libraries.util.LibraryUtil;
import org.destecs.ide.libraries.wizard.LibraryIncludePage;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class AddLibraryWizard extends Wizard implements IWorkbenchWizard
{
	private static final String WIZARD_NAME = "Add Library Wizard";
	private IProject project = null;
	private LibraryIncludePage _pageTwo;

	public AddLibraryWizard()
	{
		setWindowTitle(WIZARD_NAME);
	}

	@Override
	public boolean performFinish()
	{
		try
		{
			LibraryUtil.createSelectedLibraries(project, _pageTwo.getLibrarySelection());
		} catch (CoreException e)
		{
			DestecsLibraryPlugin.log(e);
			return false;
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{

		if (selection.getFirstElement() instanceof IResource)
		{
			IProject project = (IProject) ((IResource) selection.getFirstElement()).getProject();
			this.project = project;
		}

	}

	@Override
	public void addPages()
	{
		_pageTwo = new LibraryIncludePage("Add Library");
		addPage(_pageTwo);
	}

}
