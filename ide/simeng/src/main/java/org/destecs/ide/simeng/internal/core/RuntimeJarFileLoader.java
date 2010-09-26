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
