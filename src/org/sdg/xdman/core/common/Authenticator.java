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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Observable;

public class Authenticator extends Observable implements Serializable {
	private static final long serialVersionUID = -7089181343138243013L;

	public static HashMap<String, Credential> auth = new HashMap<String, Credential>();

	static Authenticator me;
	File file;

	private Authenticator() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public void load(File file) {
		me.file = file;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			auth = (HashMap<String, Credential>) in.readObject();
			System.out.println(auth);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		me = new Authenticator();
		me.file = file;
	}

	public void save() {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(me.file));
			out.writeObject(auth);
			System.out.println(auth);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

	public static Authenticator getInstance() {
		if (me == null)
			me = new Authenticator();
		return me;
	}

	public Credential getCredential(String host) {
		return auth.get(host);
	}

	public void addCreditential(Credential c) {
		auth.put(c.host, c);
		setChanged();
		notifyObservers();
	}

	public void removeCreditential(String host) {
		auth.remove(host);
		setChanged();
		notifyObservers();
	}
}
