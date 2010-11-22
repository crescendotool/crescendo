package org.destecs.ide.simeng.internal.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.destecs.core.simulationengine.IEngineListener;
import org.destecs.core.simulationengine.IMessageListener;
import org.destecs.core.simulationengine.ISimulationListener;
import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.protocol.structs.StepStruct;
import org.destecs.protocol.structs.StepStructoutputsStruct;

public class ListenerToLog implements IEngineListener, IMessageListener, ISimulationListener
{
	private final File base;
	
	final private CachedLogFileWriter engine =new CachedLogFileWriter();
	final private CachedLogFileWriter message=new CachedLogFileWriter();
	final private CachedLogFileWriter simulation=new CachedLogFileWriter();

	public ListenerToLog(File base) throws FileNotFoundException
	{
		this.base = base;
		
		engine.setLogfile(new PrintWriter(new File(this.base,"Engine.log")));
		message.setLogfile(new PrintWriter(new File(this.base,"Message.log")));
		simulation.setLogfile(new PrintWriter(new File(this.base,"Simulation.log")));
	}

	public void from(Simulator simulator, Double time, String messageName)
	{
		message.log(pad(10,simulator.toString())+ " , "+pad(75, messageName)+ " , "+ time.toString());	
	}
	
	private String pad(int c, String data)
	{
		StringBuffer sb = new StringBuffer(data);
		while(sb.length()<c)
		{
			sb.append(" ");
		}
		return sb.toString();
	}

	public void stepInfo(Simulator simulator, StepStruct result)
	{
		StringBuilder sb = new StringBuilder();
		for (StepStructoutputsStruct o : result.outputs)
		{
			sb.append(o.name + "=" + o.value+ " ");
		}
		simulation.log(pad(10,simulator.toString())+ " , "+ pad(20,sb.toString())+ " , "+ result.time.toString());
		
	}

	public void info(Simulator simulator, String message)
	{
		engine.log(pad(10,simulator.toString())+ " , "+ message.replace('\n', ' '));		
	}
	
	
	public void close()
	{
		engine.dump(true);
		message.dump(true);
		simulation.dump(true);
	}
}
