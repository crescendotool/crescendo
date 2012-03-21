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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.destecs.ide.simeng.ISimengConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.ManifestElement;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;


public class ClasspathUtils {

	public static void collectClasspath(String[] bundleIds, List<String> entries) {
		try {
			final boolean developmentMode = Platform.inDevelopmentMode();
			ArrayList<File> fileList = new ArrayList<File>();
			for (int i = 0; i < bundleIds.length; ++i) {
				final String bundleId = bundleIds[i];
				getPluginClassPath(bundleId, entries);
				final File file = getPluginLocation(bundleId);
				fileList.add(file);
				for (File f : fileList) {
					if (developmentMode && file.isDirectory()) {
						final File bin = new File(f, "bin"); //$NON-NLS-1$
						if (bin.isDirectory()) {
							entries.add(bin.getAbsolutePath());
							continue;
						}
					}
					entries.add(f.getAbsolutePath());
				}
			}
		} catch (CoreException e) {
//			LaunchingPlugin.error(e);
		}
	}
	
	private static void getPluginClassPath(String bundleId, List<String> entries)
	{
		try {
			final Bundle bundle = Platform.getBundle(bundleId);
			if (bundle == null){
				throw new BundleException(bundleId + " cannot be retrieved from the Platform");
			}
			
			String requires = (String) bundle.getHeaders().get((Constants.BUNDLE_CLASSPATH));
			if (requires == null) 
				requires = ".";	
			ManifestElement[] elements = ManifestElement.parseHeader(Constants.REQUIRE_BUNDLE, requires);
			for (ManifestElement manifestElement : elements) {
				String value = manifestElement.getValue();
				if (".".equals(value))
					value = "/";
				
				URL url = bundle.getEntry(value);
				if (url != null)
				{
					URL resolvedFile = FileLocator.resolve(url);
					if ("file".equals(resolvedFile.getProtocol())) //$NON-NLS-1$
					{
						File file = new File(resolvedFile.getPath());
						// adding jar-files
						if (file.isDirectory() == false)
						{
							entries.add(file.getAbsolutePath());
						}
						//System.out.println("addedFile" + resolvedFile.getPath() + " isDir: " + file.isDirectory());
					}
				}
			}
		} catch (BundleException e) {
			final String msg = NLS.bind(PLUGIN_LOCATION_ERROR, bundleId);
			System.out.println(msg);
		} catch (IOException e)
		{
			final String msg = NLS.bind(PLUGIN_LOCATION_ERROR, bundleId);
			System.out.println(msg);
		}
		
	}

	
	private static File getPluginLocation(String bundleId) throws CoreException {
		try {
			final Bundle bundle = Platform.getBundle(bundleId);			
			return getBundleFile(bundle);
		} catch (IOException e) {
			final String msg = NLS.bind(PLUGIN_LOCATION_ERROR, bundleId);
			throw new CoreException(new Status(IStatus.ERROR, ISimengConstants.PLUGIN_ID, IStatus.ERROR, msg, e));
		}
	}

	/**
	 * Returns a file for the contents of the specified bundle. Depending on how
	 * the bundle is installed the returned file may be a directory or a jar
	 * file containing the bundle content.
	 * 
	 * XXX 3.3 compatibility = {@link FileLocator#getBundleFile(Bundle)}
	 * 
	 * @param bundle
	 *            the bundle
	 * @return a file with the contents of the bundle
	 * @throws IOException
	 *             if an error occurs during the resolution
	 */
	public static File getBundleFile(Bundle bundle) throws IOException {
		URL rootEntry = bundle.getEntry("/"); //$NON-NLS-1$
		rootEntry = FileLocator.resolve(rootEntry);
		if ("file".equals(rootEntry.getProtocol())) //$NON-NLS-1$
			return new File(rootEntry.getPath());
		if ("jar".equals(rootEntry.getProtocol())) { //$NON-NLS-1$
			String path = rootEntry.getPath();
			if (path.startsWith("file:")) {
				// strip off the file: and the !/
				path = path.substring(5, path.length() - 2);
				return new File(path);
			}
		}
		throw new IOException("Unknown protocol"); //$NON-NLS-1$
	}
	

	private static final String PLUGIN_LOCATION_ERROR = "Error determining classpath from bundle {0}"; //$NON-NLS-1$
}
