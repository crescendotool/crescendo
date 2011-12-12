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
package org.destecs.ide.ui;

import org.destecs.ide.simeng.ui.views.InfoTableView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class DestecsPerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		
		String editorArea = layout.getEditorArea();
		
		IFolderLayout bottomArea = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f,editorArea);
		
		//bottomArea.addView(InfoTableView.SIMULATION_ENGINE_VIEW_ID);
		bottomArea.addView(IPageLayout.ID_PROBLEM_VIEW);
		
		
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, (float)0.75, editorArea);//CoSimStarterView.VIEW_ID);
		
		
		IFolderLayout bottomLeftArea = layout.createFolder("bottomLeft", IPageLayout.LEFT, 0.50f,"bottom");
		bottomLeftArea.addView(InfoTableView.SIMULATION_ENGINE_VIEW_ID);
		
//		
//		IFolderLayout bottomRightArea = layout.createFolder("bottomRight", IPageLayout.RIGHT, 0.50f,"bottom");
//		bottomRightArea.addView(InfoTableView.SIMULATION_VIEW_ID);
		
		
		
		
//		layout.addActionSet("org.eclipse.debug.ui.launchActionSet");
		
		
		
		//IFolderLayout outputfolder= layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.75,editorArea);
		//outputfolder.addView(InfoTableView.SIMULATION_VIEW_ID);
	}

}
