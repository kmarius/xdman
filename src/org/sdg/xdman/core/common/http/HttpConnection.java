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

package org.sdg.xdman.core.common.http;

import java.io.IOException;
import java.net.UnknownHostException;

import org.sdg.xdman.core.common.AuthenticationException;
import org.sdg.xdman.core.common.Authenticator;
import org.sdg.xdman.core.common.Connection;
import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.core.common.InvalidContentException;
import org.sdg.xdman.core.common.InvalidReplyException;
import org.sdg.xdman.core.common.ResumeNotSupportedException;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.StringResource;

public class HttpConnection extends Connection {
	// DefaultHttpClient client;
	public XDMHttpClient2 client;
	boolean clientSet = false;
	int count = 0;
	XDMConfig config;
	public int errorCode;

	public HttpConnection(String url, String fileName, long startOff,
			long length, long contentLength, ConnectionManager mgr,
			Object lock, Credential c, XDMConfig config) {
		super(url, fileName, startOff, length, contentLength, mgr, lock);
		this.credential = c;
		this.config = config;
		this.stop = false;
	}

	public HttpConnection(State state, int timeout, ConnectionManager mgr,
			Object lock, Credential c, XDMConfig config) {
		super(state, timeout, mgr, lock);
		this.credential = c;
		this.config = config;
		this.stop = false;
	}

