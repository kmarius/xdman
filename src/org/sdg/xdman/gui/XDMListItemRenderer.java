package org.sdg.xdman.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import java.awt.*;

public class XDMListItemRenderer extends JPanel implements TableCellRenderer {
	private static final long serialVersionUID = 8617303844602588189L;

	Box hbox;

	JLabel iconLbl, titleLbl, statLbl, dateLbl;

	JLabel line;

	public XDMListItemRenderer() {
		super(new BorderLayout());
		setBorder(new EmptyBorder(0, 20, 0, 20));
		setBackground(Color.WHITE);
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(new EmptyBorder(10, 10, 15, 0));
		p.setOpaque(false);

		hbox = Box.createHorizontalBox();

		iconLbl = new JLabel();// StaticResource.getIcon("pic.png"));
		iconLbl.setBorder(new EmptyBorder(10, 0, 10, 0));
		titleLbl = new JLabel("test title", JLabel.LEFT);
		titleLbl.setFont(StaticResource.plainFontBig);
		statLbl = new JLabel("status label for test status");
		statLbl.setFont(StaticResource.plainFont);
		dateLbl = new JLabel("Today");
		dateLbl.setHorizontalAlignment(JLabel.RIGHT);
		dateLbl.setFont(StaticResource.plainFont);

		hbox.add(iconLbl);
		hbox.add(p);
		p.add(titleLbl);

		Box box = Box.createHorizontalBox();
		box.add(statLbl);
		box.add(Box.createHorizontalGlue());
		box.add(dateLbl);
		box.add(Box.createRigidArea(new Dimension(5, 5)));

		p.add(box, BorderLayout.SOUTH);

		add(hbox);

		line = new JLabel();
		line.setBackground(new Color(222, 222, 222));
		line.setOpaque(true);
		line.setMinimumSize(new Dimension(10, 1));
		line.setMaximumSize(new Dimension(line.getMaximumSize().width, 1));
		line.setPreferredSize(new Dimension(line.getPreferredSize().width, 1));
		add(line, BorderLayout.SOUTH);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		DownloadListItem item = (DownloadListItem) value;
		iconLbl.setIcon(item.icon);
		titleLbl.setText(item.filename);
		String stat = "";
		if (item.q) {
			stat = "[In Queue] ";
		}
		statLbl.setText(stat + item.status);
		dateLbl.setText(item.dateadded);
		if (isSelected) {
			setBackground(StaticResource.selectedColor);
			line.setOpaque(false);
		} else {
			setBackground(Color.WHITE);
			line.setOpaque(true);
		}
		return this;
	}

	// @Override
	// public Component getListCellRendererComponent(JList list, Object obj,
	// int index, boolean selected, boolean focused) {
	//		
	// DownloadListItem item=(DownloadListItem) obj;
	// iconLbl.setIcon(item.icon);
	// titleLbl.setText(item.filename);
	// //System.out.println(item.filename);
	// statLbl.setText(item.status+" "+item.size);
	// if (selected) {
	// setBackground(StaticResource.selectedColor);
	// line.setOpaque(false);
	// } else {
	// setBackground(Color.WHITE);
	// line.setOpaque(true);
	// }
	// return this;
	// }

}
