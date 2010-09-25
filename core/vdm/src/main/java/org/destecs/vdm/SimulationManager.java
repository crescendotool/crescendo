package org.destecs.vdm;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destecs.vdm.links.Links;
import org.destecs.vdm.links.StringPair;
import org.destecs.vdmj.VDMCO;
import org.destecs.vdmj.scheduler.CoSimResourceScheduler;
import org.destecs.vdmj.scheduler.EventThread;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.config.Properties;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.SystemClock;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NumericValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class SimulationManager
{
	private final static String specFileExtension = "vdmrt";
	private final static Links links = new Links(); 
	private VDMCO controller = null;
	private Long nextTimeStep = new Long(0);
	private Long nextSchedulableActionTime = new Long(0);
	private boolean interpreterRunning = false;
	Thread runner;
	private Context mainContext = null;
	private final static String script = "new World().run()";

	private CoSimResourceScheduler scheduler = null;

	private CoSimStatusEnum status = CoSimStatusEnum.NOT_INITIALIZED;
	/**
	 * A handle to the unique Singleton instance.
	 */
	static private SimulationManager _instance = null;

	/**
	 * @return The unique instance of this class.
	 */
	static public SimulationManager getInstance()
	{
		if (null == _instance)
		{
			_instance = new SimulationManager();
		}
		return _instance;
	}

	

	public void register(CoSimResourceScheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	public synchronized Long waitForStep(long minstep)
	{
		this.nextSchedulableActionTime = SystemClock.getWallTime() + minstep;
		interpreterRunning = false;
		this.notify();

		System.out.println("Wait at clock:   " + SystemClock.getWallTime()
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
		System.out.println("Resume at clock: " + nextTimeStep);
		return nextTimeStep;
	}

	/*
	 * Shared design variables minLevel maxLevel Variables level :IN valveState :OUT Events HIGH_LEVEL LOW_LEVEL
	 */

	public List<String> getSharedDesignParameters()
	{
		return links.getSharedDesignParameters();
	}

	public List<String> getInputVariables()
	{
		return links.getInputs();
	}

	public List<String> getOutputVariables()
	{
		return links.getOutputs();
	}

	public synchronized StepStruct step(Double outputTime,
			List<StepinputsStructParam> inputs, List<String> events)
	{
		for (StepinputsStructParam p : inputs)
		{
			setValue(p.name,p.value);
		}

		nextTimeStep = outputTime.longValue();
		System.out.println("Next Step clock: " + nextTimeStep);

		if (events.size() > 0)
		{
			for (String event : events)
			{
				evalEvent(event);
			}
		}

		try
		{
			interpreterRunning = true;
			notify();// Wake up Scheduler

			while (interpreterRunning)
			{
				this.wait();// Wait for scheduler to notify
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		this.status = CoSimStatusEnum.STEP_TAKEN;

		List<StepStructoutputsStruct> outputs = new Vector<StepStructoutputsStruct>();

		for (String key : links.getOutputs())
		{
			try
			{
				outputs.add(new StepStructoutputsStruct(key, getOutput(key)));
			} catch (ValueException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		StepStruct result = new StepStruct(StepResultEnum.SUCCESS.value, new Double(nextSchedulableActionTime), new Vector<String>(), outputs);

		return result;
	}

	private void setValue(String name,Double value)
	{
		Value val = getValue(name);
		if (val != null)
		{
			// System.out.println("Setting " + p.name + ": " + val + " to "
			// + p.value);
			if (val.deref() instanceof NumericValue)
			{
				((NumericValue) val.deref()).value = value;
			}
		} else
		{
			System.err.println("Setting val error, not found: " + name);
		}
	}

	private void evalEvent(String event)
	{
		final String classDefinitionName = "testEventHandlers";

		if (mainContext != null)
		{
			for (LexNameToken key : mainContext.getGlobal().keySet())
			{
				if (key.name.equals(classDefinitionName))
				{
					ObjectValue s = (ObjectValue) mainContext.getGlobal().get(key).deref();
					for (LexNameToken memberKey : s.members.keySet())
					{
						if (memberKey.name.equals(event))
						{

							OperationValue eventOp = (OperationValue) s.members.get(memberKey);
							try
							{
								EventThread eThread = new EventThread(Thread.currentThread());
								BasicSchedulableThread.add(eThread);
								eventOp.eval(new LexLocation(new File("co-sim"), "CO-SIM", 0, 0, 0, 0), new ValueList(), mainContext);
								BasicSchedulableThread.remove(eThread);
							} catch (ValueException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println(s);
						}
					}

				}

			}
		}

	}

	private Double getOutput(String name) throws ValueException
	{
		NameValuePairList list = SystemDefinition.getSystemMembers();
		if(list!= null && links.getLinks().containsKey(name))
		{
			StringPair var =links.getBoundVariable(name);
			for (NameValuePair p : list)
			{
				if(var.instanceName.equals(p.name.getName()) && p.value.deref() instanceof ObjectValue )
				{
					ObjectValue po = (ObjectValue) p.value.deref();
					for (NameValuePair mem : po.members.asList())
					{
						if(mem.name.getName().equals(var.variableName))
						{
							return mem.value.realValue(null);
						}
					}
				}
			}
		}
		return null;
	}

	private Value getValue(String name)
	{
		NameValuePairList list = SystemDefinition.getSystemMembers();
		if(list!= null && links.getLinks().containsKey(name))
		{
			StringPair var =links.getBoundVariable(name);
			for (NameValuePair p : list)
			{
				if(var.instanceName.equals(p.name.getName()) && p.value.deref() instanceof ObjectValue )
				{
					ObjectValue po = (ObjectValue) p.value.deref();
					for (NameValuePair mem : po.members.asList())
					{
						if(mem.name.getName().equals(var.variableName))
						{
							return mem.value;
						}
					}
				}
			}
		}
		return null;
	}

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

	public Boolean load(File path)
	{
		try
		{
			Properties.init();
			Settings.dialect = Dialect.VDM_RT;
			Settings.usingCmdLine = true;
			Settings.release = Release.VDM_10;
			controller = new VDMCO();

			final List<File> files = new Vector<File>();

			files.addAll(getSpecFiles(path));

			controller.setLogFile(new File(path, "logFile.logrt"));
			controller.setScript(script);

			ExitStatus status = controller.parse(files);

			if (status == ExitStatus.EXIT_OK)
			{
				status = controller.typeCheck();

				if (status == ExitStatus.EXIT_OK)
				{
					this.status = CoSimStatusEnum.LOADED;
					return true;
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return false;
	}

	public Boolean initialize(/*
							 * List<InitializeSharedParameterStructParam> sharedParameter,
							 * List<InitializefaultsStructParam> faults
							 */)
	{

		// setSharedDesignParameters(sharedParameter);
		//
		// setFaults(faults);

		final List<File> files = new Vector<File>();
		try
		{
			files.addAll(controller.getInterpreter().getSourceFiles());
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		if (controller.interpret(files, null) == ExitStatus.EXIT_OK)
		{

			runner = new Thread(new Runnable()
			{
				public void run()
				{
					if (controller.asyncStartInterpret(files) == ExitStatus.EXIT_OK)
					{
						System.out.println("INIT OK");
					} else
					{
						System.err.println("INIT ERROR");
					}

				}
			});
			runner.setDaemon(true);
			runner.start();
			// TODO need to catch the init errors of the interpreter here and return false if any
			
			for (String l : links.getOutputs())
			{
				Value v= getValue(l);
				try
				{
					if(v.deref().isNumeric() && v.deref().realValue(null)==0.1)
					{
						setValue(l, new Double(0));
					}
				} catch (ValueException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			this.status = CoSimStatusEnum.INITIALIZED;
			return true;
		} else
		{
			this.status = CoSimStatusEnum.NOT_INITIALIZED;
			return false;
		}

	}

	// private void setFaults(List<InitializefaultsStructParam> faults)
	// {
	// // TODO set faults this might need to be set from the load instead
	// }
	//
	// private void setSharedDesignParameters(
	// List<InitializeSharedParameterStructParam> sharedParameter)
	// {
	// try
	// {
	// for (ClassDefinition cd : controller.getInterpreter().getClasses())
	// {
	// if (cd.getName().equalsIgnoreCase(interfaceClassName))
	// {
	// for (Definition d : cd.definitions)
	// {
	// if (d instanceof ValueDefinition)
	// {
	// // This should work but gives null list.add(d.getName());
	// String name = ((ValueDefinition) d).pattern.toString();
	// for (InitializeSharedParameterStructParam designParameter : sharedParameter)
	// {
	// if (name.equals(designParameter.name))
	// {
	// // ((RealLiteralExpression)((ValueDefinition) d).exp).value.value
	// // =designParameter.value;
	// // TODO cannot set value because it is final, remember that INV checks must be
	// // performed after / during the value change
	// }
	// }
	//
	// }
	// }
	// }
	// }
	// } catch (Exception e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	private static List<File> getSpecFiles(File path)
	{
		List<File> files = new Vector<File>();

		if (path.isFile()
				&& path.getName().toLowerCase().endsWith(specFileExtension))
		{
			files.add(path);
		} else if (path.isDirectory())
		{
			for (File file : path.listFiles())
			{
				files.addAll(getSpecFiles(file));
			}

		}
		return files;
	}

	public void setMainContext(Context ctxt)
	{
		this.mainContext = ctxt;
	}

	public Integer getStatus()
	{
		return this.status.value;
	}

	public synchronized Boolean stopSimulation()
	{
		try
		{
			scheduler.stop();
			notify();
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
