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
package org.destecs.core.vdmlink;

import java.util.Iterator;
import java.util.List;

public class LinkInfo {
	
	
	
	private String identifier = null;
	private List<String> qualifiedName = null;
	private int line = -1;
	
	public LinkInfo(String identifier, List<String> qualifiedName, int line) {
		this.identifier = identifier;
		this.qualifiedName = qualifiedName;
		this.line = line;
	}
	
	/**
	 * This does not return the full name, only a fraction of it. use getQualifiedName instead.
	 * @return
	 */
	@Deprecated
	public StringPair getBoundedVariable()
	{
		if(qualifiedName.size() > 1)
		{
			return new StringPair(qualifiedName.get(0), qualifiedName.get(1));
		}
		else
			return null;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public int getLine()
	{
		return line;
	}

	public List<String> getQualifiedName() {
		return qualifiedName;
	}
	
	public String getQualifiedNameString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<String> itr = getQualifiedName().iterator(); itr.hasNext();)
		{
			sb.append( itr.next());
			if(itr.hasNext())
			{
				sb.append(".");
			}
			
		}
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getIdentifier()+".");
		for (Iterator<String> itr = getQualifiedName().iterator(); itr.hasNext();)
		{
			sb.append( itr.next());
			if(itr.hasNext())
			{
				sb.append(".");
			}
			
		}
		return sb.toString();
	}
	
}
