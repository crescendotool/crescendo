package org.destecs.ide.debug.launching.ui.internal;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

public class SettingsTreeLabelProvider extends LabelProvider implements ILabelProvider {


	@Override
	public String getText(Object element) {
		if(element instanceof SettingTreeNode)
		{
			SettingTreeNode node = (SettingTreeNode) element;
			return node.getName();
		}
		return super.getText(element);
	}
	

}
