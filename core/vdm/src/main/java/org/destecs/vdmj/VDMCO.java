package org.destecs.vdmj;

import java.io.File;
import java.util.List;

import org.destecs.vdmj.runtime.CoSimClassInterpreter;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.VDMRT;



public class VDMCO extends VDMRT
{

	@Override
	public CoSimClassInterpreter getInterpreter() throws Exception
	{
		CoSimClassInterpreter interpreter = new CoSimClassInterpreter(classes);
		return interpreter;
	}
	
	@Override
	public ExitStatus interpret(List<File> filenames, String defaultName)
	{
		// TODO Auto-generated method stub
		return super.interpret(filenames, defaultName);
	}

	public void setLogFile(File file)
	{
		logfile = file.getAbsolutePath();
		
	}
	
	public void setScript(String script)
	{
		VDMRT.script = script;
	}
}
