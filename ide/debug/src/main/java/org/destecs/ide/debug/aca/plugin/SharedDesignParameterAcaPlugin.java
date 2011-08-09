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
	private static class SdpIncConfig
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
	}

	public Collection<? extends ILaunchConfiguration> generate(
			ILaunchConfiguration configuration,
			ILaunchConfiguration baseConfig,
			Set<ILaunchConfiguration> congifurations, IProject project,
			String outputPreFix)
	{
		final Set<ILaunchConfiguration> configs = new HashSet<ILaunchConfiguration>();

		Set<SdpIncConfig> sdps = getSdps(configuration);
		Set<ILaunchConfiguration> baseSet = new HashSet<ILaunchConfiguration>();
		baseSet.add(baseConfig);

		for (SdpIncConfig sdp : sdps)
		{
			configs.addAll(generatePermutations(baseSet, outputPreFix, sdp));
		}

		return configs;
	}

	public Set<ILaunchConfiguration> generatePermutations(
			Set<ILaunchConfiguration> input, String outputPreFix,
			SdpIncConfig sdp)
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
					setSharedDesignParameter(sdp.name, value, copy);
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

	public Set<SdpIncConfig> getSdps(ILaunchConfiguration baseConfig)
	{
		Set<SdpIncConfig> sdps = new HashSet<SharedDesignParameterAcaPlugin.SdpIncConfig>();
		try
		{
			String data = baseConfig.getAttribute(IDebugConstants.DESTECS_ACA_SHARED_DESIGN_PARAMETERS, "");
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
