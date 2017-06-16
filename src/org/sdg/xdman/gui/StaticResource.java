package org.sdg.xdman.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class StaticResource {
	public static Color whiteColor = new Color(235, 235, 235);
	public static Color titleColor = new Color(14, 20, 25);
	public static Color selectedColor = new Color(51, 181, 229);
	public static Color btnBgColor = selectedColor;// new Color(36, 122, 241);//
													// new Color(14,
	// 136, 177);//
	// new Color(37,
	// 117,
	// 240);//new
	// Color(0, 114,
	// 198);
	public static Font plainFont = new Font(Font.DIALOG, Font.PLAIN, 12);
	public static Font boldFont = new Font(Font.DIALOG, Font.BOLD, 12);

	public static Font plainFontBig = new Font(Font.DIALOG, Font.PLAIN, 14);
	public static Font plainFontBig2 = new Font(Font.DIALOG, Font.PLAIN, 18);

	public static ImageIcon getIcon(String name) {
		try {
			java.net.URL url = StaticResource.class.getResource("/Resources/Icons/" + name);
			// System.out.println(url);
			if (url == null)
				throw new Exception();
			return new ImageIcon(url);
		} catch (Exception e) {
			return new ImageIcon("Resources/Icons/" + name);
		}
	}

	public static ImageIcon getIcon(String name, String folder) {
		try {
			java.net.URL url = StaticResource.class.getResource("/" + folder + "/" + name);
			if (url == null)
				throw new Exception();
			return new ImageIcon(url);
		} catch (Exception e) {
			return new ImageIcon(folder + "/" + name);
		}
	}

	static MouseAdapter ma = new MouseAdapter() {
		Color bgColor;

		@Override
		public void mouseEntered(MouseEvent e) {
			bgColor = ((JButton) e.getSource()).getBackground();
			((JButton) e.getSource()).setBackground(StaticResource.selectedColor);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			((JButton) e.getSource()).setBackground(bgColor);
		}
	};
}
