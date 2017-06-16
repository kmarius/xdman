/* jcifs smb client library in Java
 * Copyright (C) 2000  "Michael B. Allen" <jcifs at samba dot org>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package jcifs;

import java.util.Properties;

/**
 * This class uses a static {@link java.util.Properties} to act as a cental
 * repository for all jCIFS configuration properties. It cannot be instantiated.
 * Similar to <code>System</code> properties the namespace is global therefore
 * property names should be unique. Before use, the <code>load</code> method
 * should be called with the name of a <code>Properties</code> file (or
 * <code>null</code> indicating no file) to initialize the <code>Config</code>.
 * The <code>System</code> properties will then populate the
 * <code>Config</code> as well potentially overwriting properties from the
 * file. Thus properties provided on the commandline with the
 * <code>-Dproperty.name=value</code> VM parameter will override properties
 * from the configuration file.
 * <p>
 * There are several ways to set jCIFS properties. See the <a
 * href="../overview-summary.html#scp">overview page of the API documentation</a>
 * for details.
 */

public class Config {

	/**
	 * The static <code>Properties</code>.
	 */

	private static Properties prp = new Properties();

	// supress javadoc constructor summary by removing 'protected'
	Config() {
	}

	public static void setProperties(Properties prp) {
		Config.prp = new Properties(prp);
		try {
			Config.prp.putAll(System.getProperties());
		} catch (SecurityException se) {
		}
	}

	
	public static Object setProperty(String key, String value) {
		return prp.setProperty(key, value);
	}

	/**
	 * Retrieve a property as an <code>Object</code>.
	 */

	public static Object get(String key) {
		return prp.get(key);
	}

	/**
	 * Retrieve a <code>String</code>. If the key cannot be found, the
	 * provided <code>def</code> default parameter will be returned.
	 */

	public static String getProperty(String key, String def) {
		return prp.getProperty(key, def);
	}

	/**
	 * Retrieve a <code>String</code>. If the property is not found,
	 * <code>null</code> is returned.
	 */

	public static String getProperty(String key) {
		return prp.getProperty(key);
	}

	/**
	 * Retrieve an <code>int</code>. If the key does not exist or cannot be
	 * converted to an <code>int</code>, the provided default argument will
	 * be returned.
	 */

	public static int getInt(String key, int def) {
		String s = prp.getProperty(key);
		if (s != null) {
			try {
				def = Integer.parseInt(s);
			} catch (NumberFormatException nfe) {
				
			}
		}
		return def;
	}

	/**
	 * Retrieve an <code>int</code>. If the property is not found,
	 * <code>-1</code> is returned.
	 */

	public static int getInt(String key) {
		String s = prp.getProperty(key);
		int result = -1;
		if (s != null) {
			try {
				result = Integer.parseInt(s);
			} catch (NumberFormatException nfe) {
				
			}
		}
		return result;
	}

	/**
	 * Retrieve a <code>long</code>. If the key does not exist or cannot be
	 * converted to a <code>long</code>, the provided default argument will
	 * be returned.
	 */

	public static long getLong(String key, long def) {
		String s = prp.getProperty(key);
		if (s != null) {
			try {
				def = Long.parseLong(s);
			} catch (NumberFormatException nfe) {
				
			}
		}
		return def;
	}

	

	/**
	 * Retrieve a boolean value. If the property is not found, the value of
	 * <code>def</code> is returned.
	 */

	public static boolean getBoolean(String key, boolean def) {
		String b = getProperty(key);
		if (b != null) {
			def = b.toLowerCase().equals("true");
		}
		return def;
	}

	
}
