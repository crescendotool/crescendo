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
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Hashtable;
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
import org.destecs.protocol.structs.GetVariablesStruct;
import org.destecs.protocol.structs.GetVariablesStructvariablesStruct;
import org.destecs.protocol.structs.GetVersionStruct;
import org.destecs.protocol.structs.LoadpropertiesStructParam;
import org.destecs.protocol.structs.QueryInterfaceStruct;
import org.destecs.protocol.structs.QueryInterfaceStructinputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructoutputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructsharedDesignParametersStruct;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destecs.vdm.utility.VDMClassHelper;
import org.destecs.vdmj.VDMCO;
import org.overture.ast.definitions.PDefinition;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.Signal;
import org.overture.interpreter.scheduler.SystemClock;
import org.overture.interpreter.scheduler.SystemClock.TimeUnit;

@SuppressWarnings("unchecked")
public class CoSimImpl implements IDestecs
{
	static int stepCount = 0;
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
	public static final String LOAD_SETTING_DISABLE_OPTIMIZATION = "settings_disable_optimization";

	public static final String LOAD_SETTING_DISABLE_RT_LOG = "settings_disable_rt_log";
	public static final String LOAD_SETTING_DISABLE_RT_VALIDATOR = "settings_disable_rt_validator";

	private static final String version = "2.0.0.0";
	private static final String simulatorMame = "Overture (VDMJ)";
	private static final String LOAD_SETTING_LOG_VARIABLES = "settings_log_variables";
	private static final String LOAD_SETTING_DISABLE_COVERAGE = "settings_disable_coverage";
	private String interfaceVersion = "3.0.4.0";
	public static boolean DEBUG = false;;
	private static Double finishTime = 0.0;

	public Map<String, Integer> getStatus()
	{
		return new GetStatusStruct(SimulationManager.getInstance().getStatus()).toMap();
	}

	public Map<String, Object> getVersion()
	{

		return new GetVersionStruct(interfaceVersion, simulatorMame, version).toMap();
	}

