package org.sdg.xdman.gui;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import org.sdg.xdman.core.common.*;
import org.sdg.xdman.util.FFmpegHelper;
import org.sdg.xdman.util.XDMUtil;

public class VideoDownloadDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -387780189370267005L;
	XDMConfig config;
	String file, url1, url2, referer, ua;
	ArrayList<String> cookies;
	JTextField txtFile;
	String outdir;
	DownloadStateListner dwn;

	public VideoDownloadDialog(String f, String url1, String url2, String referer, String ua, ArrayList<String> cookies,
			XDMConfig c, DownloadStateListner d) {
		// setModal(true);
		this.config = c;
		this.dwn = d;
		this.outdir = config.destdir;
		this.url1 = url1;
		this.url2 = url2;
		this.referer = referer;
		this.ua = ua;
		this.cookies = cookies;
		this.file = f;
		setUndecorated(true);
		setSize(400, 250);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		getContentPane().setBackground(StaticResource.titleColor);
		JPanel panel = new JPanel(null);
		panel.setBounds(1, 1, 398, 200);
		panel.setBackground(Color.WHITE);
		add(panel);
		setTitle("Download Video");

		JPanel titlePanel = new TitlePanel(null, this);
		titlePanel.setBackground(StaticResource.titleColor);
		titlePanel.setBounds(0, 0, 398, 50);

		JLabel titleLbl = new JLabel("DOWNLOAD VIDEO");
		titleLbl.setFont(StaticResource.plainFontBig2);
		titleLbl.setForeground(Color.WHITE);
		titleLbl.setBounds(20, 0, 398, 50);
		titlePanel.add(titleLbl);
		panel.add(titlePanel);

		JLabel lbl = new JLabel("Save As..", JLabel.LEFT);
		lbl.setBounds(20, 70, 100, 20);
		panel.add(lbl);

		txtFile = new JTextField(30);
		txtFile.setBounds(20, 95, 360, 20);
		panel.add(txtFile);
		txtFile.setText(file);

		final JButton brBtn = new JButton("Browse");
		brBtn.setBounds(280, 120, 100, 20);
		panel.add(brBtn);
		brBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				File f = new File(outdir, file);
				jfc.setSelectedFile(f);
				if (jfc.showOpenDialog(VideoDownloadDialog.this) == JFileChooser.APPROVE_OPTION) {
					File sf = jfc.getSelectedFile();
					file = sf.getName();
					outdir = sf.getParentFile().getAbsolutePath();
					txtFile.setText(file);
				}
			}
		});

		final JButton okBtn = new JButton("OK");
		okBtn.setBounds(170, 215, 100, 25);
		add(okBtn);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((XDMUtil.isNullOrEmpty(VideoDownloadDialog.this.url2)
						|| VideoDownloadDialog.this.url1.contains(".m3u8"))) {
					if (!FFmpegHelper.hasFFmpeg()) {
						JOptionPane.showMessageDialog(null,
								"FFmpeg is not installed.\nPlease install FFmpeg and create a symlink of 'ffmpeg' executable\nunder '"
										+ System.getProperty("user.home") + "' directory");
						return;
					}
				}

				setVisible(false);
				dn2();
				dispose();
			}
		});

		final JButton closeBtn = new JButton("CANCEL");
		closeBtn.setBounds(280, 215, 100, 25);
		add(closeBtn);
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		final JButton copyBtn = new JButton("COPY URL");
		copyBtn.setBounds(20, 215, 110, 25);
		add(copyBtn);
		copyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(new StringSelection(VideoDownloadDialog.this.url1), null);
			}
		});
	}

	void dn2() {
		dwn.downloadNow2(url1, url2, file, outdir, null, null, referer, cookies, ua);
	}
}
