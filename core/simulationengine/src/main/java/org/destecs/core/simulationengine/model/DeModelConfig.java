package org.destecs.core.simulationengine.model;

import java.io.File;
import java.util.Map.Entry;

public class DeModelConfig extends ModelConfig
{
	public final static String FILE="file";
	public final static String LINK="link";
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
		arguments.put(FILE+fileCount++, file.getAbsolutePath());
	}

	@Override
	public boolean isValid()
	{
		for (Entry<String, String> entry : arguments.entrySet())
		{
			if(entry.getKey().startsWith("file")&& !new File(entry.getValue()).exists())
			{
				return false;
			}
			if(entry.getKey().startsWith("link")&& !new File(entry.getValue()).exists())
			{
				return false;
			}
		}
		return true;
	}

}
