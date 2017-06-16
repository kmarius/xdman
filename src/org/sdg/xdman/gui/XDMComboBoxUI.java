package org.sdg.xdman.gui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class XDMComboBoxUI extends BasicComboBoxUI {

	static XDMComboBoxUI buttonUI;

	JComponent c;

	public static ComponentUI createUI(JComponent c) {
		return new XDMComboBoxUI();
	}

	protected JButton createArrowButton() {
		JButton button = new XDMButton();
		button.setIcon(XDMIconMap.getIcon("DOWN_ARROW"));
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setName("ComboBox.arrowButton");
		return button;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		this.c = c;
	}
}
