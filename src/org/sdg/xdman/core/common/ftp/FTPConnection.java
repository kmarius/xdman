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

package org.sdg.xdman.core.common.ftp;

import java.net.URI;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.sdg.xdman.core.common.Authenticator;
import org.sdg.xdman.core.common.Connection;
import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.core.common.Credential;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.gui.StringResource;

public class FTPConnection extends Connection {
	XDMConfig config;
	String dir, file;
	int port;
	String host, path;
	FTPClient client;
	int count;
	int errorCode;

	public FTPConnection(String url, String fileName, long startOff, long length, long contentLength, int timeout,
			ConnectionManager mgr, Object lock, Credential c, XDMConfig config) {
		super(url, fileName, startOff, length, contentLength, mgr, lock);
		this.credential = c;
		this.config = config;
	}

	public FTPConnection(State state, int timeout, ConnectionManager mgr, Object lock, Credential c, XDMConfig config) {
		super(state, timeout, mgr, lock);
		this.credential = c;
		this.config = config;
	}

	@Override
	public void close() {
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
			client.disconnect();
		} catch (Exception e) {
		}
		msg("Releasing all resource...done");
		message = StringResource.getString("DISCONNECT");// "disconnect";
	}

	@Override
	public boolean connect() {
		// TODO Auto-generated method stub
		try {
			URI ftpuri = new URI(url);
			host = ftpuri.getHost();
			port = ftpuri.getPort();
			path = ftpuri.getPath();
			msg("Path: " + path);
			getPath();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		status = CONNECTING;
		while (true) {
			read = 0;
			clen = 0;
			if (stop) {
				close();
				break;
			}
			chkPause();
			try {
				if (file == null || file.length() < 1) {
					throw new Error("No file to download");
				}
				message = StringResource.getString("CONNECTING");// "Connecting...";
				mgr.updated();
				msg("Connecting...");
				if (length > 0)
					if ((startOff + downloaded) - (startOff + length - 1) > 0) {
						mgr.donwloadComplete(this);
						return true;
					}
				if (config.useProxy) {
					String proxyHost = config.proxyHost;
					int proxyPort = config.proxyPort;
					String proxyUser = config.proxyUser;
					String proxyPass = config.proxyPass;
					boolean proxyAuth = false;
					if (!(proxyUser == null || proxyUser.length() < 1)) {
						if (!(proxyPass == null || proxyPass.length() < 1)) {
							proxyAuth = true;
						}
					}
					if (proxyAuth) {
						msg("Authenticating with: " + proxyUser + ":" + proxyPass);
						client = new FTPHTTPClient(proxyHost, proxyPort, proxyUser, proxyPass);
					} else {
						client = new FTPHTTPClient(proxyHost, proxyPort);
					}
				} else {
					client = new FTPClient();
				}
				// client = new DefaultHttpClient(
				// new BasicClientConnectionManager());
				// HttpParams params = new BasicHttpParams();
				// params.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
				// params.setParameter(CoreConnectionPNames.SO_TIMEOUT,
				// timeout);
				// client.setParams(params);
				// HttpGet get = new HttpGet(url);
				msg("Connecting to..." + url);
				message = StringResource.getString("CONNECTING");
				mgr.updated();
				client.setConnectTimeout(config.timeout * 1000);
				client.setDataTimeout(config.timeout * 1000);
				if (port > 0)
					client.connect(host, port);
				else
					client.connect(host);
				if (stop) {
					close();
					break;
				}
				chkPause();
				if (credential == null) {
					credential = Authenticator.getInstance().getCredential(host);
				}
				message = "Logging in...";
				mgr.updated();
				if (credential != null) {
					msg("Loggin in with specifig user/pass");
					client.login(credential.user, credential.pass);
				} else {
					msg("Loggin in with anonymous user/pass");
					client.login("anonymous", "anonymous");
				}
				if (stop) {
					close();
					break;
				}
				chkPause();
				int reply = client.getReplyCode();
				msg("Reply: " + client.getReplyString());
				if (!FTPReply.isPositiveCompletion(reply)) {
					credential = mgr.getCreditential();
					if (credential == null) {
						throw new Error(client.getReplyString());
					} else
						throw new IllegalArgumentException(client.getReplyString());
				}
				msg("Switching to binary mode...");
				client.setFileType(FTPClient.BINARY_FILE_TYPE);
				reply = client.getReplyCode();
				msg("Reply: " + client.getReplyString());
				if (!FTPReply.isPositiveCompletion(reply)) {
					throw new Error("Binary transfer not supported by server");
				}
				msg("Entering passive mode: " + dir);
				message = "PASVr...";
				mgr.updated();
				client.enterLocalPassiveMode();
				if (stop) {
					close();
					break;
				}
				msg("Changing working dir to: " + dir);
				message = "Change dir...";
				mgr.updated();
				client.changeWorkingDirectory(dir);
				if (stop) {
					close();
					break;
				}
				chkPause();
				reply = client.getReplyCode();
				msg("Reply: " + client.getReplyString());
				if (!FTPReply.isPositiveCompletion(reply)) {
					throw new Error("Could not switch to the sprecified directory.");
				}
				if (length > 0) {
					long restartOff = (startOff + downloaded);
					msg("Sending range");
					message = "Sending REST...";
					mgr.updated();
					client.setRestartOffset(restartOff);
				} else {
					msg("Listing files");
					FTPFile files[] = client.listFiles(dir);
					chkPause();
					reply = client.getReplyCode();
					msg("Reply: " + client.getReplyString());
					if (!FTPReply.isPositiveCompletion(reply)) {
						throw new Error("File information could not be retrived.");
					}
					for (int i = 0; i < files.length; i++) {
						FTPFile f = files[i];
						msg(f.toString() + " Name: " + f.getName());
						if (f.getName().equals(file)) {
							length = f.getSize();
							msg("Length found: " + length);
							break;
						}
					}
					msg("Listing files...done");
				}
				if (stop) {
					close();
					break;
				}
				chkPause();
				reply = client.getReplyCode();
				msg("Reply: " + client.getReplyString());
				if (!FTPReply.isPositiveCompletion(reply)) {
					throw new Error("Server does not support resume feature.");
				}
				msg("SEND GET...");
				message = "Send GET...";
				// client.sendGET();
				// HttpResponse response = client.execute(get);
				msg("SEND GET...Done");
				if (stop) {
					close();
					break;
				}
				chkPause();
				message = "Parsing response...";
				mgr.updated();
				chkPause();
				message = "Opening data connection...";
				mgr.updated();
				msg("Opening Stream for: " + file);
				in = client.retrieveFileStream(file);
				msg("Data connection mode: " + client.getDataConnectionMode());
				if (in == null) {
					throw new Error("Server did not sent any data.");
				}
				if (stop) {
					close();
					break;
				}
				chkPause();
				status = DOWNLOADING;
				message = "Downloading...";
				buf = new byte[8192];
				mgr.updated();
				msg("Notify...");
				msg("Going to call connected()...");
				mgr.connected(this);
				msg("Returned from connected()");
				return true;
			} catch (IllegalArgumentException e) {
				message = "ReConnecting...";
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				if (stop) {
					close();
					break;
				}
				chkPause();
			} catch (UnknownHostException e) {
				message = "Connecting...";
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				if (count > 10) {
					status = FAILED;
					errorCode = CONNECT_ERR;
					lastError = "Host not found";
					break;
				}
				if (stop) {
					close();
					break;
				}
				chkPause();
				msg("Sleeping 2 sec");
				message = "Disconnect";
				try {
					Thread.sleep(2000);
					chkPause();
				} catch (Exception err) {
				}
				message = "Connecting...";
				mgr.updated();
				count++;
			} catch (Exception e) {
				message = "ReConnecting...";
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
				message = "Invalid response.";
				errorCode = UNKNOWN_ERR;
				lastError = e.getMessage();
				mgr.updated();
				msg(e);
				e.printStackTrace();
				close();
				status = FAILED;
				if (lastError == null || lastError.length() < 1)
					lastError = "Invalid response from server";
				break;
			}
			msg("Remaining " + (this.length - this.downloaded));
			if (stop) {
				close();
				break;
			}
			chkPause();
		}
		msg("Exiting connect");
		if (!stop) {
			message = "disconnect.";
			mgr.updated();
			mgr.failed(lastError, errorCode);
		}
		return false;

	}

	@Override
	public boolean isEOF() {
		// TODO Auto-generated method stub
		return false;
	}

	void getPath() {
		int pos = path.lastIndexOf("/");
		if (pos < 0)
			return;
		dir = path.substring(0, pos);
		if (dir.length() < 1)
			dir = "/";
		if (pos == path.length() - 1)
			return;
		if (pos < path.length() - 1) {
			file = path.substring(pos + 1);
		}
	}
}
