package org.cytoscape.analyzer.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.cytoscape.analyzer.ConnComponentAnalyzer;
import org.cytoscape.analyzer.NetworkAnalyzer;
import org.cytoscape.analyzer.UndirNetworkAnalyzer;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;

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

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;

/**
 * Immutable storage of information on a connected component.
 * 
 * @author Yassen Assenov
 */
public class ConnectedComponentInfo implements Comparable<ConnectedComponentInfo> {

	/**
	 * Initializes a new instance of <code>CCInfo</code>.
	 * 
	 * @param aSize Size of the connected component (number of nodes).
	 * @param aNode One of the nodes in the component.
	 */
	public ConnectedComponentInfo(NetworkAnalyzer analyzer, int aSize, CyNode aNode, boolean directed) {
		size = aSize;
		node = aNode;
		isPaired = !directed;
		parent = analyzer;
		sharedNeighborsHist = new long[size];
		sPathLengths = new long[size];
		if (NetworkAnalyzer.verbose) 	System.out.println("Component of size " + aSize);
	}
	NetworkAnalyzer parent;
boolean isPaired = false;
	/**
	 * Number of nodes in the connected component.
	 */
	private int size;
	/**
	 * Gets the size of the connected component.
	 * 
	 * @return Number of nodes in the connected component.
	 */
	public int getSize() {		return size;	}

	/**
	 * Gets a node from the connected component.
	 * 
	 * @return Node belonging to this connected component.
	 */
	public CyNode getNode() {		return node;	}


	/**
	 * Histogram of shortest path lengths.
	 * <p>
	 * <code>sPathLength[0]</code> stores the number of nodes processed so far.<br/>
	 * <code>sPathLength[i]</code> for <code>i &gt; 0</code> stores the number of shortest paths of
	 * length <code>i</code> found so far.
	 * </p>
	 */
	protected long[] sPathLengths;

	/**
	 * Histogram of pairs of nodes that share common neighbors. The i-th element of this array
	 * accumulates the number of node pairs that share i neighbors.
	 */
	protected long[] sharedNeighborsHist;

	/**
	 * Flag indicating if node(edge) betweenness and stress should be computed. It is set to false if the
	 * number of shortest paths exceeds the maximum long value.
	 */
	// protected boolean computeNB;

	// protected int nodeCount;
	
	// Starting from here are variables initialized and used in computeAll()
	protected int networkEdgeCount;
	protected SimpleUndirParams params;
	protected int maxConnectivity;
	protected DegreeDistribution degreeDist;
	protected HashMap<Integer, SumCountPair> CCps;	// clustering coefficients
	protected ArrayList<Point2D.Double> topCoefs;	// topological coefficients
	protected ArrayList<Point2D.Double> closenessCent;	// closeness centrality
	protected ArrayList<Point2D.Double> nodeBetweennessArray;	// node betweenness
	protected HashMap<Integer, SumCountPair> NCps;	// neighborhood connectivity
	protected Map<CyNode, Double> aplMap;	// average shortest path length
	protected LogBinDistribution stressDist;	// stress
	protected double[] nodeBetweennessLean;
	protected double[] edgeBetweennessLean;
	protected long[] stressLean;
	protected int componentDiameter;
	protected CyNetwork network;
	protected NetworkStats 	stats = new NetworkStats();

	

//	private int networkEdgeCount;
	/**
	 * One of the nodes in the connected component.
	 */
	private CyNode node;		// the node currently being processed
	private Set<CyNode> connNodes;
	final Set<CyEdge> connEdges = new HashSet<CyEdge>();
	final HashMap<CyNode, Integer> node2Int = new HashMap<>();
	final HashMap<Long, Integer> edgeHash2Int = new HashMap<>();
	int[] edgeOffsets;
	int numNodes;
	int[] edges;
	int[] edgeIDs;

	public int nodeToInt(CyNode node)	{ 	return node2Int.get(node);	}
	public Integer edgeToInt(Long node)	{ 	return edgeHash2Int.get(node);	}
	
	public Set<CyNode> getAllNodes() 	{ 	return connNodes;	}
	
