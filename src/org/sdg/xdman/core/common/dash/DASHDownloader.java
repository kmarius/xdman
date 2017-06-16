package org.sdg.xdman.core.common.dash;

import java.io.*;
import java.util.*;

import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.core.common.DownloadInfo;
import org.sdg.xdman.core.common.DownloadProgressListener;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.IDownloader;
import org.sdg.xdman.core.common.IXDMConstants;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.http.XDMHttpClient2;
import org.sdg.xdman.util.FFmpegHelper;
import org.sdg.xdman.util.XDMUtil;

public class DASHDownloader implements Runnable, ISegmentProgress, IDownloader {
	String url1, url2, userAgent;
	final int MAX_SEGMENT_SIZE = 1024 * 1024;
	int segment_size;
	long length1, length2;
	int c;
	ArrayList<DASHSegment> segments;
	ArrayList<SegmentDownloader> clist = null;
	String tempdir;
	Thread t;
	String ua, referer;
	ArrayList<String> cookies;
	DownloadProgressListener prgListener;
	DownloadStateListner mgr;
	UUID id;
	String out_file, out_dir;
	XDMConfig config;
	boolean stopflag;
	boolean overwrite;
	long downloaded;
	int state = IXDMConstants.STOPPED;
	String status = "connecting...";
	long total_size = -1;
	boolean init;

	public DASHDownloader(UUID id, String url1, String url2, String file,
			String dest_dir, String temp_dir, String referer, String ua,
			ArrayList<String> cookies, XDMConfig config) {
		this.id = id;
		this.url1 = url1;
		this.url2 = url2;
		this.length1 = -1;
		this.length2 = -1;
		this.tempdir = temp_dir;
		this.out_file = file;
		this.out_dir = dest_dir;
		this.ua = ua;
		this.referer = referer;
		this.cookies = cookies;
		this.config = config;
		c = 0;
		System.out.println(url1 + "\n" + url2);
	}

	public void start() {
		state = IXDMConstants.DOWNLOADING;
		t = new Thread(this);
		t.start();
	}

	public void resume() {
		System.out.println("tempdir: " + tempdir);
		if (state == IXDMConstants.DOWNLOADING)
			return;
		state = IXDMConstants.DOWNLOADING;
		if (!restoreState()) {
			System.out.println("New");
			start();
			return;
		}
		System.out.println("Resuming");
		clist = new ArrayList<SegmentDownloader>();
		downloadSegments();
	}

	public void run() {
		init();
		downloadSegments();
	}

