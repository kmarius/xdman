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
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.sdg.xdman.util.XDMUtil;

public class RefreshLinkDlg {

	static String showDialog(Frame f, String url) {

		JTextField txtLink = new JTextField(30);
		txtLink.setText(url);
		txtLink.setEditable(false);
		txtLink.setBackground(Color.WHITE);

		JButton btnOpen = new JButton(StringResource.getString("RF_LBL5"));
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() instanceof JButton) {
					String url = ((JButton) e.getSource()).getName();
					try {
						if (Desktop.isDesktopSupported()) {
							Desktop.getDesktop().browse(new URI(url));
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		btnOpen.setName(url);

		JTextField txtNewLink = new JTextField();

		Object objs[] = new Object[] { StringResource.getString("RF_LBL1"),
				txtLink, btnOpen, StringResource.getString("RF_LBL2"),
				StringResource.getString("RF_LBL3"), txtNewLink };

		while (JOptionPane.showOptionDialog(f, objs, StringResource
				.getString("RF_LBL4"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.INFORMATION_MESSAGE, XDMIconMap.getIcon("RF_ICON"),
				null, null) == JOptionPane.OK_OPTION) {
			if (!XDMUtil.validateURL(txtNewLink.getText())) {
				JOptionPane.showMessageDialog(null, StringResource
						.getString("INVALID_URL"));
				continue;
			}
			return txtNewLink.getText();
		}
		return null;
	}

}
