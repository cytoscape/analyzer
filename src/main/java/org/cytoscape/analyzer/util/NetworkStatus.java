package org.cytoscape.analyzer.util;

/**
 * Enumeration on edge construction principles of networks, as well as options (in human readable
 * form) for interpretations.
 * 
 * @author Yassen Assenov
 */
public class NetworkStatus {


	/**
	 * HTML tag for line break. Used in generating interpretation descriptions.
	 */
	private static final String BR = "<br>";

	/**
	 * All possible network interpretations according to the different network statuses.
	 */
	private static final NetworkInterpretation[] allTrs = new NetworkInterpretation[33];

	static {
		// dir_paired_n_n :
		allTrs[0] = new NetworkInterpretation(true);
		allTrs[1] = new NetworkInterpretation(false, true);
		allTrs[2] = new NetworkInterpretation(false, false);
		// dir_paired_s_n :
		allTrs[3] = new NetworkInterpretation(true);
		allTrs[4] = new NetworkInterpretation( false, true);
		allTrs[5] = new NetworkInterpretation(false, false);
		// dir_paired_n_s :
		allTrs[6] = new NetworkInterpretation(true, true);
		allTrs[7] = new NetworkInterpretation(false, true);
		allTrs[8] = new NetworkInterpretation(false, false);
		// dir_paired_s_s :
		allTrs[9] = new NetworkInterpretation( true, true);
		allTrs[10] = new NetworkInterpretation(false, true);
		allTrs[11] = new NetworkInterpretation(false, false);
		// dir_unpaired_n_n :
		allTrs[12] = new NetworkInterpretation( true);
		allTrs[13] = new NetworkInterpretation(false);
		// dir_unpaired_s_n :
		allTrs[14] = new NetworkInterpretation( true);
		allTrs[15] = new NetworkInterpretation(false);
		// dir_unpaired_n_s :
		allTrs[16] = new NetworkInterpretation(true, true);
		allTrs[17] = new NetworkInterpretation( false);
		// dir_unpaired_s_s :
		allTrs[18] = new NetworkInterpretation( true, true);
		allTrs[19] = new NetworkInterpretation( false);
		// mixed_paired_n_n :
		allTrs[20] = new NetworkInterpretation( false, true);
		allTrs[21] = new NetworkInterpretation( false, false);
		// mixed_paired_s_n :
		allTrs[22] = new NetworkInterpretation( false, true);
		allTrs[23] = new NetworkInterpretation( false, false);
		// mixed_paired_n_s :
		allTrs[24] = new NetworkInterpretation( false, true);
		allTrs[25] = new NetworkInterpretation( false, false);
		// mixed_paired_s_s :
		allTrs[26] = new NetworkInterpretation(false, true);
		allTrs[27] = new NetworkInterpretation(false, false);
		// mixed_unpaired_n_n :
		allTrs[28] = new NetworkInterpretation( Msgs.NI_FORCETU, false);
		// mixed_unpaired_s_n :
		allTrs[29] = new NetworkInterpretation(Msgs.NI_FORCETU, false);
		// mixed_unpaired_n_s :
		allTrs[30] = new NetworkInterpretation(Msgs.NI_FORCETU, false);
		// mixed_unpaired_s_s :
		allTrs[31] = new NetworkInterpretation( Msgs.NI_FORCETU, false);
		// undir_n_n :
		allTrs[32] = new NetworkInterpretation( Msgs.NI_FORCETU, false);
		// undir_s_n : allTrs[29]
		// undir_n_s : allTrs[32]
		// undir_s_s : allTrs[31]
	}

	/**
	 * A network containing only directed edges which are paired, no self-loops.
	 */
	public static final NetworkStatus DIR_PAIRED_N_N = new NetworkStatus(Msgs.NI_DIRPAIRED,
		allTrs[0], allTrs[1], allTrs[2], 1);

