package org.destecs.ide.debug.octave;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExecuteOctaveHandler extends AbstractHandler implements IHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection instanceof IStructuredSelection)
		{
			IFile file = null;
			Object firstSelection = ((IStructuredSelection) selection).getFirstElement();
			if (firstSelection instanceof IFile && ((IFile) firstSelection).getName().equals(IDebugConstants.OCTAVE_PLOT_FILE))
			{
				file = (IFile) firstSelection;
				IPreferenceStore store = DestecsDebugPlugin.getDefault().getPreferenceStore();
				String path = store.getString(IDebugConstants.OCTAVE_PATH);
				boolean valid =path!=null && path.length() > 0;
				if (valid)
				{
					valid = new File(path).exists();
				}
				if (!valid)
				{
					MessageDialog.openError(HandlerUtil.getActiveShell(event), "Octave path not valid", "The Octave path provided in the preference page is not valid");
					path = null;
					return null;
				}
				
				List<String> commandArgs = new ArrayList<String>();
				commandArgs.add("cmd.exe");
				commandArgs.add("/K");
				commandArgs.add("start");
				commandArgs.add("Octave Console");
				commandArgs.add("/D");
				commandArgs.add(file.getParent().getLocation().toFile().getAbsolutePath());
				commandArgs.add(path );
				commandArgs.add(file.getName());
				ProcessBuilder pb = new ProcessBuilder(commandArgs);
				try
				{
					pb.start();
				} catch (IOException e)
				{
					//What ever
				}
			}
		}
		return null;
	}
}