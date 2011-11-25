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
package org.destecs.core.simulationengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.destecs.core.contract.Contract;
import org.destecs.core.contract.IVariable;
import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.simulationengine.exceptions.InvalidEndpointsExpection;
import org.destecs.core.simulationengine.exceptions.InvalidSimulationLauncher;
import org.destecs.core.simulationengine.exceptions.ModelPathNotValidException;
import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.core.simulationengine.launcher.ISimulatorLauncher;
import org.destecs.core.simulationengine.listener.IEngineListener;
import org.destecs.core.simulationengine.listener.IMessageListener;
import org.destecs.core.simulationengine.listener.IProcessCreationListener;
import org.destecs.core.simulationengine.listener.ISimulationListener;
import org.destecs.core.simulationengine.listener.ISimulationStartListener;
import org.destecs.core.simulationengine.listener.IVariableSyncListener;
import org.destecs.core.simulationengine.model.CtModelConfig;
import org.destecs.core.simulationengine.model.ModelConfig;
import org.destecs.core.simulationengine.xmlrpc.client.CustomSAXParserTransportFactory;
import org.destecs.core.xmlrpc.extensions.AnnotationClientFactory;
import org.destecs.protocol.ICoSimProtocol;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.exceptions.RemoteSimulationException;
import org.destecs.protocol.structs.GetVersionStruct;
import org.destecs.protocol.structs.Load2argumentsStructParam;
import org.destecs.protocol.structs.QueryInterfaceStruct;
import org.destecs.protocol.structs.QueryInterfaceStructinputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructoutputsStruct;
import org.destecs.protocol.structs.QueryInterfaceStructsharedDesignParametersStruct;
import org.destecs.protocol.structs.SetDesignParametersdesignParametersStructParam;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;
import org.destecs.protocol.structs.StepinputsStructParam;

public class SimulationEngine
{
	public enum Simulator
	{
		DE("VDM-RT"), CT("20-Sim"), ALL("All");

		private String name;