	public void analyze(CyNetwork net, 	ConnComponentAnalyzer cca ) 
	{
		network = net;
		networkEdgeCount = 0;
		params = new SimpleUndirParams();
		maxConnectivity = 0;
		connNodes = cca.getNodesOf(this);
		numNodes = connNodes.size();
		size = numNodes;
		degreeDist = new DegreeDistribution(size);						
		CCps = new HashMap<Integer, SumCountPair>();					// clustering coefficients
		topCoefs = new ArrayList<Point2D.Double>(size);					// topological coefficients
		closenessCent = new ArrayList<Point2D.Double>(size);			// closeness centrality
		nodeBetweennessArray = new ArrayList<Point2D.Double>(size);		// node betweenness
		NCps = new HashMap<Integer, SumCountPair>();					// neighborhood connectivity
		aplMap = new HashMap<CyNode, Double>();							// average shortest path length
		stressDist = new LogBinDistribution();							// stress
		// Get nodes of connected component
		
		
		edgeOffsets = new int[numNodes + 1];
		int numEdgesLocal = 0;
		for (CyNode node : connNodes)
		{
			edgeOffsets[node2Int.size()] = numEdgesLocal;
			node2Int.put(node, node2Int.size());
			numEdgesLocal += getNeighbors(node).size();
			for (CyEdge edge : getIncidentEdges(node, false))
				connEdges.add(edge);
		}
		final int numEdges = numEdgesLocal;
		edgeOffsets[numNodes] = numEdges;
		edges = new int[numEdges];
		edgeIDs = new int[numEdges];

		int e = 0;
		for (CyNode node : connNodes)
		{
			int nodeID = node2Int.get(node);
			int offset = edgeOffsets[nodeID];
			for (CyNode neighbor : getNeighbors(node))
			{
				int neighborID = node2Int.get(neighbor);
				long edgeHash = computeEdgeHash(nodeID, neighborID);
				if (!edgeHash2Int.containsKey(edgeHash))
					edgeHash2Int.put(edgeHash, e++);
				int edgeID = edgeHash2Int.get(edgeHash);
				edgeIDs[offset] = edgeID;
				edges[offset++] = neighborID;
			}
		}
		
		
		final Queue<CyNode> nodeQueue = new LinkedList<>();
		for (CyNode node : getAllNodes())
			nodeQueue.add(node);
		run(nodeQueue, numEdges);
		
	}
	boolean paired = false;

		
	private void run(Queue<CyNode> nodeQueue, int numEdges) 
	{
		int localNetworkEdgeCount = 0;	
		int localMaxConnectivity = 0;
		int localComponentDiameter = 0;
		long[] localSharedNeighborsHist = new long[sharedNeighborsHist.length];
		long[] localSPathLengths = new long[sPathLengths.length];

		
		nodeBetweennessLean = new double[numNodes];
		edgeBetweennessLean = new double[numEdges];
		stressLean = new long[numNodes];

		double[] localNodeBetweenness = new double[nodeBetweennessLean.length];
		double[] localEdgeBetweenness = new double[edgeBetweennessLean.length];
		long[] localStress = new long[stressLean.length];
		componentDiameter = 0;
		
	
		
		while (nodeQueue.size() > 0)
		{
			CyNode node = null;
			if (nodeQueue.size() == 0)			break;
			node = nodeQueue.remove();
			parent.progress++;
			
			long timeStart = System.nanoTime();
			int nodeID = nodeToInt(node);
			List<CyEdge> incEdges = getIncidentEdges(node, paired);
			Map<CyNode, MutInteger> neighborMap = CyNetworkUtils.getNeighborMap(parent.network, node, incEdges);
			CyRow row = parent.network.getRow(node);

			// Degree distribution calculation
			int degree = getDegree(node, incEdges, paired);
			
			localNetworkEdgeCount += degree;
			degreeDist.addObservation(degree);
			row.set("Degree",degree);
			
			int neighborCount = calcSimple(node, incEdges, neighborMap, params);
			localMaxConnectivity = Math.max(localMaxConnectivity, neighborCount);
			

			int firstEdge = edgeOffsets[nodeID], lastEdge = edgeOffsets[nodeID + 1];
			if (neighborCount > 0) 
			{
				int[] neighbors = new int[lastEdge - firstEdge];
				for (int ei = firstEdge; ei < lastEdge; ei++)
					neighbors[ei - firstEdge] = edges[ei];

				// Neighborhood connectivity computation
				; // = averageNeighbors(neighbors, edgeOffsets);
				
					double accum = 0;
					for (int n : neighbors)
						accum += edgeOffsets[n + 1] - edgeOffsets[n];
					
					double neighborConnect = accum / neighbors.length;
				
				parent.accumulate(NCps, neighborCount, neighborConnect);

				if (neighborCount > 1) 
				{
					// Topological coefficients computation
					double topCoef = UndirNetworkAnalyzer.computeTC(nodeID, numNodes, edges, edgeOffsets);
					if (!Double.isNaN(topCoef)) 
						topCoefs.add(new Point2D.Double(neighborCount, topCoef));
					else 
						topCoef = 0.0;

					// Clustering coefficients computation
					final double nodeCCp = UndirNetworkAnalyzer.computeCC(neighbors, numNodes, edges, edgeOffsets);
					parent.accumulate(CCps, neighborCount, nodeCCp);
					row.set( "ClusteringCoefficient", nodeCCp);
					row.set( "TopologicalCoefficient", topCoef);

				} 
				else  
				{
					row.set("ClusteringCoefficient", 0.0);
					row.set("TopologicalCoefficient", 0.0);
				}
				row.set( "NeighborhoodConnectivity", neighborConnect);
			} 
			else 
			{
				row.set( "NeighborhoodConnectivity", 0.0);
				row.set( "ClusteringCoefficient", 0.0);
				row.set( "TopologicalCoefficient", 0.0);
			}
			if (parent.cancelled) 
				break;
			
			PathLengthData pathLengths = UndirNetworkAnalyzer.computeSPandSN(nodeID, numNodes, edges, edgeOffsets, localSharedNeighborsHist, localSPathLengths);
			
			int eccentricity = pathLengths.getMaxLength();
			if (params.diameter < eccentricity)
				params.diameter = eccentricity;
			if (0 < eccentricity && eccentricity < params.radius)
				params.radius = eccentricity;
			localComponentDiameter = Math.max(localComponentDiameter, eccentricity);
			
			double apl = (pathLengths.getCount() > 0) ? pathLengths.getAverageLength() : 0;
			aplMap.put(node, Double.valueOf(apl));
			
			double closeness = (apl > 0.0) ? 1 / apl : 0.0;
			closenessCent.add(new Point2D.Double(neighborCount, closeness));
			

			// Store max. and avg. shortest path lengths, and closeness in node attributes
			row.set( Msgs.getAttr("spl"), eccentricity);
			row.set( Msgs.getAttr("apl"), apl);
			row.set( Msgs.getAttr("clc"), closeness);

			// CyNode and edge betweenness calculation
			UndirNetworkAnalyzer.computeNBandEB(nodeID, numNodes, edges, edgeOffsets, edgeIDs, 
			                                    localNodeBetweenness, localStress, localEdgeBetweenness);

			if (parent.cancelled)
				break;
		} // end node iteration

	// Reduce results into global (parent's) variables
		
		accumulate(localNetworkEdgeCount, localMaxConnectivity, localMaxConnectivity);
		accumulate(localSharedNeighborsHist, localSPathLengths, localNodeBetweenness, localEdgeBetweenness, localStress);
		saveStatistics();

	// Normalize and save node betweenness
	for (final CyNode n : connNodes) 
	{
		CyRow row = parent.network.getRow(n);
		int nodeID = nodeToInt(n);
		// Compute node radiality
		final double rad = (componentDiameter + 1.0 - aplMap.get(n).doubleValue()) / componentDiameter;
		row.set( "Radiality", rad);
		
		// normalize
		final double nNormFactor = UndirNetworkAnalyzer.computeNormFactor(numNodes);		
		double nb = nodeBetweennessLean[nodeID] * nNormFactor;
		if (Double.isNaN(nb)) 		nb = 0.0;

		// degree, betweenness
		final int degree = getDegree(n, getIncidentEdges(n, paired), paired);
		nodeBetweennessArray.add(new Point2D.Double(degree, nb));
		row.set( Msgs.getAttr("nbt"), nb);

		// stress
		final long nodeStress = stressLean[nodeID];
		row.set( "Stress", nodeStress);
		stressDist.addObservation(nodeStress);
	} 
	
	// Save edge betweenness
	for (CyEdge edge : connEdges)
	{
		int sourceID = nodeToInt(edge.getSource());
		int targetID = nodeToInt(edge.getTarget());
		long edgeHash = computeEdgeHash(sourceID, targetID);						
		double eb = Double.NaN;
		if (edgeToInt(edgeHash) != null)
			eb = edgeBetweennessLean[(int) edgeToInt(edgeHash)];
		if (Double.isNaN(eb)) 	eb = 0.0;
		CyRow edgeRow = network.getRow(edge);
		edgeRow.set( Msgs.getAttr("ebt"), eb);
	}
}
/**
 * Computes the average number of neighbors of the nodes in a given node set.
 * 
 * @param aNodes
 *            Non-empty set of nodes. Specifying <code>null</code> or an empty set for this
 *            parameter results in throwing an exception.
 * @return Average number of neighbors of the nodes in <code>nodes</code>.
 */
	private double averageNeighbors(int[] nodes, int[] edgeOffsets)
	{
		int neighbors = 0;
		for (int node : nodes)
			neighbors += edgeOffsets[node + 1] - edgeOffsets[node];
		
		return (double)neighbors / (double)nodes.length;
	}

