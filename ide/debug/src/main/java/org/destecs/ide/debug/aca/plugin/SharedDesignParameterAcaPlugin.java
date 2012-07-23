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
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.destecs.core.parsers.SdpParserWrapper;
import org.destecs.ide.debug.DestecsDebugPlugin;
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
		public final List<Double> values;
		
		
		public SdpValueSetConfig(String[] colls) {
			this.name = colls[0];
			this.values = new Vector<Double>();
			
			String[] valueStrings = colls[1].split(";");
			
			
			for (int i = 0; i < valueStrings.length; i++) {
				values.add(Double.parseDouble(valueStrings[i]));
			}
		}

		public List<Double> getValues()
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

		public SdpIncConfig(String[] values) throws Exception
		{
			this(values[0], Double.parseDouble(values[1]), Double.parseDouble(values[2]), Double.parseDouble(values[3]));
			if(this.by == 0)
			{
				throw new Exception("Increment cannot be 0 in incremental SDP name: " + this.name);
			}
		}

		public List<Double> getValues()
		{
			List<Double> values = new Vector<Double>();
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
			String outputPreFix) throws Exception
	{
		final Set<ILaunchConfiguration> configs = new HashSet<ILaunchConfiguration>();

		
		//generating incremental permutations
		Set<ISdpContainer> sdps = getIncrementalSdps(configuration);
		sdps.addAll(getValueSetSdps(configuration));
		
		boolean first = true;
		for (ISdpContainer sdp : filterComplex(sdps))
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
		
		
		Set<String> visitedSdps = new HashSet<String>();	
		Set<ISdpContainer> complexSdps = filterNonComplex(sdps);
		for (ISdpContainer sdp : complexSdps)
		{
			String stripedName = stripName(sdp.getName());
			if(visitedSdps.contains(stripedName))
				continue;
			else
			{
				visitedSdps.add(stripedName);
			}
			Set<ISdpContainer> counterparts = findCounterparts(stripedName,complexSdps);
			
			if(first)
			{
				configs.addAll(generatePermutations(configurations, outputPreFix, counterparts,stripedName));
				first = false;
			}
			else 
			{
				Set<ILaunchConfiguration> temp = generatePermutations(configs, outputPreFix, counterparts,stripedName);
				configs.clear();
				configs.addAll(temp);
			}
		}
	
		
		return configs;
	}

	

	private Set<ILaunchConfiguration> generatePermutations(
			Set<ILaunchConfiguration> input, String outputPreFix,
			Set<ISdpContainer> counterparts, String stripedName) {
		
		Set<ILaunchConfiguration> configs = new HashSet<ILaunchConfiguration>();
				
		
		for (ILaunchConfiguration baseConfig : input)
		{
			
			List<List<Double>> values = constructSdpValues(counterparts,baseConfig);
			for (List<Double> value : values) {
				ILaunchConfigurationWorkingCopy copy;
				try {
					copy = baseConfig.getWorkingCopy();
				
				copy.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, outputPreFix);
				setSharedDesignParameter(stripedName, value, copy);
				configs.add(copy);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}

		return configs;
	}



	private void setSharedDesignParameter(String sdpName,
			List<Double> value, ILaunchConfigurationWorkingCopy configuration) {
		
		SdpParserWrapper parser = new SdpParserWrapper();

		String data;
		try
		{
			data = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, "");

			if (data != null)
			{
				HashMap<String, Object> sdps = parser.parse(new File("memory"), data);
				for (Entry<String, Object> entry : sdps.entrySet()) {
					if(stripName(entry.getKey()).equals(sdpName))
					{
						entry.setValue(value);
						break;
					}
				}
				
								
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
			DestecsDebugPlugin.log(e);	
		}
		
	}



	private List<List<Double>> constructSdpValues(Set<ISdpContainer> counterparts, ILaunchConfiguration baseConfig) {
		List<ISdpContainer> tmp = new Vector<ISdpContainer>(counterparts);
		int length= pickHighestLengh(counterparts);
		List<List<Double>> result = new Vector<List<Double>>();
		if(tmp.size() > 0)
		{
			ISdpContainer container = tmp.get(0);
			List<Double> baseSdp = findBaseSdp(baseConfig, stripName(container.getName()));
			
			for(int i = 0; i<length;i++)
			{				
				List<Double> baseCopy = new Vector<Double>(baseSdp);
				
				for (ISdpContainer sdpContainer : counterparts) {
					try
					{						
						Double val = sdpContainer.getValues().get(i);
						baseCopy.set(getPosition(sdpContainer), val);
					}
					catch(IndexOutOfBoundsException e)
					{
						
					}
					
				}
				result.add(baseCopy);
				
			}			
		}
		System.out.println(result);

		return result;
	}



	private int getPosition(ISdpContainer sdpContainer) {
		String string = sdpContainer.getName();
		int startIndex = string.indexOf("[");
		int endIndex = string.indexOf("]");		
		String sdpDimentions = string.substring(startIndex+1,endIndex);
		String[] stringDims = sdpDimentions.split(",");
		List<Integer> dimensions = parseStringDims(stringDims);
		
		if(dimensions.size() == 1)
		{
			return dimensions.get(0) - 1;
		}
		
		if(dimensions.size() == 2)
		{
			return dimensions.get(0) - 1 * dimensions.get(1) + dimensions.get(1);
		}
		
		return -1;
		
	}
	
	private List<Integer> parseStringDims(String[] stringDims) {
		List<Integer> result = new Vector<Integer>();
		
		for (String integer : stringDims) {
			result.add(Integer.parseInt(integer));
		}
		
		return result;
	}


	private List<Double> findBaseSdp(ILaunchConfiguration configuration,String sdpName) {
		SdpParserWrapper parser = new SdpParserWrapper();
		
		String data;
		try
		{
			data = configuration.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, "");

			if (data != null)
			{
				HashMap<String, Object> sdps = parser.parse(new File("memory"), data);
				Object o = null;
				for (Entry<String, Object> entry : sdps.entrySet()) {
					if(stripName(entry.getKey()).equals(sdpName))
					{
						o = entry.getValue();
						break;
					}
				}
				if(o instanceof List)
				{
					List oList = (List) o;
					List<Double> result = new Vector<Double>();
					for (Object object : oList) {
						if(object instanceof Double)
						{
							result.add((Double)object);
						}
					}
					return result;	
				}
				
			}

		} catch (Exception e)
		{
			DestecsDebugPlugin.log(e);	
		}
		return null;
		
	}



	private int pickHighestLengh(Set<ISdpContainer> counterparts) {
		int result = 0;
		for (ISdpContainer sdp : counterparts) {
			int tmp = sdp.getValues().size();
			if( tmp > result)
			{
				result = tmp; 
			}
		}
		return result;
	}



	private Set<ISdpContainer> findCounterparts(String stripedName,
			Set<ISdpContainer> complexSdps) {
		Set<ISdpContainer> result = new HashSet<ISdpContainer>();
		
		for (ISdpContainer iSdpContainer : complexSdps) {
			if(stripName(iSdpContainer.getName()).equals(stripedName))
			{
				result.add(iSdpContainer);
			}
		}
		
		return result;
	}



	private String stripName(String name) {
		int startIndex = name.indexOf("[");	
		if(startIndex == -1)
		{
			return name;
		}
		else
		{
			return name.substring(0,startIndex);
		}
	}



	private Set<ISdpContainer> filterComplex(Set<ISdpContainer> sdps) {
		Set<ISdpContainer> result = new HashSet<ISdpContainer>();
		
		for (ISdpContainer sdp : sdps) {
			if(!sdp.getName().contains("["))
			{
				result.add(sdp);
			}
		}
		
		return result;
	}
	
	private Set<ISdpContainer> filterNonComplex(Set<ISdpContainer> sdps) {
		Set<ISdpContainer> result = new HashSet<ISdpContainer>();
		
		for (ISdpContainer sdp : sdps) {
			if(sdp.getName().contains("["))
			{
				result.add(sdp);
			}
		}
		
		return result;
	}



	@SuppressWarnings("unused")
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
					DestecsDebugPlugin.log(e);					
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
			DestecsDebugPlugin.log(e);	
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
			DestecsDebugPlugin.log(e1);	
		}
		
		return sdps;
	}
	
	public Set<ISdpContainer> getIncrementalSdps(ILaunchConfiguration baseConfig) throws Exception
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
//						try
//						{
							SdpIncConfig s = new SdpIncConfig(colls);
							sdps.add(s);
//						}
//						catch(Exception e)
//						{							
//							DestecsDebugPlugin.log(e);	
//						}
						
						
					}
				}
			}
		} catch (CoreException e1)
		{
			DestecsDebugPlugin.log(e1);	
		}
		return sdps;
	}
}
