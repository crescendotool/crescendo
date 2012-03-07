package org.destecs.core.contract;

import java.util.List;


public class MatrixVariable extends Variable {

	public MatrixVariable(String name, VariableType vType,
			Object value, List<Integer> sizes, int line) {
		super(name, vType, DataType.matrix, value, line);
		this.dimensions.clear();		
		this.dimensions.addAll(sizes);
		
	}

	@Override
	public SharedVariableType getSharedVariableType() {
		return SharedVariableType.Matrix;
	}

	@Override
	public String toString()
	{
		return getType() + " "+getDataType()+ " "+getName();
	}

}