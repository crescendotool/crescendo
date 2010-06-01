package org.destecs.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

	public static final String TYPE_ERROR = "_ERROR_UNKNOWN_TYPE_";

	/**
	 * @param args xml definition file (required), interface name, packagename ,output folder
	 */
	public static void main(String[] args)
	{String interfaceName = null;
			String outputFolder = null;

		StringBuffer sb = new StringBuffer();
		String outputFileName = "";
		try
		{
			String packageName = "org.destecs.protocol";
			String xmlFilePath = args[0];
			
			if(args.length == 4)
			{
				interfaceName = args[1];
				packageName = args[2];
				outputFolder  = args[3];
			}
			
			System.out.println("Interface Generation:");
			System.out.println("\tInterfaceName: "+ interfaceName);
			System.out.println("\tPackageName: "+ packageName);
			System.out.println("\tInput Xml: "+ xmlFilePath);
			System.out.println("\tOutput folder: "+ outputFolder);
			
			
			File file = new File(xmlFilePath);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			outputFileName = cleanName(doc.getDocumentElement().getNodeName());
			sb.append("package "+packageName+";");

			sb.append("\n");
			sb.append("\nimport " + Map.class.getName() + ";");
			sb.append("\nimport " + List.class.getName() + ";");
			sb.append("\n");
			if(interfaceName == null)
			{
				interfaceName = "I"+ outputFileName;
			}
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
					sb.append(getMethodSignature(fstElmnt));
					sb.append("\n");

				}

			}
			sb.append("\n}");
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println(sb.toString());
		FileOutputStream fos;
		try
		{
			File output = null;
			if(outputFolder!=null)
			{
				output = new File(outputFolder,interfaceName+".java");
			}else
			{
				output = new File(interfaceName+".java");
			}
			fos = new FileOutputStream( output, false);

			fos.write(sb.toString().getBytes());
			fos.close();
		} catch (FileNotFoundException e)
		{

			e.printStackTrace();
		} catch (IOException e)
		{

			e.printStackTrace();
		}
	}

	private static String getParams(Element paramsNode, boolean isReturn)
	{
		StringBuilder sb = new StringBuilder();

		// Element paramsNode = (Element) element.getElementsByTagName(TAG_PARAMS).item(0);

		if (!paramsNode.hasChildNodes())
		{
			return Void.class.getName();
		}

		for (Element param : getElements(paramsNode.getElementsByTagName(TAG_PARAM)))
		{
			if (sb.length() > 0)
			{
				sb.append(",");
			}
			sb.append(" " + getParam(param, isReturn));
		}

		return sb.toString().trim();
	}

	private static String getMethodSignature(Element element)
	{
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
					} else if (c2.getNodeName().equals(TAG_PARAMS))
					{

						parameters = getParams(c2, false);
					}

				}

				System.out.println("Generating method: " + name);

			} else if (child.getNodeName().equals(TAG_METHOD_RESPONSE))
			{

				for (Element c2 : getElements(child.getChildNodes()))
				{
					if (c2.getNodeName().equals(TAG_PARAMS))
					{

						returnType = getParams(c2, true);
					}

				}

			}
		}

		if (javaDoc)
		{
			for (Node node : getNodes(element.getChildNodes(), Node.COMMENT_NODE))
			{
				sb.append("\n\t/**");
				sb.append("\n\t* " + node.getNodeValue().replace("\n", "\n\t* "));
				sb.append("\n\t*/");
				sb.append("\n\t");
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
		System.out.println("Method generation completed: " + name);
		return sb.toString();
	}

	private static String getParam(Element param, boolean isReturn)
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
			return String.class.getName();
		}

		Element typeNode = getFirstChildElement(value);

		String type = getType(typeNode);

		if (isReturn)
		{
			return type;
		} else
		{

			return type + " " + name;
		}

	}

	private static String getType(Element typeNode)
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

	private static String getArrayType(Element typeNode)
	{
		Assert.assertEquals(TAG_ARRAY, typeNode.getNodeName());

		Element data = getFirstChildElement(typeNode);

		Assert.assertEquals(TAG_DATA, data.getNodeName());

		StringBuilder sb = new StringBuilder();

		sb.append(List.class.getSimpleName() + "<");

		String rangeType = null;
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
					rangeType = Object.class.getSimpleName();
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

		return sb.toString();
	}

	private static String getStructType(Element typeNode)
	{
		Assert.assertEquals(TAG_STRUCT, typeNode.getNodeName());

		StringBuilder sb = new StringBuilder();

		sb.append(Map.class.getSimpleName());
		sb.append("<");
		sb.append(String.class.getSimpleName());
		sb.append(",");

		String rangeType = null;
		for (Element member : getElements(typeNode.getChildNodes()))
		{
			if (member.getNodeName().equals(TAG_MEMBER))
			{
				for (Element value : getElements(member.getChildNodes()))
				{
					if (value.getNodeName().equals(TAG_VALUE))
					{
						Element nextedTypeElement = getFirstChildElement(value);
						if (rangeType == null)
						{
							rangeType = getType(nextedTypeElement);
						} else if (!rangeType.equals(getType(nextedTypeElement)))
						{
							rangeType = Object.class.getSimpleName();
							break;
						}
					}
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

		return sb.toString();
	}

	private static String getBasicType(Element typeElement)
	{
		if (typeElement.getNodeName().equals(TAG_BOOLEAN))
		{
			return Boolean.class.getSimpleName();
		} else if (typeElement.getNodeName().equals(TAG_STRING))
		{
			return String.class.getSimpleName();
		} else if (typeElement.getNodeName().equals(TAG_INT))
		{
			return Integer.class.getSimpleName();
		} else if (typeElement.getNodeName().equals(TAG_DOUBLE))
		{
			return Double.class.getSimpleName();
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
			System.err.println("Error in name: " + name + " replaced with "
					+ fixedName);
		}
		return fixedName;
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
}
