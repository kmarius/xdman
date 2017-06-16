package org.sdg.xdman.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.URI;

public class LinuxUtil {

	public static void createDesktopFile(String target, boolean min) {
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("[Desktop Entry]\n");
			buf.append("Encoding=UTF-8\n");
			buf.append("Version=1.0\n");
			buf.append("Type=Application\n");
			buf.append("Terminal=false\n");
			String jarPath = XDMUtil.getJarPath();
			buf.append("Exec=" + "\"" + new File(System.getProperty("java.home"), "bin/java").getAbsolutePath()
					+ "\" -jar \"" + new File(jarPath, "xdman.jar").getAbsolutePath() + "\"" + (min ? " -m" : "") + "\n");
			buf.append("Name=Xtreme Download Manager\n");
			buf.append("Comment=Powerfull download accelarator and video downloader\n");
			buf.append("Categories=Network;\n");
			buf.append("Icon=" + new File(jarPath, "icon.png").getAbsolutePath() + "\n");
			File desktop = new File(target, "xdm.desktop");
			OutputStream out = new FileOutputStream(desktop);
			out.write(buf.toString().getBytes());
			out.close();
			desktop.setExecutable(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean disableAutoStartLinux() {
		String autoStartDirs[] = { ".config/autostart" };// ,
		// ".kde/Autostart",
		// ".kde/autostart", ".config/Autostart", ".kde4/Autostart" };
		File home = new File(System.getProperty("user.home"));
		File autoStartDir = null;
		for (int i = 0; i < autoStartDirs.length; i++) {
			autoStartDir = new File(home, autoStartDirs[i]);
			if (!autoStartDir.exists()) {
				autoStartDir = null;
			} else {
				// createLinuxLink(autoStartDir.getAbsolutePath());
				File file = new File(autoStartDir, "xdm.desktop");
				if (file.exists()) {
					if (file.delete()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void browse(URI uri) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("xdg-open", uri.toString());
			// String str = "xdg-open \"" + + "\"";
			// System.out.println(str);
			pb.start().waitFor();
			// Runtime.getRuntime().exec("xdg-open \"" + uri + "\"").waitFor();
		} catch (Exception e) {
			try {
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(uri);
				}
			} catch (Exception ex) {

			}
		}
	}

	public static void open(final File f) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("xdg-open", f.getAbsolutePath());
			pb.start();// .waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void enableAutoStartLinux() {
		String autoStartDir = ".config/autostart";// , ".kde/Autostart",
		// ".kde/autostart", ".config/Autostart", ".kde4/Autostart" };
		File home = new File(System.getProperty("user.home"));
		File fAutoStartDir = new File(home, autoStartDir);

		if (!fAutoStartDir.exists()) {
			fAutoStartDir.mkdirs();
		}
		createDesktopFile(fAutoStartDir.getAbsolutePath(), true);
	}

	public static boolean attachProxy() {
		return setGnome3Proxy() || setGnome2Proxy() || setGnomeProxy() || setKDEProxy();
	}

	static boolean setGnome3Proxy() {
		try {
			Process proc = Runtime.getRuntime()
					.exec("gsettings set org.gnome.system.proxy " + "autoconfig-url 'http://127.0.0.1:9614/proxy.pac'");
			proc.waitFor();
			if (proc.exitValue() != 0) {
				System.out.println("gsettings Exit code: " + proc.exitValue());
				return false;
			}
			proc = Runtime.getRuntime().exec("gsettings set org.gnome.system.proxy mode 'auto'");
			proc.waitFor();
			if (proc.exitValue() != 0) {
				System.out.println("gsettings Exit code: " + proc.exitValue());
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	static boolean setGnome2Proxy() {
		try {
			Process proc = Runtime.getRuntime().exec(
					"gconftool-2 -s /system/proxy/autoconfig_url " + "--type string 'http://127.0.0.1:9614/proxy.pac'");
			proc.waitFor();
			if (proc.exitValue() != 0) {
				System.out.println("gconftool-2 Exit code: " + proc.exitValue());
				return false;
			}
			proc = Runtime.getRuntime().exec("gconftool-2 -s /system/proxy/mode --type string 'auto'");
			proc.waitFor();
			if (proc.exitValue() != 0) {
				System.out.println("gconftool-2 Exit code: " + proc.exitValue());
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	static boolean setGnomeProxy() {
		try {
			Process proc = Runtime.getRuntime().exec(
					"gconftool -s /system/proxy/autoconfig_url " + "--type string 'http://127.0.0.1:9614/proxy.pac'");
			proc.waitFor();
			if (proc.exitValue() != 0) {
				System.out.println("gconftool Exit code: " + proc.exitValue());
				return false;
			}
			proc = Runtime.getRuntime().exec("gconftool -s /system/proxy/mode --type string 'auto'");
			proc.waitFor();
			if (proc.exitValue() != 0) {
				System.out.println("gconftool Exit code: " + proc.exitValue());
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	static boolean setKDEProxy() {
		try {
			String kde = ".kde/share/config/kioslaverc", kde4 = ".kde4/share/config/kioslaverc";
			String tmp = ".kioslaverc_tmp";
			File tmp_file = new File(System.getProperty("user.home"), tmp);
			File fkde = new File(System.getProperty("user.home"), kde);
			if (!fkde.exists()) {
				fkde = new File(System.getProperty("user.home"), kde4);
				if (!fkde.exists()) {
					return false;
				}
			}
			boolean proxySettingsFound = false, autoConfigFound = false, proxyTypeFound = false;
			BufferedReader br = new BufferedReader(new FileReader(fkde));
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmp_file));
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				bw.write(line + "\n");
				if (line.equals("[Proxy Settings]")) {
					proxySettingsFound = true;
				}
				if (line.startsWith("Proxy Config Script")) {
					autoConfigFound = true;
				}
				if (line.startsWith("ProxyType")) {
					proxyTypeFound = true;
				}
			}
			br.close();
			bw.close();
			if (!proxySettingsFound) {
				br = new BufferedReader(new FileReader(tmp_file));
				bw = new BufferedWriter(new FileWriter(fkde));
				while (true) {
					String line = br.readLine();
					if (line == null)
						break;
					bw.write(line + "\n");
				}
				bw.write("[Proxy Settings]\n" + "Proxy Config Script=http://127.0.0.1:9614/proxy.pac\n"
						+ "ProxyType=2\n");
				br.close();
				bw.close();
			} else {
				br = new BufferedReader(new FileReader(tmp_file));
				bw = new BufferedWriter(new FileWriter(fkde));
				while (true) {
					String line = br.readLine();
					if (line == null)
						break;
					if (line.equals("[Proxy Settings]")) {
						bw.write(line + "\n");
						if (!autoConfigFound) {
							bw.write("Proxy Config Script=http://127.0.0.1:9614/proxy.pac\n");
						}
						if (!proxyTypeFound) {
							bw.write("ProxyType=2\n");
						}
					} else {
						if (line.startsWith("Proxy Config Script")) {
							bw.write("Proxy Config Script=http://127.0.0.1:9614/proxy.pac\n");
						}
						if (line.startsWith("ProxyType")) {
							bw.write("ProxyType=2\n");
						} else {
							bw.write(line + "\n");
						}
					}
				}
				br.close();
				bw.close();
			}
			return true;
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			return false;
		}
	}

	public static boolean startShutdownDeamon() {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			File f = new File(XDMUtil.getJarPath(), "scripts/shutdown-deamon.sh");
			f.setExecutable(true, true);
			pb.command("gksu", f.getAbsolutePath());// f.getAbsolutePath());
			pb.start();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void stopShutdownDeamon() {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			File f = new File(XDMUtil.getJarPath(), "scripts/cancel-shutdown.sh");
			pb.command("sh", f.getAbsolutePath());
			pb.start();// .waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initShutdownDeamon() {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			File f = new File(XDMUtil.getJarPath(), "scripts/init-shutdown.sh");
			pb.command("sh", f.getAbsolutePath());
			pb.start();// .waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
