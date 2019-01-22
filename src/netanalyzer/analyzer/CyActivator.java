package analyzer;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
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
		CyApplicationManager cyApplicationManagerServiceRef = getService(bc,CyApplicationManager.class);
		CyNetworkViewManager viewManagerServiceRef = getService(bc,CyNetworkViewManager.class);
		final AnalyzerManager manager = new AnalyzerManager(registrar);
		
		Map<String,String> props = new HashMap<String, String>();
		props.put(ID, "analyzeNetworkAction");
		props.put(TITLE, "Analyze Network");
		props.put(PREFERRED_MENU,"Tools");
		props.put(MENU_GRAVITY,"9.0");
		props.put(IN_TOOL_BAR, "false");
		props.put(ENABLE_FOR, "network");

		props.put(COMMAND_NAMESPACE, "analyzer");
		props.put(COMMAND, "analyze");
		props.put(COMMAND_DESCRIPTION,  "Calculate network statistics");
		props.put(COMMAND_LONG_DESCRIPTION, "Run algorithms to calculate a set of statistics on the network, and write those statistics to the node and network tables.");
		props.put(COMMAND_EXAMPLE_JSON, "");
		props.put(COMMAND_SUPPORTS_JSON, "true");

		AnalyzeNetworkAction analyzeNetworkAction = new AnalyzeNetworkAction(cyApplicationManagerServiceRef,viewManagerServiceRef, props, manager, desktop);
		AnalyzeNetworkTaskFactory analyzeNetworkTaskFactory = new AnalyzeNetworkTaskFactory(registrar, desktop );
//		registerAllServices(bc,analyzeNetworkTaskFactory, analyzeNetworkAction, new Properties());
		registerService(bc,analyzeNetworkAction,CyAction.class, new Properties());
		registerService(bc,analyzeNetworkTaskFactory,AnalyzeNetworkTaskFactory.class);


		{
			ShowResultsPanelTaskFactory results = new ShowResultsPanelTaskFactory(manager);
			Properties props1 = new Properties();
			props1.put(TITLE, "Show Results Panel");
			props1.put(PREFERRED_MENU, "Tools");
			props1.setProperty(IN_TOOL_BAR, "FALSE");
			props1.setProperty(IN_MENU_BAR, "TRUE");
			manager.registerService(results, TaskFactory.class, props1);
		}

	}
}
