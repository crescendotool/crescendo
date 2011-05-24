package org.destecs.ide.core.resources;

import org.destecs.core.contract.Contract;
import org.destecs.core.vdmlink.Links;

public class DestecsModel
{
	private Links links = null;
	private Contract contract = null;
	private boolean checked = false;
	private boolean ok = false;
	
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
	
	public boolean isOk()
	{
		return ok;
	}
}
