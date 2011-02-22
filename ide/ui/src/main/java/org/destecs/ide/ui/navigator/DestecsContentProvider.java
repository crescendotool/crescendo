package org.destecs.ide.ui.navigator;

import java.util.ArrayList;

import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.ui.DestecsUIPlugin;
import org.destecs.ide.ui.IDestecsUiConstants;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.progress.UIJob;



public class DestecsContentProvider extends BaseWorkbenchContentProvider
		implements IResourceChangeListener, ITreeContentProvider {

	private TreeViewer fViewer;
	private static final Object[] NO_CHILDREN = {};

	private UIJob jobNavigatorRefresh = new UIJob(PlatformUI.getWorkbench().getDisplay(), "Navigator Update (DestecsNavigatorContent)")
	{
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor)
		{
			try
			{
				TreeViewer viewer = fViewer;
				if (!viewer.getControl().isDisposed())
				{
					viewer.refresh();
				}
				return Status.OK_STATUS;
			} catch (Exception e)
			{
				DestecsUIPlugin.log(e);
				return new Status(IStatus.ERROR, IDestecsUiConstants.PLUGIN_ID, "Error in Navigator Update (DestecsNavigatorContent)", e);
			}
		}
	};

	public void resourceChanged(IResourceChangeEvent event)
	{
		jobNavigatorRefresh.schedule();
	}
	

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		super.inputChanged(viewer, oldInput, newInput);

		fViewer = (TreeViewer) viewer;
		IWorkspace oldWorkspace = null;
		IWorkspace newWorkspace = null;

		if (oldInput instanceof IWorkspace)
		{
			oldWorkspace = (IWorkspace) oldInput;
		} else if (oldInput instanceof IContainer)
		{
			oldWorkspace = ((IContainer) oldInput).getWorkspace();
		}

		if (newInput instanceof IWorkspace)
		{
			newWorkspace = (IWorkspace) newInput;
		} else if (newInput instanceof IContainer)
		{
			newWorkspace = ((IContainer) newInput).getWorkspace();
		}

		if (oldWorkspace != newWorkspace)
		{
			if (oldWorkspace != null)
			{
				oldWorkspace.removeResourceChangeListener(this);
			}
			if (newWorkspace != null)
			{
				newWorkspace.addResourceChangeListener(this,
						IResourceChangeEvent.POST_CHANGE);
			}
		}

	}
	
	@Override
	public Object[] getElements(Object element)
	{
		ArrayList<IProject> result = new ArrayList<IProject>();

		IWorkbenchAdapter adapter = getAdapter(element);

		if (adapter != null)
		{
			Object[] children = adapter.getChildren(element);
			for (int i = 0; i < children.length; i++)
			{
				if (children[i] instanceof IProject)
				{
					IProject p = (IProject) children[i];
					if (!p.isOpen())
					{
						result.add(p);
					} else
					{

						IDestecsProject destecsProject = (IDestecsProject) ((IProject) children[i])
								.getAdapter(IDestecsProject.class);
						if (destecsProject != null)
						{
							result.add((IProject) children[i]);
						}
					}

				}

			}

			return result.toArray();
		}
		return new Object[0];
	}
	
	@Override
	public Object[] getChildren(Object element)
	{
		IWorkbenchAdapter adapter = getAdapter(element);

		if (adapter != null)
		{
			Object[] children = adapter.getChildren(element);
			return children;
			
		}
		return NO_CHILDREN;
	}
	
}
