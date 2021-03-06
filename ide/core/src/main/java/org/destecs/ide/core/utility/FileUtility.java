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
package org.destecs.ide.core.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.destecs.ide.core.DestecsCorePlugin;
import org.destecs.ide.core.IDestecsCoreConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class FileUtility
{
	public static void addMarker(IFile file, String message, int lineNumber,
			int severity)
	{
		try
		{
			if (file == null)
				return;
			lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& (marker.getAttribute(IMarker.LINE_NUMBER)!=null && marker.getAttribute(IMarker.LINE_NUMBER).equals(lineNumber)))
					return;

			}
	 		IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, IDestecsCoreConstants.PLUGIN_ID);
			marker.setAttribute(IMarker.LOCATION, "line: " + lineNumber);

			if (lineNumber == -1)
			{
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e)
		{
			DestecsCorePlugin.log("FileUtility addMarker", e);
		}
	}

	public static void addMarker(IFile file, String message, int lineNumber,
			int columnNumber, int severity)
	{
		try
		{
			if (file == null)
				return;
			lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE) != null
						&& marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY) != null
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER) != null
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(lineNumber))
					return;

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, IDestecsCoreConstants.PLUGIN_ID);
			marker.setAttribute(IMarker.LOCATION, "line: " + lineNumber);

			SourceLocationConverter converter = new SourceLocationConverter(getContent(file));
			marker.setAttribute(IMarker.CHAR_START, converter.getStartPos(lineNumber, columnNumber));
			marker.setAttribute(IMarker.CHAR_END, converter.getEndPos(lineNumber, columnNumber));
		} catch (CoreException e)
		{
			DestecsCorePlugin.log("FileUtility addMarker", e);
		}
	}

	public static void addMarker(IFile file, String message, int lineNumber,
			int columnNumber, int severity, String content)
	{
		try
		{
			if (file == null)
				return;
			// lineNumber -= 1;
			IMarker[] markers = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.MESSAGE) != null
						&& marker.getAttribute(IMarker.MESSAGE).equals(message)
						&& marker.getAttribute(IMarker.SEVERITY) != null
						&& marker.getAttribute(IMarker.SEVERITY).equals(severity)
						&& marker.getAttribute(IMarker.LINE_NUMBER) != null
						&& marker.getAttribute(IMarker.LINE_NUMBER).equals(lineNumber))
					return;

			}
			IMarker marker = file.createMarker(IMarker.PROBLEM);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			marker.setAttribute(IMarker.SOURCE_ID, IDestecsCoreConstants.PLUGIN_ID);
			marker.setAttribute(IMarker.LOCATION, "line: " + lineNumber);

			SourceLocationConverter converter = new SourceLocationConverter(content.toCharArray());
			if (lineNumber == 0 && columnNumber == -1)
			{
				marker.setAttribute(IMarker.LINE_NUMBER, converter.getLineCount());
				marker.setAttribute(IMarker.LOCATION, "line: " + converter.getLineCount());
			} else
			{
				marker.setAttribute(IMarker.CHAR_START, converter.getStartPos(lineNumber, columnNumber));
				marker.setAttribute(IMarker.CHAR_END, converter.getEndPos(lineNumber, columnNumber));
			}
		} catch (CoreException e)
		{
			DestecsCorePlugin.log("FileUtility addMarker", e);
		}
	}

	public static void deleteMarker(IFile file, String type, String sourceId)
	{
		try
		{
			if (file == null)
				return;

			IMarker[] markers = file.findMarkers(type, true, IResource.DEPTH_INFINITE);
			for (IMarker marker : markers)
			{
				if (marker.getAttribute(IMarker.SOURCE_ID) != null
						&& marker.getAttribute(IMarker.SOURCE_ID).equals(sourceId))
					marker.delete();
			}
		} catch (CoreException e)
		{
			if (DestecsCorePlugin.DEBUG)
			{
				DestecsCorePlugin.log("FileUtility deleteMarker", e);
			}
		}
	}

	// public static void gotoLocation(IFile file, LexLocation location,
	// String message) {
	// try {
	//
	// IWorkbench wb = PlatformUI.getWorkbench();
	// IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
	//
	// IEditorPart editor = IDE.openEditor(win.getActivePage(), file, true);
	//
	// IMarker marker = file.createMarker(IMarker.MARKER);
	// marker.setAttribute(IMarker.MESSAGE, message);
	// marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
	//
	// SourceLocationConverter converter = new SourceLocationConverter(getContent(file));
	// marker.setAttribute(IMarker.CHAR_START,converter.getStartPos( location));
	// marker.setAttribute(IMarker.CHAR_END,converter.getEndPos(location));
	// // marker.setAttribute(IMarker.LINE_NUMBER, location.startLine);
	// // System.out.println("Marker- file: " + file.getName() + " ("
	// // + location.startLine + "," + location.startPos + "-"
	// // + location.endPos + ")");
	//
	// IDE.gotoMarker(editor, marker);
	//
	// marker.delete();
	//
	// } catch (CoreException e) {
	//
	// e.printStackTrace();
	// }
	// }

	public static List<Character> getContent(IFile file)
	{

		InputStream inStream;
		InputStreamReader in = null;
		List<Character> content = new Vector<Character>();
		try
		{
			if (!file.isSynchronized(IResource.DEPTH_ONE))
			{
				file.refreshLocal(IResource.DEPTH_ONE, null);
			}
			inStream = file.getContents();
			in = new InputStreamReader(inStream, file.getCharset());

			int c = -1;
			while ((c = in.read()) != -1)
				content.add((char) c);

		} catch (Exception e)
		{
			DestecsCorePlugin.log("FileUtility getContent", e);
		} finally
		{
			if (in != null)
				try
				{
					in.close();
				} catch (IOException e)
				{
				}
		}

		return content;

	}

	public static char[] getCharContent(List<Character> content)
	{
		char[] source = new char[content.size()];
		for (int i = 0; i < content.size(); i++)
		{
			source[i] = content.get(i);
		}
		return source;
	}
}
