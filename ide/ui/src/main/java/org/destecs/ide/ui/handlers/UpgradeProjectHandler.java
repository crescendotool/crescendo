package org.destecs.ide.ui.handlers;

import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.ui.DestecsUIPlugin;
import org.destecs.ide.ui.IDestecsUiConstants;
import org.destecs.ide.ui.wizards.DestecsNewWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;

public class UpgradeProjectHandler extends AbstractHandler implements IHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection)
		{

			final IContainer c = (IContainer) ((IStructuredSelection) selection).getFirstElement();

			final IProject project = c.getProject();

			if ((IDestecsProject) project.getAdapter(IDestecsProject.class) != null)
			{
				try
				{
					String comment = project.getDescription().getComment();
					if (comment == null
							|| !comment.equals(DestecsNewWizard.getPlatformBundleVersion()))
					{
						DestecsNewWizard.setDestecsSettings(project);
					}
				} catch (CoreException e)
				{
					DestecsUIPlugin.log("Failed to upgrade project in action", e);
				}

				ICommandService cmdService = (ICommandService) HandlerUtil.getActiveSite(event).getService(ICommandService.class);
				Command upgradeCommand = cmdService.getCommand(IDestecsUiConstants.UPGRADE_LIBRARY_COMMANDID);
				if (upgradeCommand.isDefined())
				{
					try
					{
						upgradeCommand.executeWithChecks(event);
					} catch (Exception e)
					{
						DestecsUIPlugin.log("Could not execute library upgrade command", e);
					}
				}
			}

		}
		return null;
	}

}