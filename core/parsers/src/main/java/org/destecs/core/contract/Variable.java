package org.destecs.core.contract;

public class Variable
{
	public Variable(String name, VariableType vType, DataType dType, Object value ) {
		this.name = name;
		this.type = vType;
		this.dataType = dType;
		this.value = value;
	}
	
	
	public enum VariableType
	{
		SharedDesignParameter("design_parameter"), Controlled("controlled"), Monitored("monitored");
		
		public final String syntaxName;
		private VariableType(String name)
		{
			this.syntaxName = name;
		}
		
		
		
	}

	public enum DataType
	{
		real, bool
	}

	
	
	public String name;
	public Object value;
	public VariableType type;
	public DataType dataType;
	
	@Override
	public String toString()
	{
		
		return type + " "+dataType+ " "+name+" := "+value;
	}
}
