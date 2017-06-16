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

public interface IXDMConstants {
	public static final int CONNECTING = 10, DOWNLOADING = 20, FAILED = 30, STOPPED = 40, COMPLETE = 50,
			ASSEMBLING = 60, REDIRECTING = 70;
	public static final String COMPRESSED = "Compressed", DOCUMENTS = "Documents", MUSIC = "Music",
			PROGRAMS = "Programs", VIDEO = "Video", OTHER = "Other";
	public static final int CONNECT_ERR = 0, CONTENT_ERR = 1, SESSION_ERR = 2, RESP_ERR = 3, RESUME_ERR = 4,
			UNKNOWN_ERR = 5, AUTH_ERR = 6, PROXY_AUTH_ERR = 7;
	public static final String errMsg[] = { "CONNECT_ERR", // "Connection
															// Error\nCould not
															// connect to
															// Server.",
			"FILE_CHANGED", // "File has been changed on the Server\n"
			// + "You have to 'Restart' the Download.",
			"SESSION_END", // "It looks like your download session has been
							// expired."
			// + "\nYou can resume the download using 'Refresh Link' option",
			"RESP_ERR", // "Server sent an Invalid Response.",
			"NO_RESUME", // "It looks like Server does not support resume
							// feature.\n"
			// + "You have to 'Restart' the Download",
			"DWN_ERR", // "Download Error.",
			"HTTP_AUTH_ERR", // "Authentication failed",
			"PROXY_AUTH_FAILED"// "Proxy Authentication failed"
	};
	public static final int HLS = 2, HTTP = 1, DASH = 3, HDS = 4;
}
