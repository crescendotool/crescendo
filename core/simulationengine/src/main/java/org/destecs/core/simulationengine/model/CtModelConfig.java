package org.destecs.core.simulationengine.model;

import java.io.File;
import java.util.Map.Entry;

public class CtModelConfig extends ModelConfig
{
	
	public static final String LOAD_SETTING_LOG_VARIABLES = "settings_log_variables";
	
	public CtModelConfig(File file)
	{
		this.arguments.put("file", file.getAbsolutePath());
	}
	
	
	@Override
	public boolean isValid()
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
		return true;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.arguments.get("file");
	}
}
