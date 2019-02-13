package java.org.cytoscape.analyzer;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action handler for the menu item &quot;Analyze Network&quot;.
 * 
 */
public class AnalyzeNetworkAction extends NetAnalyzerAction {

	private static final Logger logger = LoggerFactory.getLogger(AnalyzeNetworkAction.class);
	
	final CySwingApplication desktop;
	final AnalyzerManager manager;
	/**
	 * Initializes a new instance of <code>AnalyzeNetworkAction</code>.
	 */
	public AnalyzeNetworkAction(CyApplicationManager appMgr,
								final CyNetworkViewManager networkViewManager,
								final Map<String, String> configProps, AnalyzerManager mgr, CySwingApplication app) {
		super(Msgs.AC_ANALYZE,appMgr,configProps, networkViewManager);
		desktop = app;
		manager = mgr;

//		setPreferredMenu(NetworkAnalyzer.PARENT_MENU + Msgs.AC_MENU_ANALYSIS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.util.CytoscapeAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			
			List<CyNetwork> selected =	applicationManager.getSelectedNetworks();
			
			for (CyNetwork net : selected) {
				NetworkAnalyzer analyzer = initAnalysisExecuter(net, null, desktop);
				analyzer.computeAll();
			}
		} catch (Exception ex) {
			// NetworkAnalyzer internal error
			logger.error(Msgs.SM_LOGERROR, ex);
		}
	}

	/**
	 * Asks the user for interpretation and initializes the analysis executor class to perform the requested
	 * topological analysis.
	 * 
	 * @param aNetwork
	 *            Network to be analyzed.
	 * @param aNodeSet
	 *            Subset of nodes in <code>aNetwork</code>, for which topological parameters are to be
	 *            calculated. Set this to <code>null</code> if parameters must be calculated for all nodes in
	 *            the network.
	 * @return Newly initialized analysis executor; <code>null</code> if the user has decided to cancel the
	 *         operation.
	 */
	public NetworkAnalyzer initAnalysisExecuter(CyNetwork aNetwork, Set<CyNode> aNodeSet, CySwingApplication app) {
		// Ask the user for an interpretation of the network edges
		try {
//			final NetworkInspection status = CyNetworkUtils.inspectNetwork(aNetwork);
//			NetworkInterpretation interpr = interpretNetwork(status);
//			if (interpr == null) 			return null;
			boolean isDirected = true;
			NetworkInterpretation interpr = new NetworkInterpretation(isDirected);
			if (isDirected) 	
				return new DirNetworkAnalyzer(  aNetwork, aNodeSet, interpr, app);
			return new UndirNetworkAnalyzer(aNetwork, aNodeSet, interpr, app);
		} 
		catch (IllegalArgumentException ex) {
			System.err.println("SM_NETWORKEMPTY");
//			Utils.showInfoBox(swingApp.getJFrame(),Messages.DT_INFO, Messages.SM_NETWORKEMPTY);
			return null;
		}
	}

	/**
	 * Attempts to find an interpretation for network's edges.
	 * <p>
	 * This method displays a dialog to the user. If the network status leads to a unique interpretation, the
	 * dialog informs the user about it. In case multiple interpretations are possible, the dialog asks the
	 * user to choose one.
	 * </p>
	 * 
	 * @param aInsp
	 *            Results of inspection on the edges of a network.
	 * @return Interpretation instance containing the directions for interpretation of network's edges;
	 *         <code>null</code> if the user has decided to cancel the operation.
	 * 
	 * @see InterpretationDialog
	 */
	private static NetworkInterpretation interpretNetwork(NetworkInspection aInsp) {
		final NetworkStatus status = NetworkStatus.getStatus( aInsp);
//		final InterpretationDialog dialog = new InterpretationDialog(swingApp.getJFrame(),status);
//		dialog.setVisible(true);

//		if (dialog.pressedOK()) {
//			return status.getInterpretations()[dialog.getUserChoice()];
//		}
		return null;
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 1079760835761343070L;
}
