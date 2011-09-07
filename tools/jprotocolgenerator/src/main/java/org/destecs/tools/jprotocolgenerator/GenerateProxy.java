package org.destecs.tools.jprotocolgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.tools.jprotocolgenerator.ast.ClassDefinition;
import org.destecs.tools.jprotocolgenerator.ast.Field;
import org.destecs.tools.jprotocolgenerator.ast.FreeTextType;
import org.destecs.tools.jprotocolgenerator.ast.IInterface;
import org.destecs.tools.jprotocolgenerator.ast.ITypeNode;
import org.destecs.tools.jprotocolgenerator.ast.ListType;
import org.destecs.tools.jprotocolgenerator.ast.MapType;
import org.destecs.tools.jprotocolgenerator.ast.Method;
import org.destecs.tools.jprotocolgenerator.ast.Parameter;
import org.destecs.tools.jprotocolgenerator.ast.Type;
import org.destecs.tools.jprotocolgenerator.ast.VoidType;

public class GenerateProxy
{
	private String packageName;
	private String structPackageName;
	private static String outputFolder;
	private static IInterface stryctInterface;

	public String generate(IInterface intf, String outputFolder)
	{
		stryctInterface = new IInterface();
		stryctInterface.setName("IStruct");

		Method toMapMethod = new Method();
		toMapMethod.name = "toMap";
		toMapMethod.returnType = new FreeTextType("Map<String,? extends Object>");// new Type( new MapType(new
		// Type(String.class), new
		// Type(Object.class));
		stryctInterface.imports.add(new Type(Map.class));
		stryctInterface.definitions.add(toMapMethod);

		StringBuffer sb = new StringBuffer();
		this.packageName = intf.packageName;
		this.structPackageName = this.packageName + ".structs";
		GenerateProxy.outputFolder = outputFolder;
		sb.append("package " + intf.packageName + ";");

		sb.append("\n");

		for (ITypeNode c : intf.imports)
		{
			sb.append("\nimport " + c.getName() + ";");
		}
		sb.append("\nimport " + new Type(Hashtable.class).getName() + ";");
		sb.append("\nimport " + new Type(Vector.class).getName() + ";");
		sb.append("\nimport " + structPackageName + ".*;");

		sb.append("\n");
		String name = "Proxy" + intf.getName();
		sb.append("\n");
//		sb.append("\n@SuppressWarnings(\"unused\")");
		sb.append("\n@SuppressWarnings({\"unused\",\"unchecked\"})");
		sb.append("\n");
		sb.append("public class " + name);
		sb.append("\n{");

		sb.append("\n\t" + intf.getName() + " source;");

		sb.append("\n\tpublic " + name + "(" + intf.getName() + " source)");
		sb.append("\n\t{");
		sb.append("\n\t\tthis.source = source; \n\t}");

		for (Method m : intf.definitions)
		{
			sb.append("\n\n\t" + generate(m));
		}

		sb.append("\n}");

		FileOutputStream fos;
		try
		{
			File output = null;
			File folder = null;
			if (outputFolder != null)
			{
				folder = intf.getOutputFolder(new File(outputFolder));

				// output = new File(folder, name + ".java");
			} else
			{
				folder = intf.getOutputFolder(new File("."));
			}
			folder.mkdirs();
			output = new File(folder, name + ".java");
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

		List<IInterface> structInterface = new Vector<IInterface>();
		stryctInterface.packageName = structPackageName;
		structInterface.add(stryctInterface);
		printClasses(structInterface);

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
		List<ClassDefinition> defs = new Vector<ClassDefinition>();
		ClassDefinition returnClass = null;

		if (m.javaDoc != null)
		{
			sb.append(m.javaDoc);
			sb.append("\n\t");
		}
		sb.append("public ");
		if (m.returnType instanceof MapType)
		{
			returnClass = new ClassDefinition();
			returnClass.setName(m.name + "Struct");
			returnClass.packageName = structPackageName;
			returnClass.imports.add(new Type(Map.class));
			returnClass.imports.add(new Type(List.class));
			returnClass.implemented.add(stryctInterface);
			sb.append(returnClass.getName());
		} else
		{

			sb.append(m.returnType.toSource());
		}
		sb.append(" ");
		sb.append(m.name);
		sb.append("(");

		Parameter p1 = m.parameters.get(0);

		MapType type = (MapType) p1.type;

		Map<String, ClassDefinition> structs = new Hashtable<String, ClassDefinition>();

		for (String key : type.possibleEntries.keySet())
		{
			ITypeNode t = type.possibleEntries.get(key);
			if ((t instanceof ListType
					&& ((ListType) t).type instanceof MapType || t instanceof MapType))
			{

				ClassDefinition pClass = new ClassDefinition();
				pClass.setName(m.name+key + "StructParam");
				pClass.packageName = structPackageName;
				pClass.imports.add(new Type(Map.class));
				pClass.imports.add(new Type(List.class));
				pClass.implemented.add(stryctInterface);
				defs.add(pClass);
				structs.put(key, pClass);
				MapType mType = null;
				if (t instanceof ListType)
				{
					mType = (MapType) ((ListType) t).type;
				} else
				{
					mType = (MapType) t;
				}
				defs.addAll(generateStructs(pClass, structPackageName, mType));

				if (t instanceof ListType)
				{
					sb.append("List<" + pClass.getName() + ">");
				} else
				{

					sb.append(pClass.getName());
				}
			} else
			{

				sb.append(type.possibleEntries.get(key).toSource());
			}
			sb.append(" ");
			sb.append(key);
			sb.append(",");
		}
		if (type.possibleEntries.size() > 0)
		{
			sb.deleteCharAt(sb.length() - 1);
		}

		sb.append(")");
		
		if(!m.throwsTypes.isEmpty())
		{
			sb.append(m.getThrowsSourceSegment());
		}

		sb.append("\n\t{");

		sb.append("\n\t\t" + p1.type.toSource() + " data = new "
				+ p1.type.toSource().replaceFirst("Map", "Hashtable") + "();");

		Integer pCount = 0;
		for (String key : type.possibleEntries.keySet())
		{

			if (structs.containsKey(key))
			{
				ITypeNode t = ((MapType) p1.type).valueType;
				if (t.getName().contains(Object.class.getSimpleName()))
				{
					t = new ListType(new Type(Object.class));
				}
				
				MapType mapt = ((MapType) p1.type);
				if (mapt.valueType instanceof ListType)
				{
					sb.append("\n\n\t\tList<" + ((ListType) mapt.valueType).type.toSource() + "> pTmp" + pCount
							+ (" = new List<" + ((ListType) mapt.valueType).type.toSource()+">").replace("List", "Vector")
							+ "();");
				}else{
									
				sb.append("\n\n\t\t" + t.toSource() + " pTmp" + pCount
						+ " = new " + t.toSource().replace("List", "Vector")
						+ "();");
				}
				
				sb.append("\n\t\tfor( IStruct a: " + key + ")");
				sb.append("\n\t\t{");

				String cast = "";
				
				if (mapt.valueType instanceof ListType)
				{
					cast = ((ListType) mapt.valueType).type.toSource();
					cast = "(" + cast + ")";
				}

				sb.append("\n\t\t\tpTmp" + pCount + ".add(" + cast
						+ "a.toMap());");
				sb.append("\n\t\t}");
			}

			sb.append("\n\t\tdata.put(");
			sb.append("\"" + key + "\"");
			sb.append(",");

			if (structs.containsKey(key))
			{
				sb.append("pTmp" + pCount);
			} else
			{

				sb.append(key);
			}
			sb.append(");");
			pCount++;

		}
		sb.append("\n\t\t");

		if (m.returnType instanceof Type
				&& ((Type) m.returnType).type == Void.class)
		{
int i = 0;
		} else
		{
			sb.append("return ");
			if (m.returnType instanceof MapType)
			{
				sb.append("new " + returnClass.getName() + "(");
				defs.addAll(generateStructs(returnClass, structPackageName, (MapType) m.returnType));
				defs.add(returnClass);

			}else
			{
				int i = 0;
			}
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

		if (m.returnType instanceof MapType)
		{
			sb.append(")");
		}

		sb.append(");");

		sb.append("\n\t}");

		printClasses(defs);

		return sb.toString();
	}

	private static void printClasses(List<? extends IInterface> defs)
	{
		FileOutputStream fos;

		for (IInterface cd : defs)
		{
			try
			{
				File output = null;
				File folder = null;
				if (outputFolder != null)
				{
					folder = cd.getOutputFolder(new File(outputFolder));

					// output = new File(folder, name + ".java");
				} else
				{
					folder = cd.getOutputFolder(new File("."));
				}
				folder.mkdirs();
				output = new File(folder, cd.getName() + ".java");

				fos = new FileOutputStream(output, false);

				fos.write(cd.toSource().getBytes());
				fos.close();
			} catch (FileNotFoundException e)
			{

				e.printStackTrace();
			} catch (IOException e)
			{

				e.printStackTrace();
			}
		}

	}

	private static List<ClassDefinition> generateStructs(
			ClassDefinition returnClass, String packageName2, MapType map)
	{
		return generateStructs(returnClass, packageName2, map, false);
	}

	private static List<ClassDefinition> generateStructs(
			ClassDefinition returnClass, String packageName2, MapType map,
			boolean isParameter)
	{
		List<ClassDefinition> defs = new Vector<ClassDefinition>();

		String appendName = "Struct";
		if (isParameter)
		{
			appendName = "StructParam";
		}

		// Constructor
		Method m = new Method();
		m.name = returnClass.getName();
		m.returnType = new VoidType();
		m.body = "";
		m.isConstructor = true;
		Integer count = 0;
		Parameter param = new Parameter(map, "value");
		m.parameters.add(param);
		for (String p : map.possibleEntries.keySet())
		{
			Field f = new Field();
			f.setName(p);
			returnClass.fields.add(f);

			ITypeNode type = map.possibleEntries.get(p);
			if (type instanceof ListType)
			{
				if (((ListType) type).type instanceof MapType)
				{
					ClassDefinition newRet = new ClassDefinition();
					newRet.setName(m.name + p + appendName);
					newRet.packageName = packageName2;
					newRet.imports.add(new Type(Map.class));
					newRet.imports.add(new Type(List.class));
					newRet.implemented.add(stryctInterface);
					returnClass.imports.add(new Type(Vector.class));

					defs.addAll(generateStructs(newRet, packageName2, (MapType) ((ListType) type).type, isParameter));
					defs.add(newRet);
					f.type = new ListType(newRet);
					m.body += f.type.getName() + " tmp" + count
							+ " = new Vector<" + newRet.getName()
							+ ">();\n\t\t";

					
					m.body += "if( value.keySet().contains(\""+f.getName()+"\" ))\n\t\t";
					m.body += "{\n\t\t\t";

			
					if (map.valueType instanceof List
							|| map.valueType instanceof ListType)
					{
						m.body += "Object tmpL" + count
						+ " = value.get(\""+f.getName()+"\");\n\t\t\t";
						m.body += "for( Object m : (Object[])tmpL"+count+")\n\t\t\t";

					} else
					{
						m.body += "for( Object m : (Object[])value.get(\""+f.getName()+"\"))\n\t\t\t";
					}

					m.body += "{\n\t\t\t\t";

					m.body += "tmp" + count + ".add( new " + newRet.getName()
							+ "( (" + ((ListType) type).type + ") m)";
					m.body += ");\n\t\t\t";

					m.body += "}\n\t\t";

					m.body += "}\n\t\t";

					m.body += "this." + f.getName() + " = tmp" + count
							+ ";\n\n\n\t\t\t";

				} else
				{
					f.type = map.possibleEntries.get(p);
//					m.body += "this." + f.getName() + " = (" + f.type.getName()
//							+ ") value.get(value.keySet().toArray()[" + count
//							+ "]);\n\t\t";
					m.body += "if( value.keySet().contains(\""+f.getName()+"\" ))\n\t\t";
					m.body += "{\n\t\t\t";

			
					if (map.valueType instanceof List
							|| map.valueType instanceof ListType)
					{
						m.body += "Object tmpL" + count
						+ " = value.get(\""+f.getName()+"\");\n\t\t\t";
						m.body += "for( Object m : (Object[])tmpL"+count+")\n\t\t\t";

					} else
					{
						m.body += "for( Object m : (Object[])value.get(\""+f.getName()+"\"))\n\t\t\t";
					}

					m.body += "{\n\t\t\t\t";

					m.body += "this." + f.getName() + ".add((" + ((ListType) type).type + ") m";
					m.body += ");\n\t\t\t";

					m.body += "}\n\t\t";

					m.body += "}\n\t\t";

					
				}
			} else if (type instanceof MapType)
			{
				ClassDefinition newRet = new ClassDefinition();
				newRet.setName(m.name + p + appendName);
				newRet.packageName = packageName2;
				newRet.imports.add(new Type(Map.class));
				newRet.imports.add(new Type(List.class));
				newRet.implemented.add(stryctInterface);

				defs.addAll(generateStructs(newRet, packageName2, (MapType) type, isParameter));
				defs.add(newRet);
				f.type = newRet;
				m.body += "this." + f.getName() + " = new " + newRet.getName()
						+ "( (" + f.type.getName() + ") value.get(" + count
						+ "));\n\t\t";
			} else
			{
				f.type = map.possibleEntries.get(p);
				m.body += "this." + f.getName() + " = (" + f.type.getName()
						+ ") value.get(\""+f.getName()+"\");\n\t\t";
			}
			count++;
		}
		returnClass.definitions.add(m);

		m = new Method();
		m.isConstructor = true;
		m.name = returnClass.getName();
		m.body = "";
		returnClass.definitions.add(m);

		return defs;
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
		if (newM.returnType instanceof MapType)
		{
			// MapType returnType = ((MapType) newM.returnType);
			// This code can remove all structs for simple types, but this leads to a problem when encoding again with a
			// unknown name
			// if (returnType.possibleEntries.size() == 1
			// && !(returnType.valueType instanceof ListType || returnType.valueType instanceof MapType))
			// {
			// newM.returnType = returnType.possibleEntries.get(returnType.possibleEntries.keySet().toArray()[0]);
			// addCast = true;
			//
			// } else
			// {
			ClassDefinition returnClass = null;
			returnClass = new ClassDefinition();
			returnClass.setName(m.name + "Struct");
			returnClass.packageName = structPackageName;
			returnClass.imports.add(new Type(Map.class));
			returnClass.imports.add(new Type(List.class));
			returnClass.implemented.add(stryctInterface);
			newM.returnType = returnClass;

			sb.append("new " + returnClass.getName() + "(");
			List<ClassDefinition> defs = generateStructs(returnClass, structPackageName, (MapType) m.returnType);
			defs.add(returnClass);
			printClasses(defs);
			// }

		}

		if (addCast)
		{
			sb.append("(" + newM.returnType.toSource() + ")");
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

		if (addCast)
		{
			sb.append(".values().toArray()[0]");
		} else if (m.returnType instanceof MapType)
		{
			sb.append(")");
		}

		sb.append(";");
		newM.body = sb.toString();

		return newM.toSource();
	}
}
