package org.sdg.xdman.gui;

import java.util.HashMap;

import javax.swing.ImageIcon;

public class XDMIconMap {
	static HashMap<String, ImageIcon> iconMap = new HashMap<String, ImageIcon>();

	public static ImageIcon getIcon(String id) {
		return iconMap.get(id);
	}

	public static void setIcon(String id, ImageIcon icon) {
		iconMap.put(id, icon);
	}
}
