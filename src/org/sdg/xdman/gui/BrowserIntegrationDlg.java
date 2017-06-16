package org.sdg.xdman.gui;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import javax.swing.*;
import org.sdg.xdman.core.common.*;
import org.sdg.xdman.util.*;

public class BrowserIntegrationDlg extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4979809466939750489L;

	int diffx, diffy;

	XDMConfig config;

	final static String CHROME_URL = "https://chrome.google.com/webstore/detail/xdm-helper/fhlkncjkeinpblgldbehianfehcablpf";// "https://chrome.google.com/webstore/detail/xdm-hook-for-linux-and-ma/dbpjgfeohpghjediekpagokkeaafmklj";
	final static String FIREFOX_URL = "https://addons.mozilla.org/en-US/firefox/addon/xdm-hook/";// "https://addons.mozilla.org/en-GB/firefox/addon/xdm-hook-for-linux-mac-os-x/";
	final static String OTHER_URL = "http://xdman.sourceforge.net/xdm_helper.crx";// "https://chrome.google.com/webstore/detail/xdm-hook-for-linux-and-ma/dbpjgfeohpghjediekpagokkeaafmklj";

	public BrowserIntegrationDlg(JFrame f, XDMConfig config) {
		super(f);
		this.config = config;
		setUndecorated(true);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				diffx = me.getXOnScreen() - getLocationOnScreen().x;
				diffy = me.getYOnScreen() - getLocationOnScreen().y;
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent me) {
				setLocation(me.getXOnScreen() - diffx, me.getYOnScreen() - diffy);
			}
		});

		getContentPane().setBackground(new Color(40, 40, 40));

		setLayout(null);

		setSize(410, 350);

		JLabel title = new JLabel("BROWSER INTEGRATION");
		title.setFont(StaticResource.plainFontBig2);
		title.setForeground(Color.WHITE);
		title.setBounds(35, 30, 300, 30);
		add(title);

		JLabel note = new JLabel("Please select your browser");
		note.setForeground(Color.WHITE);
		note.setBounds(35, 70, 300, 30);
		add(note);

		JButton btn1 = new XDMButton(StaticResource.getIcon("chrome-128.png"/* "chrome-128.png" */));
		btn1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				caption.setText("Please select your browser");
				txt.setText(CHROME_URL);
				showDialog(true);
			}
		});
		btn1.setForeground(Color.LIGHT_GRAY);
		btn1.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn1.setHorizontalTextPosition(SwingConstants.CENTER);
		btn1.setText("Chrome/Opera");
		btn1.setBackground(Color.DARK_GRAY);
		btn1.setBounds(35, 110, 165, 160);
		btn1.setBorderPainted(false);
		add(btn1);

		JButton btn2 = new XDMButton(StaticResource.getIcon("firefox-128.png"));
		btn2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				caption.setText("Open Firefox and type bellow address");
				txt.setText(FIREFOX_URL);
				showDialog(false);
			}
		});

		btn2.setText("Mozilla Firefox");
		btn2.setForeground(Color.LIGHT_GRAY);
		btn2.setVerticalTextPosition(SwingConstants.BOTTOM);
		btn2.setHorizontalTextPosition(SwingConstants.CENTER);
		btn2.setBackground(Color.DARK_GRAY);
		btn2.setBounds(210, 110, 160, 160);
		btn2.setBorderPainted(false);
		add(btn2);

		JLabel note2 = new JLabel("Start monitoring when you turn on PC");
		note2.setForeground(Color.WHITE);
		note2.setBounds(35, 295, 300, 30);
		add(note2);

		btnTablet = new JLabel(StaticResource.getIcon("on.png"));
		btnTablet.putClientProperty("xdmbutton.norollover", "true");
		btnTablet.setBounds(270, 295, 128, 30);
		btnTablet.setOpaque(false);
		btnTablet.setName("TABLET");
		add(btnTablet);

		btnTablet.setIcon(config.autostart ? StaticResource.getIcon("on.png") : StaticResource.getIcon("off.png"));

		btnTablet.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				toggleMode();
			}
		});

		JButton closeBtn = new XDMButton();
		closeBtn.setBounds(370, 5, 24, 24);
		closeBtn.setBackground(getContentPane().getBackground());
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);
		closeBtn.setMargin(new Insets(0, 0, 0, 0));

		closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		add(closeBtn);

		txt = new JTextField(30);

		caption = new JLabel("");

		cpbtn = new XDMButton("Copy");
		cpbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txt.getText()), null);
			}
		});
		cpbtn.setBorderPainted(false);
		cpbtn.setBackground(StaticResource.selectedColor);
		cpbtn.setForeground(Color.WHITE);

		chromeBrowsers = new JComboBox(new String[] { "Google Chrome/Chromium", "Opera", "Vivaldi", "Slimjet",
				"Other chromium based browser" });
		chromeBrowsers.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				System.out.println(chromeBrowsers.getSelectedIndex());
				if (chromeBrowsers.getSelectedItem().toString().equals("Opera")) {
					txt.setText(OTHER_URL);
				} else {
					txt.setText(CHROME_URL);
				}
			}
		});

	}

	Object[] obj;

	JLabel caption;

	XDMButton cpbtn;

	JComboBox chromeBrowsers;

	String caption2 = "Open a new tab in the browser you have selected above, and type paste this address";

	void showDialog(boolean chrome) {
		if (chrome) {
			JOptionPane.showOptionDialog(this,
					new Object[] { "\n", caption, "\n", chromeBrowsers, "\n",
							"Open a new tab in the browser you have selected above,", "\n",
							"and type paste this address", "\n", txt },
					"Browser integration", JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
					new Object[] { cpbtn }, null);
		} else {
			JOptionPane.showOptionDialog(this, new Object[] { "\n", caption, "\n", txt }, "Browser integration",
					JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[] { cpbtn }, null);
		}
	}

	JTextField txt;

	JLabel btnTablet;

	void toggleMode() {
		// boolean autostart = config.autostart;
		config.autostart = !config.autostart;
		boolean mac = System.getProperty("os.name").contains("OS X");
		if (mac) {
			if (config.autostart) {
				OSXUtil.enableAutoStart();
			} else {
				OSXUtil.disableAutoStart();
			}
		} else {
			if (config.autostart) {
				LinuxUtil.enableAutoStartLinux();
			} else {
				LinuxUtil.disableAutoStartLinux();
			}
		}
		btnTablet.setIcon(config.autostart ? StaticResource.getIcon("on.png") : StaticResource.getIcon("off.png"));
		config.save();
	}

}