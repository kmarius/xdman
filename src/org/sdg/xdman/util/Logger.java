package org.sdg.xdman.util;

import java.io.PrintStream;

public class Logger {
	static PrintStream out = System.out;

	public static void setLogStream(PrintStream ps) {
		out = ps;
	}

	public static void log(Object obj) {
		if (obj instanceof Throwable) {
			((Throwable) obj).printStackTrace();
		} else {
			out.println(obj);
		}
	}
}
