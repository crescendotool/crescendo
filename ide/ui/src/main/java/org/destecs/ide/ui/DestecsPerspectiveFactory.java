package org.destecs.ide.ui;

import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class DestecsPerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		
		//layout.setEditorAreaVisible(false);
		//layout.addView(CoSimStarterView.VIEW_ID, IPageLayout.TOP, 0.75f, editorArea);
		
//		IFolderLayout mainArea = layout.createFolder("main", IPageLayout.TOP, 0.70f,editorArea);
//		mainArea.addView("org.eclipse.ui.editorss");
		IFolderLayout bottomArea = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.60f,editorArea);
		bottomArea.addView(InfoTableView.SIMULATION_MESSAGES_VIEW_ID);
		
		IFolderLayout projectExplorerFolder = layout.createFolder("left", IPageLayout.LEFT, 0.20f,editorArea);
		//projectExplorerFolder.addView("org.eclipse.ui.navigator.ProjectExplorer");
		projectExplorerFolder.addView("org.destecs.ide.ui.destecsExplorer");
		
		IFolderLayout rightArea = layout.createFolder("right", IPageLayout.RIGHT, 0.75f,editorArea);
		rightArea.addView(IPageLayout.ID_OUTLINE);//CoSimStarterView.VIEW_ID);
		
		
				
		
		
		IFolderLayout bottomLeftArea = layout.createFolder("bottomLeft", IPageLayout.LEFT, 0.50f,"bottom");
		bottomLeftArea.addView(InfoTableView.SIMULATION_ENGINE_VIEW_ID);
		
		IFolderLayout bottomRightArea = layout.createFolder("bottomRight", IPageLayout.RIGHT, 0.50f,"bottom");
		bottomRightArea.addView(InfoTableView.SIMULATION_VIEW_ID);
		
		
		
		
		layout.addActionSet("org.eclipse.debug.ui.launchActionSet");
		
		
		
		//IFolderLayout outputfolder= layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.75,editorArea);
		//outputfolder.addView(InfoTableView.SIMULATION_VIEW_ID);
	}

}
