package org.destecs.core.contract;

import java.util.List;


public class ArrayVariable extends Variable{

	
	
	public ArrayVariable(String name, VariableType vType,
			Object value, List<Integer> sizes) {
		super(name, vType, DataType.array, value);
		this.dimensions.addAll(sizes);
	
	}

	@Override
	public SharedVariableType getSharedVariableType() {
		return SharedVariableType.Matrix;
	}

}
