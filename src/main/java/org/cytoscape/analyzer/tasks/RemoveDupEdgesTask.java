package org.cytoscape.analyzer.tasks;

/*
 * #%L
 * Cytoscape NetworkAnalyzer Impl (network-analyzer-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013
 *   Max Planck Institute for Informatics, Saarbruecken, Germany
 *   The Cytoscape Consortium
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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import org.cytoscape.analyzer.util.CyNetworkUtils;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.ObservableTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.json.JSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Action handler for the menu item &quot;Remove Self-loops&quot;.
 * 
 * @author Yassen Assenov
 * @author Sven-Eric Schelhorn
 */
public class RemoveDupEdgesTask extends AbstractTask implements ObservableTask {

  private static final Logger logger = LoggerFactory.getLogger(RemoveDupEdgesTask.class);
  
  @Tunable(description="Network to remove the duplicate edges from", required=true)
  public CyNetwork network;

  @Tunable(description="Ignore edge direction")
  public boolean ignoreDirection = false;

  @Tunable(description="Create column with number of duplicated edges")
  public boolean createColumn = false;

  private String[] networkNames;
  private int[] removedLoops;

  /**
   * Initializes a new instance of <code>RemoveSelfLoopsAction</code>.
   */
  public RemoveDupEdgesTask() {
  }

  public static String SM_REMDUPEDGES = " edges removed from ";

  @Override
  public void run(TaskMonitor taskMonitor) {
    networkNames = new String[1];
    removedLoops = new int[1];
    if (network != null) {
      networkNames[0] = network.getRow(network).get("name",String.class);
      removedLoops[0] = CyNetworkUtils.removeDuplEdges(network, ignoreDirection, createColumn);

    }
  }

  @Override
  public List<Class<?>> getResultClasses() {
    return Arrays.asList(String.class, JSONResult.class);
  }

  @Override
  public <R> R getResults(Class<? extends R> type) {
    String r = constructReport(removedLoops, SM_REMDUPEDGES, networkNames);
    if (type.isAssignableFrom(JSONResult.class)) {
      JSONResult res = () -> {
        String str = "{\"network\":"+network.getSUID()+",";
        str += "\"removed\":"+removedLoops[0]+"}";
        return str;
      };
      return (R) res;
    }
    return (R)r;
  }

  private String constructReport(int[] removedLoops, String s, String[] networkNames) {
    String report = "";
    for (int index = 0; index < removedLoops.length; index++)
      report += removedLoops[index]+s+networkNames[index]+"\n";
    return report;
  }
}
