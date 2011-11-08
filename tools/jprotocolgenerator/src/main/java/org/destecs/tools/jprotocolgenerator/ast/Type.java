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

public class Type implements ITypeNode
{
	@SuppressWarnings("rawtypes")
	public Class type;
	private final static String unknown = "_ERROR_UNKNOWN_TYPE_";

	public Type(@SuppressWarnings("rawtypes") Class class1)
	{
		this.type = class1;
	}
	
	public Type()
	{
		this.type = null;
	}
	
	public Type(ITypeNode node)
	{
		if(node instanceof Type)
		{
			this.type = ((Type)node).type;
		}
	}

	@Override
	public String toSource()
	{
		if(type !=null)
		{
			return type.getSimpleName();
		}else
		{
			return unknown;
		}
		
	}

	@Override
	public String getName()
	{
		if(type !=null)
		{
			return type.getName();
		}else
		{
			return unknown;
		}
	}
	
	@Override
	public String toString()
	{
		return toSource();
	}

}
