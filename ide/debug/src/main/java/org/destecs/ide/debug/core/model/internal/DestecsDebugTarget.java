package org.destecs.ide.debug.core.model.internal;

import java.io.File;

import org.destecs.ide.debug.IDebugConstants;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugException;
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

	public DestecsDebugTarget(ILaunch launch, IProject project, File outputFolder)
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
		
		if(simulationThread != null)
		{
			simulationThread.stopSimulation();
		}
		
		DebugEventHelper.fireTerminateEvent(this);
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

}
