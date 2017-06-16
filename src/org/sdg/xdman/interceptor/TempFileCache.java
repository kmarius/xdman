package org.sdg.xdman.interceptor;

import java.util.*;
import java.io.*;

import org.sdg.xdman.core.common.XDMConfig;

public class TempFileCache {
	static ArrayList<TempFile> list = new ArrayList<TempFile>();

	public static void addToCache(String file, String url, XDMConfig config) {
		for (int i = 0; i < list.size(); i++) {
			TempFile f = list.get(i);
			if (f.url.equals(url)) {
				return;
			}
		}
		TempFile f = new TempFile();
		f.url = url;
		f.file = file;
		list.add(f);
		try {
			save(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static TempFile getFromCache(String url) {
		for (int i = 0; i < list.size(); i++) {
			TempFile f = list.get(i);
			if (f.url.equals(url)) {
				return f;
			}
		}
		return null;
	}

	public static void save(XDMConfig config) throws Exception {
		File f = new File(config.tempdir, ".xdmcache");
		DataOutputStream ds = new DataOutputStream(new FileOutputStream(f));
		ds.writeInt(list.size());
		for (int i = 0; i < list.size(); i++) {
			TempFile t = list.get(i);
			ds.writeUTF(t.url);
			ds.writeUTF(t.file);
		}
		ds.close();
	}
	
	public static void load(XDMConfig config)throws Exception{
		
	}
}

class TempFile {
	String file;
	String url;
}