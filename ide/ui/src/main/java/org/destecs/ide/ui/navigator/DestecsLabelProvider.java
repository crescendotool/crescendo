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
package org.destecs.ide.ui.navigator;

import org.destecs.ide.ui.DestecsPluginImages;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.overture.ide.core.resources.IVdmProject;

@SuppressWarnings("restriction")
public class DestecsLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {

	private ResourceManager resourceManager;
	
	public String getDescription(Object anElement) {
		if (anElement instanceof IResource) {
			return ((IResource) anElement).getFullPath().makeRelative().toString();
		}
		return null;
	}

	@Override
    public final Image getImage(Object element) {
				
		
        //obtain the base image by querying the element
        IWorkbenchAdapter adapter = getAdapter(element);
        if (adapter == null) {
            return null;
        }
        ImageDescriptor descriptor = adapter.getImageDescriptor(element);
        if (descriptor == null) {
            return null;
        }

      //Adds package icon to folder if the folder is in the build path
        if(element instanceof IFolder)
        {
        	IFolder folder = (IFolder) element;
        	IVdmProject project = (IVdmProject) folder.getProject().getAdapter(IVdmProject.class);
        	if(project.getModelBuildPath().contains(folder))
        	{
        		descriptor = DestecsPluginImages.getDescriptor(DestecsPluginImages.IMG_OBJS_PACKFRAG_ROOT);
        	}else if(project.getModelBuildPath().getOutput()!=null && project.getModelBuildPath().getOutput().equals(folder))
        	{
        		descriptor = DestecsPluginImages.getDescriptor(DestecsPluginImages.IMG_OBJS_CLASSFOLDER);
        	}
        }

        //add any annotations to the image descriptor
        descriptor = decorateImage(descriptor, element);

		return (Image) getResourceManager().get(descriptor);
    }

	
	protected final IWorkbenchAdapter getAdapter(Object o) {
        return (IWorkbenchAdapter)Adapters.adapt(o, IWorkbenchAdapter.class);
    }
	
	/**
     * Returns a label that is based on the given label,
     * but decorated with additional information relating to the state
     * of the provided object.
     *
     * Subclasses may implement this method to decorate an object's
     * label.
     * @param input The base text to decorate.
     * @param element The element used to look up decorations.
     * @return the resulting text
     */
    protected String decorateText(String input, Object element) {
        return input;
    }

    /**
     * Returns an image descriptor that is based on the given descriptor,
     * but decorated with additional information relating to the state
     * of the provided object.
     *
     * Subclasses may reimplement this method to decorate an object's
     * image.
     * 
     * @param input The base image to decorate.
     * @param element The element used to look up decorations.
     * @return the resuling ImageDescriptor.
     * @see org.eclipse.jface.resource.CompositeImageDescriptor
     */
    protected ImageDescriptor decorateImage(ImageDescriptor input,
            Object element) {
        return input;
    }
    
    /**
	 * Lazy load the resource manager
	 * 
	 * @return The resource manager, create one if necessary
	 */
	private ResourceManager getResourceManager() {
		if (resourceManager == null) {
			resourceManager = new LocalResourceManager(JFaceResources
					.getResources());
		}

		return resourceManager;
	}
	
	@Override
	public String getText(Object element) {
				
		
		//query the element for its label
        IWorkbenchAdapter adapter = getAdapter(element);
        if (adapter == null) {
            return ""; //$NON-NLS-1$
        }
        String label = adapter.getLabel(element);

        //return the decorated label
        return decorateText(label, element);
	}
}
