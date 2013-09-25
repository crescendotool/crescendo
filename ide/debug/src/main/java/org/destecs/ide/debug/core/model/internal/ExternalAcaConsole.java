package org.destecs.ide.debug.core.model.internal;

import java.io.PrintWriter;

import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ExternalAcaConsole
{
	public final PrintWriter out;
	public final PrintWriter err;
	boolean hasConsole = false;

	public static Display getDisplay()
	{
		Display display = Display.getCurrent();
		// may be null if outside the UI thread
		if (display == null)
			display = Display.getDefault();
		return display;
	}

	public ExternalAcaConsole()
	{
		final MessageConsole myConsole = findConsole(IDebugConstants.EXTERNAL_ACA_CONSOLE_NAME);
		if (myConsole != null)
		{
			out = new PrintWriter(myConsole.newMessageStream(), true);
			final MessageConsoleStream errConsole = myConsole.newMessageStream();
			getDisplay().asyncExec(new Runnable()
			{

				@Override
				public void run()
				{
					errConsole.setColor(getDisplay().getSystemColor(SWT.COLOR_RED));
				}
			});

			err = new PrintWriter(errConsole, true);
			hasConsole = true;
		} else
		{
			out = new PrintWriter(System.out, true);
			err = new PrintWriter(System.err, true);
		}
		// out.println(message);

	}

	public void show() throws PartInitException
	{
		if (hasConsole)
		{
			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (activeWorkbenchWindow != null)
			{
				IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
				if (activePage != null)
				{
					activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
				}
			}
		}
	}

	private MessageConsole findConsole(String name)
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		if (plugin != null)
		{
			IConsoleManager conMan = plugin.getConsoleManager();
			IConsole[] existing = conMan.getConsoles();
			for (int i = 0; i < existing.length; i++)
			{
				if (name.equals(existing[i].getName()))
				{
					return (MessageConsole) existing[i];
				}
			}
			// no console found, so create a new one
			MessageConsole myConsole = new MessageConsole(name, null);
			conMan.addConsoles(new IConsole[] { myConsole });
			return myConsole;
		}
		return null;
	}

	public void clear()
	{
		try
		{
			final MessageConsole myConsole = findConsole(IDebugConstants.EXTERNAL_ACA_CONSOLE_NAME);
			if (myConsole != null)
			{
				myConsole.clearConsole();
			}
		} catch (Exception e)
		{
		}
	}

}
