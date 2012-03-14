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

public class DeModelConfig extends ModelConfig
{
	public final static String LOAD_FILE="file";
	public final static String LOAD_LINK="link";
	public static final String LOAD_DEPLOY = "deploy";
	public static final String LOAD_ARCHITECTURE = "architecture";
	public static final String LOAD_REPLACE = "replace";
	public static final String LOAD_BASE_DIR = "basedir";
	public static final String LOAD_DEBUG_PORT = "dbgp_port";
	public static final String LOAD_OUTPUT_DIR = "output_dir";
	// settings
	public static final String LOAD_SETTING_DISABLE_PRE = "settings_disable_pre";
	public static final String LOAD_SETTING_DISABLE_POST = "settings_disable_post";
	public static final String LOAD_SETTING_DISABLE_INV = "settings_disable_inv";
	public static final String LOAD_SETTING_DISABLE_DYNAMIC_TC = "settings_disable_dtc";
	public static final String LOAD_SETTING_DISABLE_MEASURE = "settings_disable_measure";
	
	public static final String LOAD_SETTING_DISABLE_RT_LOG = "settings_disable_rt_log";
	public static final String LOAD_SETTING_DISABLE_COVERAGE = "settings_disable_coverage";
	public static final String LOAD_SETTING_DISABLE_RT_VALIDATOR = "settings_disable_rt_validator";
	
	public static final String LOAD_SETTING_LOG_VARIABLES = "settings_log_variables";
	
	
	
	private int fileCount = 0;
	public DeModelConfig()
	{
	
	}
		
	public DeModelConfig(File file)
	{
		this.arguments.put("file", file.getAbsolutePath());
	}
	
	public void addSpecFile(File file)
	{
		arguments.put(LOAD_FILE+fileCount++, file.getAbsolutePath());
	}

	@Override
	public boolean isValid()
	{
		for (Entry<String, String> entry : arguments.entrySet())
		{
			if(entry.getKey().startsWith(LOAD_FILE)&& !new File(entry.getValue()).exists())
			{
				return false;
			}
			if(entry.getKey().startsWith(LOAD_LINK)&& !new File(entry.getValue()).exists())
			{
				return false;
			}
		}
		return true;
	}

}
