package org.destecs.core.dcl;

import java.util.List;

import org.destecs.core.dcl.Action;

public class Dcl
{
	public final List<Action> actions;

	public Dcl(List<Action> actions) {
		this.actions = actions;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (Action a : actions)
		{
			sb.append(a);
			sb.append("\n");
		}
		return sb.toString();
	}
}