	/**
	 * A network containing only directed edges which are paired, plus directed (paired) self-loops.
	 */
	public static final NetworkStatus DIR_PAIRED_S_N = new NetworkStatus(Msgs.NI_DIRPAIRED + BR
		+ Msgs.NI_LOOPSDIR, allTrs[3], allTrs[4], allTrs[5], 1);

	/**
	 * A network containing only directed edges which are paired, plus undirected self-loops.
	 */
	public static final NetworkStatus DIR_PAIRED_N_S = new NetworkStatus(Msgs.NI_DIRPAIRED + BR
		+ Msgs.NI_LOOPSUNDIR, allTrs[6], allTrs[7], allTrs[8], 1);

	/**
	 * A network containing only directed edges which are paired, plus both directed and undirected
	 * self-loops.
	 */
	public static final NetworkStatus DIR_PAIRED_S_S = new NetworkStatus(Msgs.NI_DIRPAIRED + BR
		+ Msgs.NI_LOOPSBOTH, allTrs[9], allTrs[10], allTrs[11], 1);

	/**
	 * A network containing only directed edges which are not paired, and no self-loops.
	 */
	public static final NetworkStatus DIR_UNPAIRED_N_N = new NetworkStatus(Msgs.NI_DIRUNPAIRED,
		allTrs[12], allTrs[13], 1);

	/**
	 * A network containing only directed edges which are not paired, plus directed self-loops.
	 */
	public static final NetworkStatus DIR_UNPAIRED_S_N = new NetworkStatus(Msgs.NI_DIRUNPAIRED
		+ BR + Msgs.NI_LOOPSDIR, allTrs[14], allTrs[15], 1);

	/**
	 * A network containing only directed edges which are not paired, plus undirected self-loops.
	 */
	public static final NetworkStatus DIR_UNPAIRED_N_S = new NetworkStatus(Msgs.NI_DIRUNPAIRED
		+ BR + Msgs.NI_LOOPSUNDIR, allTrs[16], allTrs[17], 1);

	/**
	 * A network containing only directed edges which are not paired, plus both directed and
	 * undirected self-loops.
	 */
	public static final NetworkStatus DIR_UNPAIRED_S_S = new NetworkStatus(Msgs.NI_DIRUNPAIRED
		+ BR + Msgs.NI_LOOPSBOTH, allTrs[18], allTrs[19], 1);

	/**
	 * A network containing both undirected and paired directed edges, no self-loops.
	 */
	public static final NetworkStatus MIXED_PAIRED_N_N = new NetworkStatus(Msgs.NI_MIXED + BR
		+ Msgs.NI_PAIRED, allTrs[20], allTrs[21], 0);

	/**
	 * A network containing both undirected and paired directed edges, plus directed (paired)
	 * self-loops.
	 */
	public static final NetworkStatus MIXED_PAIRED_S_N = new NetworkStatus(Msgs.NI_MIXED + BR
		+ Msgs.NI_PAIRED + BR + Msgs.NI_LOOPSDIR, allTrs[22], allTrs[23], 0);

	/**
	 * A network containing both undirected and paired directed edges, plus undirected self-loops.
	 */
	public static final NetworkStatus MIXED_PAIRED_N_S = new NetworkStatus(Msgs.NI_MIXED + BR
		+ Msgs.NI_PAIRED + BR + Msgs.NI_LOOPSUNDIR,  allTrs[24], allTrs[25], 0);

	/**
	 * A network containing both undirected and paired directed edges, plus both undirected and
	 * paired directed self-loops.
	 */
	public static final NetworkStatus MIXED_PAIRED_S_S = new NetworkStatus(Msgs.NI_MIXED + BR
		+ Msgs.NI_UNPAIRED + BR + Msgs.NI_LOOPSBOTH,allTrs[26], allTrs[27],
		0);

	/**
	 * A network containing both undirected and unpaired directed edges, no self-loops.
	 */
	public static final NetworkStatus MIXED_UNPAIRED_N_N = new NetworkStatus(Msgs.NI_MIXED + BR
		+ Msgs.NI_UNPAIRED, allTrs[28]);

