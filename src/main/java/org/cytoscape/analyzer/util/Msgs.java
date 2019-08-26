package org.cytoscape.analyzer.util;


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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Storage class for human-readable messages.
 */
public class Msgs {

	/**
	 * Simple parameter names in the form of a hash map, the keys being the IDs and the values - the textual
	 * <code>String</code>s in human readable form.
	 */
	private static final Map<String, String> simpleParams;

	/**
	 * Node attributes names for parameters computed by NetworkAnalyzer for directed network interpretations.
	 */
	private static final Set<String> dirNodeAttributes;

	/**
	 * Node attributes names for parameters computed by NetworkAnalyzer for undirected network
	 * interpretations.
	 */
	private static final Set<String> undirNodeAttributes;

	/**
	 * Node attribute names in the form of a hash map, the keys being the IDs and the values - the textual
	 * <code>String</code>s in human readable form.
	 */
	private static final Map<String, String> nodeAttributes;

	/**
	 * Edge attribute names in the form of a hash map, the keys being the IDs and the values - the textual
	 * <code>String</code>s in human readable form.
	 */
	private static final Map<String, String> edgeAttributes;

	static {
		simpleParams = new HashMap<String, String>(16);
		simpleParams.put("networkTitle", "");
		simpleParams.put("time", "Analysis time (sec)");
		simpleParams.put("nodeCount", "Number of nodes");
		simpleParams.put("edgeCount", "Number of edges");
		simpleParams.put("density", "Network density");
		simpleParams.put("heterogeneity", "Network heterogeneity");
		simpleParams.put("centralization", "Network centralization");
		simpleParams.put("avNeighbors", "Avg. number of neighbors");
		simpleParams.put("ncc", "Connected components");
		simpleParams.put("connPairs", "Shortest paths");
		simpleParams.put("diameter", "Network diameter");
		simpleParams.put("radius", "Network radius");
		simpleParams.put("avSpl", "Characteristic path length");
		simpleParams.put("cc", "Clustering coefficient");
		simpleParams.put("nsl", "Number of self-loops");
		simpleParams.put("mnp", "Multi-edge node pairs");
		simpleParams.put("usn", "Isolated nodes");

		nodeAttributes = new HashMap<String, String>(32);
		nodeAttributes.put("spl", "Eccentricity");
		nodeAttributes.put("cco", "ClusteringCoefficient");
		nodeAttributes.put("tco", "TopologicalCoefficient");
		nodeAttributes.put("apl", "AverageShortestPathLength");
		nodeAttributes.put("clc", "ClosenessCentrality");
		nodeAttributes.put("isn", "IsSingleNode");
		nodeAttributes.put("nco", "NeighborhoodConnectivity");
		nodeAttributes.put("nde", "NumberOfDirectedEdges");
		nodeAttributes.put("nue", "NumberOfUndirectedEdges");
		nodeAttributes.put("slo", "SelfLoops");
		nodeAttributes.put("deg", "Degree");
		nodeAttributes.put("pmn", "PartnerOfMultiEdgedNodePairs");
		nodeAttributes.put("din", "Indegree");
		nodeAttributes.put("dou", "Outdegree");
		nodeAttributes.put("dal", "EdgeCount");
		nodeAttributes.put("nbt", "BetweennessCentrality");
		nodeAttributes.put("rad", "Radiality");
		nodeAttributes.put("stress", "Stress");

		dirNodeAttributes = new HashSet<String>(16);
		dirNodeAttributes.add("Eccentricity");
		dirNodeAttributes.add("AverageShortestPathLength");
		dirNodeAttributes.add("ClosenessCentrality");
		dirNodeAttributes.add("ClusteringCoefficient");
		dirNodeAttributes.add("Indegree");
		dirNodeAttributes.add("Outdegree");
		dirNodeAttributes.add("EdgeCount");
		dirNodeAttributes.add("IsSingleNode");
		dirNodeAttributes.add("SelfLoops");
		dirNodeAttributes.add("PartnerOfMultiEdgedNodePairs");
		dirNodeAttributes.add("NeighborhoodConnectivity");
		dirNodeAttributes.add("BetweennessCentrality");
		dirNodeAttributes.add("Stress");

		undirNodeAttributes = new HashSet<String>(16);
		undirNodeAttributes.add("Degree");
		undirNodeAttributes.add("NeighborhoodConnectivity");
		undirNodeAttributes.add("ClusteringCoefficient");
		undirNodeAttributes.add("TopologicalCoefficient");
		undirNodeAttributes.add("Eccentricity");
		undirNodeAttributes.add("AverageShortestPathLength");
		undirNodeAttributes.add("ClosenessCentrality");
		undirNodeAttributes.add("BetweennessCentrality");
		undirNodeAttributes.add("Stress");
		undirNodeAttributes.add("Radiality");
		undirNodeAttributes.add("SelfLoops");
		undirNodeAttributes.add("IsSingleNode");
		undirNodeAttributes.add("NumberOfUndirectedEdges");
		undirNodeAttributes.add("NumberOfDirectedEdges");
		undirNodeAttributes.add("PartnerOfMultiEdgedNodePairs");

		edgeAttributes = new HashMap<String, String>(2);
		edgeAttributes.put("ebt", "EdgeBetweenness");
		edgeAttributes.put("dpe", "NumberOfUnderlyingEdges");
	}

