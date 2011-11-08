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
package org.destecs.ide.core.metadata;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Vector;

import org.destecs.core.vdmlink.LinkInfo;
import org.destecs.core.vdmlink.Links;
import org.destecs.ide.core.resources.IDestecsProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class DeMetadataChecker {

	private IDestecsProject project = null;
	private boolean isDataLoaded = false;
	private Map<String, List<String>> vdmMetadata = new Hashtable<String, List<String>>();
	private Links links = null;
	private List<LinkError> errors = new Vector<LinkError>();

	enum LinkType {
		Event, Input, Output, SDP,Model
	}

	public DeMetadataChecker(IDestecsProject project, Links vdmlinks) {
		this.project = project;
		this.links = vdmlinks;
		try {
			loadVdmMetadata();
			isDataLoaded = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public boolean hasErrors()
	{
		return errors.size() > 0;
	}
	private void loadVdmMetadata() throws IOException, CoreException {

		Properties props = new Properties();
		IFile file = project.getVdmModelFolder().getFile(".metadata");

		if (file.exists()) {
			props.load(file.getContents());

			for (Entry<Object, Object> entry : props.entrySet()) {
				vdmMetadata.put(entry.getKey().toString(),
						Arrays.asList(entry.getValue().toString().split(",")));
			}
		}
	}

	private String findSystemClass() {
		for (Entry<String, List<String>> entry : vdmMetadata.entrySet()) {
			if (entry.getValue().size() > 0) {
				String first = entry.getValue().get(0);
				if (first.equals("_system")) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	public void checkLinks() {
		if (!isDataLoaded || links == null)
			return;

		String systemName = findSystemClass();

		if (systemName == null) {
			errors.add(new LinkError(0, "Model has no system class"));
			return;
		}

		for (String linkId : links.getLinks().keySet()) {
			LinkType linkType = getLinkType(linkId);
			LinkInfo linkInfo = links.getLinks().get(linkId);
			List<String> qualifiedName = linkInfo.getQualifiedName();
			List<String> metadataProperties = null;
			
			if(qualifiedName.size() < 2)
			{
				errors.add(new LinkError(linkInfo.getLine(), "Link "
						+ linkId + " - the qualified name has to have at least 2 elements."));
				
			}
			else
			{
				if (linkType != null) {
					
					
					switch (linkType) {
					case Event:
						metadataProperties = validate(linkId,linkInfo , true);
						if(metadataProperties == null)
						{
							errors.add(new LinkError(linkInfo.getLine(), "Link " + linkId
									+ " refers to a non-existent operation"));
						}else
						{
							validateLinkTypeWithMetadata(linkType,metadataProperties,linkInfo,linkId);
						}
						break;
					case Input:
					case Output:
					case Model:
						if(!checkFirstElement(systemName,qualifiedName))
						{							
							metadataProperties = validate(linkId,linkInfo , true);
							
						}
						else
						{
							errors.add(new LinkError(linkInfo.getLine(), "Link "
									+ linkId + " should refer to a " + systemName + " intance variable"));
						}
						if(metadataProperties == null)
						{
							errors.add(new LinkError(linkInfo.getLine(), "Link " + linkId
									+ " refers to a non-existent variable"));
						}else
						{
							validateLinkTypeWithMetadata(linkType,metadataProperties,linkInfo,linkId);
						}	
					case SDP:
						metadataProperties =  validate(linkId, links.getLinks().get(linkId), false);						
						if(metadataProperties == null)
						{
							errors.add(new LinkError(linkInfo.getLine(), "Link " + linkId
									+ " refers to a non-existent value"));
						}else
						{
							validateLinkTypeWithMetadata(linkType,metadataProperties,linkInfo,linkId);
						}
					}
					
					
				}
				
			}
			
		}
		System.out.println(errors);
	}

	private void validateLinkTypeWithMetadata(LinkType linkType,
			List<String> metadataProperties, LinkInfo linkInfo, String linkId) {
		
		if(linkType == LinkType.Event)
		{
			if (!metadataProperties.get(0).equals("_operation")) {
				errors.add(new LinkError(linkInfo.getLine(), "Link "
						+ linkId + " does not refer to a operation"));
			}
			else
			{
				if (!metadataProperties.get(1).equals("async")) {
					errors.add(new LinkError(linkInfo.getLine(), "Link "
							+ linkId + " should refer to an async operation"));
				}
			}
		}
		
		if (linkType == LinkType.Input || linkType == LinkType.Output) {
			if (!metadataProperties.get(1).equals("variable")) {
				errors.add(new LinkError(linkInfo.getLine(), "Link "
						+ linkId + " does not refer to a variable"));
			}
		}
	
		if (linkType == LinkType.SDP) {
			if (!metadataProperties.get(1).equals("const")) {
				errors.add(new LinkError(linkInfo.getLine(), "Link "
						+ linkId + " does not refer to a value"));
			}
		}
	}


	private boolean checkFirstElement(String systemName, List<String> qualifiedName) {
		return !systemName.equals(qualifiedName.get(0));
	}


	private List<String> validate(String linkId, LinkInfo linkInfo, boolean system) {

		List<String> qualifiedName = linkInfo.getQualifiedName();
		List<String> metadataProperties = null; 
		
		if(qualifiedName.size() >= 2)
		{
			String metadataKey = qualifiedName.get(0) + "." + qualifiedName.get(1);
			metadataProperties = vdmMetadata.get(metadataKey);
			if (metadataProperties != null) {
				if(qualifiedName.size() > 2)
				{
					metadataProperties = checkRestOfQualifier(qualifiedName.subList(2, qualifiedName.size()),metadataProperties);
				}
			}						
		}		
		return metadataProperties;
	}

	private List<String> checkRestOfQualifier(List<String> rest,
			List<String> metadataProperties) {
						
		for (String element : rest) {
			String newMetaKey = metadataProperties.get(0) + "." + element;
			metadataProperties = vdmMetadata.get(newMetaKey);
			if(metadataProperties == null)
			{
				return null;
			}
		}				
		return metadataProperties;
		
	}


	private LinkType getLinkType(String linkId) {

		if (links == null)
			return null;

		if (links.getInputs().keySet().contains(linkId))
			return LinkType.Input;

		if (links.getOutputs().keySet().contains(linkId))
			return LinkType.Output;

		if (links.getEvents().keySet().contains(linkId))
			return LinkType.Event;

		if (links.getSharedDesignParameters().keySet().contains(linkId)) {
			return LinkType.SDP;
		}

		return LinkType.Model;
	}


	public List<LinkError> getErrors() {
		return errors;
	}

}
