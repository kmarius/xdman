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
import java.awt.datatransfer.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.util.XDMUtil;

public class BatchDownloadDlg extends XDMFrame implements ActionListener {

	private static final long serialVersionUID = -4717157093775786226L;
	BatchTableModel model;
	JTable table;
	JTextField dir;
	JButton browse, ok, cancel, checkAll, uncheckAll;
	JCheckBox startQ;
	DownloadStateListner listener;

	public BatchDownloadDlg() {
		setSize(600, 300);
		setTitle(StringResource.getString("LIST_TTL"));
		JLabel titleLbl = new JLabel(StringResource.getString("LIST_TTL"));
		titleLbl.setForeground(Color.WHITE);
		titleLbl.setFont(StaticResource.plainFontBig2);
		titleLbl.setBorder(new EmptyBorder(10, 10, 10, 10));
		getTitlePanel().add(titleLbl);
		setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());

		JPanel panel = new JPanel(new BorderLayout());

		panel.add(new JLabel(StringResource.getString("LIST_LBL")),
				BorderLayout.NORTH);
		dir = new JTextField();
		browse = new JButton("...");
		browse.addActionListener(this);
		ok = new JButton(StringResource.getString("MSG_BOX_OK"));
		ok.addActionListener(this);
		cancel = new JButton(StringResource.getString("CANCEL"));
		cancel.addActionListener(this);
		checkAll = new JButton(StringResource.getString("LIST_LBL1"));
		checkAll.addActionListener(this);
		uncheckAll = new JButton(StringResource.getString("LIST_LBL2"));
		uncheckAll.addActionListener(this);
		startQ = new JCheckBox(StringResource.getString("LIST_LBL3"));
		startQ.addActionListener(this);
		model = new BatchTableModel();
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setShowGrid(false);

		if (System.getProperty("xdm.defaulttheme") != null) {
			table.getTableHeader().setDefaultRenderer(
					new XDMTableHeaderRenderer());
		}
		table.setFillsViewportHeight(true);

		TableColumnModel cm = table.getColumnModel();
		for (int i = 0; i < cm.getColumnCount(); i++) {
			TableColumn c = cm.getColumn(i);
			if (c.getHeaderValue().equals("#"))
				c.setPreferredWidth(20);
			else
				c.setPreferredWidth(200);
		}

		JScrollPane jsp = new JScrollPane(table);

		table.setFillsViewportHeight(true);
		jsp.getViewport().setBackground(Color.WHITE);

		// jsp.getViewport().setBackground(table.getBackground());
		// jsp.getViewport().setOpaque(false);
		// jsp.setBackground(table.getBackground());

		panel.add(jsp);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int index = table.getSelectedRow();
				if (index < 0)
					return;
				int c = table.getSelectedColumn();
				if (c != 0) {
					return;
				}
				BatchItem item = model.batchList.get(index);
				item.selected = !item.selected;
				model.fireTableRowsUpdated(index, index);
			}
		});

		Box box = Box.createVerticalBox();

		Box b1 = Box.createHorizontalBox();
		b1.add(new JLabel(StringResource.getString("SAVE_IN")));
		b1.add(Box.createRigidArea(new Dimension(5, 5)));
		dir.setMaximumSize(new Dimension(dir.getMaximumSize().width, dir
				.getPreferredSize().height));
		b1.add(dir);
		b1.add(Box.createRigidArea(new Dimension(5, 5)));
		b1.add(browse);
		box.add(b1);
		b1.setBorder(new EmptyBorder(5, 5, 5, 5));

		Box b2 = Box.createHorizontalBox();
		b2.add(checkAll);
		checkAll.setPreferredSize(uncheckAll.getPreferredSize());
		b2.add(Box.createRigidArea(new Dimension(5, 5)));
		b2.add(uncheckAll);
		b2.add(Box.createRigidArea(new Dimension(5, 5)));
		b2.add(startQ);
		b2.add(Box.createHorizontalGlue());
		b2.add(ok);
		ok.setPreferredSize(cancel.getPreferredSize());
		b2.add(Box.createRigidArea(new Dimension(5, 5)));
		b2.add(cancel);
		box.add(b2);
		// b2.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(box, BorderLayout.SOUTH);
		add(panel);
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	public void showDialog(java.util.List<BatchItem> list, String folder,
			DownloadStateListner listener) {
		try {
			// setTitle("Batch download");
			this.listener = listener;
			this.model.batchList.clear();
			dir.setText(folder);
			for (int i = 0; i < list.size(); i++) {
				this.model.batchList.add(list.get(i));
			}
			model.fireTableDataChanged();
			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showDialog(String folder, DownloadStateListner listener) {
		// setTitle("Batch download from clipboard");
		this.listener = listener;
		this.model.batchList.clear();
		dir.setText(folder);
		try {
			Object obj = Toolkit.getDefaultToolkit().getSystemClipboard()
					.getData(DataFlavor.stringFlavor);
			String txt = "";
			int count = 0;
			if (obj != null) {
				txt = obj.toString();
			} else {
				JOptionPane.showMessageDialog(this, StringResource
						.getString("LIST_MSG"));
				return;
			}
			if (txt.length() > 0) {
				String urls[] = txt.split("\n");
				for (int i = 0; i < urls.length; i++) {
					BatchItem item = new BatchItem();
					String url = urls[i];
					if (!XDMUtil.validateURL(url)) {
						continue;
					}
					count++;
					item.url = url;
					item.dir = dir.getText();
					item.fileName = XDMUtil.getFileName(url);
					// System.out.println(urls[i]);
					model.batchList.add(item);
				}
			}
			if (count < 1) {
				JOptionPane.showMessageDialog(this, StringResource
						.getString("LIST_MSG"));
				return;
			}
			model.fireTableDataChanged();
			setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, StringResource
					.getString("LIST_MSG"));
			return;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == browse) {
			JFileChooser fc = XDMFileChooser.getFileChooser(
					JFileChooser.DIRECTORIES_ONLY, null);

			if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				dir.setText(fc.getSelectedFile().getAbsolutePath());
				for (int i = 0; i < model.batchList.size(); i++) {
					model.batchList.get(i).dir = dir.getText();
				}
				model.fireTableDataChanged();
			}
		}
		if (e.getSource() == checkAll) {
			for (int i = 0; i < model.batchList.size(); i++) {
				model.batchList.get(i).selected = true;
			}
			model.fireTableDataChanged();
		}
		if (e.getSource() == uncheckAll) {
			for (int i = 0; i < model.batchList.size(); i++) {
				model.batchList.get(i).selected = false;
			}
			model.fireTableDataChanged();
		}
		if (e.getSource() == cancel) {
			setVisible(false);
		}
		if (e.getSource() == ok) {
			for (int i = 0; i < model.batchList.size(); i++) {
				BatchItem item = model.batchList.get(i);
				if (item.selected) {
					listener.add2Queue(item.url, item.fileName, item.dir,
							item.user, item.pass, null, null, null, startQ
									.isSelected());
				}
			}
			listener.startQueue();
			setVisible(false);
		}
	}

	public static void main(String[] args) {
		new BatchDownloadDlg()
				.showDialog(System.getProperty("user.home"), null);
	}

}
