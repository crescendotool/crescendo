package org.destecs.vdm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.destecs.protocol.*;

public class CoSimImpl implements ICoSimProtocol
{

	public Map<String, Boolean> Break()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer GetStatus()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String GetVersion()
	{
		return "0.0.0.1";
	}

	public Map<String, Boolean> Initialize(List<Map<String, Integer>> faults)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean Load(String data)
	{
		System.out.println("Loading file: "+ data);
		return true;
	}

	public List<Map<String, Object>> QueryFaults()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, List<Map<String, Object>>> QueryInterface()
	{
		final String SHARED_DESIGN_PARAMETERS ="SharedDesignParameters";
		final String DESIGN_PARAMETERS ="DesignParameters";
		final String INPUTS ="Inputs";
		final String OUTPUTS ="Outputs";
		
		ShareCoSimGroup sdp = new ShareCoSimGroup(SHARED_DESIGN_PARAMETERS);
		sdp.add("minLevel", 4);
		sdp.add("maxLevel", 40);
		
		ShareCoSimGroup dp = new ShareCoSimGroup(DESIGN_PARAMETERS);
		dp.add("mass", 200);
		
		ShareCoSimGroup inputs = new ShareCoSimGroup(INPUTS);
		inputs.add("x", 4);
		inputs.add("y", 40);
		
		ShareCoSimGroup output = new ShareCoSimGroup(OUTPUTS);
		output.add("x1", 4);
		output.add("y1", 40);
		
		Map<String, List<Map<String, Object>>> m = new HashMap<String, List<Map<String, Object>>>();
		
		m.putAll(sdp.encode());
		m.putAll(dp.encode());
		m.putAll(inputs.encode());
		m.putAll(output.encode());
		
		return m;
	}

	public Boolean SetActive(String data)
	{
		System.out.println("Activating: "+ data);
		return true;
	}

	public Map<String, Boolean> SetParameter(Map<String, Object> data)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> ShowData()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> Step(Double outputTime, Boolean goToOutputTime,
			List<Map<String, Object>> inputs)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> Terminate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean UnLoad(String data)
	{
		System.out.println("Unloading file: "+ data);
		return true;
	}

	public Map<String, Object> ivcCommandGetStatus(Map<String, String> data)
	{
		// TODO Auto-generated method stub
		return null;
	}



}
