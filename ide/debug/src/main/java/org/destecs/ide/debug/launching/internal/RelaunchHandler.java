package org.destecs.ide.debug.launching.internal;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class RelaunchHandler extends AbstractHandler implements IHandler
{

	private static final String DLAUNCH = ".dlaunch";

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection)
		{
			 IFile file =null;
			Object firstSelection = ((IStructuredSelection) selection).getFirstElement();
			if(firstSelection instanceof IContainer)
			{
				try
				{
					for (IResource member : ((IContainer)firstSelection).members())
					{
						if(member instanceof IFile && ((IFile)member).getName().endsWith(DLAUNCH))
						{
							file = (IFile) member;
							break;
						}
					}
				} catch (CoreException e)
				{
				}
			}else
			{
				 file = (IFile)firstSelection;
			}

			

			if (file != null && file.getName().endsWith(DLAUNCH))
			{
				ILaunchConfiguration launch;
				try
				{
					launch = LaunchStore.load(file.getContents());
					if (launch != null)
					{
						launch.launch(ILaunchManager.DEBUG_MODE, null);
					}
				} catch (FileNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		return null;
	}

}
