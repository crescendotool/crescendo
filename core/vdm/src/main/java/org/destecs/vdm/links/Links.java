package org.destecs.vdm.links;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;



public class Links
{
	private final Map<String, StringPair> link = new Hashtable<String, StringPair>();
	private final  List<String> outputs = new Vector<String>();
	private final  List<String> inputs = new Vector<String>();
	private final  List<String> events = new Vector<String>();
	private final  Map<String,Double> sharedDesignParameters = new Hashtable<String,Double>();
	
	public Links()
	{
		link.put("level", new StringPair("levelSensor", "level"));
		link.put("valve", new StringPair("valveActuator", "valveState"));
		
		outputs.add("valve");
		inputs.add("level");
		sharedDesignParameters.put("maxlevel",new Double(3));
		sharedDesignParameters.put("minlevel",new Double(2));
		
		link.put("maxlevel", new StringPair("Controller", "maxLevel"));
		link.put("minlevel", new StringPair("Controller", "minLevel"));
		
		events.add("high");
		events.add("low");
		
		link.put("high", new StringPair("-", "-"));
		link.put("low", new StringPair("-", "-"));
	}
	
	public Map<String, StringPair> getLinks()
	{
		return link;
	}
	
	public List<String> getOutputs()
	{
		return outputs;
	}
	public List<String> getInputs()
	{
		return inputs;
	}
	public List<String> getEvents()
	{
		return events;
	}
	
	public StringPair getBoundVariable(String name)
	{
		if(link.containsKey(name))
		{
			return link.get(name);
		}
		return null;
	}
	
	public List<String> getSharedDesignParameters()
	{
		List<String> names = new Vector<String>();
		for (String string : sharedDesignParameters.keySet())
		{
			names.add(string);
		}
		return names;
	}
}
