package org.sdg.xdman.core.common.dash;

import java.util.*;

import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.http.XDMHttpClient2;

import java.io.*;

public class SegmentDownloader implements ISegmentDownloader, Runnable {
	String url, referer, userAgent;
	String file;
	long offset;
	long length;
	File absfile;
	XDMHttpClient2 hc;
	OutputStream outstream;
	ISegmentProgress prg;
	boolean stopflag;
	XDMConfig config;
	ArrayList<String> cookies;
	InputStream instream;

	SegmentDownloader(String url, String file, long offset, long length,
			ISegmentProgress prg, String referer, String userAgent,
			ArrayList<String> cookies, XDMConfig config) {
		this.url = url;
		this.length = length;
		this.offset = offset;
		this.file = file;
		this.prg = prg;
		this.referer = referer;
		this.userAgent = userAgent;
		this.cookies = cookies;
		this.config = config;
	}

	public void Stop() {
		stopflag = true;
		try {
			outstream.close();
		} catch (Exception e) {
		}
	}

	void connect() throws Exception {
		while (!stopflag) {
			try {
				rem = length - getDownloaded();
				if (rem == 0) {
					if (length > 0) {
						return;
					}
				}
				hc = new XDMHttpClient2(config);
				hc.connect(url);
				hc.addCookies(cookies);
				hc.addRequestHeaders("referer", referer);
				hc.addRequestHeaders("user-agent", userAgent);
				if (length < 0) {
					System.out.println(getAbsFile() + " "
							+ getAbsFile().exists() + " "
							+ getAbsFile().length());
					if (getAbsFile().exists() && getAbsFile().length() > 0) {
						throw new Error("Cant resume this download");
					}
				}
				if (length > 0) {
					hc.addRequestHeaders("range", "bytes="
							+ (offset + getDownloaded()) + "-"
							+ (offset + length));
				}
				hc.sendRequest();
				if (stopflag) {
					close();
					return;
				}
				int rc = hc.getResponseCode();
				if (rc == 408 || rc == 503 || rc == 413 || rc == 416) {
					throw new Exception("Invalid response code: " + rc);
				}
				if (!(rc == 200 || rc == 206)) {
					throw new Error("Invalid response code: " + rc);
				}
				// long len = hc.getContentLength();
				// if (length > 0) {
				// if (rem != len - 1) {
				// throw new Error("Content length mismatch. expected " + rem +
				// " got " + len);
				// }
				// }
				instream = hc.in;
				// System.out.println("connected");
				return;
			} catch (Exception e) {
				e.printStackTrace();
				close();
			} catch (Error e) {
				e.printStackTrace();
				close();
				break;
			}
		}
	}

	long oldRead = 0;

	long _startTime = System.currentTimeMillis();// Environment.TickCount;
	long _bytesCount = 0;
	long read;
	long downloaded;
	float rate;
	long time = System.currentTimeMillis();

	void close() {
		try {
			hc.close();
		} catch (Exception e) {

		}
		try {
			instream.close();
		} catch (Exception e) {

		}
		try {
			outstream.close();
		} catch (Exception e) {

		}
	}

	long rem;

	void download() {
		try {
			if (stopflag) {
				close();
				return;
			}

			if (length > 0) {
				rem = length - getDownloaded();
				if (rem == 0) {
					prg.downloadComplete(this);
					return;
				}
			}
			outstream = new FileOutputStream(getAbsFile(), length > 0);
			if (length < 0) {
				connect();
				prg.connected(this);
				writeToFile();
				if (stopflag) {
					close();
					return;
				}
				prg.downloadComplete(this);
				return;
			} else {
				while (rem > 0) {
					if (stopflag) {
						close();
						return;
					}
					connect();
					prg.connected(this);
					try {
						writeToFile();
						if (stopflag) {
							close();
							return;
						}
						System.out.println("finished");
						prg.downloadComplete(this);
					} catch (Exception e) {
						System.out.println(e);
						close();
						hc = null;
					}
				}
			}
		} catch (Exception exx) {
			exx.printStackTrace();
			prg.downloadFailed(this);
		} finally {
			close();
		}
	}

	void throttle(int x) {
		_bytesCount += x;
		// Throttle speed
		if (x > 0) {
			long maxBPS = config.maxBPS / config.maxConn;
			if (maxBPS > 0) {
				// Start Throttling
				long _elapsedTime = System.currentTimeMillis() - _startTime;

				if (_elapsedTime > 0) {
					long bps = _bytesCount * 1000 / _elapsedTime;

					if (bps > maxBPS) {
						long _waitTime = _bytesCount * 1000 / maxBPS;
						long _toWait = _waitTime - _elapsedTime;
						if (_toWait > 1) {
							try {
								// System.out
								// .println("------------------Sleeping
								// for
								// : " + _toWait + " ms");
								Thread.sleep((int) _toWait);
							} catch (Exception e) {
							}
							long _diff = System.currentTimeMillis()
									- _startTime;
							if (_diff > 1000) {
								_bytesCount = 0;
								_startTime = System.currentTimeMillis();
							}
						}
					}
				}
			}
		}
		long currentTime = System.currentTimeMillis();// System.DateTime.currentTimeMillis();
		long tdiff = currentTime - time;
		long diff = read - oldRead;
		if (((int) (tdiff / 1000)) > 0) {
			rate = ((float) diff / tdiff) * 1000;
			oldRead = read;
			time = currentTime;
		}

	}

	void writeToFile() throws Exception {
		byte[] buf = new byte[8192];
		while (true) {
			if (stopflag) {
				close();
				return;
			}

			int b = buf.length;
			if (length > 0) {
				b = (int) (rem > buf.length ? buf.length : rem);
			}
			int x = instream.read(buf, 0, b);
			if (x < 0) {
				if (length > 0) {
					throw new Exception("unexpected eof expected: " + rem
							+ " got " + x);
				} else {
					break;
				}
			}
			if (stopflag) {
				close();
				return;
			}
			outstream.write(buf, 0, x);
			rem -= x;
			throttle(x);
			prg.downloaded(this, x);
			if (length > 0) {
				if (rem <= 0)
					break;
			}
		}
	}

	long getDownloaded() {
		if (getAbsFile().exists()) {
			return getAbsFile().length();
		} else {
			return 0L;
		}
	}

	File getAbsFile() {
		if (absfile == null) {
			absfile = new File(file);
		}
		return absfile;
	}

	public void start() {
		new Thread(this).start();
	}

	public void stop() {

	}

	public long getSize() {
		return hc.getContentLength();
	}

	public void run() {
		download();
	}
}
