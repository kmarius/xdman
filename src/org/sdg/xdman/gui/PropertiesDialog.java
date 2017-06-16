package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PropertiesDialog {
	static String getString(String id) {
		String str = StringResource.getString(id);
		return str == null ? "" : str;
	}

	static void showDetails(DownloadListItem item) {

		JLabel titleLbl = new JLabel(getString("PROP_TTL"));
		titleLbl.setFont(new Font(Font.DIALOG, Font.BOLD, 14));

		JTextField folderTxt = new JTextField(20);
		folderTxt.setEditable(false);
		folderTxt.setBackground(Color.WHITE);
		JTextField fileTxt = new JTextField(20);
		fileTxt.setEditable(false);
		fileTxt.setBackground(Color.WHITE);
		JTextField urlTxt = new JTextField(20);
		urlTxt.setEditable(false);
		urlTxt.setBackground(Color.WHITE);
		JTextField refereTxt = new JTextField(20);
		refereTxt.setEditable(false);
		refereTxt.setBackground(Color.WHITE);
		JTextField uaTxt = new JTextField(20);
		uaTxt.setEditable(false);
		uaTxt.setBackground(Color.WHITE);

		JTextArea cookiesTxt = new JTextArea();
		cookiesTxt.setLineWrap(true);
		cookiesTxt.setWrapStyleWord(true);
		cookiesTxt.setEditable(false);

		JScrollPane jsp = new JScrollPane(cookiesTxt);

		folderTxt.setText(item.saveto);
		fileTxt.setText(item.filename);
		urlTxt.setText(item.url);
		refereTxt.setText(item.referer);
		uaTxt.setText(item.userAgent);

		String txt = "";

		if (item.cookies != null) {
			for (int i = 0; i < item.cookies.size(); i++) {
				txt += (item.cookies.get(i) + "\n");
			}
		}

		cookiesTxt.setText(txt);

		JOptionPane.showOptionDialog(null, new Object[] { titleLbl,
				"\n" + getString("FILE_NAME"), fileTxt, getString("SAVED_IN"),
				folderTxt, getString("URL"), urlTxt, getString("REFERER"),
				refereTxt, getString("UA"), uaTxt,
				"\n" + getString("SIZE") + ": " + item.size,
				getString("TYPE") + ": " + item.type,
				getString("DATE") + ": " + item.lasttry, "\nCookies: ", jsp },
				getString("PROP_TTL"), JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);

	}
}
