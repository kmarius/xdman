package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class XDMProgressBarUI extends BasicProgressBarUI {

	GradientPaint high, low, back;

	public static ComponentUI createUI(JComponent c) {
		return new XDMProgressBarUI();
	}

	@Override
	public void paint(Graphics g, JComponent c) {

		if (!(g instanceof Graphics2D)) {
			return;
		}

		if (high == null)
			high = new GradientPaint(0, 0, new Color(117, 225, 248), 0, c
					.getHeight() / 2, new Color(88, 207, 229), false);
		if (low == null)
			low = new GradientPaint(0, 0, new Color(3, 157, 177), 0, c
					.getHeight() / 2, new Color(10, 160, 182), false);
		if (back == null)
			back = new GradientPaint(0, 0, Color.WHITE, 0, c.getHeight() / 2,
					Color.LIGHT_GRAY, false);

		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(back);
		g2.fillRect(0, 0, c.getWidth(), c.getHeight());
		g2.setColor(Color.GRAY);
		g2.drawRect(0, 0, c.getWidth() - 1, c.getHeight() - 1);

		if (progressBar.isIndeterminate()) {
			paintIndeterminate(g, c);
		} else {
			paintDeterminate(g, c);
		}

	}

	@Override
	protected void paintIndeterminate(Graphics g, JComponent c) {
		Insets b = progressBar.getInsets(); // area for border
		int barRectWidth = progressBar.getWidth() - (b.right + b.left);
		int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

		if (barRectWidth <= 0 || barRectHeight <= 0) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g;

		// Paint the bouncing box.
		boxRect = getBox(boxRect);
		if (boxRect != null) {
			g2.setPaint(high);
			g2
					.fillRect(boxRect.x, boxRect.y, boxRect.width,
							boxRect.height / 2);
			g2.setPaint(low);
			g2.fillRect(boxRect.x, boxRect.height / 2, boxRect.width,
					boxRect.height);
		}
	}

	@Override
	protected void paintDeterminate(Graphics g, JComponent c) {
		Insets b = progressBar.getInsets(); // area for border
		int barRectWidth = progressBar.getWidth() - (b.right + b.left);
		int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

		if (barRectWidth <= 0 || barRectHeight <= 0) {
			return;
		}

		// amount of progress to draw
		int amountFull = getAmountFull(b, barRectWidth, barRectHeight);

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(progressBar.getForeground());

		if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
			g2.setPaint(high);
			g2.fillRect(0, 0, amountFull, c.getHeight() / 2);
			g2.setPaint(low);
			g2.fillRect(0, c.getHeight() / 2, amountFull, c.getHeight());
		} else { // VERTICAL
		}

		// Deal with possible text painting
		if (progressBar.isStringPainted()) {
			paintString(g, b.left, b.top, barRectWidth, barRectHeight,
					amountFull, b);
		}
	}

}
