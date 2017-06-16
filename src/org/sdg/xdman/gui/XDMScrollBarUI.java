package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class XDMScrollBarUI extends BasicScrollBarUI {

	public static ComponentUI createUI(JComponent c) {
		return new XDMScrollBarUI();
	}

	Color borderColor;

	Color roColor;

	Color barColor;

	// Color borderColor = new Color(170, 170, 170);

	public XDMScrollBarUI() {
		borderColor = new Color(185, 185, 185);
		roColor = new Color(170, 170, 170);
		barColor = new Color(190, 190, 190);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		if ((scrollbar.getOrientation() == JScrollBar.HORIZONTAL)) {
			c.setPreferredSize(new Dimension(15, c.getPreferredSize().height));
		} else {
			c.setPreferredSize(new Dimension(c.getPreferredSize().width, 15));
		}
		// c.setMaximumSize(new Dimension(10, Integer.MAX_VALUE));
	}

	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
			return;
		}

		int w = thumbBounds.width;
		int h = thumbBounds.height;

		g.translate(thumbBounds.x, thumbBounds.y);

		Graphics2D g2 = (Graphics2D) g;

		if (isThumbRollover()) {
			g2.setColor(roColor);
		} else {
			g2.setColor(barColor);
		}

		g.fillRect(1, 1, w - 3, h - 3);

		g2.setColor(borderColor);
		g.drawRect(1, 1, w - 3, h - 3);
		g.translate(-thumbBounds.x, -thumbBounds.y);
	}

	protected JButton createDecreaseButton(int orientation) {
		JButton btn = new XDMButton();
		btn.setHorizontalAlignment(JButton.CENTER);
		btn.setPreferredSize(new Dimension(15, 15));
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		btn.setOpaque(false);
		if (orientation == SwingConstants.NORTH) {
			btn.setIcon(XDMIconMap.getIcon("UP_ARROW"));
		}
		if (orientation == SwingConstants.SOUTH) {
			btn.setIcon(XDMIconMap.getIcon("DOWN_ARROW"));
		}
		if (orientation == SwingConstants.EAST) {
			btn.setIcon(XDMIconMap.getIcon("LEFT_ARROW"));
		}
		if (orientation == SwingConstants.WEST) {
			btn.setIcon(XDMIconMap.getIcon("RIGHT_ARROW"));
		}
		return btn;
	}

	protected JButton createIncreaseButton(int orientation) {
		JButton btn = new XDMButton();
		btn.setHorizontalAlignment(JButton.CENTER);
		btn.setPreferredSize(new Dimension(15, 15));
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(false);
		if (orientation == SwingConstants.NORTH) {
			btn.setIcon(XDMIconMap.getIcon("UP_ARROW"));
		}
		if (orientation == SwingConstants.SOUTH) {
			btn.setIcon(XDMIconMap.getIcon("DOWN_ARROW"));
		}
		if (orientation == SwingConstants.EAST) {
			btn.setIcon(XDMIconMap.getIcon("LEFT_ARROW"));
		}
		if (orientation == SwingConstants.WEST) {
			btn.setIcon(XDMIconMap.getIcon("RIGHT_ARROW"));
		}
		return btn;
	}

	protected void paintTrack22(Graphics g, JComponent c, Rectangle r) {
		// if (trackGrad == null) {
		// trackGrad = new GradientPaint(0, 0, new Color(220, 220, 220), 0, c
		// .getHeight(), new Color(240, 240, 240));
		// }
		// Graphics2D g2 = (Graphics2D) g;
		// g2.setPaint(trackGrad);
		// g2.fill(r);
	}
}
