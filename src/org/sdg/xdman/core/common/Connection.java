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

package org.sdg.xdman.core.common;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

import org.sdg.xdman.gui.StringResource;

//import java.util.Random;

public abstract class Connection implements Runnable, IXDMConstants {
	protected Credential credential;
	public long read, clen;
	public String content_type, content_disposition;
	public String url, fileName;
	protected volatile long length = -1, contentLength, downloaded = 0,
			startOff;
	public int status;
	protected boolean stop = false;
	protected ConnectionManager mgr;
	protected Object lock;
	protected boolean pause = false;
	protected String lastError = null;
	protected InputStream in;
	public RandomAccessFile out;
	protected byte buf[] = new byte[1 * 8192];
	protected Thread t;
	protected String message;
	public float rate;
	public long time;

	public static class State implements Serializable {
		private static final long serialVersionUID = 4156081526363598564L;

		public State(int stat, String fileName, long length,
				long contentLength, long downloaded, long startOff, String url) {
			super();
			this.stat = stat;
			this.fileName = fileName;
			this.length = length;
			this.contentLength = contentLength;
			this.downloaded = downloaded;
			this.startOff = startOff;
			this.url = url;
		}

		int stat;
		String fileName, url;
		long length, contentLength, downloaded, startOff;
	}

	public State getState() {
		return new State(status, fileName, length, contentLength, downloaded,
				startOff, url);
	}

