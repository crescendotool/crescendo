/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.core.contract;

import java.util.List;
import java.util.Vector;



public class Variable implements IVariable
{
	protected List<Integer> dimensions = new Vector<Integer>();
	protected int line;
	
	public Variable(String name, VariableType vType, DataType dType, Object value, int line) {
		this.setName(name);
		this.setType(vType);
		this.setDataType(dType);
		this.value = value;
		this.dimensions.add(1);
		this.line = line;
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

	public int getLine()
	{
		return line;
	}
	
}
