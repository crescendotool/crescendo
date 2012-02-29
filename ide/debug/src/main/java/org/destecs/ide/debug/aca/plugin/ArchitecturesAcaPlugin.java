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
package org.destecs.ide.debug.aca.plugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.aca.IAcaGeneratorPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class ArchitecturesAcaPlugin implements IAcaGeneratorPlugin
{

	public Collection<? extends ILaunchConfiguration> generate(
			ILaunchConfiguration configuration,
			final ILaunchConfiguration baseConfig,
			final Set<ILaunchConfiguration> configurations, IProject project,final String outputPreFix)
	{
		final Set<ILaunchConfiguration> results = new HashSet<ILaunchConfiguration>();

		String unparsedString;
		try
		{
			unparsedString = configuration.getAttribute(IDebugConstants.DESTECS_ACA_ARCHITECTURES, "");
			
			if(unparsedString.trim().length() == 0)
			{
				return new HashSet<ILaunchConfiguration>(configurations);
			}
			
			String[] architectures = unparsedString.split(";");
			
			List<String> architecturesList = Arrays.asList(architectures);
			
			
			IResource folder = project.findMember(new Path("model_de/architectures"));
			

			if (folder != null && folder.exists() && folder instanceof IFolder)
			{
				if(!hasArchFiles((IFolder) folder))
				{
					return new HashSet<ILaunchConfiguration>(configurations);
				}
				
				for (IResource iResource : ((IFolder)folder).members()) {
					if(iResource instanceof IFile)
					{
						if(architecturesList.contains(((IFile)iResource).getName()))
						{
							for (ILaunchConfiguration iLaunchConf : configurations)
							{
								ILaunchConfigurationWorkingCopy copy = iLaunchConf.getWorkingCopy().copy(iLaunchConf.getName());
								copy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_DE_ARCHITECTURE, iResource.getProjectRelativePath().toString());
								copy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, outputPreFix);
								results.add(copy);
							}
						}
					}
				}
				
			}

		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	private boolean hasArchFiles(IFolder folder)
	{
		try
		{
			for (IResource resource : folder.members())
			{
				if(resource.getFileExtension()!=null && resource.getFileExtension().equals("arch"))
					return true;
			}
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	

}
