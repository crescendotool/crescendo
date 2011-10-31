package org.destecs.core.vdmlink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Links {
	private final Map<String, LinkInfo> link;
	private final List<String> outputs;
	private final List<String> inputs;
	private final List<String> events;
	private final List<String> designParameters;

	public Links(Map<String, LinkInfo> link, List<String> outputs,
			List<String> inputs, List<String> events,
			List<String> designParameters) {
		
		this.link = link;
		this.outputs = outputs;
		this.inputs = inputs;
		this.events = events;
		this.designParameters = designParameters;
		
	}


	public Map<String, LinkInfo> getLinks() {
		return link;
	}

	public Map<String, LinkInfo> getSharedDesignParameters() {
		Map<String, LinkInfo> result = new HashMap<String, LinkInfo>();
		
		for (String dp : designParameters) {
			result.put(dp, link.get(dp));
		}
		return result;
	}
	
	public Map<String, LinkInfo> getOutputs() {
		Map<String, LinkInfo> result = new HashMap<String, LinkInfo>();
		
		for (String output : outputs) {
			result.put(output, link.get(output));
		}
		return result;
	}

	public Map<String, LinkInfo> getInputs() {
		
		Map<String, LinkInfo> result = new HashMap<String, LinkInfo>();
		
		for (String input : inputs) {
			result.put(input, link.get(input));
		}
		return result;
	}

	public Map<String, LinkInfo> getEvents() {
		Map<String, LinkInfo> result = new HashMap<String, LinkInfo>();
		
		for (String event : events) {
			result.put(event, link.get(event));
		}
		return result;
	}

	public StringPair getBoundVariable(String name) {
		if (link.containsKey(name)) {
			LinkInfo lInfo = link.get(name);
			return lInfo.getBoundedVariable();
		}
		return null;
	}

	private String print(List<String> qualifiedName) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i < qualifiedName.size()-1;i++) {
			sb.append(qualifiedName.get(i));
			sb.append(".");
		}
		sb.append(qualifiedName.get(qualifiedName.size()));
		return sb.toString();		
		
	}


	


	public List<String> getQualifiedName(String name) {
		if (link.containsKey(name)) {
			LinkInfo lInfo = link.get(name);
			return lInfo.getQualifiedName();
		}
		return null;
	}
}
