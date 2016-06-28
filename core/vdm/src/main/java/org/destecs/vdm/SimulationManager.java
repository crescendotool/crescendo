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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.destecs.core.vdmlink.LinkInfo;
import org.destecs.core.vdmlink.Links;
import org.destecs.core.vdmlink.StringPair;
import org.destecs.protocol.exceptions.RemoteSimulationException;
import org.destecs.protocol.structs.GetDesignParametersStructdesignParametersStruct;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destecs.script.ast.types.ARealType;
import org.destecs.vdm.utility.VDMClassHelper;
import org.destecs.vdm.utility.ValueInfo;
import org.destecs.vdmj.VDMCO;
import org.destecs.vdmj.log.SimulationLogger;
import org.destecs.vdmj.log.SimulationMessage;
import org.destecs.vdmj.scheduler.EventThread;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.ASeqEnumSeqExp;
import org.overture.ast.expressions.AUnaryMinusUnaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SSeqExp;
import org.overture.ast.factory.AstFactoryTC;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.intf.lex.ILexRealToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexRealToken;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.debug.DBGPReaderV2;
import org.overture.interpreter.debug.DBGPStatus;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.messages.rtlog.RTTextLogger;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenRTLogger;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.RuntimeValidator;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.state.ASystemClassDefinitionRuntime;
import org.overture.interpreter.runtime.validation.BasicRuntimeValidator;
import org.overture.interpreter.runtime.validation.ConjectureDefinition;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.CPUResource;
import org.overture.interpreter.scheduler.RunState;
import org.overture.interpreter.scheduler.SharedStateListner;
import org.overture.interpreter.scheduler.SystemClock;
import org.overture.interpreter.util.ExitStatus;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.RealValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.UndefinedValue;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueListener;
import org.overture.interpreter.values.ValueListenerList;
import org.overture.parser.config.Properties;
import org.overture.typechecker.TypeChecker;

public class SimulationManager extends BasicSimulationManager
{

	// Thread runner;
	private Context mainContext = null;
	private final static String scriptClass = "World";
	private final static String scriptOperation = "run";
	private final static String script = "new " + scriptClass + "()."
			+ scriptOperation + "()";

	private CoSimStatusEnum status = CoSimStatusEnum.NOT_INITIALIZED;
	final private List<String> variablesToLog = new Vector<String>();
	private File simulationLogFile;
	private boolean isSchedulingHookConfigured = false;
	private File coverageDirectory = null;
	private boolean noOptimization;
	private long internalFinishTime;

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

	public Map<String, LinkInfo> getSharedDesignParameters()
	{
		return links.getSharedDesignParameters();
	}

	public Map<String, LinkInfo> getInputVariables()
	{
		return links.getInputs();
	}

	public Map<String, LinkInfo> getOutputVariables()
	{
		return links.getOutputs();
	}

	/**
	 * Crescendo step method using xml-rpc commands
	 * 
	 * @param outputTime
	 * @param inputs
	 * @param events
	 * @return
	 * @throws RemoteSimulationException
	 */
	public synchronized StepStruct step(Double outputTime,
			List<StepinputsStructParam> inputs, final List<String> events)
			throws RemoteSimulationException
	{
		checkMainContext();

		for (StepinputsStructParam p : inputs)
		{
			setValue(p.name, CoSimType.Auto, new ValueContents(p.value, p.size));
		}

		doInternalStep(outputTime, new ICallDelegate()
		{

			public void call() throws RemoteSimulationException
			{
				if (events.size() > 0)
				{
					for (String event : events)
					{
						evalEvent(event);
					}
				}
			}
		});

		List<StepStructoutputsStruct> outputs = new Vector<StepStructoutputsStruct>();

		for (String key : links.getOutputs().keySet())
		{
			try
			{
				ValueContents value = getOutput(key);
				if (value != null)
				{
					outputs.add(new StepStructoutputsStruct(key, value.size, value.value));
				} else
				{
					throw new RemoteSimulationException("Faild to get output parameter, output not bound for: "
							+ key);
				}
			} catch (ValueException e)
			{
				debugErr(e);
				throw new RemoteSimulationException("Faild to get output parameter", e);
			}
		}

		StepStruct result = new StepStruct(new Vector<String>(), outputs, StepResultEnum.SUCCESS.value, new Double(nextSchedulableActionTime));

		return result;
	}



