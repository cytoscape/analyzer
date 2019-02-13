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

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Storage class for information needed by node betweenness calculation.
 * <p>
 * An instance of this class is assigned to every node during the computation of
 * node and edge betweenness.
 * </p>
 * 
 * @author Nadezhda Doncheva
 */
public class NodeBetweenInfo {

	/**
	 * Initializes a new instance of <code>NodeBetweenInfo</code>.
	 * 
	 * @param initCount
	 *            Number of shortest paths to this node. Default value is
	 *            usually <code>0</code>.
	 * @param initLength
	 *            Length of a shortest path to this node Default value is
	 *            usually <code>-1</code>.
	 * @param initBetweenness
	 *            Node betweenness value of this node. Default value is usually
	 *            <code>0</code>.
	 */
	public NodeBetweenInfo(long initCount, int initLength, double initBetweenness) {
		spCount = initCount;
		spLength = initLength;
		dependency = 0.0;
		betweenness = initBetweenness;
		predecessors = new LinkedList<CyNode>();
		outedges = new LinkedList<CyEdge>();
	}

	public int getSPLength() {		return spLength;	}
	public long getSPCount() {		return spCount;	}
	public double getDependency() {		return dependency;	}
	public double getBetweenness() {		return betweenness;	}
	public CyNode pullPredecessor() {		return predecessors.removeFirst();	}
	public LinkedList<CyEdge> getOutEdges() {		return outedges;	}
	public boolean isEmptyPredecessors() {		return (predecessors.isEmpty()); 	}
	public void setSPLength(int newLength) {		spLength = newLength;	}
	public void addSPCount(long newSPCount) {		spCount += newSPCount;	}
	public void addDependency(double newDependency) {		dependency += newDependency;	}
	public void addPredecessor(CyNode pred) {		predecessors.add(pred);	}
	public void addOutedge(CyEdge outedge) {		outedges.add(outedge);	}
	public void addBetweenness(double newBetweenness) {		betweenness += newBetweenness;	}

	/**
	 * Resets all variables for the calculation of edge and node betweenness to
	 * their default values except the node betweenness.
	 */
	public void reset() {
		spCount = 0;
		spLength = -1;
		dependency = 0.0;
		predecessors = new LinkedList<CyNode>();
		outedges = new LinkedList<CyEdge>();
	}

	/**
	 * Changes the shortest path count and length for the source node of this
	 * run if BFS
	 */
	public void setSource() {
		spCount = 1;
		spLength = 0;
		dependency = 0.0;
		predecessors = new LinkedList<CyNode>();
		outedges = new LinkedList<CyEdge>();
	}

	private LinkedList<CyNode> predecessors;
	private LinkedList<CyEdge> outedges;
	private long spCount;
	private int spLength;
	private double dependency;
	private double betweenness;

}
