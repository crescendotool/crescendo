package org.destecs.ide.ui.wizards;

import org.destecs.ide.core.IDestecsCoreConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.overture.ide.vdmrt.core.IVdmRtCoreConstants;

public class DestecsNewWizard extends BasicNewProjectResourceWizard {

	private static final String WIZARD_NAME = "DESTECS New Project Wizard";

	public DestecsNewWizard() {
		setWindowTitle(WIZARD_NAME);

	}

	@Override
	public boolean performFinish() {
		boolean ok = super.performFinish();

		IProject prj = getNewProject();

		if (prj != null) {
			setDestecsSettings(prj);
		}

		return ok;
	}

	private void setDestecsSettings(IProject prj) {
		try {
			
			addNature(prj, IDestecsCoreConstants.NATURE);
			addNature(prj, IVdmRtCoreConstants.NATURE) ;
			//TODO: add builder if needed
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void addNature(IProject project, String nature)
			throws CoreException {

		if (!project.hasNature(nature)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = nature;
			description.setNatureIds(newNatures);

			IProgressMonitor monitor = null;
			project.setDescription(description, monitor);
		}
	}

}
