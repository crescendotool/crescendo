package org.destecs.vdm;

import java.io.IOException;

import org.overturetool.vdmj.debug.DBGPReaderV2;
import org.overturetool.vdmj.debug.RemoteControl;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.values.CPUValue;

public class DBGPReaderCoSim extends DBGPReaderV2
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2077781552419120714L;

	public DBGPReaderCoSim(String host, int port, String ideKey,
			Interpreter interpreter, String expression, CPUValue cpu)
	{
		super(host, port, ideKey, interpreter, expression, cpu);
	}
	
	
	/**
	 * Only connect, skip interpreter.init and remote set
	 */
	@Override
	public void startup(RemoteControl remote) throws IOException
	{
		connect();
	}

}
