package org.destecs.ide.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class NewContractCommand extends AbstractHandler{

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection s = HandlerUtil.getCurrentSelection(event);
		Command c = event.getCommand();
		return null;
	}

	

}
