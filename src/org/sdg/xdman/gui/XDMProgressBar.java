/*
 * Copyright (c)  Subhra Das Gupta
 *
 * This file is part of Xtream Download Manager.
 *
 * Xtream Download Manager is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Xtream Download Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with Xtream Download Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */


package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

public class XDMProgressBar extends JComponent {
	private static final long serialVersionUID = 8688628575007423155L;
	int value = 0;
	GradientPaint high, low, back;

	@Override
	protected void paintComponent(Graphics g) {
		if (g == null)
			return;
		if (high == null)
			high = new GradientPaint(0, 0, new Color(117, 225, 248), 0,
					getHeight() / 2, new Color(88, 207, 229), false);
		if (low == null)
			low = new GradientPaint(0, 0, new Color(3, 157, 177), 0,
					getHeight() / 2, new Color(10, 160, 182), false);
		if (back == null)
			back = new GradientPaint(0, 0, Color.WHITE, 0, getHeight() / 2,
					Color.LIGHT_GRAY, false);
		int pos = (int) (((float) getWidth() / 100) * value);
		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(back);
		g2.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2.setPaint(high);
		g2.fillRect(0, 0, pos, getHeight() / 2);
		g2.setPaint(low);
		g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		g2.fillRect(0, getHeight() / 2, pos, getHeight() - 1);
	}

	public void setValue(int value) {
		this.value = value;
		repaint();
	}
}
