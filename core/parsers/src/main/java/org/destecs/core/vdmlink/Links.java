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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Links
{
	private final Map<String, LinkInfo> link;
	private final List<String> outputs;
	private final List<String> inputs;
	private final List<String> events;
	private final List<String> designParameters;
	private final List<String> model;

	public Links(Map<String, LinkInfo> link, List<String> outputs,
			List<String> inputs, List<String> events,
			List<String> designParameters, List<String> model)
	{

		this.link = link;
		this.outputs = outputs;
		this.inputs = inputs;
		this.events = events;
		this.designParameters = designParameters;
		this.model = model;

	}

	public Map<String, LinkInfo> getLinks()
	{
		return link;
	}

	public Map<String, LinkInfo> getSharedDesignParameters()
	{
		Map<String, LinkInfo> result = new HashMap<String, LinkInfo>();

		for (String dp : designParameters)
		{
			result.put(dp, link.get(dp));
		}
		return result;
	}
	
	public List<String> getSharedDesignParametersList()
	{
		return designParameters;
	}

	public Map<String, LinkInfo> getOutputs()
	{
		Map<String, LinkInfo> result = new HashMap<String, LinkInfo>();

		for (String output : outputs)
		{
			result.put(output, link.get(output));
		}
		return result;
	}

	public List<String> getOutputsList()
	{
		return outputs;
	}
	
	public Map<String, LinkInfo> getInputs()
	{

		Map<String, LinkInfo> result = new HashMap<String, LinkInfo>();

		for (String input : inputs)
		{
			result.put(input, link.get(input));
		}
		return result;
	}
	
	public List<String> getInputsList()
	{
		return inputs;
	}
	
	public Map<String, LinkInfo> getModel()
	{

		Map<String, LinkInfo> result = new HashMap<String, LinkInfo>();

		for (String m : model)
		{
			result.put(m, link.get(m));
		}
		return result;
	}

	public Map<String, LinkInfo> getEvents()
	{
		Map<String, LinkInfo> result = new HashMap<String, LinkInfo>();

		for (String event : events)
		{
			result.put(event, link.get(event));
		}
		return result;
	}

	public List<String> getEventsList()
	{
		return events;
	}
	
	@Deprecated
	public StringPair getBoundVariable(String name)
	{
		if (link.containsKey(name))
		{
			LinkInfo lInfo = link.get(name);
			return lInfo.getBoundedVariable();
		}
		return null;
	}
	
	public LinkInfo getBoundVariableInfo(String name)
	{
		if (link.containsKey(name))
		{
			LinkInfo lInfo = link.get(name);
			return lInfo;
		}
		return null;
	}

	public List<String> getQualifiedName(String name)
	{
		if (link.containsKey(name))
		{
			LinkInfo lInfo = link.get(name);
			return lInfo.getQualifiedName();
		}
		return null;
	}
}
