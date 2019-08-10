package java.org.cytoscape.analyzer.util;


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

/**
 * Data type storing information about the shortest path lengths from one node to other nodes in the
 * networks.
 * 
  */
public class PathLengthData {
	private int count;
	private long totalLength;
	private int maxLength;

	/**
	 * Initializes a new instance of <code>PathLengthData</code>.
	 */
	public PathLengthData() {
		count = 0;
		totalLength = 0;
		maxLength = 0;
	}

	/**
	 * Accumulates a new shortest path length to this data instance.
	 * 
	 * @param aLength Length of shortest path to be accumulated.
	 */
	public void addSPL(int aLength) {
		count++;
		totalLength += aLength;
		if (maxLength < aLength) {
			maxLength = aLength;
		}
	}

	public int getCount() {		return count;	}
	public long getTotalLength() {		return totalLength;	}
	public int getMaxLength() {		return maxLength;	}
	public double getAverageLength() {
		return (count == 0) ? 0 :((double) totalLength) / count;
	}
}
