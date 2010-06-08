package org.destecs.tools.jprotocolgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import org.destecs.tools.jprotocolgenerator.ast.IInterface;
import org.destecs.tools.jprotocolgenerator.ast.ITypeNode;
import org.destecs.tools.jprotocolgenerator.ast.MapType;
import org.destecs.tools.jprotocolgenerator.ast.Method;
import org.destecs.tools.jprotocolgenerator.ast.Parameter;
import org.destecs.tools.jprotocolgenerator.ast.Type;

public class GenerateProxy
{
	public String generate(IInterface intf,String outputFolder)
	{
		StringBuffer sb = new StringBuffer();

		sb.append("package " + intf.packageName + ";");

		sb.append("\n");
		for (ITypeNode c : intf.imports)
		{
			sb.append("\nimport " + c.getName() + ";");
		}
		sb.append("\nimport " + new Type(Hashtable.class).getName() + ";");

		sb.append("\n");
		String name = "Proxy" + intf.name;
		sb.append("public class " + name);
		sb.append("\n{");

		sb.append("\n\t" + intf.name + " source;");

		sb.append("\n\tpublic " + name + "(" + intf.name + " source)");
		sb.append("\n\t{");
		sb.append("\n\t\tthis.source = source; \n\t}");

		for (Method m : intf.definitions)
		{
			sb.append("\n\t" + generate(m));
		}

		sb.append("\n}");
		
		
		
		FileOutputStream fos;
		try
		{
			File output = null;
			if (outputFolder != null)
			{
				output = new File(outputFolder, name + ".java");
			} else
			{
				output = new File(name + ".java");
			}
			fos = new FileOutputStream(output, false);

			fos.write(sb.toString().getBytes());
			fos.close();
		} catch (FileNotFoundException e)
		{

			e.printStackTrace();
		} catch (IOException e)
		{

			e.printStackTrace();
		}

		return sb.toString();
	}

	private boolean hasMapParameter(Method m)
	{

		for (Parameter p : m.parameters)
		{
			if (p.type instanceof MapType
					&& ((MapType) p.type).possibleEntries.size() > 0)
			{
				return true;
			}
		}

		return false;
	}

	private String generate(Method m)
	{
		if (hasMapParameter(m) && m.parameters.size() == 1)
		{
			return generateExtendedMethod(m);
		} else
		{
			return generateSimpleMethod(m);
		}
	}

	private String generateExtendedMethod(Method m)
	{
		

		StringBuffer sb = new StringBuffer();

		

		if(m.javaDoc!=null)
		{
		sb.append(m.javaDoc);
		sb.append("\n\t");
		}
		sb.append("public ");
		sb.append(m.returnType.toSource());
		sb.append(" ");
		sb.append(m.name);
		sb.append("(");

		Parameter p1 = m.parameters.get(0);
		
				MapType type = (MapType) p1.type;
		
				for (String key : type.possibleEntries.keySet())
				{
					sb.append(type.possibleEntries.get(key).toSource());
					sb.append(" ");
					sb.append(key);
					sb.append(",");
				}
				if (type.possibleEntries.size() > 0)
				{
					sb.deleteCharAt(sb.length() - 1);
				}
		
		
		sb.append(")");



		sb.append("\n\t{");
		
		sb.append("\n\t\t"+p1.type.toSource()+" data = new "+p1.type.toSource().replaceFirst("Map", "Hashtable")+"();");
		
		for (String key : type.possibleEntries.keySet())
		{
			sb.append("\n\t\tdata.put(");
			sb.append("\""+key+"\"");
			sb.append(",");
			sb.append(key);
			sb.append(");");
			
			
		}
		sb.append("\n\t\t");
		
		if (m.returnType instanceof Type
				&& ((Type) m.returnType).type == Void.class)
		{

		} else
		{
			sb.append("return ");
		}

		sb.append("source." + m.name + "(");

		for (Parameter p : m.parameters)
		{
			sb.append(p.name);
			sb.append(",");
		}

		if (m.parameters.size() > 0)
		{
			sb.deleteCharAt(sb.length() - 1);
		}

		sb.append(");");
		
		sb.append("\n\t}");

		return sb.toString();
	}

	private String generateSimpleMethod(Method m)
	{
		Method newM = m.clone();

		StringBuffer sb = new StringBuffer();

		if (m.returnType instanceof Type
				&& ((Type) m.returnType).type == Void.class)
		{

		} else
		{
			sb.append("return ");
		}
		
		boolean addCast = false;
		if(newM.returnType instanceof MapType)
		{
			MapType returnType = ((MapType)newM.returnType);
			if(returnType.possibleEntries.size()==1)
			{
				newM.returnType = returnType.possibleEntries.get(returnType.possibleEntries.keySet().toArray()[0]);
				addCast = true;
				
			}
		}

		if(addCast)
		{
			sb.append("("+newM.returnType.toSource()+")");
		}
		sb.append("source." + m.name + "(");

		for (Parameter p : m.parameters)
		{
			sb.append(p.name);
			sb.append(",");
		}

		if (m.parameters.size() > 0)
		{
			sb.deleteCharAt(sb.length() - 1);
		}

		sb.append(")");

		if(addCast)
		{
			sb.append(".values().toArray()[0]");
		}
		
		
		sb.append(";");
		newM.body = sb.toString();
		
		return newM.toSource();
	}
}
