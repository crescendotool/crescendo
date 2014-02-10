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
package org.destecs.vdm.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.destecs.protocol.exceptions.RemoteSimulationException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.InterpreterAssistantFactory;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntime;
import org.overture.interpreter.runtime.state.SClassDefinitionRuntime;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.ReferenceValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.TransactionValue;
import org.overture.interpreter.values.Value;

public class VDMClassHelper
{

	private static IInterpreterAssistantFactory af= new InterpreterAssistantFactory();

	public static SClassDefinition findClassByName(String className,
			ClassListInterpreter classes)
	{
		for (SClassDefinition classDefinition : classes)
		{
			if (classDefinition.getName().getName().equals(className))
				return classDefinition;
		}

		return null;

	}

	public static ValueInfo digForVariable(List<String> varName,
			NameValuePairList list) throws RemoteSimulationException,
			ValueException
	{
		Value value = null;
		if (list.size() > 0)
		{
			String namePart = varName.get(0);
			for (NameValuePair p : list)
			{
				if (namePart.equals(p.name.getName()))
				{
					value = p.value.deref();

					if (canResultBeExpanded(value))
					{
						List<ValueInfo> newArgs = getNamePairListFromResult(value);

						ValueInfo result = digForVariable(getNewName(varName), newArgs);
						return result;
					} else
					{
						return createValue(p.name, null, value, CPUValue.vCPU);
					}
				}
			}
		}

		throw new RemoteSimulationException("Value: " + varName + " not found");
	}

	public static ValueInfo digForVariable(List<String> varName,
			List<ValueInfo> list) throws RemoteSimulationException,
			ValueException
	{

		Value value = null;
		if (list.size() >= 1)
		{
			String namePart = varName.get(0);
			for (ValueInfo p : list)
			{
				if (namePart.equals(p.name.getName()))
				{
					value = p.value.deref();

					if (canResultBeExpanded(value))
					{
						List<ValueInfo> newArgs = getNamePairListFromResult(value);

						ValueInfo result = digForVariable(getNewName(varName), newArgs);
						return result;
					} else
					{
						return p;

					}
				}
			}
		}

		throw new RemoteSimulationException("Value: " + varName + " not found");
	}

	private static boolean canResultBeExpanded(Value result)
	{
		if (result instanceof ObjectValue || result instanceof ReferenceValue)
		{
			return true;
		} else
			return false;
	}

	private static List<ValueInfo> getNamePairListFromResult(Value value)
	{
		if (value instanceof ObjectValue)
		{
			ObjectValue objVal = ((ObjectValue) value);
			List<ValueInfo> values = new Vector<ValueInfo>();
			Set<String> existingFields = new HashSet<String>();

			for (NameValuePair child : objVal.members.asList())
			{
				existingFields.add(child.name.getName());
				values.add(createValue(child.name, objVal.type.getClassdef(), child.value, objVal.getCPU()));
			}

			// FIXME: Fix for BUG: 54536, problematic static initialization with more then 30 files
			// https://chessforge.chess-it.com/gf/project/destecs/tracker/?action=TrackerItemEdit&tracker_item_id=54536&start=0
			try
			{
				
				SClassDefinitionRuntime runtimeState = VdmRuntime.getNodeState(af,objVal.type.getClassdef());
				
				
				
//				for (Field fi : objVal.type.getClassdef().getClass().getDeclaredFields())
//				{
//					if (fi.getName().equals("privateStaticValues"))
//					{
//						fi.setAccessible(true);
//						NameValuePairMap privateStaticValues = (NameValuePairMap) fi.get(objVal.type.getClassdef());

						for (NameValuePair child : runtimeState.privateStaticValues.asList())
						{
							if (!existingFields.contains(child.name.getName()))
							{
								values.add(createValue(child.name, objVal.type.getClassdef(), child.value, objVal.getCPU()));
							}
						}
//					}
//					if (fi.getName().equals("publicStaticValues"))
//					{
//						fi.setAccessible(true);
//						NameValuePairMap privateStaticValues = (NameValuePairMap) fi.get(objVal.type.getClassdef());

						for (NameValuePair child : runtimeState.publicStaticValues.asList())
						{
							if (!existingFields.contains(child.name.getName()))
							{
								values.add(createValue(child.name, objVal.type.getClassdef(), child.value, objVal.getCPU()));
							}
						}
//					}
//				}

			} catch (Exception e)
			{
				//Only show in console, should never happen.
				e.printStackTrace();
			}
			return values;
		} else
			return null;
	}

