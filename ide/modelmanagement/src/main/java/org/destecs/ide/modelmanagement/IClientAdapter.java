package org.destecs.ide.modelmanagement;

import java.io.File;

import org.tigris.subversion.svnclientadapter.ISVNClientAdapter;
import org.tigris.subversion.svnclientadapter.SVNClientException;

public interface IClientAdapter extends ISVNClientAdapter{
	
    /**
     * Commits changes to the repository. This usually requires
     * authentication, see Auth.
     * @return Returns a long representing the revision. It returns a
     *         -1 if the revision number is invalid.
     * @param paths files to commit.
     * @param message log message.
     * @param recurse whether the operation should be done recursively.
     * @param keepLocks
     * @param isBasline
     * @param isImportantchange
     * @param hasAlternatives
     * @exception SVNClientException
     */
	public abstract long commit(File[] paths, String message, boolean recurse, boolean keepLocks, boolean isBaseline, boolean isImportantchange, boolean hasAlternatives)
		throws SVNClientException;
	

}