		private Simulator(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	public enum SynchronizationScheme
	{
		Default, Theoretical
	}

	/**
	 * Minimum required version for the CT simulator
	 */
	private static final Integer[] MIN_VERSION_CT = new Integer[] { 4, 1, 3, 8 };
	/**
	 * Minimum required version for the DE simulator
	 */
	private static final Integer[] MIN_VERSION_DE = new Integer[] { 0, 0, 0, 3 };

	/**
	 * Indicated that the class is used in the Eclipse Runtime environment. Used
	 * to change loading of SAX Parser for XML-RPC -needed since Eclipse
	 * replaced the javax.xml.parsers with an older Eclipse specific version.
	 * <b>Must be false if running standalone in console mode</b>
	 */
	public static boolean eclipseEnvironment = false;

	private URL deEndpoint = null;
	private URL ctEndpoint = null;

	private Contract contract = null;

	private ModelConfig deModelBase = null;
	private ModelConfig ctModel = null;

	private ISimulatorLauncher deLauncher = null;
	private ISimulatorLauncher ctLauncher = null;

	public final List<IEngineListener> engineListeners = new Vector<IEngineListener>();
	public final List<IMessageListener> messageListeners = new Vector<IMessageListener>();
	public final List<ISimulationListener> simulationListeners = new Vector<ISimulationListener>();
	public final List<IVariableSyncListener> variablesSyncListeners = new Vector<IVariableSyncListener>();
	public final List<ISimulationStartListener> simulationStartListeners = new Vector<ISimulationStartListener>();

	private final List<XmlRpcClient> clients = new Vector<XmlRpcClient>();

	private final List<IProcessCreationListener> processCreationListeners = new Vector<IProcessCreationListener>();

	private final File contractFile;

	private boolean forceStopSimulation = false;

	private String deVersion = "";
	private String ctVersion = "";

	private File outputDirectory = null;

	public SimulationEngine(File contractFile)
	{
		this.contractFile = contractFile;
	}

	public void setDeEndpoint(URL endpoint)
	{
		deEndpoint = endpoint;
	}

	public void setCtEndpoint(URL endpoint)
	{
		ctEndpoint = endpoint;
	}

	public void setDeModel(ModelConfig model) throws ModelPathNotValidException
	{
		if (model == null)
		{
			throw new ModelPathNotValidException(Simulator.DE, "null");
		}
		if (!model.isValid())
		{
			throw new ModelPathNotValidException(Simulator.DE, model.toString());
		}
		this.deModelBase = model;
	}

	public void setCtModel(ModelConfig model) throws ModelPathNotValidException
	{
		if (model == null)
		{
			throw new ModelPathNotValidException(Simulator.CT, "null");
		}
		if (!model.isValid())
		{
			throw new ModelPathNotValidException(Simulator.CT, model.toString());
		}
		this.ctModel = model;
	}

	public void setDeSimulationLauncher(ISimulatorLauncher launcher)
	{
		deLauncher = launcher;
	}

	public void setCtSimulationLauncher(ISimulatorLauncher launcher)
	{
		ctLauncher = launcher;
	}

	public void setOutputFolder(File output)
	{
		this.outputDirectory = output;
	}

	private void validate() throws InvalidEndpointsExpection,
			ModelPathNotValidException, InvalidSimulationLauncher,
			FileNotFoundException
	{
		if (contractFile == null || !contractFile.exists())
		{
			throw new FileNotFoundException("Contract file now found: "
					+ contractFile);
		}

		if (deEndpoint == null)
		{
			throw new InvalidEndpointsExpection(Simulator.DE, deEndpoint);
		}

		if (ctEndpoint == null)
		{
			throw new InvalidEndpointsExpection(Simulator.CT, ctEndpoint);
		}

		checkModel(Simulator.DE, deModelBase);

		checkModel(Simulator.CT, ctModel);

		if (deLauncher == null)
		{
			throw new InvalidSimulationLauncher(Simulator.DE,
					"Launcher not set");
		}

		if (ctLauncher == null)
		{
			throw new InvalidSimulationLauncher(Simulator.CT,
					"Launcher not set");
		}
	}

	private void checkModel(Simulator simulator, ModelConfig model)
			throws ModelPathNotValidException
	{
		if (model == null)
		{
			throw new ModelPathNotValidException(simulator, "null");
		} else if (!ctModel.isValid())
		{
			throw new ModelPathNotValidException(simulator, "null");
		}
	}

	public void simulate(
			List<SetDesignParametersdesignParametersStructParam> sharedDesignParameters,
			double totalSimulationTime) throws SimulationException,
			FileNotFoundException
	{
		try
		{
			// reset force simulation stop
			this.forceStopSimulation = false;
			engineInfo(Simulator.ALL, "Simulation engine type loaded: "
					+ getClass().getName());
			validate();

			infoSharedDesignParameters(sharedDesignParameters);

			contract = null;
			try
			{
				ContractParserWrapper parser = new ContractParserWrapper();
				contract = parser.parse(contractFile);
				if (parser.hasErrors())
				{
					throw new Exception("Invalid Contract - parse errors");
				}
			} catch (Exception e)
			{
				abort(Simulator.ALL,
						"Could not parse contract "
								+ contractFile.getAbsolutePath(), e);
				return;
			}

			// validate shared design parameters
			validateSharedDesignParameters(sharedDesignParameters, contract);

			// launch the simulators
			launchSimulator(Simulator.DE, deLauncher);

			launchSimulator(Simulator.CT, ctLauncher);

			// connect to the simulators
			ProxyICoSimProtocol dtProxy = connect(Simulator.DE, deEndpoint);

			initialize(Simulator.DE, dtProxy);

			ProxyICoSimProtocol ctProxy = connect(Simulator.CT, ctEndpoint);

			initialize(Simulator.CT, ctProxy);

			// Turn off timeout, simulators may be slow at loading the model
			// which should not cause a timeout. Instead
			// they should try to load and report an error if they fail.
			for (XmlRpcClient client : clients)
			{
				if (client.getClientConfig() instanceof XmlRpcClientConfigImpl)
				{
					((XmlRpcClientConfigImpl) client.getClientConfig())
							.setReplyTimeout(0);
				}
			}

			// load the models
			loadModel(Simulator.DE, dtProxy, deModelBase);

			loadModel(Simulator.CT, ctProxy, ctModel);

			// validate interfaces
			validateInterfaces(contract, dtProxy, ctProxy);

			// set variables to log
			setVariablesToLog(Simulator.CT, ctProxy, ctModel);

			// set SDPs
			setSharedDesignParameters(Simulator.DE, dtProxy,
					sharedDesignParameters);
			setSharedDesignParameters(Simulator.CT, ctProxy,
					sharedDesignParameters);

			// start simulation
			startSimulator(Simulator.DE, dtProxy);
			startSimulator(Simulator.CT, ctProxy);

			long before = System.currentTimeMillis();
			Double finishTime = simulate(totalSimulationTime, dtProxy, ctProxy);
			long after = System.currentTimeMillis();
			double totalSec = (double) (after - before) / 1000;
			engineInfo(Simulator.ALL, "Simulation executed in "
					+ (totalSec > 60 ? ((int) Math.floor(totalSec / 60)) + "."
							+ ((int) totalSec % 60) + " mins." : totalSec
							+ " secs."));

			//writeLogFiles(Simulator.CT, ctProxy);

			// stop the simulators
			stop(Simulator.DE, finishTime, dtProxy);
			stop(Simulator.CT, finishTime, ctProxy);

			terminate(Simulator.DE, finishTime, dtProxy, deLauncher);
			terminate(Simulator.CT, finishTime, ctProxy, ctLauncher);
		}
		// catch(Exception e)
		// {
		// System.out.println("Exception");
		// System.out.println(e.getMessage());
		// }
		finally
		{
			if (deLauncher.isRunning() || ctLauncher.isRunning())
			{
				sleep();
				shutdownSimulators();
			}
		}
	}

	private void writeLogFiles(Simulator simulator, ProxyICoSimProtocol proxy)
			throws SimulationException
	{
		String variablesToLog = ctModel.arguments
				.get(CtModelConfig.LOAD_SETTING_LOG_VARIABLES);

		if (variablesToLog == null)
		{
			return;
		}

		if (variablesToLog.trim().length() == 0)
		{
			engineInfo(simulator, "Writting variables to log: none");
		} else
		{
			engineInfo(simulator, "Writing variables to log");

			String[] vars = variablesToLog.split(",");
			List<String> varsList = new ArrayList<String>();
			for (String v : vars)
			{
				varsList.add(v);
			}
			engineInfo(simulator, "Setting variables to log: " + varsList.toString());
			String path = null;

			path = outputDirectory.getAbsolutePath()
					+ "\\20simVariablesCSV.log";

			try
			{
				if (!varsList.isEmpty())
				{
					proxy.writeCSVFile(path, false, varsList);
				}
			} catch (Exception e)
			{
				abort(simulator, "Failed to write CSV file", e);
			}
			return;
		}

	}

	private void setVariablesToLog(Simulator simulator,
			ProxyICoSimProtocol proxy, ModelConfig modelConfig)
			throws SimulationException
	{

		String variablesToLog = modelConfig.arguments
				.get(CtModelConfig.LOAD_SETTING_LOG_VARIABLES);

		if (variablesToLog == null)
		{
			return;
		}

		if (variablesToLog.trim().length() == 0)
		{
			engineInfo(simulator, "Setting variables to log: none");
		} else
		{
			String[] vars = variablesToLog.split(",");
			List<String> varsList = new ArrayList<String>();
			for (String v : vars)
			{
				varsList.add(v);
			}

			String path = null;
			engineInfo(simulator, "Writting variables to log: " + varsList.toString());
			path = outputDirectory.getParent() + "\\20simVariables.csv";
			//.getAbsolutePath() + "\\20simVariables.log";

			try
			{
				if (!varsList.isEmpty())
				{
					proxy.setLogVariables(path, false, varsList);
				}
			} catch (Exception e)
			{
				abort(simulator, "Failed to set log variables", e);
			}
			return;
		}

	}

	private static void sleep()
	{
		try
		{
			Thread.sleep(1000);
		} catch (InterruptedException e)
		{
			// ignore it
		}
	}

	public void shutdownSimulators()
	{
		if (deLauncher.isRunning())
		{
			deLauncher.kill();
		}
		if (ctLauncher.isRunning())
		{
			ctLauncher.kill();
		}
	}

	private void launchSimulator(Simulator simulator,
			ISimulatorLauncher launcher) throws SimulationException
	{
		engineInfo(simulator, "Launching");
		try
		{
			processCreated(launcher.getName(), launcher.launch());

		} catch (IOException e)
		{
			abort(simulator, "Failed to launch simulator", e);
		}
	}

	private void infoSharedDesignParameters(
			List<SetDesignParametersdesignParametersStructParam> sharedDesignParameters)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Shared Design Parameter initialized as: (");
		for (SetDesignParametersdesignParametersStructParam p : sharedDesignParameters)
		{
			sb.append(p.name + ":=" + p.value + " ");
		}
		sb.append(")");
		engineInfo(Simulator.ALL, sb.toString());
	}

	private boolean validateSharedDesignParameters(
			List<SetDesignParametersdesignParametersStructParam> sharedDesignParameters,
			Contract contract) throws SimulationException
	{
		for (SetDesignParametersdesignParametersStructParam p : sharedDesignParameters)
		{
			boolean found = false;
			for (IVariable var : contract.getSharedDesignParameters())
			{
				if (var.getName().endsWith(p.name))
				{
					found = true;
				}
			}
			if (!found)
			{
				abort(Simulator.ALL,
						"Shared design parameter invalid to contract: \""
								+ p.name + "\" in " + contractFile);
				return false;
			}
		}
		return true;
	}

	private void terminate(Simulator simulator, Double time,
			ProxyICoSimProtocol proxy, ISimulatorLauncher launcher)
			throws SimulationException
	{
		try
		{
			messageInfo(simulator, time, "terminate");
			engineInfo(simulator, "Terminating...");
			proxy.terminate();
		} catch (Exception e)
		{
			abort(simulator, "terminate faild", e);
		}

		engineInfo(simulator, "Terminating...kill");
		launcher.kill();
		engineInfo(simulator, "Terminating...done");
	}

	private boolean stop(Simulator simulator, Double finishTime,
			ProxyICoSimProtocol proxy) throws SimulationException
	{
		try
		{
			messageInfo(simulator, finishTime, "stop");
			return proxy.stop().success;
		} catch (Exception e)
		{
			abort(simulator, "stop faild", e);
		}
		return false;
	}

	private Double simulate(Double totalSimulationTime,
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy)
			throws SimulationException
	{
		Double initTime = 0.0;// 100.0;
		Double time = 0.0;

		engineInfo(Simulator.ALL, "Starting simulation: Time=" + initTime
				+ " -> " + totalSimulationTime + " Current=" + time);

		List<String> events = new Vector<String>();

		// First initialize DT
		StepStruct deResult = step(Simulator.DE, dtProxy, ctProxy, initTime,
				new Vector<StepinputsStructParam>(), false, events);
		StepStruct ctResult = step(Simulator.CT, dtProxy, ctProxy, initTime,
				outputToInput(deResult.outputs), false, events);

		// StepResult res = merge(deResult, ctResult);
		// System.out.print(res.toHeaderString());
		variableSyncInfo(merge(deResult, ctResult).getHeaders());
		while (time <= totalSimulationTime)
		{
			if (forceStopSimulation)
			{
				// simulation stop requested, stop simulation loop
				break;
			}
			// System.out.print(res.toString());
			// variableSyncInfo(res.getVariables());
			// Step DT - time calculate
			// deResult = step(Simulator.DE, dtProxy, ctProxy, res.time,
			// res.deData, false, res.events);

			// Step CT - step
			ctResult = step(Simulator.CT, dtProxy, ctProxy, deResult.time,
					outputToInput(deResult.outputs), false, deResult.events);
			checkStepStructVariableSize(ctResult, Simulator.CT);
			variableSyncInfo(merge(deResult, ctResult).getVariables());

			// Step DT - step
			deResult = step(Simulator.DE, dtProxy, ctProxy, ctResult.time,
					outputToInput(ctResult.outputs), false, ctResult.events);
			checkStepStructVariableSize(deResult, Simulator.DE);
			// res = merge(deResult, ctResult);

			time = deResult.time;// res.time;
		}
		return time;
	}

	private void checkStepStructVariableSize(StepStruct ctResult,
			Simulator simulator) throws SimulationException
	{

		IVariable varTarget = null;
		int varSize = -1;
		for (StepStructoutputsStruct elem : ctResult.outputs)
		{
			for (IVariable var : contract.getVariables())
			{
				if (var.getName().equals(elem.name))
				{
					varTarget = var;
					varSize = calculateSizeFromShape(varTarget.getDimensions());
					break;
				}
			}

			if (varTarget != null && elem.value.size() != varSize)
			{
				engineInfo(simulator, elem.name + " expected matrix size: "
						+ varSize + " in shape: (" + varTarget.getDimensions()
						+ ") but received: " + elem.value.size() + "elements");
				throw new SimulationException(simulator, "Variable "
						+ elem.name + " does not have the appropriate size");
			}
		}

	}

	private int calculateSizeFromShape(List<Integer> shape)
	{
		int result = 1;

		for (Integer v : shape)
		{
			result = result * v;
		}
		return result;
	}

	private StepResult merge(StepStruct deResult, StepStruct ctResult)
	{
		return new StepResult(ctResult.time, deResult.time,
				outputToInput(ctResult.outputs),
				outputToInput(deResult.outputs), ctResult.events);
	}

	public static class StepResult
	{
		// data time
		public final Double time; // from CT
		public final Double destTime; // from DE
		public final List<StepinputsStructParam> deData;// Merged variables
		public final List<StepinputsStructParam> ctData;// Merged variables
		public final List<String> events;
		public char split = ';';

		public StepResult(Double time, Double destTime,
				List<StepinputsStructParam> deData,
				List<StepinputsStructParam> ctData, List<String> events)
		{
			this.time = time;
			this.destTime = destTime;
			this.deData = deData;
			this.ctData = ctData;
			this.events = events;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			for (String col : getVariables())
			{
				sb.append(col);
				sb.append(split);
			}
			return sb.substring(0, sb.length() - 1) + "\n";
		}

		public List<String> getVariables()
		{
			List<String> list = new Vector<String>();

			list.add(time.toString());
			for (StepinputsStructParam elem : deData)
			{
				list.add(elem.value.toString());
			}
			for (StepinputsStructParam elem : ctData)
			{
				list.add(elem.value.toString());
			}
			return list;
		}

		public String toHeaderString()
		{
			StringBuilder sb = new StringBuilder();
			for (String col : getHeaders())
			{
				sb.append(col);
				sb.append(split);
			}
			return sb.substring(0, sb.length() - 1) + "\n";
		}

		public List<String> getHeaders()
		{
			List<String> list = new Vector<String>();

			list.add("Time");
			for (StepinputsStructParam elem : deData)
			{
				list.add("CT_" + elem.name);
			}
			for (StepinputsStructParam elem : ctData)
			{
				list.add("DE_" + elem.name);
			}
			return list;
		}
	}

	protected void beforeStep(Simulator nextStepEngine, Double nextTime,
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy)
			throws SimulationException
	{

	}

	public StepStruct step(Simulator simulator, ProxyICoSimProtocol dtProxy,
			ProxyICoSimProtocol ctProxy, Double outputTime,
			List<StepinputsStructParam> inputs, Boolean singleStep,
			List<String> events) throws SimulationException
	{
		beforeStep(simulator, outputTime, dtProxy, ctProxy);
		messageInfo(simulator, outputTime, "step");
		StepStruct result = null;
		try
		{
			if (simulator == Simulator.CT)
			{
				result = ctProxy.step(outputTime, inputs, singleStep, events);
			} else if (simulator == Simulator.DE)
			{
				result = dtProxy.step(outputTime, inputs, singleStep, events);
			}
		} catch (Exception e)
		{
			abort(simulator, "step failed(time = " + outputTime + " inputs=["
					+ inputs.toString().replaceAll("\n", ", ")
					+ "], singleStep = " + singleStep + ", events = " + events
					+ ")", e);
		}
		simulationInfo(simulator, result);
		return afterStep(simulator, result);
	}

	protected StepStruct afterStep(Simulator simulator, StepStruct result)
	{
		return result;
	}

	public void forceSimulationStop()
	{
		this.forceStopSimulation = true;
	}

	private List<StepinputsStructParam> outputToInput(
			List<StepStructoutputsStruct> outputs)
	{
		List<StepinputsStructParam> inputs = new Vector<StepinputsStructParam>();
		for (StepStructoutputsStruct stepStructoutputsStruct : outputs)
		{
			inputs.add(new StepinputsStructParam(stepStructoutputsStruct.name,
					stepStructoutputsStruct.value, stepStructoutputsStruct.size));
		}
		return inputs;
	}

	private boolean startSimulator(Simulator simulator,
			ProxyICoSimProtocol proxy) throws SimulationException
	{
		try
		{
			simulationStarting(simulator);
			messageInfo(simulator, new Double(0), "start");
			boolean result = proxy.start().success;

			if (result)
			{
				engineInfo(simulator, "Simulator started with no errors");
			} else
			{
				engineInfo(simulator, "Simulator FAILD to start");
			}
			return result;
		} catch (Exception e)
		{
			abort(simulator, "Could not start simulator", e);
		}
		abort(simulator, "Could not start simulator");

		return false;
	}

	private boolean setSharedDesignParameters(
			Simulator simulator,
			ProxyICoSimProtocol proxy,
			List<SetDesignParametersdesignParametersStructParam> sharedDesignParameters)
			throws SimulationException
	{
		try
		{
			messageInfo(simulator, new Double(0), "setDesignParameters");
			StringBuffer sb = new StringBuffer();
			sb.append("Setting sdp's: ");
			sb.append(getSdpString(sharedDesignParameters));

			engineInfo(simulator, sb.toString());
			return proxy.setDesignParameters(sharedDesignParameters).success;
		} catch (Exception e)
		{
			abort(simulator, "setDesignParameters failed: "
					+ getSdpString(sharedDesignParameters), e);
		}
		abort(simulator, "setDesignParameters failed: "
				+ getSdpString(sharedDesignParameters));

		return false;
	}

	private boolean validateInterfaces(Contract contract,
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy)
			throws SimulationException
	{
		QueryInterfaceStruct dtInterface = queryInterface(Simulator.DE, dtProxy);
		QueryInterfaceStruct ctInterface = queryInterface(Simulator.CT, ctProxy);

		engineInfo(Simulator.ALL, "Validating interfaces...");
		for (IVariable var : contract.getControlledVariables())
		{
			if (!interfaceContainsOuput(dtInterface, var.getName()))
			{
				abort(Simulator.DE, "Missing-output controlled variable: "
						+ var);
				return false;

			}
			if (!interfaceContainsInput(ctInterface, var.getName()))
			{
				abort(Simulator.CT, "Missing-input controlled variable: " + var);
				return false;
			}
		}

		for (IVariable var : contract.getMonitoredVariables())
		{
			if (!interfaceContainsInput(dtInterface, var.getName()))
			{
				abort(Simulator.DE, "Missing-input monitored variable: " + var);
				return false;

			}
			if (!interfaceContainsOuput(ctInterface, var.getName()))
			{
				abort(Simulator.CT, "Missing-output monitored variable: " + var);
				return false;
			}
		}

		// TODO validate shared design parameters
		// validate shared design parameters
		// if(contract.getSharedDesignParameters().size()!=
		// dtInterface.sharedDesignParameters.size())
		// {
		// abort(Simulator.DT,
		// "Count of shared design parameters do not match: Contract("+
		// contract.getSharedDesignParameters()+") actual ("+dtInterface.sharedDesignParameters+")");
		// }
		//
		// if(contract.getSharedDesignParameters().size()!=
		// ctInterface.sharedDesignParameters.size())
		// {
		// abort(Simulator.CT,
		// "Count of shared design parameters do not match: Contract("+
		// contract.getSharedDesignParameters()+") actual ("+ctInterface.sharedDesignParameters+")");
		// }
		//
		// for (Variable var : contract.getSharedDesignParameters())
		// {
		// if (!dtInterface.sharedDesignParameters.contains(var.name))
		// {
		// abort(Simulator.DT, "Missing-shared design parameter: " + var);
		// return false;
		//
		// }
		// if (!ctInterface.sharedDesignParameters.contains(var.name))
		// {
		// abort(Simulator.CT, "Missing-shared design parameter: " + var);
		// return false;
		// }
		// }

		engineInfo(Simulator.ALL, "Validating interfaces...completed");

		return true;
	}

	private boolean interfaceContainsInput(QueryInterfaceStruct interface_,
			String name)
	{
		for (QueryInterfaceStructinputsStruct input : interface_.inputs)
		{
			if (input.name.equals(name))
				return true;
		}
		return false;
	}

	private boolean interfaceContainsOuput(QueryInterfaceStruct interface_,
			String name)
	{
		for (QueryInterfaceStructoutputsStruct output : interface_.outputs)
		{
			if (output.name.equals(name))
				return true;
		}
		return false;
	}

	private QueryInterfaceStruct queryInterface(Simulator simulator,
			ProxyICoSimProtocol proxy) throws SimulationException
	{
		QueryInterfaceStruct intf = null;
		try
		{
			messageInfo(simulator, new Double(0), "queryInterface");
			intf = proxy.queryInterface();
			engineInfo(simulator, toStringInterface(intf));
		} catch (Exception e)
		{
			abort(simulator, "queryInterface failed", e);
		}
		return intf;
	}

	protected void abort(Simulator source, String reason)
			throws SimulationException
	{
		engineInfo(source, "[Abort] " + reason);
		throw new SimulationException(source, reason);
	}

	protected void abort(Simulator source, String reason, Throwable throwable)
			throws SimulationException
	{
		String extendedReason = "";
		if (throwable instanceof RemoteSimulationException)
		{
			extendedReason = " => "
					+ getXmlRpcCause((RemoteSimulationException) throwable);
		}

		if (throwable instanceof XmlRpcException)
		{
			XmlRpcException xmlException = (XmlRpcException) throwable;
			extendedReason = " => " + Integer.valueOf(xmlException.code) + ": "
					+ xmlException.getMessage();

		}
		engineInfo(source, "[Abort] " + reason + extendedReason);
		throw new SimulationException(source, reason + extendedReason,
				throwable);
	}

	private static String getXmlRpcCause(RemoteSimulationException exception)
	{

		return exception.getMessage();
	}

	private ProxyICoSimProtocol connect(Simulator simulator, URL url)
			throws SimulationException
	{
		ProxyICoSimProtocol protocolProxy = null;
		try
		{
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(url);

			// 0 sec timeout = no timeout and fixes Bert problem
			config.setReplyTimeout(000);// 5 sec time out

			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);

			if (SimulationEngine.eclipseEnvironment)
			{
				client.setTransportFactory(new CustomSAXParserTransportFactory(
						client));
			}

			clients.add(client);
			// add factory for annotations for generated protocol
			AnnotationClientFactory factory = new AnnotationClientFactory(
					client);

			ICoSimProtocol protocol = (ICoSimProtocol) factory
					.newInstance(ICoSimProtocol.class);

			protocolProxy = new ProxyICoSimProtocol(protocol);
		} catch (Exception e)
		{
			abort(simulator, "Connect faild to: " + url, e);
		}
		return protocolProxy;
	}

