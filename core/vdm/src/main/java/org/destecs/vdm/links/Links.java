//package org.destecs.vdm.links;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Hashtable;
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Vector;
//
//public class Links
//{
//	private final Map<String, StringPair> link = new Hashtable<String, StringPair>();
//	private final List<String> outputs = new Vector<String>();
//	private final List<String> inputs = new Vector<String>();
//	private final List<String> events = new Vector<String>();
//	private final List<String> designParameters = new Vector<String>();
//
//	// public Links()
//	// {
//	// link.put("level", new StringPair("levelSensor", "level"));
//	// link.put("valve", new StringPair("valveActuator", "valveState"));
//	//
//	// outputs.add("valve");
//	// inputs.add("level");
//	//
//	// link.put("maxlevel", new StringPair("Controller", "maxLevel"));
//	// link.put("minlevel", new StringPair("Controller", "minLevel"));
//	//
//	// designParameters.add("maxlevel");
//	// designParameters.add("maxlevel");
//	//
//	// events.add("high");
//	// events.add("low");
//	//
//	// link.put("high", new StringPair("-", "-"));
//	// link.put("low", new StringPair("-", "-"));
//	// }
//
//	public static Links load(File file) throws FileNotFoundException,
//			IOException
//	{
//		Properties props = new Properties();
//		props.load(new FileReader(file));
//		final String VDMCO_OUTPUTS = "vdmco.outputs";
//		final String VDMCO_INPUTS = "vdmco.inputs";
//		final String VDMCO_SHARED_DESIGN_PARAMETERS = "vdmco.shared-design-parameters";
//		final String VDMCO_EVENTS = "vdmco.events";
//		Links links = new Links();
//		for (Object key : props.keySet())
//		{
//			String keyName = key.toString();
//			if (keyName.equalsIgnoreCase(VDMCO_EVENTS))
//			{
//				decode(props.getProperty(keyName), links.events);
//				continue;
//			} else if (keyName.equalsIgnoreCase(VDMCO_INPUTS))
//			{
//				decode(props.getProperty(keyName), links.inputs);
//				continue;
//			} else if (keyName.equalsIgnoreCase(VDMCO_OUTPUTS))
//			{
//				decode(props.getProperty(keyName), links.outputs);
//				continue;
//			} else if (keyName.equalsIgnoreCase(VDMCO_SHARED_DESIGN_PARAMETERS))
//			{
//				decode(props.getProperty(keyName), links.designParameters);
//				continue;
//			} else if (keyName.startsWith("--") || keyName.startsWith("//")
//					|| keyName.startsWith("#"))
//			{
//				// comment
//				continue;
//			}
//
//			// no match must be a link then
//			String[] data = props.getProperty(keyName).split("\\.");
//			if (data.length == 2)
//			{
//				StringPair pair = new StringPair(data[0], data[1]);
//				links.link.put(keyName, pair);
//			}
//		}
//
//		return links;
//	}
//
//	private static void decode(String data, List<String> destination)
//	{
//		for (String string : data.split("\\,"))
//		{
//			destination.add(string);
//		}
//	}
//
//	public Map<String, StringPair> getLinks()
//	{
//		return link;
//	}
//
//	public List<String> getOutputs()
//	{
//		return outputs;
//	}
//
//	public List<String> getInputs()
//	{
//		return inputs;
//	}
//
//	public List<String> getEvents()
//	{
//		return events;
//	}
//
//	public StringPair getBoundVariable(String name)
//	{
//		if (link.containsKey(name))
//		{
//			return link.get(name);
//		}
//		return null;
//	}
//
//	public List<String> getSharedDesignParameters()
//	{
//		// List<String> names = new Vector<String>();
//		// for (String string : sharedDesignParameters.keySet())
//		// {
//		// names.add(string);
//		// }
//		// return names;
//		return designParameters;
//	}
//}
