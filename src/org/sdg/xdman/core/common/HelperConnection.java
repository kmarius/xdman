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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.sdg.xdman.core.common.http.XDMHttpClient2;

public class HelperConnection implements Runnable {
	long start, length;
	XDMConfig config;
	String url;
	XDMHttpClient2 client;
	InputStream in;
	ByteArrayOutputStream out;
	HelpListener listerner;
	Connection c;
	boolean stop = false;
	String fileName;
	Credential credential;
	int timeout;
	ConnectionManager mgr;

	public HelperConnection(XDMConfig config, long start, long length,
			String url, HelpListener l, Connection c, String fileName,
			 Credential credential, ConnectionManager mgr) {
		this.config = config;
		this.start = start;
		this.length = length;
		this.url = url;
		this.listerner = l;
		this.c = c;
		this.fileName = fileName;
		this.credential = credential;
		this.mgr = mgr;
	}

	void start() {
		Thread t = new Thread(this);
		t.start();
	}

	void stop() {
		stop = true;
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			client = new XDMHttpClient2(config);
			client.connect(url);
			if (stop) {
				close();
				return;
			}
			// client.setTimeOut(timeout);
			client.addCookies(mgr.cookieList);
			client.addRequestHeaders("referer", mgr.referer);
			client.addRequestHeaders("user-agent", mgr.userAgent);

			client.addRequestHeaders("range", "bytes=" + start + "-");

			if (credential == null) {
				credential = Authenticator.getInstance().getCredential(
						client.host);
			}
			if (credential != null) {
				client.user = credential.user;
				client.pass = credential.pass;
			}
			client.sendRequest();
			if (stop) {
				close();
				return;
			}
			int rc = client.getResponseCode();
			System.out.println("Helper RESPONSE " + rc);
			if (rc != 206) {
				throw new Exception("Invalid RESPONSE CODE");
			}
			in = client.in;
			out = new ByteArrayOutputStream();
			byte buf[] = new byte[config.tcpBuf];
			long dwn = 0;
			while (true) {
				if (stop) {
					close();
					return;
				}
				int x;
				int rem = (int) (length - dwn);
				if (buf.length > rem) {
					x = in.read(buf, 0, rem);
				} else {
					x = in.read(buf);
				}
				if (stop) {
					close();
					return;
				}
				if (x == -1)
					throw new Exception("UNEXPECTED EOF");
				out.write(buf, 0, x);
				dwn += x;
				if (dwn >= length)
					break;
			}
			if (listerner != null) {
				if (stop) {
					close();
					return;
				}
				listerner.helpComplete(this, this);
			}
			close();
		} catch (Exception e) {
			System.out.println("Error IN HELPER: " + e);
			e.printStackTrace();
			close();
		}
	}

	void close() {
		System.out.println("closing helper conn. " + stop);
		try {
			client.close();
		} catch (Exception e) {
		}
		try {
			in.close();
		} catch (Exception e) {
		}
		try {
			out.close();
		} catch (Exception e) {
		}
	}
}
