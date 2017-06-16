package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class XDMTableHeaderRenderer extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = 1182486808273962013L;
	Color bgColor;

	public XDMTableHeaderRenderer() {
		super.setOpaque(false);
		setBorder(new EmptyBorder(2, 2, 2, 2));
		setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		bgColor = new Color(230, 230, 230);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object data,
			boolean isSelected, boolean hasFocus, int row, int column) {
		setText(data + "");
		return this;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(bgColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
		// g.drawRect(-1, 0, getWidth() - 1, getHeight() - 1);
		super.paintComponent(g);
	}
}