	private boolean initialize(Simulator simulator, ProxyICoSimProtocol proxy)
			throws SimulationException
	{
		try
		{
			messageInfo(simulator, new Double(0), "getVersion");
			GetVersionStruct version = proxy.getVersion();
			engineInfo(simulator, "Interface Version: " + version);

			switch (simulator)
			{
			case ALL:
				break;
			case CT:
				ctVersion = version.version.trim();
				break;
			case DE:
				deVersion = version.version.trim();
				break;

			}
		} catch (Exception e)
		{
			abort(simulator, "getVersion failed", e);
		}

		switch (simulator)
		{
		case CT:
			versionCheck(simulator, ctVersion, MIN_VERSION_CT);
			break;
		case DE:
			versionCheck(simulator, deVersion, MIN_VERSION_DE);
			break;

		}

		if (simulator == Simulator.CT
				&& !versionCheck(simulator, ctVersion, MIN_VERSION_CT))
		{
			abort(simulator, "Simulator version not supported: " + ctVersion
					+ " <> expected " + MIN_VERSION_CT);
		}

		try
		{
			messageInfo(simulator, new Double(0), "initialize");
			boolean initializedOk = proxy.initialize().success;
			engineInfo(simulator, "Initilized ok: " + initializedOk);
			return initializedOk;
		} catch (Exception e)
		{
			abort(simulator, "initialize failed", e);
		}
		abort(simulator, "Could not initialize");
		return false;
	}

