package org.destecs.ide.debug.launching.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SettingsTreeContentProvider implements ITreeContentProvider {

	public void dispose() {
		// TODO Auto-generated method stub
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof SettingTreeNode)
		{
			SettingTreeNode node = (SettingTreeNode) inputElement;
			return node.getChildren();
		}
		return null;
	}

	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof SettingTreeNode)
		{
			SettingTreeNode node = (SettingTreeNode) parentElement;
			return node.getChildren();
		}
		
		return new Object[0];
	}

	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildren(Object element) {
		if(element instanceof SettingTreeNode)
		{
			SettingTreeNode node = (SettingTreeNode) element;
			return node.getChildren().length > 0;
		}
		return false;
	}

}
