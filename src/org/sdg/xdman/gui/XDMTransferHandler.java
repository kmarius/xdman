package org.sdg.xdman.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.interceptor.DownloadIntercepterInfo;

public class XDMTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 8226815435490071235L;

	DownloadStateListner mgr;

	public XDMTransferHandler(DownloadStateListner mgr) {
		this.mgr = mgr;
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		return true;
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		DataFlavor[] flavors = t.getTransferDataFlavors();
		if (flavors == null)
			return false;
		if (flavors.length < 1)
			return false;
		try {
			for (int i = 0; i < flavors.length; i++) {
				DataFlavor flavor = flavors[i];
				System.out.println(flavor.getMimeType());
				if (flavor.isFlavorTextType()) {
					String data = getData(t.getTransferData(flavor));
					System.out.println(data);
					try {
						new URL(data);
					} catch (Exception e) {
						continue;
					}
					if (mgr != null) {
						DownloadIntercepterInfo info = new DownloadIntercepterInfo();
						info.url = data;
						mgr.interceptDownload(info);
					}
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	String getData(Object obj) {
		try {
			if (obj instanceof Reader) {
				Reader r = (Reader) obj;
				StringBuffer data = new StringBuffer();
				while (true) {
					int x = r.read();
					if (x == -1)
						break;
					data.append((char) x);
				}
				return data.toString();
			}
			if (obj instanceof InputStream) {
				InputStream r = (InputStream) obj;
				StringBuffer data = new StringBuffer();
				while (true) {
					int x = r.read();
					if (x == -1)
						break;
					data.append((char) x);
				}
				return data.toString();
			} else if (obj instanceof String) {
				return obj.toString();
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
}