	boolean probe_url(String url, int ref_rc[], long ref_len[],
			String ref_url[]) {
		while (!stopflag) {
			XDMHttpClient2 hc = null;
			try {
				hc = new XDMHttpClient2(config);
				hc.connect(url);
				hc.addCookies(cookies);
				hc.addRequestHeaders("referer", referer);
				hc.addRequestHeaders("user-agent", userAgent);
				hc.addRequestHeaders("range", "bytes=0-");
				hc.sendRequest();
				if (stopflag) {
					hc.close();
					return false;
				}
				int rc = hc.getResponseCode();
				long len = hc.getContentLength();
				String ctype = hc.getResponseHeader("content-type");
				if (rc == 200 && "text/plain".equals(ctype)) {
					StringBuffer line = new StringBuffer();
					InputStream in = hc.in;
					if (len > 0) {
						for (int i = 0; i < len; i++) {
							line.append((char) in.read());
						}
					} else {
						while (true) {
							line.append((char) in.read());
						}
					}
					url = line.toString();
					throw new Exception("DASH_REDIRECTION");
				}
				System.out.println("response_code " + rc);
				if (!(rc == 200 || rc == 206))
					throw new Error("DASH_PROBE_FAIL");
				else {
					ref_rc[0] = rc;
					if (rc == 200) {
						ref_len[0] = -1;
					} else {
						ref_len[0] = len;
					}
					ref_url[0] = url;
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
				ref_rc[0] = 0;
				ref_len[0] = -1;
				ref_url[0] = null;
				return false;
			} finally {
				if (hc != null)
					hc.close();
			}
		}
		return false;
	}

	boolean probe_url() {
		int[] p_rc = new int[1];
		long[] p_len = new long[1];
		String[] p_url = new String[1];
		boolean ret = probe_url(url1, p_rc, p_len, p_url);
		if (!ret) {
			return false;
		} else {
			this.url1 = p_url[0];
			this.length1 = p_len[0];
		}
		ret = probe_url(url2, p_rc, p_len, p_url);
		if (!ret) {
			return false;
		} else {
			this.url2 = p_url[0];
			this.length2 = p_len[0];
		}
		return true;
	}

	// int probe() {
	// while (!stopflag) {
	// XDMHttpClient2 hc = null;
	// try {
	// hc = new XDMHttpClient2(config);
	// hc.connect(url1);
	// hc.addCookies(cookies);
	// hc.addRequestHeaders("referer", referer);
	// hc.addRequestHeaders("user-agent", userAgent);
	// hc.addRequestHeaders("range", "bytes=0-");
	//
	// hc.sendRequest();
	// if (stopflag) {
	// hc.close();
	// return 0;
	// }
	// int rc = hc.getResponseCode();
	// System.out.println("response_code " + rc);
	// if (rc != 206) {
	// return 0;
	// }
	// return 206;
	// } catch (Throwable e) {
	// e.printStackTrace();
	// return 0;
	// } finally {
	// if (hc != null)
	// hc.close();
	// }
	// }
	// return 0;
	// }

	void init() {
		try {
			if (!probe_url()) {
				throw new Exception("Download Failed");
			}
			segments = new ArrayList<DASHSegment>();
			clist = new ArrayList<SegmentDownloader>();
			long minsize = Math.min(length1, length2);
			segment_size = (int) Math.min(minsize, MAX_SEGMENT_SIZE);
			tempdir = new File(tempdir, UUID.randomUUID().toString())
					.getAbsolutePath();
			// int rc = probe();
			// System.out.println("response: " + rc);
			if (length1 < 0) {
				createSegments(segments, -1, url1, -1, true);
				createSegments(segments, -1, url2, -1, false);
			} else {
				createSegments(segments, length1, url1, segment_size, true);
				createSegments(segments, length2, url2, segment_size, false);
			}
			System.out.println("total segments: " + segments.size()
					+ " total len: " + (length1 + length2));
			clist = new ArrayList<SegmentDownloader>();
			new File(tempdir).mkdirs();
			if (!overwrite) {
				out_file = XDMUtil.getUniqueFileName(out_dir, out_file);
			}
			category = XDMUtil.findCategory(out_file);
			init = true;
			saveState();
			int state2 = state;
			state = IXDMConstants.REDIRECTING;
			updated();
			state = state2;
			updated();
			if (mgr != null) {
				mgr.downloadConfirmed(id, this);
			}
			status = "downloading...";
		} catch (Exception e) {
			e.printStackTrace();
			failed();
		}
	}

	public void failed() {
		if (init) {
			saveState();
		}
		if (clist != null) {
			for (int i = 0; i < clist.size(); i++) {
				clist.get(i).Stop();
			}
		}
		saveState();
		state = IXDMConstants.FAILED;
		status = "Download failed";
		if (mgr != null) {
			mgr.downloadFailed(id);
		}
		updated();
	}

	void createSegments(List<DASHSegment> segments, long len, String url,
			int segment_size, boolean first) {
		if (len < 0) {
			c = c + 1;
			DASHSegment s = new DASHSegment();
			s.url = url;
			s.filename = new File(tempdir, c + "").getAbsolutePath();
			System.out.println("Segment-" + segments.size() + s.url + " "
					+ s.filename);

			s.id = c;
			s.offset = 0;
			s.length = -1;
			s.first = first;
			s.finished = false;
			segments.add(s);
			return;
		}
		System.out.println("length: " + len + " " + segment_size);

		int n = (int) (len / segment_size);
		System.out.println("n: " + n);
		long length = -1;
		long offset = 0;
		for (int i = 0; i < n; i++) {
			offset = i * segment_size;
			if (i == n - 1) {
				length = len - offset;
			} else {
				length = segment_size;
			}

			c = c + 1;
			DASHSegment s = new DASHSegment();
			s.url = url;
			s.filename = new File(tempdir, c + "").getAbsolutePath();
			s.id = c;
			s.offset = offset;
			s.length = length;
			s.first = first;
			System.out.println(s.filename);
			segments.add(s);
		}
	}

	private void downloadSegments() {
		synchronized (this) {
			if (checkFinished()) {
				System.out.println("downloaded: " + downloaded + " of "
						+ (length1 + length2));
				// downloaded = (length1 + length2);
				updated();
				assemble();
				return;
			}
			if (clist != null) {
				for (int j = 0; j < segments.size(); j++) {
					DASHSegment ds = segments.get(j);
					if (ds.sd == null) {
						if (!ds.finished) {
							System.out.println("creating " + j);
							if (clist.size() < 8) {
								SegmentDownloader sd = new SegmentDownloader(
										ds.url, ds.filename, ds.offset,
										ds.length, this, referer, this.ua,
										cookies, config);
								clist.add(sd);
								ds.sd = sd;
								sd.start();
							}
						}
					}
				}
			}
		}
	}

	public void downloadComplete(ISegmentDownloader sd) throws Exception {
		System.out.println("finished*************************");
		synchronized (this) {
			clist.remove(sd);
			for (int i = 0; i < segments.size(); i++) {
				if (segments.get(i).sd == sd) {
					DASHSegment segment = segments.get(i);
					segment.sd = null;
					segment.finished = true;
					break;
				}
			}
			saveState();
			downloadSegments();
		}
	}

	private boolean checkFinished() {
		boolean finished = true;
		if (segments != null) {
			for (int i = 0; i < segments.size(); i++) {
				if (!segments.get(i).finished) {
					finished = false;
					break;
				}
			}
		}
		return finished;
	}

	void assemble() {
		System.out.println("assembling...");
		updated2();
		String outfile = out_file;
		try {
			File f1 = new File(tempdir, "file1");
			File f2 = new File(tempdir, "file2");
			FileOutputStream fs1 = new FileOutputStream(f1);
			FileOutputStream fs2 = new FileOutputStream(f2);
			for (int i = 0; i < segments.size(); i++) {
				DASHSegment s = segments.get(i);
				FileInputStream fi = new FileInputStream(s.filename);
				copy(fi, s.first ? fs1 : fs2);
				fi.close();
			}
			fs1.close();
			fs2.close();
			if (!overwrite) {
				outfile = XDMUtil.getUniqueFileName(out_dir, out_file);
			}
			out_file = outfile;
			updated();
			System.out
					.println("OUT_FILE: " + out_file + " OUT_DIR: " + out_dir);
			if (!FFmpegHelper.combineDASH(f1.getAbsolutePath(), f2
					.getAbsolutePath(), new File(out_dir, out_file)
					.getAbsolutePath())) {
				failed();
				return;
			}
			for (int i = 0; i < segments.size(); i++) {
				DASHSegment s = segments.get(i);
				new File(s.filename).delete();
			}
			f1.delete();
			f2.delete();
			try {
				new File(tempdir).delete();
			} catch (Exception e) {
			}
			if (mgr != null) {
				mgr.downloadComplete(this.id);
			}
			status = "Finished";
			state = IXDMConstants.COMPLETE;
			updated();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			failed();
		}
	}

	public void connected(ISegmentDownloader sd) {
		// System.out.println("connected");
	}

	public synchronized void downloaded(ISegmentDownloader sd, long downloaded) {
		this.downloaded += downloaded;
		updated();
	}

	public void downloadFailed(ISegmentDownloader sd) {
		synchronized (this) {
			if (state == IXDMConstants.FAILED)
				return;
			status = "Download failed due to error";
			state = IXDMConstants.FAILED;
			failed();
		}
	}

	void copy(InputStream instream, OutputStream out) throws Exception {
		byte[] buf = new byte[8192];
		while (true) {
			int x = instream.read(buf, 0, buf.length);
			if (x == -1) {
				return;
			}
			out.write(buf, 0, x);
		}
	}

	public Credential getCreditential() {
		return null;
	}

	public UUID getID() {
		return id;
	}

	public void setProgressListener(DownloadProgressListener pl) {
		this.prgListener = pl;
	}

	public void setStateListener(DownloadStateListner mgr) {
		this.mgr = mgr;
	}

	public void setTempDir(String temp_dir) {
		this.tempdir = temp_dir;
	}

	public String getTempDir() {
		return this.tempdir;
	}

	public int getDownloadType() {
		return 10;
	}

	public void setOverriteIfFileExists(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public long getFileSize() {
		return total_size;
	}

	public String getSize() {
		return total_size > 0 ? XDMUtil.getFormattedLength(total_size) : "---";
	}

	public void setCredential(String user, String pass, String host) {
	}

	public void setCredential(String user, String pass) {
	}

	public void setTempdir(String tempdir) {
		this.tempdir = tempdir;
	}

	public String getTempdir() {
		return this.tempdir;
	}

	public String getFileName() {
		return this.out_file;
	}

	public void setFileName(String file) {
		out_file = file;
	}

	public void setDestdir(String destdir) {
		this.out_dir = destdir;
	}

	public String getDestdir() {
		return this.out_dir;
	}

	public void setUrl(String url) {

	}

	public String getUrl() {
		return this.url1;
	}

	public void setDownloadListener(DownloadStateListner pl) {
		this.mgr = pl;
	}

	public void setOverwrite(boolean b) {
		this.overwrite = b;
	}

	public int getState() {
		return state;
	}

	public void setDownloaded(long downloaded) {
		this.downloaded += downloaded;
		updated();
	}

	long tgap2 = Integer.MIN_VALUE;
	long tgap = tgap2;

	public void updated() {
		long t = System.currentTimeMillis();
		if (state == IXDMConstants.FAILED || state == IXDMConstants.COMPLETE
				|| state == IXDMConstants.STOPPED
				|| state == IXDMConstants.REDIRECTING) {
			updated2();
			tgap = t;
			if (state != IXDMConstants.COMPLETE) {
				tgap2 = Integer.MIN_VALUE;
			}
		} else {
			if ((t - tgap) > 1000) {
				tgap = t;
				updated2();
			}
		}
		if ((t - tgap2) > 5000) {
			tgap2 = t;
			try {
				saveState();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	public void stop() {
		status = "Stopped";
		stopflag = true;
		if (init) {
			saveState();
		}
		if (clist != null) {
			for (int i = 0; i < clist.size(); i++) {
				clist.get(i).Stop();
			}
		}
		saveState();
		state = IXDMConstants.STOPPED;
		if (mgr != null) {
			mgr.downloadPaused(this.id);
		}
		updated();
	}

	long prevdownload;
	long startTime;
	String category;

	public void updated2() {
		try {
			// System.out.println("updated");
			DownloadInfo info = new DownloadInfo();
			info.path = new File(tempdir);
			info.url = getUrl();
			info.file = out_file;
			String stat = "downloading...";
			long dwnld = downloaded;
			info.rlen = -1;
			info.rdwn = downloaded;
			info.length = XDMUtil.getFormattedLength(length1 + length2);
			info.downloaded = XDMUtil.getFormattedLength(downloaded);
			if (state == IXDMConstants.COMPLETE) {
				info.length = info.downloaded;
			}
			long diff = dwnld - prevdownload;
			long time = System.currentTimeMillis();
			long dt = (time - startTime);
			float rt = 0;
			info.eta = "---";
			if (dt != 0) {
				rt = ((float) diff / dt) * 1000;
				info.eta = XDMUtil.getETA(
						(length1 + length2) - dwnld/* diff */, rt);
			}
			startTime = time;
			info.speed = XDMUtil.getFormattedLength(rt) + "/sec";
			prevdownload = dwnld;
			// info.eta = "---";

			info.resume = "Yes";
			info.status = stat;
			if (length1 + length2 > 0) {
				info.prg = (int) ((downloaded * 100) / (length1 + length2));
				info.progress = info.prg + "";
			}

			info.state = this.state;
			info.msg = this.status;
			info.category = category;
			info.tempdir = tempdir;

			if (prgListener != null) {
				if (prgListener.isValidWindow()) {
					prgListener.update(info);
				}
			}
			if (mgr != null) {
				// XDMUtils.KeepSystemAlive();

				mgr.updateManager(id, info);
			}
		} catch (Exception e) {
		}
	}

	private boolean restoreState() {
		synchronized (this) {
			DataInputStream br = null;
			try {
				br = new DataInputStream(new FileInputStream(new File(
						this.tempdir, ".state")));
				this.url1 = br.readUTF();
				this.url2 = br.readUTF();
				this.length1 = br.readLong();
				this.length2 = br.readLong();
				System.out.println("URL1: " + this.url1);
				System.out.println("URL2: " + this.url2);
				System.out.println("LEN1: " + this.length1);
				System.out.println("LEN2: " + this.length2);
				// this.out_file =
				br.readUTF();
				this.out_dir = br.readUTF();
				this.downloaded = br.readLong();
				if (br.readBoolean()) {
					this.ua = br.readUTF();
				}
				if (br.readBoolean()) {
					this.referer = br.readUTF();
				}
				if (br.readBoolean()) {
					cookies = new ArrayList<String>();
					int count = br.readInt();
					for (int i = 0; i < count; i++) {
						this.cookies.add(br.readUTF());
					}
				}
				init = true;
				int c = br.readInt();
				segments = new ArrayList<DASHSegment>();
				for (int i = 0; i < c; i++) {
					DASHSegment segment = new DASHSegment();
					segment.url = br.readUTF();
					segment.offset = br.readLong();
					segment.length = br.readLong();
					segment.filename = br.readUTF();
					segment.id = i;
					segment.finished = br.readBoolean();
					segment.first = br.readBoolean();
					segments.add(segment);
				}

				br.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				try {
					br.close();
				} catch (Exception e2) {
				}
			}
			return false;
		}
	}

	private void saveState() {
		synchronized (this) {
			DataOutputStream fs = null;
			try {
				if (init) {
					fs = new DataOutputStream(new FileOutputStream(new File(
							this.tempdir, ".state")));
					fs.writeUTF(this.url1);
					fs.writeUTF(this.url2);
					fs.writeLong(length1);
					fs.writeLong(length2);
					fs.writeUTF(out_file);
					fs.writeUTF(out_dir);
					fs.writeLong(downloaded);
					boolean hasUA = (!XDMUtil.isNullOrEmpty(this.ua)), hasReferer = (!XDMUtil
							.isNullOrEmpty(this.referer)), hasCookies = (!(this.cookies == null));
					fs.writeBoolean(hasUA);
					if (hasUA) {
						fs.writeUTF(this.ua);
					}
					fs.writeBoolean(hasReferer);
					if (hasReferer) {
						fs.writeUTF(this.referer);
					}
					fs.writeBoolean(hasCookies);
					if (hasCookies) {
						fs.writeInt(this.cookies.size());
						for (int i = 0; i < this.cookies.size(); i++) {
							String str = this.cookies.get(i);
							fs.writeUTF(str);
						}
					}
					fs.writeInt(this.segments.size());
					for (int i = 0; i < segments.size(); i++) {
						DASHSegment s = segments.get(i);
						fs.writeUTF(s.url);
						fs.writeLong(s.offset);
						fs.writeLong(s.length);
						fs.writeUTF(s.filename);
						// fs.writeInt(s.id);
						fs.writeBoolean(s.finished);
						fs.writeBoolean(s.first);
					}
					fs.close();
				}
			} catch (Exception e) {
				try {
					fs.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	public int getType() {
		return IXDMConstants.DASH;
	}

}
