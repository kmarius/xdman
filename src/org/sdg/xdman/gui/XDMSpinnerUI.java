package org.sdg.xdman.gui;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;

public class XDMSpinnerUI extends BasicSpinnerUI {
	public static ComponentUI createUI(JComponent c) {
		return new XDMSpinnerUI();
	}

	protected Component createNextButton() {
		JButton c = new BasicArrowButton(SwingConstants.NORTH);
		c.setName("Spinner.nextButton");
		installNextButtonListeners(c);
		return c;
	}

	protected Component createPreviousButton() {
		Component c = new BasicArrowButton(SwingConstants.SOUTH);
		c.setName("Spinner.previousButton");
		installPreviousButtonListeners(c);
		return c;
	}

}
