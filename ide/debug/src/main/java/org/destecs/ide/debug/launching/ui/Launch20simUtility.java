package org.destecs.ide.debug.launching.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.destecs.core.simulationengine.Clp20SimUtility;
import org.destecs.core.simulationengine.exceptions.SimulationException;
import org.destecs.core.simulationengine.model.CtModelConfig;
import org.destecs.ide.simeng.internal.core.Clp20SimProgramLauncher;
import org.destecs.protocol.ProxyICoSimProtocol;
import org.destecs.protocol.structs.LoadpropertiesStructParam;

public class Launch20simUtility {
	

	private static final int RETRIES = 5;

	public Launch20simUtility() {
	}
	
	public static ProxyICoSimProtocol launch20sim(String ctFile, String ctUrl,boolean remoteLaunch) 
	{
		/*
		 * Connecting to 20sim
		 */
		if(!remoteLaunch)
		{
			Clp20SimProgramLauncher clp20sim = new Clp20SimProgramLauncher(new File(ctFile));
			try {
				clp20sim.launch();
			} catch (IOException e) {
				return null;
			}
		}
		
		ProxyICoSimProtocol protocol = null;
		try
		{
			protocol = Clp20SimUtility.connect(new URL(ctUrl));
		} catch (MalformedURLException e1)
		{
			
		}catch(SimulationException e1s)
		{
			
		}
		
		
		if(protocol == null)
		{
			return null;
		}
		
		boolean couldLoad = false;
		CtModelConfig model = new CtModelConfig(ctFile);
		List<LoadpropertiesStructParam> arguments = new Vector<LoadpropertiesStructParam>();
		for (Entry<String, String> entry : model.arguments.entrySet())
		{
			arguments.add(new LoadpropertiesStructParam(entry.getKey(),entry.getValue()));
		}

		for (int i = 0; i < RETRIES; i++)
		{
			try
			{
				protocol.load(arguments);
				couldLoad = true;
				break;
			} catch (Exception e)
			{
				try
				{
					Thread.sleep(2000);
				} catch (InterruptedException e1)
				{
					// should not happen
				}
			}

		}

		return couldLoad ? protocol : null;
	}
}