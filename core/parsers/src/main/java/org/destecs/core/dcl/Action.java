package org.destecs.core.dcl;


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

//	public enum Condition
//	{
//		WHEN("when");
//		
//		private String name;
//
//		private Condition(String name)
//		{
//			this.name = name;
//		}
//
//		@Override
//		public String toString()
//		{
//			return name;
//		}
//	}

	public Action( String variableName, Double variableValue) {
		
		this.time = 0.0;
		this.targetSimulator = Simulator.CT;
		System.out.println(targetSimulator);
		this.variableName = variableName;
		this.variableValue = variableValue;	
	
	}
	
	
	public Action( String targetSimulator, String variableName, Double variableValue) {
//		this.time = 0.2;//hard coding		//0.2s
		this.time = 0.0;
		System.out.println(targetSimulator);
		if(targetSimulator.matches("ct")){
			System.out.println("this.targetSimulator: ct" );
				this.targetSimulator = Simulator.CT;
			}
		else if(targetSimulator.matches("de")){
			System.out.println("this.targetSimulator: de" );
				this.targetSimulator = Simulator.DE;
			}
//		else if{
//			System.out.println("this.targetSimulator: all" );
//				this.targetSimulator = Simulator.ALL;
//			}
		this.variableName = variableName;
//		this.variableName = "level2";
		this.variableValue = variableValue;	
//		this.condition = condi ;		
	}
	
	//***** TIME TRIGGERED ******	
	public Action( Double time, String targetSimulator,String variableName, Double variableValue) {
		
			this.time = time;
			if(targetSimulator.matches("ct")){
					this.targetSimulator = Simulator.CT;
				}
			else if(targetSimulator.matches("de")){
					this.targetSimulator = Simulator.DE;
				}
			else{
					this.targetSimulator = Simulator.ALL;
				}
			this.variableName = variableName;
			this.variableValue = variableValue;
			
		}
	

	
//	public void Name( String variableName) {
//		
//		this.variableName = variableName;
//	
//	}
	
//	public Type compare(String lhs ,Double rhs ,OP operator ) {
//		$singletonexpression.text, $expression.text, $binaryoperator.text
////		if (expression== true)
//		
//		;       
//    }
	
//	public LHS lhs;
	
	public Simulator targetSimulator;
	public Double time;
	public String variableName;
	public Double variableValue;
//	public Condition condition;
	
	
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
//	return pad( time.toString(),10)+ " " + targetSimulator.toString()+"\t" +  pad( variableName,20)+" := "+ variableValue;
		return " " + targetSimulator.toString()+"\t" +  pad( variableName,20)+" := "+ variableValue;
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
	

//	public void compare(boolean expression ) {
//			
//			if (expression== true)
//			
//			;
//	       
//	    }

}
