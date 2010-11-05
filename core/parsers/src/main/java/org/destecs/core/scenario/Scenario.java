package org.destecs.core.scenario;

import java.util.List;
import java.util.Vector;

public class Scenario
{
	public final List<Action> actions;

	public Scenario(List<Action> actions) {
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