	/**
	 * A network containing both undirected and unpaired directed edges, plus directed self-loops.
	 */
	public static final NetworkStatus MIXED_UNPAIRED_S_N = new NetworkStatus(Msgs.NI_MIXED + BR
		+ Msgs.NI_UNPAIRED + BR + Msgs.NI_LOOPSDIR, allTrs[29]);

	/**
	 * A network containing both undirected and unpaired directed edges, plus undirected self-loops.
	 */
	public static final NetworkStatus MIXED_UNPAIRED_N_S = new NetworkStatus(Msgs.NI_MIXED + BR
		+ Msgs.NI_UNPAIRED + BR + Msgs.NI_LOOPSUNDIR, allTrs[30]);

	/**
	 * A network containing both undirected and unpaired directed edges, plus both undirected and
	 * directed self-loops.
	 */
	public static final NetworkStatus MIXED_UNPAIRED_S_S = new NetworkStatus(Msgs.NI_MIXED + BR
		+ Msgs.NI_UNPAIRED + BR + Msgs.NI_LOOPSBOTH, allTrs[31]);

	/**
	 * A network containing undirected edges only, and no self-loops.
	 */
	public static final NetworkStatus UNDIR_N_N = new NetworkStatus(Msgs.NI_UNDIR,
		allTrs[32]);

	/**
	 * A network containing undirected edges only, plus directed self-loops.
	 */
	public static final NetworkStatus UNDIR_S_N = new NetworkStatus(Msgs.NI_UNDIR + BR
		+ Msgs.NI_LOOPSDIR, allTrs[29]);

	/**
	 * A network containing undirected edges only, plus undirected self-loops.
	 */
	public static final NetworkStatus UNDIR_N_S = new NetworkStatus(Msgs.NI_UNDIR + BR
		+ Msgs.NI_LOOPSUNDIR, allTrs[32]);

	/**
	 * A network containing undirected edges only, plus both undirected and directed self-loops.
	 */
	public static final NetworkStatus UNDIR_S_S = new NetworkStatus(Msgs.NI_UNDIR + BR
		+ Msgs.NI_LOOPSBOTH, allTrs[31]);

	/**
	 * Gets the network status describing the given boolean network parameters.
	 * <p>
	 * Note that this method never returns <code>null</code>. In particular, networks that
	 * contain neither directed, nor undirected edges, are considered undirected.
	 * </p>
	 * 
	 * @param aInsp Results of inspection on the edges of a network.
	 * @return The <code>NetworkStatus</code> instance that suits best the specified parameters.
	 */
	public static NetworkStatus getStatus(NetworkInspection aInsp) {
		int loops = (aInsp.dirLoops ? 1 : 0) + (aInsp.undirLoops ? 2 : 0);
		if (aInsp.dir) {
			if (aInsp.uniqueDir) {
				if (aInsp.undir) {
					// Status: mixed unpaired
					switch (loops) {
						case 0:		return NetworkStatus.MIXED_UNPAIRED_N_N;
						case 1:		return NetworkStatus.MIXED_UNPAIRED_S_N;
						case 2:		return NetworkStatus.MIXED_UNPAIRED_N_S;
						default:	return NetworkStatus.MIXED_UNPAIRED_S_S;
					}
				}
				// Status: directed unpaired
				switch (loops) {
					case 0:		return NetworkStatus.DIR_UNPAIRED_N_N;
					case 1:		return NetworkStatus.DIR_UNPAIRED_S_N;
					case 2:		return NetworkStatus.DIR_UNPAIRED_N_S;
					default:	return NetworkStatus.DIR_UNPAIRED_S_S;
				}
			}
			if (aInsp.undir) {
				// Status: mixed paired
				switch (loops) {
					case 0:		return NetworkStatus.MIXED_PAIRED_N_N;
					case 1:		return NetworkStatus.MIXED_PAIRED_S_N;
					case 2:		return NetworkStatus.MIXED_PAIRED_N_S;
					default:	return NetworkStatus.MIXED_PAIRED_S_S;
				}
			}
			// Status: directed paired
			switch (loops) {
				case 0:		return NetworkStatus.DIR_PAIRED_N_N;
				case 1:		return NetworkStatus.DIR_PAIRED_S_N;
				case 2:		return NetworkStatus.DIR_PAIRED_N_S;
				default:	return NetworkStatus.DIR_PAIRED_S_S;
			}
		}
		// Status: undirected
		switch (loops) {
			case 0:			return NetworkStatus.UNDIR_N_N;
			case 1:			return NetworkStatus.UNDIR_S_N;
			case 2:			return NetworkStatus.UNDIR_N_S;
			default:		return NetworkStatus.UNDIR_S_S;
		}
	}
	private String description;
	private NetworkInterpretation[] interprs;
	private int defaultInterprIndex;

