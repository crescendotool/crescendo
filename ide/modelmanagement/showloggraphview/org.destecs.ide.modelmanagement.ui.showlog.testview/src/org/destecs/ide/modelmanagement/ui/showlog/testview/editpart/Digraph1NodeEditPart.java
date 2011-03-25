package org.destecs.ide.modelmanagement.ui.showlog.testview.editpart;

import org.destecs.ide.modelmanagement.ui.showlog.testview.figure.Digraph1NodeFigure;
import org.destecs.ide.modelmanagement.ui.showlog.testview.model.Digraph1Node;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * The edit part which describes a node in the directed graph.
 */
public class Digraph1NodeEditPart extends AbstractGraphicalEditPart {

	/*
	 * @see org.eclipse.gef.editparts.AbstractEditPart#isSelectable()
	 */
	@Override
	public boolean isSelectable() {
		return false;
	}

	/*
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		/* not implemented */
	}

	/*
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		int x = ((Digraph1Node) getModel()).getX();
		int y = ((Digraph1Node) getModel()).getY();
		int v = ((Digraph1Node) getModel()).getV();
		return new Digraph1NodeFigure(x, y, v);
	}

	/*
	 * @see
	 * org.eclipse.gef.editparts.AbstractGraphicalEditPart#registerVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		Digraph1NodeFigure nodeFigure = (Digraph1NodeFigure) getFigure();
		Point location = nodeFigure.getRectangleFigure().getLocation();
		Dimension size = nodeFigure.getRectangleFigure().getSize();
		Digraph1GraphEditPart graph = (Digraph1GraphEditPart) getParent();
		Rectangle constraint = new Rectangle(location, size);
		graph.setLayoutConstraint(this, nodeFigure, constraint);
	}
}
