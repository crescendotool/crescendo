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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.ui.DestecsUIPlugin;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.osgi.framework.Bundle;
import org.overture.config.Release;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.ModelBuildPath;
import org.overture.ide.vdmrt.core.IVdmRtCoreConstants;

public class DestecsNewWizard extends BasicNewProjectResourceWizard
{


	public DestecsNewWizard()
	{
		setWindowTitle(Messages.CrescendoNewWizard_0);

	}

	@Override
	public boolean performFinish()
	{
		boolean ok = super.performFinish();

		IProject prj = getNewProject();

		if (prj != null)
		{
			setDestecsSettings(prj);
			try
			{
				prj.touch(new NullProgressMonitor());
			} catch (CoreException e)
			{
				e.printStackTrace();
			}
		}

		return ok;
	}

	public static void setDestecsSettings(IProject prj)
	{
		try
		{

			addNature(prj, IVdmRtCoreConstants.NATURE);
			IVdmProject p = (IVdmProject) prj.getAdapter(IVdmProject.class);
			Assert.isNotNull(p, "Project could not be adapted"); //$NON-NLS-1$
			p.setBuilder(Release.DEFAULT);
			addNature(prj, IDestecsCoreConstants.NATURE);

			addBuilder(prj, "org.destecs.ide.vdmmetadatabuilder.builder", null, null); //$NON-NLS-1$
			addBuilder(prj, IDestecsCoreConstants.BUILDER_ID, null, null);
			addBuilder(prj, IDestecsCoreConstants.SCRIPT_BUILDER_ID, null, null);

			IDestecsProject dp = (IDestecsProject) prj.getAdapter(IDestecsProject.class);
			ModelBuildPath modelPath = p.getModelBuildPath();
			modelPath.add(dp.getVdmModelFolder());
			modelPath.remove(prj);
			modelPath.setOutput(dp.getOutputFolder());
			modelPath.setLibrary(dp.getVdmModelFolder().getFolder("lib")); //$NON-NLS-1$
			modelPath.save();
			p.getModel().clean();
			prj.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());


			IProjectDescription d = prj.getDescription();
			d.setComment(Messages.CrescendoNewWizard_4+getPlatformBundleVersion());
			prj.setDescription(d, new NullProgressMonitor());

		} catch (CoreException e)
		{
			DestecsUIPlugin.log("Failed to create project",e); //$NON-NLS-1$
		}

	}
	
	public static String getPlatformBundleVersion()
	{
		Bundle bundle = Platform.getBundle("org.destecs.ide.platform"); //$NON-NLS-1$
		if(bundle!=null)
		{
			String version = ""+bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$ //$NON-NLS-2$
			return version;
		}
		return ""; //$NON-NLS-1$
	}

	private static void addNature(IProject project, String nature)
			throws CoreException
	{

		if (!project.hasNature(nature))
		{
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = nature;
			description.setNatureIds(newNatures);

			IProgressMonitor monitor = null;
			project.setDescription(description, monitor);
		}
	}

	@SuppressWarnings("unchecked")
	public static void addBuilder(IProject project, String name,
			String argumentKey, String argumentValue) throws CoreException
	{
		Vector<ICommand> buildCommands = new Vector<ICommand>();
		boolean found = false;
		IProjectDescription description = project.getDescription();
		for (ICommand command : description.getBuildSpec())
		{
			buildCommands.add(command);
			if (command.getBuilderName().equals(name))
			{
				found = true;
				if (argumentKey != null && argumentValue != null)
				{

					@SuppressWarnings("rawtypes")
					Map arguments = command.getArguments();
					if (arguments == null)
						arguments = new HashMap<String, String>();

					if (arguments.containsKey(argumentKey))
						arguments.remove(argumentKey);

					arguments.put(argumentKey, argumentValue);

					command.setArguments(arguments);
				}

			}
		}

		if (!found)
		{
			ICommand newCommand = description.newCommand();
			newCommand.setBuilderName(name);
			if (argumentKey != null && argumentValue != null)
			{
				@SuppressWarnings("rawtypes")
				Map arguments = new HashMap<String, String>();
				arguments.put(argumentKey, argumentValue);
				newCommand.setArguments(arguments);
			}

			buildCommands.add(newCommand);

		}
		ICommand[] commands = new ICommand[buildCommands.size()];
		commands = buildCommands.toArray(commands);
		description.setBuildSpec(commands);

		project.setDescription(description, null);

	}

}
