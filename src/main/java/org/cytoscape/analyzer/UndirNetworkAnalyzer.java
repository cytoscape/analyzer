package org.cytoscape.analyzer;


import org.cytoscape.analyzer.util.AttributeSetup;
import org.cytoscape.analyzer.util.ConnectedComponentInfo;
import org.cytoscape.analyzer.util.NetworkInterpretation;
import org.cytoscape.analyzer.util.PathLengthData;
import java.util.List;
import java.util.Set;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

/**
 * Network analyzer for networks that contain undirected edges only.
 * 

 */
public class UndirNetworkAnalyzer extends NetworkAnalyzer
{
	/**
	 * Initializes a new instance of <code>UndirNetworkAnalyzer</code>.
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
	public UndirNetworkAnalyzer(CyNetwork aNetwork, NetworkInterpretation aInterpr, CySwingApplication app, AnalyzerManager mgr) 
	{
		super(aNetwork, aInterpr, app, mgr);
//		if (nodeSet != null) 
//			stats.set("nodeCount", nodeSet.size());
		AttributeSetup.createUndirectedNodeAttributes(aNetwork.getTable(CyNode.class, CyNetwork.LOCAL_ATTRS));
		AttributeSetup.createEdgeBetweennessAttribute(aNetwork.getTable(CyEdge.class, CyNetwork.LOCAL_ATTRS));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see NetworkAnalyzer#computeAll()
	 */
	@Override
	public void computeAll() 
	{
	System.out.println("computeALL");	
		long time = System.currentTimeMillis();
		analysisStarting();
		ConnComponentAnalyzer cca = new ConnComponentAnalyzer(this,network);						// Compute number of connected components
		Set<ConnectedComponentInfo> components = cca.findComponents();
		int connectedComponentCount = components.size();
		stats.set("ncc", connectedComponentCount);

		for (ConnectedComponentInfo aCompInfo : components) 
		{
			aCompInfo.analyze(network, cca);
			progress++;
		}
		analysisFinished();
		time = System.currentTimeMillis() - time;
		stats.set("time", time / 1000.0);
		doOutput();	}

	/**
	 * Computes the clustering coefficient of a node's neighborhood.
	 * @param neighbors Array with neighbor indices.
	 * @param numNodes Overall number of nodes in the graph.
	 * @param edges Array with every node's neighbor indices.
	 * @param edgeOffsets Array with the indices of each node's first neighbor in <code>edges</code>.
	 * @return Clustering coefficient in the range [0; 1].
	 */
	public static double computeCC(int[] neighbors, int numNodes, int[] edges, int[] edgeOffsets)
	{
		boolean[] isNeighbor = new boolean[numNodes];
		for (int neighbor : neighbors)
			isNeighbor[neighbor] = true;
		
		int edgeCount = 0;
		for (int neighbor : neighbors)
		{
			int firstEdge = edgeOffsets[neighbor], lastEdge = edgeOffsets[neighbor + 1];
			for (int ei = firstEdge; ei < lastEdge; ei++)
				if (isNeighbor[edges[ei]])
					edgeCount++;
		}
		
		long neighborsCount = (long)neighbors.length;
		return (double)edgeCount / (double)(neighborsCount * (neighborsCount - 1));
	}

