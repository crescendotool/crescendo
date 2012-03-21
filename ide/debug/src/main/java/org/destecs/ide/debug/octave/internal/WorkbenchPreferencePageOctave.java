package org.destecs.ide.debug.octave.internal;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WorkbenchPreferencePageOctave extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{
	FileFieldEditor octavePathField = null;
	public void init(IWorkbench workbench)
	{
		
	}

	@Override
	protected void createFieldEditors()
	{
		octavePathField = new FileFieldEditor(IDebugConstants.OCTAVE_PATH, "Path to octave:", getFieldEditorParent());
		octavePathField.setPreferenceStore(getPreferenceStore());
		octavePathField.setPage(this);
		addField(octavePathField);
		
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return DestecsDebugPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void performDefaults()
	{
		octavePathField.loadDefault();
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(IDebugConstants.OCTAVE_PATH, IDebugConstants.DEFAULT_OCTAVE_PATH);
		super.performDefaults();
	}
}
