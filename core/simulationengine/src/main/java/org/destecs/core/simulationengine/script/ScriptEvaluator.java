package org.destecs.core.simulationengine.script;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.core.simulationengine.script.values.BooleanValue;
import org.destecs.core.simulationengine.script.values.DoubleValue;
import org.destecs.core.simulationengine.script.values.Value;
import org.destecs.script.ast.ACtDomain;
import org.destecs.script.ast.ADeDomain;
import org.destecs.script.ast.analysis.AnalysisAdaptor;
import org.destecs.script.ast.expressions.ABinaryExp;
import org.destecs.script.ast.expressions.ANumericalSingleExp;
import org.destecs.script.ast.expressions.ASystemTimeSingleExp;
import org.destecs.script.ast.expressions.PExp;
import org.destecs.script.ast.expressions.binop.ALessEqualBinop;
import org.destecs.script.ast.statement.AAssignStm;
import org.destecs.script.ast.statement.AErrorMessageStm;
import org.destecs.script.ast.statement.APrintMessageStm;
import org.destecs.script.ast.statement.AQuitStm;
import org.destecs.script.ast.statement.ARevertStm;
import org.destecs.script.ast.statement.AWarnMessageStm;
import org.destecs.script.ast.statement.AWhenStm;
import org.destecs.script.ast.statement.PStm;
import org.destecs.script.ast.statement.SMessageStm;

public class ScriptEvaluator extends AnalysisAdaptor
{
	public static class VariableValue
	{
		public String name;
		public Value value;
		public Simulator simulator;

		public VariableValue(String name, Value value, Simulator simulator)
		{
			this.name = name;
			this.value = value;
			this.simulator = simulator;
		}

		@Override
		public int hashCode()
		{
			return name.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof VariableValue)
			{
				return name.equals(((VariableValue) obj).name);
			}
			return super.equals(obj);
		}

		@Override
		public String toString()
		{
			return simulator + " " + name + " = " + value;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ISimulatorControl interpreter;
	ExpressionEvaluator expEval;
	Map<AWhenStm, Set<VariableValue>> variableCache = new Hashtable<AWhenStm, Set<VariableValue>>();
	Map<AWhenStm, PExp> whenForExp = new Hashtable<AWhenStm, PExp>();

	public ScriptEvaluator(ISimulatorControl simulator)
	{
		this.interpreter = simulator;
		expEval = new ExpressionEvaluator(simulator);
	}

	@Override
	public void caseAWhenStm(AWhenStm node)
	{
		Value v = node.getTest().apply(expEval);

		if (v instanceof BooleanValue && ((BooleanValue) v).value
				|| checkWhenFor(node))
		{
			if (node.getFor() != null && whenForExp.get(node)==null)
			{
				Double expireTime = ((DoubleValue) node.getFor().apply(expEval)).value
						+ ((DoubleValue) new ASystemTimeSingleExp().apply(expEval)).value;
				whenForExp.put(node, new ABinaryExp(new ASystemTimeSingleExp(), new ALessEqualBinop(), new ANumericalSingleExp(expireTime)));
			}
			for (PStm stm : node.getThen())
			{
				stm.apply(this);
			}
		} else
		{
			whenForExp.remove(node);
			for (PStm stm : node.getAfter())
			{
				stm.apply(this);
			}
		}
	}

	private boolean checkWhenFor(AWhenStm node)
	{
		PExp forExp = whenForExp.get(node);
		if (forExp != null)
		{
			Value v = forExp.apply(expEval);
			return (v instanceof BooleanValue && ((BooleanValue) v).value);
		}
		return false;
	}

	@Override
	public void caseAAssignStm(AAssignStm node)
	{
		Value v = node.getValue().apply(expEval);
		Simulator simulator = null;
		if (node.getDomain() instanceof ADeDomain)
		{
			simulator = Simulator.DE;
		} else if (node.getDomain() instanceof ACtDomain)
		{
			simulator = Simulator.CT;
		}

		storeVariable(node.getAncestor(AWhenStm.class), new VariableValue(node.getName(), Value.valueOf(interpreter.getVariableValue(simulator, node.getName())), simulator));

		if (v instanceof BooleanValue)
		{
			interpreter.setVariable(simulator, node.getName(), ((BooleanValue) v).value);
		} else if (v instanceof DoubleValue)
		{
			interpreter.setVariable(simulator, node.getName(), ((DoubleValue) v).value);
		}
	}

	private void storeVariable(AWhenStm node, VariableValue var)
	{
		Set<VariableValue> s = variableCache.get(node);
		if (s == null)
		{
			s = new HashSet<VariableValue>();
		}

		s.add(var);

		variableCache.put(node, s);
	}

	private VariableValue getVariableValue(AWhenStm node, String variableName)
	{
		Set<VariableValue> s = variableCache.get(node);
		if (s == null)
		{
			return null;
		}

		for (VariableValue variableValue : s)
		{
			if (variableValue.name.equals(variableName))
			{
				return variableValue;
			}
		}
		return null;
	}

	@Override
	public void caseARevertStm(ARevertStm node)
	{
		VariableValue var = getVariableValue(node.getAncestor(AWhenStm.class), node.getName());
		if (var != null)
		{
			if (var.value instanceof BooleanValue)
			{
				interpreter.setVariable(var.simulator, var.name, ((BooleanValue) var.value).value);
			} else if (var.value instanceof DoubleValue)
			{
				interpreter.setVariable(var.simulator, var.name, ((DoubleValue) var.value).value);
			}
		}
	}

	@Override
	public void defaultSMessageStm(SMessageStm node)
	{
		String type = "";
		if (node instanceof APrintMessageStm)
		{
			type = "[print]";
		} else if (node instanceof AErrorMessageStm)
		{
			type = "[error]";
		} else if (node instanceof AWarnMessageStm)
		{
			type = "[warning]";
		}

		interpreter.showMessage(type, node.getMessage());
	}

	@Override
	public void caseAQuitStm(AQuitStm node)
	{
		interpreter.quite();
	}
}
