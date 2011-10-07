package org.destecs.vdm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.destecs.core.vdmlink.LinkInfo;
import org.destecs.core.vdmlink.Links;
import org.destecs.core.vdmlink.StringPair;
import org.destecs.protocol.exceptions.RemoteSimulationException;
import org.destecs.vdmj.VDMCO;
import org.destecs.vdmj.scheduler.CoSimResourceScheduler;
import org.destecs.vdmj.scheduler.SharedVariableUpdateThread;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.ISchedulableThread;
import org.overturetool.vdmj.scheduler.SystemClock;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NumericValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.ReferenceValue;
import org.overturetool.vdmj.values.TransactionValue;
import org.overturetool.vdmj.values.Value;

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
		EVENT_OCCURED(2);

		public int value;

		StepResultEnum(int v)
		{
			value = v;
		}
	}

	enum CoSimStatusEnum
	{
		NOT_INITIALIZED(0), LOADED(4), INITIALIZED(1), STEP_TAKEN(2), FINISHED(
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

	protected final LexLocation coSimLocation = new LexLocation(new File("SimulationInterface"), "SimulationInterface", 0, 0, 0, 0);
	protected final Context coSimCtxt = new Context(coSimLocation, "SimulationInterface", null);

	ArrayBlockingQueue<ValueUpdateRequest> updateValueQueueRequest = new ArrayBlockingQueue<ValueUpdateRequest>(1);

	public static class ValueUpdateRequest
	{
		public final Value value;
		public final Value newValue;

		public ValueUpdateRequest(Value value, Value newValue)
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
						request.value.set(coSimLocation, request.newValue, coSimCtxt);
						if (request.value instanceof TransactionValue)
						{
							TransactionValue.commitOne(BasicSchedulableThread.getThread(Thread.currentThread()).getId());
						}
					} catch (InterruptedException e)
					{
						// ignore
					} catch (ValueException e)
					{
						debugErr(e);
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

	protected boolean setValue(String name, CoSimType inputType,
			String inputValue) throws RemoteSimulationException
	{
		Value val = null;
		try {
			val = getValueNew(name);
		} catch (Exception e) {
			// TODO: handle exception
		}
		 
		if (val != null)
		{
			Value newval = null;

			if (inputType == CoSimType.Auto)
			{
				inputType = CoSimType.NumericValue;
				if (val.deref() instanceof BooleanValue)
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
						if (inputValue.contains("true")
								|| inputValue.contains("false"))
						{
							newval = new BooleanValue(Boolean.parseBoolean(inputValue));
							break;
						}

						try
						{
							newval = new BooleanValue(Double.valueOf(inputValue) > 0 ? true
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
						newval = NumericValue.valueOf(Double.parseDouble(inputValue), coSimCtxt);
						break;
					case String:
					case Unknown:
						System.err.println("Unknown value type to set");
						return false;
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

	
	

	protected Value getValueNew(String name) throws RemoteSimulationException
	{
		try {
			NameValuePairList list = SystemDefinition.getSystemMembers();
			if (list != null && links.getLinks().containsKey(name)){
				List<String> varName = links.getQualifiedName(name);
				
				Value output = digForVariable(varName.subList(1, varName.size()),list);
				return output;
			}
		} catch (ValueException e) {
			throw new RemoteSimulationException("Value: " + name + " not found");
		}
		return null;	
	}
	
	private Value digForVariable(List<String> varName, NameValuePairList list) throws RemoteSimulationException, ValueException {

		Value value = null;
		
		
		
		if(list.size() >= 1)
		{
			String namePart = varName.get(0);
			for (NameValuePair p : list)
			{
				if (namePart.equals(p.name.getName()))
				{
					value = p.value.deref();
					
					if(canResultBeExpanded(value))
					{						
						NameValuePairList newArgs = getNamePairListFromResult(value);
						
						Value result = digForVariable(getNewName(varName), newArgs);
						return result;
					}
					else
					{
						return p.value;
						
					}
				}
			}
		}	
		
		
		if(value == null)
		{
			throw new RemoteSimulationException("Value: " + varName + " not found");
		}
		return null;
		
		
	}
	
	private List<String> getNewName(List<String> varName) {
		List<String> result = new ArrayList<String>();
		
		if(varName.size() > 1)
		{
			for(int i=1; i< varName.size(); i++)
			{
				result.add(varName.get(i));
			}
			return result;
		}
		else
		{
			return null;	
		}
		
	}
	
	private boolean canResultBeExpanded(Value result) {
		if(result instanceof ObjectValue || result instanceof ReferenceValue)
		{
			return true;
		}
		else
			return false;
	}
	
	private NameValuePairList getNamePairListFromResult(Value value) {
		if(value instanceof ObjectValue)
		{
			return ((ObjectValue) value).members.asList();
		}
		else 
			return null;
	}
	
	protected Value getValue(String name)
	{
		
//		NameValuePairList list = SystemDefinition.getSystemMembers();
//		if (list != null && links.getLinks().containsKey(name))
//		{
//			StringPair var = links.getBoundVariable(name);
//			for (NameValuePair p : list)
//			{
//				if (var.instanceName.equals(p.name.getName())
//						&& p.value.deref() instanceof ObjectValue)
//				{
//					ObjectValue po = (ObjectValue) p.value.deref();
//					for (NameValuePair mem : po.members.asList())
//					{
//						if (mem.name.getName().equals(var.variableName))
//						{
//							return mem.value;
//						}
//					}
//				}
//			}
//		}
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
			list = SystemDefinition.getSystemMembers();
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
