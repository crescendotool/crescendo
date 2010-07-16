package org.destecs.vdmj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.destecs.vdmj.runtime.CoSimClassInterpreter;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.VDMRT;
import org.overturetool.vdmj.commands.ClassCommandReader;
import org.overturetool.vdmj.commands.CommandReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.InitThread;



public class VDMCO extends VDMRT
{
	ClassInterpreter interpreter = null;

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
//		return super.interpret(filenames, defaultName);
		
		

		if (logfile != null)
		{
    		try
    		{
    			PrintWriter p = new PrintWriter(new FileOutputStream(logfile, false));
    			RTLogger.setLogfile(p);
    			println("RT events now logged to " + logfile);
    		}
    		catch (FileNotFoundException e)
    		{
    			println("Cannot create RT event log: " + e.getMessage());
    			return ExitStatus.EXIT_ERRORS;
    		}
		}

		try
		{
   			long before = System.currentTimeMillis();
   			interpreter = getInterpreter();
   			interpreter.init(null);

   			if (defaultName != null)
   			{
   				interpreter.setDefaultName(defaultName);
   			}

   			long after = System.currentTimeMillis();

   	   		infoln("Initialized " + plural(classes.size(), "class", "es") + " in " +
   	   			(double)(after-before)/1000 + " secs. ");
   	   		
   	   		return ExitStatus.EXIT_OK;
		}
		catch (ContextException e)
		{
			println("Initialization: " + e);
			e.ctxt.printStackTrace(Console.out, true);
			return ExitStatus.EXIT_ERRORS;
		}
		catch (Exception e)
		{
			println("Initialization: " + e.getMessage());
			return ExitStatus.EXIT_ERRORS;
		}
	}
	
	public ExitStatus asyncStartInterpret(List<File> filenames)
	{
		try
		{
			InitThread iniThread = new InitThread(Thread.currentThread());
			BasicSchedulableThread.setInitialThread(iniThread);
			ExitStatus status;

			if (script != null)
			{
				println(interpreter.execute(script, null).toString());
				status = ExitStatus.EXIT_OK;
			}
			else
			{
				infoln("Interpreter started");
				CommandReader reader = new ClassCommandReader(interpreter, "> ");
				status = reader.run(filenames);
			}

			if (logfile != null)
			{
				RTLogger.dump(true);
				infoln("RT events dumped to " + logfile);
			}

			return status;
		}
		catch (ContextException e)
		{
			println("Execution: " + e);
			e.ctxt.printStackTrace(Console.out, true);
		}
		catch (Exception e)
		{
			println("Execution: " + e);
		}

		return ExitStatus.EXIT_ERRORS;
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
