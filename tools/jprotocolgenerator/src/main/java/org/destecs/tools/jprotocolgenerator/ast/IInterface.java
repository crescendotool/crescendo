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

import java.io.File;
import java.util.List;
import java.util.Vector;

public class IInterface implements IAstNode, ITypeNode
{
	public List<ITypeNode> imports = new Vector<ITypeNode>();
	protected String name;
	public String packageName;
	public List<Method> definitions = new Vector<Method>();
	public List<ITypeNode> extended = new Vector<ITypeNode>();

	protected String typeName = "interface";

	@Override
	public String toSource()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("package " + packageName + ";");

		sb.append("\n");

		if (!definitions.isEmpty())
		{
			List<String> importedNames = new Vector<String>();
			for (ITypeNode c : imports)
			{
				if (importedNames.contains(c.getName()))
				{
					continue;
				} else
				{
					sb.append("\nimport " + c.getName() + ";");
					importedNames.add(c.getName());
				}
			}
		}
		sb.append("\n");

		boolean suppressUnused = false;
		boolean suppressUnchecked = false;//!definitions.isEmpty();

		sb.append("\n@SuppressWarnings(\"all\")");
		if (suppressUnchecked || suppressUnused)
		{
//			sb.append("\n@SuppressWarnings({");
//			if (suppressUnused)
//			{
//				sb.append("\"unused\"");
//			}
//			if (suppressUnchecked)
//			{
//				if (sb.toString().endsWith("\""))
//				{
//					sb.append(", ");
//				}
//				sb.append("\"unchecked\"");
//			}
//			sb.append("})");
		}

		sb.append("\npublic " + typeName + " " + getName());
		if (extended.size() > 0)
		{
			sb.append(" extends");
			for (ITypeNode type : extended)
			{
				sb.append(" ");
				sb.append(type.getName());
			}

		}
		addImplemented(sb);
		sb.append("\n{");

		toSourceDefinitions(sb);

		sb.append("\n}");

		return sb.toString();
	}

	protected void addImplemented(StringBuilder sb)
	{
	}

	protected void toSourceDefinitions(StringBuilder sb)
	{
		for (IAstNode definition : definitions)
		{
			sb.append("\n\t");
			sb.append(definition.toSource());
			sb.append("\n");
		}
	}

	@Override
	public String getName()
	{
		return firstCharToUpper(this.name, true);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	private static String firstCharToUpper(String part, boolean upper)
	{
		if (part == null || part.length() == 0)
		{
			return "";
		}
		String start = part.substring(0, 1);
		if (upper)
		{
			start = start.toUpperCase();
		} else
		{
			start = start.toLowerCase();
		}
		return start + part.substring(1);
	}

	@Override
	public String toString()
	{
		return toSource();
	}

	public File getOutputFolder(File folder)
	{
		String path = packageName.replace('.', File.separatorChar);
		return new File(folder, path);
	}
}
