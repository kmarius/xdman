package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.sdg.xdman.core.common.Authenticator;
import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.util.XDMUtil;

public class ConfigDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -4157977457853022678L;
	JButton save, cancel, br1, br2, defaults, addAuth, removeAuth, editAuth;

	JLabel title;

	JLabel arrLbl[];

	CardLayout cardLayout;

	JPanel general, saveto, advanced, connection, proxyPanel, fileTypesPanel,
			authPanel, schedulePanel, centerPanel, rightPanel, cardPanel, pane;

	Font plainFont, boldFont;

	JCheckBox chkDwnldPrg, chkDwnldNotify, chkFinishedDlg, chkFinishedNotify, schedule, chkCustCmd, chkHaltCmd;//
	// chkHngCmd,
	// chkAvCmd;

	JComboBox cmbConn, cmbTimeout, cmbTcpW, cmbDupAction;

	JScrollPane jsp;

	JTextField txtTmpDir, txtDstDir, txtCustCmd;// , txtHltCmd, txtMdmCmd,
	// txtScnCmd;

	JTextArea txtArea, txtException;

	SpinnerDateModel start, end;

	JSpinner startDate, endDate;

	CredentialTableModel model;

	JTable table;

	XDMConfig config;

	JFileChooser folderBrowser;

	DownloadStateListner mgr;

	Frame parent;

	public ConfigDialog(Frame parent, XDMConfig config, DownloadStateListner mgr) {
		super(parent, true);
		this.parent = parent;
		this.config = config;
		this.mgr = mgr;
		init();
		setConfig();
	}

	public void showDialog() {
		this.setConfig();
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}

	private void createFolderBrowser() {
		folderBrowser = new JFileChooser();
		folderBrowser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			String cmd = ((JButton) e.getSource()).getName();
			if (cmd == null)
				return;
			if (cmd.equals("BR_TMP_DIR")) {
				if (folderBrowser == null) {
					createFolderBrowser();
				}
				folderBrowser.setCurrentDirectory(new File(config.tempdir));
				if (folderBrowser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					txtTmpDir.setText(folderBrowser.getSelectedFile()
							.getAbsolutePath());
				}

			} else if (cmd.equals("BR_DST_DIR")) {
				if (folderBrowser == null) {
					createFolderBrowser();
				}
				folderBrowser.setCurrentDirectory(new File(config.destdir));
				if (folderBrowser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					txtDstDir.setText(folderBrowser.getSelectedFile()
							.getAbsolutePath());
				}
			} else if (cmd.equals("LBL_DEFAULTS")) {
				String types = "";
				for (int i = 0; i < config.defaultFileTypes.length; i++)
					types += config.defaultFileTypes[i] + " ";
				txtArea.setText(types);
			} else if (cmd.equals("LBL_EDT_AUTH")) {
				int index = table.getSelectedRow();
				if (index < 0) {
					JOptionPane.showMessageDialog(this, "No item selected");
					return;
				}
				Credential c = getCredential(model.getValueAt(index, 0) + "",
						model.getValueAt(index, 1) + "", "");
				if (c != null)
					Authenticator.getInstance().addCreditential(c);
			} else if (cmd.equals("LBL_DEL_AUTH")) {
				int index = table.getSelectedRow();
				if (index < 0) {
					JOptionPane.showMessageDialog(this,
							getString("NONE_SELECTED"));
					return;
				}
				String host = model.getValueAt(index, 0) + "";
				Authenticator.getInstance().removeCreditential(host);
			} else if (cmd.equals("LBL_ADD_AUTH")) {
				Credential c = getCredential("", "", "");
				if (c != null)
					Authenticator.getInstance().addCreditential(c);
			} else if (cmd.equals("SAVE")) {
				saveConfig();
				setVisible(false);
			} else if (cmd.equals("CANCEL")) {
				setVisible(false);
			}
		}
	}

	Credential getCredential(String shost, String suser, String spass) {
		JTextField host = new JTextField(shost);
		JTextField user = new JTextField(suser);
		JPasswordField pass = new JPasswordField(spass);
		Object[] obj = new Object[6];
		obj[0] = getString("HOST");
		obj[1] = host;
		obj[2] = getString("USER_NAME");
		obj[3] = user;
		obj[4] = getString("PASSWORD");
		obj[5] = pass;

		while (JOptionPane.showOptionDialog(null, obj, getString("LBL_CR"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, null, null) == JOptionPane.OK_OPTION) {
			if (host.getText() == null || host.getText().length() < 1) {
				JOptionPane.showMessageDialog(null, getString("LBL_HOST"));
				continue;
			}
			if (user.getText() == null || user.getText().length() < 1) {
				JOptionPane.showMessageDialog(null, getString("LBL_USER"));
				continue;
			}

			Credential c = new Credential();
			c.host = host.getText();
			c.user = user.getText();
			c.pass = pass.getPassword().length > 0 ? new String(pass
					.getPassword()) : null;
			return c;
		}
		return null;
	}

	// private void showProxyDialog(int proxy_type) {
	// JTextField address = new JTextField();
	// JTextField user = new JTextField();
	// JPasswordField pass = new JPasswordField();
	// JCheckBox useProxy = new JCheckBox();
	//
	// String type = getString("LBL_HTTP_PROXY");
	//
	// if (proxy_type == 1) {
	// type = getString("LBL_HTTPS_PROXY");
	// useProxy.setText(type);
	// useProxy.setSelected(config.useHttpsProxy);
	// if (!(XDMUtil.isNullOrEmpty(config.httpsProxyHost))) {
	// address.setText(config.httpsProxyHost + ":" + config.httpsProxyPort);
	// user.setText(config.httpsUser);
	// pass.setText(config.httpsPass);
	// }
	// } else if (proxy_type == 2) {
	// type = getString("LBL_FTP_PROXY");
	// useProxy.setText(type);
	// useProxy.setSelected(config.useFtpProxy);
	// if (!(XDMUtil.isNullOrEmpty(config.ftpProxyHost))) {
	// address.setText(config.ftpProxyHost + ":" + config.ftpProxyPort);
	// user.setText(config.ftpUser);
	// pass.setText(config.ftpPass);
	// }
	// } else {
	// useProxy.setText(type);
	// useProxy.setSelected(config.useHttpProxy);
	// if (!(XDMUtil.isNullOrEmpty(config.httpProxyHost))) {
	// address.setText(config.httpProxyHost + ":" + config.httpProxyPort);
	// user.setText(config.httpUser);
	// pass.setText(config.httpPass);
	// }
	// }
	//
	// Object objs[] = new Object[] { useProxy, getString("LBL_PROXY_ADDR"),
	// address, getString("LBL_PROXY_USER"),
	// user, getString("LBL_PROXY_PASS"), pass };
	//
	// while (JOptionPane.showOptionDialog(this, objs, type,
	// JOptionPane.OK_CANCEL_OPTION,
	// JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.OK_OPTION)
	// {
	// String proxy_addr = address.getText();
	// if (proxy_type == 0) {
	// if (proxy_addr.length() < 1) {
	// config.useHttpProxy = false;
	// return;
	// }
	// try {
	// if (!useProxy.isSelected()) {
	// config.useHttpProxy = false;
	// return;
	// }
	// String arr[] = proxy_addr.split(":");
	// if (arr[0].length() > 0) {
	// int port = Integer.parseInt(arr[1]);
	// if (port > 0 && port < Short.MAX_VALUE * 2) {
	// config.httpProxyHost = arr[0];
	// config.httpProxyPort = port;
	// if (user.getText().length() > 0) {
	// config.httpUser = user.getText();
	// if (pass.getPassword().length > 0) {
	// config.httpPass = new String(pass.getPassword());
	// }
	// }
	// config.useHttpProxy = true;
	// return;
	// }
	// }
	// } catch (Exception e) {
	//
	// }
	// } else if (proxy_type == 1) {
	// if (proxy_addr.length() < 1) {
	// config.useHttpsProxy = false;
	// return;
	// }
	// try {
	// if (!useProxy.isSelected()) {
	// config.useHttpProxy = false;
	// return;
	// }
	// String arr[] = proxy_addr.split(":");
	// if (arr[0].length() > 0) {
	// int port = Integer.parseInt(arr[1]);
	// if (port > 0 && port < Short.MAX_VALUE * 2) {
	// config.httpsProxyHost = arr[0];
	// config.httpsProxyPort = port;
	// if (user.getText().length() > 0) {
	// config.httpsUser = user.getText();
	// if (pass.getPassword().length > 0) {
	// config.httpsPass = new String(pass.getPassword());
	// }
	// }
	// config.useHttpsProxy = true;
	// return;
	// }
	// }
	// } catch (Exception e) {
	//
	// }
	// } else if (proxy_type == 2) {
	// if (proxy_addr.length() < 1) {
	// config.useFtpProxy = false;
	// return;
	// }
	// try {
	// if (!useProxy.isSelected()) {
	// config.useHttpProxy = false;
	// return;
	// }
	// String arr[] = proxy_addr.split(":");
	// if (arr[0].length() > 0) {
	// int port = Integer.parseInt(arr[1]);
	// if (port > 0 && port < Short.MAX_VALUE * 2) {
	// config.ftpProxyHost = arr[0];
	// config.ftpProxyPort = port;
	// if (user.getText().length() > 0) {
	// config.ftpUser = user.getText();
	// if (pass.getPassword().length > 0) {
	// config.ftpPass = new String(pass.getPassword());
	// }
	// }
	// config.useFtpProxy = true;
	// return;
	// }
	// }
	// } catch (Exception e) {
	//
	// }
	// }
	// JOptionPane.showMessageDialog(this, getString("LBL_PROXY_INVALID"));
	// }
	// }

	private void saveConfig() {
		config.showDownloadPrgDlg = chkDwnldPrg.isSelected();
		config.showDownloadPrgNotifySend = chkDwnldNotify.isSelected();
		config.showDownloadCompleteDlg = chkFinishedDlg.isSelected();
		config.showDownloadCompleteNotifySend = chkFinishedNotify.isSelected();
		// config.allowbrowser = chkAllowBrowser.isSelected();
		config.duplicateLinkAction = cmbDupAction.getSelectedIndex();

		config.maxConn = Integer.parseInt(cmbConn.getSelectedItem() + "");
		config.timeout = Integer.parseInt(cmbTimeout.getSelectedItem() + "");
		config.tcpBuf = Integer.parseInt(cmbTcpW.getSelectedItem() + "");

		config.tempdir = txtTmpDir.getText();
		config.destdir = txtDstDir.getText();

		config.executeCmd = chkCustCmd.isSelected();
		config.cmdTxt = txtCustCmd.getText();
		config.halt = chkHaltCmd.isSelected();
		// //config.haltTxt = txtHltCmd.getText();
		// config.hungUpTxt = txtMdmCmd.getText();
		// config.hungUp = chkHngCmd.isSelected();
		// config.antivirTxt = txtScnCmd.getText();

		ArrayList<String> lst = new ArrayList<String>();
		String arr[] = txtArea.getText().replaceAll("\n", " ").split(" ");
		for (int i = 0; i < arr.length; i++) {
			String t = arr[i].trim();
			if (t.length() > 0)
				lst.add(t);
		}
		config.fileTypes = new String[lst.size()];
		for (int i = 0; i < lst.size(); i++) {
			config.fileTypes[i] = lst.get(i);
		}

		arr = txtException.getText().split("\n");

		config.siteList = arr;

		config.schedule = schedule.isSelected();
		config.startDate = start.getDate();
		config.endDate = end.getDate();

		config.useProxyPAC = usePAC.isSelected();
		config.proxyPAC = txtPAC.getText();

		config.useProxy = useProxy.isSelected();
		if (XDMUtil.isNullOrEmpty(txtProxy.getText())) {
			config.useProxy = false;
			config.proxyHost = null;
		} else {
			String[] array = txtProxy.getText().split(":");
			try {
				if (array.length < 2) {
					config.proxyHost = txtProxy.getText();
					config.proxyPort = 80;
				} else {
					config.proxyHost = array[0];
					config.proxyPort = Integer.parseInt(array[1]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		config.proxyUser = txtProxyUser.getText();
		config.proxyPass = txtProxyPass.getText();

		config.save();

		if (mgr != null) {
			mgr.configChanged();
		}
	}

	private void setConfig() {
		chkDwnldPrg.setSelected(config.showDownloadPrgDlg);
		chkDwnldNotify.setSelected(config.showDownloadPrgNotifySend);
		chkFinishedDlg.setSelected(config.showDownloadCompleteDlg);
		chkFinishedNotify.setSelected(config.showDownloadCompleteNotifySend);
		// chkAllowBrowser.setSelected(config.allowbrowser);
		cmbDupAction.setSelectedIndex(config.duplicateLinkAction);

		cmbConn.setSelectedItem(config.maxConn + "");
		cmbTimeout.setSelectedItem(config.timeout + "");
		cmbTcpW.setSelectedItem(config.tcpBuf + "");

		txtTmpDir.setText(config.tempdir);
		txtDstDir.setText(config.destdir);

		chkCustCmd.setSelected(config.executeCmd);
		txtCustCmd.setText(config.cmdTxt);
		chkHaltCmd.setSelected(config.halt);
		// txtHltCmd.setText(config.haltTxt);
		// txtMdmCmd.setText(config.hungUpTxt);
		// chkHngCmd.setSelected(config.hungUp);
		// txtScnCmd.setText(config.antivirTxt);

		String arr[] = config.fileTypes;
		String types = "";
		for (int i = 0; i < arr.length; i++) {
			types += arr[i] + " ";
		}
		txtArea.setText(types);

		arr = config.siteList;
		String sites = "";
		for (int i = 0; i < arr.length; i++) {
			sites += arr[i] + "\n";
		}
		txtException.setText(sites);

		schedule.setSelected(config.schedule);

		usePAC.setSelected(config.useProxyPAC);
		System.out.println(config.proxyPAC);
		txtPAC.setText(config.proxyPAC);

		useProxy.setSelected(config.useProxy);
		if (!XDMUtil.isNullOrEmpty(config.proxyHost)) {
			txtProxy.setText(config.proxyHost + ":" + config.proxyPort);
		}

		if (config.startDate != null) {
			start.setValue(config.startDate);
			end.setValue(config.endDate);
		}

		txtProxyUser.setText(config.proxyUser);
		txtProxyPass.setText(config.proxyPass);
		model.load();

	}

	private void init() {
		setTitle(getString("CONFIG_TITLE"));
		setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
		setUndecorated(true);
		setSize(500, 400);
		plainFont = new Font(Font.DIALOG, Font.PLAIN, 12);
		boldFont = new Font(Font.DIALOG, Font.BOLD | Font.ITALIC, 12);

		clickHandler = new ConfigMouseAdapter();
		centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBorder(new LineBorder(Color.BLACK));
		add(centerPanel);
		createLeftPanel();
		createRightPanel();

		cardLayout = new CardLayout();

		cardPanel = new JPanel(cardLayout);
		cardPanel.setBackground(Color.white);

		pane.add(cardPanel);// createGeneralPanel());

		cardPanel.add(createGeneralPanel(), getString("CONFIG_LBL1"));

		cardPanel.add(createConnectionPanel(), getString("CONFIG_LBL2"));

		cardPanel.add(createSaveToPanel(), getString("CONFIG_LBL3"));

		cardPanel.add(createAdvancedPanel(), getString("CONFIG_LBL4"));

		cardPanel.add(createProxyPanel(), getString("CONFIG_LBL5"));

		cardPanel.add(createTypesPanel(), getString("CONFIG_LBL6"));

		cardPanel.add(createCredentialPanel(), getString("CONFIG_LBL7"));

		cardPanel.add(createSchedulerPanel(), getString("CONFIG_LBL8"));

		cardPanel.add(createExceptionsPanel(), getString("CONFIG_LBL9"));

		showPanel(getString("CONFIG_LBL1"));
	}

	void showPanel(String name) {
		title.setText(name);
		cardLayout.show(cardPanel, name);
	}

	JPanel createCredentialPanel() {
		JPanel box = new JPanel(new BorderLayout(5, 5));
		box.setOpaque(false);
		box.setBorder(new EmptyBorder(10, 0, 0, 10));

		model = new CredentialTableModel();
		Authenticator.getInstance().addObserver(model);
		table = new JTable(model);

		if (System.getProperty("xdm.defaulttheme") != null) {
			table.getTableHeader().setDefaultRenderer(
					new XDMTableHeaderRenderer());
		}
		table.setFillsViewportHeight(true);

		JScrollPane jsp = new JScrollPane(table);
		// jsp.setOpaque(false);
		// jsp.getViewport().setOpaque(false);

		jsp.setPreferredSize(new Dimension(10, 10));
		// box.add(table);
		box.add(jsp);

		Box b = Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());

		addAuth = new JButton(getString("LBL_ADD_AUTH"));
		addAuth.setName("LBL_ADD_AUTH");
		addAuth.addActionListener(this);
		removeAuth = new JButton(getString("LBL_DEL_AUTH"));
		removeAuth.setName("LBL_DEL_AUTH");
		removeAuth.addActionListener(this);
		editAuth = new JButton(getString("LBL_EDT_AUTH"));
		editAuth.setName("LBL_EDT_AUTH");
		editAuth.addActionListener(this);

		addAuth.setPreferredSize(removeAuth.getPreferredSize());
		editAuth.setPreferredSize(removeAuth.getPreferredSize());

		b.add(addAuth);
		b.add(Box.createHorizontalStrut(10));
		b.add(removeAuth);
		b.add(Box.createHorizontalStrut(10));
		b.add(editAuth);
		box.add(b, BorderLayout.SOUTH);
		return box;
	}

	Box createExceptionsPanel() {
		Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.setBorder(new EmptyBorder(10, 0, 0, 10));

		Box b0 = Box.createHorizontalBox();
		b0.add(new JLabel(getString("LBL_EXCEPT")));
		b0.setBorder(new EmptyBorder(0, 0, 10, 0));
		b0.add(Box.createHorizontalGlue());
		box.add(b0);

		txtException = new JTextArea();
		txtException.setLineWrap(false);
		txtException.setWrapStyleWord(true);

		JScrollPane jsp = new JScrollPane(txtException);
		jsp.setPreferredSize(new Dimension(10, 10));

		box.add(jsp);

		Box b = Box.createHorizontalBox();
		b.add(new JLabel(getString("LBL_EXCEPT_LN")));
		b.add(Box.createHorizontalGlue());
		b.setBorder(new EmptyBorder(5, 0, 5, 0));
		box.add(b);

		return box;
	}

	Box createSchedulerPanel() {
		Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.setBorder(new EmptyBorder(10, 0, 0, 10));

		Box b0 = Box.createHorizontalBox();
		schedule = new JCheckBox(getString("LBL_Q"));
		schedule.setContentAreaFilled(false);
		schedule.setFocusPainted(false);
		// schedule.addActionListener(this);
		b0.add(schedule);
		b0.setBorder(new EmptyBorder(0, 0, 10, 0));
		b0.add(Box.createHorizontalGlue());
		box.add(b0);

		start = new SpinnerDateModel();
		end = new SpinnerDateModel();

		startDate = new JSpinner(start);
		startDate.setEditor(new JSpinner.DateEditor(startDate,
				"dd-MMM-yy hh:mm a"));
		startDate.setMaximumSize(startDate.getPreferredSize());

		endDate = new JSpinner(end);
		endDate
				.setEditor(new JSpinner.DateEditor(endDate, "dd-MMM-yy hh:mm a"));
		endDate.setMaximumSize(endDate.getPreferredSize());

		Box b1 = Box.createHorizontalBox();
		b1.add(new JLabel(getString("LBL_START_Q")));
		b1.add(Box.createHorizontalGlue());
		b1.add(startDate);

		box.add(b1);
		box.add(Box.createRigidArea(new Dimension(10, 10)));

		Box b2 = Box.createHorizontalBox();
		b2.add(new JLabel(getString("LBL_STOP_Q")));
		b2.add(Box.createHorizontalGlue());
		b2.add(endDate);

		box.add(b2);

		return box;
	}

	Box createTypesPanel() {
		Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.setBorder(new EmptyBorder(10, 0, 0, 10));

		Box b0 = Box.createHorizontalBox();
		b0.add(new JLabel(getString("LBL_FILE_TYPES")));
		b0.setBorder(new EmptyBorder(0, 0, 10, 0));
		b0.add(Box.createHorizontalGlue());
		box.add(b0);

		txtArea = new JTextArea();
		txtArea.setWrapStyleWord(true);
		txtArea.setLineWrap(true);

		JScrollPane jsp = new JScrollPane(txtArea);
		jsp.setPreferredSize(new Dimension(10, 10));

		box.add(jsp);

		Box b = Box.createHorizontalBox();
		b.add(Box.createHorizontalGlue());

		defaults = new JButton(getString("LBL_DEFAULTS"));
		defaults.addActionListener(this);
		defaults.setName("LBL_DEFAULTS");

		b.add(defaults);
		b.setBorder(new EmptyBorder(5, 0, 5, 0));
		box.add(b);

		return box;
	}

	JCheckBox usePAC, useProxy;
	JTextField txtPAC, txtProxy, txtProxyUser, txtProxyPass;

	JPanel createProxyPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(null);

		usePAC = new JCheckBox("Use Proxy Autoconfig Script");
		usePAC.setBounds(10, 10, 200, 20);
		txtPAC = new JTextField(30);
		txtPAC.setBounds(10, 40, 340, 20);
		panel.add(usePAC);
		panel.add(txtPAC);

		useProxy = new JCheckBox("Use Proxy Server");
		useProxy.setBounds(10, 90, 200, 20);
		txtProxy = new JTextField(30);
		txtProxy.setBounds(10, 120, 340, 20);
		panel.add(useProxy);
		panel.add(txtProxy);

		JLabel lbl1 = new JLabel("User Name");
		lbl1.setBounds(10, 180, 100, 20);
		txtProxyUser = new JTextField(30);
		txtProxyUser.setBounds(110, 180, 100, 20);
		panel.add(lbl1);
		panel.add(txtProxyUser);

		JLabel lbl2 = new JLabel("Password");
		lbl2.setBounds(10, 210, 100, 20);
		txtProxyPass = new JPasswordField(30);
		txtProxyPass.setBounds(110, 210, 100, 20);
		panel.add(lbl2);
		panel.add(txtProxyPass);

		// Box box = Box.createVerticalBox();
		// box.setOpaque(false);
		// box.setBorder(new EmptyBorder(10, 0, 0, 10));
		//
		// JPanel p = new JPanel(new GridLayout(4, 1, 5, 5));
		// p.setOpaque(false);
		//
		// Box b = Box.createVerticalBox();
		// usePAC = new JCheckBox("Use Proxy Autoconfig Script");
		// txtPAC = new JTextField(30);
		// b.add(usePAC);
		// b.add(txtPAC);
		// p.add(b);
		//
		// JButton http = new JButton(getString("HTTP_PROXY"));
		// http.setName("HTTP_PROXY");
		// http.addActionListener(this);
		// p.add(http);
		//
		// JButton https = new JButton(getString("HTTPS_PROXY"));
		// https.setName("HTTPS_PROXY");
		// https.addActionListener(this);
		// p.add(https);
		//
		// JButton ftp = new JButton(getString("FTP_PROXY"));
		// ftp.setName("FTP_PROXY");
		// ftp.addActionListener(this);
		// p.add(ftp);
		//
		// box.add(p);
		// box.add(Box.createVerticalGlue());

		return panel;

	}

	Box createAdvancedPanel() {
		Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.setBorder(new EmptyBorder(10, 0, 0, 10));

		Box b0 = Box.createHorizontalBox();
		b0.add(new JLabel(getString("DWN_CMD")));
		b0.setBorder(new EmptyBorder(0, 0, 10, 0));
		b0.add(Box.createHorizontalGlue());
		box.add(b0);

		Box b = Box.createHorizontalBox();
		chkCustCmd = new JCheckBox(getString("CST_CMD"));
		chkCustCmd.setFocusPainted(false);
		chkCustCmd.setContentAreaFilled(false);
		b.add(chkCustCmd);
		b.add(Box.createHorizontalGlue());
		box.add(b);

		Box box1 = Box.createHorizontalBox();
		box1.setBorder(new EmptyBorder(5, 0, 5, 0));

		txtCustCmd = new JTextField(15);
		txtCustCmd.setMaximumSize(new Dimension(
				txtCustCmd.getMaximumSize().width, txtCustCmd
						.getPreferredSize().height));
		box1.add(txtCustCmd);

		box.add(box1);
		box.add(Box.createVerticalStrut(10));

		Box b2 = Box.createHorizontalBox();
		chkHaltCmd = new JCheckBox(
				"Shutdown computer after download is complete");
		chkHaltCmd.setFocusPainted(false);
		chkHaltCmd.setContentAreaFilled(false);
		b2.add(chkHaltCmd);
		b2.add(Box.createHorizontalGlue());
		if (System.getProperty("os.name").contains("OS X")) {
			box.add(b2);
		}

		box.add(Box.createVerticalGlue());

		// Box b2 = Box.createHorizontalBox();
		// chkHltCmd = new JCheckBox(getString("CMD_HLT"));
		// chkHltCmd.setFocusPainted(false);
		// chkHltCmd.setContentAreaFilled(false);
		//
		// b2.add(Box.createHorizontalGlue());
		// box.add(b2);
		//
		// Box box2 = Box.createHorizontalBox();
		// box2.setBorder(new EmptyBorder(5, 0, 5, 0));
		//
		// txtHltCmd = new JTextField(15);
		// txtHltCmd.setMaximumSize(new Dimension(
		// txtHltCmd.getMaximumSize().width,
		// txtHltCmd.getPreferredSize().height));
		// //box2.add(txtHltCmd);
		//
		// box.add(box2);
		//
		// Box b3 = Box.createHorizontalBox();
		// chkHngCmd = new JCheckBox(getString("CMD_HNG"));
		// chkHngCmd.setFocusPainted(false);
		// chkHngCmd.setContentAreaFilled(false);
		// b3.add(chkHngCmd);
		// b3.add(Box.createHorizontalGlue());
		// box.add(b3);
		//
		// Box box3 = Box.createHorizontalBox();
		// box3.setBorder(new EmptyBorder(5, 0, 5, 0));
		//
		// txtMdmCmd = new JTextField(15);
		// txtMdmCmd.setMaximumSize(new Dimension(
		// txtMdmCmd.getMaximumSize().width,
		// txtMdmCmd.getPreferredSize().height));
		// box3.add(txtMdmCmd);
		//
		// box.add(box3);
		//
		// Box b4 = Box.createHorizontalBox();
		// chkAvCmd = new JCheckBox(getString("CMD_SCN"));
		// chkAvCmd.setFocusPainted(false);
		// chkAvCmd.setContentAreaFilled(false);
		// b4.add(chkAvCmd);
		// b4.add(Box.createHorizontalGlue());
		// box.add(b4);
		//
		// Box box4 = Box.createHorizontalBox();
		// box4.setBorder(new EmptyBorder(5, 0, 5, 0));
		//
		// txtScnCmd = new JTextField(15);
		// txtScnCmd.setMaximumSize(new Dimension(
		// txtScnCmd.getMaximumSize().width,
		// txtScnCmd.getPreferredSize().height));
		// box4.add(txtScnCmd);
		//
		// box.add(box4);
		//
		// b2.add(chkHltCmd);
		//
		//
		// box.add(Box.createVerticalGlue());
		//
		return box;

	}

	Box createSaveToPanel() {
		Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.setBorder(new EmptyBorder(10, 0, 0, 10));

		Box b = Box.createHorizontalBox();
		b.add(new JLabel(getString("TMP_DIR")));
		b.add(Box.createHorizontalGlue());
		box.add(b);

		Box box1 = Box.createHorizontalBox();
		box1.setBorder(new EmptyBorder(5, 0, 5, 0));

		txtTmpDir = new JTextField(15);
		txtTmpDir.setMaximumSize(new Dimension(
				txtTmpDir.getMaximumSize().width,
				txtTmpDir.getPreferredSize().height));
		txtTmpDir.setEditable(false);
		txtTmpDir.setBackground(Color.white);

		box1.add(txtTmpDir);

		box1.add(Box.createRigidArea(new Dimension(10, 10)));

		br1 = new JButton("...");
		br1.addActionListener(this);
		br1.setName("BR_TMP_DIR");
		box1.add(br1);

		box.add(box1);

		Box b2 = Box.createHorizontalBox();
		b2.add(new JLabel(getString("DST_DIR")));
		b2.add(Box.createHorizontalGlue());
		box.add(b2);

		Box box2 = Box.createHorizontalBox();
		box2.setBorder(new EmptyBorder(5, 0, 5, 0));

		txtDstDir = new JTextField(15);
		txtDstDir.setMaximumSize(new Dimension(
				txtDstDir.getMaximumSize().width,
				txtDstDir.getPreferredSize().height));
		txtDstDir.setEditable(false);

		box2.add(txtDstDir);

		box2.add(Box.createRigidArea(new Dimension(10, 10)));

		br2 = new JButton("...");
		br2.addActionListener(this);
		br2.setName("BR_DST_DIR");
		box2.add(br2);

		txtDstDir.setBackground(Color.white);

		box.add(box2);

		box.add(Box.createVerticalGlue());

		return box;
	}

	Box createConnectionPanel() {
		Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.setBorder(new EmptyBorder(10, 0, 0, 10));

		Box box2 = Box.createHorizontalBox();
		box2.add(new JLabel(getString("DWN_TYM")));
		box2.add(Box.createHorizontalGlue());
		cmbTimeout = new JComboBox(new String[] { "10", "15", "20", "30", "45",
				"60", "120", "180", "240", "300" });
		cmbTimeout.setMaximumSize(cmbTimeout.getPreferredSize());
		box2.add(cmbTimeout);
		box.add(box2);
		box2.setBorder(new EmptyBorder(2, 0, 2, 0));

		Box box1 = Box.createHorizontalBox();
		box1.add(new JLabel(getString("DWN_SEG")));
		box1.add(Box.createHorizontalGlue());
		cmbConn = new JComboBox(new String[] { "1", "2", "3", "4", "5", "6",
				"7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "20",
				"24", "26", "28", "30", "32" });
		cmbConn.setMaximumSize(cmbTimeout.getPreferredSize());
		cmbConn.setPreferredSize(cmbTimeout.getPreferredSize());
		box1.add(cmbConn);
		box.add(box1);
		box1.setBorder(new EmptyBorder(2, 0, 2, 0));

		Box box3 = Box.createHorizontalBox();
		box3.add(new JLabel(getString("DWN_TCP")));
		box3.add(Box.createHorizontalGlue());
		cmbTcpW = new JComboBox(new String[] { "8", "16", "32", "64" });
		cmbTcpW.setMaximumSize(cmbTimeout.getPreferredSize());
		cmbTcpW.setPreferredSize(cmbTimeout.getPreferredSize());
		box3.add(cmbTcpW);
		box.add(box3);
		box3.setBorder(new EmptyBorder(2, 0, 2, 0));

		box.add(Box.createVerticalGlue());
		return box;
	}

	Box createGeneralPanel() {
		chkDwnldPrg = new JCheckBox(getString("SHOW_DWNLD_PRG"));
		chkDwnldPrg.setContentAreaFilled(false);
		chkDwnldPrg.setFocusPainted(false);
		chkDwnldNotify = new JCheckBox(getString("SHOW_DWNLD_PRG_NOTIFY"));
		chkDwnldPrg.setContentAreaFilled(false);
		chkDwnldPrg.setFocusPainted(false);
		chkFinishedDlg = new JCheckBox(getString("SHOW_DWNLD_DLG"));
		chkFinishedDlg.setContentAreaFilled(false);
		chkFinishedDlg.setFocusPainted(false);
		chkFinishedNotify = new JCheckBox(getString("SHOW_DWNLD_NOTIFY"));
		chkFinishedNotify.setContentAreaFilled(false);
		chkFinishedNotify.setFocusPainted(false);
		// chkAllowBrowser = new JCheckBox(getString("ALLOW_BROWSER"));
		// chkAllowBrowser.setContentAreaFilled(false);
		// chkAllowBrowser.setFocusPainted(false);
		cmbDupAction = new JComboBox(new String[] {
				StringResource.getString("DUP__OP1"),
				StringResource.getString("DUP__OP2"),
				StringResource.getString("DUP__OP3"),
				StringResource.getString("DUP__OP4") });
		cmbDupAction.setBorder(null);
		cmbDupAction.setMaximumSize(new Dimension(chkDwnldPrg
				.getPreferredSize().width,
				cmbDupAction.getPreferredSize().height));

		Box box = Box.createVerticalBox();
		box.setOpaque(false);
		box.setBorder(new EmptyBorder(10, 0, 0, 10));

		Box b0 = Box.createHorizontalBox();
		b0.add(chkDwnldPrg);
		b0.add(Box.createHorizontalGlue());
		box.add(b0);
		Box b1 = Box.createHorizontalBox();
		b1.add(chkFinishedDlg);
		b1.add(Box.createHorizontalGlue());
		box.add(b1);
		
		Box b31 = Box.createHorizontalBox();
		b31.add(chkDwnldNotify);
		b31.add(Box.createHorizontalGlue());
		box.add(b31);

		Box b3 = Box.createHorizontalBox();
		b3.add(chkFinishedNotify);
		b3.add(Box.createHorizontalGlue());
		box.add(b3);
		// Box b2 = Box.createHorizontalBox();
		// b2.add(chkAllowBrowser);
		// b2.add(Box.createHorizontalGlue());
		// box.add(b2);

		Box b4 = Box.createHorizontalBox();
		b4.add(new JLabel(getString("SHOW_DUP_ACTION")));
		b4.add(Box.createHorizontalGlue());
		b4.add(cmbDupAction);
		box.add(Box.createVerticalStrut(10));
		box.add(b4);

		return box;
	}

	void createRightPanel() {
		rightPanel = new JPanel(new BorderLayout());
		rightPanel.setBackground(Color.white);

		TitlePanel tp = new TitlePanel(new BorderLayout(), this);
		tp.setBackground(Color.WHITE);

		title = new JLabel();
		title.setBorder(new EmptyBorder(20, 20, 20, 20));
		title.setFont(title.getFont().deriveFont(Font.BOLD,
				title.getFont().getSize() * 1.2f));
		tp.add(title, BorderLayout.CENTER);

		rightPanel.add(tp, BorderLayout.NORTH);

		pane = new JPanel(new BorderLayout());
		pane.setBackground(Color.white);

		jsp = new JScrollPane(pane);
		jsp.setBackground(Color.white);
		jsp.setBorder(new EmptyBorder(10, 10, 10, 0));

		rightPanel.add(jsp);

		save = new JButton(getString("SAVE"));
		save.setName("SAVE");
		save.addActionListener(this);
		cancel = new JButton(getString("CANCEL"));
		cancel.setName("CANCEL");
		cancel.addActionListener(this);
		save.setPreferredSize(cancel.getPreferredSize());
		Box downBox = Box.createHorizontalBox();
		downBox.add(Box.createHorizontalGlue());
		downBox.add(save);
		downBox.add(Box.createRigidArea(new Dimension(5, 5)));
		downBox.add(cancel);
		downBox.setBorder(new EmptyBorder(10, 10, 10, 10));
		rightPanel.add(downBox, BorderLayout.SOUTH);

		centerPanel.add(rightPanel);
	}

	void createLeftPanel() {
		Box leftBox = Box.createVerticalBox();
		leftBox.setOpaque(true);
		leftBox.setBackground(StaticResource.titleColor);

		JLabel title = new JLabel(getString("CONFIG_TITLE"));
		title.setForeground(Color.white);
		title.setFont(title.getFont().deriveFont(Font.BOLD,
				title.getFont().getSize() * 1.2f));
		title.setBorder(new EmptyBorder(20, 20, 20, 40));

		leftBox.add(title);

		centerPanel.add(leftBox, BorderLayout.WEST);

		arrLbl = new JLabel[9];

		for (int i = 0; i < 9; i++) {
			String id = "CONFIG_LBL" + (i + 1);
			arrLbl[i] = new JLabel(getString(id));
			arrLbl[i].setName(id);
			arrLbl[i].addMouseListener(clickHandler);
			arrLbl[i].setForeground(Color.white);
			arrLbl[i].setFont(plainFont);
			arrLbl[i].setBorder(new EmptyBorder(5, 20, 5, 20));
			leftBox.add(arrLbl[i]);
		}
	}

	private String getString(String id) {
		return StringResource.getString(id);
	}

	ConfigMouseAdapter clickHandler;

	class ConfigMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getSource() instanceof JLabel) {
				JLabel src = (JLabel) e.getSource();
				String id = src.getName();
				showPanel(getString(id));
				for (int i = 0; i < arrLbl.length; i++) {
					if (arrLbl[i] == src) {
						src.setFont(boldFont);
					} else {
						arrLbl[i].setFont(plainFont);
					}
				}
			}
		}
	}
}
