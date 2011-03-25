package org.destecs.ide.modelmanagement.ui.showlog.testview.factory;

import org.destecs.ide.modelmanagement.ui.showlog.testview.editpart.Digraph1GraphEditPart;
import org.destecs.ide.modelmanagement.ui.showlog.testview.editpart.Digraph1NodeEditPart;
import org.destecs.ide.modelmanagement.ui.showlog.testview.model.Digraph1Graph;
import org.destecs.ide.modelmanagement.ui.showlog.testview.model.Digraph1Node;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * A factory for creating new EditParts for the directed graph.
 */
public class Digraph1EditPartFactory implements EditPartFactory {

	/*
	 * @see
	 * org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart,
	 * java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart editPart = null;
		if (model instanceof Digraph1Graph) {
			editPart = new Digraph1GraphEditPart();
		} else if (model instanceof Digraph1Node) {
			editPart = new Digraph1NodeEditPart();
		}

		if (editPart != null) {
			editPart.setModel(model);
		}

		return editPart;
	}
}
