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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.util.LinuxUtil;
import org.sdg.xdman.util.OSXUtil;
import org.sdg.xdman.util.XDMUtil;

public class BrowserIntDlg extends JDialog implements ActionListener {

	private static final long serialVersionUID = -6629147016436649030L;
	JTabbedPane pane;
	JPanel p0, p1, p2, p3;
	JTextArea text1, text2, text3, text4;
	JLabel ff;
	JButton helpff, auto, man, openfolder1, chromeHelp, openfolder2, operaHelp;
	JCheckBox autoStart;
	JCheckBox adv;
	boolean unix = false;
	JButton btn1, btn2;
	XDMConfig config;

	Color bgColor;

	JPanel centerPanel;

	JPanel chromePanel, ffPanel, othersPanel;

	CardLayout card;

	JButton cb, fb, ob;

	JPanel cardPanel;

	public BrowserIntDlg(JFrame f, XDMConfig config) {
		super(f);
		setUndecorated(true);
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(new LineBorder(Color.BLACK));
		add(centerPanel);
		this.config = config;
		setTitle(StringResource.getString("BI_LBL_11"));
		setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());

		TitlePanel tp = new TitlePanel(new BorderLayout(), this);
		tp.setBackground(StaticResource.titleColor);

		JLabel title = new JLabel(StringResource.getString("BI_LBL_11"));
		title.setForeground(Color.WHITE);

		title.setBorder(new EmptyBorder(20, 20, 20, 20));
		title.setFont(title.getFont().deriveFont(Font.BOLD,
				title.getFont().getSize() * 1.2f));
		tp.add(title, BorderLayout.CENTER);