	private boolean loadModel(Simulator simulator, ProxyICoSimProtocol proxy,
			ModelConfig model) throws SimulationException
	{
		// FIXME: This should be fixed we should not have two different load
		// methods in the protocol.
		if (simulator == Simulator.DE
				&& (deVersion.equals("0.0.0.2") || deVersion.equals("0.0.0.3")))
		{
			try
			{
				engineInfo(simulator, "Loading model: " + model);
				messageInfo(simulator, new Double(0), "load");
				List<Load2argumentsStructParam> arguments = new Vector<Load2argumentsStructParam>();
				for (Entry<String, String> entry : model.arguments.entrySet())
				{
					arguments.add(new Load2argumentsStructParam(entry
							.getValue(), entry.getKey()));
				}

				boolean success = proxy.load2(
						outputDirectory.getAbsolutePath(), arguments).success;
				engineInfo(simulator,
						"Loading model completed with no errors: " + success);
				return success;
			} catch (Exception e)
			{
				abort(simulator, "Could not load model", e);
			}
			abort(simulator, "Could not load model: " + model);
		} else
		{
			String absolutePath = new File(model.arguments.values().iterator()
					.next()).getAbsolutePath();
			try
			{
				engineInfo(simulator, "Loading model: " + model);
				messageInfo(simulator, new Double(0), "load");
				boolean success = proxy.load(absolutePath).success;
				engineInfo(simulator,
						"Loading model completed with no errors: " + success);
				return success;
			} catch (Exception e)
			{
				abort(simulator, "Could not load model: " + absolutePath, e);
			}
			abort(simulator, "Could not load model: " + absolutePath);
		}
		return false;
	}

