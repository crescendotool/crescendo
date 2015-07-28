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
package org.destecs.vdm;

import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;

import org.destecs.core.vdmlink.Links;
import org.destecs.protocol.exceptions.RemoteSimulationException;
import org.destecs.vdm.utility.SeqValueInfo;
import org.destecs.vdm.utility.VDMClassHelper;
import org.destecs.vdm.utility.ValueInfo;
import org.destecs.vdmj.VDMCO;
import org.destecs.vdmj.scheduler.*;
import org.overture.ast.lex.LexLocation;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.runtime.*;
import org.overture.interpreter.runtime.state.ASystemClassDefinitionRuntime;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.ISchedulableThread;
import org.overture.interpreter.scheduler.SystemClock;
import org.overture.interpreter.values.*;

public abstract class BasicSimulationManager
{
	enum StepResultEnum
	{
		/**
		 * 0=the step action succeeded, and additional steps can be taken,
		 */
		SUCCESS(0),
		/**
		 * 1=the step action succeeeded, and the simulation is finished,
		 */
		SUCCESS_AND_FINISHED(1),
		/**
		 * 2=the step action succeeded, but an event occurred. if failed then the function will return a fault response
		 */
		EVENT_OCCURED(2),
		/**
		 * 3=the step was stopped by the user
		 */
		SUCCESS_STOPPED_BY_USER(3);

		public int value;

		StepResultEnum(int v)
		{
			value = v;
		}
	}

	enum CoSimStatusEnum
	{
		NOT_INITIALIZED(0), LOADED(4), INITIALIZED(1), STARTED(5),STEP_TAKEN(2), FINISHED(
				3);

		public int value;

		CoSimStatusEnum(int v)
		{
			value = v;
		}
	}

	enum CoSimType
	{
		NumericValue, Boolean, String, Unknown, Auto
	}

	private static final boolean DEBUG = false;
	protected Links links = null;
	protected VDMCO controller = null;
	protected CoSimResourceScheduler scheduler = null;
	protected boolean interpreterRunning = false;

	protected Long nextTimeStep = Long.valueOf(0);
	protected Long nextSchedulableActionTime = Long.valueOf(0);

	protected final LexLocation coSimLocation = new LexLocation(new File("SimulationInterface"), "SimulationInterface", 0, 0, 0, 0,0,0);

	ArrayBlockingQueue<ValueUpdateRequest> updateValueQueueRequest = new ArrayBlockingQueue<ValueUpdateRequest>(1);

	protected ClassInterpreter interpreter;
	
	protected RemoteSimulationException runtimeException = null;

	public static class ValueUpdateRequest
	{
		public final ValueInfo value;
		public final Value newValue;

		public ValueUpdateRequest(ValueInfo value, Value newValue)
		{
			this.value = value;
			this.newValue = newValue;
		}
	}

	public BasicSimulationManager()
	{
		Thread t = new Thread(new Runnable()
		{

			public void run()
			{

				while (true)
				{
					try
					{
						ValueUpdateRequest request = updateValueQueueRequest.take();
						try
						{
							Context coSimCtxt = new ClassContext(interpreter.getAssistantFactory(),coSimLocation, "SimulationInterface", interpreter.initialContext, request.value.classDef);
							coSimCtxt.setThreadState(null, request.value.cpu);

							request.value.value.set(coSimLocation, request.newValue, coSimCtxt);
							if (request.value.value instanceof TransactionValue)
							{
								TransactionValue.commitOne(BasicSchedulableThread.getThread(Thread.currentThread()).getId());
							}
						} catch (ValueException e)
						{
							debugErr(e);
						} catch (ContextException e)
						{
							String message = "Error in simulation: Cannot set shared instance variable \""
								+ request.value.name.getModule()
								+ "."
								+ request.value.name.getName()
								+ "\""
								+ "\n\tReasong is: " + e.getMessage();
							Console.err.println(message);
							
							runtimeException = new RemoteSimulationException(message);
						} catch (Exception e)
						{
							debugErr(e);
							e.printStackTrace();
						}
					} catch (InterruptedException e)
					{
						// ignore
					}
				}
			}
		}, "SharedVariableUpdateThread");
		t.setDaemon(true);
		ISchedulableThread thread = new SharedVariableUpdateThread(t);
		BasicSchedulableThread.add(thread);
		t.start();
	}

