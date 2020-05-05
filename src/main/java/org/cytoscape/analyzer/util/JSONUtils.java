package org.cytoscape.analyzer.util;

import java.util.HashMap;
import java.util.Map;

/**
 *  Some useful JSON utils
 *
 */
public class JSONUtils  {
	//-----------------------------------------------
	public static Map<String, Object> jsonToMap(String json) {
		String[] lines = json.split("\n");
		Map<String, Object> map = new HashMap<>();
		
		for (String line : lines)
		{
			if (line.trim().length() == 0) continue;
			String[] tokens = line.split(":");
			if (tokens.length < 2) continue;
			String key = clean(tokens[0]);
			Object val = clean(tokens[1]);
			String s = val.toString();
			if (isDouble(s) && !isInteger(s))
				val = Double.parseDouble(val.toString());
				
			map.put(key,val);
		}
		return map;
	}
	
	private static String clean(String in) {
		String trimmed = in.trim();
		if (trimmed.charAt(0) == '"')
			trimmed = trimmed.substring(1);
		if (trimmed.endsWith(","))
			trimmed = trimmed.substring(0, trimmed.length()-1);
		if (trimmed.endsWith("\""))
			trimmed = trimmed.substring(0, trimmed.length()-1);
		return trimmed;
	}

	private static boolean isDouble( String input ) {
	    try {
	    	Double.parseDouble( input );	        return true;
	    }
	    catch( NumberFormatException e ) {	        return false;	    }
	}
	
	private static boolean isInteger( String input ) {
	    try {
	    	Integer.parseInt( input );	        return true;
	    }
	    catch( NumberFormatException e ) {      return false;    }
	}
}
