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

import java.util.Map;
import java.util.TreeMap;


public class MapType implements ITypeNode
{
	
	public ITypeNode keyType;
	public ITypeNode valueType;
	public Map<String,ITypeNode> possibleEntries = new TreeMap<String, ITypeNode>();

	public MapType(Type type, ITypeNode rangeType)
	{
		this.keyType = type;
		this.valueType = rangeType;
	}

	public MapType()
	{
	}

	@Override
	public String getName()
	{
		return "Map<"+ keyType.toSource()+","+valueType.toSource()+">";
	}

	@Override
	public String toSource()
	{
		return "Map<"+ keyType.toSource()+","+valueType.toSource()+">";
	}
	
	@Override
	public String toString()
	{
		return toSource();
	}

}
