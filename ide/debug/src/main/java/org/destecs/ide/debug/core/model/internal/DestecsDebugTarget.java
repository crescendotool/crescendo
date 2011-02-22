package org.destecs.ide.debug.core.model.internal;

import org.eclipse.core.resources.IMarkerDelta;
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

	public DestecsDebugTarget(ILaunch launch)
	{
		this.launch = launch;
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
		return null;
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
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		if (adapter == IDebugElement.class) {
			return this;
		}

		/*
		 * Not implemented currently
		 * 
		 * if (adapter == IStepFilters.class) { return getDebugTarget(); }
		 */

		if (adapter == IDebugTarget.class) {
			return getDebugTarget();
		}

		if (adapter == ITerminate.class) {
			return getDebugTarget();
		}

//		if (adapter == IVdmDebugTarget.class) {
//			return getVdmDebugTarget();
//		}

		if (adapter == ILaunch.class) {
			return getLaunch();
		}
		
		return super.getAdapter(adapter);
	}

	public boolean canTerminate()
	{
		return false;
	}

	public boolean isTerminated()
	{
		return this.isTerminated;
	}

	public void terminate() throws DebugException
	{

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

	public void setTerminated(boolean terminated)
	{
		this.isTerminated = terminated;
		DebugEventHelper.fireTerminateEvent(this);
		DebugEventHelper.fireChangeEvent(this);
	}

}
