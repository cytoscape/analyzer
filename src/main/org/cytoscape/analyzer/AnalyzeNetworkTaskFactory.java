package org.cytoscape.analyzer;

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

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkCollectionTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class AnalyzeNetworkTaskFactory extends AbstractNetworkCollectionTaskFactory implements TaskFactory {
	private final CySwingApplication app; 
	private final CyServiceRegistrar reg;
	
	public AnalyzeNetworkTaskFactory(CyServiceRegistrar registrar, CySwingApplication desktop)
	{
		reg = registrar;
		app = desktop;
	}
	@Override
	public TaskIterator createTaskIterator(final Collection<CyNetwork> networks) {
		return new TaskIterator(new AnalyzeNetworkTask(networks, reg, app));
	}
	@Override
	public TaskIterator createTaskIterator() {
		return null;
	}
	@Override
	public boolean isReady() {
		return false;
	}

}