	public synchronized void register(CoSimResourceScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	protected boolean setScalarValue(ValueInfo val, CoSimType inputType,
			String p, String name) throws RemoteSimulationException
	{
		if (val != null)
		{
			Value newval = null;

			if (inputType == CoSimType.Auto)
			{
				inputType = CoSimType.NumericValue;
				if (val.value.deref() instanceof BooleanValue)
				{
					inputType = CoSimType.Boolean;
				}
			}

			try
			{
				switch (inputType)
				{
					case Boolean:
					{
						if (p.contains("true") || p.contains("false"))
						{
							newval = new BooleanValue(Boolean.parseBoolean(p));
							break;
						}

						try
						{
							newval = new BooleanValue(Double.valueOf(p) > 0 ? true
									: false);
						} catch (NumberFormatException e)
						{
							debugErr(e);
							throw new RemoteSimulationException("Faild to setvalue from: "
									+ name, e);
						}

					}
						break;
					case NumericValue:
						newval = NumericValue.valueOf(Double.parseDouble(p), null);
						break;
					case String:
					case Unknown:
						System.err.println("Unknown value type to set");
						return false;
					case Auto:
						break;
					default:
						break;
				}

				if (newval == null)
				{
					throw new RemoteSimulationException("Error in setValue with variable: "
							+ name
							+ " properly a type mismatch. Requested input type was: "
							+ inputType);
				}

				updateValueQueueRequest.put(new ValueUpdateRequest(val, newval));
			} catch (ValueException e)
			{
				debugErr(e);
				throw new RemoteSimulationException("Faild to setvalue from: "
						+ name, e);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
				throw new RemoteSimulationException("Internal error in setValue with name: "
						+ name, e);
			}

		} else
		{
			throw new RemoteSimulationException("Faild to find variable in setValue with name: "
					+ name);
		}
		return true;

	}

	protected boolean setValue(String name, CoSimType inputType,
			ValueContents valueContents) throws RemoteSimulationException
	{

		ValueInfo val = getValue(name);

		if (valueContents.size.size() == 0
				|| (valueContents.size.size() == 1 && valueContents.size.get(0) == 1))
		{
			return setScalarValue(val, inputType, valueContents.value.get(0).toString(), name);
		} else
		{
			if (val instanceof SeqValueInfo)// (val.value.deref() instanceof SeqValue)
			{
				return setNonScalar((SeqValueInfo) val, valueContents, name);// ((SeqValue) val.value.deref(),
																				// valueContents, name);
			} else
			{
				return false;
			}
		}

		// return true;
	}

	private boolean setNonScalar(SeqValueInfo val, ValueContents valueContents,
			String name) throws RemoteSimulationException
	{

		List<Integer> size = valueContents.size;
		List<Double> values = valueContents.value;

		List<List<Integer>> indexes = generateIndexes(size.subList(0, size.size() - 1));

		if (indexes.size() == 0) // it is a flat array
		{
			int sizeOfSeq = size.get(0);
			setSeqValue(val, values, sizeOfSeq, name);
		} else
		{
			for (List<Integer> index : indexes)
			{
				SeqValueInfo seqVal = findNestedSeq(val, index);
				int sizeOfSeq = size.get(size.size() - 1);
				setSeqValue(seqVal, values, sizeOfSeq, name);
				values = values.subList(sizeOfSeq, values.size());
			}
		}

		return true;
	}

	private List<List<Integer>> generateIndexes(List<Integer> shape)
	{
		List<List<Integer>> tempResult = new Vector<List<Integer>>();
		List<List<Integer>> result = new Vector<List<Integer>>();

		for (int i = 0; i < shape.size(); i++)
		{
			List<Integer> temp = new Vector<Integer>();
			for (Integer j = 0; j < shape.get(i); j++)
			{
				temp.add(j);
			}
			tempResult.add(temp);
		}

		if (tempResult.size() > 1)
		{
			result.add(tempResult.get(0));
			tempResult = tempResult.subList(1, tempResult.size());
		} else
		{
			return flatten(tempResult);
		}

		for (int i = 0; i < tempResult.size(); i++)
		{
			appendList(result, tempResult.get(i));
		}

		return result;
	}

	private List<List<Integer>> flatten(List<List<Integer>> tempResult)
	{
		List<List<Integer>> result = new Vector<List<Integer>>();

		for (List<Integer> list : tempResult)
		{
			for (Integer integer : list)
			{
				List<Integer> temp = new Vector<Integer>();
				temp.add(integer);
				result.add(temp);
			}
		}
		return result;
	}

	private List<List<Integer>> appendList(List<List<Integer>> l1,
			List<Integer> l2)
	{
		for (int i = 0; i < l1.size(); i++)
		{
			for (Integer j : l2)
			{
				l1.get(i).add(j);
			}
		}
		return l1;
	}

	private SeqValueInfo findNestedSeq(SeqValueInfo val, List<Integer> indexes)
			throws RemoteSimulationException
	{

		if (indexes.size() == 0)
		{
			return val;
		}

		int index = indexes.get(0);

		if (index + 1 > val.value.values.size())
		{
			return null;
		} else
		{
			Value valToInspect = val.value.values.get(index).deref();

			if (valToInspect instanceof SeqValue)
			{
				SeqValueInfo valToInspectSeq = (SeqValueInfo) VDMClassHelper.createValue(val.name, val.classDef, (SeqValue) valToInspect, val.cpu);
				return findNestedSeq(valToInspectSeq, indexes.subList(1, indexes.size()));
			}
		}
		return null;

	}

	private void setSeqValue(SeqValueInfo val, List<Double> values,
			int sizeOfSeq, String name) throws RemoteSimulationException
	{

		if (val.value.values.size() > values.size())
		{
			throw new RemoteSimulationException("Values received are not enough to fill matrix "
					+ name);
		}

		for (int i = 0; i < val.value.values.size(); i++)
		{
			ValueInfo elementValue =  VDMClassHelper.createValue(val.name, val.classDef, val.value.values.get(i), val.cpu);
			setScalarValue(elementValue, CoSimType.Auto, values.get(i).toString(), name);
		}

	}

	protected ValueInfo getValue(String name) throws RemoteSimulationException
	{
		try
		{
			NameValuePairList list = ASystemClassDefinitionRuntime.getSystemMembers();
			if (list != null && links.getLinks().containsKey(name))
			{
				List<String> varName = links.getQualifiedName(name);

				ValueInfo output = VDMClassHelper.digForVariable(varName.subList(1, varName.size()), list);
				return output;
			}
		} catch (ValueException e)
		{
			throw new RemoteSimulationException("Value: " + name + " not found");
		}
		return null;
	}

	protected Value getRawValue(List<String> name, Value v)
	{
		if (name.isEmpty())
		{
			return v;
		}
		NameValuePairList list = null;
		if (v == null)
		{
			list = ASystemClassDefinitionRuntime.getSystemMembers();
		} else if (v.deref() instanceof ObjectValue)
		{
			list = ((ObjectValue) v.deref()).members.asList();
		}

		if (list != null)
		{
			for (NameValuePair p : list)
			{
				if (name.get(0).equals(p.name.getName()))
				{
					return getRawValue(name.subList(1, name.size()), p.value);
				}
			}
		}
		return null;
	}

	public synchronized Long waitForStep(long minstep)
	{
		this.nextSchedulableActionTime = SystemClock.getWallTime() + minstep;
		interpreterRunning = false;
		this.notify();

		debug("Wait at clock:   " + SystemClock.getWallTime() + " - for "
				+ minstep + " steps");
		try
		{
			this.wait();

		} catch (InterruptedException e)
		{
			// Ok just check again
		}

		debug("Resume at clock: " + nextTimeStep);
		return nextTimeStep;
	}

	protected static void debug(String message)
	{
		if (DEBUG)
		{
			System.out.println(message);
		}
	}

	protected static void debugErr(Object message)
	{
		if (DEBUG)
		{
			if (message instanceof Exception)
			{
				((Exception) message).printStackTrace();
			} else
			{
				System.err.println(message);
			}
		}
	}
}
