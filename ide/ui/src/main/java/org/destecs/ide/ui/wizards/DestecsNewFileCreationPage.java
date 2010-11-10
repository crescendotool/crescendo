package org.destecs.ide.ui.wizards;

import org.destecs.ide.core.resources.IDestecsProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class DestecsNewFileCreationPage extends WizardNewFileCreationPage {

	private IStructuredSelection _selection = null;
	
	public DestecsNewFileCreationPage(String pageName,
			IStructuredSelection selection) {
		super(pageName, selection);
		this._selection = selection;
	}
	
	
	
	

}
