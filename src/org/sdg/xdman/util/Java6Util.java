package org.sdg.xdman.util;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;

import javax.swing.JDialog;
import javax.swing.JTable;

public class Java6Util {
	@SuppressWarnings("unchecked")
	public static boolean window$setType(Window window, boolean val) {
		try {
			Class c = Class.forName("java.awt.Window.Type", true,
					Java6Util.class.getClassLoader());
			Enum window$Type = Enum.valueOf(c, "UTILITY");
			Method m = window.getClass().getMethod("setType", c);
			m.invoke(window, window$Type);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean jtable$setFillsViewportHeight(JTable table,
			boolean val) {
		try {
			Method m = table.getClass().getMethod("setFillsViewportHeight",
					Boolean.TYPE);
			m.invoke(table, val);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static int jtable$convertRowIndexToModel(JTable table, int index) {
		try {
			Method m = table.getClass().getMethod("convertRowIndexToModel",
					Integer.TYPE);
			return (Integer) m.invoke(table, index);
		} catch (Exception e) {
			e.printStackTrace();
			return index;
		}
	}

	public static void jdialog$setIconImage(JDialog dlg, Image image) {
		try {
			Method m = dlg.getClass().getMethod("setIconImage", Image.class);
			m.invoke(dlg, image);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void jtable$setAutoCreateRowSorter(JTable table) {
		try {
			Method m = table.getClass().getMethod("setAutoCreateRowSorter",
					Boolean.TYPE);
			m.invoke(table, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean desktop$browse(URI uri) {
		try {
			Class deskClass = Class.forName("java.awt.Desktop");
			Method m = deskClass.getMethod("getDesktop");
			Object deskObj = m.invoke(null);
			m = deskClass.getMethod("browse", URI.class);
			m.invoke(deskObj, uri);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean desktop$open(File file) {
		try {
			Class deskClass = Class.forName("java.awt.Desktop");
			Method m = deskClass.getMethod("getDesktop");
			Object deskObj = m.invoke(null);
			m = deskClass.getMethod("open", File.class);
			m.invoke(deskObj, file);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean file$setExecutable(File file) {
		try {
			Method m = file.getClass().getMethod("setExecutable", Boolean.TYPE);
			m.invoke(file, true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean trayIcon$isSupported() {
		try {
			Class trayClass = Class.forName("java.awt.SystemTray");
			Method m = trayClass.getMethod("isSupported");
			return (Boolean) m.invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static Object getSystemTray() {
		try {
			Class trayClass = Class.forName("java.awt.SystemTray");
			Method m = trayClass.getMethod("getSystemTray");
			return m.invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static Object createTrayIcon(Image image, String string) {
		try {
			Class trayIconClass = Class.forName("java.awt.TrayIcon");
			Constructor c = trayIconClass.getConstructor(Image.class,
					String.class);
			return c.newInstance(image, string);
			// Method m = trayClass.getMethod("getSystemTray");
			// return m.invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setImageAutoSize(Object trayIconObj) {
		try {
			Method m = trayIconObj.getClass().getMethod("setImageAutoSize",
					Boolean.TYPE);
			m.invoke(trayIconObj, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setPopupMenu(Object obj, Object arg) {
		try {
			Method m = obj.getClass()
					.getMethod("setPopupMenu", PopupMenu.class);
			m.invoke(obj, arg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addActionListener(Object obj, Object arg) {
		try {
			Method m = obj.getClass().getMethod("addActionListener",
					ActionListener.class);
			m.invoke(obj, arg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addMouseListener(Object obj, Object arg) {
		try {
			Method m = obj.getClass().getMethod("addMouseListener",
					MouseListener.class);
			m.invoke(obj, arg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void add(Object obj, Object arg) {
		try {
			Class trayIconClass = Class.forName("java.awt.TrayIcon");
			Method m = obj.getClass().getMethod("add", trayIconClass);
			m.invoke(obj, arg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void showMessage(Object obj, Object arg1, Object arg2,
			Object arg3) {
		try {
			Class c = Class.forName("java.awt.TrayIcon.MessageType.INFO");
			Method m = obj.getClass().getMethod("displayMessage", String.class,
					String.class, c);
			m.invoke(obj, arg1, arg2, arg3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
