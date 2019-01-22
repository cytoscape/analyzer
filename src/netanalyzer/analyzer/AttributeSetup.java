package analyzer;

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


import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyTable;


final public class AttributeSetup {

	private AttributeSetup() {}

	public static void createDirectedNodeAttributes(CyTable nodeTable) {
		createCommonNodeAttributes(nodeTable);
		createAttr(nodeTable,Msgs.getAttr("dal"), Integer.class);
		createAttr(nodeTable,Msgs.getAttr("din"), Integer.class);
		createAttr(nodeTable,Msgs.getAttr("dou"), Integer.class);
		createAttr(nodeTable,Msgs.getAttr("nbt"), Double.class);
		createAttr(nodeTable,Msgs.getAttr("nco"), Double.class);
	}

	public static void createUndirectedNodeAttributes(CyTable nodeTable) {
		createCommonNodeAttributes(nodeTable);
		createAttr(nodeTable,Msgs.getAttr("deg"), Integer.class);
		createAttr(nodeTable,Msgs.getAttr("nbt"), Double.class); 
		createAttr(nodeTable,Msgs.getAttr("nco"), Double.class);
		createAttr(nodeTable,Msgs.getAttr("nde"), Integer.class);
		createAttr(nodeTable,Msgs.getAttr("nue"), Integer.class);
		createAttr(nodeTable,Msgs.getAttr("rad"), Double.class);
		createAttr(nodeTable,Msgs.getAttr("tco"), Double.class);
	}

	private static void createCommonNodeAttributes(CyTable nodeTable) {
		createAttr(nodeTable,Msgs.getAttr("apl"), Double.class); 
		createAttr(nodeTable,Msgs.getAttr("cco"), Double.class);
		createAttr(nodeTable,Msgs.getAttr("clc"), Double.class);
		createAttr(nodeTable,Msgs.getAttr("isn"), Boolean.class);
		createAttr(nodeTable,Msgs.getAttr("pmn"), Integer.class);
		createAttr(nodeTable,Msgs.getAttr("slo"), Integer.class);
		createAttr(nodeTable,Msgs.getAttr("spl"), Integer.class);
		createAttr(nodeTable,Msgs.getAttr("stress"), Long.class);
	}

	public static void createEdgeBetweennessAttribute(CyTable edgeTable) {
		createAttr(edgeTable,Msgs.getAttr("ebt"),Double.class);
	}
	
	public static void createEdgeDuplicateAttribute(CyTable edgeTable) {
		createAttr(edgeTable,Msgs.getAttr("dpe"),Integer.class);
	}

	private static void createAttr(final CyTable table, final String col, final Class<?> newType) {
		final CyColumn column = table.getColumn(col);
		if (column == null)
			table.createColumn(col, newType, false);
		else if (column.getType() == newType)
			return;
		else
			throw new IllegalArgumentException("trying to set table column: " + col + 
			                                   " to type: " + newType.getName() + " when it already " +
							   " has a type of: " + column.getType().getName());
	}
}
