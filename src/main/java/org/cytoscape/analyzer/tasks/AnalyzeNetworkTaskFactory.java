package org.cytoscape.analyzer.tasks;

import java.util.ArrayList;

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

import java.util.Collection;
import java.util.List;

import org.cytoscape.analyzer.AnalyzerManager;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkCollectionTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class AnalyzeNetworkTaskFactory extends AbstractNetworkCollectionTaskFactory implements TaskFactory {
	private final CySwingApplication app; 
	private final CyServiceRegistrar reg;
	private final AnalyzerManager mgr;
	private boolean degreeOnly = false;
	
	public AnalyzeNetworkTaskFactory(CyServiceRegistrar registrar, CySwingApplication desktop, AnalyzerManager manager)
	{
		reg = registrar;
		app = desktop;
		mgr = manager;
	}

	@Override
	public TaskIterator createTaskIterator(final Collection<CyNetwork> networks) {
		return new TaskIterator(new AnalyzeNetworkTask(networks, reg, app, mgr));
	}
	@Override
	public TaskIterator createTaskIterator() {
		CyApplicationManager appMgr = reg.getService(CyApplicationManager.class);
		CyNetwork current = appMgr.getCurrentNetwork();
			
		 List<CyNetwork> networks = new ArrayList<CyNetwork>();
		 networks.add(current);
		return new TaskIterator(new AnalyzeNetworkTask(networks, reg, app, mgr));

	}
	@Override
	public boolean isReady() {
		return true;
	}
	
	static public boolean isDirected(final Collection<CyNetwork> networks)
	{
		for (CyNetwork net : networks)
			if (isDirected(net)) return true;
		return false;
	}
	static public boolean isDirected(CyNetwork current)
	{
		List<CyEdge> edges = current.getEdgeList();
		int ct = Math.min(100, edges.size());
		for (int i = 0; i < ct; i++)
			if (edges.get(i).isDirected()) 
				return true;
	return false;
		
	}

}
