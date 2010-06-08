package org.destecs.protocol;

import java.util.Map;
import java.util.List;
import org.destetcs.core.xmlrpc.extensions.RpcMethod;

public interface IDestecs
{
	
	/**
	*  Gets the version of the CoSim implementation in the form: majorVersion.MinorVersion.majorBuild.minorBuild  
	*/
	@RpcMethod(methodName = "Destecs.GetVersion")
	public Map<String,String> getVersion();

	
	/**
	*  Loads a file from its path. The model is activated after load 
	*/
	@RpcMethod(methodName = "Destecs.Load")
	public Map<String,Boolean> load(Map<String,String> data);

	
	/**
	*  Unloads a file from its path 
	*/
	@RpcMethod(methodName = "Destecs.UnLoad")
	public Map<String,Boolean> unLoad(Map<String,String> data);

	
	/**
	*  Activates a file or project from its path 
	*/
	@RpcMethod(methodName = "Destecs.SetActive")
	public Map<String,Boolean> setActive(Map<String,String> data);

	
	/**
	*  Gets the status: 0=Not Initialized, 1=Initialized, 2=Step Taken, not finished, 3=Finished 
	*/
	@RpcMethod(methodName = "Destecs.GetStatus")
	public Map<String,Integer> getStatus();

	
	/**
	*  Queries the interface used in Co-Simulation Grouped in a Map with keys SharedDesignParameters,DesignParameters,Inputs,Outputs.
	* 		The structure of each of them is a Map from name to value 
	*/
	@RpcMethod(methodName = "Destecs.QueryInterface")
	public Map<String,Object> queryInterface();

	@RpcMethod(methodName = "Destecs.QueryFaults")
	public Map<String,List<Map<String,Object>>> queryFaults();

	@RpcMethod(methodName = "Destecs.Initialize")
	public Map<String,Boolean> initialize(Map<String,List<Map<String,Integer>>> data);

	@RpcMethod(methodName = "Destecs.Step")
	public Map<String,Object> step(Map<String,Object> data);

	@RpcMethod(methodName = "Destecs.Terminate")
	public Map<String,Boolean> terminate();

	@RpcMethod(methodName = "Destecs.SetParameter")
	public Map<String,Boolean> setParameter(Map<String,Object> data);

	@RpcMethod(methodName = "Destecs.Break")
	public Map<String,Boolean> break_();

	@RpcMethod(methodName = "Destecs.ShowData")
	public Map<String,Boolean> showData();

}