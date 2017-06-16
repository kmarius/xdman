package org.sdg.xdman.gui;

import java.awt.datatransfer.Transferable;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class OSXAppTransferHandler extends TransferHandler {

	private static final long serialVersionUID = -339416811193865790L;

	public void setAppFolderLocation(File f) {
		list[0] = f;
	}

	File[] list = new File[1];

	/**
	 * Create a Transferable to use as the source for a data transfer.
	 * 
	 * @param c
	 *            The component holding the data to be transfered. This argument
	 *            is provided to enable sharing of TransferHandlers by multiple
	 *            components.
	 * @return The representation of the data to be transfered.
	 * 
	 */
	protected Transferable createTransferable(JComponent c) {
		//list[0] = new File("C:\\Users\\subhro\\Desktop\\xdm\\xdm.app");
		Object[] values = new Object[list.length];
		for (int i = 0; i < list.length; i++) {
			values[i] = list[i];
		}

		StringBuffer plainBuf = new StringBuffer();
		StringBuffer htmlBuf = new StringBuffer();

		htmlBuf.append("<html>\n<body>\n<ul>\n");

		for (int i = 0; i < values.length; i++) {
			Object obj = values[i];
			String val = ((obj == null) ? "" : obj.toString());
			plainBuf.append(val + "\n");
			htmlBuf.append("  <li>" + val + "\n");
		}

		// remove the last newline
		plainBuf.deleteCharAt(plainBuf.length() - 1);
		htmlBuf.append("</ul>\n</body>\n</html>");

		return new FileTransferHandler.FileTransferable(plainBuf.toString(),
				htmlBuf.toString(), values);
	}

	public int getSourceActions(JComponent c) {
		return COPY;
	}

}
