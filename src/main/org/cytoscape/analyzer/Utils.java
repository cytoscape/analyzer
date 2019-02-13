package org.cytoscape.analyzer;

/**
 * Utility class providing helper methods for data manipulation.
 * 
 * @author Yassen Assenov
 */
public abstract class Utils {

//	/**
//	 * Computes a relaxed power function.
//	 * 
//	 * @param a Base number.
//	 * @param b Power number.
//	 * @return <code>a<sup>b</sup></code> if <code>a</code> &ne; <code>0</code>; <code>0</code>
//	 *         otherwise.
//	 */
//	public static double pow(double a, double b) {
//		if (a != 0) {
//			return Math.pow(a, b);
//		}
//		return 0;
//	}

	/**
	 * Rounds a double <code>value</code> to <code>digit</code> digits after the point.
	 * 
	 * @param aValue Value to be rounded. 
	 * @param aDigits Digits the value should have after the point after the rounding.
	 * @return <code>Double</code> object that contains the rounded <code>value</code>.       
	 */
	public static Double roundTo(double aValue, int aDigits) {
		double d = Math.pow(10, aDigits);
		return new Double(Math.round(aValue*d)/d);
	}
}
