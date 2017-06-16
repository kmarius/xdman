package org.sdg.xdman.util;

import java.io.*;
import java.util.*;

public class FFmpegHelper {

	public static boolean hasFFmpeg() {
		boolean ffmpeg = new File(System.getProperty("user.home"), "ffmpeg").exists();
		if (!ffmpeg) {
			String jarfile = XDMUtil.getJarPath();
			if (jarfile != null) {
				ffmpeg = new File(jarfile, "ffmpeg").exists();
			}
		}
		return ffmpeg;
	}

	public static String getFFmpegPath() {
		String jarfile = XDMUtil.getJarPath();
		if (jarfile == null) {
			return new File(System.getProperty("user.home"), "ffmpeg").getAbsolutePath();
		} else {
			File ffFile = new File(jarfile, "ffmpeg");
			if (!ffFile.exists()) {
				return new File(System.getProperty("user.home"), "ffmpeg").getAbsolutePath();
			}
			return ffFile.getAbsolutePath();
		}
	}

	public static boolean combineHLS(ArrayList<String> list, String target_file, String tempdir) {
		try {
			File tmpFile = new File(System.getProperty("user.home"), ".xdm");
			File lstFile = new File(tempdir, "ffmpeg_" + System.currentTimeMillis() + ".txt");
			FileOutputStream fs = new FileOutputStream(lstFile);
			for (int i = 0; i < list.size(); i++) {
				fs.write(("file " + list.get(i) + "\r\n").getBytes());
			}
			fs.close();
			String ffmpeg = getFFmpegPath();
			String script = ffmpeg + " -f concat -safe 0 -i \"" + lstFile.getAbsolutePath()
					+ "\" -acodec copy -vcodec copy \"" + target_file + "\" -y  -loglevel quiet >/dev/null 2>&1\n";
			File ffScript = new File(tmpFile, "ffmpeg_" + System.currentTimeMillis() + ".sh");
			fs = new FileOutputStream(ffScript);
			fs.write(script.getBytes());
			fs.close();

			ProcessBuilder pb = new ProcessBuilder("sh", ffScript.getAbsolutePath());
			int r = pb.start().waitFor();
			// ffScript.delete();
			// lstFile.delete();
			System.out.println("ffmpeg exit status: " + r);
			return r == 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean combineDASH(String file1, String file2, String target_file) {
		try {
			String ffmpeg = getFFmpegPath();
			String script = ffmpeg + " -i \"" + file1 + "\" -i \"" + file2 + "\" -acodec copy -vcodec copy \""
					+ target_file + "\" -y  -loglevel quiet >/dev/null 2>&1\n";
			File tmpFile = new File(System.getProperty("user.home"), ".xdm");
			File ffScript = new File(tmpFile, "ffmpeg_" + System.currentTimeMillis() + ".sh");
			FileOutputStream fs = new FileOutputStream(ffScript);
			fs.write(script.getBytes());
			fs.close();

			ProcessBuilder pb = new ProcessBuilder("sh", ffScript.getAbsolutePath());
			int r = pb.start().waitFor();
			ffScript.delete();
			System.out.println("ffmpeg exit status: " + r);
			return r == 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