	protected void checkMainContext() throws RemoteSimulationException
	{
		if (runtimeException != null)
		{
			throw runtimeException;
		}
		// first check if main thread is active
		if (mainContext != null)
		{
			if (mainContext.threadState != null
					&& mainContext.threadState.dbgp != null
					&& mainContext.threadState.dbgp instanceof DBGPReaderCoSim
					&& ((DBGPReaderCoSim) mainContext.threadState.dbgp).getStatus() == DBGPStatus.STOPPED)
			{
				throw new RemoteSimulationException("VDM Main Thread no longer active. Forced to stop before end time.", null);
			}
		} else
		{
			throw new RemoteSimulationException("Interpreter is not correctly initialized.", null);
		}
	}

	private static interface ICallDelegate
	{
		void call() throws RemoteSimulationException;
	}

	protected void doInternalStep(Double outputTime, ICallDelegate preStepAction)
			throws RemoteSimulationException
	{
		nextTimeStep = outputTime.longValue();
		debug("Next Step clock: " + nextTimeStep);

		if (preStepAction != null)
		{
			preStepAction.call();
		}

		try
		{
			interpreterRunning = true;
			SharedStateListner.resetAutoIncrementTime();
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
	}

	private void evalEvent(String event) throws RemoteSimulationException
	{
		boolean evaluated = false;
		if (links.getEvents().keySet().contains(event))
		{
			Value val = getValue(event).value;
			if (val.deref() instanceof OperationValue)
			{
				OperationValue eventOp = (OperationValue) val;
				if (eventOp.paramPatterns.size() == 0)
				{
					try
					{
						EventThread eThread = new EventThread(Thread.currentThread());
						BasicSchedulableThread.add(eThread);
						CPUResource.vCPU.register(eThread, 1000);
						eventOp.eval(coSimLocation, new ValueList(), mainContext);
						eThread.setState(RunState.COMPLETE);
						BasicSchedulableThread.remove(eThread);
						evaluated = true;
					} catch (ValueException e)
					{
						debugErr(e);
						throw new RemoteSimulationException("Faild to evaluate event: "
								+ event, e);
					} catch (AnalysisException e)
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

	/**
	 * gets an output with dimensions
	 * 
	 * @param name
	 * @return
	 * @throws ValueException
	 * @throws RemoteSimulationException
	 */
	private ValueContents getOutput(String name) throws ValueException,
			RemoteSimulationException
	{
		NameValuePairList list = ASystemClassDefinitionRuntime.getSystemMembers();
		if (list != null && links.getLinks().containsKey(name))
		{
			List<String> varName = links.getQualifiedName(name);

			Value value = VDMClassHelper.digForVariable(varName.subList(1, varName.size()), list).value;

			if (value.deref() instanceof UndefinedValue)
			{
				throw new RemoteSimulationException("Value: " + name
						+ " not initialized");
			}

			return new ValueContents(VDMClassHelper.getDoubleListFromValue(value), VDMClassHelper.getValueDimensions(value));

		}
		throw new RemoteSimulationException("Value: " + name + " not found");
	}

	public Boolean load(List<File> specfiles, Links links, File outputDir,
			File baseDirFile, boolean disableRtLog, boolean disableCoverage,
			boolean disableOptimization) throws RemoteSimulationException
	{
		try
		{
			noOptimization = disableOptimization;
			this.variablesToLog.clear();
			// this.variablesToLog.addAll(variablesToLog);
			this.links = links;

			if (disableRtLog)
			{
				RTLogger.enable(false);
				controller.setLogFile(null);
			} else
			{
				File logFile = new File(outputDir, "ExecutionTrace.logrt");
				controller.setLogFile(logFile);
				RTLogger.enable(true);
				RTLogger.setLogfile(RTTextLogger.class, logFile);
				RTLogger.setLogfile(NextGenRTLogger.class, logFile);
			}

			if (Settings.timingInvChecks)
			{
				PrintWriter p = new PrintWriter(new FileOutputStream(new File(outputDir, "Timing.logtv"), false));
				RuntimeValidator.setLogFile(p);
			}
			if (!disableCoverage)
			{
				coverageDirectory = new File(outputDir, "coverage");
			}
			if (this.variablesToLog.isEmpty())
			{
				SimulationLogger.enable(false);
			} else
			{
				this.simulationLogFile = new File(outputDir, "desimulation.log");
			}
			controller.setScript(script);
			Settings.baseDir = baseDirFile;

			VDMCO.outputDir = outputDir;
			ExitStatus status = controller.parse(specfiles);

			if (status == ExitStatus.EXIT_OK)
			{
				status = controller.typeCheck();

				if (status == ExitStatus.EXIT_OK)
				{
					this.status = CoSimStatusEnum.LOADED;

				} else
				{
					final Writer result = new StringWriter();
					final PrintWriter printWriter = new PrintWriter(result);
					TypeChecker.printWarnings(printWriter);
					TypeChecker.printErrors(printWriter);
					throw new RemoteSimulationException("Type check error: "
							+ result.toString());
				}
			} else
			{
				throw new RemoteSimulationException("Cannot not parse specification", null);
			}

			boolean hasScriptCall = false;
			int cpus = 0;
			this.interpreter = controller.getInterpreter();
			for (SClassDefinition def : interpreter.getClasses())
			{
				if (def instanceof ASystemClassDefinition)
				{
					for (PDefinition d : def.getDefinitions())
					{
						PType t = d.getType();

						if (t instanceof AClassType)
						{
							AClassType ct = (AClassType) t;
							if (ct.getClassdef() instanceof ACpuClassDefinition)
							{
								cpus++;
							}
						}
					}
				} else if (def instanceof AClassClassDefinition)
				{
					if (def.getName().getName().equals(scriptClass))
					{
						for (PDefinition d : def.getDefinitions())
						{
							if (d.getName() != null
									&& d.getName().getName().equals(scriptOperation)
									&& (d instanceof AExplicitOperationDefinition || d instanceof AExplicitFunctionDefinition))
							{
								hasScriptCall = true;
							}
						}
					}
				}
			}

			if (!hasScriptCall)
			{
				throw new RemoteSimulationException("The specification do not contain the entrypoint: "
						+ script, null);
			}

			if (cpus == 0)
			{
				throw new RemoteSimulationException("Cannot load system to few CPUS", null);
			}
			return true;

		} catch (RemoteSimulationException e)
		{
			throw e;
		} catch (Exception e)

		{
			debugErr(e);
			throw new RemoteSimulationException("Internal Error while loading the model.", e);

		}

	}

	private void configureSchedulingHooks()
	{
		if (!isOptimizationEnabled())
		{
			return;
		}
		final List<ILexNameToken> readCheck = new Vector<ILexNameToken>();
		final List<ILexNameToken> writeCheck = new Vector<ILexNameToken>();
		try
		{

			for (Entry<String, LinkInfo> entry : links.getInputs().entrySet())
			{

				NameValuePairList list = ASystemClassDefinitionRuntime.getSystemMembers();
				if (list != null)
				{
					List<String> varName = entry.getValue().getQualifiedName();
					ValueInfo value = VDMClassHelper.digForVariable(varName.subList(1, varName.size()), list);
					readCheck.add(value.name);
				}

			}
			for (Entry<String, LinkInfo> entry : links.getOutputs().entrySet())
			{
				NameValuePairList list = ASystemClassDefinitionRuntime.getSystemMembers();
				if (list != null)
				{
					List<String> varName = entry.getValue().getQualifiedName();
					ValueInfo value = VDMClassHelper.digForVariable(varName.subList(1, varName.size()), list);
					writeCheck.add(value.name);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		SharedStateListner.setIdentityChecker(new SharedStateListner.IdentityChecker()
		{

			public boolean reuiresCheck(PStateDesignator target)
			{
				if (target instanceof AIdentifierStateDesignator)
				{
					ILexNameToken name = ((AIdentifierStateDesignator) target).getName();
					for (ILexNameToken n : writeCheck)
					{
						if (name.getModule().equals(n.getModule())
								&& name.getName().equals(n.getName()))
						{
							return true;
						}
					}
				}
				return false;
			}

			public boolean requiresCheck(ILexNameToken name)
			{
				for (ILexNameToken n : readCheck)
				{
					if (name.getModule().equals(n.getModule())
							&& name.getName().equals(n.getName()))
					{
						return true;
					}
				}
				return false;
			}
		});
		isSchedulingHookConfigured = true;
	}

	public Boolean initialize() throws RemoteSimulationException
	{
		runtimeException = null;
		Properties.init();
		Properties.parser_tabstop = 1;
		Properties.rt_duration_transactions = true;

		Settings.dialect = Dialect.VDM_RT;
		Settings.usingCmdLine = false;
		Settings.usingDBGP = true;
		Settings.release = Release.VDM_10;
		Settings.timingInvChecks = true;
		RuntimeValidator.setLogFile(null);

		controller = new VDMCO();
		SharedStateListner.setIdentityChecker(null);
		isSchedulingHookConfigured = false;
		internalFinishTime = -1;
		return true;
	}

	public Boolean start(long internalFinishTime)
			throws RemoteSimulationException
	{
		final List<File> files = getFiles();
		this.internalFinishTime = internalFinishTime;

		if (controller.asyncStartInterpret(files) == ExitStatus.EXIT_OK)
		{
			// this.status = CoSimStatusEnum.INITIALIZED;
			while (this.status != CoSimStatusEnum.STARTED)
			{
				try
				{
					Thread.sleep(100);
				} catch (InterruptedException e)
				{
					// ignore
				}
			}
			return true;
		} else
		{
			this.status = CoSimStatusEnum.NOT_INITIALIZED;
			throw new RemoteSimulationException("Status = "
					+ this.status
					+ ". Internal error: "
					+ (controller.exception != null ? controller.exception.getMessage()
							: "unknown"));
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

	public void setMainContext(Context ctxt)
	{
		this.mainContext = ctxt;

		try
		{
			setLogVariables(simulationLogFile, new Vector<String>(variablesToLog));
		} catch (RemoteSimulationException e)
		{
			this.runtimeException = e;
		}

		if (!isSchedulingHookConfigured)
		{
			configureSchedulingHooks();
		}

		// show timing invariants if any
		if (Settings.timingInvChecks)
		{
			if (RuntimeValidator.validator != null
					&& RuntimeValidator.validator instanceof BasicRuntimeValidator)
			{
				Console.out.println("----------------------------------------------------------------------------------");
				Console.out.println("Runtime Validator Initialized with conjectures:");
				BasicRuntimeValidator validator = (BasicRuntimeValidator) RuntimeValidator.validator;
				for (ConjectureDefinition conj : validator.getConjectures())
				{
					Console.out.println("\t" + conj + "\n");
				}
				Console.out.println("----------------------------------------------------------------------------------");
			}
		}
		this.status = CoSimStatusEnum.STARTED;
	}

	protected void setLogVariables(File logFile, List<String> logVariables)
			throws RemoteSimulationException
	{
		// Cache info it called before execution is started.
		this.simulationLogFile = logFile;
		this.variablesToLog.clear();
		this.variablesToLog.addAll(logVariables);

		if (this.mainContext == null)
		{
			return;// Cant set log variables we dont have a running system.
		}
		// This is called from the scheduler so we have a running system. So add listeners for log variables
		if (!logVariables.isEmpty())
		{
			try
			{
				PrintWriter p = new PrintWriter(new FileOutputStream(logFile, false));
				SimulationLogger.setLogfileCsv(p);

				p = new PrintWriter(new FileOutputStream(new File(logFile.getAbsolutePath()
						+ ".log"), false));
				SimulationLogger.setLogfile(p);
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}

			for (String name : logVariables)
			{
				String[] names = name.split("\\.");
				Value v = getRawValue(Arrays.asList(names), null);
				if (v == null)
				{
					throw new RemoteSimulationException("Could not find variable: "
							+ name + " logging is skipped.");
				}

				if (v instanceof UpdatableValue)
				{
					UpdatableValue upVal = (UpdatableValue) v;
					final String variableLogName = name;
					ValueListener listener = new ValueListener()
					{
						final String name = variableLogName;

						public void changedValue(ILexLocation location,
								Value value, Context ctxt)
						{
							SimulationLogger.log(new SimulationMessage(name, SystemClock.getWallTime(), value.toString()));
						}
					};
					if (upVal.listeners == null)
					{
						upVal.listeners = new ValueListenerList(listener);
					} else
					{
						upVal.listeners.add(listener);
					}
					try
					{
						listener.changedValue(null, upVal, null);
					} catch (AnalysisException e)
					{
						debugErr(e);
						throw new RemoteSimulationException("Faild to change value internally", e);
					}
				} else
				{
					System.err.println("A non updatable value cannot be logged...it is constant!");
				}
			}
			SimulationLogger.prepareCsv();
		}
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

				if (!links.getSharedDesignParameters().keySet().contains(parameterName))
				{
					debugErr("Tried to set unlinked shared design parameter: "
							+ parameterName);
					throw new RemoteSimulationException("Tried to set unlinked shared design parameter: "
							+ parameterName);
				}
				@SuppressWarnings("deprecation")
				StringPair vName = links.getBoundVariable(parameterName);
				Object[] objValue = (Object[]) parameter.get("value");
				Object[] dimension = (Object[]) parameter.get("size");

				for (SClassDefinition cd : controller.getInterpreter().getClasses())
				{
					if (!cd.getName().getName().equals(vName.instanceName))
					{
						// wrong class
						continue;
					}
					for (PDefinition def : cd.getDefinitions())
					{
						if (def instanceof AValueDefinition)
						{
							AValueDefinition vDef = (AValueDefinition) def;
							if (vDef.getPattern().toString().equals(vName.variableName)
									&& Interpreter.getInstance().getAssistantFactory().createPDefinitionAssistant().isValueDefinition(vDef))
							{
								if (dimension.length == 1
										&& ((Integer) dimension[0] == 1))
								{
									Double newValue = (Double) objValue[0];
									found = setValueForSDP(newValue, vDef);
								} else
								{
									// dealing with a sequence
									if (vDef.getExpression() instanceof ASeqEnumSeqExp)
									{
										ASeqEnumSeqExp seqEnum = (ASeqEnumSeqExp) vDef.getExpression();
										found = createSeqEnum(vName.variableName, seqEnum, objValue, dimension);
									}

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
		} catch (RemoteSimulationException e)
		{
			throw e;
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

	private boolean createSeqEnum(String variableName, ASeqEnumSeqExp seqEnum,
			Object[] objValue, Object[] objDimensions)
			throws NoSuchFieldException, IllegalAccessException,
			RemoteSimulationException
	{
		List<Integer> dimensions = new Vector<Integer>();
		int index = 0;
		boolean found = true;
		for (Object o : objDimensions)
		{
			if (o instanceof Integer)
			{
				dimensions.add((Integer) o);
			}
		}

		if (dimensions.size() == 1) // it's an array
		{
			for (PExp exp : seqEnum.getMembers())
			{
				if (exp instanceof ASeqEnumSeqExp)
				{
					found = found
							&& setValueForSDP((Double) objValue[index++], exp);
				}
			}
		} else
		// it's a matrix
		{
			boolean match = checkDimentionsOfSeqEnum(seqEnum, dimensions);
			if (!match)
			{
				throw new RemoteSimulationException("Dimension of \""
						+ variableName + "\" does not match the SDP ");
			}
			for (PExp exp : seqEnum.getMembers())
			{
				if (exp instanceof ASeqEnumSeqExp)
				{
					ASeqEnumSeqExp seqEnumInner = (ASeqEnumSeqExp) exp;
					for (PExp expInner : seqEnumInner.getMembers())
					{
						found = found
								&& setValueForSDP((Double) objValue[index++], expInner);
					}
				}
			}
		}

		return found;
	}

	private boolean checkDimentionsOfSeqEnum(ASeqEnumSeqExp seqEnum,
			List<Integer> dimensions)
	{
		if (seqEnum.getMembers().size() != dimensions.get(0))
			return false;

		for (PExp exp : seqEnum.getMembers())
		{
			if (exp instanceof ASeqEnumSeqExp)
			{
				ASeqEnumSeqExp seqEnumInner = (ASeqEnumSeqExp) exp;
				if (seqEnumInner.getMembers().size() != dimensions.get(1))
					return false;
			}
		}

		return true;

	}

	private boolean setValueForSDP(Double newValue, PExp exp)
			throws IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, SecurityException
	{
		boolean found = false;

		if (exp != null && exp instanceof ARealLiteralExp)
		{
			ARealLiteralExp rExp = ((ARealLiteralExp) exp);
			ILexRealToken token = rExp.getValue();

			Field valueField = LexRealToken.class.getField("value");
			valueField.setAccessible(true);

			valueField.setDouble(token, newValue);
			found = true;
		}
		return found;
	}

	private boolean setValueForSDP(Double newValue, AValueDefinition vDef)
			throws NoSuchFieldException, IllegalAccessException
	{
		boolean found = false;

		if (vDef.getExpression() != null
				&& vDef.getExpression() instanceof ARealLiteralExp)
		{
			found = setValueForSDP(newValue, vDef.getExpression());

		} else if (vDef.getExpression() != null
				&& vDef.getExpression() instanceof AIntLiteralExp
				|| vDef.getExpression() instanceof AUnaryMinusUnaryExp)
		{
			ARealLiteralExp newReal = AstFactoryTC.newARealLiteralExp(new LexRealToken(newValue, vDef.getLocation()));// new
																														// ARealLiteralExp(new
																														// LexRealToken(newValue,
																														// vDef.location));
			// Field valDefField = AValueDefinition.class.getField("exp");
			// valDefField.setAccessible(true);
			// valDefField.set(vDef, newReal);
			vDef.setExpression(newReal);
			found = true;
		}

		return found;
	}

	public GetDesignParametersStructdesignParametersStruct getDesignParameter(
			String parameterName) throws RemoteSimulationException
	{
		try
		{
			boolean found = false;

			if (!links.getSharedDesignParameters().keySet().contains(parameterName))
			{
				debugErr("Tried to set unlinked shared design parameter: "
						+ parameterName);
				throw new RemoteSimulationException("Tried to set unlinked shared design parameter: "
						+ parameterName);
			}
			@SuppressWarnings("deprecation")
			StringPair vName = links.getBoundVariable(parameterName);

			for (SClassDefinition cd : controller.getInterpreter().getClasses())
			{
				if (!cd.getName().equals(vName.instanceName))
				{
					// wrong class
					continue;
				}
				for (PDefinition def : cd.getDefinitions())
				{
					if (def instanceof AValueDefinition)
					{
						AValueDefinition vDef = (AValueDefinition) def;
						if (vDef.getPattern().toString().equals(vName.variableName)
								&& Interpreter.getInstance().getAssistantFactory().createPDefinitionAssistant().isValueDefinition(vDef)
								&& vDef.getType() instanceof ARealType)
						{
							if (vDef.getExpression() instanceof ARealLiteralExp)
							{
								ARealLiteralExp exp = ((ARealLiteralExp) vDef.getExpression());
								ILexRealToken token = exp.getValue();
								List<Double> value = new Vector<Double>();
								value.add(token.getValue());
								List<Integer> size = new Vector<Integer>();
								size.add(1);
								return new GetDesignParametersStructdesignParametersStruct(parameterName, size, value);
							} else if (vDef.getExpression() instanceof SSeqExp)
							{
								throw new RemoteSimulationException("getDesignParameter with type SeqExpression not supported: "
										+ vDef.getExpression());
							}

						}
					}
				}
			}
			if (!found)
			{
				debugErr("Tried to get unlinked shared design parameter: "
						+ parameterName);
				throw new RemoteSimulationException("Tried to get unlinked shared design parameter: "
						+ parameterName);
			}

		} catch (RemoteSimulationException e)
		{
			throw e;
		} catch (Exception e)
		{
			debugErr(e);
			if (e instanceof RemoteSimulationException)
			{
				throw (RemoteSimulationException) e;
			}
			throw new RemoteSimulationException("Internal error in get design parameter", e);
		}

		throw new RemoteSimulationException("Internal error in get design parameter");
	}

	/**
	 * This function returns VDM values which is constants
	 * 
	 * @return
	 * @throws RemoteSimulationException
	 */
	public Map<String, ValueContents> getParameters()
			throws RemoteSimulationException
	{
		try
		{
			Map<String, ValueContents> parameters = new Hashtable<String, ValueContents>();

			NameValuePairList list = ASystemClassDefinitionRuntime.getSystemMembers();
			if (list != null && list.size() > 0)
			{
				parameters.putAll(getParameters(list.get(0).name.getModule(), list, 0));
			}

			return parameters;
		} catch (Exception e)
		{
			debugErr(e);
			if (e instanceof RemoteSimulationException)
			{
				throw (RemoteSimulationException) e;
			}
			throw new RemoteSimulationException("Internal error in get parameters", e);
		}
	}

	private Map<String, ValueContents> getParameters(String name,
			NameValuePairList members, int depth) throws ValueException
	{
		Map<String, ValueContents> parameters = new Hashtable<String, ValueContents>();
		String prefix = (name.length() == 0 ? "" : name + ".");
		if (depth < 10)
		{
			for (NameValuePair p : members)
			{
				if (p.value.deref() instanceof ObjectValue)
				{
					ObjectValue po = (ObjectValue) p.value.deref();
					parameters.putAll(getParameters(prefix + p.name.getName(), po.members.asList(), depth++));

				} else if (p.value.deref() instanceof RealValue)
				{
					List<Double> realValue = new Vector<Double>();
					realValue.add(p.value.realValue(null));
					List<Integer> valueSize = new Vector<Integer>();
					valueSize.add(1);
					parameters.put(prefix + p.name.getName(), new ValueContents(realValue, valueSize));
				} else if (p.value.deref() instanceof BooleanValue)
				{
					List<Double> realValue = new Vector<Double>();
					realValue.add(p.value.boolValue(null) ? 1.0 : 0.0);
					List<Integer> valueSize = new Vector<Integer>();
					valueSize.add(1);
					parameters.put(prefix + p.name.getName(), new ValueContents(realValue, valueSize));
				} else if (p.value.deref() instanceof SeqValue)
				{
					System.out.println("getParameters:sequence");
				}
			}
		}
		return parameters;
	}

	/**
	 * This function returns a collection of VDM instance variables
	 * 
	 * @param filter
	 *            a filter used to restrict the returned collection, if empty the full set is returned
	 * @return
	 * @throws RemoteSimulationException
	 */
	public Map<String, ValueContents> getInstanceVariables(List<String> filter)
			throws RemoteSimulationException
	{
		try
		{
			Map<String, ValueContents> parameters = new Hashtable<String, ValueContents>();

			for (Entry<String, LinkInfo> entrySet : this.links.getModel().entrySet())
			{
				if (!filter.isEmpty() && !filter.contains(entrySet.getKey()))
				{
					continue;
				}
				ValueContents val = getOutput(entrySet.getKey());
				parameters.put(entrySet.getKey(), val);
			}

			return parameters;
		} catch (Exception e)
		{
			debugErr(e);
			if (e instanceof RemoteSimulationException)
			{
				throw (RemoteSimulationException) e;
			}
			throw new RemoteSimulationException("Internal error in get parameters", e);
		}
	}

	public Boolean setInstanceVariable(String name, ValueContents valueContents)
			throws RemoteSimulationException
	{
		try
		{
			return setValue(name, CoSimType.Auto, valueContents);
		} catch (RemoteSimulationException e)
		{
			throw e;
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

	public synchronized Boolean stopSimulation()
			throws RemoteSimulationException
	{
		try
		{
			if (scheduler != null)
			{
				scheduler.stop();
			}
			if (coverageDirectory != null)
			{
				coverageDirectory.mkdirs();
				DBGPReaderV2.writeCoverage(interpreter, coverageDirectory);
				for (File source : interpreter.getSourceFiles())
				{
					String name = source.getName() + "cov";

					try
					{
						InputStream in = new FileInputStream(source);
						OutputStream out = new FileOutputStream(new File(coverageDirectory, name));
						byte[] buf = new byte[1024];
						int len;
						while ((len = in.read(buf)) > 0)
						{
							out.write(buf, 0, len);
						}
						in.close();
						out.close();
					} catch (Exception e)
					{

					}

				}
			}
			RTLogger.dump(true);
			SimulationLogger.dump(true);
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

	public List<Double> getParameter(String name)
			throws RemoteSimulationException
	{
		try
		{
			Value value = getValue(name).value;

			List<Double> result = VDMClassHelper.getDoubleListFromValue(value);
			if (result != null)
			{
				return result;
			}
			throw new RemoteSimulationException("Could not get parameter: "
					+ name);

		} catch (ValueException e)
		{
			debugErr(e);
			throw new RemoteSimulationException("Could not get parameter: "
					+ name, e);
		}
	}

	public List<Integer> getParameterSize(String name)
			throws RemoteSimulationException
	{
		Value value = getValue(name).value;
		return VDMClassHelper.getValueDimensions(value);

	}

	public boolean hasEvents()
	{
		return !links.getEvents().isEmpty();
	}

	public boolean isOptimizationEnabled()
	{
		return !noOptimization && !hasEvents();
	}

	public List<String> getLogEnabledVariables()
	{
		List<String> list = new Vector<String>();
		for (SClassDefinition c : interpreter.getClasses())
		{
			if (c instanceof ASystemClassDefinition)
			{
				String prefix = "System";
				for (PDefinition def : c.getDefinitions())
				{
					list.addAll(getLogEnabledVariables(list, prefix, def, new Vector<PDefinition>()));
				}
			}
		}
		return list;
	}

	private Collection<? extends String> getLogEnabledVariables(
			final List<String> list, String prefix, PDefinition def,
			List<PDefinition> path)
	{
		List<String> result = new Vector<String>();
		if (def instanceof AInstanceVariableDefinition && !path.contains(def))
		{
			AInstanceVariableDefinition var = (AInstanceVariableDefinition) def;
			String name = prefix + "." + var.getName();
			result.add(name);
			path.add(var);
			if (var.getType() instanceof AClassType)
			{
				result.addAll(getLogEnabledVariables(result, name, ((AClassType) var.getType()).getClassdef(), path));
			}

		}

		return result;
	}

	public long getFinishTime()
	{
		return internalFinishTime;
	}
}
