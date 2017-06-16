package org.sdg.xdman.core.common.hls;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.core.common.InvalidContentException;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.http.XDMHttpClient2;
import org.sdg.xdman.util.XDMUtil;

public class BasicDownloader implements Runnable {
	String url;
	String file;
	private XDMHttpClient2 conn;
	private String user_agent, referer;
	private ArrayList<String> cookies;
	private XDMConfig config;
	// private Credential credential;
	private InputStream stream;
	// private long size;
	private IHLSProgress prg;
	private Thread t;
	// private boolean started = false;
	private OutputStream fs;
	private boolean stopflag = false;

	public BasicDownloader(String url, String file, Credential auth, String referer, String user_agent,
			ArrayList<String> cookies, XDMConfig config, IHLSProgress prg) {
		this.url = url;
		this.file = file;
		//credential = auth;
		this.referer = referer;
		this.user_agent = user_agent;
		this.cookies = cookies;
		this.config = config;
		this.prg = prg;
	}

	public void start() {
		t = new Thread(this);
		//started = true;
		t.start();
	}

	public void Stop() {
		stopflag = true;
		try {
			fs.close();
		} catch (Exception e) {
		}
	}

	private void connect() throws InvalidContentException, Exception {
		int maxretry = 10;
		int count = 0;
		while (!stopflag) {
			try {
				conn = new XDMHttpClient2(config);
				System.out.println("Connecting to: " + url);
				conn.connect(url);
				if (!XDMUtil.isNullOrEmpty(referer)) {
					conn.addRequestHeaders("referer", referer);
				}
				if (!XDMUtil.isNullOrEmpty(user_agent)) {
					conn.addRequestHeaders("user-agent", user_agent);
				}
				if (cookies != null) {
					conn.addCookies(cookies);
				}
				conn.sendRequest();

				if (stopflag)
					return;

				stream = conn.in;
				//size = conn.getContentLength();
				int rc = conn.getResponseCode();
				if (!(rc == 200 || rc == 206)) {
					throw new Error("Error downloading video");
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
				conn.close();
			} catch (Error e) {
				conn.close();
				e.printStackTrace();
				throw new InvalidContentException("download error");
			}
			System.out.println("reconnecting");
			Thread.sleep(1000);
			if (stopflag)
				return;
			count++;
			if (count > maxretry)
				throw new InvalidContentException("download error");
		}
	}

	public String GetFile() {
		return file;
	}

	byte[] buf = new byte[8192];

	long oldRead = 0;

	long _startTime = System.currentTimeMillis();// Environment.TickCount;
	long _bytesCount = 0;
	long read;
	long downloaded;
	float rate;
	long time = System.currentTimeMillis();

	public float GetDownloadRate() {
		return rate;
	}

	public void run() {
		// MessageBox.Show("download");
		// long dw = fs.Length;
		// prg.SetDownloaded(dw);
		try {
			while (!stopflag) {
				// MessageBox.Show("connecting...");
				connect();
				fs = new FileOutputStream(file);
				try {
					read = 0;
					if (stopflag)
						break;
					while (!stopflag) {
						int x = stream.read(buf, 0, buf.length);
						if (x == -1)
							break;
						fs.write(buf, 0, x);
						downloaded += x;
						_bytesCount += x;
						read += x;
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
												System.out
														.println("------------------Sleeping for : " + _toWait + " ms");
												Thread.sleep((int) _toWait);
											} catch (Exception e) {
											}
											long _diff = System.currentTimeMillis() - _startTime;
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
						// dw += x;
						prg.setDownloaded(x);
					}
					fs.close();
					try {
						stream.close();
					} catch (Exception e) {
					}
					try {
						conn.close();
					} catch (Exception e) {
					}
					// System.Windows.Forms.MessageBox.Show("done");
					if (!stopflag) {
						prg.downloadComplete(this);
					}
					break;
				} catch (InvalidContentException ee) {
					try {
						fs.close();
					} catch (Exception e) {
					}
					try {
						stream.close();
					} catch (Exception e) {
					}
					try {
						conn.close();
					} catch (Exception e) {
					}
					throw ee;
				} catch (Exception e2) {
					// MessageBox.Show(e+"");
					try {
						fs.close();
					} catch (Exception e) {
					}
					try {
						stream.close();
					} catch (Exception e) {
					}
					try {
						conn.close();
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception exx) {
			try {
				fs.close();
			} catch (Exception e) {
			}
			try {
				stream.close();
			} catch (Exception e) {
			}
			try {
				// response.close();
				conn.close();
			} catch (Exception e) {
			}
			System.out.println(exx + "");
			if (!stopflag) {
				this.lasterror = exx;
				prg.downloadFailed(this);
			}
		}
	}

	Exception lasterror;
}
