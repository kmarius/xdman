package org.sdg.xdman.core.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.sdg.xdman.util.XDMUtil;

public class Assembler {
	public static boolean stop = false;

	private static void assemble(OutputStream out, ArrayList<ChunkFileInfo> fileList) throws Exception {
		InputStream in = null;
		for (int i = 0; i < fileList.size(); i++) {
			ChunkFileInfo info = fileList.get(i);
			System.out.println("Reading..." + info.file);
			in = new FileInputStream(info.file);
			long rem = info.len;
			byte buf[] = new byte[8192 * 8];
			while (true) {
				int x = (int) (rem > buf.length ? buf.length : rem);
				int r = in.read(buf, 0, x);
				if (stop) {
					in.close();
					out.close();
					throw new InterruptedException();
				}
				if (r == -1) {
					break;
				}
				out.write(buf, 0, r);
				rem -= r;
				// count += r;
				if (rem == 0)
					break;
			}
			in.close();
		}
		out.close();
	}

	public static synchronized boolean forceAssemble(String tempdir, String destdir, String filename) {
		stop = false;
		try {
			File statefile = new File(tempdir, ".state");
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(statefile));
			in.readUTF();
			in.readObject();
			in.readUTF();
			in.readUTF();
			in.readUTF();
			in.readLong();
			in.readLong();
			int sz = in.readInt();
			ArrayList<ChunkFileInfo> fileList = new ArrayList<ChunkFileInfo>();
			for (int i = 0; i < sz; i++) {
				fileList.add((ChunkFileInfo) in.readObject());
			}
			Collections.sort(fileList, new ChunkFileInfo());
			File outDir = new File(destdir);
			if (!outDir.exists()) {
				outDir.mkdirs();
			}
			String finalFileName = XDMUtil.getUniqueFileName(destdir, filename);
			File outFile = new File(destdir, finalFileName);
			OutputStream out = new FileOutputStream(outFile);
			try {
				assemble(out, fileList);
			} catch (Exception e) {
				try {
					out.close();
				} catch (Exception ex) {
				}
				outFile.delete();
				in.close();
				throw e;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
