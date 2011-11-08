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
package org.destecs.core.vdmlink;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class LinksFactory {

	private Map<String, LinkInfo> link = new Hashtable<String,LinkInfo>();
	private List<String> outputs = new ArrayList<String>();
	private List<String> inputs = new ArrayList<String>();
	private List<String> events = new ArrayList<String>();
	private List<String> sdp = new ArrayList<String>();	
	
	
	public void addEvent(String name){
		events.add(name);
	}
	
	public void addEvents(List<String> names){
		events.addAll(names);
	}
	
	public void addOutput(String name){
		outputs.add(name);
	}
	
	public void addOutputs(List<String> names){
		outputs.addAll(names);
	}
	
	public void addInput(String name){
		inputs.add(name);
	}
	
	public void addInputs(List<String> names){
		inputs.addAll(names);
	}
	
	public void addSDP(String name){
		sdp.add(name);
	}
	
	public void addSDPs(List<String> names){
		sdp.addAll(names);
	}
	
	public void addLink(String name, LinkInfo lInfo){
//		if(link.containsKey(name)){
//			valid = false;
//		}
		link.put(name, lInfo);
	}
	
	public Links getLinks(){
		Links links = new Links(link,outputs,inputs,events,sdp);
		return links;
	}
	
}
