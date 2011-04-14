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
	public static final String LOAD_DEBUG_PORT = "dbgp_port";
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
