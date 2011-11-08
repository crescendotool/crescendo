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
package org.destecs.ide.core.resources;

import org.destecs.ide.core.internal.core.resources.DestecsProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;



public class DestecsProjectAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		
		if(adapterType == IDestecsProject.class)
		{
			if(adaptableObject instanceof IProject){
				IProject project = (IProject) adaptableObject;
				if(DestecsProject.isDestecsProject(project))
				{
					return DestecsProject.createProject(project);
				}
			}
		}
	 	
		
		
		if(adapterType == IProject.class)
		{
			if(adaptableObject instanceof DestecsProject){
				DestecsProject project = (DestecsProject) adaptableObject;
				return project.project;
			}
		}
		
		
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[]{IDestecsProject.class, IProject.class};
	}

}