	// Dialog titles
	public static String DT_DIRECTED = " (directed)";
	public static String DT_UNDIRECTED = " (undirected)";

	// Short informative messages to the user
	public static String SM_CONNECTED = " is connected, i.e. has a single connected component.";
	public static String SM_CREATEVIEW = constructLabel("No nodes are selected.",
			"Please create a network view and select nodes.");
	public static String SM_DEFFAILED = "An I/O error occurred while saving the settings as default.";
	public static String SM_DONE = "done";
	public static String SM_FILEEXISTS = "<html>The specified file already exists.<br>Overwrite?";
//
//	public static String SM_FITLINE = "<html>A line in the form <b><font face=Monospaced>y = a + bx</font></b> was fitted.</html>";
//	public static String SM_FITLINEERROR = "Could not fit line to the points.";
//	public static String SM_FITLINENODATA = "There are not enough data points to fit a line.";
//	public static String SM_FITNONPOSITIVE = "<html>Some data points have non-positive coordinates.<br>Only points with positive coordinates are included in the fit.</html>";
//	public static String SM_FITPL = "<html>A power law of the form <b><font face=Monospaced>y = ax<sup>b</sup></font></b> was fitted.</html>";
//	public static String SM_FITPLERROR = "Could not fit power law to the points.";
//	public static String SM_FITPLNODATA = "There are not enough data points to fit a power law.";
//	public static String SM_GUIERROR = "An error occurred while initializing the window.";
//	public static String SM_IERROR = "An error occurred while opening or reading from the file.";
//	public static String SM_NOINPUTFILES = "No network files found in the specified input directory.";
	public static String SM_INTERNALERROR = "Internal error occurred during computation.";
//	public static String SM_LOADSETTINGSFAIL1 = "NetworkAnalyzer: Loading settings from ";

//	public static String SM_LOADSETTINGSFAIL2 = " failed.";
	public static String SM_LOGERROR = "NetworkAnalyzer - Internal Error";
	public static String SM_NETWORKEMPTY = "Network contains no nodes.";
//	public static String SM_NETMODIFICATION = "<html><b>Note:</b> This operation cannot be undone.</html>";
	public static String SM_NETWORKFILEINVALID = "Network file is invalid.";
	public static String SM_NETWORKNOTOPENED = "Could not load network from file.";
//	public static String SM_OERROR = "An error occurred while creating or writing to the file.";
	public static String SM_OUTPUTIOERROR = "Could not save network statistics file.";
	public static String SM_OUTPUTNOTCREATED = "Could not write to output directory.";
//	public static String SM_READERROR = "\n  ERROR: Could not create network from network file!\n";
	public static String SM_REMDUPEDGES = " duplicated edge(s) removed from ";
//	public static String SM_REMOVEFILTER = "Do you want to restore the whole range for this topological parameter?";
//	public static String SM_REMSELFLOOPS = " self-loop(s) removed from ";

//	public static String SM_SECERROR1 = "NetworkAnalyzer could not be initialized due to security restrictions.";
//	public static String SM_SECERROR2 = "The operation was stopped due to security restrictions.";

//	public static String SM_SELECTNET = "Please select a network from the list of loaded networks.";
//	public static String SM_VISUALIZEERROR = "Parameters cannot be visualized because the network was modified or deleted.";
	public static String SM_UNKNOWNERROR = "Unknown error occurred.";
//	public static String SM_UNLOADING = "Unloading ";
//	public static final String SM_WRONGDATAFILE = "The file specified is not recognized as Network Statistics file.";

	// Menu items added in Cytoscape
//
	public static String AC_ANALYZE = "Analyze Network...";
//	public static String AC_ANALYZE_SUBSET = "Analyze Subset of Nodes...";

	/**
	 * Name of Submenu in Cytoscape's menubar, where network analysis actions are added.
	 */
	public static String AC_MENU_ANALYSIS = "Network Analysis[1.0]";

	/**
	 * Name of Submenu in Cytoscape's menubar, where network modification actions are added.
	 */
//	public static String AC_MENU_MODIFICATION = "Subnetwork Creation[2.0]";
//	public static String AC_PLOTPARAM = "Plot Parameters...";
//	public static String AC_SETTINGS = "Settings...";
//	public static String AC_REMDUPEDGES = "Remove Duplicated Edges...";
//	public static String AC_REMSELFLOOPS = "Remove Self-Loops...";
//	public static String AC_MAPPARAM = "Generate Style from Statistics...";


	// Messages related to the network interpretation

