package org.cytoscape.analyzer;


import static org.cytoscape.work.ServiceProperties.COMMAND;
import static org.cytoscape.work.ServiceProperties.COMMAND_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_EXAMPLE_JSON;
import static org.cytoscape.work.ServiceProperties.COMMAND_LONG_DESCRIPTION;
import static org.cytoscape.work.ServiceProperties.COMMAND_NAMESPACE;
import static org.cytoscape.work.ServiceProperties.COMMAND_SUPPORTS_JSON;

/*
 * #%L
 * Cytoscape NetworkAnalyzer Impl (network-analyzer-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import static org.cytoscape.work.ServiceProperties.ENABLE_FOR;
import static org.cytoscape.work.ServiceProperties.ID;
import static org.cytoscape.work.ServiceProperties.IN_MENU_BAR;
import static org.cytoscape.work.ServiceProperties.IN_TOOL_BAR;
import static org.cytoscape.work.ServiceProperties.MENU_GRAVITY;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

import java.util.Properties;

import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.NetworkCollectionTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;


public class CyActivator extends AbstractCyActivator {
	
	public CyActivator() {
		super();
	}
	
	@Override
	public void start(BundleContext bc) {
        final CyServiceRegistrar registrar = getService(bc, CyServiceRegistrar.class);
        final CySwingApplication desktop = getService(bc, CySwingApplication.class);
		final AnalyzerManager manager = new AnalyzerManager(registrar);		

		Properties props = new Properties();
		
		{	// create and register the task factory to run the analysis
			props.clear();
			props.put(ID, "analyzeNetworkTaskFactory");	
			props.put(TITLE, "Analyze Network");
			props.put(PREFERRED_MENU,"Tools");
			props.put(MENU_GRAVITY,"9.1");
			props.put(IN_MENU_BAR, "true");
			props.put(IN_TOOL_BAR, "false");
			props.put(ENABLE_FOR, "network");
			props.put(COMMAND_NAMESPACE, "analyzer");
			props.put(COMMAND, "analyze");
			props.put(COMMAND_DESCRIPTION,  "Calculate statistics on the current network");
			props.put(COMMAND_LONG_DESCRIPTION, "Run algorithms to calculate a set of statistics on the network, and write those statistics to the node and network tables.");
			props.put(COMMAND_EXAMPLE_JSON, "{   \"networkTitle\": \"galFiltered.sif (undirected)\",   \"nodeCount\": \"330\",  \"avNeighbors\": \"2.167\"}");
			props.put(COMMAND_SUPPORTS_JSON, "true");

			AnalyzeNetworkTaskFactory analyzeNetworkTaskFactory = new AnalyzeNetworkTaskFactory(registrar, desktop );
			registerService(bc,analyzeNetworkTaskFactory, NetworkCollectionTaskFactory.class, props);
			registerService(bc,analyzeNetworkTaskFactory, TaskFactory.class, props);
		}

		{	// create and register the results panel, 
			// and listen for network change events, 
			// so we always show the current network stats
			ResultsPanel resultsPanel = new ResultsPanel(manager);
			registerService(bc, resultsPanel, CytoPanelComponent.class);
			registerService(bc, resultsPanel, SetCurrentNetworkListener.class);
			CytoPanel panel = desktop.getCytoPanel(CytoPanelName.EAST);
			panel.setState(CytoPanelState.DOCK);
		}
		{	String version = "4.0.1a";			// TODO keep in synch with POM

			VersionTaskFactory versionTask = new VersionTaskFactory(version);
			props = new Properties();
			props.setProperty(COMMAND_NAMESPACE, "analyzer");
			props.setProperty(COMMAND, "version");
			props.setProperty(COMMAND_DESCRIPTION, "Display the analyzer version");
			props.setProperty(COMMAND_LONG_DESCRIPTION, "Display the version of the analyzer app.");
			props.setProperty(COMMAND_SUPPORTS_JSON, "true");
			props.setProperty(COMMAND_EXAMPLE_JSON, "{\"version\":\"1.0\"}");
			registerService(bc, versionTask, TaskFactory.class, props);

		}
	}
}
