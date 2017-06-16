package org.sdg.xdman.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.sdg.xdman.gui.StaticResource;

//import javax.swing.JOptionPane;

public class OSXUtil {

	public static void open(final File f) {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command("open", f.getAbsolutePath());
			pb.start();// .waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createAppBundle(File folder) {
		try {
			File dMacOS = new File(folder, "xdm.app/Contents/MacOS");
			dMacOS.mkdirs();
			File fExe = new File(dMacOS, "xdm");
			FileWriter fw = new FileWriter(fExe);
			fw.write("#!/bin/sh\n" + "\"" + new File(System.getProperty("java.home"), "bin/java").getAbsolutePath()
					+ "\"" + " -Xdock:name=XDM -jar \"" + new File(XDMUtil.getJarPath(), "xdm.jar").getAbsolutePath()
					+ "\"");
			fw.close();
			fExe.setExecutable(true);
			File fRes = new File(folder, "xdm.app/Contents/Resources");
			fRes.mkdirs();
			XDMUtil.copyStream(OSXUtil.class.getResourceAsStream("/Resources/OSX/icon.icns"),
					new FileOutputStream(new File(fRes, "icon.icns")));
			XDMUtil.copyStream(OSXUtil.class.getResourceAsStream("/Resources/OSX/Info.plist"),
					new FileOutputStream(new File(new File(folder, "xdm.app/Contents"), "Info.plist")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createFixedAppBundle(File folder) {
		try {
			File dMacOS = new File(folder, "xdm.app/Contents/MacOS");
			dMacOS.mkdirs();
			File fExe = new File(dMacOS, "xdm");
			FileWriter fw = new FileWriter(fExe);
			fw.write("#!/bin/sh\n" + "\"" + new File(System.getProperty("java.home"), "bin/java").getAbsolutePath()
					+ "\"" + " -Xdock:name=XDM -jar \""
					+ new File("/Applications/xdm.app/Contents/Resources", "xdm.jar").getAbsolutePath() + "\"");
			fw.close();
			fExe.setExecutable(true);
			System.out.println("chmod 755 \"" + fExe + "\"");
			System.out.println("chmod " + Runtime.getRuntime().exec("chmod 755 \"" + fExe + "\"").waitFor());
			File fRes = new File(folder, "xdm.app/Contents/Resources");
			fRes.mkdirs();
			XDMUtil.copyStream(OSXUtil.class.getResourceAsStream("/Resources/OSX/icon.icns"),
					new FileOutputStream(new File(fRes, "icon.icns")));
			XDMUtil.copyStream(new FileInputStream(new File(XDMUtil.getJarPath(), "xdm.jar")),
					new FileOutputStream(new File(fRes, "xdm.jar")));
			XDMUtil.copyStream(OSXUtil.class.getResourceAsStream("/Resources/OSX/Info.plist"),
					new FileOutputStream(new File(new File(folder, "xdm.app/Contents"), "Info.plist")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void disableAutoStart() {
		try {
			new File(new File(System.getProperty("user.home"), "Library/LaunchAgents/org.sdg.xdman.plist")
					.getAbsolutePath()).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void enableAutoStart() {
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(StaticResource.class.getResourceAsStream("/Resources/OSX/plist")));
			StringBuilder sb = new StringBuilder();
			while (true) {
				String ln = br.readLine();
				if (ln == null) {
					break;
				}
				if (ln.contains("$JAVA")) {
					ln = ln.replace("$JAVA", new File(System.getProperty("java.home"), "bin/java").getAbsolutePath());
				}
				if (ln.contains("$JAR")) {
					ln = ln.replace("$JAR", new File(XDMUtil.getJarPath(), "xdm.jar").getAbsolutePath());
				}
				sb.append(ln + "\n");
			}
			File startupDir = new File(System.getProperty("user.home"), "Library/LaunchAgents");
			if (!startupDir.exists()) {
				startupDir.mkdirs();
			}

			File f = new File(startupDir, "org.sdg.xdman.plist");

			FileWriter fw = new FileWriter(f);
			fw.write(sb.toString());
			fw.close();

			f.setExecutable(true);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public static boolean attachProxy() {
		File scriptFile = null;
		try {
			ProcessBuilder pb = new ProcessBuilder("osascript");
			scriptFile = new File(System.getProperty("user.home"), ".attach");
			pb.command("osascript", "-e", "do shell script \"sh \\\"" + scriptFile.getAbsolutePath()
					+ "\\\"\" with administrator privileges");

			System.out.println("do shell script \"sh \\\"" + scriptFile.getAbsolutePath()
					+ "\\\"\" with administrator privileges");

			ArrayList<String> list = new ArrayList<String>();

			String script = "#!/bin/bash\n";

			Process proc = Runtime.getRuntime().exec("networksetup -listallnetworkservices");
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			while (true) {
				String ln = br.readLine();
				if (ln == null) {
					break;
				}
				if (!ln.contains("*")) {
					list.add(ln);
				}
			}

			for (String str : list) {
				script = script + "networksetup -setautoproxyurl \"" + str + "\""
						+ " http://127.0.0.1:9614/proxy.pac\n";
			}

			FileWriter fw = new FileWriter(scriptFile);

			fw.write(script);

			fw.close();

			proc = pb.start();// Runtime.getRuntime().exec(cmd);
			// br = new BufferedReader(
			// new InputStreamReader(proc.getErrorStream()));
			// StringBuilder sb = new StringBuilder();
			// while (true) {
			// String str = br.readLine();
			// if (str == null)
			// break;
			// sb.append(str);
			// }

			return (proc.waitFor() == 0);
		} catch (Exception e) {
			// JOptionPane.showMessageDialog(null, e + "");
			e.printStackTrace();
			return false;
		} finally {
			try {
				scriptFile.delete();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public static void shutdown() {
		try {
			ProcessBuilder pb = new ProcessBuilder("osascript");
			pb.command("osascript", "-e", "tell app \"System Events\" to shut down");
			pb.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
