package org.destecs.ide.modelmanagement.ui.showlog.testview.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The graph model object which describes the list of nodes in the directed
 * graph.
 */
public class Digraph1Graph {

	/**
	 * A fixed number of nodes in this directed graph.
	 */
	// protected int y = 5;
	// protected int x = 1;

	/**
	 * The list of nodes in the graph.
	 */
	private List<Digraph1Node> nodes = new ArrayList<Digraph1Node>();

	/**
	 * Constructor for a Digraph1Graph.
	 */
	public Digraph1Graph() {
		createNodes();
	}

	/**
	 * Create the fixed number of nodes in this directed graph.
	 */
	protected void createNodes() {
		// for (int y = 0; y < this.y; y++) {
		Digraph1Node node = new Digraph1Node(1, 1, 1);
		getNodes().add(node);
		Digraph1Node node2 = new Digraph1Node(1, 2, 3);
		getNodes().add(node2);
		Digraph1Node node3 = new Digraph1Node(1, 3, 4);
		getNodes().add(node3);
		Digraph1Node node4 = new Digraph1Node(1, 4, 6);
		getNodes().add(node4);

		// }
	}

	/**
	 * Get the list of nodes.
	 * 
	 * @return the list of nodes.
	 */
	public List<Digraph1Node> getNodes() {
		return this.nodes;
	}
}