	protected void engineInfo(Simulator simulator, String message)
	{
		for (IEngineListener listener : engineListeners)
		{
			listener.info(simulator, message);
		}
	}

	protected void messageInfo(Simulator fromSimulator, Double time,
			String message)
	{
		for (IMessageListener listener : messageListeners)
		{
			listener.from(fromSimulator, time, message);
		}
	}

	protected void simulationInfo(Simulator fromSimulator, StepStruct result)
	{

		for (ISimulationListener listener : simulationListeners)
		{
			listener.stepInfo(fromSimulator, result);
		}
	}

	protected void variableSyncInfo(List<String> colls)
	{

		for (IVariableSyncListener listener : variablesSyncListeners)
		{
			listener.info(colls);
		}
	}

	protected void simulationStarting(Simulator fromSimulator)
	{

		for (ISimulationStartListener listener : simulationStartListeners)
		{
			listener.simulationStarting(fromSimulator);
		}
	}

	public synchronized void addProcessCreationListener(
			IProcessCreationListener listener)
	{
		processCreationListeners.add(listener);
	}

	protected synchronized void processCreated(String name, Process p)
	{
		if (p != null)
		{
			for (IProcessCreationListener listener : processCreationListeners)
			{
				listener.processCreated(name, p);
			}
		}
	}

