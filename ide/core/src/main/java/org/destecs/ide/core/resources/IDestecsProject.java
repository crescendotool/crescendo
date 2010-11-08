package org.destecs.ide.core.resources;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

public interface IDestecsProject extends IAdaptable
{
	IFolder getVdmModelFolder();
	
	IFile getVdmLinkFile();
	
	IFile getContractFile();

	IFolder getScenarioFolder();

	List<IFile> getScenarioFiles() throws CoreException;

	IFile getSharedDesignParameterFile();
	
	IFolder getOutputFolder();
	
	void createStructure();
}
