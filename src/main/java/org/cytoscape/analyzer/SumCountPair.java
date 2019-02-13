package java.org.cytoscape.analyzer;

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
 * Utility class for storing the sum and length of a non-empty sequence of numbers.
 * 
 * @author Yassen Assenov
 */
public class SumCountPair {

	/**
	 * Initializes a new instance of <code>SumCountPair</code>.
	 * 
	 * @param aValue The first number of the sequence.
	 */
	public SumCountPair(double aValue) {
		mSum = aValue;
		mCount = 1;
	}
	public void add(double aValue) {
		mSum += aValue;
		mCount++;
	}

	public double getSum() 		{		return mSum;	}
	public double getAverage() 	{		return mSum / mCount;	}
	public int getCount() 		{		return mCount;	}

	private double mSum;
	private int mCount;
}
