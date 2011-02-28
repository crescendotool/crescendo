package org.destecs.ide.core.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class DestecsProjectNature implements IProjectNature {

	protected IProject project = null;
	
	public IProject getProject() {		
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;

	}

	public void configure() throws CoreException {
		// TODO Auto-generated method stub
		
	 }

	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub
		
	}

}
