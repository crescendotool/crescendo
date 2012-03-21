package org.destecs.ide.libraries.handlers;

import java.util.Set;

import org.destecs.ide.libraries.DestecsLibraryPlugin;
import org.destecs.ide.libraries.store.LibStore;
import org.destecs.ide.libraries.store.Library;
import org.destecs.ide.libraries.util.LibraryUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class UpgradeLibraryLinkHandler extends AbstractHandler implements
		IHandler
{
private Set<Library> libraries = null;
	private IResourceVisitor visitor = new IResourceVisitor()
	{

		public boolean visit(IResource resource) throws CoreException
		{
			if(resource.isLinked() && resource.getLocation().toFile()!=null && !resource.getLocation().toFile().exists())
			{
				upgradeLibraryLink(resource);
				return false;
			}
			return true;
		}
	};

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection)
		{

			final IContainer c = (IContainer) ((IStructuredSelection) selection).getFirstElement();

			final IProject project = c.getProject();

			if (project != null)
			{
				try
				{
					project.accept(visitor);
					project.refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (CoreException e)
				{
					DestecsLibraryPlugin.log("Failed to upgrade project in action", e);
				}
			}

		}
		return null;
	}

	protected void upgradeLibraryLink(IResource resource)
	{
		if(libraries == null)
		{
			libraries = new LibStore().getLibraries();
		}
		
		String name = resource.getName();
		String path = resource.getProjectRelativePath().removeLastSegments(1).toString();
		if(!libraries.isEmpty())
		{
			try{
			for (Library lib : libraries)
			{
				if(lib.deFiles.contains(name) || lib.ctFiles.contains(name))
				{
					LibraryUtil.updateLink(resource,lib,name,path,false);
				}
			}
			}catch(CoreException e)
			{
				DestecsLibraryPlugin.log("Failed to update linked library file", e);
			}
		}
		
	}


}