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

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.analyzer.util.CyNetworkUtils;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action handler for the menu item &quot;Remove Self-loops&quot;.
 * 
 * @author Yassen Assenov
 * @author Sven-Eric Schelhorn
 */
public class RemoveSelfLoopsAction extends NetAnalyzerAction {

	private static final long serialVersionUID = -7465036491341908005L;
	private static final Logger logger = LoggerFactory.getLogger(RemoveSelfLoopsAction.class);
	
	private final CyNetworkManager netMgr;
	CySwingApplication desktop;

	/**
	 * Initializes a new instance of <code>RemoveSelfLoopsAction</code>.
	 */
	public RemoveSelfLoopsAction(CyApplicationManager appMgr,CySwingApplication swingApp, CyNetworkManager netMgr) {
		super(AC_REMSELFLOOPS,appMgr);
		setPreferredMenu("Edit");
		setMenuGravity(4.2f);
		this.netMgr = netMgr;
		desktop = swingApp;
	}
	public static String DI_REMOVESL = "Remove self-loops from the following networks:";
	public static String AC_REMSELFLOOPS = "Remove Self-Loops...";
	public static String SM_REMSELFLOOPS = " self-loop(s) removed from ";
	public static String SM_LOGERROR = "NetworkAnalyzer - Internal Error";
	public static String DT_REMSELFLOOPS = "Remove Self-Loops";

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (!selectNetwork()) {
				return;
			}

			final Frame frame = desktop.getJFrame();
			final String helpURL = null;  // HelpConnector.getRemSelfloopsURL();
			final NetModificationDialog d = new NetModificationDialog(frame, DT_REMSELFLOOPS,
					DI_REMOVESL, helpURL, netMgr);
			d.setVisible(true);

			// Remove the self-loops from all networks selected by the user
			final CyNetwork[] networks = d.getSelectedNetworks();
			
			if (networks != null) {
				final int size = networks.length;
				int[] removedLoops = new int[size];
				String[] networkNames = new String[size];
				
				for (int i = 0; i < size; ++i) {
					final CyNetwork currentNet = networks[i];
					networkNames[i] = currentNet.getRow(currentNet).get("name",String.class);
					removedLoops[i] = CyNetworkUtils.removeSelfLoops(currentNet);
				}

				final String r = constructReport(removedLoops, SM_REMSELFLOOPS, networkNames);
				Utils.showInfoBox(frame, DT_REMSELFLOOPS, r);
			}
		} catch (Exception ex) {
			// NetworkAnalyzer internal error
			logger.error(SM_LOGERROR, ex);
		}
	}

	private String constructReport(int[] removedLoops, String s, String[] networkNames) {
		// TODO Auto-generated method stub
		return null;
	}
}