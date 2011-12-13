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
package org.destecs.ide.debug.aca.plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.destecs.core.parsers.SdpParserWrapper;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.aca.IAcaGeneratorPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class SharedDesignParameterAcaPlugin implements IAcaGeneratorPlugin
{
	private static class SdpValueSetConfig implements ISdpContainer
	{
		public final String name;
		public final Set<Double> values;
		
		
		public SdpValueSetConfig(String[] colls) {
			this.name = colls[0];
			this.values = new HashSet<Double>();
			
			String[] valueStrings = colls[1].split(";");
			
			
			for (int i = 0; i < valueStrings.length; i++) {
				values.add(Double.parseDouble(valueStrings[i]));
			}
		}

		public Set<Double> getValues()
		{
			return values;
		}
		
		@Override
		public String toString() {
			return name + "=" + values.toString();
		}

		public String getName() {
			return name;
		}
	}
	
	
	private static class SdpIncConfig implements ISdpContainer
	{
		public final String name;
		public final Double from;
		public final Double to;
		public final Double by;

		public SdpIncConfig(String name, Double from, Double to, Double by)
		{
			this.name = name;
			this.from = from;
			this.to = to;
			this.by = by;
		}

		public SdpIncConfig(String[] values)
		{
			this(values[0], Double.parseDouble(values[1]), Double.parseDouble(values[2]), Double.parseDouble(values[3]));
		}

		public Set<Double> getValues()
		{
			Set<Double> values = new HashSet<Double>();
			for (Double i = from; i <= to; i += by)
			{
				values.add(i);
			}

			return values;
		}

		@Override
		public String toString()
		{
			return name + " " + from + " to " + to + " by " + by;
		}

		public String getName() {
			return name;
		}
	}

	public Collection<? extends ILaunchConfiguration> generate(
			ILaunchConfiguration configuration,
			ILaunchConfiguration baseConfig,
			Set<ILaunchConfiguration> configurations, IProject project,
			String outputPreFix)
	{
		final Set<ILaunchConfiguration> configs = new HashSet<ILaunchConfiguration>();

		
		//generating incremental permutations
		Set<ISdpContainer> sdps = getIncrementalSdps(configuration);
		sdps.addAll(getValueSetSdps(configuration));
		
		boolean first = true;
		for (ISdpContainer sdp : sdps)
		{
			if(first)
			{
				configs.addAll(generatePermutations(configurations, outputPreFix, sdp));
				first = false;
			}
			else 
			{
				Set<ILaunchConfiguration> temp = generatePermutations(configs, outputPreFix, sdp);
				configs.clear();
				configs.addAll(temp);
			}
		}
		
		printPermutations(configs);
		return configs;
	}

	

	private void printPermutations(Set<ILaunchConfiguration> configs)
	{
		System.out.println("Printing permutations");
		for (ILaunchConfiguration iLaunchConfiguration : configs)
		{			
			try
			{
				System.out.println(iLaunchConfiguration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, ""));
				
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public Set<ILaunchConfiguration> generatePermutations(
			Set<ILaunchConfiguration> input, String outputPreFix,
			ISdpContainer sdp)
	{
		Set<ILaunchConfiguration> configs = new HashSet<ILaunchConfiguration>();

		for (ILaunchConfiguration baseConfig : input)
		{
			for (Double value : sdp.getValues())
			{
				try
				{
					ILaunchConfigurationWorkingCopy copy;
					copy = baseConfig.getWorkingCopy();
					copy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, outputPreFix);
					setSharedDesignParameter(sdp.getName(), value, copy);
					configs.add(copy);
				} catch (CoreException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return configs;
	}

	private void setSharedDesignParameter(String name, Double value,
			ILaunchConfigurationWorkingCopy configuration)
	{
		SdpParserWrapper parser = new SdpParserWrapper();

		String data;
		try
		{
			data = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, "");

			if (data != null)
			{
				HashMap<String, Object> sdps = parser.parse(new File("memory"), data);
				sdps.put(name, value);
				data = "";
				Iterator<Entry<String, Object>> itr = sdps.entrySet().iterator();
				while (itr.hasNext())
				{
					Entry<String, Object> next = itr.next();
					data += next.getKey() + " := " + next.getValue()+";";
				}
				configuration.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, data);
			}

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Set<SdpValueSetConfig> getValueSetSdps(
			ILaunchConfiguration baseConfig) {
		
		Set<SdpValueSetConfig> sdps = new HashSet<SharedDesignParameterAcaPlugin.SdpValueSetConfig>();
		
		try
		{
			String data = baseConfig.getAttribute(IDebugConstants.DESTECS_ACA_VALUESET_SDPS, "");
			if (data != null && !data.isEmpty())
			{
				String[] items = data.split(",");
				for (String item : items)
				{
					String[] colls = item.split("\\|");
					if(colls.length == 2)
					{
						sdps.add(new SdpValueSetConfig(colls));
					}
				}
			}
		} catch (CoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return sdps;
	}
	
	public Set<ISdpContainer> getIncrementalSdps(ILaunchConfiguration baseConfig)
	{
		Set<ISdpContainer> sdps = new HashSet<ISdpContainer>();
		try
		{
			String data = baseConfig.getAttribute(IDebugConstants.DESTECS_ACA_INCREMENTAL_SDPS, "");
			if (data != null && !data.isEmpty())
			{
				String[] items = data.split(",");
				for (String item : items)
				{
					String[] colls = item.split("\\|");
					if (colls.length == 4)
					{
						sdps.add(new SdpIncConfig(colls));
					}
				}
			}
		} catch (CoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return sdps;
	}
}
