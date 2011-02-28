package org.destecs.ide.core.resources;

import org.destecs.core.contract.Contract;
import org.destecs.core.vdmlink.Links;

public class DestecsModel
{
	private Links links = null;
	private Contract contract = null;
	
	
	
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
}
