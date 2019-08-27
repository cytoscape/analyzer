package org.cytoscape.analyzer;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.cytoscape.analyzer.util.NetworkStats;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
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
	private JTextArea intro;
	private JTextArea label;
	private JButton degreeHisto;
	private JButton betweenScatter;
	private JButton closenessClusterScatter;

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
		add(createGraphButtons());
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
				stats = network.getDefaultNetworkTable().getRow(network.getSUID()).get("statistics", String.class);
				stats = parseJson(stats);
			}
			if (stats == null)
				stats = "Tools >> Analyze Network\nto calculate statistics";
		}
		
		label.setText(stats);
	}
	private void enableButtons(boolean b) {
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

	private Map<String, Object> jsonToMap(String json)
	{
		String[] lines = json.split("\n");
		Map<String, Object> map = new HashMap<String, Object>();
		
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
	
	private String clean(String in)
	{
		
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
	@Override	public Icon getIcon() {		return null;	}
	@Override	public String getTitle() {		return "Analyzer";	}

	static String INTRO = "Summary statistics of the network.  \nNode specific statistics are found in the node table.";
	///-----------------------------------------------
	private JPanel createLabelPanel() {
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
		intro = new JTextArea();
		intro.setFont(new Font("Serif", Font.BOLD, 12));
		intro.setOpaque(false);
		intro.setVisible(false);
		intro.setText(INTRO);
		intro.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		intro.setMaximumSize(new Dimension(300, 40));
		label = new JTextArea();
		labelPanel.add(intro);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		labelPanel.add(label);
		label.setEditable(false);
		labelPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		return labelPanel;
	}
	private JPanel createGraphButtons() {
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
		
		degreeHisto = new JButton("Show Node Degree Distribution");
		betweenScatter = new JButton("Show Betweenness by Degree");
		closenessClusterScatter = new JButton("Show Closeness Graph");
		enableButtons(false);
		
//		degreeHisto.addActionListener(new MouseE));
		degreeHisto.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {     manager.makeDegreeHisto();  } } );		
		betweenScatter.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {     manager.makeBetweenScatter();  } } );		
		closenessClusterScatter.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {     manager.makeClosenessClusterScatter();  } } );		
		JPanel line1 = new JPanel(); 
		line1.setLayout(new BoxLayout(line1, BoxLayout.LINE_AXIS));
		line1.add(degreeHisto);
		line1.add(Box.createHorizontalGlue());
		labelPanel.add(line1); 
		
		JPanel line2 = new JPanel(); 
		line2.setLayout(new BoxLayout(line2, BoxLayout.LINE_AXIS));
		line2.add(betweenScatter);
		line2.add(Box.createHorizontalGlue());
		labelPanel.add(line2);
		
		JPanel line3 = new JPanel(); 
		line3.setLayout(new BoxLayout(line3, BoxLayout.LINE_AXIS));
		line3.add(closenessClusterScatter);
		line3.add(Box.createHorizontalGlue());
		labelPanel.add(line3);
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
		intro.setVisible(true);
		enableButtons(true);
		
	}
}