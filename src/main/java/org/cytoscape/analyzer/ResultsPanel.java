package org.cytoscape.analyzer;


import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.GroupLayout.Alignment.LEADING;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.analyzer.util.IconUtil;
import org.cytoscape.analyzer.util.Msgs;
import org.cytoscape.analyzer.util.NetworkStats;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CytoPanelComponent2;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.AddedEdgesEvent;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesEvent;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.RemovedEdgesEvent;
import org.cytoscape.model.events.RemovedEdgesListener;
import org.cytoscape.model.events.RemovedNodesEvent;
import org.cytoscape.model.events.RemovedNodesListener;
import org.cytoscape.util.swing.IconManager;
import org.cytoscape.util.swing.LookAndFeelUtil;
import org.cytoscape.util.swing.TextIcon;
import org.cytoscape.work.TaskFactory;


/**
 * Displays controls for Network Analyzer
 * @author Adam Treister
 */
@SuppressWarnings("serial")
public class ResultsPanel extends JPanel implements CytoPanelComponent2, ActionListener, SetCurrentNetworkListener,
		AddedNodesListener, AddedEdgesListener, RemovedNodesListener, RemovedEdgesListener,
		ColumnCreatedListener, ColumnDeletedListener {

	final String HEADER_TITLE = "SUMMARY STATISTICS:";

	/** Service filter used to look up the task factory behind the "New Analysis..." button. */
	private static final String ANALYZE_FACTORY_FILTER = "(id=analyzeNetworkTaskFactory)";

	/** The y axis of the scatter chart. Both interpretations compute it. */
	private static final String BETWEENNESS_COLUMN = Msgs.getAttr("nbt");

	/** Degree column the charts use for undirected results, where there is nothing to choose. */
	private static final String TOTAL_DEGREE_COLUMN = Msgs.getAttr("deg");

	/** The two degree columns a directed analysis lets the user pick between. */
	private static final String IN_DEGREE_COLUMN = Msgs.getAttr("din");
	private static final String OUT_DEGREE_COLUMN = Msgs.getAttr("dou");

	/**
	 * Columns only a directed analysis produces, used to recognize directed results on a network
	 * whose statistics are not in this session.
	 */
	private static final String[] DIRECTED_ONLY_COLUMNS = { IN_DEGREE_COLUMN, OUT_DEGREE_COLUMN };

	private static final String[] EXTRA_INFO = {
		"Node specific statistics are found in the Node Table.",
		"Edge <i>Betweenness</i> is added to the Edge Table."
	};

	final AnalyzerManager manager;
	final CyApplicationManager appManager;
	private Icon icon;
	private JLabel networkName;
	private JButton newAnalysis;
	private JButton degreeHisto;
	private JButton betweenScatter;
	private JButton helpButton;
	private JButton aboutButton;
	private JButton closeButton;
	private JPanel degreeChoicePanel;
	private JRadioButton inDegreeChoice;
	private JRadioButton outDegreeChoice;
	private BodyPanel bodyPanel;

	private CyNetwork network;

	/** Whether the results shown come from a directed analysis, which is what offers the choice. */
	private boolean directedResults;

	public ResultsPanel(AnalyzerManager manager) {
		this.manager = manager;
		this.appManager = manager.getService(CyApplicationManager.class);
		this.network = appManager.getCurrentNetwork();

		setBackground(UIManager.getColor("Table.background"));
		
		createChartButtons();

		JPanel headerPanel = createHeaderPanel();
		JPanel footerPanel = createFooterPanel();

		bodyPanel = new BodyPanel();
		bodyPanel.setBackground(getBackground());
		bodyPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		var scrollPane = new JScrollPane(bodyPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getViewport().setBackground(getBackground());
		scrollPane.setBorder(BorderFactory.createEmptyBorder());

		var layout = new GroupLayout(this);
		setLayout(layout);

		layout.setHorizontalGroup(layout.createParallelGroup(LEADING, true)
			.addComponent(headerPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(scrollPane, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(footerPanel, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addComponent(headerPanel, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
			.addComponent(scrollPane, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(footerPanel, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE));

		setResultString("");

		update();
		revalidate();
		repaint();
	}

	@Override
	public String getIdentifier() {
		return "org.cytoscape.analyzer.ResultsPanel";
	}

	@Override
	public void handleEvent(SetCurrentNetworkEvent e) {
		update();
	}
	
	@Override
	public void handleEvent(AddedEdgesEvent e) {
		update();
	}

	@Override
	public void handleEvent(AddedNodesEvent e) {
		update();
	}
	
	@Override
	public void handleEvent(RemovedEdgesEvent e) {
		update();
	}

	@Override
	public void handleEvent(RemovedNodesEvent e) {
		update();
	}

	// A column the charts need can appear or disappear without the network itself changing --
	// through an analysis, a table import or a column deletion -- so the chart buttons have to
	// be re-evaluated when that happens.
	@Override
	public void handleEvent(ColumnCreatedEvent e) {
		if (isCurrentNodeTable(e.getSource()))
			updateButtons();
	}

	@Override
	public void handleEvent(ColumnDeletedEvent e) {
		if (isCurrentNodeTable(e.getSource()))
			updateButtons();
	}

	private boolean isCurrentNodeTable(CyTable table) {
		var current = appManager.getCurrentNetwork();
		return current != null && table != null && table.equals(current.getDefaultNodeTable());
	}

	protected void update() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> update());
			return;
		}

		network = appManager.getCurrentNetwork();
		updateButtons();
		
		NetworkStats st = null;
		String stats = "No Network Selected";

		if (network != null) {
			if (network.getNodeCount() < 4 || network.getEdgeCount() < 1) {
				stats = network.getNodeCount() == 0 ? "Empty Network" : "Network Too Small<br>(must have at least 4 nodes and 1 edge)";
			} else {
				stats = getStoredStatistics();
				st = parseJson(stats);
			}
			if (st == null && stats == null)
				stats = "No Statistics Found<br>(run a new analysis to calculate statistics for this network)";
		}
		
		updateHeader(st);

		if (st == null) {
			setResultString(stats);
		} else {
			setResults(st);
		}
	}

	private void updateButtons() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> updateButtons());
			return;
		}
		
		int nodeCount = network == null ? 0 : network.getNodeCount();
		int edgeCount = network == null ? 0 : network.getEdgeCount();

		newAnalysis.setEnabled(nodeCount >= 4 && edgeCount >= 1);

		boolean wasDirected = directedResults;
		directedResults = isDirectedResult();
		degreeChoicePanel.setVisible(directedResults);

		// Undirected results plot Degree whatever the radio buttons say, so the labels follow
		// the interpretation as well as the choice
		if (wasDirected != directedResults)
			updateChartButtonLabels();

		String degreeColumn = getDegreeColumn();
		boolean hasData = nodeCount > 0 && edgeCount > 0;

		updateChartButton(degreeHisto, hasData, new String[] { degreeColumn });
		updateChartButton(betweenScatter, hasData, new String[] { degreeColumn, BETWEENNESS_COLUMN });

		revalidate();
		repaint();
	}

	/**
	 * Whether the results on the current network come from a directed analysis. The stored
	 * statistics say so outright and are the authority, since a network re-analyzed under the
	 * other interpretation keeps the columns of both. Without them -- a network imported with the
	 * columns of an analysis run in another session -- the directed-only columns are the evidence.
	 */
	private boolean isDirectedResult() {
		if (network == null)
			return false;

		var stats = parseJson(getStoredStatistics());
		var title = stats == null ? null : (String)stats.get("networkTitle");

		if (title != null) {
			if (title.endsWith(Msgs.DT_DIRECTED))
				return true;
			if (title.endsWith(Msgs.DT_UNDIRECTED))
				return false;
		}

		return getMissingColumns(DIRECTED_ONLY_COLUMNS).isEmpty();
	}

	/** The statistics the last analysis stored on the network, or null if it has none. */
	private String getStoredStatistics() {
		if (network == null)
			return null;

		var hiddenTable = network.getTable(CyNetwork.class, CyNetwork.HIDDEN_ATTRS);
		if (hiddenTable == null)
			return null;

		var row = hiddenTable.getRow(network.getSUID());
		return row == null ? null : row.get("statistics", String.class);
	}

	/**
	 * Enables a chart button only when the network has something to plot <i>and</i> every node
	 * column the chart passes to CyPlot is present. The columns can be there without the results
	 * being in memory -- a network imported after a previous session keeps them -- which is why
	 * this looks at the table rather than at the statistics.
	 */
	private void updateChartButton(JButton button, boolean hasData, String[] columns) {
		var missing = getMissingColumns(columns);

		button.setEnabled(hasData && missing.isEmpty());

		if (hasData && !missing.isEmpty()) {
			boolean plural = missing.size() > 1;
			button.setToolTipText("<html>Requires the node column" + (plural ? "s " : " ") + "\""
					+ String.join("\" and \"", missing) + "\".<br>Run a new " + getInterpretationHint(missing)
					+ "analysis to compute " + (plural ? "them" : "it") + ".</html>");
		} else {
			button.setToolTipText(null);
		}
	}

	/**
	 * Names the interpretation the user has to pick, when only one of the two produces the missing
	 * columns -- "Degree", for one, comes out of an undirected analysis only, so an unqualified
	 * "run a new analysis" would send the user back to a directed run that cannot create it.
	 * Empty when either interpretation would do, or when no single run can produce them all.
	 */
	private static String getInterpretationHint(List<String> missing) {
		boolean undirected = missing.stream().anyMatch(Msgs::isUndirectedOnlyAttr);
		boolean directed = missing.stream().anyMatch(Msgs::isDirectedOnlyAttr);

		if (undirected && !directed)
			return "undirected ";
		if (directed && !undirected)
			return "directed ";

		return "";
	}

	/** Names of the given columns that the current network's default node table does not have. */
	private List<String> getMissingColumns(String[] columns) {
		var table = network == null ? null : network.getDefaultNodeTable();
		var missing = new ArrayList<String>();

		for (String name : columns) {
			if (table == null || table.getColumn(name) == null)
				missing.add(name);
		}

		return missing;
	}

	//-----------------------------------------------
	private NetworkStats parseJson(String stats) {
		if (stats == null || !stats.startsWith("{")) return null;
		NetworkStats st = new NetworkStats(stats);
		return st;
	}

	//-----------------------------------------------
	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.EAST;
	}

	@Override
	public Icon getIcon() {
		if (icon == null)
			icon = new TextIcon(IconUtil.LOGO, IconUtil.getIconFont(15.0f), 16, 16);

		return icon;
	}

	@Override
	public String getTitle() {
		return "Analyzer";
	}

	///-----------------------------------------------
	/**
	 * Header showing the interpretation and the network name, plus the "New Analysis..." button.
	 */
	private JPanel createHeaderPanel() {
		networkName = new JLabel();

		newAnalysis = new JButton("New Analysis...");
		newAnalysis.addActionListener(evt -> runNewAnalysis());

		var panel = new JPanel();
		panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Separator.foreground")));

		var layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		// The name is free to shrink, so a narrow panel clips the title instead of the button
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addComponent(networkName, 0, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(newAnalysis, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE));
		layout.setVerticalGroup(layout.createParallelGroup(CENTER)
			.addComponent(networkName)
			.addComponent(newAnalysis));

		return panel;
	}

	/**
	 * Footer holding the degree choice and the chart buttons, stacked and centered.
	 */
	private JPanel createFooterPanel() {
		var panel = new JPanel();
		panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("Separator.foreground")));

		var layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(!LookAndFeelUtil.isAquaLAF());

		var sep = new JSeparator(SwingConstants.HORIZONTAL);
		
		// Stacked and centered. The buttons are the same size thanks to equalizeSize(), and are
		// allowed to shrink so that this row does not dictate a minimum width for the whole
		// panel, which would clip the statistics when the CytoPanel is narrow. GroupLayout
		// honors visibility by default, so the hidden degree choice takes no room.
		layout.setHorizontalGroup(layout.createParallelGroup(CENTER, true)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGap(0, 0, Short.MAX_VALUE)
				.addGroup(layout.createParallelGroup(CENTER, true)
					.addComponent(degreeChoicePanel, 0, PREFERRED_SIZE, PREFERRED_SIZE)
					.addComponent(degreeHisto, 0, PREFERRED_SIZE, PREFERRED_SIZE)
					.addComponent(betweenScatter, 0, PREFERRED_SIZE, PREFERRED_SIZE))
				.addGap(0, 0, Short.MAX_VALUE)
				.addContainerGap()
			)
			.addComponent(sep, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(getHelpButton(), PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(getAboutButton(), PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
				.addGap(0, 0, Short.MAX_VALUE)
				.addComponent(getCloseButton(), PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
				.addContainerGap()
			)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addContainerGap()
			.addComponent(degreeChoicePanel, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
			.addComponent(degreeHisto)
			.addComponent(betweenScatter)
			.addComponent(sep, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
			.addGroup(layout.createParallelGroup(CENTER)
				.addComponent(getHelpButton())
				.addComponent(getAboutButton())
				.addComponent(getCloseButton())
			)
			.addContainerGap()
		);

		return panel;
	}

	/**
	 * Runs the same task as Tools &rarr; Analyze Network, so the user gets the
	 * "Analyze as Directed Graph?" dialog built from the task's tunables.
	 */
	private void runNewAnalysis() {
		var factory = manager.getService(TaskFactory.class, ANALYZE_FACTORY_FILTER);
		if (factory != null)
			manager.executeTasks(factory);
	}

	private void updateHeader(NetworkStats stats) {
		String title = null;
		if (stats != null)
			title = (String)stats.get("networkTitle");
		else if (network != null)
			title = network.getRow(network).get(CyNetwork.NAME, String.class);

		if (title == null) {
			networkName.setText("");
			return;
		}

		// NetworkStats stores the title as "<network name> (directed|undirected)".
		// Show it the way the web version does: "Directed—<network name>".
		String interpretation = null;
		if (title.endsWith(Msgs.DT_DIRECTED)) {
			interpretation = "Directed";
			title = title.substring(0, title.length() - Msgs.DT_DIRECTED.length());
		} else if (title.endsWith(Msgs.DT_UNDIRECTED)) {
			interpretation = "Undirected";
			title = title.substring(0, title.length() - Msgs.DT_UNDIRECTED.length());
		}

		StringBuilder text = new StringBuilder("<html><nobr>");
		if (interpretation != null)
			text.append("<b>").append(interpretation).append("</b>&#8212;");
		text.append(escapeHtml(title)).append("</nobr></html>");
		networkName.setText(text.toString());
	}

	private void createChartButtons() {
		var iconManager = manager.getService(IconManager.class);
		var icon = new TextIcon(IconManager.ICON_BAR_CHART, iconManager.getIconFont(18.0f), 24, 24);

		degreeHisto = new JButton(icon);
		betweenScatter = new JButton(icon);

		// By default the icon and the label are centered as a single block, so buttons with
		// different text lengths put their icons at different offsets. Anchoring the content
		// to the left lines the icons up, since the buttons are the same size
		degreeHisto.setHorizontalAlignment(SwingConstants.LEFT);
		betweenScatter.setHorizontalAlignment(SwingConstants.LEFT);

		degreeHisto.addActionListener(evt -> manager.makeDegreeHisto(getDegreeColumn()));
		betweenScatter.addActionListener(evt -> manager.makeBetweenScatter(getDegreeColumn()));

		degreeChoicePanel = createDegreeChoicePanel();
		updateChartButtonLabels();
	}
	
	private JButton getHelpButton() {
		if (helpButton == null) {
			helpButton = LookAndFeelUtil.createHelpButton("https://manual.cytoscape.org/en/stable/Network_Analyzer.html");
			helpButton.setToolTipText("Online Help...");
		}
		
		return helpButton;
	}
	
	protected JButton getAboutButton() {
		if (aboutButton == null) {
			aboutButton = new JButton("About");
		}
		
		return aboutButton;
	}
	
	protected JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton("Close");
		}
		
		return closeButton;
	}
	
	/**
	 * A directed analysis computes in-degree and out-degree separately, and either can be the one
	 * worth plotting, so the user picks. Only shown for directed results: an undirected analysis
	 * has the single <i>Degree</i> column and nothing to choose between.
	 */
	private JPanel createDegreeChoicePanel() {
		inDegreeChoice = new JRadioButton(IN_DEGREE_COLUMN);
		outDegreeChoice = new JRadioButton(OUT_DEGREE_COLUMN);
		outDegreeChoice.setSelected(true);

		var group = new ButtonGroup();
		group.add(inDegreeChoice);
		group.add(outDegreeChoice);

		LookAndFeelUtil.makeSmall(inDegreeChoice, outDegreeChoice);

		// The choice changes both what the buttons plot and which column has to be there, so the
		// labels and the enabled state are rebuilt together
		ActionListener onChoice = evt -> {
			updateChartButtonLabels();
			updateButtons();
		};
		inDegreeChoice.addActionListener(onChoice);
		outDegreeChoice.addActionListener(onChoice);

		var panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.add(inDegreeChoice);
		panel.add(Box.createHorizontalStrut(8));
		panel.add(outDegreeChoice);
		panel.setVisible(false); // updateButtons() shows it once the results are known to be directed

		return panel;
	}

	/** The degree column the charts plot: the user's pick for directed results, else Degree. */
	private String getDegreeColumn() {
		if (!directedResults)
			return TOTAL_DEGREE_COLUMN;

		return inDegreeChoice.isSelected() ? IN_DEGREE_COLUMN : OUT_DEGREE_COLUMN;
	}

	/** Names the buttons after the column they actually plot. */
	private void updateChartButtonLabels() {
		String column = getDegreeColumn();

		degreeHisto.setText("Node " + column + " Distribution");
		betweenScatter.setText("Betweenness by " + column);

		// equalizeSize() pins explicit sizes on both buttons, which would keep them at the width
		// of the previous labels, so they have to be released before measuring the new text
		for (JButton button : new JButton[] { degreeHisto, betweenScatter }) {
			button.setMinimumSize(null);
			button.setPreferredSize(null);
			button.setMaximumSize(null);
		}

		LookAndFeelUtil.equalizeSize(degreeHisto, betweenScatter);
	}

	private static String escapeHtml(String s) {
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	/**
	 * Formats a statistic for display. Values parsed back from the stored JSON arrive as
	 * <code>String</code> unless they have a fractional part, in which case they are
	 * <code>Double</code>.
	 */
	private static String formatValue(Object val) {
		if (val instanceof Double)
			return String.format("%.3f", val);
		if (val instanceof Float)
			return String.format("%.3f", ((Float)val).doubleValue());
		return String.valueOf(val).trim();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		switch (command) {
			default:
		}
	}

	private void setResults(NetworkStats stats) {
		// The analysis runs on a task thread and calls this from NetworkAnalyzer.doOutput(),
		// so the rebuild below has to be moved onto the EDT. Otherwise the EDT can validate
		// bodyPanel while the new GroupLayout is half built -- GroupLayout adds a component to
		// the container as soon as it joins a group, so a layout query made before
		// setHorizontalGroup() fails with "is not attached to a horizontal group".
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> setResults(stats));
			return;
		}

		bodyPanel.removeAll();

		var title = new JLabel(HEADER_TITLE);
		title.setFont(title.getFont().deriveFont(Font.BOLD));

		var statsTable = createStatsTable(stats);

		var time = stats.get("time");
		var timeLabel = new JLabel(time == null ? "" : Msgs.get("time") + ": " + formatValue(time));
		timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		var layout = new GroupLayout(bodyPanel);
		bodyPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);

		// Everything is allowed to shrink to nothing, so that a narrow panel never forces the
		// content wider than the viewport and pushes the values out of sight
		var copyButton = createCopyButton(statsTable);
		
		LookAndFeelUtil.makeSmall(timeLabel, copyButton);

		ParallelGroup horizontal = layout.createParallelGroup(LEADING, true)
			.addGroup(layout.createSequentialGroup()
				.addComponent(title, 0, DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(copyButton, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
			)
			.addComponent(statsTable, 0, DEFAULT_SIZE, Short.MAX_VALUE)
			.addComponent(timeLabel, 0, DEFAULT_SIZE, Short.MAX_VALUE);
		SequentialGroup vertical = layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(CENTER)
				.addComponent(title)
				.addComponent(copyButton, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
			)
			.addPreferredGap(ComponentPlacement.UNRELATED)
			.addComponent(statsTable, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.RELATED)
			.addComponent(timeLabel, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
			.addPreferredGap(ComponentPlacement.UNRELATED);

		for (String info : EXTRA_INFO) {
			// nobr keeps these on one line, so shrinking clips them instead of re-wrapping
			// into a taller label than the layout reserved room for
			JLabel bullet = new JLabel("<html><nobr>&#8226;&nbsp;" + info + "</nobr></html>");
			LookAndFeelUtil.makeSmall(bullet);
			horizontal.addComponent(bullet, 0, DEFAULT_SIZE, Short.MAX_VALUE);
			vertical.addComponent(bullet);
		}
		
		vertical.addPreferredGap(ComponentPlacement.UNRELATED);

		layout.setHorizontalGroup(horizontal);
		layout.setVerticalGroup(vertical);

		revalidate();
		repaint();
	}

	/**
	 * Small icon button that puts the whole statistics table on the clipboard, for when the
	 * user does not want to select the rows by hand.
	 */
	private JButton createCopyButton(JTable table) {
		var iconManager = manager.getService(IconManager.class);
		var icon = new TextIcon(IconManager.ICON_COPY, iconManager.getIconFont(18.0f), 24, 24);

		var button = new JButton(icon);
		button.setToolTipText("Copy to clipboard");
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(evt -> copyToClipboard(table));

		return button;
	}

	private static void copyToClipboard(JTable table) {
		var text = new StringBuilder();

		for (int row = 0; row < table.getRowCount(); row++) {
			for (int col = 0; col < table.getColumnCount(); col++) {
				if (col > 0)
					text.append('\t');
				text.append(table.getValueAt(row, col));
			}
			text.append('\n');
		}

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text.toString()), null);
	}

	/**
	 * Two column, read-only table of statistics: names on the left, values right-aligned
	 * against the right edge. Being a table, the rows can be selected and copied to the
	 * clipboard with the usual select-all / copy keystrokes.
	 */
	private JTable createStatsTable(NetworkStats stats) {
		var rows = new ArrayList<String[]>();

		for (String key : stats.getKeys()) {
			// "" marks a group separator, and these two are shown elsewhere in the panel
			if (key == null || key.trim().isEmpty() || key.equals("networkTitle") || key.equals("time"))
				continue;

			Object val = stats.get(key);
			String name = Msgs.get(key);
			if (val == null || name == null)
				continue;

			rows.add(new String[] { name, formatValue(val) });
		}

		var model = new DefaultTableModel(rows.toArray(new String[rows.size()][]),
		                                  new String[] { "Statistic", "Value" }) {
			@Override
			public boolean isCellEditable(int row, int column) {	return false;	}
		};

		var table = new JTable(model);
		table.setTableHeader(null);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.setGridColor(UIManager.getColor("Separator.foreground"));
		table.setIntercellSpacing(new Dimension(0, 1));
		table.setCellSelectionEnabled(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setBackground(getBackground());

		var labelFont = UIManager.getFont("Label.font");
		if (labelFont != null)
			table.setFont(labelFont);

		var metrics = table.getFontMetrics(table.getFont());
		table.setRowHeight(metrics.getHeight() + 6);

		var rightAligned = new DefaultTableCellRenderer();
		rightAligned.setHorizontalAlignment(SwingConstants.RIGHT);

		// Pinning the value column to its content width keeps the numbers against the right
		// edge and visible: the name column is the one that gives way on a narrow panel
		int valueWidth = 0;
		for (String[] row : rows)
			valueWidth = Math.max(valueWidth, metrics.stringWidth(row[1]));
		valueWidth += metrics.charWidth('0') * 2;

		var valueColumn = table.getColumnModel().getColumn(1);
		valueColumn.setCellRenderer(rightAligned);
		valueColumn.setMinWidth(valueWidth);
		valueColumn.setMaxWidth(valueWidth);
		valueColumn.setPreferredWidth(valueWidth);

		return table;
	}

	private void setResultString(String out) {
		if (!SwingUtilities.isEventDispatchThread()) {
			final String text = out;
			SwingUtilities.invokeLater(() -> setResultString(text));
			return;
		}

		bodyPanel.removeAll();
		
		if (out != null)
			out = "<html><div style='text-align: center;'>" + out + "</div></html>";
		else
			out = "";

		var label = new JLabel(out);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setEnabled(false);

		var layout = new GroupLayout(bodyPanel);
		bodyPanel.setLayout(layout);

		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addComponent(label, DEFAULT_SIZE, DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGap(0, 0, Short.MAX_VALUE)
			.addComponent(label, PREFERRED_SIZE, PREFERRED_SIZE, PREFERRED_SIZE)
			.addGap(0, 0, Short.MAX_VALUE));

		revalidate();
		repaint();
	}

	/**
	 * Scrollable content panel that follows the viewport width, so right-aligned values stay
	 * on the right edge instead of being clipped.
	 */
	private static class BodyPanel extends JPanel implements Scrollable {

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) {
			return 16;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) {
			return 64;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			// Forces the panel to stretch to full viewport height
			// if the panel's content is smaller than the scroll pane window.
			if (getParent() instanceof JViewport) {
				return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
			}
			return false;
		}
	}
}
