package org.destecs.ide.libraries.store;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.destecs.ide.libraries.ILibrariesConstants;
import org.destecs.ide.libraries.util.PluginFolderInclude;

public class LibStore
{
	static Set<Library> libs;

	public Set<Library> getLibraries()
	{
		// if(libs==null)
		{
			libs = load();

		}
		return libs;
	}

	private Set<Library> load()
	{
		Set<Library> loadedLibs = new TreeSet<Library>();

		URL storeUrl = PluginFolderInclude.getResource(ILibrariesConstants.PLUGIN_ID, "included_libs/store.txt");

		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(new InputStreamReader(storeUrl.openStream()));
			String text = null;

			// repeat until all lines is read
			while ((text = reader.readLine()) != null)
			{
				String pathToLibFolder = "included_libs/" + text;
				URL infoUrl = PluginFolderInclude.getResource(ILibrariesConstants.PLUGIN_ID, pathToLibFolder
						+ "/info.properties");
				if (infoUrl != null)
				{
					Properties props = new Properties();
					try
					{
						props.load(new InputStreamReader(infoUrl.openStream()));
						loadedLibs.add(Library.create(props,pathToLibFolder));
					} catch (FileNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return loadedLibs;
	}
}
