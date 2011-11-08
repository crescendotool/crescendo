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
package org.destecs.tools.jprotocolgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.destecs.tools.jprotocolgenerator.ast.FreeTextType;
import org.destecs.tools.jprotocolgenerator.ast.IInterface;
import org.destecs.tools.jprotocolgenerator.ast.ITypeNode;
import org.destecs.tools.jprotocolgenerator.ast.ListType;
import org.destecs.tools.jprotocolgenerator.ast.MapType;
import org.destecs.tools.jprotocolgenerator.ast.Method;
import org.destecs.tools.jprotocolgenerator.ast.Parameter;
import org.destecs.tools.jprotocolgenerator.ast.RpcMethodAnnotation;
import org.destecs.tools.jprotocolgenerator.ast.RpcMethodType;
import org.destecs.tools.jprotocolgenerator.ast.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlRpcJavaInterfaceGenerator
{
	static boolean comments = false;
	static boolean javaDoc = true;

	public static final String TAG_METHODS = "methods";
	public static final String TAG_METHOD = "method";
	public static final String TAG_METHOD_CALL = "methodCall";
	public static final String TAG_METHOD_NAME = "methodName";

	public static final String TAG_METHOD_RESPONSE = "methodResponse";
	public static final String TAG_PARAMS = "params";
	public static final String TAG_PARAM = "param";
	public static final String TAG_VALUE = "value";

	public static final String TAG_STRUCT = "struct";
	public static final String TAG_MEMBER = "member";
	public static final String TAG_NAME = "name";

	public static final String TAG_DESCRIPTION = "description";

	public static final String TAG_ARRAY = "array";
	public static final String TAG_DATA = "data";

	public static final String TAG_STRING = "string";
	public static final String TAG_BOOLEAN = "boolean";
	public static final String TAG_INT = "int";
	public static final String TAG_DOUBLE = "double";

	public static final ITypeNode TYPE_ERROR = new Type();

	/**
	 * @param args
	 *            xml definition file (required), interface name, packagename ,output folder
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException
	{
		String interfaceName = null;
		String outputFolder = null;

		StringBuffer sb = new StringBuffer();
		IInterface interfaceNode = new IInterface();
		String outputFileName = "";
		
			String packageName = "org.destecs.protocol";
			String xmlFilePath = args[0];

			if (args.length == 4)
			{
				interfaceName = args[1];
				packageName = args[2];
				outputFolder = args[3];
			}

			System.out.println("Interface Generation:");
			System.out.println("\tInterfaceName: " + interfaceName);
			System.out.println("\tPackageName: " + packageName);
			System.out.println("\tInput Xml: " + xmlFilePath);
			System.out.println("\tOutput folder: " + outputFolder);

			File file = new File(xmlFilePath);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			outputFileName = cleanName(doc.getDocumentElement().getNodeName());

			interfaceNode.packageName = packageName;
			sb.append("package " + packageName + ";");

			sb.append("\n");

			interfaceNode.imports.add(new Type(Map.class));
			interfaceNode.imports.add(new Type(List.class));
			interfaceNode.imports.add(new RpcMethodType());

			sb.append("\nimport " + Map.class.getName() + ";");
			sb.append("\nimport " + List.class.getName() + ";");
			sb.append("\n");
			if (interfaceName == null)
			{
				interfaceName = "I" + outputFileName;
			}

			interfaceNode.setName(interfaceName);

			sb.append("\npublic interface " + interfaceName);
			sb.append("\n{");

			NodeList nodeLst = doc.getElementsByTagName("method");

			for (int s = 0; s < nodeLst.getLength(); s++)
			{

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE)
				{

					Element fstElmnt = (Element) fstNode;

					sb.append("\n\t");
					// sb.append(getMethodSignature(fstElmnt));
					interfaceNode.definitions.add(getMethodSignature(fstElmnt));
					sb.append("\n");

				}

			}
			sb.append("\n}");
			sb = new StringBuffer();
			sb.append(interfaceNode.toSource());
		

//		System.out.println(sb.toString());
//
//		System.out.println("\n\n");
		new GenerateProxy().generate(interfaceNode, outputFolder);

		List<IInterface> interfaceDefinitions = unpackGroups(interfaceNode);

		saveFile(interfaceName, outputFolder, interfaceNode);

		for (IInterface iInterface : interfaceDefinitions)
		{
			saveFile(iInterface.getName(), outputFolder, iInterface);
		}

	}

	private static List<IInterface> unpackGroups(IInterface interfaceNode)
	{
		List<String> groups = new Vector<String>();
		for (Method m : interfaceNode.definitions)
		{
			if (m.group != null && !groups.contains(m.group)
					&& m.group.trim().length() > 0)
			{
				groups.add(m.group);
			}
		}

		List<IInterface> interfaces = new Vector<IInterface>();
		List<Method> medthods = new Vector<Method>();
		for (String group : groups)
		{
			IInterface intf = new IInterface();
			intf.imports = interfaceNode.imports;
			intf.packageName = interfaceNode.packageName;
			intf.setName(getValidJavaName("I"
					+ firstCharToUpper(makeJavaName(group), true)));
			for (Method m : interfaceNode.definitions)
			{
				if (m.group != null && m.group.equals(group))
				{
					intf.definitions.add(m);
					medthods.add(m);
				}
			}
			interfaceNode.extended.add(intf);
			interfaces.add(intf);
		}

		// remove all grouped from source interface
		interfaceNode.definitions.removeAll(medthods);

		return interfaces;

	}

	private static void saveFile(String fileName, String outputFolder,
			IInterface intf)
	{
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
			output = new File(folder, fileName + ".java");
			fos = new FileOutputStream(output, false);

			fos.write(intf.toSource().getBytes());
			fos.close();
		} catch (FileNotFoundException e)
		{

			e.printStackTrace();
		} catch (IOException e)
		{

			e.printStackTrace();
		}
	}

	private static List<Parameter> getParams(Element paramsNode,
			boolean isReturn)
	{
		// StringBuilder sb = new StringBuilder();

		List<Parameter> parameters = new Vector<Parameter>();
		// Element paramsNode = (Element) element.getElementsByTagName(TAG_PARAMS).item(0);

		if (!paramsNode.hasChildNodes())
		{
			parameters.add(new Parameter(new Type(Void.class)));
		} else
		{

			for (Element param : getElements(paramsNode.getElementsByTagName(TAG_PARAM)))
			{
				// if (sb.length() > 0)
				// {
				// sb.append(",");
				// }
				// sb.append(" " + getParam(param, isReturn));
				parameters.add(getParam(param, isReturn));
			}
		}
		return parameters;// sb.toString().trim();
	}

	private static Method getMethodSignature(Element element)
	{
		Method method = new Method();
		method.throwsTypes.add(new FreeTextType(Exception.class.getName()));
		StringBuilder sb = new StringBuilder();

		String name = "";
		String parameters = "";
		String returnType = "";

		for (Element child : getElements(element.getChildNodes()))
		{
			if (child.getNodeName().equals(TAG_METHOD_CALL))
			{
				for (Element c2 : getElements(child.getChildNodes()))
				{
					if (c2.getNodeName().equals(TAG_METHOD_NAME))
					{
						name = c2.getFirstChild().getNodeValue();
						method.name = getValidJavaName(makeJavaName(removeGroup(name)));
						method.group = getGroup(name);
						if (!method.name.equals(name))
						{
							method.annotation = new RpcMethodAnnotation(name);
						}
					} else if (c2.getNodeName().equals(TAG_PARAMS))
					{

						// parameters = getParams(c2, false);
						method.parameters = getParams(c2, false);
					}

				}

//				System.out.println("Generating method: " + name);

			} else if (child.getNodeName().equals(TAG_METHOD_RESPONSE))
			{

				for (Element c2 : getElements(child.getChildNodes()))
				{
					if (c2.getNodeName().equals(TAG_PARAMS))
					{

						// returnType = getParams(c2, true);
						method.returnType = getParams(c2, true).get(0).type;
					}

				}

			}else if(javaDoc && child.getNodeName().equals(TAG_DESCRIPTION) )
			{
				method.javaDocText = child.getFirstChild().getNodeValue().replace("\n", "\n\t* ");
			}
		}

		if (javaDoc)
		{
			for (Node node : getNodes(element.getChildNodes(), Node.COMMENT_NODE))
			{
				StringBuilder doc = new StringBuilder();
				doc.append("\n\t/**");
				doc.append("\n\t* "
						+ node.getNodeValue().replace("\n", "\n\t* "));
				doc.append("\n\t*/");
				// doc.append("\n\t");
				method.javaDoc = doc.toString();
			}
		}
		sb.append(returnType);
		sb.append(" ");
		sb.append(cleanName(name));
		sb.append("(");
		if (parameters != null && parameters.length() > 0)
		{
			sb.append(parameters);
		}
		sb.append(");");
