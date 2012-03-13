package org.destecs.ide.debug.launching.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.MessageDialog;

public class LaunchStore
{
	public static class LaunchStoreConfig implements Serializable
	{
		/**
		 * Serializable id
		 */
		private static final long serialVersionUID = -5056038290485875316L;
		@SuppressWarnings("rawtypes")
		public Map attributes;
		public String type;
		public String name;
		
		public LaunchStoreConfig()
		{
			//Needed for serilization
		}
		
		public LaunchStoreConfig(String name, String type,
				@SuppressWarnings("rawtypes") Map attributes)
		{
			this.name = name;
			this.type = type;
			this.attributes = attributes;
		}
		
	}
	public static void store(ILaunchConfiguration configuration, File file)
			throws CoreException
	{
	        try
			{
	        	LaunchStoreConfig conf = new LaunchStoreConfig(configuration.getName(),configuration.getType().getIdentifier(),configuration.getAttributes());
	        	File outFile = new File(file,configuration.getName()+".dlaunch");
	        	FileOutputStream out = new FileOutputStream(outFile);
	 	        ObjectOutputStream objOut = new ObjectOutputStream(out);
	 	        objOut.writeObject(conf);
				objOut.close();
			} catch (IOException e)
			{
				DestecsDebugPlugin.logWarning("Failed to store launch configuration shapshot", e);
			}
	}

	public static ILaunchConfiguration load(InputStream inputStream) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream objIn = new ObjectInputStream(inputStream);
		LaunchStoreConfig conf = (LaunchStoreConfig) objIn.readObject();
        return createLaunchConfiguration(conf);
	}
	
	@SuppressWarnings("rawtypes")
	private static ILaunchConfiguration createLaunchConfiguration(LaunchStoreConfig conf)
	{
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try
		{
			ILaunchConfigurationType configType = getConfigurationType(conf.type);
			wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName("Generated_"+conf.name));
			for ( Object attKey : conf.attributes.keySet())
			{
				Object value = conf.attributes.get(attKey.toString());
				if(value instanceof String)
				{
					wc.setAttribute(attKey.toString(), value.toString());	
				}else if(value instanceof Integer)
				{
					wc.setAttribute(attKey.toString(), (Integer)value);	
				}else if(value instanceof Boolean)
				{
					wc.setAttribute(attKey.toString(), (Boolean)value);	
				}else if(value instanceof Map)
				{
					wc.setAttribute(attKey.toString(), (Map)value);	
				}else if(value instanceof List)
				{
					wc.setAttribute(attKey.toString(), (List)value);	
				}else if(value instanceof Set)
				{
					wc.setAttribute(attKey.toString(), (Set)value);	
				}
				
			}
			config = wc.doSave();
		} catch (CoreException exception)
		{
			MessageDialog.openError(DestecsDebugPlugin.getActiveWorkbenchShell(), "Failed to create launch", exception.getStatus().getMessage());
		}
		return config;
	}
	
	protected static ILaunchConfigurationType getConfigurationType(String id)
	{
		return getLaunchManager().getLaunchConfigurationType(id);
	}
	
	protected static ILaunchManager getLaunchManager()
	{
		return DebugPlugin.getDefault().getLaunchManager();
	}
	
}
