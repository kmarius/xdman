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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JWindow;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.util.XDMUtil;

public class DropBox extends JWindow implements ActionListener {
	private static final long serialVersionUID = -6560446385567170000L;
	JLabel label;
	int relx, rely;
	DownloadStateListner mgr;
	Icon lblIcon;
	JButton closeBtn;
	MediaTableModel model;

	JMenu menu;

	public DropBox(JLabel label, DownloadStateListner mgr, MediaTableModel m,
			DownloadStateListner dl, XDMConfig config) {
		setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
		setAlwaysOnTop(true);
		add(label);
		this.mgr = mgr;
		this.label = label;
		this.model = m;
		this.dl = dl;
		this.config = config;
		this.label.setTransferHandler(new XDMTransferHandler(mgr));
		add(label);

		label.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent me) {
				int x = getX();
				int y = getY();
				setLocation(x + me.getX() - relx, y + me.getY() - rely);
			}
		});
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				relx = me.getX();
				rely = me.getY();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				int x = getLocationOnScreen().x;
				int y = getLocationOnScreen().y;
				if (DropBox.this.config.dbX != x
						|| DropBox.this.config.dbY != y) {
					DropBox.this.config.dbX = x;
					DropBox.this.config.dbY = y;
					System.out.println("Saving dropbox location...");
					DropBox.this.config.save();
				}
			}
		});

		setSize(22, 22);

		closeBtn = new JButton();
		closeBtn.setContentAreaFilled(false);
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);
		closeBtn.setMargin(new Insets(0, 0, 0, 0));

		closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVideoPopup(false);
				model.clear();
				validate();
				repaint();
			}
		});
		add(closeBtn, BorderLayout.EAST);
		closeBtn.setVisible(false);
		closeBtn.setBackground(StaticResource.selectedColor);

		menu = new JMenu("DOWNLOAD VIDEO");
		menu.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		menu.setForeground(Color.WHITE);
		menu.setBackground(StaticResource.selectedColor);
		menu.setBorderPainted(false);

		menu.getPopupMenu().addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {

			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				populateMenu();
			}

		});

		bar = new JMenuBar();
		bar.setOpaque(false);
		bar.setCursor(hc);

		bar.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent me) {
				int x = getX();
				int y = getY();
				setLocation(x + me.getX() - relx, y + me.getY() - rely);
			}
		});
		bar.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				relx = me.getX();
				rely = me.getY();
			}
		});

		getContentPane().setBackground(StaticResource.selectedColor);

		// bar.setLayout(new GridLayout());
		bar.setBackground(StaticResource.selectedColor);
		bar.setBorderPainted(false);

		bar.add(Box.createHorizontalGlue());
		bar.add(menu);
		bar.add(Box.createHorizontalGlue());

		bar.setVisible(false);
	}

	JMenuBar bar;

	boolean isVideoMode = false;

	void setVideoPopup(boolean enable) {
		try {
			if (!enable) {
				if (!isVideoMode)
					return;
				setSize(1, 1);
				if (isVideoMode) {
					int x = getLocationOnScreen().x + VIDEO_WIDTH - 22;
					if (x > 0) {
						x = Math.min(x, Toolkit.getDefaultToolkit()
								.getScreenSize().width - 30);
					}
					setLocation(x, getLocationOnScreen().y);
				}
				// label.setText("");
				// if (lblIcon != null) {
				// label.setIcon(lblIcon);
				// }
				closeBtn.setVisible(false);
				// label.setCursor(nc);
				add(label);
				label.setBackground(Color.BLACK);
				// label.setVisible(true);
				// bar.setVisible(false);
				remove(bar);
			} else {
				if (isVideoMode)
					return;
				int x = getLocationOnScreen().x - VIDEO_WIDTH + 22;
				setSize(VIDEO_WIDTH, 22);
				label.setOpaque(true);
				label.setBackground(StaticResource.selectedColor);
				label.setForeground(Color.WHITE);
				setLocation(x, getLocationOnScreen().y);
				// lblIcon = label.getIcon();
				// label.setIcon(null);
				label.setFont(StaticResource.plainFontBig);
				// label.setText("DOWNLOAD VIDEO");
				closeBtn.setVisible(true);
				bar.setVisible(true);
				add(bar);
				remove(label);
				// label.setVisible(false);
				// label.setCursor(hc);
			}
			isVideoMode = enable;
		} finally {
			validate();
			repaint();
		}
	}

	Cursor hc = new Cursor(Cursor.HAND_CURSOR);

	public static final int VIDEO_WIDTH = 200;

	void menuClicked(int i) {
		MediaInfo info = model.list.get(i);
		dl.addDownload(info.url, info.name, config.destdir, null, null,
				info.referer, info.cookies, info.userAgent);
	}

	DownloadStateListner dl;
	XDMConfig config;
	JMenuItem items[];

	void populateMenu() {
		if (!isVideoMode) {
			return;
		}
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				menu.remove(items[i]);
			}
		}
		items = new JMenuItem[model.list.size()];
		for (int i = 0; i < model.getRowCount(); i++) {
			try {
				MediaInfo mi = model.list.get(i);
				String name = mi.name;
				String info = (mi.type == null ? "" : mi.type) + " "
						+ (mi.size == null ? "" : mi.size);
				name = XDMUtil.createSafeFileName(name);
				if (name.length() > 30) {
					name = name.substring(0, 30) + "...";
				}
				JMenuItem item = new JMenuItem(name + " " + info.toUpperCase());
				item.addActionListener(this);
				menu.add(item);
				items[i] = item;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				if (e.getSource() == items[i]) {
					menuClicked(i);
				}
			}
		}
	}
}
