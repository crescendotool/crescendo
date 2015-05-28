/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.vdmj;

import java.io.File;
import java.io.FileInputStream;
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
import org.overture.ast.lex.LexLocation;
import org.overture.ast.messages.InternalException;
import org.overture.config.Settings;
import org.overture.interpreter.VDMRT;
import org.overture.interpreter.debug.DBGPStatus;
import org.overture.interpreter.messages.Console;
import org.overture.interpreter.messages.rtlog.RTLogger;
import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.ContextException;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.scheduler.BasicSchedulableThread;
import org.overture.interpreter.scheduler.InitThread;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.interpreter.util.ExitStatus;
import org.overture.parser.lex.BacktrackInputReader;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ClassReader;

public class VDMCO extends VDMRT
{
	private static final CharSequence ARCHITECTURE_COMMENT = "-- ## Architecture ## --";
	private static final CharSequence DEPLOYMENT_COMMENT = "-- ## Deployment ## --";

	public static Map<String, String> replaceNewIdentifier = new Hashtable<String, String>();
	public static String architecture = "";
	public static String deploy = "";

	public static File outputDir;
	ClassInterpreter interpreter = null;
	public static int debugPort = 10000;
	public Exception exception = null;

	@Override
	public CoSimClassInterpreter getInterpreter() throws Exception
	{
		CoSimClassInterpreter interpreter = new CoSimClassInterpreter(classes);
		return interpreter;
	}

	public Exception getException()
	{
		return exception;
	}

