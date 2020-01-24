package org.cytoscape.analyzer;


import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;

import org.cytoscape.analyzer.util.IconUtil;
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
	private JLabel title;
	private JLabel info1;
	private JLabel info2;
	private JTextArea label;
	private JButton degreeHisto;
	private JButton betweenScatter;
	private JButton closenessClusterScatter;

	private CyNetwork network;
//	private String enrichmentType = "entireNetwork";

	public ResultsPanel(final AnalyzerManager manager) {
		this.manager = manager;
		this.appManager = manager.getService(CyApplicationManager.class);
		this.network = appManager.getCurrentNetwork();

		createLabels();
		createGraphButtons();
		
		var scrollPane = new JScrollPane(label, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, UIManager.getColor("Separator.foreground")));
		
		var layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(!LookAndFeelUtil.isAquaLAF());
		
		layout.setHorizontalGroup(layout.createParallelGroup(LEADING, true)
				.addComponent(title)
				.addComponent(scrollPane, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(info1)
				.addComponent(info2)
				.addGroup(layout.createSequentialGroup()
						.addGap(5, 5, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(CENTER, false)
								.addComponent(degreeHisto)
								.addComponent(betweenScatter)
								.addComponent(closenessClusterScatter)
						)
						.addGap(5, 5, Short.MAX_VALUE)
				)
				.addComponent(degreeHisto)
				.addComponent(betweenScatter)
				.addComponent(closenessClusterScatter)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(title)
				.addComponent(scrollPane, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(info1)
				.addComponent(info2)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(degreeHisto)
				.addComponent(betweenScatter)
				.addComponent(closenessClusterScatter)
				.addContainerGap()
		);
		
		revalidate();
		repaint();
	}

	public String getIdentifier() {		return "org.cytoscape.analyzer.ResultsPanel";	}


	@Override
	public void handleEvent(SetCurrentNetworkEvent scne) {
		network = scne.getNetwork();
		String stats = "No Network Selected";
		enableButtons(network != null);
		if (network != null) 
		{
			if (network.getNodeCount() < 1 || network.getEdgeCount() < 1)
				stats = "Empty Network";
			else 
			{
//				stats = network.getDefaultNetworkTable().getRow(network.getSUID()).get("statistics", String.class);
				CyTable hiddenTable = network.getTable(CyNetwork.class, CyNetwork.HIDDEN_ATTRS);
				stats = hiddenTable.getRow(network.getSUID()).get("statistics", String.class);
				stats = parseJson(stats);
			}
			if (stats == null)
				stats = "Tools >> Analyze Network\nto calculate statistics";
		}
		
		label.setText(stats);
	}
	public void enableButtons(boolean b) {
		degreeHisto.setEnabled(b);;
		betweenScatter.setEnabled(b);
		closenessClusterScatter.setEnabled(b);
		
	}

	//-----------------------------------------------
	private String parseJson(String stats) {
		if (stats == null) return null;
		if (!stats.startsWith("{")) return stats;
		Map<String, Object> map = jsonToMap(stats);
		stats = NetworkStats.formattedOutput(map);
		return stats;
		
	}

	private Map<String, Object> jsonToMap(String json) {
		String[] lines = json.split("\n");
		Map<String, Object> map = new HashMap<>();
		
		for (String line : lines)
		{
			if (line.trim().length() == 0) continue;
			String[] tokens = line.split(":");
			if (tokens.length < 2) continue;
			String key = clean(tokens[0]);
			Object val = clean(tokens[1]);
			String s = val.toString();
			if (isDouble(s) && !isInteger(s))
				val = Double.parseDouble(val.toString());
				
			map.put(key,val);
		}
		return map;
	}
	
	private String clean(String in) {
		String trimmed = in.trim();
		if (trimmed.charAt(0) == '"')
			trimmed = trimmed.substring(1);
		if (trimmed.endsWith(","))
			trimmed = trimmed.substring(0, trimmed.length()-1);
		if (trimmed.endsWith("\""))
			trimmed = trimmed.substring(0, trimmed.length()-1);
		return trimmed;
	}

	private boolean isDouble( String input ) {
	    try {
	    	Double.parseDouble( input );	        return true;
	    }
	    catch( NumberFormatException e ) {	        return false;	    }
	}
	
	private boolean isInteger( String input ) {
	    try {
	    	Integer.parseInt( input );	        return true;
	    }
	    catch( NumberFormatException e ) {      return false;    }
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
	private void createLabels() {
		title = new JLabel("Summary Statistics of the Network:");
		title.setVisible(false);
		title.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		info1 = new JLabel("- Node specific statistics are found in the Node Table");
		info1.setVisible(false);
		info1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		info2 = new JLabel("- Edge Betweenness is added to the Edge Table");
		info2.setVisible(false);
		info2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		label = new JTextArea();
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		label.setEditable(false);
		
		LookAndFeelUtil.makeSmall(info1, info2);
	}
	
	private void createGraphButtons() {
		degreeHisto = new JButton("Show Node Degree Distribution");
		betweenScatter = new JButton("Show Betweenness by Degree");
		closenessClusterScatter = new JButton("Show Closeness");
		enableButtons(false);
		LookAndFeelUtil.equalizeSize(degreeHisto, betweenScatter, closenessClusterScatter);
		
		degreeHisto.addActionListener(evt -> manager.makeDegreeHisto());
		betweenScatter.addActionListener(evt -> manager.makeBetweenScatter());
		closenessClusterScatter.addActionListener(evt -> manager.makeClosenessClusterScatter());
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		switch (command) {
			default:
		}
	}

	public void setResultString(String out) {
		label.setText(out);
		title.setVisible(true);
		info1.setVisible(true);
		info2.setVisible(true);
		enableButtons(true);
	}
}