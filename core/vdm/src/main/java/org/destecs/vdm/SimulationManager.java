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

import org.destecs.core.parsers.VdmLinkParserWrapper;
import org.destecs.core.vdmlink.LinkInfo;
import org.destecs.core.vdmlink.StringPair;
import org.destecs.protocol.exceptions.RemoteSimulationException;
import org.destecs.protocol.structs.GetDesignParametersStructdesignParametersStruct;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;
import org.destecs.protocol.structs.StepinputsStructParam;
import org.destecs.vdm.utility.VDMClassHelper;
import org.destecs.vdm.utility.ValueInfo;
import org.destecs.vdmj.VDMCO;
import org.destecs.vdmj.log.SimulationLogger;
import org.destecs.vdmj.log.SimulationMessage;
import org.destecs.vdmj.scheduler.EventThread;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.config.Properties;
import org.overturetool.vdmj.debug.DBGPReaderV2;
import org.overturetool.vdmj.debug.DBGPStatus;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.IntegerLiteralExpression;
import org.overturetool.vdmj.expressions.RealLiteralExpression;
import org.overturetool.vdmj.expressions.SeqEnumExpression;
import org.overturetool.vdmj.expressions.SeqExpression;
import org.overturetool.vdmj.expressions.UnaryMinusExpression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.rtlog.RTLogger;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.RuntimeValidator;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.runtime.validation.BasicRuntimeValidator;
import org.overturetool.vdmj.runtime.validation.ConjectureDefinition;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.SharedStateListner;
import org.overturetool.vdmj.scheduler.SystemClock;
import org.overturetool.vdmj.statements.IdentifierDesignator;
import org.overturetool.vdmj.statements.StateDesignator;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.RealValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.UndefinedValue;
import org.overturetool.vdmj.values.UpdatableValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueListener;
import org.overturetool.vdmj.values.ValueListenerList;

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

	public synchronized StepStruct step(Double outputTime,
			List<StepinputsStructParam> inputs, List<String> events)
			throws RemoteSimulationException
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

		for (StepinputsStructParam p : inputs)
		{
			setValue(p.name, CoSimType.Auto, new ValueContents(p.value, p.size));
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

		List<StepStructoutputsStruct> outputs = new Vector<StepStructoutputsStruct>();

		for (String key : links.getOutputs().keySet())
		{
			try
			{
				ValueContents value = getOutput(key);
				if (value != null)
				{
					outputs.add(new StepStructoutputsStruct(key, value.value, value.size));
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

		StepStruct result = new StepStruct(StepResultEnum.SUCCESS.value, new Double(nextSchedulableActionTime), new Vector<String>(), outputs);

		return result;
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

	private ValueContents getOutput(String name) throws ValueException,
			RemoteSimulationException
	{
		NameValuePairList list = SystemDefinition.getSystemMembers();
		if (list != null && links.getLinks().containsKey(name))
		{
			List<String> varName = links.getQualifiedName(name);

			Value value = VDMClassHelper.digForVariable(varName.subList(1, varName.size()), list).value;

			if(value.deref() instanceof UndefinedValue)
			{
				throw new RemoteSimulationException("Value: " + name + " not initialized");
			}
			
			return new ValueContents(VDMClassHelper.getDoubleListFromValue(value), VDMClassHelper.getValueDimensions(value));

		}
		throw new RemoteSimulationException("Value: " + name + " not found");
	}

	public Boolean load(List<File> specfiles, File linkFile, File outputDir,
			File baseDirFile, boolean disableRtLog, boolean disableCoverage, boolean disableOptimization)
			throws RemoteSimulationException
	{
		try
		{
			noOptimization = disableOptimization;
			this.variablesToLog.clear();
			// this.variablesToLog.addAll(variablesToLog);
			if (!linkFile.exists() || linkFile.isDirectory())
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

			if (disableRtLog)
			{
				RTLogger.enable(false);
				controller.setLogFile(null);
			} else
			{
				File logFile = new File(outputDir, "ExecutionTrace.logrt");
				controller.setLogFile(logFile);
				RTLogger.enable(true);
				RTLogger.setLogfile(new PrintWriter(logFile));
			}
			
			if(Settings.timingInvChecks)
			{
				PrintWriter p = new PrintWriter(new FileOutputStream(
						new File(outputDir,"Timing.logtv"), false));
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
			for (ClassDefinition def : interpreter.getClasses())
			{
				if (def instanceof SystemDefinition)
				{
					for (Definition d : def.definitions)
					{
						Type t = d.getType();

						if (t instanceof ClassType)
						{
							ClassType ct = (ClassType) t;
							if (ct.classdef instanceof CPUClassDefinition)
							{
								cpus++;
							}
						}
					}
				} else if (def instanceof ClassDefinition)
				{
					if (def.getName().equals(scriptClass))
					{
						for (Definition d : def.definitions)
						{
							if (d.getName() != null
									&& d.getName().equals(scriptOperation)
									&& (d instanceof ExplicitOperationDefinition || d instanceof ExplicitFunctionDefinition))
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
		if(!isOptimizationEnabled())
		{
			return;
		}
		final List<LexNameToken> readCheck = new Vector<LexNameToken>();
		final List<LexNameToken> writeCheck = new Vector<LexNameToken>();
		try
		{

			for (Entry<String, LinkInfo> entry : links.getInputs().entrySet())
			{

				NameValuePairList list = SystemDefinition.getSystemMembers();
				if (list != null)
				{
					List<String> varName = entry.getValue().getQualifiedName();
					ValueInfo value = VDMClassHelper.digForVariable(varName.subList(1, varName.size()), list);
					readCheck.add(value.name);
				}

			}
			for (Entry<String, LinkInfo> entry : links.getOutputs().entrySet())
			{
				NameValuePairList list = SystemDefinition.getSystemMembers();
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

			public boolean reuiresCheck(StateDesignator target)
			{
				if (target instanceof IdentifierDesignator)
				{
					LexNameToken name = ((IdentifierDesignator) target).name;
					for (LexNameToken n : writeCheck)
					{
						if (name.module.equals(n.module)
								&& name.name.equals(n.name))
						{
							return true;
						}
					}
				}

				return false;
			}

			public boolean reuiresCheck(LexNameToken name)
			{
				for (LexNameToken n : readCheck)
				{
					if (name.module.equals(n.module)
							&& name.name.equals(n.name))
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

	public Boolean start(long internalFinishTime) throws RemoteSimulationException
	{
		final List<File> files = getFiles();
		this.internalFinishTime = internalFinishTime;

		if (controller.asyncStartInterpret(files) == ExitStatus.EXIT_OK)
		{
//			this.status = CoSimStatusEnum.INITIALIZED;
			while(this.status!=CoSimStatusEnum.STARTED)
			{
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					//ignore
				}
			}
			return true;
		} else
		{
			this.status = CoSimStatusEnum.NOT_INITIALIZED;
			throw new RemoteSimulationException("Status = " + this.status
					+ ". Internal error: " + controller.exception != null ? controller.exception.getMessage()
					: "unknown");
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

		try {
			setLogVariables(simulationLogFile, new Vector<String>(variablesToLog));
		} catch (RemoteSimulationException e) {
			this.runtimeException = e;
		}
		
		if (!isSchedulingHookConfigured)
		{
			configureSchedulingHooks();
		}
		
		//show timing invariants if any
		if(Settings.timingInvChecks)
		{
			if(RuntimeValidator.validator!=null && RuntimeValidator.validator instanceof BasicRuntimeValidator)
			{
				Console.out.println("----------------------------------------------------------------------------------");
				Console.out.println("Runtime Validator Initialized with conjectures:");
				BasicRuntimeValidator validator = (BasicRuntimeValidator) RuntimeValidator.validator;
				for (ConjectureDefinition conj : validator.getConjectures())
				{
					Console.out.println("\t"+conj+"\n");
				}
				Console.out.println("----------------------------------------------------------------------------------");
			}
		}
		this.status = CoSimStatusEnum.STARTED;
	}

	protected void setLogVariables(File logFile, List<String> logVariables) throws RemoteSimulationException
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
					throw new RemoteSimulationException("Could not find variable: " + name
							+ " logging is skipped.");
				}

				if (v instanceof UpdatableValue)
				{
					UpdatableValue upVal = (UpdatableValue) v;
					final String variableLogName = name;
					ValueListener listener = new ValueListener()
					{
						final String name = variableLogName;

						public void changedValue(LexLocation location,
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
					listener.changedValue(null, upVal, null);
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
									&& vDef.isValueDefinition())
							{
								if(dimension.length == 1 && ((Integer) dimension[0] == 1))
								{
									Double newValue = (Double) objValue[0];
									found = setValueForSDP(newValue, vDef);
								}
								else
								{
									//dealing with a sequence				
									if(vDef.exp instanceof SeqEnumExpression)
									{
										SeqEnumExpression seqEnum = (SeqEnumExpression) vDef.exp;
										found = createSeqEnum(vName.variableName,seqEnum,objValue,dimension);										
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

	private boolean createSeqEnum(String variableName, SeqEnumExpression seqEnum, Object[] objValue,
			Object[] objDimensions) throws NoSuchFieldException, IllegalAccessException, RemoteSimulationException
	{
		List<Integer> dimensions = new Vector<Integer>();
		int index = 0;
		boolean found = true;
		for (Object o : objDimensions)
		{
			if(o instanceof Integer)
			{
				dimensions.add((Integer) o);
			}
		}
		
		if(dimensions.size() == 1) //it's an array
		{
			for (Expression exp : seqEnum.members)
			{
				if(exp instanceof SeqEnumExpression)
				{
					found = found && setValueForSDP((Double) objValue[index++], exp);
				}
			}
		}
		else //it's a matrix
		{
			boolean match = checkDimentionsOfSeqEnum(seqEnum,dimensions);
			if(!match)
			{
				throw new RemoteSimulationException("Dimension of \""+ variableName + "\" does not match the SDP ");
			}
			for (Expression exp : seqEnum.members)
			{
				if(exp instanceof SeqEnumExpression)
				{
					SeqEnumExpression seqEnumInner = (SeqEnumExpression) exp;
					for (Expression expInner : seqEnumInner.members)
					{
						found = found && setValueForSDP((Double) objValue[index++], expInner);
					}				
				}
			}
		}
		
		
		return found;
	}

	private boolean checkDimentionsOfSeqEnum(SeqEnumExpression seqEnum,
			List<Integer> dimensions)
	{
		if(seqEnum.members.size() != dimensions.get(0))
			return false;
		
		for (Expression exp : seqEnum.members)
		{
			if(exp instanceof SeqEnumExpression)
			{
				SeqEnumExpression seqEnumInner = (SeqEnumExpression) exp;
				if(seqEnumInner.members.size() != dimensions.get(1))
					return false;
			}
		}
		
		return true;
		
	}

	private boolean setValueForSDP( 
			Double newValue, Expression exp) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException
	{
		boolean found = false;
		
		if (exp != null && exp instanceof RealLiteralExpression)
		{
			RealLiteralExpression rExp = ((RealLiteralExpression) exp);
			LexRealToken token = rExp.value;

			Field valueField = LexRealToken.class.getField("value");
			valueField.setAccessible(true);

			valueField.setDouble(token, newValue);
			found = true;
		}
		return found;
	}
	
	private boolean setValueForSDP( 
			Double newValue, ValueDefinition vDef) throws NoSuchFieldException,
			IllegalAccessException
	{
		boolean found = false;
		
				if (vDef.exp != null && vDef.exp instanceof RealLiteralExpression)
				{
					found = setValueForSDP(newValue, vDef.exp);

				} else if (vDef.exp != null && vDef.exp instanceof IntegerLiteralExpression || vDef.exp instanceof UnaryMinusExpression)
				{
					RealLiteralExpression newReal = new RealLiteralExpression(new LexRealToken(newValue, vDef.location));
					Field valDefField = ValueDefinition.class.getField("exp");
					valDefField.setAccessible(true);
					valDefField.set(vDef, newReal);
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
								List<Double> value = new Vector<Double>();
								value.add(token.value);
								List<Integer> size = new Vector<Integer>();
								size.add(1);
								return new GetDesignParametersStructdesignParametersStruct(parameterName, value, size);
							} else if (vDef.exp instanceof SeqExpression)
							{
								throw new RemoteSimulationException("getDesignParameter with type SeqExpression not supported: "+vDef.exp);
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

			NameValuePairList list = SystemDefinition.getSystemMembers();
			if (list != null && list.size() > 0)
			{
				parameters.putAll(getParameters(list.get(0).name.module, list, 0));
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
					parameters.putAll(getParameters(prefix + p.name.name, po.members.asList(), depth++));

				} else if (p.value.deref() instanceof RealValue)
				{
					List<Double> realValue = new Vector<Double>();
					realValue.add(p.value.realValue(null));
					List<Integer> valueSize = new Vector<Integer>();
					valueSize.add(1);
					parameters.put(prefix + p.name.name, new ValueContents(realValue, valueSize));
				} else if (p.value.deref() instanceof BooleanValue)
				{
					List<Double> realValue = new Vector<Double>();
					realValue.add(p.value.boolValue(null) ? 1.0 : 0.0);
					List<Integer> valueSize = new Vector<Integer>();
					valueSize.add(1);
					parameters.put(prefix + p.name.name, new ValueContents(realValue, valueSize));
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
		for (ClassDefinition c : interpreter.getClasses())
		{
			if(c instanceof SystemDefinition)
			{
				String prefix = "System";
				for (Definition def : c.getDefinitions())
				{
					list.addAll(getLogEnabledVariables(list,prefix,def,new Vector<Definition>()));	
				}
			}
		}
		return list;
	}

	private Collection<? extends String> getLogEnabledVariables(
			final List<String> list, String prefix, Definition def,List<Definition> path)
	{
		List<String> result = new Vector<String>();
		if(def instanceof InstanceVariableDefinition && !path.contains(def))
		{
			InstanceVariableDefinition var = (InstanceVariableDefinition) def;
			String name=prefix+"."+var.getName();
			result.add(name);
			path.add(var);
			if (var.type instanceof ClassType)
			{
				result.addAll(getLogEnabledVariables(result, name, ((ClassType) var.type).classdef,path));
			}
			
		}
		
		return result;
	}

	public long getFinishTime() {
		return internalFinishTime;
	}
}
