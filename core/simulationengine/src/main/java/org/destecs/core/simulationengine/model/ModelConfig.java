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
package org.destecs.core.simulationengine.model;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class ModelConfig
{
	public final Map<String, String> arguments = new Hashtable<String, String>();
	public final Set<String> logVariables = new HashSet<String>();
	public File logFile = null;

	public abstract boolean isValid();

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append("Model(");
		for (Entry<String, String> entry : arguments.entrySet())
		{
			String v = entry.getValue();
			if ((v + "").isEmpty())
			{
				v = "\"\"";
			}
			sb.append(entry.getKey() + "=" + v + " ");
		}
		sb.append(")");
		return sb.toString();
	}
}
