package org.destecs.ide.debug.launching.ui.internal;

import org.overturetool.vdmj.definitions.Definition;

public class TreeNodeContainer
{

	public TreeNodeContainer parent = null;
	public Definition data = null;
	public boolean isVirtual = false;
	
	public TreeNodeContainer(TreeNodeContainer parent, Definition child, boolean isVirtual)
	{
		this.parent = parent;
		this.data = child;
		this.isVirtual = isVirtual;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if(parent == null)
		{			
			return null;
		}
		else
		{
			String pString = parent.toString();
			if(pString != null)
			{
				sb.append(pString);
				sb.append(".");
			}
			sb.append(data.getName());
		}
		
		return sb.toString();
	}
}
