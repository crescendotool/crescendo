package org.destecs.vdm;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.core.parsers.VdmLinkParserWrapper;
import org.destecs.core.vdmlink.StringPair;
import org.destecs.protocol.exceptions.RemoteSimulationException;
import org.destecs.protocol.structs.QueryToolSettingsStructsettingsStruct;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;
import org.destecs.protocol.structs.StepinputsStructParam;

import org.destecs.vdmj.VDMCO;
import org.destecs.vdmj.scheduler.EventThread;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.config.Properties;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.expressions.RealLiteralExpression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.messages.rtlog.RTLogger;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class SimulationManager extends BasicSimulationManager
{
	
	
	// Thread runner;
	private Context mainContext = null;
	private final static String script = "new World().run()";

	private CoSimStatusEnum status = CoSimStatusEnum.NOT_INITIALIZED;
	/**
	 * A handle to the unique Singleton instance.
	 */
	static private volatile SimulationManager _instance = null;

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
			throws RemoteSimulationException
	{
		for (StepinputsStructParam p : inputs)
		{
			setValue(p.name, CoSimType.NumericValue, p.value.toString());
		}

		nextTimeStep = outputTime.longValue();
		debug("Next Step clock: " + nextTimeStep);

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
			debugErr(e);
			throw new RemoteSimulationException("Notification of scheduler faild", e);
		}
		debug("Next Step return at clock: " + nextTimeStep);

		this.status = CoSimStatusEnum.STEP_TAKEN;

		List<StepStructoutputsStruct> outputs = new Vector<StepStructoutputsStruct>();

		for (String key : links.getOutputs())
		{
			try
			{
				Double value = getOutput(key);
				if(value != null)
				{
					outputs.add(new StepStructoutputsStruct(key, value));
				}else
				{
					throw new RemoteSimulationException("Faild to get output parameter, output not bound for: "+key);
				}
			} catch (ValueException e)
			{
				debugErr(e);
				throw new RemoteSimulationException("Faild to get output parameter", e);
			}
		}

		StepStruct result = new StepStruct(StepResultEnum.SUCCESS.value, new Double(nextSchedulableActionTime), new Vector<String>(), outputs);

		return result;
	}

	private void evalEvent(String event) throws RemoteSimulationException
	{
		boolean evaluated = false;
		if (links.getEvents().contains(event))
		{
			Value val = getValue(event);
			if (val.deref() instanceof OperationValue)
			{
				OperationValue eventOp = (OperationValue) val;
				if (eventOp.paramPatterns.size() == 0)
				{
					try
					{
						EventThread eThread = new EventThread(Thread.currentThread());
						BasicSchedulableThread.add(eThread);
						eventOp.eval(coSimLocation, new ValueList(), mainContext);
						BasicSchedulableThread.remove(eThread);
						evaluated = true;
					} catch (ValueException e)
					{
						debugErr(e);
						throw new RemoteSimulationException("Faild to evaluate event: "
								+ event, e);
					}

				}
			}
		}
		if (!evaluated)
		{
			debugErr("Event: " + event + " not found");
			throw new RemoteSimulationException("Faild to find event: " + event);
		}
	}

	private Double getOutput(String name) throws ValueException
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
							return mem.value.realValue(null);
						}
					}
				}
			}
		}
		return null;
	}

	public Boolean load(List<File> specfiles, File linkFile,File outputDir) throws RemoteSimulationException
	{
		try
		{
			
			if (!linkFile.exists()|| linkFile.isDirectory())
			{
				throw new RemoteSimulationException("The VDM link file does not exist: "
						+ linkFile);
			}
			VdmLinkParserWrapper linksParser = new VdmLinkParserWrapper();
			links = linksParser.parse(linkFile);// Links.load(linkFile);

			if (links == null || linksParser.hasErrors())
			{
				throw new RemoteSimulationException("Faild to parse vdm links");
			}

			
			
			controller.setLogFile(new File(outputDir, "logFile.logrt"));
			controller.setScript(script);
			Settings.DGBPbaseDir = outputDir.getParentFile();

			VDMCO.outputDir = outputDir;
			ExitStatus status = controller.parse(specfiles);

			if (status == ExitStatus.EXIT_OK)
			{
				status = controller.typeCheck();

				if (status == ExitStatus.EXIT_OK)
				{
					this.status = CoSimStatusEnum.LOADED;
					return true;
				} else
				{
					final Writer result = new StringWriter();
					final PrintWriter printWriter = new PrintWriter(result);
					TypeChecker.printWarnings(printWriter);
					TypeChecker.printErrors(printWriter);
					throw new RemoteSimulationException("Type check error: "
							+ result.toString());
				}
			}

		} catch (Exception e)
		{
			debugErr(e);
			throw new RemoteSimulationException("Faild to load model from " + outputDir, e);

		}

		return false;
	}

	public Boolean initialize() throws RemoteSimulationException
	{
		Properties.init();
		Properties.parser_tabstop = 1;
		Properties.rt_duration_transactions = false;;

		Settings.dialect = Dialect.VDM_RT;
		Settings.usingCmdLine = false;
		Settings.usingDBGP = true;
		Settings.release = Release.VDM_10;
		controller = new VDMCO();
		return true;

	}

	public Boolean start() throws RemoteSimulationException
	{
		final List<File> files = getFiles();

		// init
		if (controller.interpret(files, null) == ExitStatus.EXIT_OK)
		{
			this.status = CoSimStatusEnum.INITIALIZED;
		} else
		{
			this.status = CoSimStatusEnum.NOT_INITIALIZED;
		}

		if (this.status == CoSimStatusEnum.INITIALIZED)
		{
			// start
			if (controller.asyncStartInterpret(files) == ExitStatus.EXIT_OK)
			{
				this.status = CoSimStatusEnum.INITIALIZED;
				return true;
			} else
			{
				this.status = CoSimStatusEnum.NOT_INITIALIZED;
				return false;
			}
		} else
		{					
			throw new RemoteSimulationException(//"Model must be "
					//+ CoSimStatusEnum.INITIALIZED
					//+ " before it can be started. " +
							"Status = " + this.status + ". Internal error: " + controller.exception.getMessage());
		}

	}


	private List<File> getFiles() throws RemoteSimulationException
	{
		final List<File> files = new Vector<File>();
		try
		{
			files.addAll(getInstance().controller.getInterpreter().getSourceFiles());
		} catch (Exception e)
		{
			debugErr(e);
			if (e instanceof RemoteSimulationException)
			{
				throw (RemoteSimulationException) e;
			}
			throw new RemoteSimulationException("Faild to initialize, could not get sourcefiles", e);
		}
		return files;
	}

	// private void setFaults(List<InitializefaultsStructParam> faults)
	// {
	// // set faults this might need to be set from the load instead
	// }
	//

	public void setMainContext(Context ctxt)
	{
		this.mainContext = ctxt;
	}

	public Integer getStatus()
	{
		return this.status.value;
	}

	/**
	 * Sets design parameters. All class definitions in the loaded model are searched and existing value definitions are
	 * exstracted. If their name match the parameter the LexRealToken value is updated by reflection (value is final)
	 * with the new value from the design parameter
	 * 
	 * @param parameters
	 *            A list of Maps containing (name,value) keys and name->String, value->Double
	 * @return false if any error occur else true
	 * @throws RemoteSimulationException
	 */
	public Boolean setDesignParameters(List<Map<String, Object>> parameters)
			throws RemoteSimulationException
	{
		try
		{
			for (Map<String, Object> parameter : parameters)
			{
				boolean found = false;
				String parameterName = parameter.get("name").toString();

				if (!links.getSharedDesignParameters().contains(parameterName))
				{
					debugErr("Tried to set unlinked shared design parameter: "
							+ parameterName);
					throw new RemoteSimulationException("Tried to set unlinked shared design parameter: "
							+ parameterName);
				}
				StringPair vName = links.getBoundVariable(parameterName);
				double newValue = (Double) parameter.get("value");

				for (ClassDefinition cd : controller.getInterpreter().getClasses())
				{
					if (!cd.getName().equals(vName.instanceName))
					{
						// wrong class
						continue;
					}
					for (Definition def : cd.definitions)
					{
						if (def instanceof ValueDefinition)
						{
							ValueDefinition vDef = (ValueDefinition) def;
							if (vDef.pattern.toString().equals(vName.variableName)
									&& vDef.isValueDefinition()
									&& vDef.getType() instanceof RealType)
							{
								if (vDef.exp instanceof RealLiteralExpression)
								{
									RealLiteralExpression exp = ((RealLiteralExpression) vDef.exp);
									LexRealToken token = exp.value;

									Field valueField = LexRealToken.class.getField("value");
									valueField.setAccessible(true);

									valueField.setDouble(token, newValue);
									found = true;
								}
							}
						}
					}
				}
				if (!found)
				{
					debugErr("Tried to set unlinked shared design parameter: "
							+ parameterName);
					throw new RemoteSimulationException("Tried to set unlinked shared design parameter: "
							+ parameterName);
				}
			}
		} catch (Exception e)
		{
			debugErr(e);
			if (e instanceof RemoteSimulationException)
			{
				throw (RemoteSimulationException) e;
			}
			throw new RemoteSimulationException("Internal error in set design parameters", e);
		}

		return true;
	}

	public Boolean setParameter(String name, Double value)
			throws RemoteSimulationException
	{
		try
		{
			return setValue(name, CoSimType.NumericValue, value.toString());
		} catch (Exception e)
		{
			debugErr(e);
			if (e instanceof RemoteSimulationException)
			{
				throw (RemoteSimulationException) e;
			}
			throw new RemoteSimulationException("Error in set parameter", e);
		}
	}

	public synchronized Boolean stopSimulation() throws RemoteSimulationException
	{
		try
		{
			scheduler.stop();
			RTLogger.dump(true);
			notify();
			return true;
		} catch (Exception e)
		{
			debugErr(e);
			if (e instanceof RemoteSimulationException)
			{
				throw (RemoteSimulationException) e;
			}
			throw new RemoteSimulationException("Could not stop the scheduler", e);

		}
	}

	public List<QueryToolSettingsStructsettingsStruct> queryToolSettings() {
		// TODO Auto-generated method stub
		return null;
	}
}
