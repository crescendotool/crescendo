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

	private List<IEngineListener> engineListeners = new Vector<IEngineListener>();
	private List<IMessageListener> messageListeners = new Vector<IMessageListener>();
	private List<ISimulationListener> simulationListeners = new Vector<ISimulationListener>();

	public SimulationEngine()
	{

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

	public void simulate() throws InvalidEndpointsExpection,
			ModelPathNotValidException, InvalidSimulationLauncher
	{
		validate();

		// launch the simulators

		// connect to the simulators
		ProxyICoSimProtocol dtProxy = connect(dtEndpoint);
		
		if(!initialize(Simulator.DT,dtProxy))
		{
			terminate(Simulator.DT);
		}
		
		ProxyICoSimProtocol ctProxy = connect(ctEndpoint);
		
		if(!initialize(Simulator.CT,ctProxy))
		{
			terminate(Simulator.CT);
		}
		
		//load the models
		if(!loadModel(Simulator.DT, dtProxy, dtModelBase))
		{
			terminate(Simulator.DT);
		}
		
		if(!loadModel(Simulator.CT, ctProxy, ctModel))
		{
			terminate(Simulator.CT);
		}
		
		//validate interfaces
		if(!valideteInterfaces(dtProxy,ctProxy))
		{
			terminate(Simulator.ALL);
		}
		
	}

	private boolean valideteInterfaces(ProxyICoSimProtocol dtProxy,
			ProxyICoSimProtocol ctProxy)
	{
		messageInfo(Simulator.DT, "queryInterface");
		QueryInterfaceStruct dtInterface = dtProxy.queryInterface();
		
		messageInfo(Simulator.CT, "queryInterface");
		QueryInterfaceStruct ctInterface = ctProxy.queryInterface();
		
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
	
	private boolean loadModel(Simulator simulator, ProxyICoSimProtocol proxy, File model)
	{
		engineInfo(simulator, "Loading model: "+ model);
		messageInfo(simulator, "load");
		boolean success =proxy.load(model.getAbsolutePath()).success;
		engineInfo(simulator, "Loading model completed with no errors: "+ success);
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

	private void simulationInfo(Simulator fromSimulator, float timestamp,
			float desiredTime, List<StepinputsStructParam> variables)
	{
		for (ISimulationListener listener : simulationListeners)
		{
			listener.stepInfo(fromSimulator, timestamp, desiredTime, variables);
		}
	}

}
