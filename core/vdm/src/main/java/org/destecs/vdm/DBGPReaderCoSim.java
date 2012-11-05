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
package org.destecs.vdm;

import java.io.IOException;

import org.overturetool.vdmj.debug.DBGPReaderV2;
import org.overturetool.vdmj.debug.DBGPStatus;
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
	 * Changed visibility to alloy external access
	 */
	@Override
	public void startup(RemoteControl remote) throws IOException
	{
		super.startup(remote);
	}

	public DBGPStatus getStatus()
	{
		return this.status;
	}
	
	public void startCoSimulation() throws Exception
	{
//		 interpreter.execute(expression, this);
	}
}
