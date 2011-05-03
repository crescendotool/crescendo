package org.destecs.ide.modelmanagement;

import org.tigris.subversion.svnclientadapter.ISVNLogMessage;

public interface ILogMessage extends ISVNLogMessage{
	
	//not sure in which means
	public final String BASELINE = "svn:baseline";
	public final String IMPORTANTCHANGE = "svn:importantchange";
	public final String EXPLORATION = "svn:exploration";
	
	public abstract boolean isBaseline();
	
	public abstract boolean isImportantchange();
	
	public abstract boolean hasAlternatives();
	
	public abstract long getNumberOfAlternatives();
	
	public abstract ILogMessage[] getAlternativeMessages();
	
}
