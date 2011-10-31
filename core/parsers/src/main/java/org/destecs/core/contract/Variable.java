package org.destecs.core.contract;

import java.util.List;
import java.util.Vector;



public class Variable implements IVariable
{
	protected List<Integer> dimensions = new Vector<Integer>();
	
	public Variable(String name, VariableType vType, DataType dType, Object value ) {
		this.setName(name);
		this.setType(vType);
		this.setDataType(dType);
		this.value = value;
		this.dimensions.add(1);
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
		real, bool, array
	}

	
	
	private String name;
	private Object value;
	private VariableType type;
	private DataType dataType;
	
	@Override
	public String toString()
	{
		
		return getType() + " "+getDataType()+ " "+getName()+" := "+value;
	}

	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.destecs.core.contract.IVariable#getName()
	 */
	public String getName() {
		return name;
	}

	public void setType(VariableType type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see org.destecs.core.contract.IVariable#getType()
	 */
	public VariableType getType() {
		return type;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see org.destecs.core.contract.IVariable#getValue()
	 */
	public Object getValue() {
		return value;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	/* (non-Javadoc)
	 * @see org.destecs.core.contract.IVariable#getDataType()
	 */
	public DataType getDataType() {
		return dataType;
	}

	public SharedVariableType getSharedVariableType() {
		return SharedVariableType.Scalar;
	}

	public List<Integer> getDimensions() {
		return dimensions;
	}
	
}
