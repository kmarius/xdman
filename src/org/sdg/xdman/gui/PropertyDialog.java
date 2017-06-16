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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PropertyDialog extends JDialog {

	private static final long serialVersionUID = -6629147016436649030L;

	public PropertyDialog(JFrame f, DownloadListItem item) {
		super(f);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setUndecorated(true);
		setSize(400, 400);
		setLocationRelativeTo(f);
		getContentPane().setLayout(null);
		getContentPane().setBackground(StaticResource.titleColor);
		JPanel panel = new JPanel(null);
		panel.setBounds(1, 1, 398, 398 - 50 + 10);
		panel.setBackground(Color.WHITE);
		add(panel);

		JPanel titlePanel = new TitlePanel(null, this);
		titlePanel.setBackground(StaticResource.titleColor);
		titlePanel.setBounds(0, 0, 398, 50);

		JLabel titleLbl = new JLabel("DOWNLOAD PROPERTIES");
		titleLbl.setFont(StaticResource.plainFontBig2);
		titleLbl.setForeground(Color.WHITE);
		titleLbl.setBounds(20, 0, 398, 50);
		titlePanel.add(titleLbl);
		panel.add(titlePanel);

		JLabel[] lbls = new JLabel[10];
		JTextField[] txts = new JTextField[10];

		for (int i = 0; i < lbls.length; i++) {
			lbls[i] = new JLabel();
			lbls[i].setText("field " + i);
			lbls[i].setHorizontalAlignment(JLabel.LEFT);
			lbls[i].setBounds(20+30+30, i * 30 + 60, 90, 20);
			lbls[i].setForeground(Color.BLACK);
			//lbls[i].setFont(StaticResource.boldFont);
			panel.add(lbls[i]);
			txts[i] = new JTextField(30);
			txts[i].setText("value " + i);
			txts[i].setBounds(120+30+10, i * 30 + 60, 250-30-10, 20);
			txts[i].setBorder(null);
			panel.add(txts[i]);
		}

		lbls[0].setText("File Name:");
		txts[0].setText(item.filename);

		lbls[1].setText("Save in:");
		txts[1].setText(item.saveto);

		lbls[2].setText("Size:");
		txts[2].setText(item.size);

		lbls[3].setText("Type:");
		txts[3].setText(item.type);

		lbls[4].setText("Date:");
		txts[4].setText(item.dateadded);

		lbls[5].setText("Status:");
		txts[5].setText(item.status);

		lbls[6].setText("URL:");
		txts[6].setText(item.url);

		lbls[7].setText("User-Agent:");
		txts[7].setText(item.userAgent);

		lbls[8].setText("Referer:");
		txts[8].setText(item.referer);

		lbls[9].setText("Cookies:");
		StringBuffer s=new StringBuffer();
		if(item.cookies!=null){
			for(int i=0;i<item.cookies.size();i++){
				s.append(item.cookies.get(i));
			}
		}
		txts[9].setText(s.toString());
		
		
		JLabel iconLbl=new JLabel();
		iconLbl.setBounds(20,60,50,50);
		iconLbl.setIcon(item.icon);
		panel.add(iconLbl);
		

		final JButton closeBtn = new JButton("CLOSE");
		closeBtn.setBounds(270, 360 + 6, 100, 25);
		add(closeBtn);
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

}
