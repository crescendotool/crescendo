//package org.destecs.core.simulationengine.senario;
//
//import org.destecs.core.simulationengine.SimulationEngine.Simulator;
//
//public class Action implements Comparable<Action>
//{
//	public Simulator targetSimulator;
//	public Double time;
//	public String variableName;
//	public Double variableValue;
//	public int compareTo(Action o)
//	{
//		return this.time.compareTo(o.time);
//	}
//	
//	@Override
//	public String toString()
//	{
//	return pad( time.toString(),10)+ " " +targetSimulator.toString()+"\t" + pad( variableName,20)+" := "+ variableValue;
//	}
//	
//	private static String pad(String text,int size)
//	{
//		while (text.length()<size)
//		{
//			text+=" ";	
//		}
//		return text;
//	}
//}
