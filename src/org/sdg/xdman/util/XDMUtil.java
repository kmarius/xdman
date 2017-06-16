/*
 * Copyright (c)  Subhra Das Gupta
 *
 * This file is part of Xtream Download Manager.
 *
 * Xtream Download Manager is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Xtream Download Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with Xtream Download Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.sdg.xdman.util;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import org.sdg.xdman.core.common.IXDMConstants;

public class XDMUtil implements IXDMConstants {

	public static final int OS_X = 10, LINUX = 20, WINDOWS = 30, OS_UNKNOWN = 50;

	private static String jarPath = null;

	public static String getContentName(String header) {
		try {
			String cd = header;// resposeHeaders.get("content-disposition");
			if (cd == null)
				return null;
			cd = cd.toLowerCase();
			if (cd.startsWith("attachment")) {
				String arr[] = cd.split(";");
				for (int i = 0; i < arr.length; i++) {
					String str = arr[i].trim();
					if (str.toLowerCase().startsWith("filename")) {
						int index = str.indexOf('=');
						return str.substring(index + 1).replace("\"", "").trim();
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static String trimStart(String str, String s) {
		return str.replaceAll("^[" + s + "]+", "");
	}

	public static String trimEnd(String str, String s) {
		return str.replaceAll("[" + s + "]+$", "");
	}

	public static String trim(String str, String chars) {
		return trimEnd(trimStart(str, chars), chars);
	}

	public static String getFileNameWithoutExt(String name) {
		if (XDMUtil.isNullOrEmpty(name)) {
			return name;
		}
		int index = name.lastIndexOf(".");
		if (index < 0) {
			return name;
		}
		return name.substring(0, index);
	}

	public static void copyStream(InputStream instream, OutputStream outstream, long size) throws Exception {
		byte[] b = new byte[8192];
		long rem = size;
		while (true) {
			int bs = (int) (size > 0 ? (rem > b.length ? b.length : rem) : b.length);
			int x = instream.read(b, 0, bs);
			if (x == -1) {
				if (size > 0) {
					throw new EOFException("Unexpected EOF");
				} else {
					break;
				}
			}
			outstream.write(b, 0, x);
			rem -= x;
			if (size > 0) {
				if (rem <= 0)
					break;
			}
		}
	}

	public static String getExtension(String file) {
		int index = file.lastIndexOf(".");
		if (index > 0) {
			String ext = file.substring(index).toLowerCase();
			return ext;
		} else {
			return null;
		}
	}

	public static void browse(String url) {
		try {
			if (System.getProperty("os.name").toLowerCase().contains("linux")) {
				LinuxUtil.browse(new URI(url));
			} else {
				Desktop.getDesktop().browse(new URI(url));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getJarPath() {
		if (jarPath == null) {
			try {
				String rawPath = XDMUtil.class.getResource("/res/icon.png").toString();
				rawPath = rawPath.replace("jar:", "");
				int index = rawPath.lastIndexOf("!");
				if (index > 0) {
					rawPath = rawPath.substring(0, index);
				}
				String path = new URI(rawPath).getPath();
				jarPath = new File(path).getParent();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					jarPath = new File(
							XDMUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
									.getParent();
				} catch (Exception exx) {
					e.printStackTrace();
				}
			}
		}
		return jarPath;
	}

	public static int getOS() {
		String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
		if (os.contains("OS X") || os.contains("Mac") || os.contains("Darwin") || os.contains("os X")
				|| os.contains("os x")) {
			return OS_X;
		} else if (os.contains("Linux") || os.contains("LINUX") || os.contains("linux")) {
			return LINUX;
		} else if (os.contains("WINDOWS") || os.contains("Windows") || os.contains("windows")) {
			return WINDOWS;
		} else {
			return OS_UNKNOWN;
		}
	}

	static final int MB = 1024 * 1024, KB = 1024;

	public static boolean isNullOrEmpty(String str) {
		return (str == null || str.length() < 1);
	}

	public static String getFormattedLength(double length) {
		if (length < 0)
			return "---";
		if (length > MB) {
			return String.format("%.1f MB", (float) length / MB);
		} else if (length > KB) {
			return String.format("%.1f KB", (float) length / KB);
		} else {
			return String.format("%d B", (int) length);
		}
	}

	public static String getETA(double length, float rate) {
		if (length == 0)
			return "00:00:00";
		if (length < 1 || rate <= 0)
			return "---";
		int sec = (int) (length / rate);
		return hms(sec);
	}

	public static String hms(int sec) {
		int hrs = 0, min = 0;
		hrs = sec / 3600;
		min = (sec % 3600) / 60;
		sec = sec % 60;
		String str = String.format("%02d:%02d:%02d", hrs, min, sec);
		// if (hrs > 0)
		// str += (hrs + " hour ");
		// str += (min + " min ");
		// str += (sec + " seconds ");
		return str;
	}

	public static boolean validateURL(String url) {
		try {
			new URL(url);
			if (url.startsWith("http") || url.startsWith("ftp://"))
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	static String doc[] = { ".doc", ".docx", ".txt", ".pdf", ".rtf", ".xml", ".c", ".cpp", ".java", ".cs", ".vb",
			".html", ".htm", ".chm", ".xls", ".xlsx", ".ppt", ".pptx" };
	static String cmp[] = { ".7z", ".zip", ".rar", ".gz", ".tgz", ".tbz2", ".bz2", ".lzh", ".sit", ".z" };
	static String music[] = { ".mp3", ".wma", ".ogg", ".aiff", ".au", ".mid", ".midi", ".mp2", ".mpa", ".wav", ".aac" };
	static String vid[] = { ".mpg", ".mpeg", ".avi", ".flv", ".asf", ".mov", ".mpe", ".wmv", ".mkv", ".mp4", ".3gp",
			".divx", ".vob", ".webm", ".ts" };
	static String prog[] = { ".exe", ".msi", ".bin", ".sh", ".deb", ".cab", ".cpio", ".dll", ".jar", "rpm" };

	public static String findCategory(String filename) {
		String file = filename.toLowerCase();
		for (int i = 0; i < doc.length; i++) {
			if (file.endsWith(doc[i])) {
				return DOCUMENTS;
			}
		}
		for (int i = 0; i < cmp.length; i++) {
			if (file.endsWith(cmp[i])) {
				return COMPRESSED;
			}
		}
		for (int i = 0; i < music.length; i++) {
			if (file.endsWith(music[i])) {
				return MUSIC;
			}
		}
		for (int i = 0; i < prog.length; i++) {
			if (file.endsWith(prog[i])) {
				return PROGRAMS;
			}
		}
		for (int i = 0; i < vid.length; i++) {
			if (file.endsWith(vid[i])) {
				return VIDEO;
			}
		}
		return OTHER;
	}

	public static String getFileName2(String url) {
		String file = null;
		try {
			file = new File(new URI(url).getPath()).getName();
			System.out.println("File name: " + file);
		} catch (Exception e) {
		}
		if (file == null || file.length() < 1)
			file = "FILE";
		return file;
	}

	public static void open(File f) {
		char ch = File.separatorChar;
		if (ch == '\\') {
			openWindows(f);
		} else {
			if (System.getProperty("os.name").toLowerCase().contains("linux")) {
				LinuxUtil.open(f);
			} else if (System.getProperty("os.name").toLowerCase().contains("OS X")) {
				OSXUtil.open(f);
			} else {
				Java6Util.desktop$open(f);
			}
		}
	}

	private static void openWindows(File f) {
		try {
			ProcessBuilder builder = new ProcessBuilder();
			ArrayList<String> lst = new ArrayList<String>();
			lst.add("rundll32");
			lst.add("url.dll,FileProtocolHandler");
			lst.add(f.getAbsolutePath());
			builder.command(lst);
			builder.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getFileName(String uri) {
		try {
			if (uri == null)
				return "FILE";
			if (uri.equals("/") || uri.length() < 1) {
				return "FILE";
			}
			int x = uri.lastIndexOf("/");
			String path = uri;
			if (x > -1) {
				path = uri.substring(x);
			}
			int qindex = path.indexOf("?");
			if (qindex > -1) {
				path = path.substring(0, qindex);
			}
			path = decode(path);
			if (path.length() < 1)
				return "FILE";
			if (path.equals("/"))
				return "FILE";
			return path;
		} catch (Exception e) {
			return "FILE";
		}
	}

	public static String createSafeFileName(String str) {
		String safe_name = str;
		for (int i = 0; i < invalid_chars.length; i++) {
			if (safe_name.indexOf(invalid_chars[i]) != -1) {
				safe_name = safe_name.replace(invalid_chars[i], '_');
			}
		}
		return safe_name;
	}

	static char[] invalid_chars = { '/', '\\', '"', '?', '*', '<', '>', ':', '|' };

	static String getWithoutExt(String file) {
		int index = file.lastIndexOf(".");
		if (index < 0) {
			return file;
		}
		return file.substring(0, index);
	}

	static String getExt(String file) {
		try {
			int index = file.lastIndexOf(".");
			if (index < 0) {
				return "";
			}
			return file.substring(index);
		} catch (Exception e) {
			return "";
		}
	}

	public static String getUniqueFileName(String dir, String f) {
		File target = new File(dir, f);
		while (target.exists()) {
			String name = getWithoutExt(target.getName());
			int index = name.lastIndexOf('_');
			String prefix = name;
			int count = 0;
			if (index > 0 && index < name.length() - 1) {
				try {
					count = Integer.parseInt(name.substring(index + 1));
					prefix = name.substring(0, index);
				} catch (Exception e) {

				}
			}
			count++;
			String ext = getExt(target.getName());
			f = prefix + "_" + count + ext;// "copy of " + f;
			target = new File(dir, f);
		}
		return f;
	}

	public static String decode(String str) {
		char ch[] = str.toCharArray();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < ch.length; i++) {
			if (ch[i] == '/' || ch[i] == '\\' || ch[i] == '"' || ch[i] == '?' || ch[i] == '*' || ch[i] == '<'
					|| ch[i] == '>' || ch[i] == ':')
				continue;
			if (ch[i] == '%') {
				if (i + 2 < ch.length) {
					int c = Integer.parseInt(ch[i + 1] + "" + ch[i + 2], 16);
					buf.append((char) c);
					i += 2;
					continue;
				}
			}
			buf.append(ch[i]);
		}
		return buf.toString();
	}

	public static String getType(String url) {
		try {
			int index = url.indexOf("?");
			if (index > 0) {
				String substr = url.substring(index + 1);
				String arr[] = substr.split("&");
				for (int i = 0; i < arr.length; i++) {
					if (arr[i].toLowerCase().startsWith("itag=")) {
						return arr[i].split("=")[1];
					}
				}
			}

		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static String nvl(Object o) {
		if (o == null)
			return "";
		return o.toString();
	}

	public static String createURL(String str) {
		try {
			new URL(str);
		} catch (MalformedURLException e) {
			return "http://" + str;
		}
		return null;
	}

	static long counter = 1;

	public synchronized static File getTempFile(String tmpdir) {
		String name = UUID.randomUUID().toString();
		return new File(tmpdir, name);
	}

	public static void copyStream(InputStream in, OutputStream out) throws Exception {
		byte b[] = new byte[512];
		while (true) {
			int x = in.read(b);
			if (x == -1)
				break;
			out.write(b, 0, x);
		}
		in.close();
		out.close();
	}
}
