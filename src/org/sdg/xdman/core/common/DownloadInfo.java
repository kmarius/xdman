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

import java.io.File;
import java.util.UUID;

public class DownloadInfo {
	public UUID id;
	public String url;
	public String file;
	public String status;
	public String length;
	public String downloaded;
	public String speed;
	public String eta;
	public String resume;
	public long startoff[], len[], dwn[];
	public long rlen, rdwn;
	public int prg;
	public String stat[];
	public String dwnld[];
	public int state;
	public String msg;
	public String progress;
	public String category;
	public String tempdir;
	public File path;
}