	/**
	 * Computes the shortest path lengths from the given node to all other nodes in the network. In
	 * addition, this method accumulates values in the {@link #sharedNeighborsHist} histogram.
	 * <p>
	 * This method stores the lengths found in the array {@link #sPathLengths}.<br/>
	 * <code>sPathLengths[i] == 0</code> when i is the index of <code>aNode</code>.<br/>
	 * <code>sPathLengths[i] == Integer.MAX_VALUE</code> when node i and <code>aNode</code> are
	 * disconnected.<br/>
	 * <code>sPathLengths[i] == d &gt; 0</code> when every shortest path between node i and
	 * <code>aNode</code> contains <code>d</code> edges.
	 * </p>
	 * <p>
	 * This method uses a breadth-first traversal through the network, starting from the specified
	 * node, in order to find all reachable nodes and accumulate their distances to
	 * <code>aNode</code> in {@link #sPathLengths}.
	 * </p>
	 * 
	 * @param aNode
	 *            Starting node of the shortest paths to be found.
	 * @return Data on the shortest path lengths from the current node to all other reachable nodes
	 *         in the network.
	 */
	public static PathLengthData computeSPandSN(int node, int numNodes, int[] edges, int[] edgeOffsets, long[] outSharedNeighborsHist, long[] outSPathLengths) 
	{
		boolean[] visited = new boolean[numNodes];
		visited[node] = true;
		int[] frontier = new int[numNodes];
		int[] nextFrontier = new int[numNodes];
		frontier[0] = node;
		int frontierSize = 1;
		int i = 1;
		boolean[] startNeighbors = new boolean[numNodes];
		{
			int firstNeighbor = edgeOffsets[node], lastNeighbor = edgeOffsets[node + 1];
			for (int ni = firstNeighbor; ni < lastNeighbor; ni++)
				startNeighbors[edges[ni]] = true;
		}
		
		PathLengthData result = new PathLengthData();
		
		while (frontierSize > 0)
		{
			int nextFrontierSize = 0;
			
			for (int fi = 0; fi < frontierSize; fi++)
			{
				int n = frontier[fi];
				int firstNeighbor = edgeOffsets[n], lastNeighbor = edgeOffsets[n + 1];
				int sharedNeighbors = 0;
				
				for (int ni = firstNeighbor; ni < lastNeighbor; ni++)
				{
					int neighbor = edges[ni];
					if (startNeighbors[neighbor])
						sharedNeighbors++;
					if (!visited[neighbor])
					{
						visited[neighbor] = true;
						nextFrontier[nextFrontierSize++] = neighbor;
					}
				}
				int index = i > 2 ? sharedNeighbors : 0;
				outSharedNeighborsHist[index]++;
			}
			
			for (int nfi = 0; nfi < nextFrontierSize; nfi++)
			{
				frontier[nfi] = nextFrontier[nfi];
				result.addSPL(i);
			}
			if (i < outSPathLengths.length)
				outSPathLengths[i] += nextFrontierSize;
			frontierSize = nextFrontierSize;
			i++;
		}
		
		return result;
	}

	/**
	 * Accumulates the node and edge betweenness of all nodes in a connected component. The node
	 * betweenness is calculate using the algorithm of Brandes (U. Brandes: A Faster Algorithm for
	 * Betweenness Centrality. Journal of Mathematical Sociology 25(2):163-177, 2001). The edge
	 * betweenness is calculated as used by Newman and Girvan (M.E. Newman and M. Girvan: Finding
	 * and Evaluating Community Structure in Networks. Phys. Rev. E Stat. Nonlin. Soft. Matter
	 * Phys., 69, 026113.). In each run of this method a different source node is chosen and the
	 * betweenness of all nodes is replaced by the new one. For the final result this method has to
	 * be run for all nodes of the connected component.
	 * 
	 * This method uses a breadth-first search through the network, starting from a specified source
	 * node, in order to find all paths to the other nodes in the network and to accumulate their
	 * betweenness.
	 * 
	 * @param source
	 *     CyNode where a run of breadth-first search is started, in order to accumulate the
	 *     node and edge betweenness of all other nodes
	 */
	public static void computeNBandEB(int source, int numNodes, int[] edges, int[] edgeOffsets, int[] edgeIDs,
								double[] outNodeBetweenness, long[] outStress,  double[] outEdgeBetweenness)
	{		
		int[] Q = new int[numNodes];		// Serves as queue for the first part, as stack for the second part
		Q[0] = source;
		int Qlow = 0, Qhigh = 1;			// Keep track of queue's first and last element / stack size

		int[] P = new int[edges.length];	// Predecessors
		int[] Pedge = new int[edges.length];
		int[] Pcount = new int[numNodes];	// Predecessor count, for each node at most its edge count
		
		int[] Dedge = new int[edges.length];	// Edges to descendants
		int[] Dcount = new int[numNodes];
		
		int[] sigma = new int[numNodes];	// Sigma in Brandes paper, W in Newman
		sigma[source] = 1;
		
		int[] d = new int[numNodes];		// Distance from source, with source having d = 0
		for (int i = 0; i < numNodes; i++)
			d[i] = -1;
		d[source] = 0;
		
		double[] delta = new double[numNodes];	// Delta in Brandes paper
		
		long[] stressDependency = new long[numNodes];		// Keep track of node stress metric
		double[] edgeDependency = new double[edges.length];	// This round's edge betweenness values
		
		while (Qlow < Qhigh)	// While query.size > 0
		{
			int node = Q[Qlow++];	// Dequeue
			int firstEdge = edgeOffsets[node], lastEdge = edgeOffsets[node + 1];
			int dnodeplus = d[node] + 1;
			int sigmanode = sigma[node];
			
			for (int ei = firstEdge; ei < lastEdge; ei++)	// For each neighbor of node
			{
				int neighbor = edges[ei];
				
				if (d[neighbor] < 0)	// Has not been found yet
				{
					Q[Qhigh++] = neighbor;		// Enqueue
					d[neighbor] = dnodeplus;	// d[node] + 1
				}
				
				if (d[neighbor] == dnodeplus)	// Is descendant
				{
					sigma[neighbor] += sigmanode;
					int pi = edgeOffsets[neighbor] + Pcount[neighbor];	// Predecessor number
					P[pi] = node;	// Store node as its neighbor's predecessor
					Pedge[pi] = edgeIDs[ei];	// Also remember the edge from predecessor for edge betweenness later
					Pcount[neighbor]++;	// Got one more predecessor
					
					int di = edgeOffsets[node] + Dcount[node];
					Dedge[di] = edgeIDs[ei];
					Dcount[node]++;
				}
			}
		}
		
		while (Qhigh > 0)	// While stack.size > 0
		{
			int w = Q[--Qhigh];				// Pop from stack
			int firstP = edgeOffsets[w];	// First predecessor number
			int lastP = firstP + Pcount[w];	// Last predecessor number
			double sigmaw = 1.0 / (double)sigma[w], deltaw = delta[w];	// Precalc for later
			long stressw = stressDependency[w];

			double Dbetweenness = 0.0;		// Precalc 
			int firstD = edgeOffsets[w];
			int lastD = firstD + Dcount[w];
			boolean isLeaf = lastD - firstD == 0;
			for (int di = firstD; di < lastD; di++)
				Dbetweenness += edgeDependency[Dedge[di]];
			
			for (int pi = firstP; pi < lastP; pi++)	// For each predecessor
			{
				int v = P[pi];	// v is predecessor
				double sigmavw = (double)sigma[v] * sigmaw;	// Precalc
				delta[v] += sigmavw * (1 + deltaw);
				stressDependency[v] += 1 + stressw;
				
				double edgeBetweenness = 0;
				int edgeID = Pedge[pi];
				if (isLeaf)
					edgeBetweenness = sigmavw;
				else
					edgeBetweenness = (1.0 + Dbetweenness) * sigmavw;
				
				edgeDependency[edgeID] = edgeBetweenness;
				outEdgeBetweenness[edgeID] += edgeBetweenness;
			}
			
			if (w != source)
			{
				outNodeBetweenness[w] += deltaw;
				outStress[w] += sigma[w] * stressw;
			}
		}
	}
	
