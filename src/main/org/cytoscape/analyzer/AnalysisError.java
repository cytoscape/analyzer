package org.cytoscape.analyzer;

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
 * Enumeration on possible errors which can occur on a single network analysis during batch processing.
 * 
 * @author Nadezhda Doncheva
 */
public enum AnalysisError {

	/**
	 * Output (.netstats) file could not be created.
	 */
	OUTPUT_NOT_CREATED,

	/**
	 * I/O error has occurred while writing to the netstats file.
	 */
	OUTPUT_IO_ERROR,

	/**
	 * Exception has occurred during computation of topological parameters.
	 */
	INTERNAL_ERROR,

	/**
	 * Network with no nodes loaded.
	 */
	NETWORK_EMPTY,
	
	/**
	 * Network file is invalid.
	 */
	NETWORK_FILE_INVALID,

	/**
	 * Network file could not be opened.
	 */
	NETWORK_NOT_OPENED;

	/**
	 * Gets the message explaining the occurred <code>aError</code> to the user. 
	 * 
	 * @param aError Error occurred during batch analysis.
	 * @return Message for the user explaining the occurred error.
	 */
	public static String getMessage(AnalysisError aError) {
		switch (aError) {
		case OUTPUT_NOT_CREATED:	return Msgs.SM_OUTPUTNOTCREATED;
		case OUTPUT_IO_ERROR:		return Msgs.SM_OUTPUTIOERROR;
		case INTERNAL_ERROR:		return Msgs.SM_INTERNALERROR;
		case NETWORK_EMPTY:			return Msgs.SM_NETWORKEMPTY;
		case NETWORK_FILE_INVALID:	return Msgs.SM_NETWORKFILEINVALID;
		case NETWORK_NOT_OPENED:	return Msgs.SM_NETWORKNOTOPENED;
		default:					return Msgs.SM_UNKNOWNERROR;
		}
	}
}
