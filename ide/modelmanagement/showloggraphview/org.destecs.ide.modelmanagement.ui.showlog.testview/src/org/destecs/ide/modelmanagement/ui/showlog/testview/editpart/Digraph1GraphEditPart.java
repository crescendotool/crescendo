package org.destecs.ide.modelmanagement.ui.showlog.testview.editpart;

import java.util.List;

import org.destecs.ide.modelmanagement.ui.showlog.testview.model.Digraph1Graph;
import org.destecs.ide.modelmanagement.ui.showlog.testview.model.Digraph1Node;
import org.destecs.ide.modelmanagement.ui.showlog.testview.policy.Digraph1XYLayoutEditPolicy;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * The edit part for the directed graph.
 */
public class Digraph1GraphEditPart extends AbstractGraphicalEditPart {

	/*
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new Digraph1XYLayoutEditPolicy());
	}

	/*
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		FreeformLayer freeformLayer = new FreeformLayer();
		freeformLayer.setLayoutManager(new FreeformLayout());
		return freeformLayer;
	}

	/*
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected List<Digraph1Node> getModelChildren() {
		List<Digraph1Node> nodes = ((Digraph1Graph) getModel()).getNodes();
		return nodes;
	}

}
