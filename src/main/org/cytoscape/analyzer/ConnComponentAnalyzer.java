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


import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * Control class providing analysis of the connected components in a Cytoscape network.
 * 
 * @author Yassen Assenov
 */
public class ConnComponentAnalyzer {

	/**
	 * Initializes a new instance of <code>ConnComponentAnalyzer</code>.
	 * 
	 * @param aNetwork Network to be analyzed.
	 */
	public ConnComponentAnalyzer(NetworkAnalyzer analz, CyNetwork aNetwork) 
	{		
		network = aNetwork;	
		analyzer = analz;
	}
	
	NetworkAnalyzer analyzer;
	/**
	 * Gets all nodes in the given connected component.
	 * 
	 * @param aNetwork Network in which the given connected components is contained.
	 * @param aCompInfo Information about the connected component of interest.
	 * @return Set of all nodes in <code>aCompInfo</code>; empty set if this component is not
	 *         contained in <code>aNetwork</code>.
	 */
	public static Set<CyNode> getNodesOf(CyNetwork aNetwork, ConnectedComponentInfo aCompInfo) {
		Set<CyNode> nodes = new HashSet<CyNode>(aCompInfo.getSize());
		nodes.add(aCompInfo.getNode());
		LinkedList<CyNode> toTraverse = new LinkedList<CyNode>();
		toTraverse.add(aCompInfo.getNode());

		while (!toTraverse.isEmpty()) {
			final CyNode node = toTraverse.removeFirst();
			final Set<CyNode> neighbors = CyNetworkUtils.getNeighbors(aNetwork, node);
			for (CyNode nb : neighbors) {
				if (!nodes.contains(nb)) {
					nodes.add(nb);
					toTraverse.add(nb);
				}
			}
		}
		return nodes;
	}

	/**
	 * Finds all components in the analyzer network.
	 * 
	 * @return Set of all components in the analyzed network ({@link #getNetwork()}); empty set if
	 *         the analyzed network is empty.
	 */
	public Set<ConnectedComponentInfo> findComponents() {
		int untravCount = network.getNodeCount();

		Set<CyNode> traversed = new HashSet<CyNode>(untravCount);
		Set<ConnectedComponentInfo> components = new HashSet<ConnectedComponentInfo>();

		for ( CyNode node : network.getNodeList()) {
			if (!traversed.contains(node)) {
				// Unmarked node reached - create new conn. component
				final int ccSize = traverseReachable(node, traversed);
				untravCount -= ccSize;
				components.add(new ConnectedComponentInfo(analyzer, ccSize, node, false));
			}
		}
		return components;
	}

	/**
	 * Finds (the) largest connected component of the analyzed network. The largest component is the
	 * one that contains the most nodes.
	 * <p>
	 * If the network contains more than one largest connected components, one of them is returned.
	 * There is no guarantee that the one returned satisfies any requirements (apart from having
	 * largest size).
	 * </p>
	 * 
	 * @return Connected component C in the analyzed network, such that no other connected component
	 *         in the network contains more nodes than C.
	 */
	public ConnectedComponentInfo findLargestComponent() {
		ConnectedComponentInfo largest = new ConnectedComponentInfo(analyzer, 0, null, false);
		final Set<ConnectedComponentInfo> comps = findComponents();
		for (ConnectedComponentInfo current : comps) 
			if (current.getSize() > largest.getSize()) 
				largest = current;
		return largest;
	}

	private CyNetwork network;
	public CyNetwork getNetwork() {		return network;	}

	/**
	 * Gets all nodes in the given connected component.
	 * 
	 * @param aCompInfo Information about the connected component of interest.
	 * @return Set of all nodes in <code>aCompInfo</code>; empty set if this component is not
	 *         contained in the analyzed network ({@link #getNetwork()}).
	 * 
	 * @see #getNodesOf(CyNetwork, ConnectedComponentInfo)
	 */
	public Set<CyNode> getNodesOf(ConnectedComponentInfo aCompInfo) {		return getNodesOf(network, aCompInfo);	}

	/**
	 * Traverses all nodes that are reachable from the given node.
	 * 
	 * @param aNode Node to start the traversal from.
	 * @param aTraversed Set of traversed nodes. This method add all visited nodes to this set.
	 * @return Number of nodes which were traversed. <code>aNode</code> itself is also counted as
	 *         traversed.
	 */
	private int traverseReachable(CyNode aNode, Set<CyNode> aTraversed) {
		int size = 1;
		LinkedList<CyNode> toTraverse = new LinkedList<CyNode>();
		aTraversed.add(aNode);
		toTraverse.add(aNode);
		while (!toTraverse.isEmpty()) {
			final CyNode currentNode = toTraverse.removeFirst();
			final Set<CyNode> neighbors = CyNetworkUtils.getNeighbors(network, currentNode);
			for (CyNode nb : neighbors) {
				if (!aTraversed.contains(nb)) {
					size++;
					toTraverse.add(nb);
					aTraversed.add(nb);
				}
			}
		}
		return size;
	}
}