	public ExitStatus asyncStartInterpret(final List<File> filenames)
	{
		class AsyncInterpreterExecutionThread extends Thread
		{
			ExitStatus status = null;
			boolean finished = false;
			public DBGPReaderCoSim dbgpreader = null;

			public AsyncInterpreterExecutionThread()
			{
				setDaemon(true);
				setName("Async interpreter thread - runs scheduler");
			}

			@Override
			public void run()
			{
				if (Settings.usingCmdLine)
				{
					try
					{
						Thread main = new Thread(new Runnable()
						{

							public void run()
							{
								Interpreter i = Interpreter.getInstance();
								i.init(null);
								try
								{
									i.execute("new World().run()", null);
								} catch (Exception e)
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
						main.setDaemon(true);
						main.setName("VDM console main");
						main.start();

						status = ExitStatus.EXIT_OK;
						finished = true;
						return;
					} catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						status = ExitStatus.EXIT_ERRORS;
					}
				} else
					try
					{
						InitThread iniThread = new InitThread(this);
						BasicSchedulableThread.setInitialThread(iniThread);

						if (script != null)
						{
							status = ExitStatus.EXIT_OK;
							String host = "localhost";

							String ideKey = "1";
							dbgpreader = new DBGPReaderCoSim(host, debugPort, ideKey, getInterpreter(), script, null);

							int retryCountDown = 5;
							int retried = 0;
							while (dbgpreader.getStatus() == null)
							{
								if (retried > 0)
								{
									Thread.sleep(500);
								}
								retried++;
								System.out.println("Trying to connect to IDE...("
										+ retried + ")");
								System.out.println("Status of DBGPReader is: "
										+ dbgpreader.getStatus()
										+ " with retried: " + retried);
								dbgpreader.startup(null);

								if (retryCountDown == 0)
								{
									status = ExitStatus.EXIT_ERRORS;
								}
								retryCountDown--;
							}

							retryCountDown = 5;
							while (dbgpreader.getStatus() == DBGPStatus.STARTING)
							{
								System.out.println("DBGPReader status is now STARTING and a new try to start dbgpreader will be made in 1 sec.");
								Thread.sleep(1000);
								dbgpreader.startup(null);
								if (retryCountDown == 0)
								{
									status = ExitStatus.EXIT_ERRORS;
									finished = true;
									return;
								}
								retryCountDown--;
							}
							try
							{
								dbgpreader.startCoSimulation();
							} catch (Exception e)
							{
								// Failed to start simulation
								e.printStackTrace();
								exception = e;

								status = ExitStatus.EXIT_ERRORS;
								finished = true;
							}

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
						Console.err.println("Execution: " + e);
						e.ctxt.printStackTrace(Console.err, true);
						Console.err.flush();
						exception = e;
					} catch (Exception e)
					{
						exception = e;
						println("Execution: " + e);
					}

				status = ExitStatus.EXIT_ERRORS;
				finished = true;
				return;
			}

			public boolean isFinished()
			{
				return finished
						|| (dbgpreader != null && (/* dbgpreader.getStatus()==DBGPStatus.STARTING|| */dbgpreader.getStatus() == DBGPStatus.RUNNING));
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
		if (file != null)
		{
			logfile = file.getAbsolutePath();
		}
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

		for (File file : files)
		{
			ClassReader reader = null;

			try
			{
				if (file.getName().endsWith(".lib"))
				{
					FileInputStream fis = new FileInputStream(file);
					GZIPInputStream gis = new GZIPInputStream(fis);
					ObjectInputStream ois = new ObjectInputStream(gis);

					ClassListInterpreter loaded = null;
					long begin = System.currentTimeMillis();

					try
					{
						loaded = (ClassListInterpreter) ois.readObject();
					} catch (Exception e)
					{
						println(file + " is not a valid VDM++ library");
						perrs++;
						continue;
					} finally
					{
						ois.close();
					}

					long end = System.currentTimeMillis();
					loaded.setLoaded();
					classes.addAll(loaded);
					classes.remap();

					infoln("Loaded " + plural(loaded.size(), "class", "es")
							+ " from " + file + " in " + (double) (end - begin)
							/ 1000 + " secs");
				} else
				{
					if (replaceNewIdentifier.isEmpty()
							&& architecture.isEmpty() && deploy.isEmpty())
					{
						long before = System.currentTimeMillis();
						LexTokenReader ltr = new LexTokenReader(file, Settings.dialect, filecharset);
						reader = new ClassReader(ltr);
						classes.addAll(reader.readClasses());
						long after = System.currentTimeMillis();
						duration += (after - before);
					} else
					{
						long before = System.currentTimeMillis();

						BacktrackInputReader fileReader = new BacktrackInputReader(file, filecharset);
						StringBuffer buf = new StringBuffer();
						char c;
						while ((c = fileReader.readCh()) != (char) -1)
						{
							buf.append(c);
						}
						fileReader.close();

						String patchedContent = patch(buf.toString());
						logChangedFileContent(patchedContent, file);

						LexTokenReader ltr = new LexTokenReader(patchedContent, Settings.dialect, file);
						reader = new ClassReader(ltr);
						classes.addAll(reader.readClasses());
						long after = System.currentTimeMillis();
						duration += (after - before);
					}
				}
			} catch (InternalException e)
			{
				println(e.toString());
				perrs++;
			} catch (Throwable e)
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
			info("Parsed " + plural(n, "class", "es") + " in "
					+ (double) (duration) / 1000 + " secs. ");
			info(perrs == 0 ? "No syntax errors" : "Found "
					+ plural(perrs, "syntax error", "s"));
			infoln(pwarn == 0 ? "" : " and " + (warnings ? "" : "suppressed ")
					+ plural(pwarn, "warning", "s"));
		}

		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

	private void logChangedFileContent(String patchedContent, File file)
	{
		PrintWriter out = null;
		try
		{
			FileWriter outFile = new FileWriter(new File(outputDir, file.getName()));
			out = new PrintWriter(outFile);
			out.append(patchedContent);

		} catch (IOException e)
		{

		} finally
		{
			if (out != null)
			{
				out.close();
			}
		}
	}

	public static String patch(String tmp)
	{
		for (Entry<String, String> entry : replaceNewIdentifier.entrySet())
		{
			tmp = tmp.replaceAll("new( )+" + entry.getKey() + "\\(", "new "
					+ entry.getValue() + "(");
			tmp = tmp.replaceAll("new(	)+" + entry.getKey() + "\\(", "new "
					+ entry.getValue() + "(");
			tmp = tmp.replace(entry.getKey() + "`", entry.getValue() + "`");
		}

		if (tmp.contains(ARCHITECTURE_COMMENT) && architecture != null)
		{
			tmp = tmp.replace(ARCHITECTURE_COMMENT, architecture);
		}

		if (tmp.contains(DEPLOYMENT_COMMENT) && deploy != null)
		{
			tmp = tmp.replace(DEPLOYMENT_COMMENT, deploy);
		}
		return tmp;
	}
}
