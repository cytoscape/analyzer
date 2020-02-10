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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cytoscape.model.CyNetworkManager;

/**
 * Dialog for selecting networks on which multiple edges are to be cleared.
 * 
 * @author Yassen Assenov
 */
public class ClearMultEdgesDialog extends NetModificationDialog implements ActionListener {

	private static final long serialVersionUID = -2207325147812076427L;

	/**
	 * Check box control for selecting if edge direction should be ignored.
	 */
	private JCheckBox cbxIgnoreDirection;
	/**
	 * Check box control for selecting if an edge attribute representing the number of duplicated edges should
	 * be created.
	 */
	private JCheckBox cbxCreateEdgeAttr;

	/**
	 * Flag indicating if edge direction should be ignored, that is, if all edges are to be treated as
	 * undirected.
	 */
	private boolean ignoreDirection;
	/**
	 * Flag indicating if an edge attribute representing the number of duplicated edges should be created.
	 */
	private boolean createEdgeAttr;
	
	/**
	 * Initializes a new instance of <code>ClearMultEdgesDialog</code>.
	 * 
	 * @param aOwner
	 *            The <code>Frame</code> from which this dialog is displayed.
	 */
	public static String DT_REMDUPEDGES = "Remove Duplicated Edges";
	public static String DI_REMDUPEDGES = "Remove duplicated edges from the following networks:";
	public ClearMultEdgesDialog(Frame aOwner, CyNetworkManager netMgr) {
		super(aOwner, DT_REMDUPEDGES, DI_REMDUPEDGES, null, netMgr);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cbxIgnoreDirection) {
			ignoreDirection = cbxIgnoreDirection.isSelected();
		} else if (e.getSource() == cbxCreateEdgeAttr) {
			createEdgeAttr = cbxCreateEdgeAttr.isSelected();
		}
	}

	/**
	 * Gets the value of the &quot;Ignore edge direction&quot; flag.
	 * 
	 * @return <code>true</code> if the user has chosen to ignore edge direction; <code>false</code>
	 *         otherwise.
	 */
	public boolean getIgnoreDirection() {
		return ignoreDirection;
	}

	/**
	 * Gets the value of the &quot;Create edge attribute &quot; flag.
	 * 
	 * @return <code>true</code> if the user has chosen to create an edge attribute; <code>false</code>
	 *         otherwise.
	 */
	public boolean getCreateEdgeAttr() {
		return createEdgeAttr;
	}
	public static String DI_IGNOREEDGEDIR = "Ignore edge direction";
	public static String TT_IGNOREEDGEDIR = "Treat all edges as undirected";
	public static String TT_SAVENUMBEREDGES = "<html>Edge table column represents the number of duplicated edges<br>"
			+ "between two nodes, i.e. 1 means no duplicated edges.</html>";
	public static String DI_SAVENUMBEREDGES = "Create an edge table column with number of duplicated edges";

	@Override
	protected JComponent initAdditionalControls() {
		final JPanel panel = new JPanel(new GridLayout(2, 1));
		cbxIgnoreDirection = Utils.createCheckBox(DI_IGNOREEDGEDIR, TT_IGNOREEDGEDIR, this);
		panel.add(cbxIgnoreDirection);
		cbxCreateEdgeAttr = Utils.createCheckBox(DI_SAVENUMBEREDGES, TT_SAVENUMBEREDGES, this);
		panel.add(cbxCreateEdgeAttr);
		
		return panel;
	}
}