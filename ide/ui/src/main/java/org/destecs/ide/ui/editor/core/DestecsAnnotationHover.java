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
package org.destecs.ide.ui.editor.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.destecs.ide.ui.DestecsUIPlugin;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class DestecsAnnotationHover extends DefaultAnnotationHover {

	
	/**
	 * Creates a new HTML annotation hover.
	 *
	 * @param showLineNumber <code>true</code> if the line number should be shown when no annotation is found
	 * @since 3.4
	 */
	public DestecsAnnotationHover(boolean showLineNumber) {
		super(showLineNumber);
	}

//	/*
//	 * Formats a message as HTML text.
//	 */
//	protected String formatSingleMessage(String message) {
//		StringBuffer buffer= new StringBuffer();
//		HTMLPrinter.addPageProlog(buffer);
//		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent(message));
//		HTMLPrinter.addPageEpilog(buffer);
//		return buffer.toString();
//	}
//
//	/*
//	 * Formats several message as HTML text.
//	 */
//	protected String formatMultipleMessages(List messages) {
//		StringBuffer buffer= new StringBuffer();
//		HTMLPrinter.addPageProlog(buffer);
//		HTMLPrinter.addParagraph(buffer, HTMLPrinter.convertToHTMLContent("Multiple markers found in this line"));
//
//		HTMLPrinter.startBulletList(buffer);
//		Iterator e= messages.iterator();
//		while (e.hasNext())
//			HTMLPrinter.addBullet(buffer, HTMLPrinter.convertToHTMLContent((String) e.next()));
//		HTMLPrinter.endBulletList(buffer);
//
//		HTMLPrinter.addPageEpilog(buffer);
//		return buffer.toString();
//	}
	
	
	
	
	private List<Annotation> getAnnotations(ISourceViewer viewer, int lineNumber) {
		IAnnotationModel model= viewer.getAnnotationModel();
		IDocument document= viewer.getDocument();
		return getAnnotationsAtLine(model, document, lineNumber);
		}
	
	@SuppressWarnings("unchecked")
	private List<Annotation> getAnnotationsAtLine(IAnnotationModel model,
			IDocument document, int lineNumber) {
		List<Annotation> result = new ArrayList<Annotation>();
		Iterator<Annotation> it = model.getAnnotationIterator();
		
		
		while(it.hasNext()){
			Annotation ann = it.next();
			
			Position pos = model.getPosition(ann);
			
			try {
				if(document.getLineOfOffset(pos.offset) == lineNumber)
				{
					result.add(ann);
				}
			} catch (BadLocationException e) {
				DestecsUIPlugin.log("Bad location in annotation hover", e);
			}			
		}
		return result;
	}

	

	public String getHoverInfo(ISourceViewer viewer, int lineNumber) {
		List<Annotation> annotations= getAnnotations(viewer, lineNumber);
		return printAnnotations(annotations);
		}

	private String printAnnotations(List<Annotation> annotations) {
		
		List<String> hoverResult = new ArrayList<String>();
		
		for (Annotation ann : annotations) {
			if(ann instanceof MarkerAnnotation){
				
				MarkerAnnotation mAnn = (MarkerAnnotation) ann;
				IMarker m = mAnn.getMarker();
				
				try {
					String a = (String) m.getAttribute(IMarker.MESSAGE);
					hoverResult.add(a + "  ");					
				} catch (CoreException e) {
					DestecsUIPlugin.log("Error in annotation print", e);
				}
			}
		}
		
		if(hoverResult.size() == 0)
		{
			return null;
		}
		
		if(hoverResult.size() == 1)
		{
			return hoverResult.get(0);
		} else
		{
			StringBuilder s = new StringBuilder();
			s.append("Multiple markers found on this line:\n");
			for (String string : hoverResult) {
				s.append("  - ");
				s.append(string);
				s.append("\n");				
			}
			return s.toString();
		}
		
		
	}
	
}
