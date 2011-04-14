package org.destecs.vdmj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.destecs.vdm.DBGPReaderCoSim;
import org.destecs.vdmj.runtime.CoSimClassInterpreter;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.VDMRT;
import org.overturetool.vdmj.debug.DBGPStatus;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.BacktrackInputReader;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.messages.rtlog.RTLogger;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;
import org.overturetool.vdmj.scheduler.InitThread;
import org.overturetool.vdmj.syntax.ClassReader;

public class VDMCO extends VDMRT
{
	private static final CharSequence ARCHITECTURE_COMMENT = "-- ## Architecture ## --";
	private static final CharSequence DEPLOYMENT_COMMENT = "-- ## Deploy ## --";
	public static File outputDir;
	ClassInterpreter interpreter = null;
	public static int debugPort = 10000;

	@Override
	public CoSimClassInterpreter getInterpreter() throws Exception
	{
		CoSimClassInterpreter interpreter = new CoSimClassInterpreter(classes);
		return interpreter;
	}

	@Override
	public ExitStatus interpret(List<File> filenames, String defaultName)
	{
		// return super.interpret(filenames, defaultName);
		if (logfile != null)
		{
			try
			{
				PrintWriter p = new PrintWriter(new FileOutputStream(logfile, false));
				RTLogger.setLogfile(p);
				println("RT events now logged to " + logfile);
			} catch (FileNotFoundException e)
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

			infoln("Initialized " + plural(classes.size(), "class", "es")
					+ " in " + (double) (after - before) / 1000 + " secs. ");

			return ExitStatus.EXIT_OK;
		} catch (ContextException e)
		{
			println("Initialization: " + e);
			e.ctxt.printStackTrace(Console.out, true);
			return ExitStatus.EXIT_ERRORS;
		} catch (Exception e)
		{
			println("Initialization: " + e.getMessage());
			return ExitStatus.EXIT_ERRORS;
		}
	}

	public ExitStatus asyncStartInterpret(final List<File> filenames)
	{
		class AsyncInterpreterExecutionThread extends Thread
		{
			ExitStatus status = null;
			boolean finished = false;

			public AsyncInterpreterExecutionThread()
			{
				setDaemon(true);
				setName("Async interpreter thread - runs scheduler");
			}

			@Override
			public void run()
			{
				try
				{
					InitThread iniThread = new InitThread(this);
					BasicSchedulableThread.setInitialThread(iniThread);
					// ExitStatus status;

					if (script != null)
					{
						status = ExitStatus.EXIT_OK;
						finished = true;
						String host="localhost";
						//int port=10000;
						String ideKey="1";
						DBGPReaderCoSim dbgpreader = new DBGPReaderCoSim(host, debugPort, ideKey, interpreter, script, null);
//						interpreter.init(dbgpreader);
						int retried = 0;
						while(dbgpreader.getStatus()== null )
						{
							if(retried>0)
							{
								Thread.sleep(500);
							}
							retried++;
							System.out.println("Trying to connect to IDE...("+retried+")");
							dbgpreader.startup(null);
							
						}
						while(dbgpreader.getStatus()==DBGPStatus.STARTING)
						{
							Thread.sleep(1000);
							dbgpreader.startup(null);
						}
						System.out.println(dbgpreader.getStatus());
//						println(interpreter.execute(script, null).toString());

					} else
					{
						status = ExitStatus.EXIT_ERRORS;
					}

					if (logfile != null)
					{
						RTLogger.dump(true);
						infoln("RT events dumped to " + logfile);
					}
					finished = true;
					return;
				} catch (ContextException e)
				{
					println("Execution: " + e);
					e.ctxt.printStackTrace(Console.out, true);
				} catch (Exception e)
				{
					println("Execution: " + e);
				}

				status = ExitStatus.EXIT_ERRORS;
				finished = true;
				return;
			}

			public boolean isFinished()
			{
				return finished;
			}

			public ExitStatus getExitStatus()
			{
				return status;
			}
		}
		;
		AsyncInterpreterExecutionThread runner = new AsyncInterpreterExecutionThread();

		runner.start();

		while (!runner.isFinished())
		{
			try
			{
				Thread.sleep(100);
			} catch (InterruptedException e)
			{
				// Ignore it
			}
		}

		return runner.getExitStatus();
	}

	public void setLogFile(File file)
	{
		logfile = file.getAbsolutePath();

	}

	public void setScript(String script)
	{
		VDMRT.script = script;
	}
	
