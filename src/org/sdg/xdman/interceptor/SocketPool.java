package org.sdg.xdman.interceptor;

import java.net.Socket;
import java.util.ArrayList;

import org.sdg.xdman.util.Logger;

public class SocketPool {
	static ArrayList<SocketInfo> list = new ArrayList<SocketInfo>();

	public synchronized static Socket getSocket(String host, int port) {
		for (int i = 0; i < list.size(); i++) {
			SocketInfo sock = list.get(i);
			if (sock.host.equals(host) && sock.port == port) {
				list.remove(sock);
				Logger.log("Found existing socket for: " + host + " "
						+ sock.socket);
				return sock.socket;
			}
		}
		return null;
	}

	public synchronized static void putSocket(String host, int port,
			Socket socket) {
		if (socket == null) {
			throw new NullPointerException("Socket being added to pool is null");
		}
		SocketInfo info = new SocketInfo();
		info.host = host;
		info.port = port;
		info.socket = socket;
		list.add(info);
	}

}

final class SocketInfo {
	Socket socket;
	String host;
	int port;
}