	public String getDescription() {		return description;	}
	public NetworkInterpretation[] getInterpretations() {		return interprs;	}
	public int getDefaultInterprIndex() {		return defaultInterprIndex;	}

	/**
	 * Initializes a new instance of <code>NetworkStatus</code> with a unique interpretation.
	 * 
	 * @param aDescription Status description in a human-readable format.
	 * @param aIcon Image that schematically represents the status. This should be one of the
	 *        elements of {@link #allIcons}.
	 * @param aInterpr The only network interpretation for this status.
	 */
	private NetworkStatus(String aDescription, NetworkInterpretation aInterpr) {
		this(aDescription, new NetworkInterpretation[] { aInterpr }, 0);
	}

	/**
	 * Initializes a new instance of <code>NetworkStatus</code> with two interpretations.
	 * 
	 * @param aDescription Status description in a human-readable format.
	 * @param aIcon Image that schematically represents the status. This should be one of the
	 *        elements of {@link #allIcons}.
	 * @param aInterpr1 The first network interpretation for this status.
	 * @param aInterpr2 The second network interpretation for this status.
	 * @param aDefIndex Index of the default interpretation. This value must be <code>0</code> or
	 *        <code>1</code>.
	 */
	private NetworkStatus(String aDescription, NetworkInterpretation aInterpr1,
		NetworkInterpretation aInterpr2, int aDefIndex) {
		this(aDescription,new NetworkInterpretation[] { aInterpr1, aInterpr2 }, aDefIndex);
	}

	/**
	 * Initializes a new instance of <code>NetworkStatus</code> with three interpretations.
	 * 
	 * @param aDescription Status description in a human-readable format.
	 * @param aIcon Image that schematically represents the status. This should be one of the
	 *        elements of {@link #allIcons}.
	 * @param aInterpr1 The first network interpretation for this status.
	 * @param aInterpr2 The second network interpretation for this status.
	 * @param aInterpr3 The third network interpretation for this status.
	 * @param aDefIndex Index of the default interpretation. This value must be <code>0</code>,
	 *        <code>1</code> or <code>2</code>.
	 */
	private NetworkStatus(String aDescription, NetworkInterpretation aInterpr1,
		NetworkInterpretation aInterpr2, NetworkInterpretation aInterpr3, int aDefIndex) {
		this(aDescription, new NetworkInterpretation[] { aInterpr1, aInterpr2, aInterpr3 },
			aDefIndex);
	}

	/**
	 * Initializes a new instance of <code>NetworkStatus</code>.
	 * 
	 * @param aDescription Status description in a human-readable format.
	 * @param aIcon Image that schematically represents the status. This should be one of the
	 *        elements of {@link #allIcons}.
	 * @param aInterprs Array of possible interpretations for this status.
	 * @param aDefIndex Index of the default interpretation. This value must point to an element in
	 *        the array of possible interpretations.
	 */
	private NetworkStatus(String aDescription, NetworkInterpretation[] aInterprs,
		int aDefIndex) {
		description = "<html><b>" + aDescription + "</b>";
		interprs = aInterprs;
		defaultInterprIndex = aDefIndex;
	}
}
