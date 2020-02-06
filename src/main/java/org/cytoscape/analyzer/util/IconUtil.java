package org.cytoscape.analyzer.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

public abstract class IconUtil {
	
	public static final String LOGO = "a";
	
	private static Font iconFont;

	static {
		try {
			iconFont = Font.createFont(Font.TRUETYPE_FONT, IconUtil.class.getResourceAsStream("/fonts/analyzer.ttf"));
		} catch (FontFormatException e) {
			throw new RuntimeException();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	public static Font getIconFont(float size) {
		return iconFont.deriveFont(size);
	}

	private IconUtil() {
		// ...
	}
}