	public Boolean initialize() throws RemoteSimulationException
	{
		stepCount = 0;
		try
		{
			return SimulationManager.getInstance().initialize();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Boolean load(
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
		boolean disableCoverage = false;
		boolean disableOptimization = false;

		List<File> specfiles = new Vector<File>();
		File linkFile = null;
		File baseDirFile = null;
		List<String> variablesToLog = new Vector<String>();
		String outputDir = null;
		for (Object in : oo)
		{
			if (in instanceof Map)
			{
				LoadpropertiesStructParam arg = new LoadpropertiesStructParam((Map<String, Object>) in);

				if (arg.key.startsWith(LOAD_FILE))
				{
					specfiles.add(new File(arg.value));
				} else if (arg.key.startsWith(LOAD_LINK))
				{
					linkFile = new File(arg.value);
				} else if (arg.key.startsWith(LOAD_BASE_DIR))
				{
					baseDirFile = new File(arg.value);
				} else if (arg.key.startsWith(LOAD_REPLACE))
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
				} else if (arg.key.startsWith(LOAD_ARCHITECTURE))
				{
					VDMCO.architecture = arg.value;
				} else if (arg.key.startsWith(LOAD_DEPLOY))
				{
					VDMCO.deploy = arg.value;
				} else if (arg.key.startsWith(LOAD_DEBUG_PORT))
				{
					VDMCO.debugPort = Integer.valueOf(arg.value);
				} else if (arg.key.startsWith(LOAD_SETTING_DISABLE_PRE))
				{
					Settings.prechecks = false;
				} else if (arg.key.startsWith(LOAD_SETTING_DISABLE_POST))
				{
					Settings.postchecks = false;
				} else if (arg.key.startsWith(LOAD_SETTING_DISABLE_INV))
				{
					Settings.invchecks = false;
				} else if (arg.key.startsWith(LOAD_SETTING_DISABLE_DYNAMIC_TC))
				{
					Settings.dynamictypechecks = false;
				} else if (arg.key.startsWith(LOAD_SETTING_DISABLE_MEASURE))
				{
					Settings.measureChecks = false;
				} else if (arg.key.startsWith(LOAD_SETTING_DISABLE_RT_LOG))
				{
					disableRtLog = true;
				} else if (arg.key.startsWith(LOAD_SETTING_DISABLE_RT_VALIDATOR))
				{
					Settings.timingInvChecks = false;
				} else if (arg.key.startsWith(LOAD_SETTING_DISABLE_COVERAGE))
				{
					disableCoverage = true;
				} else if (arg.key.startsWith(LOAD_SETTING_DISABLE_OPTIMIZATION))
				{
					disableOptimization = true;
				} else if (arg.key.startsWith(LOAD_SETTING_LOG_VARIABLES))
				{
					String[] variables = arg.value.split(",");
					variablesToLog.addAll(Arrays.asList(variables));
				} else if (arg.key.startsWith(LOAD_OUTPUT_DIR))
				{
					outputDir = arg.value;
				}
			}
		}

		try
		{
			return SimulationManager.getInstance().load(specfiles, linkFile, new File(outputDir), baseDirFile, disableRtLog, disableCoverage, disableOptimization);
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	private Integer findVariableDimension(LinkInfo linkInfo)
			throws RemoteSimulationException
	{
		List<String> qualifiedName = linkInfo.getQualifiedName();

		if (qualifiedName.size() < 2)
		{
			throw new RemoteSimulationException("Error in dimention calculation for \""
					+ linkInfo
					+ "\". Qualified name too small: "
					+ qualifiedName);
		}

		try
		{
			PDefinition def = VDMClassHelper.findDefinitionInClass(SimulationManager.getInstance().controller.getInterpreter().getClasses(), qualifiedName);
			if (def == null)
			{
				return -2;
			} else
			{
				return extractDefinitionDimensions(def);
			}

		}catch(RemoteSimulationException e){
			throw e;
		}catch (Exception e)
		{
			ErrorLog.log(e);
			throw new RemoteSimulationException("Fatal error in dimention calculation for \"" 	+ linkInfo 	+ "\"",e);

		}


	}

	private Integer extractDefinitionDimensions(PDefinition def)
	{

		if (PTypeAssistantInterpreter.isSeq(def.getType()))
		{
			return -1;
		} else
		{
			return 1;
		}

	}

	public Map<String, Object> queryInterface() throws RemoteSimulationException
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
			stepCount++;
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

			List<Object> tmp1 = Arrays.asList((Object[]) data.get("events"));

			List<String> events = new Vector<String>();
			for (Object in : tmp1)
			{
				if (in instanceof String)
				{
					events.add((String) in);
				}
			}

			StepStruct result;

			outputTime = new Double(SystemClock.timeToInternal(TimeUnit.seconds, outputTime));
			result = SimulationManager.getInstance().step(outputTime, inputs, events);

			result.time = SystemClock.internalToTime(TimeUnit.seconds, result.time.longValue());
			if (result.time > finishTime)
			{
				// The next point where VDM needs CT communication is result.time but if the simulation stops before we
				// are not by protocol allowed to ask for this.
				result.time = finishTime;
			}

			return result.toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Boolean terminate()
	{
		System.out.println("The VDM interpreter is terminating now...");

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
				CoSim.shutdown();
			}
		});
		if(!DEBUG )
		{
			shutdown.start();
		}
		return true;
	}

