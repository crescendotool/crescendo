package org.destecs.vdm;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.protocol.IDestecs;
import org.destecs.protocol.structs.GetStatusStruct;
import org.destecs.protocol.structs.GetVersionStruct;
import org.destecs.protocol.structs.InitializeStruct;
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

@SuppressWarnings("unchecked")
public class CoSimImpl implements IDestecs
{

	private static final String version = "0.0.0.1";

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
		// List<InitializeSharedParameterStructParam> SharedParameter = new
		// Vector<InitializeSharedParameterStructParam>();
		// if (data.keySet().contains("SharedParameter"))
		// {
		// Object tmpL0 = data.get("SharedParameter");
		// for (Object m : (Object[]) tmpL0)
		// {
		// SharedParameter.add(new InitializeSharedParameterStructParam((Map<String, Object>) m));
		// }
		// }
		//
		// List<InitializefaultsStructParam> faults = new Vector<InitializefaultsStructParam>();
		// if (data.keySet().contains("faults"))
		// {
		// Object tmpL0 = data.get("faults");
		// for (Object m : (Object[]) tmpL0)
		// {
		// faults.add(new InitializefaultsStructParam((Map<String, Integer>) m));
		// }
		// }
		//
		// return new InitializeStruct(SimulationManager.getInstance().initialize(SharedParameter, faults)).toMap();
		return new InitializeStruct(true).toMap();
	}

	public Map<String, Boolean> load(Map<String, String> data)
	{
		String path = data.get(data.keySet().toArray()[0]);

		return new LoadStruct(SimulationManager.getInstance().load(new File(path))).toMap();
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
		outputTime = outputTime * 1000;

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
		StepStruct result = SimulationManager.getInstance().step(outputTime, inputs, events);
		result.time = result.time / 1000;

		return result.toMap();
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
		return new StopStruct(SimulationManager.getInstance().stopSimulation()).toMap();
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
		boolean success = false;
		if (data.values().size() > 0)
		{
			Object s = data.values().iterator().next();
			List tmp = Arrays.asList((Object[]) s);
			success = SimulationManager.getInstance().setDesignParameters(tmp);
		}
		return new SetDesignParametersStruct(success).toMap();
	}

	public Map<String, Boolean> setParameter(Map<String, Object> data)
	{
		String name = (String)data.get("name");
		Double value=(Double)data.get("value");
		Boolean success = SimulationManager.getInstance().setParameter(name, value);
		return new SetParametersStruct(success).toMap();
	}

	public Map<String, Boolean> setParameters(
			Map<String, List<Map<String, Object>>> data)
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	public Map<String, Boolean> start()
	{
		return new StartStruct(SimulationManager.getInstance().initialize()).toMap();
	}
}