	public void setState(State t) {
		status = t.stat;
		fileName = t.fileName;
		length = t.length;
		contentLength = t.contentLength;
		downloaded = t.downloaded;
		startOff = t.startOff;
		url = t.url;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public long getDownloaded() {
		return downloaded;
	}

	public void setDownloaded(long downloaded) {
		this.downloaded = downloaded;
	}

	public long getStartOff() {
		return startOff;
	}

	public void setStartOff(long startOff) {
		this.startOff = startOff;
	}

	protected Connection(String url, String fileName, long startOff,
			long length, long contentLength, ConnectionManager mgr, Object lock) {
		this.url = url;
		this.fileName = fileName;
		this.length = length;
		this.contentLength = contentLength;
		this.mgr = mgr;
		this.startOff = startOff;
		this.lock = lock;
	}

	protected Connection(State state, int timeout, ConnectionManager mgr,
			Object lock) {
		setState(state);
		this.mgr = mgr;
		this.lock = lock;
		this.stop = false;
		this.pause = false;
	}

	public abstract boolean connect();

	public void run() {
		while (!download()) {
			if (stop) {
				close();
				break;
			}
			chkPause();
			if (!connect()) {
				close();
				return;
			}
			if (stop) {
				close();
				break;
			}
			chkPause();
		}

	}

	public abstract void close();

	public void start() {
		t = new Thread(this);
		t.start();
	}

	public void stop() {
		stop = true;
		t.interrupt();
		message = StringResource.getString("DISCONNECT");
		// close();
	}

	public void pause() {
		pause = true;
	}

	protected void chkPause() {
		if (!pause)
			return;
		try {
			msg("Pausing...");
			synchronized (lock) {
				lock.wait();
			}
			msg("Resuming...");
		} catch (Exception e) {
			msg(e);
			e.printStackTrace();
		}
	}

	public void resume() {
		pause = false;
		synchronized (lock) {
			lock.notify();
		}
	}

	long oldRead;

	// Random r = new Random();

	long _startTime = System.currentTimeMillis();
	long _bytesCount = 0;

	public boolean download() {
		read = 0;
		msg("To download: " + length);
		if (stop) {
			close();
			msg("Returning because STOP");
			return true;
		}
		chkPause();
		if (status != DOWNLOADING) {
			msg("Returning because NOT DOWNLOADING");
			return false;
		}
		if (in == null) {
			msg("Returning because IN IS NULL");
			return false;
		}
		try {
			msg("Init download...");
			msg("Opening file: " + fileName);
			out = new RandomAccessFile(fileName, "rw");// new
			out.seek(downloaded); // FileOutputStream(fileName);
			while (!stop) {
				message = StringResource.getString("DOWNLOADING");// "downloading...";
				mgr.updated();
				chkPause();
				if (length > -1)
					if (downloaded >= length) {
						// reset read
						if (downloaded > length)
							read = 0;
						msg("Download complete before: " + downloaded + " / "
								+ length);
						msg("Going to call downloadComplete()...");
						status = COMPLETE;
						if (mgr == null)
							return true;
						if (mgr.donwloadComplete(this)) {
							msg("called downloadComplete()");
							message = StringResource
									.getString("DOWNLOAD_COMPLETE");// "Complete";
							close();
							return true;
						}
						status = DOWNLOADING;
						msg("called downloadComplete()...");
						msg("Download complete after: " + downloaded + " / "
								+ length);
					}
				int x;
				int len = (int) (length - downloaded);
				/*
				 * if (read >= clen) { x = -1; } else {
				 */
				if (len < buf.length && len > 0) {
					x = in.read(buf, 0, len);
				} else {
					x = in.read(buf, 0, buf.length);
				}
				// Thread.sleep(10 + r.nextInt(900));
				// }
				if (x != -1)
					read += x;
				if (stop) {
					close();
					return true;
				}
				chkPause();
				if (x == -1) {
					if (length > -1) {
						if (downloaded >= length) {
							status = COMPLETE;
							mgr.donwloadComplete(this);
							close();
							return true;
						}
						throw new Exception("Unexpected End Of Stream: "
								+ downloaded + " / " + length);
					} else {
						status = COMPLETE;
						mgr.donwloadComplete(this);
						return true;
					}
				}
				chkPause();
				out.write(buf, 0, x);
				downloaded += x;

				_bytesCount += x;

				try {
					// Throttle speed
					if (x > 0) {
						//System.out.println("CECK+++++++++++++++++++++"+mgr + mgr.config.maxBPS);
						if (mgr != null && mgr.config.maxBPS > 0) {
							long maxBPS = mgr.config.maxBPS
									/ mgr.config.maxConn;
							if (maxBPS > 0) {
								// Start Throttling
								long _elapsedTime = System.currentTimeMillis()
										- _startTime;

								if (_elapsedTime > 0) {
									long bps = _bytesCount * 1000
											/ _elapsedTime;

									if (bps > maxBPS) {
										long _waitTime = _bytesCount * 1000
												/ maxBPS;
										long _toWait = _waitTime - _elapsedTime;
										if (_toWait > 1) {
											try {
												//System.out
													//	.println("------------------Sleeping for : "
														//		+ _toWait
															//	+ " ms");
												Thread.sleep((int) _toWait);
											} catch (Exception exx) {
											}
											long _diff = System
													.currentTimeMillis()
													- _startTime;
											if (_diff > 1000) {
												_bytesCount = 0;
												_startTime = System
														.currentTimeMillis();
											}
										}
									}
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				long currentTime = System.currentTimeMillis();
				long tdiff = currentTime - time;
				long diff = read - oldRead;
				if (((int) (tdiff / 1000)) > 0) {
					rate = ((float) diff / tdiff) * 1000;
					oldRead = read;
					time = currentTime;
				}
				if (stop) {
					close();
					return true;
				}
				// Thread.sleep(500);
				message = StringResource.getString("DOWNLOADING");// "downloading...";
				chkPause();
				mgr.updated();
				mgr.saveDownload();
			}
			close();
			return true;
		} catch (Exception e) {
			if (length < 0) {
				close();
				return true;
			}
			msg(e);
			e.printStackTrace();
			if (stop) {
				close();
				return true;
			}
			chkPause();
			close();
			System.out.println("Returing false@@@@Error in : " + e);
			return false;
		}
	}

	public abstract boolean isEOF();

	public void msg(Object o) {
		System.out.println(startOff + " :  " + o.toString());
	}

}
