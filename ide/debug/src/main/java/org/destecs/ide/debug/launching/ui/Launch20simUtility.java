package org.destecs.ide.debug.launching.ui;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import org.destecs.core.simulationengine.Clp20SimUtility;
import org.destecs.core.simulationengine.model.CtModelConfig;
import org.destecs.ide.simeng.internal.core.Clp20SimProgramLauncher;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.structs.LoadpropertiesStructParam;

public class Launch20simUtility {
	

	public Launch20simUtility() {
	}
	
	public static ProxyICoSimProtocol launch20sim(File ctFile, String ctUrl) throws Exception
	{
		/*
		 * Connecting to 20sim
		 */
		Clp20SimProgramLauncher clp20sim = new Clp20SimProgramLauncher(ctFile);
		clp20sim.launch();
		ProxyICoSimProtocol protocol = Clp20SimUtility.connect(new URL(ctUrl));
		
		CtModelConfig model = new CtModelConfig(ctFile.getAbsolutePath());
		List<LoadpropertiesStructParam> arguments = new Vector<LoadpropertiesStructParam>();
		for (Entry<String, String> entry : model.arguments.entrySet())
		{
			arguments.add(new LoadpropertiesStructParam(entry.getValue(), entry.getKey()));
		}

		 protocol.load(arguments);
		 return protocol;
	}
}