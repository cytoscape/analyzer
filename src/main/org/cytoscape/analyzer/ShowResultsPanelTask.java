package org.cytoscape.analyzer;

import java.util.Properties;

import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class ShowResultsPanelTask extends AbstractTask {
	final AnalyzerManager manager;
	NetworkStats experiment = null;

	public ShowResultsPanelTask(final AnalyzerManager manager) {
		super();
		this.manager = manager;
	}
	public void run(TaskMonitor monitor) {

		ResultsPanel resultsPanel = new ResultsPanel(manager);
		manager.registerService(resultsPanel, CytoPanelComponent.class, new Properties());
		manager.registerService(resultsPanel, SetCurrentNetworkListener.class, new Properties());

		CySwingApplication swingApp = manager.getService(CySwingApplication.class);
		CytoPanel panel = swingApp.getCytoPanel(CytoPanelName.EAST);
		panel.setState(CytoPanelState.DOCK);
	}
}
