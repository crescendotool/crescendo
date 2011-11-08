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

import java.util.List;
import java.util.Vector;



public class Method implements IAstNode, Cloneable
{
	public ITypeNode returnType;
	public String name;
	public List<Parameter> parameters = new Vector<Parameter>();
	public String body;
	public String javaDoc;
	public String javaDocText;
	public IAnnotation annotation;
	public String group;
	public boolean isConstructor = false;
	public List<FreeTextType> throwsTypes = new Vector<FreeTextType>();

	public Method clone()
	{
		Method m = new Method();
		m.returnType = this.returnType;
		m.name = this.name;
		m.parameters = this.parameters;
		m.body = this.body;
		m.javaDoc = this.javaDoc;
		m.group = this.group;
		m.isConstructor = this.isConstructor;
		m.throwsTypes = this.throwsTypes;
		return m;
	}

	@Override
	public String toSource()
	{
		StringBuilder sb = new StringBuilder();

		if (javaDoc != null)
		{
			sb.append(javaDoc);
			sb.append("\n\t");
		} else if (javaDocText != null)
		{
			sb.append("/**\n\t* " + javaDocText + "\n\t*/");
			sb.append("\n\t");
		}
		if (annotation != null)
		{
			sb.append(annotation.toSource());
			sb.append("\n\t");
		}
		sb.append("public ");
		if (!isConstructor)
		{
			sb.append(returnType.getName());
			sb.append(" ");
		}
		sb.append(name);
		sb.append("(");
		for (IAstNode node : parameters)
		{
			sb.append(node.toSource());
			sb.append(", ");
		}
		if (parameters.size() > 0)
		{
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append(")");
		
		sb.append(getThrowsSourceSegment());
		
		if (body == null)
		{
			sb.append(";");
		} else
		{
			sb.append("\n\t{\n\t\t");
			sb.append(body);
			sb.append("\n\t}");
		}

		return sb.toString();
	}

	public String getThrowsSourceSegment()
	{
		StringBuilder sb= new StringBuilder();
		if(!throwsTypes.isEmpty())
		{
			sb.append(" throws ");
			for (int i = 0; i < throwsTypes.size(); i++)
			{
				sb.append(throwsTypes.get(i));
				if(i<throwsTypes.size()-1)
				{
					sb.append(", ");
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String toString()
	{
		return toSource();
	}
}
