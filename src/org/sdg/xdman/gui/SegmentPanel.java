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

import java.awt.*;
import javax.swing.*;

public class SegmentPanel extends JComponent {
	private static final long serialVersionUID = -6537879808121349569L;
	long start[], length[], dwnld[], len;

	public void setValues(long start[], long length[], long dwnld[], long len) {
		this.length = length;
		this.start = start;
		this.dwnld = dwnld;
		this.len = len;
		repaint();
	}

	public void paintComponent(Graphics g) {
		if (g == null)
			return;

		

		Graphics2D g2 = (Graphics2D) g;
		g2.setPaint(Color.GRAY);
		g2.fillRect(0, 0, getWidth(), getHeight());
		if (start == null || length == null || dwnld == null) {
			return;
		}
		if (len == 0)
			return;
		float r = (float) getWidth() / len;
		// g2.setPaint(low);// g.setColor(Color.BLACK);
		// g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		for (int i = 0; i < start.length; i++) {
			int _start = (int) (start[i] * r);
			int _length = (int) (length[i] * r);
			int _dwnld = (int) (dwnld[i] * r);
			if (_dwnld > _length)
				_dwnld = _length;
			// g2.drawRect(_start, 0, _length, getHeight() - 1);
			g2.setPaint(StaticResource.selectedColor);
			g2.fillRect(_start, 0, _dwnld + 1, getHeight());
			// g2.setPaint(low);
			// g2.fillRect(_start, getHeight() / 2, _dwnld + 1, getHeight() -
			// 1);
			// g.setColor(Color.RED);
			// g.drawLine(_start, 0, _start, getHeight() - 1);
			// g.setColor(Color.BLACK);
		}
		// g2.setColor(Color.GRAY);
		// g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}
}
