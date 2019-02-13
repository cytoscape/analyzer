package java.org.cytoscape.analyzer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.RowsSetEvent;
import org.cytoscape.model.events.RowsSetListener;
import org.cytoscape.util.swing.IconManager;


/**
 * Displays controls for Network Analyzer
 * @author Adam Treister
 *
 */
public class ResultsPanel extends JPanel 
       implements CytoPanelComponent2, ActionListener, SetCurrentNetworkListener {

	private static final long serialVersionUID = 1L;
	final AnalyzerManager manager;
	final CyApplicationManager appManager;
	final Font iconFont;
	private JTextArea label;

	private CyNetwork network;
//	private String enrichmentType = "entireNetwork";

	public ResultsPanel(final AnalyzerManager manager) {
		super();
		this.manager = manager;
		this.appManager = manager.getService(CyApplicationManager.class);
		this.network = appManager.getCurrentNetwork();

		IconManager iconManager = manager.getService(IconManager.class);
		iconFont = iconManager.getIconFont(17.0f);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(createLabelPanel());
		revalidate();
		repaint();
	}

	public String getIdentifier() {		return "org.cytoscape.analyzer.ResultsPanel";	}


	@Override
	public void handleEvent(SetCurrentNetworkEvent scne) {
		network = scne.getNetwork();
		String stats = "No Network Selected";
		if (network != null) 
		{
			if (network.getNodeCount() < 1 || network.getEdgeCount() < 1)
				stats = "Empty Network";
			else stats = network.getDefaultNetworkTable().getRow(network.getSUID()).get("statistics", String.class);
		if (stats == null)
			stats = "Tools >> Analyze Network\nto calculate statistics";
		}
		label.setText(stats);
	}

	@Override	public Component getComponent() {		return this;	}
	@Override	public CytoPanelName getCytoPanelName() {		return CytoPanelName.EAST;	}
	@Override	public Icon getIcon() {		return null;	}
	@Override	public String getTitle() {		return "Analyzer";	}

	///-----------------------------------------------
	private JPanel createLabelPanel() {
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
		label = new JTextArea();
		labelPanel.add(label);
		labelPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		return labelPanel;
	}


	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		switch (command) {
			default:
		}
	}

	public void setResultString(String out) {
		label.setText(out);
		
	}
}