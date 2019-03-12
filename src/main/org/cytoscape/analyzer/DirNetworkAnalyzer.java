package org.cytoscape.analyzer;

/*
 * #%L
 * Cytoscape NetworkAnalyzer Impl (network-analyzer-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013
 *   Max Planck Institute for Informatics, Saarbruecken, Germany
 *   The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.w3c.dom.Node;

/**
 * Network analyzer for networks that contain directed edges only.
  */
public class DirNetworkAnalyzer extends NetworkAnalyzer {

	/**
	 * Initializes a new instance of <code>DirNetworkAnalyzer</code>.
	 * 
	 * @param aNetwork
	 *            Network to be analyzed.
	 * @param aNodeSet
	 *            Subset of nodes in <code>aNetwork</code>, for which topological parameters are to
	 *            be calculated. Set this to <code>null</code> if parameters must be calculated for
	 *            all nodes in the network.
	 * @param aInterpr
	 *            Interpretation of the network edges.
	 */
	public DirNetworkAnalyzer(CyNetwork aNetwork, Set<CyNode> aNodeSet, NetworkInterpretation aInterpr, CySwingApplication app) 
	{
		super(aNetwork, aNodeSet, aInterpr, app);
		if (nodeSet != null)
			stats.set("nodeCount", nodeSet.size());
		nodeCount = stats.getInt("nodeCount");
	}

		
	@Override	public void computeAll() {
		long time = System.currentTimeMillis();
		analysisStarting();

		// Compute number of connected components
		final ConnComponentAnalyzer cca = new ConnComponentAnalyzer(network);
		Set<ConectedComponentInfo> components = cca.findComponents();
//		final int connectedComponentsCount = components.size();
		CyTable nodeTable = network.getDefaultNodeTable();
		CyColumn INcol = nodeTable.getColumn("INDEGR");
		if (INcol == null)
		{
			nodeTable.createColumn("INDEGR",Integer.class, true);
			INcol = nodeTable.getColumn("INDEGR");
		}
		CyColumn OUTcol = nodeTable.getColumn("OUT_DEGR");
		if (OUTcol == null)
		{
			nodeTable.createColumn("OUT_DEGR",Integer.class, true);
			OUTcol = nodeTable.getColumn("OUT_DEGR");
		}

		// Compute node and edge betweenness
		for (ConectedComponentInfo aCompInfo : components) {

			// Get nodes of connected component
			final Set<CyNode> connNodes = cca.getNodesOf(aCompInfo);
//			final Set<CyEdge> connEdges = new HashSet<CyEdge>();
			if (nodeSet != null)
				connNodes.retainAll(nodeSet);
			
			for (CyNode node : connNodes)
			{
				int nIn =  getInNeighbors(node).size();
				int nOut =  getOutNeighbors(node).size();
				CyRow nodeRow = network.getRow(node);
				String name = nodeRow.get("name", String.class);
//				System.out.println(name + " has " + nIn + " incoming and " + nOut + " outgoing neighbors");
				nodeRow.set("INDEGR", nIn);
				nodeRow.set("OUT_DEGR", nOut);
		}
		 }
		analysisFinished();
		time = System.currentTimeMillis() - time;
		stats.set("time", time / 1000.0);
		progress = nodeCount;
		doOutput();
	}

	/**
	 * Gets all incoming edges of the given node.
	 * 
	 * @param aNode
	 *            Node, of which incoming edges are to be found.
	 * @return Array of edge indices, containing all the edges in the network that point to
	 *         <code>aNode</code> .
	 */
	private List<CyEdge> getInEdges(CyNode aNode) {
		return network.getAdjacentEdgeList(aNode, CyEdge.Type.INCOMING);
	}

	/**
	 * Gets all outgoing edges of the given node.
	 * 
	 * @param aNode
	 *            Node, of which outgoing edges are to be found.
	 * @return Array of edge indices, containing all the edges in the network that start from
	 *         <code>aNode</code>.
	 */
	private List<CyEdge> getOutEdges(CyNode aNode) {
		return network.getAdjacentEdgeList(aNode, CyEdge.Type.OUTGOING);
	}

	/**
	 * Gets all out-neighbors of the given node.
	 * 
	 * @param aNode
	 *            Node, whose out-neighbors are to be found.
	 * @return <code>Set</code> of <code>Node</code> instances, containing all the out-neighbors of
	 *         <code>aNode</code>; empty set if the specified node does not have outgoing edges.
	 * @see CyNetworkUtils#getNeighbors(CyNetwork, Node, int[])
	 */
	private Set<CyNode> getOutNeighbors(CyNode aNode) {
		return CyNetworkUtils.getNeighbors(network, aNode, getOutEdges(aNode));
	}

	/**
	 * Gets all in-neighbors of the given node.
	 * 
	 * @param aNode
	 *            Node, whose in-neighbors are to be found.
	 * @return <code>Set</code> of <code>Node</code> instances, containing all the in-neighbors of
	 *         <code>aNode</code>; empty set if the specified node does not have incoming edges.
	 * @see CyNetworkUtils#getNeighbors(CyNetwork, Node, int[])
	 */
	private Set<CyNode> getInNeighbors(CyNode aNode) {
		return CyNetworkUtils.getNeighbors(network, aNode, getInEdges(aNode));
	}

	/**
	 * Integer of how many nodes are in the network.
	 * <p>
	 * This is used by all histograms as range.
	 * </p>
	 */
	private int nodeCount;

	/**
	 * Flag, if we want to compute and store node attributes.
	 */
}
