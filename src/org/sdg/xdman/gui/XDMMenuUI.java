package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

public class XDMMenuUI extends BasicMenuUI {
	Color colorSelect,colorBg;

	public static ComponentUI createUI(JComponent c) {
		return new XDMMenuUI();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		this.colorBg = Color.WHITE;
		this.colorSelect=StaticResource.selectedColor;
	}

	protected void paintButtonPressed(Graphics g, AbstractButton b) {
		Color c = g.getColor();

		

		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(colorSelect);
		g2.fillRect(0, 0, b.getWidth(), b.getHeight());
		
//		if ("THEME".equals(b.getName()) || "CTX_SORT".equals(b.getName())) {
//			g2.setPaint(gradPressed);
//			int gapx = 0;
//
//			g2.fillRect(gapx + 2, 0, b.getWidth() - (4 + gapx + 2), b
//					.getHeight() - 2);
//			g2.setColor(Color.LIGHT_GRAY);
//			g2.drawRect(gapx + 2, 0, b.getWidth() - (4 + gapx + 2), b
//					.getHeight() - 2);
//			if (menuItem.getIcon() != null) {
//				int gap = menuItem.getIcon().getIconWidth() + 2;
//				g.setColor(this.darkColor);
//				g.drawLine(gap, 1, gap, menuItem.getHeight() - 3);
//				g.setColor(this.lightColor2);
//				g.drawLine(gap + 1, 1, gap + 1, menuItem.getHeight() - 3);
//			}
//		} else {
//			g2.setPaint(gradPressed);
//			g2.fillRoundRect(0, 0, b.getWidth() - 1, b.getHeight() - 1, 4, 4);
//			g2.setColor(Color.LIGHT_GRAY);
//			g2.drawRoundRect(0, 0, b.getWidth() - 1, b.getHeight() - 1, 4, 4);
//		}
		g.setColor(c);
	}

	@Override
	protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
		ButtonModel model = menuItem.getModel();
		Color oldColor = g.getColor();
		if (model.isArmed()
				|| (menuItem instanceof JMenu && model.isSelected())) {
			paintButtonPressed(g, menuItem);
		} else {
			g.setColor(this.colorBg);
			//g.fillRect(0, 0, menuItem.getWidth(), menuItem.getHeight());//(0, 0, gap + 1, menuItem.getHeight());
//			g.drawLine(gap + 1, 0, gap + 1, menuItem.getHeight());
//			if (menuItem.getIcon() != null) {
//				int gap = menuItem.getIcon().getIconWidth() + 2;
//				g.setColor(this.darkColor);
//				g.drawLine(gap, 0, gap, menuItem.getHeight());
//				g.setColor(this.lightColor);
//				g.drawLine(gap + 1, 0, gap + 1, menuItem.getHeight());
//			}
		}
		g.setColor(oldColor);
	}
}
