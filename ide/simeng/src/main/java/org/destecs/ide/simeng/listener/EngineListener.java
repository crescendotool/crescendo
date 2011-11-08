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
package org.destecs.ide.simeng.listener;

import java.util.List;
import java.util.Vector;

import org.destecs.core.simulationengine.SimulationEngine.Simulator;
import org.destecs.core.simulationengine.listener.IEngineListener;
import org.destecs.ide.simeng.ui.views.InfoTableView;

public class EngineListener extends BaseListener implements IEngineListener
{
	

	public EngineListener(InfoTableView view)
	{
		super(view);
		initialColPack=-1;
		refreshCount=1;
	}

	public void info(Simulator simulator, String message)
	{
		List<String> l = new Vector<String>();
		l.add(simulator.toString());
		l.add(message.replace('\n', ' '));
		insetData(l);
	}
}
