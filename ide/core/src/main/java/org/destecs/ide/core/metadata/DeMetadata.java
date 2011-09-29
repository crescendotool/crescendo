package org.destecs.ide.core.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.destecs.core.vdmlink.Links;
import org.destecs.core.vdmlink.StringPair;
import org.destecs.ide.core.resources.IDestecsProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class DeMetadata
{

	private Map<String, List<String>> vdmMetadata = new Hashtable<String, List<String>>();
	private Links links = null;
	private IDestecsProject project;
	private List<String> errorMsgs = new ArrayList<String>();
	private String systemClass = null;

	public DeMetadata(Links links, IDestecsProject project)
	{
		this.links = links;
		this.project = project;
	}

	private void loadVdmMetadata(IDestecsProject p) throws IOException,
			CoreException
	{

		Properties props = new Properties();
		IFile file = p.getVdmModelFolder().getFile(".metadata");

		if (file.exists())
		{
			props.load(file.getContents());

			for (Entry<Object, Object> entry : props.entrySet())
			{
				vdmMetadata.put(entry.getKey().toString(), Arrays.asList(entry.getValue().toString().split(",")));
			}
		}

	}

	public synchronized void checkLinks()
	{
		try
		{
			loadVdmMetadata(project);
			matchLinksAndMetaData();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	private void matchLinksAndMetaData()
	{

		String systemClass = findSystemClass();
		
		if(systemClass == null)
		{
			errorMsgs.add("The VDM-RT model does not contain a system class");
			return;
		}
		
		printMetadata();
		
		for (String key : links.getLinks().keySet())
		{
			StringPair p = links.getLinks().get(key);

			String keyToFind = systemClass + "." + p.toString();
			System.out.println("Trying to find key: " + keyToFind);
			if (!vdmMetadata.containsKey(keyToFind))
			{
				// System.out.println(p.toString()
				// + " not present in the metadata");
				errorMsgs.add(p.toString()
						+ " does not exist in the VDM model or it is not at real number");
			}
		}
	}

	private String findSystemClass() {
		for(Entry<String, List<String>> entry : vdmMetadata.entrySet())
		{
			if(entry.getValue().size() > 0)
			{
				String first = entry.getValue().get(0);
				if(first.equals("_system"))
				{
					return entry.getKey();
				}
			}
		}
		return null;
	}

	public List<String> getErrorMsgs()
	{
		return errorMsgs;
	}

	public void printMetadata()
	{
		System.out.println("---- PRINTING TRIMMED METADATA ----");
		for (String key : vdmMetadata.keySet())
		{
			System.out.println(key + ": " + vdmMetadata.get(key));
		}
	}

}