	public static ValueInfo createValue(ILexNameToken name,
			SClassDefinition classDef, Value value, CPUValue cpu)
	{
		Value val = value.deref();
		if (val instanceof SeqValue)
		{
			return new SeqValueInfo(name, classDef, (SeqValue) val, cpu);
		}
		return new ValueInfo(name, classDef, value, cpu);
	}

	private static List<String> getNewName(List<String> varName)
	{
		List<String> result = new ArrayList<String>();

		if (varName.size() > 1)
		{
			for (int i = 1; i < varName.size(); i++)
			{
				result.add(varName.get(i));
			}
			return result;
		} else
		{
			return null;
		}
	}

	public static List<Double> getDoubleListFromValue(Value value)
			throws ValueException
	{
		List<Double> result = new Vector<Double>();

		if (value instanceof TransactionValue)
		{
			value = ((TransactionValue) value).deref();
		}

		if (value instanceof SeqValue)
		{
			SeqValue seqValue = (SeqValue) value;
			result = getDoubleListFromSeq(seqValue);
			return result;
		}

		if (value.isNumeric())
		{
			result.add(value.realValue(null));
			return result;
		} else if (value instanceof BooleanValue)
		{
			result.add(((BooleanValue) value).boolValue(null) ? 1.0 : 0.0);
			return result;
		}
		return null;
	}

	private static List<Double> getDoubleListFromSeq(SeqValue seqValue)
			throws ValueException
	{
		List<Double> result = new Vector<Double>();

		for (Value insideValue : seqValue.values)
		{

			result.addAll(getDoubleListFromValue(insideValue.deref()));
		}

		return result;
	}

	private static String dotName(List<String> names)
	{
		String name = "";
		for (Iterator<String> iterator = names.iterator(); iterator.hasNext();)
		{
			name += (String) iterator.next();
			if (iterator.hasNext())
			{
				name += ".";
			}
		}
		return name;
	}

	public static PDefinition findDefinitionInClass(ClassListInterpreter classList,
			List<String> variableName) throws RemoteSimulationException
	{

		if (variableName.size() <= 1)
		{
			return null;
		}

		String className = variableName.get(0);
		String varName = variableName.get(1);

		PDefinition s = classList.findName(new LexNameToken(className, varName, null), NameScope.NAMESANDSTATE);

		if (s == null)
		{
			throw new RemoteSimulationException("Unable to locate: \""
					+ dotName(variableName)
					+ "\" failed with: \""
					+ varName
					+ "\". Is this accessiable through the system while inilializing");
		}

		List<String> restOfQuantifier = variableName.subList(2, variableName.size());

		if (restOfQuantifier.size() == 0)
		{
			return s;
		} else
		{
			for (int i = 0; i < restOfQuantifier.size(); i++)
			{
				if(s.getType() instanceof AClassType)
				{
				className =((AClassType) s.getType()).getName().getName();

				s = classList.findName(new LexNameToken(className, restOfQuantifier.get(i), null), NameScope.NAMESANDSTATE);
				}
				// if(s== null)
				// {
				// throw new
				// RemoteSimulationException("Unable to locate: \""+dotName(variableName)+"\" failed with: \""+varName+"\". Is this accessiable through the system while inilializing");
				// }
			}
		}

		return s;

	}

	public static List<Integer> getValueDimensions(Value value)
	{
		List<Integer> result = new Vector<Integer>();

		if (value instanceof SeqValue)
		{
			SeqValue seqValue = (SeqValue) value;
			result.add(seqValue.values.size());
			if (seqValue.values.size() > 0)
			{
				if (seqValue.values.get(0) instanceof SeqValue)
				{
					result.addAll(getSequenceInnerDimensions(seqValue));
				}
			}
		} else
		{
			result.add(1);
		}

		return result;
	}

	private static List<Integer> getSequenceInnerDimensions(SeqValue seqValue)
	{
		List<Integer> result = new Vector<Integer>();
		if (seqValue.values.size() > 0)
		{
			result.addAll(getValueDimensions(seqValue.values.get(0)));
		}

		return result;
	}
}
