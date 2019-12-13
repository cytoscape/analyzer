package org.cytoscape.analyzer.tasks;

import java.util.Arrays;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.analyzer.AnalyzerManager;
import org.cytoscape.analyzer.DirNetworkAnalyzer;
import org.cytoscape.analyzer.NetworkAnalyzer;
import org.cytoscape.analyzer.UndirNetworkAnalyzer;
import org.cytoscape.analyzer.util.CyNetworkUtils;
import org.cytoscape.analyzer.util.NetworkInspection;
import org.cytoscape.analyzer.util.NetworkInterpretation;
import org.cytoscape.analyzer.util.NetworkStatus;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.AbstractNetworkCollectionTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.presentation.property.ArrowShapeVisualProperty;
import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.values.ArrowShape;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.json.JSONResult;

public class AnalyzeNetworkTask extends AbstractNetworkCollectionTask implements TunableValidator, ObservableTask {

	@Tunable(description = "Analyze as Directed Graph?")
	public Boolean directed = false;
	
//	@Tunable(description = "Analyze only selected nodes?")
	public Boolean selectedOnly = false;
	
	final CyServiceRegistrar registrar;
	final CySwingApplication desktop;
	private NetworkAnalyzer analyzer;
	final AnalyzerManager manager;
	
	public AnalyzeNetworkTask(final Collection<CyNetwork> networks, CyServiceRegistrar reg, CySwingApplication app, AnalyzerManager mgr) {
		super(networks);
		desktop = app;
		registrar = reg;
		manager = mgr;	
		directed = anyDirected(networks);   // this relies on the style defined, so is not error-free
	}

	private Boolean anyDirected(Collection<CyNetwork> networks) {
		
		for (CyNetwork net : networks)
			if (isDireceted(net)) return true;
		return false;
	}

		// guess if the network is directed, based on it having a arrow shape defined on the target
	private boolean isDireceted(CyNetwork net) {
		CyNetworkView view = registrar.getService(CyApplicationManager.class).getCurrentNetworkView();
		ArrowShape arrow = (ArrowShape) view.getVisualProperty(BasicVisualLexicon.EDGE_TARGET_ARROW_SHAPE);
		return (arrow != null && arrow != ArrowShapeVisualProperty.NONE);
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		double processed = 0.0d;
		final double increment= 1.0d/networks.size();
		
		taskMonitor.setProgress(processed);
		taskMonitor.setTitle("Analyzing Networks");

		for (final CyNetwork network : networks) {
			System.out.println((network == null ? "null" : network.getSUID()));
			taskMonitor.setStatusMessage("Analyzing Network: " + network.getRow(network).get(CyNetwork.NAME, String.class));
			
			final Set<CyNode> selectedNodes = new HashSet<CyNode>();
			Collection<CyRow> matched;
			if(selectedOnly) 
				matched = network.getDefaultNodeTable().getMatchingRows(CyNetwork.SELECTED, true);
			else 	
				matched = network.getDefaultNodeTable().getAllRows();
			
			for(CyRow row : matched)
				selectedNodes.add(network.getNode(row.get(CyIdentifiable.SUID, Long.class)));
			System.out.println(("analyze " + network.getSUID()));
			analyze(network, selectedNodes);
			processed = processed+increment;
			taskMonitor.setProgress(processed);
		}
	}

	private void analyze(final CyNetwork network, final Set<CyNode> nodes) {
		System.out.println("A:" + (network == null ? "null" : network.getSUID()));
		System.out.println("B:" + ( nodes == null ? 0 : nodes.size()));
		
		final NetworkInspection status = CyNetworkUtils.inspectNetwork(network);
		final NetworkInterpretation interpr = interpretNetwork(status);
	
		System.out.println((network == null ? "null" : network.getSUID()) + " " + nodes == null ? 0 : nodes.size());
		if(interpr == null)
			throw new NullPointerException("NetworkInterpretation is null.");
		if (nodes.size() < 4)
			throw new IllegalArgumentException("Network too small: 4 node minimum.");

		if (directed)
			analyzer = new DirNetworkAnalyzer(network, nodes, interpr, desktop, manager);
		else
			analyzer = new UndirNetworkAnalyzer(network, interpr, desktop, manager);
		
		analyzer.computeAll();
		
	}
	
	private final NetworkInterpretation interpretNetwork(NetworkInspection aInsp) {
		final NetworkStatus status = NetworkStatus.getStatus(aInsp);
		final NetworkInterpretation[] interpretations = status.getInterpretations();
		for(NetworkInterpretation ni: interpretations) {
			if(directed == ni.isDirected())
				return ni;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> R getResults(Class<? extends R> type) {
	    String response = analyzer.getStats().jsonOutput();
		if (type.equals(String.class)) {
      return (R)response;
    } else if (type.equals(JSONResult.class)) {
			JSONResult res = () -> { return analyzer.getStats().jsonOutput(); };
			return (R)res;
		}
    return null;
  }
	@Override
	public ValidationState getValidationState(Appendable errMsg) {
		return ValidationState.OK;		// no illegal options with 2 booleans
	}

	@Override
	public List<Class<?>> getResultClasses() {
		return Arrays.asList(String.class, JSONResult.class);
	}

}
