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
package org.destecs.vdm.utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ShareCoSimGroup
{
	private static final String TAG_KEY = "name";
	private static final String TAG_VALUE = "value";
	private String name;
	Map<String, Object> sharedDefinitions = new HashMap<String, Object>();

	public ShareCoSimGroup(String name)
	{
		this.name = name;
	}

	public void add(String name, Object value)
	{
		if (sharedDefinitions.containsKey(name))
		{
			sharedDefinitions.remove(name);
		}

		sharedDefinitions.put(name, value);
	}
	
	private List<Map<String, Object>> getValue()
	{
		List<Map<String, Object>> definitions = new Vector<Map<String, Object>>();
		
		for (String key : sharedDefinitions.keySet())
		{
			Map<String, Object> m = new HashMap<String, Object>();
			m.put(TAG_KEY, key);
			m.put(TAG_VALUE, sharedDefinitions.get(key));
			definitions.add(m);
		}
		
		return definitions;
	}
	
	public Map<String, List<Map<String, Object>>> encode()
	{
		Map<String, List<Map<String, Object>>> map = new HashMap<String, List<Map<String,Object>>>();
		
		map.put(name, getValue());
		
		return map;
	}
}
