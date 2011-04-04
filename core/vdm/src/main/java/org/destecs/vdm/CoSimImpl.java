package org.destecs.vdm;

import java.io.File;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.overturetool.vdmj.scheduler.SystemClock.TimeUnit;

import org.destecs.protocol.IDestecs;
import org.destecs.protocol.structs.GetStatusStruct;
import org.destecs.protocol.structs.GetVersionStruct;
import org.destecs.protocol.structs.Load2Struct;
import org.destecs.protocol.structs.Load2argumentsStructParam;
import org.destecs.protocol.structs.LoadStruct;
import org.destecs.protocol.structs.QueryInterfaceStruct;
import org.destecs.protocol.structs.SetDesignParametersStruct;
import org.destecs.protocol.structs.SetParametersStruct;
import org.destecs.protocol.structs.StartStruct;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destecs.protocol.structs.StopStruct;
import org.destecs.protocol.structs.TerminateStruct;
import org.destecs.protocol.structs.UnLoadStruct;
import org.overturetool.vdmj.scheduler.SystemClock;

@SuppressWarnings("unchecked")
public class CoSimImpl implements IDestecs
{

	private static final String version = "0.0.0.2";

	public Map<String, Integer> getStatus()
	{
		return new GetStatusStruct(SimulationManager.getInstance().getStatus()).toMap();
	}

	public Map<String, Object> getVersion()
	{
		return new GetVersionStruct("VDMJ", version).toMap();
	}

	public Map<String, Boolean> initialize()
	{
		try
		{
			return new StartStruct(SimulationManager.getInstance().initialize()).toMap();
		} catch (SimulationException e)
		{
			ErrorLog.log(e);
			throw new UndeclaredThrowableException(e);
		}
	}

	public Map<String, Boolean> load(Map<String, String> data)
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
			return new LoadStruct(SimulationManager.getInstance().load(files,outputFolder,linkFile)).toMap();
		} catch (SimulationException e)
		{
			ErrorLog.log(e);
			throw new UndeclaredThrowableException(e);
		}
	}
	
	/***
	 * local helper function of load
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
	{
		List tmp = Arrays.asList((Object[]) arg0.get("arguments"));

		List<File> specfiles = new Vector<File>();
		File linkFile = null;
		for (Object in : tmp)
		{
			if (in instanceof Map)
			{
				Load2argumentsStructParam arg =new Load2argumentsStructParam((Map<String, Object>) in);
				
				if(arg.argumentName.startsWith("file"))
				{
					specfiles.add(new File(arg.argumentValue));
				}
				
				if(arg.argumentName.startsWith("link"))
				{
					linkFile= new File(arg.argumentValue);
				}
			}
		}
		
		String outputDir = (String) arg0.get("outputDir");

		try
		{
			return new Load2Struct(SimulationManager.getInstance().load(specfiles,linkFile,new File(outputDir))).toMap();
		} catch (SimulationException e)
		{
			ErrorLog.log(e);
			throw new UndeclaredThrowableException(e);
		}
	}

	// public Map<String, List<Map<String, Object>>> queryFaults()
	// {
	// QueryFaultsStruct faults = new QueryFaultsStruct();
	//
	// faults.faults.add(new QueryFaultsStructfaultsStruct(3, "Bad valve"));
	//
	// return faults.toMap();
	// }

	public Map<String, Object> queryInterface()
	{
		/*
		 * Shared design variables minLevel maxLevel Variables level :IN valveState :OUT Events HIGH_LEVEL LOW_LEVEL
		 */
		QueryInterfaceStruct s = new QueryInterfaceStruct();

		for (String name : SimulationManager.getInstance().getSharedDesignParameters())
		{
			s.sharedDesignParameters.add(name);
		}

		for (String name : SimulationManager.getInstance().getInputVariables())
		{
			s.inputs.add(name);
		}

		for (String name : SimulationManager.getInstance().getOutputVariables())
		{
			s.outputs.add(name);
		}

		// No events from VDM

		return s.toMap();
	}

	public Map<String, Object> step(Map<String, Object> data)
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
		try
		{
			outputTime = new Double(SystemClock.timeToInternal(TimeUnit.seconds, outputTime));
			result = SimulationManager.getInstance().step(outputTime, inputs, events);

			result.time = SystemClock.internalToTime(TimeUnit.seconds, result.time.longValue());

			return result.toMap();
		} catch (SimulationException e)
		{
			ErrorLog.log(e);
			throw new UndeclaredThrowableException(e);
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

	public Map<String, Boolean> stop()
	{
		try
		{
			return new StopStruct(SimulationManager.getInstance().stopSimulation()).toMap();
		} catch (SimulationException e)
		{
			ErrorLog.log(e);
			throw new UndeclaredThrowableException(e);
		}
	}

	public Map<String, Double> getDesignParameter(Map<String, String> data)
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	public Map<String, List<Map<String, Object>>> getDesignParameters()
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	public Map<String, Double> getParameter(Map<String, String> data)
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	public Map<String, List<Map<String, Object>>> getParameters()
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	public Map<String, Boolean> setDesignParameter(Map<String, Object> data)
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	public Map<String, Boolean> setDesignParameters(
			Map<String, List<Map<String, Object>>> data)
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
		} catch (SimulationException e)
		{
			ErrorLog.log(e);
			throw new UndeclaredThrowableException(e);
		}
	}

	public Map<String, Boolean> setParameter(Map<String, Object> data)
	{
		String name = (String) data.get("name");
		Double value = (Double) data.get("value");
		Boolean success;
		try
		{
			success = SimulationManager.getInstance().setParameter(name, value);

			return new SetParametersStruct(success).toMap();
		} catch (SimulationException e)
		{
			ErrorLog.log(e);
			throw new UndeclaredThrowableException(e);
		}
	}

	public Map<String, Boolean> setParameters(
			Map<String, List<Map<String, Object>>> data)
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	public Map<String, Boolean> start()
	{
		try
		{
			return new StartStruct(SimulationManager.getInstance().start()).toMap();
		} catch (SimulationException e)
		{
			ErrorLog.log(e);
			throw new UndeclaredThrowableException(e);
		}
	}

	
}