	/**
	 * Calculates a set of simple properties of the given node.
	 * 
	 * @param aNodeID
	 *            ID of the node of interest. This parameter is used for storing attribute values.
	 * @param aIncEdges
	 *            Array of the indices of all the neighbors of the node of interest.
	 * @param aNeMap
	 *            Map of neighbors of the node of interest and their frequency.
	 * @param aParams
	 *            Instance to accumulate the computed values.
	 * @return Number of neighbors of the node of interest.
	 */
	protected int calcSimple(CyNode aNode, List<CyEdge> aIncEdges, Map<CyNode, MutInteger> aNeMap, SimpleUndirParams aParams) 
	{
		final int neighborCount = aNeMap.size();
	
		// Avg. number of neighbors, density & centralization calculation
		if (aParams.connectivityAccum != null)
			aParams.connectivityAccum.add(neighborCount);
		else
			aParams.connectivityAccum = new SumCountPair(neighborCount);
		
		// Heterogeneity calculation
		if (aParams.sqConnectivityAccum != null)
			aParams.sqConnectivityAccum.add(neighborCount * neighborCount);
		else
			aParams.sqConnectivityAccum = new SumCountPair(neighborCount * neighborCount);

		// Number of unconnected nodes calculation
		if (neighborCount == 0)
			aParams.unconnectedNodeCount++;
	
		// Number of self-loops and number of directed/undirected edges
		// calculation
		boolean isDirected = parent.isDirected();
		int selfLoops = 0;
		int dirEdges = 0;
		for (int j = 0; j < aIncEdges.size(); j++) 
		{
			CyEdge e = aIncEdges.get(j);
			if (isDirected && e.isDirected())
				dirEdges++;
			if (e.getSource() == e.getTarget())
				selfLoops++;
		}
		aParams.selfLoopCount += selfLoops;
		int undirEdges = aIncEdges.size() - dirEdges;
	
		// Number of multi-edge node partners calculation
		int partnerOfMultiEdgeNodePairs = 0;
		for (final MutInteger freq : aNeMap.values()) 
			if (freq.value > 1)
				partnerOfMultiEdgeNodePairs++;
		aParams.multiEdgePartners += partnerOfMultiEdgeNodePairs;
	
		// Storing the values in attributes
		CyRow nodeRow = network.getRow(aNode);
		nodeRow.set(Msgs.getAttr("slo"), selfLoops);
		nodeRow.set(Msgs.getAttr("isn"), (neighborCount == 0));
		nodeRow.set(Msgs.getAttr("nue"), undirEdges);
		nodeRow.set(Msgs.getAttr("nde"), dirEdges);
		nodeRow.set(Msgs.getAttr("pmn"), partnerOfMultiEdgeNodePairs);
		return neighborCount;
	}

