package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class XDMBlankIcon implements Icon {

	int width, height;

	// GradientPaint grad;

	Color lightColor, darkColor;

	public XDMBlankIcon(int width, int height) {
		this.width = width;
		this.height = height;
		this.lightColor = Color.WHITE;
		this.darkColor = new Color(230, 230, 230);
	}

	@Override
	public int getIconHeight() {
		return this.height;
	}

	@Override
	public int getIconWidth() {
		return this.width;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		// //g.setColor(c.getBackground());
		// //g.fillRect(0, 0, 15, c.getHeight());
		// g.setColor(this.darkColor);
		// g.drawLine(13, 0, 13, c.getHeight());
		// g.setColor(this.lightColor);
		// g.drawLine(14, 0, 14, c.getHeight());
		// // if (c instanceof JMenuItem) {
		// // JMenuItem mi = (JMenuItem) c;
		// // ButtonModel model = mi.getModel();
		// // if (model.isSelected()) {
		// // g.setColor(c.getBackground());
		// // g.fillRect(0, 0, 15, c.getHeight());
		// // return;
		// // }
		// // }
		// // if (grad == null) {
		// // grad = new GradientPaint(0, 0, new Color(240, 240, 240), width, 0,
		// // new Color(220, 220, 220));
		// // }
		// // Graphics2D g2 = (Graphics2D) g;
		// // g2.setPaint(grad);
		// // g2.fillRect(0, 0, 15, c.getHeight());
	}

}
