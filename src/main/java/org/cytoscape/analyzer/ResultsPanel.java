package org.cytoscape.analyzer;


import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.cytoscape.analyzer.util.EasyGBC;
import org.cytoscape.analyzer.util.IconUtil;
import org.cytoscape.analyzer.util.JSONUtils;
import org.cytoscape.analyzer.util.Msgs;
import org.cytoscape.analyzer.util.NetworkStats;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.util.swing.LookAndFeelUtil;
import org.cytoscape.util.swing.TextIcon;


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
	private Icon icon;
	private JLabel networkName;
	private JLabel label;
	private JButton degreeHisto;
	private JButton betweenScatter;
	private JPanel mainPanel;
	private EasyGBC mainPanelGBC;
	private Font labelFont;
	private Font textFont;

	private CyNetwork network;

	final String HEADER_TITLE = "Summary Statistics";

	public ResultsPanel(final AnalyzerManager manager) {
		this.manager = manager;
		this.appManager = manager.getService(CyApplicationManager.class);
		this.network = appManager.getCurrentNetwork();

		labelFont = new Font("SansSerif", Font.BOLD, 10);
		textFont = new Font("SansSerif", Font.PLAIN, 10);

		createGraphButtons();
		
		// var layout = new GroupLayout(this);
		setLayout(new GridBagLayout());

		EasyGBC c = new EasyGBC();

		c.insets(2,5,2,5);
		
		{
			String name = "Blank";
			if (network != null) {
				name = network.getRow(network).get(CyNetwork.NAME, String.class);
			}
			networkName = new JLabel(name);
			networkName.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			networkName.setHorizontalAlignment(SwingConstants.CENTER);
			networkName.setFont(labelFont.deriveFont(12.0f));
			add(networkName, c.down().anchor("west").expandHoriz());
		}

		{
			mainPanel = new JPanel(new GridBagLayout());
			mainPanelGBC = new EasyGBC();
			var scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
			                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, UIManager.getColor("Separator.foreground")));
			scrollPane.setBackground(getBackground());
			add(scrollPane, c.insets(5,5,0,5).down().anchor("west").expandBoth());
		}

		{
			// This is used if we don't have any network stats
			label = new JLabel();
			label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			label.setBackground(getBackground());
			mainPanel.add(label, mainPanelGBC.anchor("west").expandHoriz());
		}

		{
			var info1 = new JLabel("- Node specific statistics are found in the Node Table");
			info1.setVisible(true);
			info1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			LookAndFeelUtil.makeSmall(info1);
			add(info1, c.down().anchor("west").expandHoriz());
		}

		{
			var info2 = new JLabel("- Edge Betweenness is added to the Edge Table");
			info2.setVisible(true);
			info2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			LookAndFeelUtil.makeSmall(info2);
			add(info2, c.down().anchor("west").expandHoriz());
		}

		{
			JPanel buttonBox = new JPanel();
			buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.PAGE_AXIS));
			degreeHisto.setAlignmentX(Component.CENTER_ALIGNMENT);
			betweenScatter.setAlignmentX(Component.CENTER_ALIGNMENT);
			buttonBox.add(degreeHisto);
			buttonBox.add(betweenScatter);
			add(buttonBox, c.down().expandHoriz());
		}

		revalidate();
		repaint();
	}

	public String getIdentifier() {		return "org.cytoscape.analyzer.ResultsPanel";	}


	@Override
	public void handleEvent(SetCurrentNetworkEvent scne) {
		network = scne.getNetwork();
		NetworkStats st = null;
		String stats = "No Network Selected";

		updateHeader(st);

		enableButtons(network != null);
		if (network != null) 
		{
			if (network.getNodeCount() < 1 || network.getEdgeCount() < 1) {
				stats = "Empty Network";
				enableButtons(false);
			} else 
			{
//				stats = network.getDefaultNetworkTable().getRow(network.getSUID()).get("statistics", String.class);
				CyTable hiddenTable = network.getTable(CyNetwork.class, CyNetwork.HIDDEN_ATTRS);
				stats = hiddenTable.getRow(network.getSUID()).get("statistics", String.class);
				st = parseJson(stats);
			}
			if (st == null && stats == null)
				stats = "<html><body><p style='text-align:center'>Tools &rarr; Analyze Network<br/>to calculate statistics</p></body></html>";
		}

		if (st == null) {
			setResultString(stats);
		} else {
			setResults(st);
		}
	}

	public void enableButtons(boolean b) {
		degreeHisto.setEnabled(b);;
		betweenScatter.setEnabled(b);
//		closenessClusterScatter.setEnabled(b);
		
	}

	//-----------------------------------------------
	private NetworkStats parseJson(String stats) {
		if (stats == null || !stats.startsWith("{")) return null;
		// Map<String, Object> map = JSONUtils.jsonToMap(stats);
		NetworkStats st = new NetworkStats(stats);
		return st;
		
	}

	//-----------------------------------------------
	@Override	public Component getComponent() {		return this;	}
	@Override	public CytoPanelName getCytoPanelName() {		return CytoPanelName.EAST;	}

	@Override
	public Icon getIcon() {
		if (icon == null)
			icon = new TextIcon(IconUtil.LOGO, IconUtil.getIconFont(15.0f), 16, 16);

		return icon;
	}
	
	@Override	public String getTitle() {		return "Analyzer";	}

	///-----------------------------------------------
	private void updateHeader(NetworkStats stats) {
		String name;
		if (stats != null)
			name = (String)stats.get("networkTitle");
		else if (network != null)
			name = network.getRow(network).get(CyNetwork.NAME, String.class);
		else
			name = "No Network Selected";
		networkName.setText(name);
	}

	private void createGraphButtons() {
		degreeHisto = new JButton("Node Degree Distribution");
		betweenScatter = new JButton("Betweenness by Degree");
//		closenessClusterScatter = new JButton("Show Closeness");
		enableButtons(false);
		LookAndFeelUtil.equalizeSize(degreeHisto, betweenScatter);//, closenessClusterScatter

		degreeHisto.setFont(labelFont);
		betweenScatter.setFont(labelFont);
		
		degreeHisto.addActionListener(evt -> manager.makeDegreeHisto());
		betweenScatter.addActionListener(evt -> manager.makeBetweenScatter());
//		closenessClusterScatter.addActionListener(evt -> manager.makeClosenessClusterScatter());
	}

	private JPanel addLine(String key, Object val) {
		JPanel line = new JPanel(new GridBagLayout());
		EasyGBC d = new EasyGBC();

		String strVal = null;

		if (val instanceof String)
			strVal = (String)val;
		else if (val instanceof Double )
			strVal = String.format("%8.3f", val);
		else if (val instanceof Integer )
			strVal = String.format("%3d", val);

		JLabel keyLabel = new JLabel(key);
		keyLabel.setFont(labelFont);

		line.add(keyLabel, d.anchor("west").expandHoriz());

		JLabel valLabel = new JLabel(strVal);
		valLabel.setFont(textFont);
		line.add(valLabel, d.right().noExpand());
		return line;
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		switch (command) {
			default:
		}
	}

	public void setResults(NetworkStats stats) {
		updateHeader(stats);

		mainPanel.removeAll();
		mainPanelGBC.reset();

		JPanel statsPanel = new JPanel(new GridBagLayout());
		EasyGBC d = new EasyGBC();
		{
			var title = new JLabel(HEADER_TITLE);
			title.setHorizontalAlignment(SwingConstants.CENTER);
			title.setVisible(true);
			title.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			LookAndFeelUtil.makeSmall(title);
			statsPanel.add(title, d.anchor("northwest").expandHoriz());
		}

		var keys = stats.getKeys();
		for (String key: keys) {
			Object val = stats.get(key);
			String s = Msgs.get(key);
			if (val != null && !key.equals("networkTitle"))
				statsPanel.add(addLine(s, val), d.insets(2, 0, 0, 0).anchor("west").down().expandHoriz());
		}
		mainPanel.add(statsPanel, mainPanelGBC.insets(10, 5, 5, 5).anchor("northwest").expandHoriz());
		mainPanel.add(new JLabel(), mainPanelGBC.anchor("west").expandBoth());
		enableButtons(true);
		revalidate();
		repaint();
	}

	public void setResultString(String out) {
		mainPanel.removeAll();
		mainPanelGBC.reset();
		label = new JLabel();
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		label.setBackground(getBackground());
		label.setText(out);
		label.setOpaque(true);
		mainPanel.add(label, mainPanelGBC.anchor("west").expandBoth());
		revalidate();
		repaint();
	}
}