//		System.out.println("Method generation completed: " + name);
		return method;// sb.toString();
	}

	private static String removeGroup(String name)
	{
		if (name.contains("."))
		{
			return name.substring(name.lastIndexOf('.') + 1);
		}
		return name;
	}

	private static String getGroup(String name)
	{
		if (name.contains("."))
		{
			return name.substring(0, name.lastIndexOf('.'));
		}
		return "";
	}

	private static String makeJavaName(String name)
	{
		StringBuilder sb = new StringBuilder();
		if (name.contains("."))
		{
			int count = 0;
			for (String part : name.split("\\."))
			{
				if (count > 0)
				{
					sb.append("_");
				}
				sb.append(firstCharToUpper(part, false));

				count++;
			}
		} else
		{
			sb.append(name);
		}
		return firstCharToUpper(sb.toString(), false);
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

	private static Parameter getParam(Element param, boolean isReturn)
	{

		Element value = null;// (Element)getFirstChildElement( param);// .getElementsByTagName(TAG_VALUE).item(0);

		String name = "data";

		for (Element child : getElements(param.getChildNodes()))
		{
			if (child.getNodeName().equals(TAG_NAME))
			{
				name = child.getFirstChild().getNodeValue();
			} else if (child.getNodeName().equals(TAG_VALUE))
			{
				value = child;
			}
		}

		if (!value.getNodeName().equals(TAG_VALUE))
		{
			System.err.println("Invalid param value node: " + value);
		}

		if (!value.hasChildNodes())
		{
			return new Parameter(new Type(String.class), getValidJavaName(name));

		} else
		{

			Element typeNode = getFirstChildElement(value);

			ITypeNode type = getType(typeNode);

			if (isReturn)
			{
				return new Parameter(type);
			} else
			{
				return new Parameter(type, getValidJavaName(name));

			}
		}

	}

	private static ITypeNode getType(Element typeNode)
	{
		if (typeNode.getNodeName().equals(TAG_BOOLEAN)
				|| typeNode.getNodeName().equals(TAG_STRING)
				|| typeNode.getNodeName().equals(TAG_INT)
				|| typeNode.getNodeName().equals(TAG_DOUBLE))
		{
			return getBasicType(typeNode);
		} else if (typeNode.getNodeName().equals(TAG_ARRAY))
		{
			return getArrayType(typeNode);
		} else if (typeNode.getNodeName().equals(TAG_STRUCT))
		{
			return getStructType(typeNode);
		}

		return TYPE_ERROR;
	}

	private static ITypeNode getArrayType(Element typeNode)
	{
		Assert.assertEquals(TAG_ARRAY, typeNode.getNodeName());

		Element data = getFirstChildElement(typeNode);

		Assert.assertEquals(TAG_DATA, data.getNodeName());

		StringBuilder sb = new StringBuilder();

		sb.append(List.class.getSimpleName() + "<");

		ITypeNode rangeType = null;
		for (Element value : getElements(data.getChildNodes()))
		{
			if (value.getNodeName().equals(TAG_VALUE))
			{
				Element nextedTypeElement = getFirstChildElement(value);
				if (rangeType == null)
				{
					rangeType = getType(nextedTypeElement);
				} else if (!rangeType.equals(getType(nextedTypeElement)))
				{
					rangeType = new Type(Object.class);
					break;
				}
			}
		}

		if (rangeType == null)
		{
			System.err.println("Error at node: " + getNodePath(typeNode));
			rangeType = TYPE_ERROR;
		}

		sb.append(rangeType);
		sb.append(">");

		return new ListType(rangeType);// sb.toString();
	}

	private static ITypeNode getStructType(Element typeNode)
	{
		Assert.assertEquals(TAG_STRUCT, typeNode.getNodeName());

		StringBuilder sb = new StringBuilder();
		MapType mapType = new MapType();
		sb.append(Map.class.getSimpleName());
		sb.append("<");
		sb.append(String.class.getSimpleName());
		sb.append(",");

		ITypeNode rangeType = null;
		for (Element member : getElements(typeNode.getChildNodes()))
		{
			if (member.getNodeName().equals(TAG_MEMBER))
			{
				String possibleName = null;
				ITypeNode possibleType = null;

				for (Element value : getElements(member.getChildNodes()))
				{
					if (value.getNodeName().equals(TAG_VALUE))
					{
						Element nextedTypeElement = getFirstChildElement(value);
						possibleType = getType(nextedTypeElement);
						if (rangeType == null)
						{
							rangeType = possibleType;

						} else if (!rangeType.equals(possibleType))
						{
							rangeType = new Type(Object.class);
							break;
						}
					} else if (value.getNodeName().equals(TAG_NAME))
					{
						if (value.hasChildNodes())
						{
							possibleName = value.getFirstChild().getNodeValue();
						} else
						{
							possibleName = "";
						}
					}
				}
				if (possibleName != null
						&& !mapType.possibleEntries.containsKey(possibleName))
				{
					mapType.possibleEntries.put(possibleName, possibleType);
				}
			}
		}

		if (rangeType == null)
		{
			System.err.println("Error at node: " + getNodePath(typeNode));
			rangeType = TYPE_ERROR;
		}

		sb.append(rangeType);
		sb.append(">");

		mapType.keyType = new Type(String.class);
		mapType.valueType = rangeType;

		return mapType; // new MapType(new Type(String.class),rangeType); //sb.toString();
	}

	private static ITypeNode getBasicType(Element typeElement)
	{
		if (typeElement.getNodeName().equals(TAG_BOOLEAN))
		{
			return new Type(Boolean.class);
		} else if (typeElement.getNodeName().equals(TAG_STRING))
		{
			return new Type(String.class);
		} else if (typeElement.getNodeName().equals(TAG_INT))
		{
			return new Type(Integer.class);
		} else if (typeElement.getNodeName().equals(TAG_DOUBLE))
		{
			return new Type(Double.class);
		}

		return null;
	}

	public static List<Element> getElements(NodeList nodeList)
	{
		List<Element> elements = new Vector<Element>();
		NodeList childs = nodeList;
		for (int j = 0; j < childs.getLength(); j++)
		{
			Node cNode = childs.item(j);
			if (cNode.getNodeType() == Node.ELEMENT_NODE)
			{
				elements.add((Element) cNode);
			}
		}
		return elements;
	}

	public static List<Node> getNodes(NodeList nodeList, short fileter)
	{
		List<Node> elements = new Vector<Node>();
		NodeList childs = nodeList;
		for (int j = 0; j < childs.getLength(); j++)
		{
			Node cNode = childs.item(j);
			if (cNode.getNodeType() == fileter)
			{
				elements.add(cNode);
			}
		}
		return elements;
	}

	public static Element getFirstChildElement(Node node)
	{
		for (Element element : getElements(node.getChildNodes()))
		{
			return element;
		}
		return null;
	}

	private static String cleanName(String name)
	{
		String fixedName = name.replace('.', '_').replaceAll("_", "").replaceAll("-", "");
		if (name.contains(".") || name.contains("_") || name.contains("-"))
		{
			System.out.println("Name normalized: " + padToWidth(name,' ',25) + " -> "
					+ fixedName);
		}
		return fixedName;
	}
	
	public static String padToWidth(String name,char padChar,int length)
	{
		StringBuilder sb = new StringBuilder(name);
		while(sb.length()<length)
		{
			sb.append(padChar);
		}
		return sb.toString();
	}

	public static StringBuffer getNodePath(Node node)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(node.getNodeName());
		if (node.getParentNode() != null
				&& !(node.getParentNode() instanceof Document))
		{
			sb.append(" -> " + getNodePath(node.getParentNode()));
		}
		return sb;
	}

	public static String getValidJavaName(String name)
	{
		final String[] reservedWords = { "abstract", "assert", "boolean",
				"break", "byte", "case", "catch", "char", "class", "const*",
				"continue", "default", "double", "do", "else", "enum",
				"extends", "false", "final", "finally", "float", "for",
				"goto*", "if", "implements", "" + "import", "instanceof",
				"int", "interface", "long", "native", "new", "null", "package",
				"private", "protected", "public", "return", "short", "static",
				"strictfp", "super", "switch", "synchronized", "this", "throw",
				"throws", "transient", "" + "true", "try", "void", "volatile",
				"while" };
		name = name.trim();
		for (String reserved : reservedWords)
		{
			if (reserved.equals(name))
			{
				return name + "_";
			}
		}
		return name;
	}
}
