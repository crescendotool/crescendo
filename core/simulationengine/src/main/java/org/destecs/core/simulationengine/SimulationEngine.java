package org.destecs.core.simulationengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.destecs.core.contract.Contract;

import org.destecs.core.contract.Variable;
import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.simulationengine.exceptions.InvalidEndpointsExpection;
import org.destecs.core.simulationengine.exceptions.InvalidSimulationLauncher;
import org.destecs.core.simulationengine.exceptions.ModelPathNotValidException;
import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.core.xmlrpc.extensions.AnnotationClientFactory;
import org.destecs.protocol.ICoSimProtocol;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.structs.QueryInterfaceStruct;
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

	/**
	 * Indicated that the class is used in the Eclipse Runtime environment. Used to change loading of SAX Parser for
	 * XML-RPC -needed since Eclipse replaced the javax.xml.parsers with an older Eclipse specific version. <b>Must be
	 * false if running standalone in console mode</b>
	 */
	public static boolean eclipseEnvironment = false;

	private URL dtEndpoint = null;
	private URL ctEndpoint = null;

	private File dtModelBase = null;
	private File ctModel = null;

	private ISimulatorLauncher dtLauncher = null;
	private ISimulatorLauncher ctLauncher = null;

	public final List<IEngineListener> engineListeners = new Vector<IEngineListener>();
	public final List<IMessageListener> messageListeners = new Vector<IMessageListener>();
	public final List<ISimulationListener> simulationListeners = new Vector<ISimulationListener>();
	
	private final List<XmlRpcClient> clients= new Vector<XmlRpcClient>();

	private final List<IProcessCreationListener> processCreationListeners = new Vector<IProcessCreationListener>();

	private final File contractFile;

	private boolean forceStopSimulation = false;

	public SimulationEngine(File contractFile)
	{
		this.contractFile = contractFile;
	}

	public void setDtEndpoint(URL endpoint)
	{
		dtEndpoint = endpoint;
	}

	public void setCtEndpoint(URL endpoint)
	{
		ctEndpoint = endpoint;
	}

	public void setDtModel(File model) throws ModelPathNotValidException
	{
		if (model == null)
		{
			throw new ModelPathNotValidException(Simulator.DE, "null");
		}
		if (!model.exists())
		{
			throw new ModelPathNotValidException(Simulator.DE, model.toString());
		}
		this.dtModelBase = model;
	}

	public void setCtModel(File model) throws ModelPathNotValidException
	{
		if (model == null)
		{
			throw new ModelPathNotValidException(Simulator.CT, "null");
		}
		if (!model.exists() || model.isDirectory())
		{
			throw new ModelPathNotValidException(Simulator.CT, model.toString());
		}
		this.ctModel = model;
	}

	public void setDtSimulationLauncher(ISimulatorLauncher launcher)
	{
		dtLauncher = launcher;
	}

	public void setCtSimulationLauncher(ISimulatorLauncher launcher)
	{
		ctLauncher = launcher;
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

		if (dtEndpoint == null)
		{
			throw new InvalidEndpointsExpection(Simulator.DE, dtEndpoint);
		}

		if (ctEndpoint == null)
		{
			throw new InvalidEndpointsExpection(Simulator.CT, ctEndpoint);
		}

		checkModel(Simulator.DE, dtModelBase);

		checkModel(Simulator.CT, ctModel);

		if (dtLauncher == null)
		{
			throw new InvalidSimulationLauncher(Simulator.DE, "Launcher not set");
		}

		if (ctLauncher == null)
		{
			throw new InvalidSimulationLauncher(Simulator.CT, "Launcher not set");
		}
	}

	private void checkModel(Simulator simulator, File model)
			throws ModelPathNotValidException
	{
		if (model == null)
		{
			throw new ModelPathNotValidException(simulator, "null");
		} else if (!ctModel.exists())
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

			Contract contract = null;
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
				abort(Simulator.ALL, "Could not parse contract "
						+ contractFile.getAbsolutePath(), e);
				return;
			}

			// validate shared design parameters
			validateSharedDesignParameters(sharedDesignParameters, contract);

			// launch the simulators
			launchSimulator(Simulator.DE, dtLauncher);

			launchSimulator(Simulator.CT, ctLauncher);

			// connect to the simulators
			ProxyICoSimProtocol dtProxy = connect(Simulator.DE, dtEndpoint);

			initialize(Simulator.DE, dtProxy);

			ProxyICoSimProtocol ctProxy = connect(Simulator.CT, ctEndpoint);

			initialize(Simulator.CT, ctProxy);
			
			//Turn off timeout, simulators may be slow at loading the model which should not cause a timeout. Instead they should try to load and report an error if they fail.
			for (XmlRpcClient client : clients)
			{
				if(client.getClientConfig() instanceof XmlRpcClientConfigImpl)
				{
					((XmlRpcClientConfigImpl)client.getClientConfig()).setReplyTimeout(0);
				}
			}

			// load the models
			loadModel(Simulator.DE, dtProxy, dtModelBase);

			loadModel(Simulator.CT, ctProxy, ctModel);

			// validate interfaces
			valideteInterfaces(contract, dtProxy, ctProxy);

			// set SDPs
			setSharedDesignParameters(Simulator.DE, dtProxy, sharedDesignParameters);
			setSharedDesignParameters(Simulator.CT, ctProxy, sharedDesignParameters);

			// start simulation
			startSimulator(Simulator.DE, dtProxy);
			startSimulator(Simulator.CT, ctProxy);

			Double finishTime = simulate(totalSimulationTime, dtProxy, ctProxy);

			// stop the simulators
			stop(Simulator.DE, finishTime, dtProxy);
			// TODO: stop(Simulator.CT, finishTime, ctProxy);

			terminate(Simulator.DE, finishTime, dtProxy, dtLauncher);
			terminate(Simulator.CT, finishTime, ctProxy, ctLauncher);
		} finally
		{
			if (dtLauncher.isRunning() || ctLauncher.isRunning())
			{
				sleep();
				shutdownSimulators();
			}
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
		if (dtLauncher.isRunning())
		{
			dtLauncher.kill();
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
			for (Variable var : contract.getSharedDesignParameters())
			{
				if (var.name.endsWith(p.name))
				{
					found = true;
				}
			}
			if (!found)
			{
				abort(Simulator.ALL, "Shared design parameter invalid to contract: \""
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
		} catch (UndeclaredThrowableException undeclaredException)
		{
			abort(simulator, "terminate faild", undeclaredException);
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
		} catch (UndeclaredThrowableException undeclaredException)
		{
			abort(simulator, "stop faild", undeclaredException);
		}
		return false;
	}

	private Double simulate(Double totalSimulationTime,
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy)
			throws SimulationException
	{
		Double initTime = 0.0;// 100.0;
		Double time = 0.0;

		engineInfo(Simulator.ALL, "Running simulation: InitialTime=" + initTime
				+ " CurrentTime=" + time);

		List<String> events = new Vector<String>();

		// First initialize DT
		StepStruct result = step(Simulator.DE, dtProxy, ctProxy, initTime, new Vector<StepinputsStructParam>(), false, events);

		while (time <= totalSimulationTime)
		{
			if (forceStopSimulation)
			{
				// simulation stop requested, stop simulation loop
				break;
			}
			// Step CT
			result = step(Simulator.CT, dtProxy, ctProxy, result.time, outputToInput(result.outputs), false, result.events);

			// Step DT
			result = step(Simulator.DE, dtProxy, ctProxy, result.time, outputToInput(result.outputs), false, result.events);

			time = result.time;
		}
		return time;
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
		} catch (UndeclaredThrowableException undeclaredException)
		{
			abort(simulator, "step failed(time = " + outputTime + " inputs=["
					+ inputs.toString().replaceAll("\n", ", ")
					+ "], singleStep = " + singleStep + ", events = " + events
					+ ")", undeclaredException);
		}
		simulationInfo(simulator, result);
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
			inputs.add(new StepinputsStructParam(stepStructoutputsStruct.name, stepStructoutputsStruct.value));
		}
		return inputs;
	}

	private boolean startSimulator(Simulator simulator,
			ProxyICoSimProtocol proxy) throws SimulationException
	{
		try
		{
			messageInfo(simulator, new Double(0), "start");
			return proxy.start().success;
		} catch (UndeclaredThrowableException undeclaredException)
		{
			abort(simulator, "Could not start simulator", undeclaredException);
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
		} catch (UndeclaredThrowableException undeclaredException)
		{
			abort(simulator, "setDesignParameters failed: "
					+ getSdpString(sharedDesignParameters), undeclaredException);
		}
		abort(simulator, "setDesignParameters failed: "
				+ getSdpString(sharedDesignParameters));

		return false;
	}

	private boolean valideteInterfaces(Contract contract,
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy)
			throws SimulationException
	{
		QueryInterfaceStruct dtInterface = queryInterface(Simulator.DE, dtProxy);
		QueryInterfaceStruct ctInterface = queryInterface(Simulator.CT, ctProxy);

		engineInfo(Simulator.ALL, "Validating interfaces...");
		for (Variable var : contract.getControlledVariables())
		{
			if (!dtInterface.outputs.contains(var.name))
			{
				abort(Simulator.DE, "Missing-output controlled variable: "
						+ var);
				return false;

			}
			if (!ctInterface.inputs.contains(var.name))
			{
				abort(Simulator.CT, "Missing-input controlled variable: " + var);
				return false;
			}
		}

		for (Variable var : contract.getMonitoredVariables())
		{
			if (!dtInterface.inputs.contains(var.name))
			{
				abort(Simulator.DE, "Missing-input monitored variable: " + var);
				return false;

			}
			if (!ctInterface.outputs.contains(var.name))
			{
				abort(Simulator.CT, "Missing-output monitored variable: " + var);
				return false;
			}
		}

		// TODO validate shared design parameters
		// validate shared design parameters
		// if(contract.getSharedDesignParameters().size()!= dtInterface.sharedDesignParameters.size())
		// {
		// abort(Simulator.DT, "Count of shared design parameters do not match: Contract("+
		// contract.getSharedDesignParameters()+") actual ("+dtInterface.sharedDesignParameters+")");
		// }
		//		
		// if(contract.getSharedDesignParameters().size()!= ctInterface.sharedDesignParameters.size())
		// {
		// abort(Simulator.CT, "Count of shared design parameters do not match: Contract("+
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

	private QueryInterfaceStruct queryInterface(Simulator simulator,
			ProxyICoSimProtocol proxy) throws SimulationException
	{
		QueryInterfaceStruct intf = null;
		try
		{
			messageInfo(simulator, new Double(0), "queryInterface");
			intf = proxy.queryInterface();
			engineInfo(simulator, toStringInterface(intf));
		} catch (UndeclaredThrowableException undeclaredException)
		{
			abort(simulator, "queryInterface failed", undeclaredException);
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
		if (throwable instanceof UndeclaredThrowableException)
		{
			extendedReason = " => "
					+ getXmlRpcCause((UndeclaredThrowableException) throwable);
		}
		engineInfo(source, "[Abort] " + reason + extendedReason);
		throw new SimulationException(source, reason + extendedReason, throwable);
	}

	private static String getXmlRpcCause(UndeclaredThrowableException exception)
	{
		if (exception != null
				&& exception.getCause() instanceof XmlRpcException)
		{
			XmlRpcException cause = (XmlRpcException) exception.getCause();
			return Integer.valueOf(cause.code) + ": " + cause.getMessage();
		}
		return "";
	}

	private ProxyICoSimProtocol connect(Simulator simulator, URL url)
			throws SimulationException
	{
		ProxyICoSimProtocol protocolProxy = null;
		try
		{
			XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
			config.setServerURL(url);
			config.setReplyTimeout(5000);// 5 sec time out

			XmlRpcClient client = new XmlRpcClient();
			client.setConfig(config);

			if (SimulationEngine.eclipseEnvironment)
			{
				client.setTransportFactory(new CustomSAXParserTransportFactory(client));
			}
			
			clients.add(client);
			// add factory for annotations for generated protocol
			AnnotationClientFactory factory = new AnnotationClientFactory(client);

			ICoSimProtocol protocol = (ICoSimProtocol) factory.newInstance(ICoSimProtocol.class);

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
			engineInfo(simulator, "Interface Version: " + proxy.getVersion());
		} catch (UndeclaredThrowableException undeclaredException)
		{
			abort(simulator, "getVersion failed", undeclaredException);
		}

		try
		{
			messageInfo(simulator, new Double(0), "initialize");
			boolean initializedOk = proxy.initialize().success;
			engineInfo(simulator, "Initilized ok: " + initializedOk);
			return initializedOk;
		} catch (UndeclaredThrowableException undeclaredException)
		{
			abort(simulator, "initialize failed", undeclaredException);
		}
		abort(simulator, "Could not initialize");
		return false;
	}

	private boolean loadModel(Simulator simulator, ProxyICoSimProtocol proxy,
			File model) throws SimulationException
	{
		try
		{
			engineInfo(simulator, "Loading model: " + model);
			messageInfo(simulator, new Double(0), "load");
			boolean success = proxy.load(model.getAbsolutePath()).success;
			engineInfo(simulator, "Loading model completed with no errors: "
					+ success);
			return success;
		} catch (UndeclaredThrowableException undeclaredException)
		{
			abort(simulator, "Could not load model: " + model.getAbsolutePath(), undeclaredException);
		}
		abort(simulator, "Could not load model: " + model.getAbsolutePath());

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
				for (String p : result.sharedDesignParameters)
				{
					sb.append("|    " + p/* p.name + " : " + p.value */+ "\n");
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
				for (String p : result.inputs)
				{
					sb.append("|    " + p /* p.name + " : " + p.value */+ "\n");
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
				for (String p : result.outputs)
				{
					sb.append("|    " + p/* p.name + " : " + p.value */+ "\n");
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
				for (String p : result.sharedDesignParameters)
				{
					sb.append("" + p/* p.name + " : " + p.value */+ ", ");
				}
			} else
			{
				sb.append("- ");
			}

			sb.append(") Inputs( ");

			if (result.inputs.size() > 0)
			{
				for (String p : result.inputs)
				{
					sb.append("" + p /* p.name + " : " + p.value */+ ", ");
				}
			} else
			{
				sb.append("-  ");
			}

			sb.append(") Outputs( ");

			if (result.outputs.size() > 0)
			{
				for (String p : result.outputs)
				{
					sb.append("" + p/* p.name + " : " + p.value */+ ", ");
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
}
