package org.destecs.core.scenario;

public class Action implements Comparable<Action>
{
	public enum Simulator
	{
		DE("VDM-RT"), CT("20-Sim"), ALL("All");

		private String name;

		private Simulator(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	
	public Action(Double time, Simulator targetSimulator, String variableName, Double variableValue) {
		this.time = time;
		this.targetSimulator = targetSimulator;
		this.variableName = variableName;
		this.variableValue = variableValue;	
	}
	
	public Simulator targetSimulator;
	public Double time;
	public String variableName;
	public Double variableValue;
	
	
	public int compareTo(Action o)
	{
		return this.time.compareTo(o.time);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
	
	
	@Override
	public String toString()
	{
	return pad( time.toString(),10)+ " " + targetSimulator.toString()+"\t" +  pad( variableName,20)+" := "+ variableValue;
	}
	
	private static String pad(String text,int size)
	{
		StringBuffer sb = new StringBuffer(text);
		while (sb.length()<size)
		{
			sb.append(" ");	
		}
		return sb.toString();
	}
}
