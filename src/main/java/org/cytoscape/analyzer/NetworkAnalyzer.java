package org.cytoscape.analyzer;


import java.awt.Component;

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

import org.cytoscape.analyzer.util.NetworkInterpretation;
import org.cytoscape.analyzer.util.NetworkStats;
import org.cytoscape.analyzer.util.SumCountPair;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.subnetwork.CySubNetwork;

/**
 * Base class for all control classes that perform analysis on a specific network type.

 */
public abstract class NetworkAnalyzer {
	
	public static final boolean verbose = false;
	/**
	 * Gets the title of the network being analyzed.
	 * 
	 * @return Title of the network as a <code>String</code> instance; <code>null</code> if the network does
	 *         not have a title.
	 */
	public String getNetworkTitle() {
		return stats.getTitle();
	}

	/**
	 * Gets the statistics of the analyzed network.
	 * <p>
	 * This method should be called only after the call to {@link #computeAll()}. Otherwise the
	 * <code>NetworkStats</code> instance returned will not be populated with data.
	 * </p>
	 * 
	 * @return Computed statistics of the analyzed network, wrapped as a <code>NetworkStats</code> instance.
	 */
	public NetworkStats getStats() {
		return stats;
	}

	/**
	 * Gets the current progress of the analyzer as a number of steps.
	 * 
	 * @return Number of steps completed in the analysis process.
	 */
	public int getCurrentProgress() {
		return progress;
	}

	static String columnName = "statistics";
	public void doOutput()
	{
		String json = stats.jsonOutput();
		CyTable netTable = network.getDefaultNetworkTable();
		CyColumn col = netTable.getColumn(columnName);
		if (col == null)
		{
			netTable.createColumn(columnName, String.class, true);
			col = netTable.getColumn(columnName);
		}
		netTable.getRow(network.getSUID()).set(columnName, json);
		
		
		String out = NetworkStats.formattedOutput(stats.getParameters());
		CytoPanel panel = desktop.getCytoPanel(CytoPanelName.EAST);
		panel.setState(CytoPanelState.DOCK);
		int nPanels = panel.getCytoPanelComponentCount();
		if (nPanels > 0)
		{
			Component comp = panel.getComponentAt(0);
			if (comp instanceof ResultsPanel)
			{
				ResultsPanel results = (ResultsPanel) comp;
				results.setResultString(out);
			}
		}
			
//		stats.dump();
	}

	/**
	 * Gets the maximum progress of the analyzer as a number of steps.
	 * 
	 * @return Total number of steps required for the analyzer to finish.
	 */
	public int getMaxProgress() {
		return stats.getInt("nodeCount") + 1;
	}

	/**
	 * Computes all the network parameters.
	 */
	public abstract void computeAll();

	/**
	 * Cancels the process of network analysis.
	 * <p>
	 * Note that this method does not force the analyzer to cancel immediately; it takes an unspecified period
	 * of time until the analysis thread actually stops.
	 * </p>
	 */
	public void cancel() {
		cancelled = true;
	}

	/**
	 * Checks if this analyzer performs analysis on the whole network.
	 * 
	 * @return <code>true</code> if this analyzed calculates topological parameters for all nodes in the
	 *         targeted network; <code>false</code> if topological parameters are calculated for a subset of
	 *         nodes only.
	 */
	public boolean isGlobal() {
		return true;  // nodeSet == null;
	}

	/**
	 * Initializes the fields of this class.
	 * 
	 * @param aNetwork
	 *            Network to be analyzed.
	 * @param aNodeSet
	 *            Subset of nodes in <code>aNetwork</code>, for which topological parameters are to be
	 *            calculated. Set this to <code>null</code> if parameters must be calculated for all nodes in
	 *            the network.
	 * @param aInterpr
	 *            Interpretation of network's edges.
	 * @see #network
	 * @see #interpr
	 * @see #stats
	 */
	
	
	protected NetworkAnalyzer(CyNetwork aNetwork, NetworkInterpretation aInterpr, CySwingApplication app, AnalyzerManager mgr) {
		network = aNetwork;
//		nodeSet = aNodeSet;
		interpr = aInterpr;
		stats = new NetworkStats(aNetwork, aInterpr.getInterpretSuffix());
		progress = 0;
		desktop = app;
		manager = mgr;
	}

	/**
	 * Prepares the network, if necessary, before the analysis starts.
	 * <p>
	 * If stored in the interpretation, this method removes all undirected self-loops from the network.
	 * </p>
	 */
	protected void analysisStarting() {
		manager.registerResultsPanel();
		if (interpr.isIgnoreUSL()) {
			removedEdges = new HashSet<CyEdge>();
			for ( CyEdge edge : network.getEdgeList() ) {
				if (!edge.isDirected()) {
					if (edge.getSource() == edge.getTarget()) {
						removedEdges.add(edge);
					}
				}
			}
			network.removeEdges(removedEdges);
			// Update edge count
			stats.set("edgeCount", new Integer(network.getEdgeCount()));
		}
	}

