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
import org.destecs.protocol.structs.GetDesignParameterStruct;
import org.destecs.protocol.structs.GetDesignParametersStruct;
import org.destecs.protocol.structs.GetDesignParametersStructdesignParametersStruct;
import org.destecs.protocol.structs.GetParameterStruct;
import org.destecs.protocol.structs.GetParametersStruct;
import org.destecs.protocol.structs.GetParametersStructparametersStruct;
import org.destecs.protocol.structs.GetStatusStruct;
import org.destecs.protocol.structs.GetVersionStruct;
import org.destecs.protocol.structs.Load2Struct;
import org.destecs.protocol.structs.Load2argumentsStructParam;
import org.destecs.protocol.structs.LoadStruct;
import org.destecs.protocol.structs.QueryInterfaceStruct;
import org.destecs.protocol.structs.QueryInterfaceStructinputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructoutputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructsharedDesignParametersStruct;
import org.destecs.protocol.structs.SetDesignParameterStruct;
import org.destecs.protocol.structs.SetDesignParametersStruct;
import org.destecs.protocol.structs.SetParametersStruct;
import org.destecs.protocol.structs.StartStruct;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destecs.protocol.structs.StopStruct;
import org.destecs.protocol.structs.TerminateStruct;
import org.destecs.protocol.structs.UnLoadStruct;
import org.destecs.vdm.utility.VDMClassHelper;
import org.destecs.vdmj.VDMCO;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.Definition;
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
	// settings
	public static final String LOAD_SETTING_DISABLE_PRE = "settings_disable_pre";
	public static final String LOAD_SETTING_DISABLE_POST = "settings_disable_post";
	public static final String LOAD_SETTING_DISABLE_INV = "settings_disable_inv";
	public static final String LOAD_SETTING_DISABLE_DYNAMIC_TC = "settings_disable_dtc";
	public static final String LOAD_SETTING_DISABLE_MEASURE = "settings_disable_measure";

	public static final String LOAD_SETTING_DISABLE_RT_LOG = "settings_disable_rt_log";
	public static final String LOAD_SETTING_DISABLE_RT_VALIDATOR = "settings_disable_rt_validator";

	private static final String version = "0.0.0.2";
	private static final String LOAD_SETTING_LOG_VARIABLES = "settings_log_variables";

	public Map<String, Integer> getStatus()
	{
		return new GetStatusStruct(SimulationManager.getInstance().getStatus()).toMap();
	}

	public Map<String, Object> getVersion()
	{
		return new GetVersionStruct("VDMJ", version).toMap();
	}

	public Map<String, Boolean> initialize() throws RemoteSimulationException
	{
		try
		{
			return new StartStruct(SimulationManager.getInstance().initialize()).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Map<String, Boolean> load(Map<String, String> data)
			throws RemoteSimulationException
	{
		String path = data.get(data.keySet().toArray()[0]);

		try
		{
			final String linkFileName = "vdm.link";
			final String specFileExtension = "vdmrt";
			File root = new File(path);
			File linkFile = new File(new File(root.getParentFile(), "configuration"), linkFileName);
			final List<File> files = new Vector<File>();

			files.addAll(getFiles(root, specFileExtension));

			File outputFolder = new File(root.getParentFile(), "output");
			return new LoadStruct(SimulationManager.getInstance().load(files, outputFolder, linkFile, files.get(0).getParentFile(), false, new Vector<String>())).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	/***
	 * local helper function of load
	 * 
	 * @param path
	 * @param extension
	 * @return
	 */
	private static List<File> getFiles(File path, String extension)
	{
		List<File> files = new Vector<File>();

		if (path.isFile() && path.getName().toLowerCase().endsWith(extension))
		{
			files.add(path);
		} else if (path.isDirectory())
		{
			for (File file : path.listFiles())
			{
				files.addAll(getFiles(file, extension));
			}

		}
		return files;
	}

	public Map<String, Boolean> load2(Map<String, Object> arg0)
			throws RemoteSimulationException
	{
		List tmp = Arrays.asList((Object[]) arg0.get("arguments"));

		VDMCO.replaceNewIdentifier.clear();

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
		for (Object in : tmp)
		{
			if (in instanceof Map)
			{
				Load2argumentsStructParam arg = new Load2argumentsStructParam((Map<String, Object>) in);

				if (arg.argumentName.startsWith(LOAD_FILE))
				{
					specfiles.add(new File(arg.argumentValue));
				}

				if (arg.argumentName.startsWith(LOAD_LINK))
				{
					linkFile = new File(arg.argumentValue);
				}
				if (arg.argumentName.startsWith(LOAD_BASE_DIR))
				{
					baseDirFile = new File(arg.argumentValue);
				}
				if (arg.argumentName.startsWith(LOAD_REPLACE))
				{
					List<String> replacePatterns = Arrays.asList(arg.argumentValue.split(","));
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
				if (arg.argumentName.startsWith(LOAD_ARCHITECTURE))
				{
					VDMCO.architecture = arg.argumentValue;
				}
				if (arg.argumentName.startsWith(LOAD_DEPLOY))
				{
					VDMCO.deploy = arg.argumentValue;
				}
				if (arg.argumentName.startsWith(LOAD_DEBUG_PORT))
				{
					VDMCO.debugPort = Integer.valueOf(arg.argumentValue);
				}
				if (arg.argumentName.startsWith(LOAD_SETTING_DISABLE_PRE))
				{
					Settings.prechecks = false;
				}
				if (arg.argumentName.startsWith(LOAD_SETTING_DISABLE_POST))
				{
					Settings.postchecks = false;
				}
				if (arg.argumentName.startsWith(LOAD_SETTING_DISABLE_INV))
				{
					Settings.invchecks = false;
				}
				if (arg.argumentName.startsWith(LOAD_SETTING_DISABLE_DYNAMIC_TC))
				{
					Settings.dynamictypechecks = false;
				}
				if (arg.argumentName.startsWith(LOAD_SETTING_DISABLE_MEASURE))
				{
					Settings.measureChecks = false;
				}
				if (arg.argumentName.startsWith(LOAD_SETTING_DISABLE_RT_LOG))
				{
					disableRtLog = true;
				}
				if (arg.argumentName.startsWith(LOAD_SETTING_DISABLE_RT_VALIDATOR))
				{
					// TODO: disable runtime validation.
				}
				if (arg.argumentName.startsWith(LOAD_SETTING_LOG_VARIABLES))
				{
					String[] variables = arg.argumentValue.split(",");
					variablesToLog.addAll(Arrays.asList(variables));
				}
			}
		}

		String outputDir = (String) arg0.get("outputDir");

		try
		{
			return new Load2Struct(SimulationManager.getInstance().load(specfiles, linkFile, new File(outputDir), baseDirFile, disableRtLog, variablesToLog)).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	private Integer findVariableDimension(LinkInfo linkInfo)
	{
		List<String> qualifiedName = linkInfo.getQualifiedName();
		
		if(qualifiedName.size() < 2)
		{
			System.out.println("qualified name is too small");
			return -2;
		}
				
		
		try {				
			Definition def= VDMClassHelper.findDefinitionInClass(SimulationManager.getInstance().controller.getInterpreter().getClasses(),qualifiedName);
			if(def == null)
			{
				return -2;
			}
			else
			{
				return extractDefinitionDimensions(def);
			}
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return -2;
		
	}
	
	

	

	private Integer extractDefinitionDimensions(Definition def) {
		
		if(def.getType().isSeq())
		{
			return -1;
		}
		else
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
			
			//dimension does not matter at this point
			List<Integer> dimensions = new Vector<Integer>();
			dimensions.add(findVariableDimension(sdp));
			s.sharedDesignParameters.add(new QueryInterfaceStructsharedDesignParametersStruct(sdp.getIdentifier(),dimensions));
		}

		for (LinkInfo input : SimulationManager.getInstance().getInputVariables().values())
		{
			List<Integer> dimensions = new Vector<Integer>();
			dimensions.add(findVariableDimension(input));
			s.inputs.add(new QueryInterfaceStructinputsStruct(input.getIdentifier(),dimensions));
			
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
	{try 
	{
		Double outputTime = (Double) data.get("outputTime");

		List tmp = Arrays.asList((Object[]) data.get("inputs"));

		List<StepinputsStructParam> inputs = new Vector<StepinputsStructParam>();
		for (Object in : tmp)
		{
			if (in instanceof Map)
			{
				inputs.add(new StepinputsStructParam((Map<String, Object>) in));
			}
		}

		// Boolean singleStep = (Boolean) data.get("singleStep");

		List tmp1 = Arrays.asList((Object[]) data.get("events"));

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
//		try
//		{
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

	public Map<String, Boolean> terminate()
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
		return new TerminateStruct(true).toMap();
	}

	public Map<String, Boolean> unLoad(Map<String, String> data)
	{
		return new UnLoadStruct(true).toMap();
	}

	public Map<String, Boolean> stop() throws RemoteSimulationException
	{
		try
		{
			return new StopStruct(SimulationManager.getInstance().stopSimulation()).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Map<String, Object> getDesignParameter(Map<String, String> data)
			throws RemoteSimulationException
	{
		String parameterName = data.get("name");
		return SimulationManager.getInstance().getDesignParameter(parameterName).toMap();
	}

	public Map<String, List<Map<String, Object>>> getDesignParameters()
			throws RemoteSimulationException
	{
		List<GetDesignParametersStructdesignParametersStruct> list = new Vector<GetDesignParametersStructdesignParametersStruct>();
//		for (String name : SimulationManager.getInstance().getSharedDesignParameters())
//		{
//			list.add(new GetDesignParametersStructdesignParametersStruct(name, SimulationManager.getInstance().getDesignParameter(name)));
//		}
		return new GetDesignParametersStruct(list).toMap();
	}

	public Map<String, Object> getParameter(Map<String, String> data)
			throws RemoteSimulationException
	{
		String name = (String) data.get("name");

		List<Double> value;
		List<Integer> size;
		try
		{
			value = SimulationManager.getInstance().getParameter(name);
			size  = SimulationManager.getInstance().getParameterSize(name);
			return new GetParameterStruct(value,size).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Map<String, List<Map<String, Object>>> getParameters()
			throws RemoteSimulationException
	{
		List<GetParametersStructparametersStruct> list = new Vector<GetParametersStructparametersStruct>();
		try
		{
			for (Entry<String, ValueContents> p : SimulationManager.getInstance().getParameters().entrySet())
			{
				list.add(new GetParametersStructparametersStruct(p.getKey(), p.getValue().value,p.getValue().size));
			}
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
		return new GetParametersStruct(list).toMap();
	}

	public Map<String, Boolean> setDesignParameter(Map<String, Object> data)
			throws RemoteSimulationException
	{

		try
		{
			List<Map<String, Object>> argument = new Vector<Map<String, Object>>();
			argument.add(data);
			boolean success = SimulationManager.getInstance().setDesignParameters(argument);
			return new SetDesignParameterStruct(success).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Map<String, Boolean> setDesignParameters(
			Map<String, List<Map<String, Object>>> data)
			throws RemoteSimulationException
	{
		try
		{
			boolean success = false;
			if (data.values().size() > 0)
			{
				Object s = data.values().iterator().next();
				List tmp = Arrays.asList((Object[]) s);

				success = SimulationManager.getInstance().setDesignParameters(tmp);

			}
			return new SetDesignParametersStruct(success).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Map<String, Boolean> setParameter(Map<String, Object> data)
			throws RemoteSimulationException
	{
		String name = (String) data.get("name");
		List<Double> value = (List<Double>) data.get("value");
		List<Integer> size = (List<Integer>) data.get("size");
		
		Boolean success;
		try
		{
			success = SimulationManager.getInstance().setParameter(name, new ValueContents(value, size));

			return new SetParametersStruct(success).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Map<String, Boolean> setParameters(
			Map<String, List<Map<String, Object>>> data)
			throws RemoteSimulationException
	{
		try
		{
			boolean success = false;
			if (data.values().size() > 0)
			{
				Map<String, Object> s = (Map<String, Object>) data.values().iterator().next();

				success = setParameter(s).get("success");
				return new SetParametersStruct(success).toMap();
			}
			return new SetDesignParametersStruct(success).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Map<String, Boolean> start() throws RemoteSimulationException
	{
		try
		{
			return new StartStruct(SimulationManager.getInstance().start()).toMap();
		} catch (RemoteSimulationException e)
		{
			ErrorLog.log(e);
			throw e;
		}
	}

	public Map<String, Boolean> setToolSetting(Map<String, Object> data)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getToolSetting(Map<String, String> data)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> queryToolSettings() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> writeCSVFile(Map<String, Object> data)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> setLogVariables(Map<String, Object> data)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> queryVariables() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, Object>> queryParameters() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	

	

}
