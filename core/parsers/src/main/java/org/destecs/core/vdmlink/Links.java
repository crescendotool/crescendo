package org.destecs.core.vdmlink;

import java.util.List;
import java.util.Map;

public class Links {
	private final Map<String, StringPair> link;
	private final List<String> outputs;
	private final List<String> inputs;
	private final List<String> events;
	private final List<String> designParameters;

	public Links(Map<String, StringPair> link, List<String> outputs,
			List<String> inputs, List<String> events,
			List<String> designParameters) {
		
		this.link = link;
		this.outputs = outputs;
		this.inputs = inputs;
		this.events = events;
		this.designParameters = designParameters;
		
	}


	public Map<String, StringPair> getLinks() {
		return link;
	}

	public List<String> getOutputs() {
		return outputs;
	}

	public List<String> getInputs() {
		return inputs;
	}

	public List<String> getEvents() {
		return events;
	}

	public StringPair getBoundVariable(String name) {
		if (link.containsKey(name)) {
			return link.get(name);
		}
		return null;
	}

	public List<String> getSharedDesignParameters() {
		// List<String> names = new Vector<String>();
		// for (String string : sharedDesignParameters.keySet())
		// {
		// names.add(string);
		// }
		// return names;
		return designParameters;
	}
}
