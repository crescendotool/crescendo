package org.destecs.vdm;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.destecs.vdm.links.Links;
import org.destecs.vdm.links.StringPair;
import org.destecs.vdmj.VDMCO;
import org.destecs.vdmj.scheduler.CoSimResourceScheduler;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.scheduler.SystemClock;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NumericValue;
import org.overturetool.vdmj.values.ObjectValue;
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
		NumericValue, Boolean, String, Unknown
	}
	
	private static final boolean DEBUG = false;
	protected static Links links = new Links();
	protected VDMCO controller = null;
	protected CoSimResourceScheduler scheduler = null;
	protected boolean interpreterRunning = false;
	
	protected Long nextTimeStep = new Long(0);
	protected Long nextSchedulableActionTime = new Long(0);
	
	protected final LexLocation coSimLocation = new LexLocation(new File("SimulationInterface"), "SimulationInterface", 0, 0, 0, 0);
	protected final Context coSimCtxt = new Context(coSimLocation, "SimulationInterface", null);
	
	
	public void register(CoSimResourceScheduler scheduler)
	{
		this.scheduler = scheduler;
	}
		

	
	
	
	protected boolean setValue(String name, CoSimType inputType, String inputValue) throws SimulationException
	{
		Value val = getValue(name);
		if (val != null)
		{
						Value newval = null;

			try
			{
				switch (inputType)
				{
					case Boolean:
						newval = new BooleanValue(Boolean.parseBoolean(inputValue));
						break;
					case NumericValue:
						newval = NumericValue.valueOf(Double.parseDouble(inputValue), coSimCtxt);
						break;
					case String:
					case Unknown:
						System.err.println("Unknown value type to set");
						return false;
				}

//				val.set(location, newval.convertTo(estimateType(val), ctxt), ctxt);
				val.set(coSimLocation, newval, coSimCtxt);
			} catch (ValueException e)
			{
				debugErr(e);
				throw new SimulationException("Faild to servalue from: "+name,e);
			}

		}
		
		return true;
	}

//	private static Type estimateType(Value val)
//	{
//		if(val instanceof UpdatableValue)
//		{
//			return estimateType(val.deref());
//		}
//		if (val instanceof BooleanValue)
//		{
//			return new BooleanType(null);
//		} else if (val instanceof NaturalOneValue)
//		{
//			return new NaturalOneType(null);
//		} else if (val instanceof NaturalOneValue)
//		{
//			return new NaturalOneType(null);
//		} else if (val instanceof NaturalValue)
//		{
//			return new NaturalType(null);
//		} else if (val instanceof IntegerValue)
//		{
//			return new IntegerType(null);
//		} else if (val instanceof RationalValue)
//		{
//			return new RationalType(null);
//		} else if (val instanceof RealValue)
//		{
//			return new RealType(null);
//		}
//		System.err.println("Unknown type: " + val);
//		return null;
//	}
	
	protected Value getValue(String name)
	{
		NameValuePairList list = SystemDefinition.getSystemMembers();
		if (list != null && links.getLinks().containsKey(name))
		{
			StringPair var = links.getBoundVariable(name);
			for (NameValuePair p : list)
			{
				if (var.instanceName.equals(p.name.getName())
						&& p.value.deref() instanceof ObjectValue)
				{
					ObjectValue po = (ObjectValue) p.value.deref();
					for (NameValuePair mem : po.members.asList())
					{
						if (mem.name.getName().equals(var.variableName))
						{
							return mem.value;
						}
					}
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

		debug("Wait at clock:   " + SystemClock.getWallTime()
				+ " - for " + minstep + " steps");
		try
		{
			this.wait();

		} catch (InterruptedException e)
		{
			// Ok just check again
		}

		// Long nextStep = null;
		// synchronized (nextTimeStep)
		// {
		// nextStep = nextTimeStep;
		// }
		debug("Resume at clock: " + nextTimeStep);
		return nextTimeStep;
	}
	
	
	protected static List<File> getFiles(File path,String extension)
	{
		List<File> files = new Vector<File>();

		if (path.isFile()
				&& path.getName().toLowerCase().endsWith(extension))
		{
			files.add(path);
		} else if (path.isDirectory())
		{
			for (File file : path.listFiles())
			{
				files.addAll(getFiles(file,extension));
			}

		}
		return files;
	}

	
	protected static void debug(String message)
	{
		if(DEBUG)
		{
			System.out.println(message);
		}
	}
	protected static void debugErr(Object message)
	{
		if(DEBUG)
		{
			if(message instanceof Exception)
			{
				((Exception)message).printStackTrace();
			}else
			{
			System.err.println(message);
			}
		}
	}
}