	/**
	 * Gets all the neighbors of the given node.
	 * 
	 * @param aNode
	 *            CyNode , whose neighbors are to be found.
	 * @return <code>Set</code> of <code>Node</code> instances, containing all the neighbors of
	 *         <code>aNode</code>; empty set if the node specified is an isolated vertex.
	 * @see CyNetworkUtils#getNeighbors(CyNetwork, CyNode , int[])
	 */
	protected Set<CyNode> getNeighbors(CyNode aNode) {
		return CyNetworkUtils.getNeighbors(network, aNode, getIncidentEdges(aNode, false));
	}
	/**
	 * Gets all edges incident on the given node.
	 * 
	 * @param aNode
	 *            CyNode , on which incident edges are to be found.
	 * @return Array of edge indices, containing all the edges in the network incident on
	 *         <code>aNode</code>.
	 */
	public List<CyEdge> getIncidentEdges(CyNode aNode, boolean paired) {
		return network.getAdjacentEdgeList(aNode, (paired ? CyEdge.Type.INCOMING : CyEdge.Type.ANY));
	}
	/**
	 * Computes a direction-invariant 64 bit hash of an edge (represented by its two nodes' IDs).
	 * @param ID of the first node
	 * @param ID of the second node
	 * @return 64 bit hash, where the left 32 bit are the smaller ID, and the right 32 bit the larger ID
	 */
	public static long computeEdgeHash(int id1, int id2)
	{
		int smaller = id1 < id2 ? id1 : id2;
		int bigger = id1 > id2 ? id1 : id2;
		return (((long)smaller) << 32) + (long)bigger;
	}
	/**
	 * Gets the averages of the accumulated values and stores them in a set.
	 * <p>
	 * This method is used for computing the average neighborhood connectivity. In this case, the keys of the
	 * map are node connectivities and the values - the accumulated connectivities of the neighbors of a node
	 * with k links.
	 * </p>
	 * 
	 * @param pAccumulatedValues
	 *            Mapping of integers and accumulated values.
	 * @return Set of points that stores the averages of the accumulated values in the mapping. The
	 *         <code>x</code> coordinate of each point is a key in the mapping and the <code>y</code>
	 *         coordinate - the average of the accumulated numbers in the corresponding value of the map.
	 */
	protected static Set<Point2D.Double> getAverages(Map<Integer, SumCountPair> pAccumulatedValues) {
		Set<Point2D.Double> averages = new HashSet<Point2D.Double>(pAccumulatedValues.size());
		for (Integer x : pAccumulatedValues.keySet()) {
			final double y = pAccumulatedValues.get(x).getAverage();
			averages.add(new Point2D.Double(x.doubleValue(), y));
		}
		return averages;
	}
	/**
	 * Gets the degree of a given node.
	 * 
	 * @param aNode
	 *            CyNode to get the degree of.
	 * @param aIncEdges
	 *            Array of the indices of all edges incident on the given node.
	 * @return Degree of the given node, as defined in the book &qout;Graph Theory&qout; by Reinhard
	 *         Diestel.
	 */
	int getDegree(CyNode aNode, List<CyEdge> aIncEdges, boolean isPaired) {
		int degree = aIncEdges.size();
		for (int i = 0; i < aIncEdges.size(); ++i) {
			CyEdge e = aIncEdges.get(i);
			if (e.getSource() == e.getTarget() && (!(e.isDirected() && isPaired))) {
				degree++;
			}
		}
		return degree;
	}

