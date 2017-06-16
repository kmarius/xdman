package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

public class XDMMenuItemUI extends BasicMenuItemUI {
	Color colorSelect, colorBg;

	public static ComponentUI createUI(JComponent c) {
		return new XDMMenuItemUI();
	}

	public XDMMenuItemUI() {
		colorSelect = StaticResource.selectedColor;
		colorBg = Color.WHITE;
	}

	@Override
	protected Dimension getPreferredMenuItemSize(JComponent c, Icon checkIcon,
			Icon arrowIcon, int defaultTextIconGap) {
		Dimension d = super.getPreferredMenuItemSize(c, checkIcon, arrowIcon,
				defaultTextIconGap);
		return new Dimension(d.width + 10, d.height);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setBorder(null);
		if (c instanceof AbstractButton) {
			AbstractButton btn = (AbstractButton) c;
			//btn.setMargin(new Insets(10,10,10,10));
			btn.setBorder(new EmptyBorder(5, 10, 5, 10));
			// btn.setIcon(new XDMBlankIcon(15, 10));
			btn.setBorderPainted(false);
			// btn.setMargin(new Insets(10, 10, 10, 10));
			// btn.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		}
		// this.lightColor = Color.WHITE;
		// this.darkColor = new Color(230, 230, 230);
		// this.lightColor2 = new Color(245, 245, 245);
		// chkIcon = UIManager.getIcon("CheckBoxMenuItem.checkIcon");
	}

	protected void paintButtonPressed(Graphics g, AbstractButton b) {
		Color c = g.getColor();
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(colorSelect);
		g2.fillRect(0, 0, b.getWidth(), b.getHeight());
		g.setColor(c);
	}

	@Override
	protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
		ButtonModel model = menuItem.getModel();
		Color oldColor = g.getColor();
		int menuWidth = menuItem.getWidth();
		int menuHeight = menuItem.getHeight();

		g.setColor(colorBg);
		g.fillRect(0, 0, menuWidth, menuHeight);

		if (model.isArmed()
				|| (menuItem instanceof JMenu && model.isSelected())) {
			paintButtonPressed(g, menuItem);
		} else {
			// if (menuItem.getIcon() != null) {
			// int gap = menuItem.getIcon().getIconWidth() + 2;
			// g.setColor(this.darkColor);
			// g.drawLine(gap, 0, gap, menuItem.getHeight());
			// g.setColor(this.lightColor);
			// g.drawLine(gap + 1, 0, gap + 1, menuItem.getHeight());
			// }
		}

		if (menuItem instanceof JCheckBoxMenuItem) {
			if (((JCheckBoxMenuItem) menuItem).isSelected()) {
				// chkIcon.paintIcon(menuItem, g, 5, 5);
			}
		}

		g.setColor(oldColor);
	}
}