	public static String NI_COMBPAIRED = "Combine paired edges.";
	public static String NI_DIRPAIRED = "The network contains only directed edges and they are paired.";
	public static String NI_DIRUNPAIRED = "The network contains only directed edges and they are not paired.";
	public static String NI_FORCETU = "It will be treated as undirected.";
	public static String NI_IGNOREUSL = "Ignore undirected self-loops.";
	public static String NI_LOOPSBOTH = "It also contains both directed and undirected self-loops.";
	public static String NI_LOOPSDIR = "It also contains directed self-loops.";
	public static String NI_LOOPSUNDIR = "It also contains undirected self-loops.";
	public static String NI_MIXED = "The network contains both directed and undirected edges.";
	public static String NI_NOTCOMB = "Do not combine paired edges.";
	public static String NI_PAIRED = "The directed edges are paired.";
	public static String NI_R_DIR = "Directed.";
	public static String NI_R_DIRL = " Undirected self-loops were ignored.";
	public static String NI_R_UNDIR = "Undirected.";
	public static String NI_R_UNDIRC = " Paired edges were combined.";
	public static String NI_TD = "Treat the network as directed.";
	public static String NI_TU = "Treat the network as undirected.";
	public static String NI_UNDIR = "The network contains only undirected edges.";
	public static String NI_UNPAIRED = "The directed edges are not paired.";


	/**
	 * Checks if a description for a given simple parameter is present.
	 * 
	 * @param aParamID
	 *            ID of the simple parameter to inspect.
	 * @return <code>true</code> if a human-readable description for the specified simple parameter exists;
	 *         <code>false</code> otherwise.
	 */
	public static boolean containsSimpleParam(String aParamID) {
		return simpleParams.containsKey(aParamID);
	}

	/**
	 * Gets simple parameter description by the specified ID.
	 * 
	 * @param aParamID
	 *            ID of the simple parameter to get.
	 * @return Human-readable description mapped to the given <code>aParamID</code>; <code>null</code> if such
	 *         a description does not exist.
	 */
	public static String get(String aParamID) {
		return simpleParams.get(aParamID);
	}

	/**
	 * Gets attribute name for the specified ID.
	 * 
	 * @param aID
	 *            ID of attribute name.
	 * @return Attribute name in human-readable form that is mapped to the given ID; <code>null</code> if such
	 *         a name does not exist.
	 */
	public static String getAttr(String aID) {
		String attribute = nodeAttributes.get(aID);
		if (attribute == null) {
			attribute = edgeAttributes.get(aID);
		}
		return attribute;
	}

	/**
	 * Gets all possible computed edge attributes.
	 * 
	 * @return Set of the names of all edge attributes which are computed by NetworkAnalyzer.
	 */
	public static Set<String> getEdgeAttributes() {
		return new HashSet<String>(edgeAttributes.values());
	}

	/**
	 * Gets all possible computed node attributes.
	 * 
	 * @return Set of the names of all node attributes which are computed by NetworkAnalyzer.
	 */
	public static Set<String> getNodeAttributes() {
		return new HashSet<String>(nodeAttributes.values());
	}

	/**
	 * Gets node attributes computed for directed network interpretation.
	 * 
	 * @return Set of the names of all node attributes which are computed by NetworkAnalyzer for a directed
	 *         network interpretation.
	 */
//	public static Set<String> getDirNodeAttributes() {
//		return new HashSet<String>(dirNodeAttributes);
//	}

	/**
	 * Gets node attributes computed for undirected network interpretation.
	 * 
	 * @return Set of the names of all node attributes which are computed by NetworkAnalyzer for an undirected
	 *         network interpretation.
	 */
//	public static Set<String> getUndirNodeAttributes() {
//		return new HashSet<String>(undirNodeAttributes);
//	}

	/**
	 * Constructs a two-line message for an HTML label.
	 * 
	 * @param aLine1
	 *            First line of the text in the label. This text will be bold.
	 * @param aLine2
	 *            Second line of the text in the label.
	 * @return String of newly constructed HTML message.
	 */
	public static String constructLabel(String aLine1, String aLine2) {
		return "<html><b>" + aLine1 + "</b><br><br>" + aLine2 + "</html>";
	}

	/**
	 * Constructs a multi-line report for an HTML label.
	 * 
	 * @param aValues
	 *            Values to be reported, one value per network.
	 * @param aAction
	 *            Action performed on each network.
	 * @param aNetworks
	 *            Networks on which the action was performed.
	 * @return String of newly constructed HTML report.
	 * 
	 * @throws IllegalArgumentException
	 *             If the length of <code>aValues</code> is different than the length of
	 *             <code>aNetworks</code>.
	 * @throws NullPointerException
	 *             If ant of the given parameters is <code>null</code>.
	 */
//	public static String constructReport(int[] aValues, String aAction, String[] aNetworks) {
//		if (aValues.length != aNetworks.length) {
//			throw new IllegalArgumentException();
//		}
//		final StringBuilder answer = new StringBuilder("<html>");
//		for (int i = 0; i < aValues.length; ++i) {
//			answer.append(String.valueOf(aValues[i]) + Msgs.SM_REMDUPEDGES + aNetworks[i]);
//			answer.append("<br>");
//		}
//		answer.append("</html>");
//		return answer.toString();
//	}
}
