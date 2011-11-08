/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.ide.debug.launching.ui.internal;

import java.util.ArrayList;
import java.util.List;



public class LinesProvider {

	static private LinesProvider instance = null;
	private List<String> lines = null;
	
	public LinesProvider() {
		lines = new ArrayList<String>();
	}
	
	
//	private void fillLines()
//	{		
//		lines.add("A");
//		lines.add("B");
//		lines.add("C");
//	}
	
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
