package org.destecs.tools.jprotocolgenerator.ast;

import java.util.List;
import java.util.Vector;

public class IInterface implements IAstNode,ITypeNode
{
	public List<ITypeNode> imports = new Vector<ITypeNode>();
	public String name;
	public String packageName;
	public List<Method> definitions = new Vector<Method>();
	public List<ITypeNode> extended = new Vector<ITypeNode>();

	@Override
	public String toSource()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("package " + packageName + ";");

		sb.append("\n");
		for (ITypeNode c : imports)
		{
			sb.append("\nimport " + c.getName() + ";");
		}

		sb.append("\n");

		sb.append("\npublic interface " + name);
		if(extended.size()>0)
		{
			sb.append(" extends");
			for (ITypeNode type : extended)
			{
				sb.append(" ");
				sb.append(type.getName());
			}
		}
		sb.append("\n{");

		for (IAstNode definition : definitions)
		{
			sb.append("\n\t");
			sb.append(definition.toSource());
			sb.append("\n");
		}

		sb.append("\n}");

		return sb.toString();
	}

	@Override
	public String getName()
	{
		return this.name;
	}
}
