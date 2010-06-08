package org.destecs.protocol;

import java.util.Map;
import java.util.List;
import java.util.Hashtable;
public class ProxyICoSimProtocol
{
	ICoSimProtocol source;
	public ProxyICoSimProtocol(ICoSimProtocol source)
	{
		this.source = source; 
	}
	
	/**
	*  Gets the version of the CoSim implementation in the form: majorVersion.MinorVersion.majorBuild.minorBuild  
	*/
	public String getVersion()
	{
		return (String)source.getVersion().values().toArray()[0];
	}
	
	/**
	*  Loads a file from its path. The model is activated after load 
	*/
	public Map<String,Boolean> load(String name)
	{
		Map<String,String> data = new Hashtable<String,String>();
		data.put("name",name);
		return source.load(data);
	}
	
	/**
	*  Unloads a file from its path 
	*/
	public Map<String,Boolean> unLoad(String name)
	{
		Map<String,String> data = new Hashtable<String,String>();
		data.put("name",name);
		return source.unLoad(data);
	}
	
	/**
	*  Activates a file or project from its path 
	*/
	public Map<String,Boolean> setActive(String name)
	{
		Map<String,String> data = new Hashtable<String,String>();
		data.put("name",name);
		return source.setActive(data);
	}
	
	/**
	*  Gets the status: 0=Not Initialized, 1=Initialized, 2=Step Taken, not finished, 3=Finished 
	*/
	public Integer getStatus()
	{
		return (Integer)source.getStatus().values().toArray()[0];
	}
	
	/**
	*  Queries the interface used in Co-Simulation Grouped in a Map with keys SharedDesignParameters,DesignParameters,Inputs,Outputs.
	* 		The structure of each of them is a Map from name to value 
	*/
	public Map<String,Object> queryInterface()
	{
		return source.queryInterface();
	}
	public List<Map<String,Object>> queryFaults()
	{
		return (List<Map<String,Object>>)source.queryFaults().values().toArray()[0];
	}
	public Map<String,Boolean> initialize(List<Map<String,Integer>> faults)
	{
		Map<String,List<Map<String,Integer>>> data = new Hashtable<String,List<Map<String,Integer>>>();
		data.put("faults",faults);
		return source.initialize(data);
	}
	public Map<String,Object> step(Double outputTime,List<Map<String,Object>> inputs,Boolean singleStep)
	{
		Map<String,Object> data = new Hashtable<String,Object>();
		data.put("outputTime",outputTime);
		data.put("inputs",inputs);
		data.put("singleStep",singleStep);
		return source.step(data);
	}
	public Boolean terminate()
	{
		return (Boolean)source.terminate().values().toArray()[0];
	}
	public Map<String,Boolean> setParameter(String name,Double value)
	{
		Map<String,Object> data = new Hashtable<String,Object>();
		data.put("name",name);
		data.put("value",value);
		return source.setParameter(data);
	}
	public Boolean break_()
	{
		return (Boolean)source.break_().values().toArray()[0];
	}
	public Boolean showData()
	{
		return (Boolean)source.showData().values().toArray()[0];
	}
}