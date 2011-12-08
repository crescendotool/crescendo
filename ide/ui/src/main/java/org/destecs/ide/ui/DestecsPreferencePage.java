package org.destecs.ide.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DestecsPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage
{

	private BooleanFieldEditor preventLaunch = null;
	
	public DestecsPreferencePage()
	{
		super(FieldEditorPreferencePage.GRID);
		IPreferenceStore store =
			DestecsUIPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		store.setDefault(IDestecsPreferenceConstants.ACTIVATE_DESTECSCHECK_PREFERENCE, true);
	}

	public DestecsPreferencePage(int style)
	{
		super(style);
		
	}

	public DestecsPreferencePage(String title, int style)
	{
		super(title, style);
	}

	public DestecsPreferencePage(String title, ImageDescriptor image, int style)
	{
		super(title, image, style);
	}

	public void init(IWorkbench workbench)
	{

	}

	@Override
	protected void createFieldEditors()
	{
		preventLaunch = new BooleanFieldEditor(IDestecsPreferenceConstants.ACTIVATE_DESTECSCHECK_PREFERENCE, "Prevent launch of Co-Simulations with Contract/Link errors", getFieldEditorParent()) ;		
		addField(preventLaunch);

	}
	
	@Override
	protected void performDefaults()
	{		
		super.performDefaults();
	}

}
