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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class HelpDialog extends JFrame implements ListSelectionListener,
		ActionListener {

	private static final long serialVersionUID = 2861769092407816472L;
	JButton back, next;
	JEditorPane htmlPane;
	HelpListModel model;
	JList helpList;
	private static HelpDialog dlg;
	HashMap<String, URL> map = new HashMap<String, URL>();
	String keys[] = { "BROWSER_INTEGRATION", "CAPTURE_VIDEO", "REFRESH_LINK" };
	String values[] = { "Browser integration", "How to save videos",
			"Refresh broken download" };

	public static HelpDialog getHelpDialog() {
		if (dlg == null) {
			dlg = new HelpDialog();
		}
		return dlg;
	}

	public HelpDialog() {
		setTitle(StringResource.getString("DEFAULT_TITLE"));
		setSize(640, 480);
		htmlPane = new JEditorPane();
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.add(new JScrollPane(htmlPane));
		add(panel);
		model = new HelpListModel();
		helpList = new JList(values);
		JPanel p = new JPanel(new BorderLayout());
		p.add(createToolBar(), BorderLayout.NORTH);
		p.add(new JScrollPane(helpList));
		panel.add(p, BorderLayout.WEST);
		setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
		helpList.addListSelectionListener(this);
		htmlPane.setEditable(false);
		map.put(keys[0], getClass().getResource(
				"/help/browser_integration.html"));
		map.put(keys[1], getClass().getResource("/help/video_download.html"));
		map.put(keys[2], getClass().getResource("/help/refresh_link.html"));
	}

	JToolBar createToolBar() {
		JToolBar toolbar = new JToolBar();
		next = new JButton(XDMIconMap.getIcon("NEXT_ICON"));
		XDMToolBarButtonUI btnUI = new XDMToolBarButtonUI();
		next.setUI(btnUI);
		next.addActionListener(this);
		next.setRolloverIcon(XDMIconMap.getIcon("NEXT_R_ICON"));
		next.setContentAreaFilled(false);
		next.setFocusPainted(false);
		back = new JButton(XDMIconMap.getIcon("BACK_ICON"));
		back.setUI(btnUI);
		back.setContentAreaFilled(false);
		back.setFocusPainted(false);
		back.addActionListener(this);
		back.setRolloverIcon(XDMIconMap.getIcon("BACK_R_ICON"));
		toolbar.add(back);
		toolbar.add(next);
		return toolbar;// add(toolbar, BorderLayout.NORTH);
	}

	public void valueChanged(ListSelectionEvent e) {
		int index = helpList.getSelectedIndex();
		if (index < 0)
			return;
		String key = keys[index];
		setDocument(key);
	}

	public void setDocument(String page) {
		try {
			setPage(page);
		} catch (Exception e) {

		}
	}

	void setPage(String key) throws IOException {
		htmlPane.setPage(map.get(key));
	}

	public void addPages(HashMap<String, URL> map) {
		model.map = map;
		helpList.setModel(model);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == back) {
			int index = helpList.getSelectedIndex();
			if (index <= 0) {
				index = 0;
			} else {
				index--;
			}
			helpList.setSelectedIndex(index);
		}
		if (e.getSource() == next) {
			int index = helpList.getSelectedIndex();
			if (index < 0) {
				index = 0;
			} else {
				if (index < keys.length - 1) {
					index++;
				}
			}
			helpList.setSelectedIndex(index);
		}
	}
}
