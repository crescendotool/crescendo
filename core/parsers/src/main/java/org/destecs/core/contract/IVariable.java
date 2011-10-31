package org.destecs.core.contract;

import java.util.List;

import org.destecs.core.contract.Variable.DataType;
import org.destecs.core.contract.Variable.VariableType;

public interface IVariable {

	enum SharedVariableType {Scalar, Matrix};
	
	public abstract String getName();

	public abstract VariableType getType();

	public abstract Object getValue();

	public abstract DataType getDataType();
	
	public abstract SharedVariableType getSharedVariableType();
	
	public abstract List<Integer> getDimensions();
	

}