package org.destecs.core.vdmlink;

import java.util.List;

public class LinkInfo {
	
	private String identifier = null;
	private List<String> qualifiedName = null;
	private int line = -1;
	
	public LinkInfo(String identifier, List<String> qualifiedName, int line) {
		this.identifier = identifier;
		this.qualifiedName = qualifiedName;
		this.line = line;
	}
	
	//@Deprecated
//	public StringPair getBoundedVariable()
//	{
//		if(qualifiedName.size() > 1)
//		{
//			return new StringPair(qualifiedName.get(0), qualifiedName.get(1));
//		}
//		else
//			return null;
//	}
	
	public List<String> getQualifiedName()
	{
		return qualifiedName;
	}
	
	public int getLine()
	{
		return line;
	}
	
}
