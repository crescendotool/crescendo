package org.destecs.ide.modelmanagement.ui.showlog.testview.editor;

import org.destecs.ide.modelmanagement.ui.showlog.testview.factory.Digraph1EditPartFactory;
import org.destecs.ide.modelmanagement.ui.showlog.testview.model.Digraph1Graph;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

/**
 * The graphical editor for the directed graph.
 */
public class Digraph1GraphicalEditor extends GraphicalEditor {

	/**
	 * The default tool on the diagram that does not do anything. Otherwise the
	 * selection tool is used and it looks like you can select nodes with the
	 * selection tool when you actually cannot for this example.
	 */
	public class DoNothingTool extends AbstractTool {

		/*
		 * @see org.eclipse.gef.tools.AbstractTool#getCommandName()
		 */
		@Override
		protected String getCommandName() {
			return null;
		}
	}

	/**
	 * Constructor for a Digraph1GraphicalEditor.
	 */
	public Digraph1GraphicalEditor() {
		setEditDomain(new DefaultEditDomain(this));
		getEditDomain().setActiveTool(new DoNothingTool());
	}

	/*
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 */
	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		GraphicalViewer viewer = getGraphicalViewer();

		viewer.setRootEditPart(new FreeformGraphicalRootEditPart());
		viewer.setEditPartFactory(new Digraph1EditPartFactory());
	}

	/*
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		/* not implemented */
	}

	/*
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(new Digraph1Graph());
	}

	/*
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		if (input instanceof IFileEditorInput) {
			setPartName(((IFileEditorInput) input).getName());
		}
	}
}