		JButton closeBtn = new XDMButton();
		closeBtn.setBounds(320, 5, 24, 24);
		closeBtn.setBackground(StaticResource.titleColor);
		// closeBtn.setContentAreaFilled(false);
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);
		closeBtn.setMargin(new Insets(0, 0, 0, 0));

		closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		Box b = Box.createVerticalBox();
		b.setBorder(new EmptyBorder(5, 5, 5, 5));
		b.add(closeBtn);
		b.add(Box.createVerticalGlue());

		tp.add(b, BorderLayout.EAST);

		add(tp, BorderLayout.NORTH);

		ff = new JLabel(XDMIconMap.getIcon("APP_ICON"));
		bgColor = new Color(UIManager.getColor("Label.background").getRGB());
		ff.setText("http://127.0.0.1:9614/xdmff.xpi");
		ff.setName("http://127.0.0.1:9614/xdmff.xpi");
		ff.setHorizontalAlignment(JLabel.CENTER);
		ff.setVerticalAlignment(JLabel.CENTER);
		ff.setHorizontalTextPosition(JLabel.CENTER);
		ff.setVerticalTextPosition(JLabel.BOTTOM);
		ff.setBackground(Color.WHITE);
		ff.setOpaque(true);
		ff.setBorder(new LineBorder((Color.GRAY)));
		ff.setCursor(new Cursor(Cursor.MOVE_CURSOR));
		ff.setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Transferable createTransferable(JComponent c) {
				return new StringSelection(c.getName());
			}

			public int getSourceActions(JComponent c) {
				return COPY;
			}
		});
		ff.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JComponent c = (JComponent) e.getSource();
				TransferHandler th = c.getTransferHandler();

				th.exportAsDrag(c, e, TransferHandler.COPY);
			}
		});

		btn1 = new JButton(StringResource.getString("BI_LBL_7"));
		btn1.addActionListener(this);
		btn2 = new JButton(StringResource.getString("BI_LBL_12"));
		btn2.addActionListener(this);
		autoStart = new JCheckBox(StringResource.getString("BI_LBL_8"));
		autoStart.addActionListener(this);
		autoStart.setSelected(config.autostart);
		autoStart.setBorder(new EmptyBorder(20, 20, 20, 20));
		autoStart.setBackground(Color.WHITE);

		createChromePanel();
		createFFPanel();
		createOthersPanel();

		card = new CardLayout();

		cardPanel = new JPanel(card);

		cardPanel.add(chromePanel, "CP");
		cardPanel.add(ffPanel, "FP");
		cardPanel.add(othersPanel, "OP");

		card.first(cardPanel);

		centerPanel.add(cardPanel);

		setSize(450, 450);

		cb = new XDMButton("Google Chrome");
		fb = new XDMButton("Mozilla Firefox");
		ob = new XDMButton("Other Browsers");

		cb.setIcon(StaticResource.getIcon("chrome.png"));
		fb.setIcon(StaticResource.getIcon("firefox.png"));
		ob.setIcon(StaticResource.getIcon("other.png"));

		cb.setBackground(StaticResource.selectedColor);

		cb.addActionListener(this);
		fb.addActionListener(this);
		ob.addActionListener(this);

		prepareButton(cb);
		prepareButton(fb);
		prepareButton(ob);

		JPanel bp = new JPanel(new GridLayout());

		bp.add(cb);
		bp.add(fb);
		bp.add(ob);

		centerPanel.add(bp, BorderLayout.NORTH);
		createExt();
	}

	private void createExt() {
		try {
			File folder = new File(System.getProperty("user.home"),
					"xdm-helper");
			folder.mkdirs();
			XDMUtil.copyStream(getClass().getResourceAsStream(
					"/ext/xdm-helper/background.js"), new FileOutputStream(
					new File(folder, "background.js")));
			XDMUtil.copyStream(getClass().getResourceAsStream(
					"/ext/xdm-helper/manifest.json"), new FileOutputStream(
					new File(folder, "manifest.json")));
			XDMUtil.copyStream(
					getClass().getResourceAsStream("/ext/xdmff.xpi"),
					new FileOutputStream(new File(folder, "xdmff.xpi")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void prepareButton(JButton btn) {
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setHorizontalAlignment(JButton.CENTER);
		btn.setHorizontalTextPosition(JButton.CENTER);
		btn.setVerticalTextPosition(JButton.BOTTOM);
	}

	void createChromePanel() {
		chromePanel = new JPanel(new BorderLayout());
		chromePanel.setBackground(Color.WHITE);
		JLabel lbl = new JLabel(XDMIconMap.getIcon("CI_ICON"), JLabel.LEFT);
		lbl.setBorder(new EmptyBorder(20, 20, 20, 20));
		chromePanel.add(lbl, BorderLayout.NORTH);
		JTextArea text3 = new JTextArea();
		text3.setBackground(bgColor);
		text3.setOpaque(false);
		text3.setWrapStyleWord(true);
		text3.setEditable(false);
		text3.setLineWrap(true);
		text3.setBorder(new EmptyBorder(0, 20, 20, 20));
		String txt = new File(System.getProperty("user.home"), "xdm-helper")
				.getAbsolutePath();
		text3.setText(StringResource.getString("BI_LBL_17").replace("<FOLDER>",
				txt));
		chromePanel.add(text3);
	}

	void createFFPanel() {
		ffPanel = new JPanel(new BorderLayout(20, 20));
		ffPanel.setBackground(Color.WHITE);
		ffPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		text2 = new JTextArea();
		Cursor c = text2.getCursor();
		text2.setBackground(bgColor);
		text2.setOpaque(false);
		text2.setWrapStyleWord(true);
		text2.setEditable(false);
		text2.setLineWrap(true);
		text2.setText(StringResource.getString("BI_LBL_2"));
		text2.setCursor(c);
		ffPanel.add(text2, BorderLayout.NORTH);
		ffPanel.add(ff);

		helpff = new JButton(StringResource.getString("BI_LBL_3"));
		helpff.addActionListener(this);
		JPanel pp = new JPanel(new BorderLayout(10, 10));
		pp.setBackground(Color.white);
		JTextArea txt2 = new JTextArea();
		txt2.setOpaque(false);
		txt2.setWrapStyleWord(true);
		txt2.setEditable(false);
		txt2.setLineWrap(true);
		String txt = new File(System.getProperty("user.home"),
				"xdm-helper/xdmff.xpi").getAbsolutePath();
		txt2.setText(StringResource.getString("BI_LBL_FF").replace("<FILE>",
				txt));
		pp.add(txt2);
		pp.add(helpff, BorderLayout.SOUTH);
		ffPanel.add(pp, BorderLayout.SOUTH);
	}

	void createOthersPanel() {
		othersPanel = new JPanel(new BorderLayout(10, 10));
		othersPanel.setBackground(Color.WHITE);
		othersPanel.setBorder(new EmptyBorder(0, 20, 0, 20));
		JTextArea txt1 = new JTextArea(StringResource.getString("BI_LBL_6"));
		txt1.setBorder(new EmptyBorder(20, 20, 20, 20));
		txt1.setBackground(bgColor);
		txt1.setOpaque(false);
		txt1.setWrapStyleWord(true);
		txt1.setEditable(false);
		txt1.setLineWrap(true);
		othersPanel.add(txt1, BorderLayout.NORTH);

		JPanel biPanel = new JPanel(new GridLayout(2, 1, 20, 20));
		biPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		biPanel.add(btn1);
		biPanel.add(btn2);
		biPanel.setBackground(Color.WHITE);

		othersPanel.add(biPanel);

		othersPanel.add(autoStart, BorderLayout.SOUTH);
	}

	void setProxy() {
		if (JOptionPane.showConfirmDialog(this, StringResource
				.getString("BI_LBL_13"), StringResource
				.getString("DEFAULT_TITLE"), JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
			String osName = System.getProperty("os.name");
			if (osName.contains("OS X")) {
				if (!OSXUtil.attachProxy()) {
					JOptionPane.showMessageDialog(null, StringResource
							.getString("BI_LBL_14"), StringResource
							.getString("DEFAULT_TITLE"),
							JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, StringResource
							.getString("BI_LBL_16"));
				}
			} else {
				if (!LinuxUtil.attachProxy()) {
					JOptionPane.showMessageDialog(null, StringResource
							.getString("BI_LBL_14"), StringResource
							.getString("DEFAULT_TITLE"),
							JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, StringResource
							.getString("BI_LBL_16"));
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn1) {
			setProxy();
		} else if (e.getSource() == cb) {
			card.show(cardPanel, "CP");
			cb.setBackground(StaticResource.selectedColor);
			ob.setBackground(bgColor);
			fb.setBackground(bgColor);
		} else if (e.getSource() == fb) {
			card.show(cardPanel, "FP");
			fb.setBackground(StaticResource.selectedColor);
			ob.setBackground(bgColor);
			cb.setBackground(bgColor);
		} else if (e.getSource() == ob) {
			card.show(cardPanel, "OP");
			ob.setBackground(StaticResource.selectedColor);
			fb.setBackground(bgColor);
			cb.setBackground(bgColor);
		} else if (e.getSource() == autoStart) {
			if (autoStart.isSelected()) {
				if (System.getProperty("os.name").contains("OS X")) {
					OSXUtil.enableAutoStart();
				} else {
					LinuxUtil.enableAutoStartLinux();
				}
				config.autostart = true;
			} else {
				if (System.getProperty("os.name").contains("OS X")) {
					OSXUtil.disableAutoStart();
				} else {
					LinuxUtil.disableAutoStartLinux();
				}
				config.autostart = false;
			}
		} else if (e.getSource() == btn2) {
			showHelp("BROWSER_INTEGRATION");
		} else if (e.getSource() == helpff) {
			showHelp("CAPTURE_VIDEO");
		}
	}

	void showHelp(String topic) {
		try {
			HelpDialog hlp = HelpDialog.getHelpDialog();
			hlp.setDocument(topic);
			hlp.setLocationRelativeTo(null);
			hlp.setVisible(true);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
}
