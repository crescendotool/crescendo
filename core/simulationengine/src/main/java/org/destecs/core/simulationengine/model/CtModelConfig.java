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
package org.destecs.core.simulationengine.model;

import java.io.File;
import java.util.Map.Entry;

public class CtModelConfig extends ModelConfig
{
	boolean remote = false;
	
//	public static final String LOAD_SETTING_LOG_VARIABLES = "settings_log_variables";
	
	/**
	 * Constructor for local simulation
	 */
	public CtModelConfig(File file)
	{
		this.arguments.put("file", file.getAbsolutePath());
	}
	
	/**
	 * Custom constructor for remote ct simulation
	 */
	public CtModelConfig(String remoteBase)
	{
		this.remote = true;
		this.arguments.put("file", remoteBase);
	}
	
	
	@Override
	public boolean isValid()
	{
		if(!remote)
		{	
			for (Entry<String, String> entry : arguments.entrySet())
			{
				if (entry.getKey().startsWith("file")) 
				{
					if((!(new File(entry.getValue()).exists()) && !(new File(entry.getValue()).isDirectory()))){
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return this.arguments.get("file");
	}
}
