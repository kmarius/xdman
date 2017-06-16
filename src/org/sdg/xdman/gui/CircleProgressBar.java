package org.sdg.xdman.gui;

import javax.swing.*;
import java.awt.*;

public class CircleProgressBar extends JComponent {
	private static final long serialVersionUID = 3778513245025142955L;

	public CircleProgressBar() {
		foreColor = StaticResource.selectedColor;
		backColor = new Color(73, 73, 73);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (g2 == null) {
			return;
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// g2.setRenderingHint(RenderingHints.KEY_RENDERING,
		// RenderingHints.VALUE_RENDER_QUALITY);

		int sweep_angle = (value * 360) / 100;
		g2.setColor(Color.GRAY);
		g2.setStroke(stroke);
		g2.drawArc(2, 2, getWidth() - 4 - 8, getHeight() - 4 - 8, 90, -360);
		g2.setColor(foreColor);
		g2.drawArc(2, 2, getWidth() - 4 - 8, getHeight() - 4 - 8, 90,// (int)(-90*(Math.PI/2)),
				-sweep_angle);

		g2.setFont(StaticResource.plainFontBig2);
		FontMetrics fm = g2.getFontMetrics();
		String str = value + "%";
		int w = fm.stringWidth(str);
		g2.drawString(str, getWidth() / 2 - w / 2, getHeight() / 2 + 5);
	}

	Stroke stroke = new BasicStroke(4);
	private int value;

	Color foreColor, backColor;

	public void setValue(int value) {
		this.value = value;
		repaint();
	}

	public int getValue() {
		return value;
	}
}
