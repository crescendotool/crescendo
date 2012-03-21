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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.destecs.ide.debug.DestecsDebugPlugin;
import org.destecs.ide.debug.IDebugConstants;
import org.destecs.ide.debug.octave.OctaveFactory;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ITerminate;
import org.eclipse.debug.core.model.IThread;

public class DestecsDebugTarget extends PlatformObject implements IDebugTarget
{

	private ILaunch launch;
	private boolean isTerminated;
	final private IProject project;
	private CoSimulationThread simulationThread;
	private File outputFolder;
	private File deCsvFile;
	private File ctCsvFile;

	public DestecsDebugTarget(ILaunch launch, IProject project,
			File outputFolder)
	{
		this.launch = launch;
		this.project = project;
		this.outputFolder = outputFolder;
	}

	public String getName() throws DebugException
	{
		return "DESTECS Application";
	}

	public IProcess getProcess()
	{
		return null;
	}

	public IThread[] getThreads() throws DebugException
	{
		return new IThread[0];
	}

	public boolean hasThreads() throws DebugException
	{
		return false;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint)
	{
		return false;
	}

	public IDebugTarget getDebugTarget()
	{
		return this;
	}

	public ILaunch getLaunch()
	{
		return this.launch;
	}

	public String getModelIdentifier()
	{
		return IDebugConstants.PLUGIN_ID;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter)
	{
		if (adapter == IDebugElement.class)
		{
			return this;
		}

		/*
		 * Not implemented currently if (adapter == IStepFilters.class) { return getDebugTarget(); }
		 */

		if (adapter == IDebugTarget.class)
		{
			return getDebugTarget();
		}

		if (adapter == ITerminate.class)
		{
			return getDebugTarget();
		}

		// if (adapter == IVdmDebugTarget.class) {
		// return getVdmDebugTarget();
		// }

		if (adapter == ILaunch.class)
		{
			return getLaunch();
		}

		return super.getAdapter(adapter);
	}

	public boolean canTerminate()
	{
		return !this.isTerminated;
	}

	public boolean isTerminated()
	{
		return this.isTerminated;
	}

	public void terminate() throws DebugException
	{
		this.isTerminated = true;

		if (simulationThread != null)
		{
			simulationThread.stopSimulation();
		}

		DebugEventHelper.fireTerminateEvent(this);

		handlePostTerminationActions();
	}

	private void handlePostTerminationActions()
	{
		if (deCsvFile != null || ctCsvFile != null)
		{
			try
			{
				String content = OctaveFactory.createResultScript(outputFolder.getName(), deCsvFile, ctCsvFile,launch.getLaunchConfiguration().getAttribute(IDebugConstants.DESTECS_LAUNCH_CONFIG_SHOW_OCTAVE_PLOTS, false));
				writeFile(outputFolder, IDebugConstants.OCTAVE_PLOT_FILE, content);
				
			} catch (IOException e)
			{
				DestecsDebugPlugin.logError("Failed to write Octave script file.", e);
			}catch (CoreException e)
			{
				DestecsDebugPlugin.logError("Failed to get Octave shoew option from launchconfig in destecs debug target.", e);
			}
		}
		
		try {
			this.project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			DebugPlugin.log(e);
			
		}
	}

	public static void writeFile(File outputFolder, String fileName,
			String content) throws IOException
	{
		FileWriter outputFileReader = new FileWriter(new File(outputFolder, fileName));
		BufferedWriter outputStream = new BufferedWriter(outputFileReader);
		outputStream.write(content);
		outputStream.close();
	}

	public boolean canResume()
	{
		return false;
	}

	public boolean canSuspend()
	{
		return false;
	}

	public boolean isSuspended()
	{
		return false;
	}

	public void resume() throws DebugException
	{

	}

	public void suspend() throws DebugException
	{

	}

	public void breakpointAdded(IBreakpoint breakpoint)
	{

	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta)
	{

	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta)
	{

	}

	public boolean canDisconnect()
	{
		return false;
	}

	public void disconnect() throws DebugException
	{

	}

	public boolean isDisconnected()
	{
		return false;
	}

	public IMemoryBlock getMemoryBlock(long startAddress, long length)
			throws DebugException
	{
		return null;
	}

	public boolean supportsStorageRetrieval()
	{
		return false;
	}

	public IProject getProject()
	{
		return project;
	}

	public void setCoSimulationThread(CoSimulationThread simThread)
	{
		this.simulationThread = simThread;
	}

	public File getOutputFolder()
	{
		return this.outputFolder;
	}

	public void setCtCsvFile(File file)
	{
		this.ctCsvFile = file;
	}

	public void setDeCsvFile(File file)
	{
		this.deCsvFile = file;
	}

	public File getCtCsvFile()
	{
		return this.ctCsvFile;
	}

	public File getDeCsvFile()
	{
		return this.deCsvFile;
	}
}
