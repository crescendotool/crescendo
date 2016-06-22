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

import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.tools.jprotocolgenerator.ast.SuppressWarningAnnotation.WarningTypes;

public class ClassDefinition extends IInterface
{
	private List<Field> fields = new Vector<Field>();
	public List<ITypeNode> implemented = new Vector<ITypeNode>();
	Method toMapMethod;
	Method toStringMethod;
	Method completeConstructorMethod;

	public ClassDefinition()
	{
		this.typeName = "class";
		
		completeConstructorMethod = new Method();
		completeConstructorMethod.javaDocText="Generated complete field list constructor";
		completeConstructorMethod.isConstructor=true;
		this.definitions.add(completeConstructorMethod);
		

		toMapMethod = new Method();
		toMapMethod.javaDocText = "Generates RPC Map used for parameters from the struct";
		toMapMethod.name = "toMap";
		toMapMethod.returnType = new MapType(new Type(String.class), new Type(Object.class));
		imports.add(new Type(Hashtable.class));
		this.definitions.add(toMapMethod);
		
		toStringMethod = new Method();
		toStringMethod.name="toString";
		toStringMethod.returnType = new Type(String.class);
		toStringMethod.javaDocText="Generated toString which includes fields to default toString/";
		toStringMethod.annotation = new OverrideAnnotation();
		
		this.definitions.add(toStringMethod);
	}

	@Override
	public String toSource()
	{
		fillToMapMethod();
		
		fillToStringMethod();
		
		fillConstructor();
		
		return super.toSource();
	}

	private void fillToMapMethod()
	{
		StringBuilder sb = new StringBuilder();
				
		toMapMethod.returnType= getToMapReturnType();
		toMapMethod.annotation =new SuppressWarningAnnotation(WarningTypes.unchecked);
		sb.append("\n\t\tMap<String," +((MapType)toMapMethod.returnType).valueType.toSource()
				+ "> data = new Hashtable<String," + ((MapType)toMapMethod.returnType).valueType.toSource()
				+ ">();");

		Integer count = Integer.valueOf(0);
//		boolean first = true;
		for (Field f : fields)
		{
			
			String fName = f.getName();

			if (f.type instanceof ListType || f.type instanceof List)
			{
				fName = "tmp" + count;
				imports.add(new Type(Vector.class));
				sb.append("\n\t\t@SuppressWarnings(\"rawtypes\")");
				sb.append("\n\t\tList " + fName + " = new Vector();");
				sb.append("\n\t\tfor( Object o : " + f.getName() + ")");
				sb.append("\n\t\t{");

				sb.append("\n\t\t\tif( o instanceof IStruct)");
				sb.append("\n\t\t\t{");
				sb.append("\n\t\t\t\t"+ fName + ".add(((IStruct)o).toMap());");
				sb.append("\n\t\t\t}");
				sb.append("\n\t\t\telse");
				sb.append("\n\t\t\t{");
				sb.append("\n\t\t\t\t" + fName + ".add(o);");
				sb.append("\n\t\t\t}");
				

				sb.append("\n\t\t}");
			} else if (f.type instanceof MapType || f.type instanceof Map)
			{
				fName += ".toMap()";
			} 

				sb.append("\n\t\tdata.put(\"" + f.getName() + "\"," + fName
						+ ");");
				sb.append("\n\n");

			count++;
		}
		sb.append("\n\t\treturn data;");
		toMapMethod.body = sb.toString();
	}

	private void fillToStringMethod()
	{
		
		StringBuilder sbToString = new StringBuilder();
		
		
		sbToString.append("return ");
		
		boolean first = true;
		for (Field f : fields)
		{
			
			String fName = f.getName();

			if(!first)
			{
				sbToString.append("+");
				sbToString.append("\n\t\t\t\t" +"\"\\n"+fName+": \"+"+fName );
				
			}else
			{
				first = false;
				sbToString.append("\""+fName+": \"+"+fName );
			}
			
		}
				
		sbToString.append(";");
		toStringMethod.body = sbToString.toString();
		
	}

	public ITypeNode getToMapReturnType()
	{
		ITypeNode theType = null;
		for (Field f : fields)
		{
			if (theType == null)
			{
				theType = f.type;
			}
			if (theType != f.type)
			{
				theType = new Type(Object.class);
				break;
			}
		}
		
		if(theType instanceof ListType && ((ListType)theType).type instanceof ClassDefinition)
		{
			ClassDefinition cd = (ClassDefinition) ((ListType)theType).type ;
//			boolean implementsStruct = false;
			for (ITypeNode impl : cd.implemented)
			{
				if(impl.getName().equals("IStruct"))
				{
//					implementsStruct = true;
					break;
				}
			}
			 theType = new ListType( cd.getToMapReturnType());
			
		}
		
		return new MapType(new Type(String.class), theType);
		
	}

	private void fillConstructor()
	{
		StringBuilder sb = new StringBuilder();
		completeConstructorMethod.name = this.getName();
		for (Field f : fields)
		{
			Parameter p = new Parameter(f.type);
			p.name = f.getName();
			completeConstructorMethod.parameters.add(p);
			
			sb.append("\n\t\tthis."+f.getName()+" = "+f.getName()+";");
		}
		completeConstructorMethod.body=sb.toString();
	}

	@Override
	protected void toSourceDefinitions(StringBuilder sb)
	{
		for (IAstNode field : fields)
		{
			sb.append("\n\t");
			sb.append(field.toSource());
			sb.append("\n");
		}
		super.toSourceDefinitions(sb);
	}

	@Override
	protected void addImplemented(StringBuilder sb)
	{
		super.addImplemented(sb);
		if (implemented.size() > 0)
		{
			sb.append(" implements");
			for (ITypeNode type : implemented)
			{
				sb.append(" ");
				sb.append(type.getName());
			}

		}
	}

	@Override
	public String toString()
	{
		return toSource();
	}

	public void addField(Field f)
	{
		this.fields.add(f);
		
		 Collections.sort(this.fields, new Comparator<Field>() {
             @Override
             public int compare(Field lhs, Field rhs) {
                 return lhs.getName().compareTo(rhs.getName());
             }
         });
		
	}
}
