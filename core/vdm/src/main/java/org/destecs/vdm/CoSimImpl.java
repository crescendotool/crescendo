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
package org.destecs.vdm;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.destecs.core.vdmlink.LinkInfo;
import org.destecs.protocol.IDestecs;
import org.destecs.protocol.exceptions.RemoteSimulationException;
import org.destecs.protocol.structs.GetDesignParametersStruct;
import org.destecs.protocol.structs.GetDesignParametersStructdesignParametersStruct;
import org.destecs.protocol.structs.GetParametersStruct;
import org.destecs.protocol.structs.GetParametersStructparametersStruct;
import org.destecs.protocol.structs.GetStatusStruct;
import org.destecs.protocol.structs.GetVersionStruct;
import org.destecs.protocol.structs.Load2Struct;
import org.destecs.protocol.structs.Load2propertiesStructParam;
import org.destecs.protocol.structs.QueryInterfaceStruct;
import org.destecs.protocol.structs.QueryInterfaceStructinputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructoutputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructsharedDesignParametersStruct;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destecs.vdm.utility.VDMClassHelper;
import org.destecs.vdmj.VDMCO;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.Signal;
import org.overturetool.vdmj.scheduler.SystemClock;
import org.overturetool.vdmj.scheduler.SystemClock.TimeUnit;

@SuppressWarnings("unchecked")
public class CoSimImpl implements IDestecs
{

	private static final String LOAD_DEPLOY = "deploy";
	private static final String LOAD_ARCHITECTURE = "architecture";
	private static final String LOAD_REPLACE = "replace";
	private static final String LOAD_LINK = "link";
	private static final String LOAD_FILE = "file";
	private static final String LOAD_BASE_DIR = "basedir";
	private static final String LOAD_DEBUG_PORT = "dbgp_port";
	public static final String LOAD_OUTPUT_DIR = "output_dir";
	// settings
	public static final String LOAD_SETTING_DISABLE_PRE = "settings_disable_pre";
	public static final String LOAD_SETTING_DISABLE_POST = "settings_disable_post";
	public static final String LOAD_SETTING_DISABLE_INV = "settings_disable_inv";
	public static final String LOAD_SETTING_DISABLE_DYNAMIC_TC = "settings_disable_dtc";
	public static final String LOAD_SETTING_DISABLE_MEASURE = "settings_disable_measure";

	public static final String LOAD_SETTING_DISABLE_RT_LOG = "settings_disable_rt_log";
	public static final String LOAD_SETTING_DISABLE_RT_VALIDATOR = "settings_disable_rt_validator";

	private static final String version = "0.0.1.0";
	private static final String LOAD_SETTING_LOG_VARIABLES = "settings_log_variables";
	private String interfaceVersion = "3.0.0.0";

	public Map<String, Integer> getStatus()
	{
		return new GetStatusStruct(SimulationManager.getInstance().getStatus()).toMap();
	}

	public Map<String, Object> getVersion()
	{

		return new GetVersionStruct(interfaceVersion, "VDMJ", version).toMap();
	}