	public boolean connect() {
		status = CONNECTING;
		int code = 0;
		while (true) {
			read = 0;
			clen = 0;
			if (stop) {
				close();
				break;
			}
			chkPause();
			try {
				message = StringResource.getString("CONNECTING");
				mgr.updated();
				msg("Connecting...");
				if (length > 0)
					if ((startOff + downloaded) - (startOff + length - 1) > 0) {
						mgr.donwloadComplete(this);
						return true;
					}
				client = new XDMHttpClient2(config);

				msg("Connecting to..." + url);
				client.connect(url);
				count = 0;
				client.addCookies(mgr.cookieList);
				client.addRequestHeaders("referer", mgr.referer);
				client.addRequestHeaders("user-agent", mgr.userAgent);
				if (length > 0) {
					client.addRequestHeaders("Range", "bytes="
							+ (startOff + downloaded) + "-");
				} else {
					client.addRequestHeaders("Range", "bytes=0-");
				}

				if (credential == null) {
					credential = Authenticator.getInstance().getCredential(
							client.host);
				}
				if (credential != null) {
					client.user = credential.user;
					client.pass = credential.pass;
				}

				msg("SEND GET...");
				message = StringResource.getString("SEND_GET");// "Send GET...";
				client.sendRequest();
				count = 0;
				// HttpResponse response = client.execute(get);
				msg("SEND GET...Done");
				if (stop) {
					close();
					break;
				}
				chkPause();
				message = StringResource.getString("PARSING_RESPONSE");// "Parsing response...";
				mgr.updated();
				code = client.getResponseCode();// response.getStatusLine().getStatusCode();
				msg("Response code: " + code);
				if (code >= 300 && code < 400) {
					client.close();
					if (length < 0) {
						url = client.getResponseHeader("location");
						if (!url.startsWith("http")) {
							url = "http://" + client.getHostString() + "/"
									+ url;
						}
						url = url.replace(" ", "%20");
						throw new IllegalAccessException("Redirecting to: "
								+ url);
					} else {
						throw new InvalidReplyException(code,
								"Invalid redirect");
					}
				}

				if (code != 200 && code != 206 && code != 416 && code != 413
						&& code != 401 && code != 408 && code != 407
						&& code != 503)
					throw new InvalidReplyException(code,
							"Invalid response from server");
				if (code == 503) {
					throw new Exception();
				}
				if (code == 401) {
					credential = mgr.getCreditential();
					if (credential == null) {
						throw new AuthenticationException(client.statusLine);
					} else
						throw new IllegalArgumentException("Unauthorized");
				}
				if (code == 407) {
					throw new AuthenticationException(client.statusLine);
				}
				if (startOff + downloaded > 0) {
					if (code != 206)
						throw new ResumeNotSupportedException(
								"Server does not support partial content(Resume feature)");
				}
				long len = client.getContentLength();
				clen = len;
				if (length < 0) {
					try {
						length = len;
						contentLength = len;
					} catch (Exception e) {
					}
				}
				msg("Expected contentlength: " + contentLength + " found "
						+ len + " " + length);
				if (contentLength != -1 && length != -1) {
					if (contentLength != len)
						if (contentLength - downloaded != len)
							throw new InvalidContentException(
									"Invalid Content Length: Expected: "
											+ contentLength + " but got: "
											+ len);
				}
				if (stop) {
					close();
					break;
				}
				chkPause();
				in = client.in;
				status = DOWNLOADING;
				message = StringResource.getString("DOWNLOADING");
				buf = new byte[config.tcpBuf];
				mgr.updated();
				msg("Notify...");
				msg("Going to call connected()...");
				content_type = client.getResponseHeader("content-type");
				try {
					if (content_type.indexOf(";") >= 0) {
						content_type = content_type.split(";")[0].trim();
					}
				} catch (Exception e) {
				}
				System.out.println("Final content-type: " + content_type);
				content_disposition = client.getContentName();
				mgr.connected(this);
				msg("Returned from connected()");
				System.out.println(client.sock.isConnected() + " "
						+ client.sock.isClosed());
				return true;
			} catch (UnknownHostException e) {
				message = StringResource.getString("DISCONNECT");
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				if (count > 5) {
					status = FAILED;
					lastError = "Host not found";
					errorCode = CONNECT_ERR;
					break;
				}
				if (stop) {
					close();
					break;
				}
				chkPause();
				msg("Sleeping 5 sec");
				message = StringResource.getString("DISCONNECT");
				try {
					Thread.sleep(5000);
					chkPause();
				} catch (Exception err) {
				}
				message = StringResource.getString("CONNECTING");
				mgr.updated();
				count++;
			} catch (IllegalAccessException e) {
				message = "Redirecting...";
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				if (stop) {
					close();
					break;
				}
				chkPause();
			} catch (IllegalArgumentException e) {
				message = "Authenticating...";
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				if (stop) {
					close();
					break;
				}
				chkPause();
			} catch (AuthenticationException e) {
				message = "Authenticating...";
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				if (stop) {
					close();
					break;
				}
				chkPause();
				if (client != null)
					code = client.getResponseCode();
				if (code == 401) {
					credential = mgr.getCreditential();
				}
				if (credential == null) {
					message = e.getMessage();
					errorCode = CONNECT_ERR;
					mgr.updated();
					msg(e);
					e.printStackTrace();
					close();
					status = FAILED;
					lastError = "Content size invalid";
					break;
				}
			} catch (InvalidContentException e) {
				message = e.getMessage();
				errorCode = CONTENT_ERR;
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				status = FAILED;
				lastError = "Content size invalid";
				break;
			} catch (InvalidReplyException e) {
				message = e.getMessage();
				errorCode = RESP_ERR;
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				status = FAILED;
				lastError = client.statusLine;
				break;
			} catch (ResumeNotSupportedException e) {
				message = e.getMessage();
				errorCode = RESUME_ERR;
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				status = FAILED;
				lastError = "Resume not supported";
				break;
			} catch (Exception e) {
				message = StringResource.getString("CONNECTING");
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				if (stop) {
					close();
					break;
				}
				chkPause();
				msg("Sleeping 2 sec");
				try {
					Thread.sleep(2000);
					chkPause();
				} catch (Exception err) {
				}
			} catch (Error e) {
				message = "Not a valid response";
				errorCode = UNKNOWN_ERR;
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				status = FAILED;
				lastError = "Not a valid response";
				break;
			}
			msg("Remaining " + (this.length - this.downloaded));
			if (stop) {
				close();
				break;
			}
			chkPause();
			client = null;
			clientSet = false;
			try {
				client.close();
			} catch (Exception e) {
			}
		}
		msg("Exiting connect");
		if (!stop) {
			status = FAILED;
			message = StringResource.getString("DISCONNECT");
			mgr.updated();
			mgr.failed(lastError + " ", errorCode);
		}
		return false;
	}

	long getContentLengthFromRange(String r) {
		try {
			String len = r.split("/")[0].split("-")[1];
			return Long.parseLong(len) + 1;
		} catch (Exception e) {
			return -1;
		}
	}

	public void close() {
		System.out.println("*************************STOP********************");
		msg(stop);
		msg(stop ? "STOP " : "" + "Releasing all resource...");
		try {
			// if (!Thread.currentThread().equals(t))
			// t.stop();
		} catch (Exception e) {
		}
		try {
			// in.close();
		} catch (Exception e) {
		}
		try {
			out.close();
		} catch (Exception e) {
		}
		try {
			// client.getConnectionManager().shutdown();
			client.close();
		} catch (Exception e) {
		}
		msg("Releasing all resource...done");
		message = StringResource.getString("DISCONNECT");
	}

	public boolean isEOF() {
		try {
			System.out.println("IS EOF: " + in.read());
			// in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ((read == clen) && (read > 0));
	}
}