	@Override
	public ExitStatus parse(List<File> files)
	{
		classes.clear();
		LexLocation.resetLocations();
   		int perrs = 0;
   		int pwarn = 0;
   		long duration = 0;

   		for (File file: files)
   		{
   			ClassReader reader = null;

   			try
   			{
   				if (file.getName().endsWith(".lib"))
   				{
   					FileInputStream fis = new FileInputStream(file);
   	    	        GZIPInputStream gis = new GZIPInputStream(fis);
   	    	        ObjectInputStream ois = new ObjectInputStream(gis);

   	    	        ClassList loaded = null;
   	    	        long begin = System.currentTimeMillis();

   	    	        try
   	    	        {
   	    	        	loaded = (ClassList)ois.readObject();
   	    	        }
       	 			catch (Exception e)
       				{
       	   				println(file + " is not a valid VDM++ library");
       	   				perrs++;
       	   				continue;
       				}
       	 			finally
       	 			{
       	 				ois.close();
       	 			}

   	    	        long end = System.currentTimeMillis();
   	    	        loaded.setLoaded();
   	    	        classes.addAll(loaded);
   	    	        classes.remap();

   	    	   		infoln("Loaded " + plural(loaded.size(), "class", "es") +
   	    	   			" from " + file + " in " + (double)(end-begin)/1000 + " secs");
   				}
   				else
   				{
   					long before = System.currentTimeMillis();
   					
   					BacktrackInputReader fileReader = new BacktrackInputReader(file,filecharset);
   					StringBuffer buf = new StringBuffer();
   					char c;
   					while((c=fileReader.readCh())!=(char)-1)
   					{
   						buf.append(c);
   					}
   					fileReader.close();
   					
   					String patchedContent = patch(buf.toString());
   					logChangedFileContent(patchedContent,file);
   					
    				LexTokenReader ltr = new LexTokenReader(patchedContent, Settings.dialect, file);
        			reader = new ClassReader(ltr);
        			classes.addAll(reader.readClasses());
        	   		long after = System.currentTimeMillis();
        	   		duration += (after - before);
   				}
    		}
			catch (InternalException e)
			{
   				println(e.toString());
   				perrs++;
			}
			catch (Throwable e)
			{
   				println(e.toString());
   				perrs++;
			}

			if (reader != null && reader.getErrorCount() > 0)
			{
    			perrs += reader.getErrorCount();
    			reader.printErrors(Console.out);
			}

			if (reader != null && reader.getWarningCount() > 0)
			{
				pwarn += reader.getWarningCount();
    			reader.printWarnings(Console.out);
			}
   		}

   		int n = classes.notLoaded();

   		if (n > 0)
   		{
       		info("Parsed " + plural(n, "class", "es") + " in " +
       			(double)(duration)/1000 + " secs. ");
       		info(perrs == 0 ? "No syntax errors" :
       			"Found " + plural(perrs, "syntax error", "s"));
    		infoln(pwarn == 0 ? "" : " and " +
    			(warnings ? "" : "suppressed ") + plural(pwarn, "warning", "s"));
   		}

   		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	private void logChangedFileContent(String patchedContent, File file)
	{
		PrintWriter out =null;
		try{
			FileWriter outFile = new FileWriter(new File(outputDir,file.getName()));
			out = new PrintWriter(outFile);
			out.append(patchedContent);
			
		}catch(IOException e)
		{
			
		}finally
		{
			if(out!=null)
			{
				out.close();
			}
		}
	}

	public static Map<String,String> replaceNewIdentifier = new Hashtable<String,String>();
	public static String architecture = "";
	public static String deploy = "";
	
//	static
//	{
//		replaceNewIdentifier.put("A", "B");
//		
//	}
	
	public static String patch(String tmp)
	{
		for (Entry<String, String> entry: replaceNewIdentifier.entrySet())
		{
			tmp = tmp.replaceAll("new( )+"+entry.getKey()+"\\(", "new "+entry.getValue()+"(");
			tmp = tmp.replaceAll("new(	)+"+entry.getKey()+"\\(", "new "+entry.getValue()+"(");
			tmp = tmp.replace(entry.getKey()+"`", entry.getValue()+"`");
		}
		
		if(tmp.contains(ARCHITECTURE_COMMENT) && architecture != null)
		{
			tmp = tmp.replace(ARCHITECTURE_COMMENT, architecture);
		}
		
		if(tmp.contains(DEPLOYMENT_COMMENT) && deploy != null)
		{
			tmp = tmp.replace(DEPLOYMENT_COMMENT, deploy);
		}
		return tmp;
	}
}