	public Boolean initialize() throws RemoteSimulationException
	{
		try
		{
			return SimulationManager.getInstance().initialize();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Map<String, Boolean> load2(
			Map<String, List<Map<String, Object>>> data)
			throws RemoteSimulationException
	{
		VDMCO.replaceNewIdentifier.clear();
		Object o = data.get("properties");
		Object[] oo = (Object[]) o;

		Settings.prechecks = true;
		Settings.postchecks = true;
		Settings.invchecks = true;
		Settings.dynamictypechecks = true;
		Settings.measureChecks = true;
		boolean disableRtLog = false;

		List<File> specfiles = new Vector<File>();
		File linkFile = null;
		File baseDirFile = null;
		List<String> variablesToLog = new Vector<String>();
		String outputDir = null;
		for (Object in : oo)
		{
			if (in instanceof Map)
			{
				Load2propertiesStructParam arg = new Load2propertiesStructParam((Map<String, Object>) in);

				if (arg.key.startsWith(LOAD_FILE))
				{
					specfiles.add(new File(arg.value));
				}

				if (arg.key.startsWith(LOAD_LINK))
				{
					linkFile = new File(arg.value);
				}
				if (arg.key.startsWith(LOAD_BASE_DIR))
				{
					baseDirFile = new File(arg.value);
				}
				if (arg.key.startsWith(LOAD_REPLACE))
				{
					List<String> replacePatterns = Arrays.asList(arg.value.split(","));
					for (String pattern : replacePatterns)
					{
						if (pattern != null && pattern.contains("/"))
						{
							String key = pattern.split("/")[0];
							String value = pattern.split("/")[1];
							if (!VDMCO.replaceNewIdentifier.containsKey(key))
							{
								VDMCO.replaceNewIdentifier.put(key, value);
							}
						}
					}
				}
				if (arg.key.startsWith(LOAD_ARCHITECTURE))
				{
					VDMCO.architecture = arg.value;
				}
				if (arg.key.startsWith(LOAD_DEPLOY))
				{
					VDMCO.deploy = arg.value;
				}
				if (arg.key.startsWith(LOAD_DEBUG_PORT))
				{
					VDMCO.debugPort = Integer.valueOf(arg.value);
				}
				if (arg.key.startsWith(LOAD_SETTING_DISABLE_PRE))
				{
					Settings.prechecks = false;
				}
				if (arg.key.startsWith(LOAD_SETTING_DISABLE_POST))
				{
					Settings.postchecks = false;
				}
				if (arg.key.startsWith(LOAD_SETTING_DISABLE_INV))
				{
					Settings.invchecks = false;
				}
				if (arg.key.startsWith(LOAD_SETTING_DISABLE_DYNAMIC_TC))
				{
					Settings.dynamictypechecks = false;
				}
				if (arg.key.startsWith(LOAD_SETTING_DISABLE_MEASURE))
				{
					Settings.measureChecks = false;
				}
				if (arg.key.startsWith(LOAD_SETTING_DISABLE_RT_LOG))
				{
					disableRtLog = true;
				}
				if (arg.key.startsWith(LOAD_SETTING_DISABLE_RT_VALIDATOR))
				{
					// TODO: disable runtime validation.
				}
				if (arg.key.startsWith(LOAD_SETTING_LOG_VARIABLES))
				{
					String[] variables = arg.value.split(",");
					variablesToLog.addAll(Arrays.asList(variables));
				}
				if (arg.key.startsWith(LOAD_OUTPUT_DIR))
				{
					outputDir = arg.value;
				}
			}
		}

		// String outputDir = (String) arg0.get("outputDir");

		try
		{
			return new Load2Struct(SimulationManager.getInstance().load(specfiles, linkFile, new File(outputDir), baseDirFile, disableRtLog)).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	private Integer findVariableDimension(LinkInfo linkInfo)
	{
		List<String> qualifiedName = linkInfo.getQualifiedName();

		if (qualifiedName.size() < 2)
		{
			System.out.println("qualified name is too small");
			return -2;
		}

		try
		{
			Definition def = VDMClassHelper.findDefinitionInClass(SimulationManager.getInstance().controller.getInterpreter().getClasses(), qualifiedName);
			if (def == null)
			{
				return -2;
			} else
			{
				return extractDefinitionDimensions(def);
			}

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return -2;

	}

	private Integer extractDefinitionDimensions(Definition def)
	{

		if (def.getType().isSeq())
		{
			return -1;
		} else
		{
			return 1;
		}

	}

	public Map<String, Object> queryInterface()
	{
		/*
		 * Shared design variables minLevel maxLevel Variables level :IN valveState :OUT Events HIGH_LEVEL LOW_LEVEL
		 */
		QueryInterfaceStruct s = new QueryInterfaceStruct();

		for (LinkInfo sdp : SimulationManager.getInstance().getSharedDesignParameters().values())
		{

			// dimension does not matter at this point
			List<Integer> dimensions = new Vector<Integer>();
			dimensions.add(findVariableDimension(sdp));
			s.sharedDesignParameters.add(new QueryInterfaceStructsharedDesignParametersStruct(sdp.getIdentifier(), dimensions));
		}

		for (LinkInfo input : SimulationManager.getInstance().getInputVariables().values())
		{
			List<Integer> dimensions = new Vector<Integer>();
			dimensions.add(findVariableDimension(input));
			s.inputs.add(new QueryInterfaceStructinputsStruct(input.getIdentifier(), dimensions));

		}

		for (LinkInfo output : SimulationManager.getInstance().getOutputVariables().values())
		{
			List<Integer> dimensions = new Vector<Integer>();
			dimensions.add(findVariableDimension(output));
			s.outputs.add(new QueryInterfaceStructoutputsStruct(output.getIdentifier(), dimensions));
		}

		// No events from VDM

		return s.toMap();
	}

	public Map<String, Object> step(Map<String, Object> data)
			throws RemoteSimulationException
	{
		try
		{
			Double outputTime = (Double) data.get("outputTime");

			List<Object> tmp = Arrays.asList((Object[]) data.get("inputs"));

			List<StepinputsStructParam> inputs = new Vector<StepinputsStructParam>();
			for (Object in : tmp)
			{
				if (in instanceof Map)
				{
					inputs.add(new StepinputsStructParam((Map<String, Object>) in));
				}
			}

			// Boolean singleStep = (Boolean) data.get("singleStep");

			List<Object> tmp1 = Arrays.asList((Object[]) data.get("events"));

			List<String> events = new Vector<String>();
			for (Object in : tmp1)
			{
				if (in instanceof String)
				{
					events.add((String) in);
				}
			}

			// Ignore single step
			StepStruct result;
			// try
			// {
			outputTime = new Double(SystemClock.timeToInternal(TimeUnit.seconds, outputTime));
			result = SimulationManager.getInstance().step(outputTime, inputs, events);

			result.time = SystemClock.internalToTime(TimeUnit.seconds, result.time.longValue());

			return result.toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Boolean terminate()
	{
		System.out.println("DESTECS VDM is terminating now...");

		Thread shutdown = new Thread(new Runnable()
		{

			public void run()
			{
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
					// Wait for terminate to reply to client then terminate
				}
				System.exit(0);
			}
		});
		shutdown.start();
		return true;
	}

	public Boolean stop() throws RemoteSimulationException
	{
		try
		{
			return SimulationManager.getInstance().stopSimulation();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	// public Map<String, Object> getDesignParameter(Map<String, String> data)
	// throws RemoteSimulationException
	// {
	// String parameterName = data.get("name");
	// return SimulationManager.getInstance().getDesignParameter(parameterName).toMap();
	// }

	public Map<String, List<Map<String, Object>>> getDesignParameters(
			List<String> data) throws RemoteSimulationException
	{
		List<GetDesignParametersStructdesignParametersStruct> list = new Vector<GetDesignParametersStructdesignParametersStruct>();
		for (Entry<String, LinkInfo> entry : SimulationManager.getInstance().getSharedDesignParameters().entrySet())
		{
			list.add(SimulationManager.getInstance().getDesignParameter(entry.getKey()));
		}
		return new GetDesignParametersStruct(list).toMap();
	}

	// public Map<String, Object> getParameter(Map<String, String> data)
	// throws RemoteSimulationException
	// {
	// String name = (String) data.get("name");
	//
	// List<Double> value;
	// List<Integer> size;
	// try
	// {
	// value = SimulationManager.getInstance().getParameter(name);
	// size = SimulationManager.getInstance().getParameterSize(name);
	// return new GetParameterStruct(value, size).toMap();
	// } catch (RemoteSimulationException e)
	// {
	// ErrorLog.log(e);
	// throw e;
	// }
	// }

	public Map<String, List<Map<String, Object>>> getParameters(
			List<String> data) throws RemoteSimulationException
	{
		List<GetParametersStructparametersStruct> list = new Vector<GetParametersStructparametersStruct>();
		try
		{
			for (Entry<String, ValueContents> p : SimulationManager.getInstance().getParameters().entrySet())
			{
				list.add(new GetParametersStructparametersStruct(p.getKey(), p.getValue().value, p.getValue().size));
			}
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
		return new GetParametersStruct(list).toMap();
	}

	// public Map<String, Boolean> setDesignParameter(Map<String, Object> data)
	// throws RemoteSimulationException
	// {
	//
	// try
	// {
	// List<Map<String, Object>> argument = new Vector<Map<String, Object>>();
	// argument.add(data);
	// boolean success = SimulationManager.getInstance().setDesignParameters(argument);
	// return new SetDesignParameterStruct(success).toMap();
	// } catch (RemoteSimulationException e)
	// {
	// ErrorLog.log(e);
	// throw e;
	// }
	// }

	public Boolean setDesignParameters(
			Map<String, List<Map<String, Object>>> data)
			throws RemoteSimulationException
	{
		try
		{
			boolean success = false;
			if (data.values().size() > 0)
			{
				Object s = data.values().iterator().next();
				@SuppressWarnings("rawtypes")
				List tmp = Arrays.asList((Object[]) s);

				success = SimulationManager.getInstance().setDesignParameters(tmp);

			}
			return success;
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	/**
	 * Local method
	 * 
	 * @param data
	 * @return
	 * @throws RemoteSimulationException
	 */
	private Boolean setParameter(Map<String, Object> data)
			throws RemoteSimulationException
	{
		String name = (String) data.get("name");
		List<Double> value = new Vector<Double>();

		for (Object o : (Object[]) data.get("value"))
		{
			if (o instanceof Double)
			{
				value.add((Double) o);
			} else
			{
				throw new RemoteSimulationException("Internal error converting parameter: "
						+ o + " to Double");
			}
		}

		List<Integer> size = new Vector<Integer>();

		for (Object o : (Object[]) data.get("size"))
		{
			if (o instanceof Integer)
			{
				size.add((Integer) o);
			} else
			{
				throw new RemoteSimulationException("Internal error converting parameter size: "
						+ o + " to Integer");
			}
		}

		Boolean success;
		try
		{
			success = SimulationManager.getInstance().setParameter(name, new ValueContents(value, size));

			return success;
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Boolean setParameters(Map<String, List<Map<String, Object>>> data)
			throws RemoteSimulationException
	{
		try
		{
			boolean success = false;

			if (data.values().size() > 0)
			{

				for (Object parms : data.values())
				{
					for (Object tmp2 : (Object[]) parms)
					{
						Map<String, Object> s = (Map<String, Object>) tmp2;
						success = setParameter(s);
					}
				}
				return success;
			}
			return success;
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Boolean start(Map<String, Object> data)
			throws RemoteSimulationException
	{
		try
		{
			return SimulationManager.getInstance().start();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Boolean setLogVariables(Map<String, Object> data) throws Exception
	{
		try
		{
			List<String> logVariables = new Vector<String>();

			for (Object o : (Object[]) data.get("variables"))
			{
				logVariables.add(o.toString());
			}

			SimulationManager.getInstance().setLogVariables(new File(data.get("filePath").toString()), logVariables);

			return true;
		} catch (Exception e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public List<Map<String, Object>> queryVariables() throws Exception
	{
		// TODO return all instance variables accessible from the system class
		return null;
	}

	public List<Map<String, Object>> queryParameters() throws Exception
	{
		// TODO return all values
		return null;
	}

	@Deprecated
	public Boolean load(Map<String, String> data) throws Exception
	{
		throw new RemoteSimulationException("Deprecated: Load not supported, use Load2");
	}

	public Boolean suspend() throws Exception
	{
		try
		{
			BasicSchedulableThread.signalAll(Signal.SUSPEND);
			return true;
		} catch (Exception e)
		{
			throw new RemoteSimulationException("Failed to suspend the VDM debugger");
		}

	}

	/**
	 * This method is just a skip, we need to resume from the IDE. This is needed because the IDE hold a state
	 * representing the state of the threads
	 */
	public Boolean resume() throws Exception
	{
		return true;
	}

	public Boolean setSettings(List<Map<String, Object>> data) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> querySettings(List<String> data)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

}
