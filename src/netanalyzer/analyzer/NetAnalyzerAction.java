package analyzer;

import java.util.Map;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkViewManager;

/**
 * Base class for all NetworkAnalyzer actions which operate on a single network.
 * 
 * @author Yassen Assenov
 */
public abstract class NetAnalyzerAction extends AbstractCyAction {

	protected final CyApplicationManager applicationManager;

	/**
	 * Target network for the action.
	 */
	protected CyNetwork network;

	/**
	 * Constructs an action with the given name.
	 * 
	 * @param aName
	 *            Name of the action as it will appear in a menu.
	 */
	protected NetAnalyzerAction(final String aName, final CyApplicationManager appMgr) {
		super(aName);
		this.applicationManager = appMgr;
		network = null;
	}

	protected NetAnalyzerAction(final String aName, final CyApplicationManager appMgr,
			final Map<String,String> configProps,
			final CyNetworkViewManager networkViewManager) {
		super( configProps, appMgr, networkViewManager);
		this.applicationManager = appMgr;
		network = null;
	}

	/**
	 * Finds the network of interest to the user.
	 * <p>
	 * In case a network has been identified, the value of the field
	 * {@link #network} is updated, otherwise the value of <code>network</code>
	 * is set to the empty network or <code>null</code>. There are three
	 * possible reasons for the inability to choose a network - (1) no network
	 * is loaded; (2) there are two or more networks loaded and none selected;
	 * and (3) there is more than one network selected. For each of the two
	 * cases above, the method displays an appropriate message dialog before
	 * exiting and returning <code>false</code>.
	 * </p>
	 * 
	 * @return <code>true</code> if a network targeting analysis has been
	 *         identified, <code>false</code> otherwise.
	 */
	protected boolean selectNetwork() {
		network = applicationManager.getCurrentNetwork();
		if (network == null) {
//			Utils.showErrorBox(swingApp.getJFrame(), Messages.DT_WRONGDATA, Messages.SM_LOADNET);
			return false;
		}
		return true;
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = -6263068520728141892L;
}
