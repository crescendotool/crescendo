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
package org.destecs.tools.jprotocolgenerator.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class SuppressWarningAnnotation implements IAnnotation
{
	final public Set<WarningTypes> warnings = new HashSet<WarningTypes>();
	public enum WarningTypes {unchecked,rawtypes};
	
	
	public SuppressWarningAnnotation(WarningTypes... w)
	{
		warnings.clear();
		warnings.addAll(Arrays.asList(w));
	}

	@Override
	public String toSource()
	{
		String tmp = "@SuppressWarnings({";
	
			for (Iterator<WarningTypes> itr = warnings.iterator(); itr.hasNext();)
			{
				WarningTypes warning = (WarningTypes) itr.next();
				tmp += "\""+warning.toString().toLowerCase()+"\"";
				if (itr.hasNext())
				{
					tmp += ",";
				}
			}
			tmp += "})";
			return tmp;
	}

}
