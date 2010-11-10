package org.destecs.core.vdmlink;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class LinksFactory {

	boolean valid = true;
	
	private Map<String, StringPair> link = new Hashtable<String, StringPair>();
	private List<String> outputs = new ArrayList<String>();
	private List<String> inputs = new ArrayList<String>();
	private List<String> events = new ArrayList<String>();
	private List<String> sdp = new ArrayList<String>();

	
	
	public void addEvents(List<String> names){
		events.addAll(names);
	}
	
	public void addOutputs(List<String> names){
		outputs.addAll(names);
	}
	
	public void addInputs(List<String> names){
		inputs.addAll(names);
	}
	
	public void addSDPs(List<String> names){
		sdp.addAll(names);
	}
	
	public void addLink(String name, StringPair pair){
		if(link.containsKey(name)){
			valid = false;
		}
		link.put(name, pair);
	}
	
	public Links getLinks(){
		Links links = new Links(link,outputs,inputs,events,sdp);
		return links;
	}
	
}