	public Boolean stop() throws RemoteSimulationException
	{
		try
		{
			System.out.println("Total steps taken: " + stepCount);
			return SimulationManager.getInstance().stopSimulation();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

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

	public Map<String, List<Map<String, Object>>> getParameters(
			List<String> data) throws RemoteSimulationException
	{
		List<GetParametersStructparametersStruct> list = new Vector<GetParametersStructparametersStruct>();
		try
		{
			for (String name : data)
			{
				list.add(new GetParametersStructparametersStruct(name, SimulationManager.getInstance().getParameter(name), SimulationManager.getInstance().getParameterSize(name)));
			}
			
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
		return new GetParametersStruct(list).toMap();
	}

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
			success = SimulationManager.getInstance().setInstanceVariable(name, new ValueContents(value, size));

			return success;
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Boolean setParameters(Map<String, List<Map<String, Object>>> data)
			throws Exception
	{
		try
		{
			boolean success = true;

			if (data.values().size() > 0)
			{
				Object t = data.get("parameters");
				for (Object parms : (Object[])t)
				{
					Map<String, Object> s = (Map<String, Object>) parms;
					success =success && setParameter(s);
				}
				return success;
			}
			return success;
		} catch (Exception e)
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
			finishTime = (Double) data.get("finishTime");
			long internalFinishTime = SystemClock.timeToInternal(TimeUnit.seconds, finishTime);
			return SimulationManager.getInstance().start(internalFinishTime);
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
		List<Map<String, Object>> result = new Vector<Map<String, Object>>();
		for (String name : SimulationManager.getInstance().getLogEnabledVariables())
		{
			Hashtable<String, Object> table = new Hashtable<String, Object>();
			table.put("name", name);
			table.put("size", new Integer[]{});
			result.add(table);
		}
		return result;
	}

	public List<Map<String, Object>> queryParameters() throws Exception
	{
		List<Map<String, Object>> result = getParameters(new Vector<String>()).get("parameters");
		for (Map<String, Object> map : result)
		{
			if(map.containsKey("value"))
			{
				map.remove("value");
			}
		}
		return result;
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

	@SuppressWarnings("static-access")
	public Boolean setSettings(List<Map<String, Object>> data) throws Exception
	{
		try
		{
			for (Map<String, Object> map : data)
			{
				String name = map.get("key").toString();
				Object  value = map.get("value");
				if(value instanceof Boolean)
				{
					Field field = Settings.class.getField(name);
					if(field.getType() == Boolean.class)
					{
						field.set(null, value);
					}
				}else if(value instanceof Integer)
				{
					Field field = Settings.class.getField(name);
					if(field.getType() == Integer.class)
					{
						field.set(null, value);
					}
				}else if(value instanceof Double)
				{
					Field field = Settings.class.getField(name);
					if(field.getType() == Double.class)
					{
						field.set(null, value);
					}
				}else if(value instanceof String)
				{
					Field field = Settings.class.getField(name);
					if(field.getType() == String.class)
					{
						field.set(null, value);
					}
				}else 
				{
					Field field = Settings.class.getField(name);
					if(field.getType().isEnum())
					{
						@SuppressWarnings("rawtypes")
						Enum e =(Enum) field.getType().newInstance();
						field.set(null, e.valueOf(e.getClass(), value.toString()));
					}
				}
				
			}
		} catch (Exception e)
		{
			ErrorLog.log(e);
			throw e;
		}
		return true;
	}

	public List<Map<String, Object>> querySettings(List<String> data)
			throws Exception
	{
		try
		{
			List<Map<String, Object>> settings = new Vector<Map<String, Object>>();
			settings.add(createSettingsMapElement("release", Settings.release, Settings.release.name()));
			settings.add(createSettingsMapElement("dialect", Settings.dialect, Settings.dialect.name()));
			settings.add(createSettingsMapElement("prechecks", Settings.prechecks));
			settings.add(createSettingsMapElement("postchecks", Settings.postchecks));
			settings.add(createSettingsMapElement("invchecks", Settings.invchecks));
			settings.add(createSettingsMapElement("dynamictypechecks", Settings.dynamictypechecks));
			settings.add(createSettingsMapElement("measureChecks", Settings.measureChecks));
			settings.add(createSettingsMapElement("timingInvChecks", Settings.timingInvChecks));
			settings.add(createSettingsMapElement("", Settings.dialect));
			settings.add(createSettingsMapElement("", Settings.dialect));

			return settings;
		} catch (Exception e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}
	private Map<String,Object> createSettingsMapElement(String key,Object value,String... values)
	{
		Map<String,Object> setting = new Hashtable<String, Object>();
		setting.put("key", key);
		setting.put("value", value.toString());
		if(value instanceof Integer)
		{
			setting.put("type", "int");
		}else if(value instanceof Double)
		{
			setting.put("type", "double");
		}else if(value instanceof Boolean)
		{
			setting.put("type", "bool");
		}else if(value instanceof Enum)
		{
			setting.put("type", "enum");
		}else
		{
			setting.put("type", "string");
		}
		
		if(values == null)
		{
			values = new String[]{};
		}
		setting.put("enumerations", value);
		setting.put("properties", new Object[]{});
		return setting;
	}

	public List<Map<String, Object>> queryImplementations() throws Exception
	{
		return new Vector<Map<String, Object>>();
	}

	public Boolean setImplementations(List<Map<String, Object>> data)
			throws Exception
	{
		return false;
	}

	public Map<String, List<Map<String, Object>>> getVariables(List<String> data)
			throws Exception
	{
		GetVariablesStruct vars = new GetVariablesStruct();
		try
		{
			for (Entry<String, ValueContents> p : SimulationManager.getInstance().getInstanceVariables(data).entrySet())
			{
				vars.variables.add(new GetVariablesStructvariablesStruct(p.getKey(), p.getValue().value, p.getValue().size));
			}
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
		
		return vars.toMap();
	}

	public Boolean saveRequired() throws Exception
	{
		return false;
	}

}
