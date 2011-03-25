package org.destecs.ide.modelmanagement.ui.showlog.testview.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * The figure for a node in the directed graph.
 */
public class Digraph1NodeFigure extends Figure {

	/**
	 * Label for name of the node.
	 */
	private Label label;

	/**
	 * Rectangle for the node.
	 */
	private RectangleFigure rectangleFigure;

	/**
	 * Constructor for a Digraph1NodeFigure.
	 * 
	 * @param number
	 *            the node number in the directed graph.
	 */
	public Digraph1NodeFigure(int x, int y, int v) {
		setLayoutManager(new XYLayout());
		this.rectangleFigure = new RectangleFigure();
		this.rectangleFigure.setBackgroundColor(ColorConstants.lightBlue);
		// this.rectangleFigure.setLocation(new Point((number + 1) * 57,
		this.rectangleFigure.setLocation(new Point(x * 90, y * 50));
		this.rectangleFigure.setSize(new Dimension(90, 40));
		add(this.rectangleFigure);
		this.label = new Label();
		this.label.setText("Version " + v); //$NON-NLS-1$
		add(this.label);
	}

	/**
	 * Get the label in the node figure.
	 * 
	 * @return the label in the node figure.
	 */
	public Label getLabel() {
		return this.label;
	}

	/**
	 * Get the rectangle in the node figure.
	 * 
	 * @return the rectangle in the node figure.
	 */
	public RectangleFigure getRectangleFigure() {
		return this.rectangleFigure;
	}

	/*
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	@Override
	public void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		setConstraint(getRectangleFigure(), new Rectangle(0, 0, r.width,
				r.height));
		setConstraint(getLabel(), new Rectangle(0, 0, r.width, r.height));
		getRectangleFigure().invalidate();
		getLabel().invalidate();
	}
}