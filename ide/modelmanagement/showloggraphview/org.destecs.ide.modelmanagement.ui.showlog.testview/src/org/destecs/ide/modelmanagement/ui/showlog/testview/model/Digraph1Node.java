package org.destecs.ide.modelmanagement.ui.showlog.testview.model;

/**
 * The node model object which describes a node in the directed graph.
 */
public class Digraph1Node {

	/**
	 * The node knows what node number it is on the graph.
	 */
	// private int number;
	private int x;
	private int y;
	private int v;

	/**
	 * Constructor for a Digraph1Node.
	 * 
	 * @param aNumber
	 *            the node number.
	 */
	// public Digraph1Node(int aNumber) {
	// super();
	// this.number = aNumber;
	// }

	public Digraph1Node(int ax, int ay, int versionNo) {
		super();
		this.x = ax;
		this.y = ay;
		this.v = versionNo;
	}

	/**
	 * Get the node number.
	 * 
	 * @return the node number.
	 */
	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getV() {
		return this.v;
	}
}