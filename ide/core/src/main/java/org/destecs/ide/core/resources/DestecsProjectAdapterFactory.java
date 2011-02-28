package org.destecs.ide.core.resources;

import org.destecs.ide.core.internal.core.resources.DestecsProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
