package org.destecs.tools.jprotocolgenerator.ast;

import java.util.List;
import java.util.Vector;

public class Method implements IAstNode
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

	@Override
	public String toString()
	{
		return toSource();
	}
}
