package org.destecs.ide.modelmanagement.ui.showlog.testview.view;

import org.destecs.ide.modelmanagement.ui.showlog.testview.factory.Digraph1EditPartFactory;
import org.destecs.ide.modelmanagement.ui.showlog.testview.model.Digraph1Graph;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * The example view for the directed graph.
 */
public class Digraph1GraphicalView extends ViewPart {

	private DefaultEditDomain editDomain;

	private GraphicalViewer graphicalViewer;

	/*
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		setEditDomain(new DefaultEditDomain(null));
		setGraphicalViewer(new ScrollingGraphicalViewer());
		getGraphicalViewer().createControl(parent);
		getGraphicalViewer().setRootEditPart(
				new FreeformGraphicalRootEditPart());
		getGraphicalViewer().setEditPartFactory(new Digraph1EditPartFactory());
		getGraphicalViewer().setContents(new Digraph1Graph());
		getGraphicalViewer().getControl().setBackground(
				ColorConstants.listBackground);
	}

	/**
	 * Get the EditDomain for this ViewPart.
	 * 
	 * @return the EditDomain for this ViewPart.
	 */
	protected DefaultEditDomain getEditDomain() {
		return this.editDomain;
	}

	/**
	 * Returns the graphical viewer.
	 * 
	 * @return the graphical viewer
	 */
	protected GraphicalViewer getGraphicalViewer() {
		return this.graphicalViewer;
	}

	/**
	 * Sets the EditDomain for this ViewPart.
	 * 
	 * @param anEditDomain
	 *            the EditDomain for this ViewPart.
	 */
	protected void setEditDomain(DefaultEditDomain anEditDomain) {
		this.editDomain = anEditDomain;
	}

	/*
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		getGraphicalViewer().getControl().setFocus();
	}

	/**
	 * Sets the graphicalViewer for this EditorPart.
	 * 
	 * @param viewer
	 *            the graphical viewer
	 */
	protected void setGraphicalViewer(GraphicalViewer viewer) {
		getEditDomain().addViewer(viewer);
		this.graphicalViewer = viewer;
	}

}
