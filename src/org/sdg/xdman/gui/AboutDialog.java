package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = -214717043594709353L;

	Color bgColor;

	public AboutDialog() {
		init();
	}

	void init() {
		setUndecorated(true);
		setSize(350, 260);
		getContentPane().setLayout(null);
		bgColor = new Color(73, 73, 73);
		getContentPane().setBackground(bgColor);

		JPanel titlePanel = new TitlePanel(null, this);
		titlePanel.setOpaque(false);
		titlePanel.setBounds(0, 0, 350, 50);

		JLabel titleLbl = new JLabel("XDM 2016", JLabel.CENTER);
		titleLbl.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
		titleLbl.setForeground(StaticResource.selectedColor);
		titleLbl.setBounds(0, 0, 320, 74);
		titlePanel.add(titleLbl);
		add(titlePanel);

		JLabel lineLbl = new JLabel();
		lineLbl.setBackground(StaticResource.selectedColor);
		lineLbl.setBounds(0, 75, 350, 1);
		lineLbl.setOpaque(true);
		add(lineLbl);

		JLabel iconLbl = new JLabel(XDMMainWindow.getIcon("icon.png"));
		iconLbl.setBounds(32, 95, 70, 70);
		add(iconLbl);

		JTextArea txtInfo = new JTextArea(
				"6.2 For Linux & Mac OS X\nBuilt on Nov 9, 2016\nCopyright (C) 2016\nSubhra Das Gupta");
		txtInfo.setBounds(114, 100, 201, 80);
		txtInfo.setEditable(false);
		txtInfo.setBackground(bgColor);
		txtInfo.setForeground(Color.WHITE);
		add(txtInfo);

		ImageIcon icon = XDMIconMap.getIcon("APP_ICON");

		setTitle(StringResource.getString("ABT_TTL"));
		setIconImage(icon.getImage());

		JPanel p = new JPanel(null);
		p.setBackground(Color.GRAY);
		p.setBounds(0, 190, 350, 70);
		add(p);

		JButton okBtn = new JButton("OK");
		okBtn.setForeground(Color.WHITE);
		okBtn.setFont(StaticResource.plainFontBig2);
		okBtn.setBackground(bgColor);
		okBtn.setBounds(0, 1, 350, 70);
		p.add(okBtn);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog.this.setVisible(false);
			}
		});

		if (true) {
			return;
		}

		// bgColor = new Color(70, 99, 152);
		//
		// Box hBox = Box.createHorizontalBox();
		//
		// hBox.setBorder(new EmptyBorder(10, 10, 10, 10));
		// hBox.setOpaque(true);
		// hBox.setBackground(bgColor);
		//
		// JLabel iconLbl = new JLabel(icon);
		// iconLbl.setBackground(Color.WHITE);
		// iconLbl.setOpaque(true);
		// iconLbl.setBorder(new LineBorder(Color.BLACK));
		// Dimension d = new Dimension(icon.getIconWidth() + 10, icon
		// .getIconHeight() + 10);
		// iconLbl.setPreferredSize(d);
		// iconLbl.setMinimumSize(d);
		// iconLbl.setMaximumSize(d);
		//
		// hBox.add(iconLbl);
		//
		// Box vBox = Box.createVerticalBox();
		// vBox.setBorder(new EmptyBorder(10, 10, 10, 10));
		//
		// JLabel titleLbl = new JLabel("Xtreme Download Manager");
		// titleLbl.setForeground(Color.WHITE);
		// titleLbl.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		// titleLbl.setHorizontalAlignment(JLabel.LEFT);
		// titleLbl.setVerticalAlignment(JLabel.BOTTOM);
		// titleLbl.setHorizontalTextPosition(JLabel.LEFT);
		// titleLbl.setVerticalTextPosition(JLabel.BOTTOM);
		//
		// JLabel verLbl = new JLabel(AppInfo.VERSION);
		// verLbl.setForeground(Color.WHITE);
		// verLbl.setHorizontalAlignment(JLabel.LEFT);
		// verLbl.setVerticalAlignment(JLabel.TOP);
		// verLbl.setHorizontalTextPosition(JLabel.LEFT);
		// verLbl.setVerticalTextPosition(JLabel.TOP);
		//
		// vBox.add(titleLbl);
		// vBox.add(verLbl);
		//
		// hBox.add(vBox);
		//
		// Box vBox2 = Box.createVerticalBox();
		// vBox2.setPreferredSize(hBox.getPreferredSize());
		// vBox2.setBorder(new EmptyBorder(10, 10, 10, 10));
		// vBox2.setBackground(Color.WHITE);
		// vBox2.setOpaque(true);
		//
		// vBox2.add(Box.createVerticalGlue());
		//
		// JLabel lbl1 = new JLabel(StringResource.getString("ABT_LBL1"));
		// final JLabel lbl2 = new JLabel(StringResource.getString("ABT_LBL2"));
		// lbl2.setForeground(Color.BLUE);
		// lbl2.addMouseListener(new MouseAdapter() {
		// Cursor cursor;
		// Cursor handCursor;
		//
		// @Override
		// public void mouseEntered(MouseEvent e) {
		// cursor = lbl2.getCursor();
		// if (handCursor == null) {
		// handCursor = new Cursor(Cursor.HAND_CURSOR);
		// }
		// lbl2.setCursor(handCursor);
		// }
		//
		// @Override
		// public void mouseExited(MouseEvent e) {
		// if (cursor != null) {
		// lbl2.setCursor(cursor);
		// }
		// }
		//
		// @Override
		// public void mouseClicked(MouseEvent e) {
		// XDMUtil.browse(AppInfo.HOMEPAGE);
		// }
		// });
		// // lbl2.setUI(new XDMToolBarButtonUI());
		// // lbl2.setMargin(new Insets(0, 0, 0, 0));
		// Map map = lbl2.getFont().getAttributes();
		// map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		// lbl2.setFont(lbl2.getFont().deriveFont(map));
		// // lbl2.setContentAreaFilled(false);
		// JLabel lbl3 = new JLabel("Copyright (C) 2014 Subhra Das Gupta");
		//
		// vBox2.add(Box.createVerticalStrut(30));
		// vBox2.add(lbl1);
		// vBox2.add(Box.createVerticalStrut(10));
		// vBox2.add(lbl2);
		// vBox2.add(Box.createVerticalStrut(20));
		// vBox2.add(lbl3);
		// vBox2.add(Box.createVerticalStrut(30));
		//
		// vBox2.add(Box.createVerticalGlue());
		// vBox2.setPreferredSize(new Dimension(vBox2.getPreferredSize().width,
		// (int) (hBox.getPreferredSize().height * 1.7)));
		//
		// Box hBox2 = Box.createHorizontalBox();
		// JButton okBtn = new JButton(StringResource.getString("MSG_BOX_OK"));
		// okBtn.setMargin(new Insets(2, 20, 2, 20));
		// okBtn.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// AboutDialog.this.setVisible(false);
		// }
		// });
		// hBox2.add(Box.createHorizontalGlue());
		// hBox2.add(okBtn);
		// hBox2.setBorder(new EmptyBorder(10, 10, 10, 10));
		//
		// add(hBox, BorderLayout.NORTH);
		// add(vBox2);
		// add(hBox2, BorderLayout.SOUTH);
		//
		// pack();

	}
}
