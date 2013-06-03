package org.destecs.ide.debug.launching.ui.internal;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.SClassDefinition;

public class VdmLogTreeLabelProvider extends LabelProvider implements
		IBaseLabelProvider
{
	@Override
	public String getText(Object element)
	{
		if (element instanceof TreeNodeContainer)
		{
			Object child = ((TreeNodeContainer) element).data;
			if (child instanceof SClassDefinition)
			{
				SClassDefinition classDef = (SClassDefinition) child;
				return classDef.getName().getName();

			}
			if (child instanceof AInstanceVariableDefinition)
			{
				AInstanceVariableDefinition insVarDef = (AInstanceVariableDefinition) child;
				return insVarDef.getName().getName();
			} else
			{
				System.out.println("VdmLogTreeLabelProvider getText");

			}
		}
		return super.getText(element);
	}
}