	public void accumulate(int localNetworkEdgeCount, int localMaxConnectivity, int localMaxConnectivity2) {
		networkEdgeCount += localNetworkEdgeCount;
		maxConnectivity = Math.max(maxConnectivity, localMaxConnectivity);
		componentDiameter = Math.max(componentDiameter, localMaxConnectivity);
			
	}

	public void accumulate(long[] sharedNeighbors, long[] pathLens, double[] nodeBetweenness,
			double[] edgeBetweenness, long[] stress) {
		
		for (int i = 0; i < sharedNeighbors.length; i++)
			sharedNeighborsHist[i] += sharedNeighbors[i];
		for (int i = 0; i < pathLens.length; i++)
			sPathLengths[i] += pathLens[i];
		for (int i = 0; i < nodeBetweenness.length; i++)
			nodeBetweennessLean[i] += nodeBetweenness[i];
		for (int i = 0; i < edgeBetweenness.length; i++)
			edgeBetweennessLean[i] += edgeBetweenness[i];
		for (int i = 0; i < stress.length; i++)
			stressLean[i] += stress[i];
		
	}
	
	void saveStatistics() {
		// save statistics
		if (params.connectivityAccum != null) {
			final double meanConnectivity = params.connectivityAccum.getAverage();
			stats.set("avNeighbors", meanConnectivity);
			final double density = meanConnectivity / (numNodes - 1);
			stats.set("density", meanConnectivity / (numNodes - 1));
			stats.set("centralization", (numNodes / ((double) numNodes - 2))
					* (maxConnectivity / ((double) numNodes - 1) - density));
			final double nom = params.sqConnectivityAccum.getSum() * numNodes;
			final double denom = params.connectivityAccum.getSum()
					* params.connectivityAccum.getSum();
			stats.set("heterogeneity", Math.sqrt(nom / denom - 1));
		}

	// Save degree distribution in the statistics instance
		stats.set("degreeDist", degreeDist.createHistogram());

	// Save C(k) in the statistics instance
		if (CCps.size() > 0) {
			Point2D.Double[] averages = new Point2D.Double[CCps.size()];
			double cc = parent.accumulateCCs(CCps, averages) / numNodes;
			stats.set("cc", cc);
			if (averages.length > 1) 
				stats.set("cksDist", new Points2D(averages));
		}

		// Save topological coefficients in the statistics instance
		if (topCoefs.size() > 1) 
			stats.set("topCoefs", new Points2D(topCoefs));
	
		stats.set(Msgs.get("usn"), params.unconnectedNodeCount);
		stats.set(Msgs.get("nsl"), params.selfLoopCount);
		stats.set(Msgs.get("mnp"), params.multiEdgePartners / 2);
		if (isPaired) 
			stats.set("edgeCount", networkEdgeCount / 2);
	
		long connPairs = 0; // total number of connected pairs of nodes
		long totalPathLength = 0;
		for (int i = 1; i <= params.diameter; ++i) {
			connPairs += sPathLengths[i];
			totalPathLength += i * sPathLengths[i];
		}
		stats.set("connPairs", connPairs);
	
			// Save shortest path lengths distribution
		if (params.diameter > 0) {
			stats.set("diameter", params.diameter);
			stats.set("radius", params.radius);
			stats.set("avSpl", (double) totalPathLength / connPairs);
			if (params.diameter > 1) 
				stats.set("splDist", new LongHistogram(sPathLengths, 1, params.diameter));
			int largestCommN = 0;
			for (int i = 1; i < numNodes; ++i) {
				if (sharedNeighborsHist[i] != 0) {
					sharedNeighborsHist[i] /= 2;
					largestCommN = i;
				}
			}
			// Save common neighbors distribution
			if (largestCommN > 0) 
				stats.set("commNeighbors", new LongHistogram(sharedNeighborsHist, 1,largestCommN));
		}
	
	
		if (closenessCent.size() > 1) 
			stats.set("closenessCent", new Points2D(closenessCent));		// Save closeness centrality in the statistics instance
	
		if (nodeBetweennessArray.size() > 2) 							
			stats.set("nodeBetween", new Points2D(nodeBetweennessArray));	// Save node betweenness in the statistics instance
	
		if (NCps.size() > 1) 										
			stats.set("neighborConn", new Points2D(getAverages(NCps)));		// Save neighborhood connectivity in the statistics instance
	
		// if (computeNB) 						
			stats.set("stressDist", stressDist.createPoints2D());			// Save stress distribution in the statistics instance
		
	}

	@Override
	public int compareTo(ConnectedComponentInfo o) {
		return Integer.valueOf(getSize()).compareTo(Integer.valueOf(o.getSize()));
	}

	public NetworkStats getStats() { return stats; }
}
