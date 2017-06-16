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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.util.XDMUtil;

public class DownloadCompleteDialog extends JFrame implements ActionListener {
	private static final long serialVersionUID = -6952846084893748315L;
	JTextField file, folder;
	JButton open, close, open_folder;
	JCheckBox chk;
	XDMConfig config;

	public static void main(String[] args) {
		new DownloadCompleteDialog(null).setVisible(true);
	}

	Color bgColor;

	public DownloadCompleteDialog(XDMConfig config) {
		setSize(350, 210);
		setAlwaysOnTop(true);
		this.config = config;
		bgColor = new Color(73, 73, 73);
		getContentPane().setLayout(null);
		getContentPane().setBackground(bgColor);
		setUndecorated(true);
		setResizable(false);

		JPanel titlePanel = new TitlePanel(null, this);

		titlePanel.setOpaque(false);
		titlePanel.setBounds(0, 0, 400, 50);
		JButton closeBtn = new XDMButton();

		closeBtn.setBounds(320, 5, 24, 24);
		closeBtn.setBackground(bgColor);
		//closeBtn.setContentAreaFilled(false);
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);

		closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
		// closeBtn.setRolloverIcon(StaticResource.getIcon("close_btn_r.png"));
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});

		titlePanel.add(closeBtn);

		JLabel titleLbl = new JLabel("DOWNLOAD COMPLETE");
		titleLbl.setFont(StaticResource.plainFontBig2);
		titleLbl.setForeground(StaticResource.selectedColor);
		titleLbl.setBounds(25, 15, 300, 30);
		titlePanel.add(titleLbl);

		add(titlePanel);

		JLabel lineLbl = new JLabel();
		lineLbl.setBackground(StaticResource.selectedColor);
		lineLbl.setBounds(0, 55, 400, 1);
		lineLbl.setOpaque(true);
		add(lineLbl);

		JLabel lblFile = new JLabel("File Name", JLabel.RIGHT);
		// lblFile.setVerticalTextPosition(JLabel.CENTER);//VerticalAlignment(JLabel.CENTER);
		lblFile.setBounds(0, 75, 70, 20);
		lblFile.setForeground(Color.WHITE);
		add(lblFile);

		JLabel lblSave = new JLabel("Save In", JLabel.RIGHT);
		lblSave.setBounds(0, 100, 70, 20);
		lblSave.setForeground(Color.WHITE);
		add(lblSave);

		file = new JTextField();
		file.setEditable(false);
		file.setBorder(new LineBorder(StaticResource.selectedColor, 1));
		file.setBackground(bgColor);
		file.setForeground(Color.WHITE);
		file.setBounds(80, 75, 220, 20);
		file.setCaretColor(StaticResource.selectedColor);
		add(file);

		folder = new JTextField();
		folder.setEditable(false);
		folder.setBorder(new LineBorder(StaticResource.selectedColor, 1));
		folder.setBackground(bgColor);
		folder.setForeground(Color.WHITE);
		folder.setBounds(80, 100, 220, 20);
		folder.setCaretColor(StaticResource.selectedColor);
		add(folder);

		chk = new JCheckBox(StringResource.getString("DONT_SHOW_AGAIN"));
		chk.setBackground(bgColor);
		chk.setForeground(Color.WHITE);
		chk.setBounds(80, 130, 200, 20);
		add(chk);

		JPanel panel = new JPanel(null);
		panel.setBounds(0, 155, 400, 55);
		panel.setBackground(Color.GRAY);
		add(panel);

		final JButton btnMore = new JButton("OPEN"), btnDN = new JButton(
				"OPEN FOLDER"), btnCN = new JButton("CLOSE");
		open = btnMore;
		open.addActionListener(this);
		btnMore.setBounds(0, 1, 100, 55);
		btnMore.setBackground(bgColor);
		btnMore.setForeground(Color.WHITE);
		btnMore.setFont(StaticResource.plainFontBig);
		btnMore.setBorderPainted(false);
		btnMore.setMargin(new Insets(0, 0, 0, 0));
		btnMore.setFocusPainted(false);
		btnMore.addMouseListener(StaticResource.ma);

		panel.add(btnMore);

		open_folder = btnDN;
		open_folder.addActionListener(this);
		btnDN.setBounds(101, 1, 148, 55);
		btnDN.setName("DOWNLOAD_NOW");
		btnDN.setBackground(bgColor);
		btnDN.setForeground(Color.WHITE);
		btnDN.setBorderPainted(false);
		btnDN.setFont(StaticResource.plainFontBig);
		btnDN.setBorderPainted(false);
		btnDN.setMargin(new Insets(0, 0, 0, 0));
		btnDN.setFocusPainted(false);
		btnDN.addMouseListener(StaticResource.ma);
		btnDN.addActionListener(this);
		panel.add(btnDN);

		close = btnCN;
		close.addActionListener(this);
		btnCN.setBounds(250, 1, 100, 55);
		btnCN.setName("CANCEL");
		btnCN.setBackground(bgColor);
		btnCN.setForeground(Color.WHITE);
		btnCN.setFont(StaticResource.plainFontBig);
		btnCN.setBorderPainted(false);
		btnCN.setMargin(new Insets(0, 0, 0, 0));
		btnCN.setFocusPainted(false);
		btnCN.addMouseListener(StaticResource.ma);
		btnCN.addActionListener(this);
		panel.add(btnCN);

		if (true)
			return;

		// JPanel panel = new JPanel(new GridBagLayout());
		// panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		// GridBagConstraints gc = new GridBagConstraints();
		// gc.insets = new Insets(5, 5, 5, 5);
		// gc.gridwidth = 3;
		// gc.fill = GridBagConstraints.HORIZONTAL;
		// JLabel lbl = new JLabel(StringResource.getString("DWN_DONE"));
		// lbl.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		// lbl.setHorizontalAlignment(JLabel.CENTER);
		// lbl.setHorizontalTextPosition(JLabel.CENTER);
		// lbl.setVerticalTextPosition(JLabel.CENTER);
		// lbl.setVerticalAlignment(JLabel.CENTER);
		// panel.add(lbl, gc);
		// gc.gridx = 0;
		// gc.gridy = 1;
		// panel.add(new JLabel(StringResource.getString("FILE")), gc);
		// gc.gridx = 0;
		// gc.gridy = 2;
		// file = new JTextField(30);
		// file.setBackground(Color.WHITE);
		// panel.add(file, gc);
		// file.setEditable(false);
		// gc.gridx = 0;
		// gc.gridy = 3;
		// panel.add(new JLabel(StringResource.getString("SAVE_IN")), gc);
		// folder = new JTextField(30);
		// folder.setBackground(Color.WHITE);
		// folder.setEditable(false);
		// gc.gridy = 4;
		//
		// panel.add(folder, gc);
		// open = new JButton(StringResource.getString("CTX_OPEN"));
		// open.addActionListener(this);
		// open_folder = new
		// JButton(StringResource.getString("CTX_OPEN_FOLDER"));
		// open_folder.addActionListener(this);
		// close = new JButton(StringResource.getString("CANCEL"));
		// close.addActionListener(this);
		//
		// open.setPreferredSize(open_folder.getPreferredSize());
		// open.setMinimumSize(open_folder.getPreferredSize());
		// open.setMaximumSize(open_folder.getPreferredSize());
		//
		// close.setPreferredSize(open_folder.getPreferredSize());
		// close.setMinimumSize(open_folder.getPreferredSize());
		// close.setMaximumSize(open_folder.getPreferredSize());
		//
		// gc.gridx = 0;
		// gc.gridy = 5;
		// gc.gridwidth = 1;
		// panel.add(open, gc);
		// gc.gridx = 1;
		// panel.add(open_folder, gc);
		// gc.gridx = 2;
		// panel.add(close, gc);
		// add(panel);
		//
		// chk = new JCheckBox(StringResource.getString("DONT_SHOW_AGAIN"));
		// gc.gridy = 6;
		// gc.gridx = 0;
		// gc.gridwidth = 3;
		// panel.add(chk, gc);
		// setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
	}

	void setData(String file, String path) {
		this.file.setText(file);
		this.folder.setText(path);
		setTitle(file);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == open) {
			XDMUtil.open(new File(folder.getText(), file.getText()));
			System.out.println(new File(folder.getText(), file.getText()));
			config.showDownloadCompleteDlg = !chk.isSelected();
			setVisible(false);
		}
		if (e.getSource() == open_folder) {
			XDMUtil.open(new File(folder.getText()));
			config.showDownloadCompleteDlg = !chk.isSelected();
			setVisible(false);
		}
		if (e.getSource() == close) {
			config.showDownloadCompleteDlg = !chk.isSelected();
			setVisible(false);
			dispose();
		}
	}
}
