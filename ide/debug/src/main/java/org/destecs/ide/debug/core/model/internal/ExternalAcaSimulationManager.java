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
package org.destecs.ide.debug.core.model.internal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class ExternalAcaSimulationManager extends AcaSimulationManager
{
	String host;

	public ExternalAcaSimulationManager(DestecsAcaDebugTarget target,
			IProgressMonitor monitor, String host)
	{
		super(target, monitor);
		this.host = host;
	}

	public void run()
	{
		ExternalAcaConsole console = new ExternalAcaConsole();
		console.clear();

		List<ILaunchConfiguration> configurations = new Vector<ILaunchConfiguration>();
		configurations.addAll(target.getAcaConfigs());
		Integer currentIndex = 0;

		boolean completedASimulation = false;

		Socket clientSocket = null;
		// ServerSocket serverSocket = null;
		try
		{
			// String host = System.getProperty("Crescendo-matlab-host");
			if (host == null)
			{
				host = "localhost";
			}

			int port = 30000;
			// serverSocket = new ServerSocket(30000);
			// serverSocket.setSoTimeout(10000);
			// clientSocket = serverSocket.accept();
			console.out.println("Connecting to \"" + host + ":" + port + "\"");
			clientSocket = new Socket(host, port);
			console.out.println("Setting timeout to 5s");
			clientSocket.setSoTimeout(5000);

			try
			{
				// matlab tcp init is a bit slow so give it time to initialize
				Thread.sleep(500);
			} catch (InterruptedException e1)
			{
			}

			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			final ILaunchConfiguration originalConfig = target.getAcaConfigs().iterator().next();
			ILaunchConfiguration initialConfig = originalConfig;

			File lastOutput = null;

			String dataFromServer = null;
			boolean ready = false;
			try
			{

				dataFromServer = inFromServer.readLine();
				console.out.println("Server send with: " + dataFromServer);
				if (!dataFromServer.trim().startsWith("READY"))
				{
					console.err.println("No server was ready to handle ACA generation");
				} else
				{
					ready = true;
				}
			} catch (java.net.SocketTimeoutException e)
			{

			}

			final String SEPERATOR = "|";
			while (ready && !shouldStop && !monitor.isCanceled())
			{
				String sdps = initialConfig.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, "");
				String totalTime = initialConfig.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SIMULATION_TIME, "");
				String ctSettings = initialConfig.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS, "");

				String message = " File=" + lastOutput + "" + SEPERATOR
						+ "SDP=" + sdps + SEPERATOR + "Time=" + totalTime
						+ SEPERATOR + "CT-Settings=" + ctSettings + "\n";

				// sentence = inFromUser.readLine();
				console.out.println("Sending(" + message.length()
						+ " bytes): \"" + message + "\"");
				outToServer.writeBytes(message);

				try
				{
					dataFromServer = inFromServer.readLine();
					console.out.println("Server replied with: "
							+ dataFromServer);
				} catch (java.net.SocketTimeoutException e)
				{
					console.err.println("Time out - stopping");
					break;
				}

				if (dataFromServer == null)
				{
					break;
				}
				if (dataFromServer.equals("STOP"))
				{
					break;
				}

				List<String> data = new Vector<String>();

				if (dataFromServer.contains(SEPERATOR))
				{
					data.addAll(Arrays.asList(dataFromServer.split(SEPERATOR)));
				}

				for (String parameter : data)
				{
					if (parameter.contains("="))
					{
						String p[] = parameter.split("=");
						if (p[0].equalsIgnoreCase("SDP"))
						{
							sdps = p[1];
						} else if (p[0].equalsIgnoreCase("Time"))
						{
							totalTime = p[1];
						} else if (p[0].equalsIgnoreCase("CT-Settings"))
						{
							ctSettings = p[1];
						} else
						{
							console.err.println("Could not parse parameter="
									+ parameter);
						}
					} else
					{
						console.err.println("Could not parse parameter="
								+ parameter);
					}
				}

				ILaunchConfigurationWorkingCopy modifiedConfig = originalConfig.getWorkingCopy();
				modifiedConfig.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHARED_DESIGN_PARAM, sdps);
				modifiedConfig.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SIMULATION_TIME, totalTime);
				modifiedConfig.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_20SIM_SETTINGS, ctSettings);

				monitor.subTask("Running (" + (++currentIndex) + ") "
						+ originalConfig.getName());
				if (!launchSingleLaunchConfig(true, currentIndex, modifiedConfig))
				{
					break;
				}

				lastOutput = lastUsedOutputFolder;
				completedASimulation = true;
				initialConfig = modifiedConfig;

			}
			console.out.println("Done.");

		} catch (java.net.ConnectException e)
		{
			if (shouldStop || monitor.isCanceled())
			{
				return;
			}
			DestecsDebugPlugin.logError("Failed to connect to host in External ACA", e);
			e.printStackTrace(console.err);
		} catch (UnknownHostException e)
		{
			DestecsDebugPlugin.logError("Failed to connect to host in External ACA", e);
			e.printStackTrace(console.err);
		} catch (IOException e)
		{
			DestecsDebugPlugin.logError("IO error in External ACA", e);
			e.printStackTrace(console.err);
		} catch (CoreException e)
		{
			DestecsDebugPlugin.logError("Error in External ACA", e);
			e.printStackTrace(console.err);
		} finally
		{
			try
			{
				if (clientSocket != null)
				{
					clientSocket.close();
				}
			} catch (IOException e)
			{
			}
		}

		if (completedASimulation)
		{
			try
			{
				target.terminate();
			} catch (DebugException e)
			{
				DestecsDebugPlugin.logError("Failed to terminate destecs target", e);
			}
		}
		monitor.done();
		refreshProject();
	}

	// private File launchSingleLaunchConfig(ILaunchConfiguration configOriginal,
	// Integer currentIndex)
	// {
	// File outputFolder = null;
	// if (shouldStop)
	// {
	// return null;
	// }
	// try
	// {
	// if (target.getLaunch().isTerminated())
	// {
	// return null;
	// }
	//
	// ILaunchConfigurationWorkingCopy config = configOriginal.getWorkingCopy();
	// config.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_NAME_POSTFIX, currentIndex.toString());
	// // if (currentIndex > 500)
	// {// protect eclipse
	// config.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_FILTER_OUTPUT, true);
	// }
	//
	// // group runs
	// String outputPreFix = config.getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, "");
	// // modify
	// // total count
	// //
	// config.setAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_OUTPUT_PRE_FIX, outputPreFix);
	//
	// // monitor.worked(step);
	// System.out.println("Running ACA with: " + config.getName());
	//
	// ILaunch acaRunLaunch = launch(config, ILaunchManager.DEBUG_MODE);
	// setActiveLaunch(acaRunLaunch);
	// IDebugTarget target = acaRunLaunch.getDebugTarget();
	// completedTargets.add((DestecsDebugTarget) target);
	// outputFolder = (target == null ? null
	// : ((DestecsDebugTarget) target).getOutputFolder());
	//
	// while (!acaRunLaunch.isTerminated())
	// {
	// internalSleep(100);
	// }
	//
	// if (acaRunLaunch != null && !acaRunLaunch.isTerminated())
	// {
	// acaRunLaunch.terminate();
	// }
	//
	// setActiveLaunch(null);
	//
	// @SuppressWarnings("unchecked")
	// Map<Object, Object> attributes = config.getAttributes();
	// String data = "** launch summery for ACA: " + config.getName();
	// for (Map.Entry<Object, Object> entry : attributes.entrySet())
	// {
	// data += entry.getKey() + " = " + entry.getValue() + "\n";
	// }
	//
	// data += "\n\n----------------------- MEMENTO -------------------------------\n\n";
	// data += config.getMemento();
	//
	// if (outputFolder != null)
	// {
	// try
	// {
	// FileWriter outFile = new FileWriter(new File(outputFolder, "launch"));
	// PrintWriter out = new PrintWriter(outFile);
	// out.println(data);
	// out.close();
	// } catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// }
	// internalSleep(1000);// just let the tools calm down.
	// } catch (Exception e)
	// {
	// DestecsDebugPlugin.logError("Error in AcaSimlation manager", e);
	// }
	// return outputFolder;
	// }

	// public void internalSleep(long millis)
	// {
	// try
	// {
	// Thread.sleep(millis);
	// } catch (InterruptedException e)
	// {
	// }
	// }

}