	/**
	 * Computes a normalization factor for node betweenness normalization.
	 * 
	 * @param count
	 *            Number of nodes for which betweenness has been computed.
	 * @return Normalization factor for node betweenness normalization.
	 */
	public static double computeNormFactor(int count) 
	{
		return (count > 2) ? (1.0 / ((count - 1) * (count - 2))) : 1.0;
	}


	/**
	 * Computes the topological coefficient of the given node.
	 * @param node The node's index.
	 * @param numNodes Number of nodes in the graph.
	 * @param edges Array with every node's neighbor indices.
	 * @param edgeOffsets Array with the indices of each node's first neighbor in <code>edges</code>.
	 * @return	The node's topological coefficient in the range [0; 1];
	 *          <code>NaN</code> in case the topological coefficient is not defined for this case.
	 */
	public static double computeTC(int node, int numNodes, int[] edges, int[] edgeOffsets)
	{
		boolean[] commNNodes = new boolean[numNodes];
		int commNNodesSize = 0;
		boolean[] isNeighbor = new boolean[numNodes];
		int tc = 0;
		
		int firstEdge = edgeOffsets[node], lastEdge = edgeOffsets[node + 1];
		
		for (int ni = firstEdge; ni < lastEdge; ni++)
			isNeighbor[edges[ni]] = true;
		
		for (int ni = firstEdge; ni < lastEdge; ni++)
		{
			int neighbor = edges[ni];
			int firstNEdge = edgeOffsets[neighbor], lastNEdge = edgeOffsets[neighbor + 1];
			for (int nni = firstNEdge; nni < lastNEdge; nni++)
			{
				int nneighbor = edges[nni];
				if (nneighbor == node)
					continue;
				tc++;
				if (!commNNodes[nneighbor])
				{
					commNNodes[nneighbor] = true;
					commNNodesSize++;
					if (isNeighbor[nneighbor])
						tc++;
				}
			}
		}
		
		return (double)tc / ((double)commNNodesSize * (double)(lastEdge - firstEdge));
	}



}
