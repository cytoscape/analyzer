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
import java.util.Map;

/**
 * Brief documentation for the summary statistics shown in the results panel: a one-line
 * plain-text description per statistic for tooltips, and a longer HTML one -- formula
 * included, where there is one -- for the description area. Keyed by the same statistic
 * IDs as {@link Msgs} and {@link NetworkStats}.
 */
public class StatsDoc {

	/** One-line plain-text descriptions, for tooltips. */
	private static final Map<String, String> shortDocs;

	/** Longer HTML descriptions, without the enclosing &lt;html&gt; tag. */
	private static final Map<String, String> longDocs;

	/** Replacements for {@link #longDocs} entries whose text differs under a directed analysis. */
	private static final Map<String, String> directedLongDocs;

	static {
		shortDocs = new HashMap<String, String>(16);
		shortDocs.put("nodeCount", "Total number of nodes in the network.");
		shortDocs.put("edgeCount", "Total number of edges in the network.");
		shortDocs.put("avNeighbors", "Average number of neighbors per node.");
		shortDocs.put("ncc", "Number of disconnected parts the network splits into.");
		shortDocs.put("diameter", "Largest distance between any two connected nodes.");
		shortDocs.put("radius", "Smallest node eccentricity; a node's eccentricity is its largest distance to any other node.");
		shortDocs.put("avSpl", "Average shortest-path length between connected node pairs.");
		shortDocs.put("cc", "How connected each node's neighbors are to one another, on average (0–1).");
		shortDocs.put("density", "Fraction of possible edges that actually exist (0–1).");
		shortDocs.put("heterogeneity", "Variability of the node degrees; high values indicate hub nodes.");
		shortDocs.put("centralization", "How star-like the network is: near 1 = one central hub, near 0 = uniform connectivity.");
		shortDocs.put("connPairs", "Number of node pairs connected by at least one path.");
		shortDocs.put("nsl", "Number of edges that connect a node to itself.");
		shortDocs.put("mnp", "Number of node pairs connected by more than one edge.");
		shortDocs.put("usn", "Number of nodes without any edges.");

		longDocs = new HashMap<String, String>(16);
		longDocs.put("nodeCount",
			"The total number of nodes in the network, including nodes with no connections.");
		longDocs.put("edgeCount",
			"The total number of edges in the network, as interpreted by the analysis"
			+ " (paired edges may have been combined, depending on the chosen interpretation).");
		longDocs.put("avNeighbors",
			"The average number of neighbors over all nodes, indicating the average connectivity"
			+ " of a node in the network. A normalized version of this value is the network density.");
		longDocs.put("ncc",
			"A connected component is a maximal set of nodes such that each pair of them is linked"
			+ " by a path. The number of connected components indicates how fragmented the network"
			+ " is: a fully connected network has a single component. Components are computed"
			+ " ignoring edge direction.");
		longDocs.put("diameter",
			"The distance between two nodes is the length, in edges, of the shortest path between"
			+ " them. The network diameter is the largest distance between any two nodes;"
			+ " node pairs with no connecting path are ignored.");
		longDocs.put("radius",
			"The eccentricity of a node is the largest distance between it and any node reachable"
			+ " from it. The network radius is the smallest node eccentricity"
			+ " (the diameter is the largest).");
		longDocs.put("avSpl",
			"Also known as the average shortest path length: the average of the distances between"
			+ " all pairs of nodes that are connected by a path. It gives the expected distance"
			+ " between two randomly chosen connected nodes.");
		longDocs.put("cc",
			"The clustering coefficient of a node <i>n</i> measures how connected its neighbors"
			+ " are to one another: <i>C<sub>n</sub></i> = 2<i>e<sub>n</sub></i> /"
			+ " (<i>k<sub>n</sub></i>(<i>k<sub>n</sub></i> - 1)), where <i>k<sub>n</sub></i> is"
			+ " the number of neighbors of <i>n</i> and <i>e<sub>n</sub></i> is the number of"
			+ " edges between those neighbors. The network clustering coefficient is the average"
			+ " of <i>C<sub>n</sub></i> over all nodes with two or more neighbors. It ranges from"
			+ " 0 (no neighbor of any node connects to another neighbor) to 1 (every neighborhood"
			+ " is fully connected).");
		longDocs.put("density",
			"The fraction of possible edges that actually exist:"
			+ " <i>D</i> = 2<i>E</i> / (<i>N</i>(<i>N</i> - 1)) for a network with <i>N</i> nodes"
			+ " and <i>E</i> edges. It ranges from 0 (no edges) to 1 (every pair of nodes is"
			+ " connected); self-loops and duplicated edges are not considered.");
		longDocs.put("heterogeneity",
			"The coefficient of variation of the node degrees: the standard deviation of the"
			+ " degrees divided by their mean. It reflects the tendency of the network to contain"
			+ " hub nodes; biological networks are typically highly heterogeneous.");
		longDocs.put("centralization",
			"An index of how strongly the connectivity is concentrated in a few nodes:"
			+ " <i>C</i> = (<i>N</i> / (<i>N</i> - 2)) &#183;"
			+ " (<i>k</i><sub>max</sub> / (<i>N</i> - 1) - <i>D</i>), where <i>k</i><sub>max</sub>"
			+ " is the maximum node degree and <i>D</i> is the network density. Star-like networks"
			+ " have centralization close to 1, whereas networks where every node has the same"
			+ " number of neighbors have centralization close to 0.");
		longDocs.put("connPairs",
			"The number of ordered pairs of nodes that are connected by at least one path."
			+ " The shortest path statistics (characteristic path length, diameter and radius)"
			+ " are computed over these pairs.");
		longDocs.put("nsl",
			"The number of edges whose source and target are the same node.");
		longDocs.put("mnp",
			"The number of pairs of nodes that are connected by more than one edge"
			+ " (duplicated or parallel edges).");
		longDocs.put("usn",
			"The number of nodes with no incident edges (degree zero).");

		directedLongDocs = new HashMap<String, String>(8);
		directedLongDocs.put("avNeighbors", longDocs.get("avNeighbors")
			+ " Neighbors are counted regardless of edge direction.");
		directedLongDocs.put("cc",
			"The clustering coefficient of a node <i>n</i> measures how connected its neighbors"
			+ " are to one another: <i>C<sub>n</sub></i> = <i>e<sub>n</sub></i> /"
			+ " (<i>k<sub>n</sub></i>(<i>k<sub>n</sub></i> - 1)), where <i>k<sub>n</sub></i> is"
			+ " the number of neighbors of <i>n</i> and <i>e<sub>n</sub></i> is the number of"
			+ " directed edges between those neighbors. The network clustering coefficient is the"
			+ " average of <i>C<sub>n</sub></i> over all nodes with two or more neighbors."
			+ " It ranges from 0 (no clustering) to 1 (every neighborhood is fully connected).");
		directedLongDocs.put("density",
			"The fraction of possible edges that actually exist:"
			+ " <i>D</i> = <i>E</i> / (<i>N</i>(<i>N</i> - 1)) for a directed network with"
			+ " <i>N</i> nodes and <i>E</i> edges, since each pair of nodes may be connected by an"
			+ " edge in each direction. It ranges from 0 (no edges) to 1 (fully connected);"
			+ " self-loops and duplicated edges are not considered.");

		// These are defined on shortest paths, which follow edge direction when the network is
		// analyzed as directed
		for (String key : new String[] { "diameter", "radius", "avSpl", "connPairs" })
			directedLongDocs.put(key, longDocs.get(key)
				+ " In a directed analysis, paths follow edge direction.");
	}

	/** The one-line tooltip description of the given statistic; null if there is none. */
	public static String getShort(String aParamID) {
		return shortDocs.get(aParamID);
	}

	/**
	 * The long HTML description of the given statistic, under the given interpretation;
	 * null if there is none.
	 */
	public static String getLong(String aParamID, boolean aDirected) {
		if (aDirected) {
			String doc = directedLongDocs.get(aParamID);
			if (doc != null)
				return doc;
		}
		return longDocs.get(aParamID);
	}
}
