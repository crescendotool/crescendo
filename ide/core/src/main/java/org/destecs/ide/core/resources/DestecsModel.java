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
package org.destecs.ide.core.resources;

import org.destecs.core.contract.Contract;
import org.destecs.core.vdmlink.Links;

public class DestecsModel
{
	private Links links = null;
	private Contract contract = null;
	private boolean checked = false;
	private boolean ok = false;
	private boolean scriptOk = false;
	
	public synchronized void setLinks(Links links)
	{
		this.links=links;
	}
	
	public synchronized Links getLinks()
	{ 
		return links;
	}
	
	public synchronized void setContract(Contract contract)
	{
		this.contract = contract;
	}
	
	public synchronized Contract getContract()
	{
		return contract;
	}
	
	public synchronized void setChecked(boolean b)
	{
		this.checked = b;
	}
	
	public boolean isChecked()
	{
		return checked;
	}
	
	public synchronized void setOk(boolean b)
	{
		this.ok = b;
	}
	
	public synchronized void setScriptOk(boolean b)
	{
		this.scriptOk= b;
	}
	
	public boolean isOk()
	{
		return ok&&scriptOk;
	}
}
