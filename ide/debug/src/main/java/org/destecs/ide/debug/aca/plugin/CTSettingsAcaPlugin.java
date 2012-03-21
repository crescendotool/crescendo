package org.destecs.ide.debug.aca.plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.aca.IAcaGeneratorPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class CTSettingsAcaPlugin implements IAcaGeneratorPlugin {

	public Collection<? extends ILaunchConfiguration> generate(
			ILaunchConfiguration configuration,
			ILaunchConfiguration baseConfig,
			Set<ILaunchConfiguration> configurations, IProject project,
			String outputPreFix) {
		
		String unparsedString;
		try
		{
			unparsedString = configuration.getAttribute(IDebugConstants.DESTECS_ACA_20SIM_SETTINGS, "");
			if(unparsedString.trim().length() == 0)
			{
				return new HashSet<ILaunchConfiguration>(configurations);
			}
			
			HashMap<String, List<String>> parsedSettings = parseConfigString(
					configurations, unparsedString);
			
			List<String> settingsPermutations = generatePermutations(parsedSettings);
			
			return generateCofigurations(configurations,settingsPermutations,outputPreFix);
			
			
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}

	private Set<ILaunchConfiguration> generateCofigurations(
			Set<ILaunchConfiguration> configurations,
			List<String> settingsPermutations, String outputPreFix) throws CoreException {
		
		Set<ILaunchConfiguration> results = new HashSet<ILaunchConfiguration>();
		
		for (ILaunchConfiguration configuration : configurations) {
			
			for (String setting : settingsPermutations) {
				ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
				workingCopy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS, setting);
				workingCopy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, outputPreFix);
				results.add(workingCopy);
			}
		}
		
		return results;
		
	}

	private List<String> generatePermutations(
			HashMap<String, List<String>> parsedSettings) {
		
		List<String> accumulator = new Vector<String>();
		
		Iterator<Entry<String, List<String>>> it = parsedSettings.entrySet().iterator();
		
		Entry<String, List<String>> first = it.next();
		String firstKey = first.getKey();
		for (String value : first.getValue()) {
			accumulator.add(firstKey+"="+value+";");
		}
		
		while(it.hasNext())
		{
			Entry<String, List<String>> next = it.next();
			accumulator = permutateEntry(next,accumulator);			
		}
		
		
		return accumulator;
		
	}

	private List<String> permutateEntry(Entry<String, List<String>> next,
			List<String> accumulator) {
		
		List<String> result = new Vector<String>();
		
		String entryKey = next.getKey();
		
		for (String setting : accumulator) {
			for (String value : next.getValue()) {
				result.add(setting + entryKey + "=" + value + ";" ); 
			}
		}
		
		return result;
		
	}

	private HashMap<String, List<String>> parseConfigString(
			Set<ILaunchConfiguration> configurations, String unparsedString) {
		
		String[] singleSettings = unparsedString.split(";");
		HashMap<String, List<String>> parsedSettings = new HashMap<String, List<String>>();
		
		for (String singleSetting : singleSettings) {
			String[] keyValuesPair = singleSetting.split("=");

			if(keyValuesPair.length == 2)
			{
				String key = keyValuesPair[0];
				String values = keyValuesPair[1];
				List<String> valuesList = new Vector<String>();
				for(String value : values.split(","))
				{
					valuesList.add(value);
				}
				parsedSettings.put(key, valuesList);
			}
		}
		return parsedSettings;
	}

	
	
}
