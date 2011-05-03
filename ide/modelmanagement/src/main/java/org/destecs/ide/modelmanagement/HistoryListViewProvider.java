package org.destecs.ide.modelmanagement;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.tigris.subversion.subclipse.core.ISVNRemoteResource;
import org.tigris.subversion.subclipse.ui.SVNUIPlugin;
import org.tigris.subversion.subclipse.ui.history.HistoryTableProvider;
import org.tigris.subversion.subclipse.ui.settings.ProjectProperties;
import org.tigris.subversion.svnclientadapter.SVNRevision;

/**
 * This class provides extra co-model dev info
 * Include top-level view and detail-level view by filtering extra dev info.
 * This class is used for displaying History View (text list log with filter function).
 */
public class HistoryListViewProvider extends HistoryTableProvider{
	
	private ISVNRemoteResource currentRemoteResource;
	private SVNRevision.Number currentRevision;
	private TableViewer viewer;
	private Font currentRevisionFont;
	private IDialogSettings settings = SVNUIPlugin.getPlugin().getDialogSettings();
	ProjectProperties projectProperties = null;

	//column constants
	private final static int COL_REVISION = 0;
	private final static int COL_DATE = 1;
	private final static int COL_AUTHOR = 2;
	private final static int COL_COMMENT = 3;
	private final static int COL_BASELINE = 4;
	private final static int COL_IMPORTANTCHANGE = 5;
	private final static int COL_ALTERNATIVES = 6;
	private final static int COL_OTHERISSUES = 7;
	
	public HistoryListViewProvider() {
		this(SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION, null);
	}

	public HistoryListViewProvider(int i, Object object) {
		// TODO will fix after extra dev info can be displayed in console
	}
	
	
}