	/**
	 * Returns the network to initial state, if necessary, after the analysis has finished.
	 * <p>
	 * If previously removed, this method puts back all undirected self-loops in the network.
	 * </p>
	 */
	protected void analysisFinished() {
		if (interpr.isIgnoreUSL()) {
			for (final CyEdge e : removedEdges) {
				// TODO we should consider using CySubNetwork carefully!!!
				// Perhaps we shouldn't use it at all, or we might actually want to use it 
				// more pervasively.  I don't know.
				((CySubNetwork)network).addEdge(e);
			}
		}
		
	}

	/**
	 * Adds a value in a mapping of integers and <code>SummCountPair</code>s.
	 * <p>
	 * This method accumulates the given value to the sequence statistics (<code>SummCountPair</code>
	 * instance) mapped to the specified key. If the mapping does not contain such a key, a new (key, value)
	 * pair is created, where the value is a new <code>SummCountPair</code> instance with the given value
	 * added.
	 * </p>
	 * 
	 * @param aMapping
	 *            Mapping between integers and sequence statistics.
	 * @param aKey
	 *            Key to which the value is to ba added.
	 * @param aValue
	 *            Value to be added.
	 */
	public void accumulate(Map<Integer, SumCountPair> aMapping, int aKey, double aValue) {
		accumulate(aMapping, new Integer(aKey), aValue);
	}

	/**
	 * Adds a value in a mapping of integers and <code>SummCountPair</code>s.
	 * <p>
	 * This method accumulates the given value to the sequence statistics (<code>SummCountPair</code>
	 * instance) mapped to the specified key. If the mapping does not contain such a key, a new (key, value)
	 * pair is created, where the value is a new <code>SummCountPair</code> instance with the given value
	 * added.
	 * </p>
	 * 
	 * @param aMapping
	 *            Mapping between integers and sequence statistics.
	 * @param aKey
	 *            Key to which the value is to ba added.
	 * @param aValue
	 *            Value to be added.
	 */
	protected void accumulate(Map<Integer, SumCountPair> aMapping, Integer aKey, double aValue) {
		final SumCountPair sequenceStat = aMapping.get(aKey);
		if (sequenceStat != null) {
			sequenceStat.add(aValue);
		} else {
			aMapping.put(aKey, new SumCountPair(aValue));
		}
	}

	/**
	 * Fills the values for the average clustering coefficients given their sums.
	 * 
	 * @param aCCps
	 *            Map of clustering coefficients. The keys of the map must be node connectivities, and the
	 *            values - their sums, encapsulated in <code>SumCountPair</code> instances.
	 * @param aAverages
	 *            Array of points to be filled with the average clustering coefficient for every connectivity.
	 * @return Sum of all clustering coefficients in the given hashmap.
	 * 
	 * @throws ArrayIndexOutOfBoundsException
	 *             If the size of the <code>aAverages</code> array is less than the number of entries in
	 *             <code>aCCps</code>.
	 * @throws NullPointerException
	 *             If <code>aCCps</code> is <code>null</code>; or if <code>aCCps</code> is non-empty and
	 *             <code>aAverages</code> is <code>null</code>.
	 */
	public double accumulateCCs(Map<Integer, SumCountPair> aCCps, Point2D.Double[] aAverages) {
		double total = 0;
		Set<Integer> neighborCounts = aCCps.keySet();
		int i = 0;
		for (Integer nc : neighborCounts) {
			SumCountPair coefs = aCCps.get(nc);
			total += coefs.getSum();
			aAverages[i++] = new Point2D.Double(nc.doubleValue(), coefs.getAverage());
		}
		return total;
	}
	
	/**
	 * the local object that remembers locations for our app
	 */
	AnalyzerManager  manager;
	/**
	 * the frame is used to access the results panel
	 */
	protected CySwingApplication desktop;
	/**
	 * Target network for analysis.
	 */
	public CyNetwork network;

	/**
	 * Subset of nodes to be analyzed.
	 */
//	protected Set<CyNode> nodeSet;

	/**
	 * Interpretation of edges in {@link #network}.
	 */
	protected NetworkInterpretation interpr;

	/**
	 * Statistics computed over the network.
	 */
	protected NetworkStats stats;

	/**
	 * Current progress of the analysis.
	 * <p>
	 * The progress of the analyzer is measured in number of steps. Extender classes are responsible for
	 * maintaining the value of this field up to date. The progress must be a natural number not greater than
	 * the maximal progress.
	 * </p>
	 * 
	 * @see #getMaxProgress()
	 */
	public int progress;

	/**
	 * Flag indicating if the process of analysis was cancelled by the user.
	 * <p>
	 * This flag should only be modified by calling {@link #cancel()}. Extender classes should terminate the
	 * analysis once this flag is set to <code>true</code>. Therefore, in the process of network analysis, the
	 * value of this flag should be checked at regular intervals.
	 * </p>
	 */
	public boolean cancelled;

	/**
	 * Flag indicating if only the minimal calculations should be run
	 * <p>
	 * This flag supports a subset of the functionality, especially when the size of the network is larger.
	 * </p>
	 */
	protected boolean degreeOnly;

	/**
	 * Set of all edges that are removed from {@link #network} before the analysis. These edges are added back
	 * to the network after the analysis completes or is cancelled.
	 */
	private Set<CyEdge> removedEdges;
}
