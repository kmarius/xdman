package org.sdg.xdman.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.util.XDMUtil;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class NewDownloadWindow extends JFrame implements ActionListener,
		DocumentListener {

	private static final long serialVersionUID = 6595621766449726115L;

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof AbstractButton) {
			AbstractButton b = (AbstractButton) e.getSource();
			String id = b.getName();
			if (id == null)
				return;
			if (id.equals("CANCEL")) {
				setVisible(false);
				dispose();
			} else if (id.equals("DOWNLOAD_LATER")) {
				if (getURL().length() < 1) {
					JOptionPane.showMessageDialog(this, getString("URL_EMPTY"));
					return;
				}
				if (!XDMUtil.validateURL(getURL())) {
					String cu = XDMUtil.createURL(getURL());
					if (cu != null) {
						setURL(cu);
					} else {
						JOptionPane.showMessageDialog(this,
								getString("INVALID_URL"));
					}
					return;
				}
				setVisible(false);
				dispose();
				if (dwnListener != null) {
					dwnListener.add2Queue(getURL(), getFile(), getDir(),
							getUser(), getPass(), referer, cookies, userAgent,
							true);
				}
			} else if (id.equals("DOWNLOAD_NOW")) {
				if (!XDMUtil.validateURL(getURL())) {
					if (getURL().length() < 1) {
						JOptionPane.showMessageDialog(this,
								getString("URL_EMPTY"));
						return;
					}
					String cu = XDMUtil.createURL(getURL());
					if (cu != null) {
						setURL(cu);
					} else {
						JOptionPane.showMessageDialog(this,
								getString("INVALID_URL"));
					}
					return;
				}
				setVisible(false);
				dispose();
				if (dwnListener != null) {
					dwnListener.downloadNow(getURL(), getFile(), getDir(),
							getUser(), getPass(), referer, cookies, userAgent);
				}
			} else if (id.equals(" ... ")) {
				JFileChooser jfc = XDMFileChooser
						.getFileChooser(JFileChooser.DIRECTORIES_ONLY,
								new File(config.destdir));
				if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					dir = selectedFile.getAbsolutePath();
					config.destdir = dir;
				}
			} else if (id.equals("IGNORE_CHK_TXT")) {
				try {
					URL url = new URL(getURL());
					String host = url.getHost();
					if (config.siteList == null) {
						config.siteList = new String[] { host };
					}
					String newArray[] = new String[config.siteList.length + 1];
					System.arraycopy(config.siteList, 0, newArray, 0,
							config.siteList.length);
					newArray[config.siteList.length] = host;
					config.siteList = newArray;
					setVisible(false);
					dispose();
				} catch (Exception exx) {
					exx.printStackTrace();
				}
			} else if (id.equals("BTN_MORE")) {
				if (pop == null) {
					pop = new JPopupMenu();
					JMenuItem dl = new JMenuItem("Download Later");
					dl.setName("DOWNLOAD_LATER");
					dl.addActionListener(this);
					pop.add(dl);

					JMenuItem ig = new JMenuItem(
							"Don't capture downloads from this address");
					ig.setName("IGNORE_CHK_TXT");
					ig.addActionListener(this);
					pop.add(ig);
				}
				pop.setInvoker(b);
				pop.show(b, 0, b.getHeight());
			}
		}
	}

	JPopupMenu pop;

	String user, pass;

	String getUser() {
		return null;
	}

	String getPass() {
		return null;
	}

	int diffx, diffy;
	Color bgColor;

	DownloadStateListner dwnListener;
	String dir = "";
	Object interceptor;
	XDMConfig config;
	boolean cancelled = true;
	String referer;
	String userAgent;
	boolean noconfirm;
	public ArrayList<String> cookies;

	JTextField url, file;

	void setURL(String uri) {
		url.setText(uri);
	}

	String getURL() {
		return url.getText();
	}

	String getFile() {
		return file.getText();
	}

	String getDir() {
		return dir;
	}

	void setDir(String f) {
		dir = f;
	}

	DownloadList list;
	JButton dl, dn, cn, br;

	public NewDownloadWindow() {
		init();
	}

	public NewDownloadWindow(DownloadStateListner dwnListner, XDMConfig config) {
		this.dwnListener = dwnListner;
		this.config = config;
		setAlwaysOnTop(true);
		init();
		url.requestFocus();
	}

	String getFileName(String url) {
		String file = null;
		try {
			file = XDMUtil.getFileName(url);
		} catch (Exception e) {
		}
		if (file == null || file.length() < 1)
			file = "FILE";
		return file;
	}

	void update(DocumentEvent e) {
		try {
			Document doc = e.getDocument();
			int len = doc.getLength();
			String text = doc.getText(0, len);
			file.setText(getFileName(text));
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	public void changedUpdate(DocumentEvent e) {
		update(e);
	}

	public void insertUpdate(DocumentEvent e) {
		update(e);
	}

	public void removeUpdate(DocumentEvent e) {
		update(e);
	}

	String getString(String id) {
		return StringResource.getString(id);
	}

	void showDlg() {
		if (url.getText().length() < 1) {
			try {
				Object obj = Toolkit.getDefaultToolkit().getSystemClipboard()
						.getData(DataFlavor.stringFlavor);
				String txt = "";
				if (obj != null) {
					txt = obj.toString();
				}
				if (txt.length() > 0) {
					int index = txt.indexOf('\n');
					if (index != -1) {
						txt = txt.substring(0, index);
					}
					url.setText(new URL(txt).toString());
				}
			} catch (Exception e) {
			}
		}
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight()
				/ 2);
		if (noconfirm == false) {
			setVisible(true);
		} else {
			if (!XDMUtil.validateURL(getURL())) {
				if (getURL().length() < 1) {
					JOptionPane.showMessageDialog(this,
							getString("URL_EMPTY"));
					return;
				}
				String cu = XDMUtil.createURL(getURL());
				if (cu != null) {
					setURL(cu);
				} else {
					JOptionPane.showMessageDialog(this,
							getString("INVALID_URL"));
				}
				return;
			}
			// setVisible(false);
			dispose();
			if (dwnListener != null) {
				dwnListener.downloadNow(getURL(), getFile(), getDir(),
						getUser(), getPass(), referer, cookies, userAgent);
			}
		}
	}

	void init() {
		setSize(400, 210);
		bgColor = new Color(73, 73, 73);
		getContentPane().setLayout(null);
		getContentPane().setBackground(bgColor);
		setUndecorated(true);
		setResizable(false);
		JPanel titlePanel = new TitlePanel(null, this);
		titlePanel.setOpaque(false);
		titlePanel.setBounds(0, 0, 400, 50);
		JButton closeBtn = new XDMButton();
		closeBtn.setBounds(370, 5, 24, 24);
		closeBtn.setBackground(bgColor);
		// closeBtn.setContentAreaFilled(false);
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);

		closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
		//closeBtn.setRolloverIcon(StaticResource.getIcon("close_btn_r.png"));
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		titlePanel.add(closeBtn);

		JLabel titleLbl = new JLabel("NEW DOWNLOAD");
		titleLbl.setFont(StaticResource.plainFontBig2);
		titleLbl.setForeground(StaticResource.selectedColor);
		titleLbl.setBounds(25, 15, 200, 30);
		titlePanel.add(titleLbl);

		JLabel lineLbl = new JLabel();
		lineLbl.setBackground(StaticResource.selectedColor);
		lineLbl.setBounds(0, 55, 400, 1);
		lineLbl.setOpaque(true);
		add(lineLbl);

		JTextField txtURL = new JTextField();
		url = txtURL;
		txtURL.getDocument().addDocumentListener(this);
		txtURL.setBorder(new LineBorder(StaticResource.selectedColor, 1));
		txtURL.setBackground(bgColor);
		txtURL.setForeground(Color.WHITE);
		txtURL.setBounds(77, 79, 291, 20);
		txtURL.setCaretColor(StaticResource.selectedColor);

		add(txtURL);

		JTextField txtFile = new JTextField();
		file = txtFile;
		txtFile.setBorder(new LineBorder(StaticResource.selectedColor, 1));
		txtFile.setBackground(bgColor);
		txtFile.setForeground(Color.WHITE);
		txtFile.setBounds(77, 111, 241, 20);
		txtFile.setCaretColor(StaticResource.selectedColor);

		add(txtFile);

		JButton browse = new XDMButton("...");
		browse.setName(" ... ");
		browse.setMargin(new Insets(0, 0, 0, 0));
		browse.setBounds(325, 111, 40, 20);
		browse.addMouseListener(StaticResource.ma);
		browse.setFocusPainted(false);
		browse.setBackground(bgColor);
		browse.setBorder(new LineBorder(StaticResource.selectedColor, 1));
		browse.setForeground(Color.WHITE);
		browse.addActionListener(this);
		add(browse);

		add(titlePanel);

		JLabel lblURL = new JLabel("Address", JLabel.RIGHT);
		lblURL.setFont(StaticResource.plainFont);
		lblURL.setForeground(Color.WHITE);
		lblURL.setBounds(10, 78, 61, 23);
		add(lblURL);

		JLabel lblFile = new JLabel("File", JLabel.RIGHT);
		lblFile.setFont(StaticResource.plainFont);
		lblFile.setForeground(Color.WHITE);
		lblFile.setBounds(10, 108, 61, 23);
		add(lblFile);
		// add(closeBtn);

		JPanel panel = new JPanel(null);
		panel.setBounds(0, 155, 400, 55);
		panel.setBackground(Color.GRAY);
		add(panel);

		final JButton btnMore = new JButton("MORE..."), btnDN = new JButton(
				"DOWNLOAD NOW"), btnCN = new JButton("CANCEL");
		btnMore.setBounds(0, 1, 120, 55);
		btnMore.setBackground(bgColor);
		btnMore.setForeground(Color.WHITE);
		btnMore.setFont(StaticResource.plainFontBig);
		btnMore.setBorderPainted(false);
		btnMore.setMargin(new Insets(0, 0, 0, 0));
		btnMore.setFocusPainted(false);
		btnMore.addMouseListener(StaticResource.ma);
		btnMore.addActionListener(this);
		btnMore.setName("BTN_MORE");

		panel.add(btnMore);

		btnDN.setBounds(121, 1, 160, 55);
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

		btnCN.setBounds(282, 1, 120, 55);
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

	}

	public static void main(String[] args) {
		new NewDownloadWindow().setVisible(true);
	}
}
