package org.destecs.core.simulationengine;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.destecs.core.simulationengine.exceptions.InvalidEndpointsExpection;
import org.destecs.core.simulationengine.exceptions.InvalidSimulationLauncher;
import org.destecs.core.simulationengine.exceptions.ModelPathNotValidException;
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
		DT("VDM-RT"), CT("20-Sim"), ALL("All");

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

	private URL dtEndpoint = null;
	private URL ctEndpoint = null;

	private File dtModelBase = null;
	private File ctModel = null;

	private ISimulatorLauncher dtLauncher = null;
	private ISimulatorLauncher ctLauncher = null;

	public final List<IEngineListener> engineListeners = new Vector<IEngineListener>();
	public final List<IMessageListener> messageListeners = new Vector<IMessageListener>();
	public final List<ISimulationListener> simulationListeners = new Vector<ISimulationListener>();

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
			throw new ModelPathNotValidException("null");
		}
		if (!model.exists())
		{
			throw new ModelPathNotValidException(model.toString());
		}
		this.dtModelBase = model;
	}

	public void setCtModel(File model) throws ModelPathNotValidException
	{
		if (model == null)
		{
			throw new ModelPathNotValidException("null");
		}
		if (!model.exists() || model.isDirectory())
		{
			throw new ModelPathNotValidException(model.toString());
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
			ModelPathNotValidException, InvalidSimulationLauncher
	{
		if (dtEndpoint == null || ctEndpoint == null)
		{
			throw new InvalidEndpointsExpection();
		}

		if (dtModelBase == null || !dtModelBase.exists())
		{
			throw new ModelPathNotValidException(dtModelBase.toString());
		}

		if (ctModel == null || !ctModel.exists())
		{
			throw new ModelPathNotValidException(ctModel.toString());
		}

		if (dtLauncher == null)
		{
			throw new InvalidSimulationLauncher("DT Launcher not set");
		}

		if (ctLauncher == null)
		{
			throw new InvalidSimulationLauncher("CT Launcher not set");
		}
	}

	public void simulate(
			List<SetDesignParametersdesignParametersStructParam> sharedDesignParameters,
			float totalSimulationTime) throws InvalidEndpointsExpection,
			ModelPathNotValidException, InvalidSimulationLauncher
	{
		validate();

		// launch the simulators

		// connect to the simulators
		ProxyICoSimProtocol dtProxy = connect(dtEndpoint);

		if (!initialize(Simulator.DT, dtProxy))
		{
			terminate(Simulator.DT);
		}

		ProxyICoSimProtocol ctProxy = connect(ctEndpoint);

		if (!initialize(Simulator.CT, ctProxy))
		{
			terminate(Simulator.CT);
		}

		// load the models
		if (!loadModel(Simulator.DT, dtProxy, dtModelBase))
		{
			terminate(Simulator.DT);
		}

		if (!loadModel(Simulator.CT, ctProxy, ctModel))
		{
			terminate(Simulator.CT);
		}

		// validate interfaces
		if (!valideteInterfaces(dtProxy, ctProxy))
		{
			terminate(Simulator.ALL);
		}

		// validate shared design parameters
		if (!validateSharedDesignParameters(sharedDesignParameters))
		{
			terminate(Simulator.ALL);
		}

		if (!setSharedDesignParameters(Simulator.DT, dtProxy, sharedDesignParameters))
		{
			terminate(Simulator.DT);
		}

		// if(!setSharedDesignParameters(Simulator.CT,ctProxy,sharedDesignParameters))
		// {
		// terminate(Simulator.CT);
		// }

		// start simulation
		if (!startSimulator(Simulator.DT, dtProxy))
		{
			terminate(Simulator.DT);
		}

		if (!startSimulator(Simulator.CT, ctProxy))
		{
			terminate(Simulator.CT);
		}

		simulate(totalSimulationTime, dtProxy, ctProxy);

		// stop the simulators

		stop(Simulator.DT, dtProxy);
		// stop(Simulator.CT,ctProxy);

		terminate(Simulator.DT, dtProxy, dtLauncher);
		terminate(Simulator.CT, ctProxy, ctLauncher);

	}

	private void terminate(Simulator simulator, ProxyICoSimProtocol proxy,
			ISimulatorLauncher launcher)
	{
		messageInfo(Simulator.DT, "terminate");
		engineInfo(simulator, "Terminating...");
		proxy.terminate();
		engineInfo(simulator, "Terminating...kill");
		launcher.kill();
		engineInfo(simulator, "Terminating...done");
	}

	private boolean stop(Simulator simulator, ProxyICoSimProtocol proxy)
	{
		messageInfo(simulator, "stop");
		return proxy.stop().success;
	}

	private void simulate(float totalSimulationTime,
			ProxyICoSimProtocol dtProxy, ProxyICoSimProtocol ctProxy)
	{
		Double initTime = 100.0;
		Double time = 0.0;

		List<String> events = new Vector<String>();

		// First initialize DT
		messageInfo(Simulator.DT, "step");
		StepStruct result = dtProxy.step(initTime, new Vector<StepinputsStructParam>(), false, events);
		simulationInfo(Simulator.DT, result);

		while (time < 5)
		{
			// Step CT
			messageInfo(Simulator.CT, "step");
			result = ctProxy.step((result.time) / 1000, outputToInput(result.outputs), false, events);
			simulationInfo(Simulator.CT, result);
			// Step DT
			messageInfo(Simulator.DT, "step");
			result = dtProxy.step(((result.time) * 1000) + 5, outputToInput(result.outputs), false, events);
			simulationInfo(Simulator.DT, result);

			time = (result.time) / 1000;
		}
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
			ProxyICoSimProtocol proxy)
	{
		messageInfo(simulator, "start");
		return proxy.start().success;
	}

	private boolean setSharedDesignParameters(
			Simulator simulator,
			ProxyICoSimProtocol proxy,
			List<SetDesignParametersdesignParametersStructParam> sharedDesignParameters)
	{
		messageInfo(simulator, "setDesignParameters");
		boolean success = proxy.setDesignParameters(sharedDesignParameters).success;

		return success;
	}

	private boolean valideteInterfaces(ProxyICoSimProtocol dtProxy,
			ProxyICoSimProtocol ctProxy)
	{
		messageInfo(Simulator.DT, "queryInterface");
		QueryInterfaceStruct dtInterface = dtProxy.queryInterface();
		engineInfo(Simulator.DT, toStringInterface(dtInterface));

		messageInfo(Simulator.CT, "queryInterface");
		QueryInterfaceStruct ctInterface = ctProxy.queryInterface();
		engineInfo(Simulator.CT, toStringInterface(ctInterface));

		engineInfo(Simulator.ALL, "Validating interfaces... - Skipped");

		return true;
	}

	private void terminate(Simulator source)
	{
		engineInfo(source, "Simulation abourted.");

	}

	private ProxyICoSimProtocol connect(URL url)
	{
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(url);

		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);

		// add factory for annotations for generated protocol
		AnnotationClientFactory factory = new AnnotationClientFactory(client);

		ICoSimProtocol protocol = (ICoSimProtocol) factory.newInstance(ICoSimProtocol.class);

		ProxyICoSimProtocol protocolProxy = new ProxyICoSimProtocol(protocol);

		return protocolProxy;
	}

	private boolean initialize(Simulator simulator, ProxyICoSimProtocol proxy)
	{
		messageInfo(simulator, "getVersion");
		engineInfo(simulator, "Interface Version: " + proxy.getVersion());

		messageInfo(simulator, "initialize");
		boolean initializedOk = proxy.initialize().success;
		engineInfo(simulator, "Initilized ok: " + initializedOk);

		return initializedOk;
	}

	private boolean loadModel(Simulator simulator, ProxyICoSimProtocol proxy,
			File model)
	{
		engineInfo(simulator, "Loading model: " + model);
		messageInfo(simulator, "load");
		boolean success = proxy.load(model.getAbsolutePath()).success;
		engineInfo(simulator, "Loading model completed with no errors: "
				+ success);
		return success;
	}

	private void engineInfo(Simulator simulator, String message)
	{
		for (IEngineListener listener : engineListeners)
		{
			listener.info(simulator, message);
		}
	}

	private void messageInfo(Simulator fromSimulator, String message)
	{
		for (IMessageListener listener : messageListeners)
		{
			listener.from(fromSimulator, message);
		}
	}

	private void simulationInfo(Simulator fromSimulator, StepStruct result)
	{

		for (ISimulationListener listener : simulationListeners)
		{
			listener.stepInfo(fromSimulator, result);
		}
	}

	private boolean validateSharedDesignParameters(
			List<SetDesignParametersdesignParametersStructParam> sharedDesignParameters)
	{
		for (SetDesignParametersdesignParametersStructParam parameter : sharedDesignParameters)
		{
			engineInfo(Simulator.ALL, parameter.name + ": " + parameter.value);
		}
		return true;
	}

	public static String toStringInterface(QueryInterfaceStruct result)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("_____________________________\n");
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
		return (sb.toString());
	}
}
