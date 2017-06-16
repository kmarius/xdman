package org.sdg.xdman.gui;

import java.awt.*;

import javax.swing.*;

public class CategoryBtn extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7018359807269926427L;
	Color selectBgColor, rolloverBgColor;
	Image imgBar;

	public CategoryBtn() {
		super();
		imgBar = new ImageIcon("Resources/Icons/bg_nav.png").getImage();
		rolloverBgColor = StaticResource.selectedColor;
		selectBgColor = Color.WHITE;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(selectBgColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Color.BLACK);
		g.drawString(getText(), 20, getHeight() / 2 - fm.getHeight() / 2);
	}
}
