package org.sdg.xdman.interceptor;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;

public class XDMServer implements Runnable {

	ServerSocket server;

	XDMConfig config;

	DownloadStateListner mgr;
	IMediaGrabber gbr;

	public XDMServer(XDMConfig config, DownloadStateListner mgr,
			IMediaGrabber gbr) {
		this.config = config;
		this.mgr = mgr;
		this.gbr = gbr;
	}

	public void sendParams(HashMap<String, String> args) {
		try {
			Socket sock = new Socket("127.0.0.1", 9614);
			OutputStream out = sock.getOutputStream();
			out.write("PARAM\r\n".getBytes());
			Set<String> keys = args.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				String key = it.next();
				out.write((key + ": " + args.get(key) + "\r\n").getBytes());
				System.out.println((key + ": " + args.get(key) + "\r\n"));
			}
			out.write("\r\n".getBytes());
			out.flush();
			out.close();
			sock.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean start() {
		try {
			server = new ServerSocket(9614, Integer.MAX_VALUE, InetAddress
					.getByName("127.0.0.1"));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		new Thread(this).start();
		return true;
	}

	public void run() {
		try {
			while (true) {

				Socket sock = server.accept();
				Intercepter interceptor = new Intercepter(sock, config, mgr,
						gbr);
				new Thread(interceptor).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
