package org.destecs.ide.debug.launching.ui.internal;

import java.util.ArrayList;
import java.util.List;



public class LinesProvider {

	static private LinesProvider instance = null;
	private List<String> lines = null;
	
	public LinesProvider() {
		lines = new ArrayList<String>();
	}
	
	
	private void fillLines()
	{		
		lines.add("A");
		lines.add("B");
		lines.add("C");
	}
	
	static public LinesProvider getInstance()
	{
		if(instance == null)
		{
			instance = new LinesProvider();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void addArchitecture(String s)
	{
		this.lines.add(s);
	}
	
	public List<String> getLines() {
		
		return lines;
	}
	
}
