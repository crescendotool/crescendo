package org.destecs.vdm;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.protocol.ICoSimProtocol;

public class CoSimImpl implements ICoSimProtocol
{

	
	public Boolean load(String data)
	{
		System.out.println("Loading file: "+ data);
		return true;
	}

//	public List<Map<String, Object>> QueryFaults()
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}

//	public Map<String, List<Map<String, Object>>> queryInterface()
//	{
//		final String SHARED_DESIGN_PARAMETERS ="SharedDesignParameters";
//		final String DESIGN_PARAMETERS ="DesignParameters";
//		final String INPUTS ="Inputs";
//		final String OUTPUTS ="Outputs";
//		
//		ShareCoSimGroup sdp = new ShareCoSimGroup(SHARED_DESIGN_PARAMETERS);
//		sdp.add("minLevel", 4);
//		sdp.add("maxLevel", 40);
//		
//		ShareCoSimGroup dp = new ShareCoSimGroup(DESIGN_PARAMETERS);
//		dp.add("mass", 200);
//		
//		ShareCoSimGroup inputs = new ShareCoSimGroup(INPUTS);
//		inputs.add("x", 4);
//		inputs.add("y", 40);
//		
//		ShareCoSimGroup output = new ShareCoSimGroup(OUTPUTS);
//		output.add("x1", 4);
//		output.add("y1", 40);
//		
//		Map<String, List<Map<String, Object>>> m = new HashMap<String, List<Map<String, Object>>>();
//		
//		m.putAll(sdp.encode());
//		m.putAll(dp.encode());
//		m.putAll(inputs.encode());
//		m.putAll(output.encode());
//		
//		return m;
//	}

	public Boolean setActive(String data)
	{
		System.out.println("Activating: "+ data);
		return true;
	}



	public Boolean unLoad(String data)
	{
		System.out.println("Unloading file: "+ data);
		return true;
	}




	public Map<String, List<Boolean>> testMethodKL()
	{
		Map<String, List<Boolean>> map = new Hashtable<String, List<Boolean>>();
		
		List<Boolean> res = new Vector<Boolean>();
		res.add(true);
		map.put("a",res);
		map.put("b",res);
		return map;
	}

	public Map<String, Boolean> break_()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Integer> getStatus()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getVersion()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> initialize(
			Map<String, List<Map<String, Integer>>> data)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> load(Map<String, String> data)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, List<Map<String, Object>>> queryFaults()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> queryInterface()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> setActive(Map<String, String> data)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> setParameter(Map<String, Object> data)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> showData()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> step(Map<String, Object> data)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> terminate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Boolean> unLoad(Map<String, String> data)
	{
		// TODO Auto-generated method stub
		return null;
	}

	



}