	public static String toStringInterface(QueryInterfaceStruct result)
	{
		StringBuilder sb = new StringBuilder();
		if (!eclipseEnvironment)
		{

			sb.append("\n_____________________________\n");
			sb.append("|\tInterface\n");
			sb.append("|----------------------------\n");

			sb.append("|  Shared Design Parameters\n");
			sb.append("|\n");
			if (result.sharedDesignParameters.size() > 0)
			{
				for (QueryInterfaceStructsharedDesignParametersStruct p : result.sharedDesignParameters)
				{
					sb.append("|    " + p.name/* p.name + " : " + p.value */
							+ "\n");
				}
			} else
			{
				sb.append("|    None.\n");
			}
			sb.append("|----------------------------\n");
			sb.append("|  Input Variables\n");
			sb.append("|\n");
			if (result.inputs.size() > 0)
			{
				for (QueryInterfaceStructinputsStruct p : result.inputs)
				{
					sb.append("|    " + p.name /* p.name + " : " + p.value */
							+ "\n");
				}
			} else
			{
				sb.append("|    None.\n");
			}
			sb.append("|----------------------------\n");
			sb.append("|  Output Variables\n");
			sb.append("|\n");
			if (result.outputs.size() > 0)
			{
				for (QueryInterfaceStructoutputsStruct p : result.outputs)
				{
					sb.append("|    " + p.name/* p.name + " : " + p.value */
							+ "\n");
				}
			} else
			{
				sb.append("|    None.\n");
			}
			sb.append("_____________________________");
		} else
		{
			sb.append("");
			sb.append("Interface => ");

			sb.append("SDP( ");

			if (result.sharedDesignParameters.size() > 0)
			{
				for (QueryInterfaceStructsharedDesignParametersStruct p : result.sharedDesignParameters)
				{
					sb.append("" + p.name/* p.name + " : " + p.value */+ ", ");
				}
			} else
			{
				sb.append("- ");
			}

			sb.append(") Inputs( ");

			if (result.inputs.size() > 0)
			{
				for (QueryInterfaceStructinputsStruct p : result.inputs)
				{
					sb.append("" + p.name /* p.name + " : " + p.value */+ ", ");
				}
			} else
			{
				sb.append("-  ");
			}

			sb.append(") Outputs( ");

			if (result.outputs.size() > 0)
			{
				for (QueryInterfaceStructoutputsStruct p : result.outputs)
				{
					sb.append("" + p.name/* p.name + " : " + p.value */+ ", ");
				}
			} else
			{
				sb.append("- ");
			}
			sb.append(")");
		}
		return (sb.toString());
	}

