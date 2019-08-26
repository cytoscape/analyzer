package org.cytoscape.analyzer.util;

/**
 * Storage class for information on how to interpret a network.
 * 
 * @author Yassen Assenov
 */
public class NetworkInterpretation {

	/**
	 * Initializes a new instance of <code>NetworkInterpretation</code>.
	 * 
	 * @param aIcon
	 *            Image showing the state of the network resulting from the interpretation.
	 * @param aMessage
	 *            Message describing the network interpretation.
	 * @param aDirected
	 *            Flag indicating whether the network should be treated as directed.
	 */
	public NetworkInterpretation(String aMessage, boolean aDirected) {
		directed = aDirected;
		ignoreUSL = false;
		paired = false;
		message = "<html>" + aMessage;
	}

	/**
	 * Initializes a new instance of <code>NetworkInterpretation</code>.
	 * 
	 * @param aIcon
	 *            Image showing the state of the network resulting from the interpretation.
	 * @param aDirected
	 *            Flag indicating whether the network should be treated as directed.
	 */
	public NetworkInterpretation(boolean aDirected) {
		this ((aDirected) ? Msgs.NI_TD : Msgs.NI_TU, aDirected);
	}

	/**
	 * Initializes a new instance of <code>NetworkInterpretation</code>.
	 * 
	 * @param aIcon
	 *            Image showing the state of the network resulting from the interpretation.
	 * @param aDirected
	 *            Flag indicating whether the network should be treated as directed.
	 * @param aAdditional
	 *            Flag giving additional information to this interpretation. If <code>aDirected</code> is
	 *            <code>true</code>, this flag indicates whether undirected self-loops exist (and must be
	 *            ignored). If <code>aDirected</code> is <code>false</code>, this flag indicates whether
	 *            directed edges must be paired.
	 */
	public NetworkInterpretation(boolean aDirected, boolean aAdditional) {
		directed = aDirected;
		if (aDirected) {
			ignoreUSL = aAdditional;
			paired = false;
			if (aAdditional) {
				message = "<html>" + Msgs.NI_IGNOREUSL + "<br>" + Msgs.NI_TD;
			} else {
				message = "<html>" + Msgs.NI_TD;
			}
		} else {
			ignoreUSL = false;
			paired = aAdditional;
			if (aAdditional) {
				message = "<html>" + Msgs.NI_COMBPAIRED + "<br>" + Msgs.NI_TU;
			} else {
				message = "<html>" + Msgs.NI_NOTCOMB + "<br>" + Msgs.NI_TU;
			}
		}
	}
	/**
	 * Flag indicating whether directed edges should be paired.
	 * <p>
	 * Directed edges may be paired when a network containing directed edges must be treated as undirected. If
	 * this flag is <code>true</code>, every (directed) outgoing edge of a given node is combined with one
	 * opposite (incoming), and thus both edges are counted as one.
	 * </p>
	 */
	private boolean paired;
	private String message;
	private boolean directed;
	private boolean ignoreUSL;

	/**
	 * Gets the interpretation suffix.
	 * 
	 * @return Interpretation suffix which states if the network is treated as directed or undirected.
	 */
	public String getInterpretSuffix() { return directed ? Msgs.DT_DIRECTED : Msgs.DT_UNDIRECTED; }

	public String getMessage() {		return message;	}
	public boolean isDirected() {		return directed;	}

	//Checks if undirected self-loops must be ignored.
	public boolean isIgnoreUSL() {		return ignoreUSL;	}

	//Checks if directed edges are to be paired.
	public boolean isPaired() {		return paired;	}

}
