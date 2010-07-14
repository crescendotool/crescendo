package org.destecs.vdm;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.protocol.IDestecs;
import org.destecs.protocol.structs.GetStatusStruct;
import org.destecs.protocol.structs.GetVersionStruct;
import org.destecs.protocol.structs.InitializeSharedParameterStructParam;
import org.destecs.protocol.structs.InitializeStruct;
import org.destecs.protocol.structs.InitializefaultsStructParam;
import org.destecs.protocol.structs.LoadStruct;
import org.destecs.protocol.structs.QueryFaultsStruct;
import org.destecs.protocol.structs.QueryFaultsStructfaultsStruct;
import org.destecs.protocol.structs.QueryInterfaceStruct;
import org.destecs.protocol.structs.QueryInterfaceStructInputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructOutputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructSharedParameterStruct;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destecs.protocol.structs.TerminateStruct;

@SuppressWarnings("unchecked")
public class CoSimImpl implements IDestecs
{

	private static final String version = "0.0.0.1";

	public Map<String, Boolean> break_()
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	public Map<String, Integer> getStatus()
	{
		return new GetStatusStruct(SimulationManager.getInstance().getStatus()).toMap();
	}

	public Map<String, String> getVersion()
	{
		return new GetVersionStruct(version).toMap();
	}

	public Map<String, Boolean> initialize(Map<String, Object> data)
	{
		List<InitializeSharedParameterStructParam> SharedParameter = new Vector<InitializeSharedParameterStructParam>();
		if (data.keySet().contains("SharedParameter"))
		{
			Object tmpL0 = data.get("SharedParameter");
			for (Object m : (Object[]) tmpL0)
			{
				SharedParameter.add(new InitializeSharedParameterStructParam((Map<String, Object>) m));
			}
		}

		List<InitializefaultsStructParam> faults = new Vector<InitializefaultsStructParam>();
		if (data.keySet().contains("faults"))
		{
			Object tmpL0 = data.get("faults");
			for (Object m : (Object[]) tmpL0)
			{
				faults.add(new InitializefaultsStructParam((Map<String, Integer>) m));
			}
		}

		return new InitializeStruct(SimulationManager.getInstance().initialize(SharedParameter,faults)).toMap();
	}

	public Map<String, Boolean> load(Map<String, String> data)
	{
		String path = data.get(data.keySet().toArray()[0]);

		return new LoadStruct(SimulationManager.getInstance().load(new File(path))).toMap();
	}

	public Map<String, List<Map<String, Object>>> queryFaults()
	{
		QueryFaultsStruct faults = new QueryFaultsStruct();

		faults.faults.add(new QueryFaultsStructfaultsStruct(3, "Bad valve"));

		return faults.toMap();
	}

	public Map<String, Object> queryInterface()
	{
		/*
		 * Shared design variables minLevel maxLevel Variables level :IN valveState :OUT Events HIGH_LEVEL LOW_LEVEL
		 */
		QueryInterfaceStruct s = new QueryInterfaceStruct();

		for (String name : SimulationManager.getInstance().getSharedDesignParameters())
		{
			s.SharedParameter.add(new QueryInterfaceStructSharedParameterStruct(name, new Double(0)));
		}

		for (String name : SimulationManager.getInstance().getInputVariables())
		{
			s.Inputs.add(new QueryInterfaceStructInputsStruct(name, new Double(0)));
		}

		for (String name : SimulationManager.getInstance().getOutputVariables())
		{
			s.Outputs.add(new QueryInterfaceStructOutputsStruct(name, new Double(0)));
		}

		// TODO add events

		return s.toMap();
	}

	public Map<String, Boolean> setActive(Map<String, String> data)
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	public Map<String, Boolean> setParameter(Map<String, Object> data)
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	public Map<String, Boolean> showData()
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
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

//		Boolean singleStep = (Boolean) data.get("singleStep");

//		System.out.println("Decoded:");
//		System.out.println("outputtime:" + outputTime);
//		System.out.println("singlestep:" + singleStep);
//		System.out.println("Inputs: " + inputs);

		// Ignore single step
		StepStruct result = SimulationManager.getInstance().step(outputTime, inputs);

		return result.toMap();
	}

	public Map<String, Boolean> terminate()
	{
		System.out.println("DESTECS VDM is terminating now...");
		return new TerminateStruct(true).toMap();
		// TODO terminate
	}

	public Map<String, Boolean> unLoad(Map<String, String> data)
	{
		throw new NoSuchMethodError("Not supported by VDMJ");
	}

	// public Map<String, List<Map<String, Object>>> queryInterface()
	// {
	// final String SHARED_DESIGN_PARAMETERS ="SharedDesignParameters";
	// final String DESIGN_PARAMETERS ="DesignParameters";
	// final String INPUTS ="Inputs";
	// final String OUTPUTS ="Outputs";
	//		
	// ShareCoSimGroup sdp = new ShareCoSimGroup(SHARED_DESIGN_PARAMETERS);
	// sdp.add("minLevel", 4);
	// sdp.add("maxLevel", 40);
	//		
	// ShareCoSimGroup dp = new ShareCoSimGroup(DESIGN_PARAMETERS);
	// dp.add("mass", 200);
	//		
	// ShareCoSimGroup inputs = new ShareCoSimGroup(INPUTS);
	// inputs.add("x", 4);
	// inputs.add("y", 40);
	//		
	// ShareCoSimGroup output = new ShareCoSimGroup(OUTPUTS);
	// output.add("x1", 4);
	// output.add("y1", 40);
	//		
	// Map<String, List<Map<String, Object>>> m = new HashMap<String, List<Map<String, Object>>>();
	//		
	// m.putAll(sdp.encode());
	// m.putAll(dp.encode());
	// m.putAll(inputs.encode());
	// m.putAll(output.encode());
	//		
	// return m;
	// }

}