	private static String getSdpString(
			List<SetDesignParametersdesignParametersStructParam> sharedDesignParameters)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (SetDesignParametersdesignParametersStructParam sdp : sharedDesignParameters)
		{
			sb.append(sdp.name + " = " + sdp.value);
			sb.append(", ");
		}
		if (sb.toString().endsWith(", "))
		{
			sb = sb.delete(sb.length() - 2, sb.length());
		}
		sb.append("]");
		return sb.toString().trim();
	}

	public boolean versionCheck(Simulator simulator, String version,
			Integer... number) throws SimulationException
	{
		try
		{
			String[] elements = version.split("\\.");
			for (int i = 0; i < number.length; i++)
			{
				Integer num = number[i];

				if (i < elements.length)
				{
					if (!(Integer.valueOf(elements[i]) >= num))
					{
						abort(simulator, "Simulator version not supported: "
								+ version + " <> expected " + number);
					}
				} else
				{
					abort(simulator, "Simulator version not supported: "
							+ version + " <> expected " + number);
				}
			}
			return true;
		} catch (NumberFormatException e)
		{
			abort(simulator, "Failed to parse version number: " + version
					+ " tried to check for version: " + number);
		}
		return true;
	}

	public void debug(boolean enable)
	{
		if (enable)
		{
			Logger log = Logger
					.getLogger("org.destecs.core.simulationengine.xmlrpc.client.CustomSAXParserTransportFactory$XmlRpcSun15HttpTransportCustomSAXReader");
			log.addAppender(new ConsoleAppender(new SimpleLayout()));
			try
			{
				log.addAppender(new WriterAppender(new SimpleLayout(),
						new FileWriter(new File(outputDirectory,
								"log_xmlrpc.txt"))));
			} catch (IOException e)
			{
			}

			log.setLevel(Level.DEBUG);
		}
	}
}
