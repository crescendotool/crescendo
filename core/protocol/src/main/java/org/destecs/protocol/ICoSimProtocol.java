package org.destecs.protocol;

import java.util.Map;
import java.util.List;

public interface ICoSimProtocol
{
	
	/**
	*  Gets the version of the CoSim implementation in the form: majorVersion.MinorVersion.majorBuild.minorBuild  
	*/
	String GetVersion();

	
	/**
	*  Loads a file from its path. The model is activated after load 
	*/
	Boolean Load(String data);

	
	/**
	*  Unloads a file from its path 
	*/
	Boolean UnLoad(String data);

	
	/**
	*  Activates a file or project from its path 
	*/
	Boolean SetActive(String data);

	
	/**
	*  Gets the status: 0=Not Initialized, 1=Initialized, 2=Step Taken, not finished, 3=Finished 
	*/
	Integer GetStatus();

	
	/**
	*  Queries the interface used in Co-Simulation Grouped in a Map with keys SharedDesignParameters,DesignParameters,Inputs,Outputs.
	* 		The structure of each of them is a Map from name to value 
	*/
	Map<String,List<Map<String,Object>>> QueryInterface();

	List<Map<String,Object>> QueryFaults();

	Map<String,Boolean> Initialize(List<Map<String,Integer>> faults);

	Map<String,Object> Step(Double outputTime, Boolean goToOutputTime, List<Map<String,Object>> inputs);

	Map<String,Boolean> Terminate();

	Map<String,Boolean> SetParameter(Map<String,Object> data);

	Map<String,Boolean> Break();

	Map<String,Boolean> ShowData();

	Map<String,Object> ivcCommandGetStatus(Map<String,String> data);

}