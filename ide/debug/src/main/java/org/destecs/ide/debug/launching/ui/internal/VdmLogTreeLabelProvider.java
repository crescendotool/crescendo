package org.destecs.ide.debug.launching.ui.internal;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;

public class VdmLogTreeLabelProvider extends LabelProvider implements
		IBaseLabelProvider
{
	@Override
	public String getText(Object element)
	{
		if (element instanceof TreeNodeContainer)
		{
			Object child = ((TreeNodeContainer) element).data;
			if (child instanceof ClassDefinition)
			{
				ClassDefinition classDef = (ClassDefinition) child;
				return classDef.getName();

			}
			if (child instanceof InstanceVariableDefinition)
			{
				InstanceVariableDefinition insVarDef = (InstanceVariableDefinition) child;
				return insVarDef.getName();
			} else
			{
				System.out.println("VdmLogTreeLabelProvider getText");

			}
		}
		return super.getText(element);
	}
}
