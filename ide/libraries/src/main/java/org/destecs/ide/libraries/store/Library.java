package org.destecs.ide.libraries.store;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class Library
{
	public final String name;
	public final String description;
	public final String version;
	public final List<String> deFiles = new Vector<String>();
	public final List<String> ctFiles = new Vector<String>();
	public final String pathToFileRoot;

	public Library(String name, String description, String version,
			List<String> deFiles, List<String> ctFiles,String pathToFileRoot)
	{
		this.name = name;
		this.description = description;
		this.version = version;
		this.deFiles.addAll(deFiles);
		this.ctFiles.addAll(ctFiles);
		this.pathToFileRoot = pathToFileRoot;
	}

	public static Library create(Properties props, String pathToFileRoot)
	{
		return new Library(props.getProperty("Name"), props.getProperty("Description"), props.getProperty("Version"), getList(props.getProperty("DEFiles")), getList(props.getProperty("CTFiles")),pathToFileRoot);
	}

	private static List<String> getList(String property)
	{
		List<String> files = new Vector<String>();
		for(String f:property.split(","))
		{
			if(f!=null && !f.trim().isEmpty())
			{
				files.add(f);
			}
		}
		return files;
	}
	
	@Override
	public String toString()
	{
	return name +" (v"+version+")"+" - "+ description;
	}
}


