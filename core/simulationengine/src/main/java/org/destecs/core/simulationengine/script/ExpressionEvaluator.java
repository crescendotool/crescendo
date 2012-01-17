package org.destecs.core.simulationengine.script;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.core.simulationengine.script.values.BooleanValue;
import org.destecs.core.simulationengine.script.values.DoubleValue;
import org.destecs.core.simulationengine.script.values.Value;
import org.destecs.script.ast.analysis.AnswerAdaptor;
import org.destecs.script.ast.expressions.ABinaryExp;
import org.destecs.script.ast.expressions.AIdentifierSingleExp;
import org.destecs.script.ast.expressions.ANumericalSingleExp;
import org.destecs.script.ast.expressions.ASystemTimeSingleExp;
import org.destecs.script.ast.expressions.AUnaryExp;

public class ExpressionEvaluator extends AnswerAdaptor<Value>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ISimulatorControl interpreter;

	public ExpressionEvaluator(ISimulatorControl simulator)
	{
		this.interpreter = simulator;
	}

	@Override
	public Value caseASystemTimeSingleExp(ASystemTimeSingleExp node)
	{
		return new DoubleValue(interpreter.getSystemTime());
	}

	@Override
	public Value caseANumericalSingleExp(ANumericalSingleExp node)
	{
		return new DoubleValue(node.getValue());
	}

	@Override
	public Value caseAIdentifierSingleExp(AIdentifierSingleExp node)
	{
		Simulator simulator = null;
		switch (node.getDomain().kindPDomain())
		{
			case CT:
				simulator = Simulator.CT;
				break;
			case DE:
				simulator = Simulator.DE;
				break;

		}
		Object val = interpreter.getVariableValue(simulator, node.getName());

		Value newValue = Value.valueOf(val);
		if (newValue == null)
		{
			interpreter.scriptError("Could not find variable: " + simulator
					+ " " + node.getName());
		}
		return newValue;
	}

	@Override
	public Value caseABinaryExp(ABinaryExp node)
	{
		Value left = node.getLeft().apply(this);
		Value right = node.getRight().apply(this);
		if (left.getClass() != right.getClass())
		{
			return new BooleanValue(false);
		}

		switch (node.getOperator().kindPBinop())
		{
			case AND:
			{
				if (left instanceof BooleanValue)
				{
					return new BooleanValue(((BooleanValue) left).value
							&& ((BooleanValue) right).value);
				}
			}
				break;
			case DIFFERENT:
			{
				if (left instanceof DoubleValue)
				{
					return new BooleanValue(((DoubleValue) left).value != ((DoubleValue) right).value);
				} else if (left instanceof BooleanValue)
				{
					return new BooleanValue(((BooleanValue) left).value != ((BooleanValue) right).value);
				}
			}
				break;
			case DIV:
				// TODO
				break;
			case DIVIDE:
			{
				if (left instanceof DoubleValue)
				{
					return new DoubleValue(((DoubleValue) left).value
							/ ((DoubleValue) right).value);
				}
			}
				break;
			case EQUAL:
			{
				if (left instanceof DoubleValue)
				{
					return new BooleanValue(((DoubleValue) left).value == ((DoubleValue) right).value);
				} else if (left instanceof BooleanValue)
				{
					return new BooleanValue(((BooleanValue) left).value == ((BooleanValue) right).value);
				}
			}
			case EQUIV:
				// TODO
				break;
			case FOR:
				// TODO
				break;
			case IMPLIES:
			{
				if (left instanceof BooleanValue)
				{
					if (((BooleanValue) left).value)
					{
						return right;
					} else
					{
						return new BooleanValue(true);
					}
				}
			}
				break;
			case LESSEQUAL:
			{
				if (left instanceof DoubleValue)
				{
					return new BooleanValue(((DoubleValue) left).value <= ((DoubleValue) right).value);
				}
			}
				break;
			case LESSTHAN:
			{
				if (left instanceof DoubleValue)
				{
					return new BooleanValue(((DoubleValue) left).value < ((DoubleValue) right).value);
				}
			}
				break;
			case MINUS:
			{
				if (left instanceof DoubleValue)
				{
					return new DoubleValue(((DoubleValue) left).value
							- ((DoubleValue) right).value);
				}
			}
				break;
			case MOD:
			{
				if (left instanceof DoubleValue)
				{
					double lv = ((DoubleValue) left).value;
					double rv = ((DoubleValue) right).value;

					return new DoubleValue(lv - rv * (long) Math.floor(lv / rv));
				}
			}
				break;
			case MOREEQUAL:
			{
				if (left instanceof DoubleValue)
				{
					return new BooleanValue(((DoubleValue) left).value > ((DoubleValue) right).value);
				}
			}
				break;
			case MORETHAN:
			{
				if (left instanceof DoubleValue)
				{
					return new BooleanValue(((DoubleValue) left).value >= ((DoubleValue) right).value);
				}
			}
				break;
			case MULTIPLY:
			{
				if (left instanceof DoubleValue)
				{
					return new DoubleValue(((DoubleValue) left).value
							* ((DoubleValue) right).value);
				}
			}
				break;
			case OR:
			{
				if (left instanceof BooleanValue)
				{
					return new BooleanValue(((BooleanValue) left).value
							|| ((BooleanValue) right).value);
				}
			}
				break;
			case PLUS:
			{
				if (left instanceof DoubleValue)
				{
					return new DoubleValue(((DoubleValue) left).value
							+ ((DoubleValue) right).value);
				}
			}
				break;

		}
		return new BooleanValue(false);
	}

	@Override
	public Value caseAUnaryExp(AUnaryExp node)
	{
		Value val = node.getExp().apply(this);
		switch (node.getOperator().kindPUnop())
		{
			case ABS:
				if (val instanceof DoubleValue)
				{
					return new DoubleValue(Math.abs(((DoubleValue) val).value));
				}
				break;
			case ADD:
				// TODO
				break;
			case CEIL:
				if (val instanceof DoubleValue)
				{
					return new DoubleValue(Math.ceil(((DoubleValue) val).value));
				}
				break;
			case FLOOR:
				if (val instanceof DoubleValue)
				{
					return new DoubleValue(Math.floor(((DoubleValue) val).value));
				}
				break;
			case MINUS:
				if (val instanceof DoubleValue)
				{
					return new DoubleValue(0 - ((DoubleValue) val).value);
				}
				break;

		}
		return new BooleanValue(false);
	}

}
