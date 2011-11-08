/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.ide.simeng.internal.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * This snippet shows how to create a custom class loader and add jars to the
 * classpath at runtime.
 * 
 * @author mvohra
 * 
 */
public class RuntimeJarFileLoader extends URLClassLoader {
	public RuntimeJarFileLoader(URL[] urls) {
		super(urls);
	}

	/**
	 * add the Jar file to the classpath
	 * 
	 * @param path
	 * @throws MalformedURLException
	 */
	public void addFile(String path) throws MalformedURLException {
		// construct the jar url path
		String urlPath = "jar:file:" + path + "!/";

		// invoke the base method
		addURL(new URL(urlPath));
	}

	/**
	 * add the Jar file to the classpath
	 * 
	 * @param path
	 * @throws MalformedURLException
	 */
	public void addFile(String paths[]) throws MalformedURLException {
		if (paths != null)
			for (int i = 0; i < paths.length; i++)
				addFile(paths[i]);
	}

	/**
	 * Main method
	 * 
	 * @param args
	 */
//	public static void main(String args[]) {
//
//		String classToLoad = "com.mysql.jdbc.Driver";
//		try {
//			System.out.println("First attempt, try to load the class...");
//
//			// try to load the class, not yet in the classpath
//			Class.forName(classToLoad);
//
//		} catch (Exception ex) {
//			System.out.println("Failed to load : " + ex.getMessage());
//			ex.printStackTrace();
//		}
//
//		try {
//			// initialize with empty path
//			URL urls[] = {};
//
//			// create instance
//			RuntimeJarFileLoader loader = new RuntimeJarFileLoader(urls);
//
//			// linux
//			// String jarPath = "/opt/app/lib/mysql-connector-java-5.0.5.jar";
//
//			// cygwin
//			// String jarPath =
//			// "/cygdrive/c/workspace/livrona/projects/prototype/lib/mysql-connector-java-5.0.5.jar";
//
//			// widows
//			String jarPath = "c:/workspace/livrona/projects/prototype/lib/mysql-connector-java-5.0.5.jar";
//
//			loader.addFile(jarPath);
//			System.out.println("Second attempt...");
//
//			// load the class
//			loader.loadClass(classToLoad);
//			System.out.println("Success");
//		} catch (Exception ex) {
//			System.out.println("Failed to load : " + ex.getMessage());
//			ex.printStackTrace();
//		}
//	}
}
