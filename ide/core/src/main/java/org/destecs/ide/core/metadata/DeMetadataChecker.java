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
		Event, Input, Output, SDP
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
						break;
					case Input:
					case Output:
						if(!checkFirstElement(systemName,qualifiedName))
						{							
							validate(linkId,linkInfo , true,systemName, linkType);
						}
						else
						{
							errors.add(new LinkError(linkInfo.getLine(), "Link "
									+ linkId + " should refer to a " + systemName + " intance variable"));
						}
						break;				
					case SDP:
						validate(linkId, links.getLinks().get(linkId), false,
								systemName, linkType);
						break;
	
					}
				}
			}
		}
		System.out.println(errors);
	}

	private boolean checkFirstElement(String systemName, List<String> qualifiedName) {
		return !systemName.equals(qualifiedName.get(0));
	}


	private void validate(String linkId, LinkInfo linkInfo, boolean system,
			String systemName, LinkType linkType) {

		List<String> qualifiedName = linkInfo.getQualifiedName();
		
		
		
		String metadataKey = null;
		List<String> metadataProperties = null;
		if (system) {
			metadataKey = systemName + "." + qualifiedName.get(0);
			metadataProperties = vdmMetadata.get(metadataKey);
			if (metadataProperties != null) {
				metadataProperties = vdmMetadata.get(metadataProperties.get(0)
						+ "." + qualifiedName.get(1));

			}

			if (metadataProperties == null) {
				errors.add(new LinkError(linkInfo.getLine(), "Link " + linkId
						+ " refers to a non-existent variable"));
			} else {

				if (linkType == LinkType.Input || linkType == LinkType.Output) {
					if (!metadataProperties.get(1).equals("variable")) {
						errors.add(new LinkError(linkInfo.getLine(), "Link "
								+ linkId + " does not refer to a variable"));
					}
				}
			}
		} else {
			metadataKey = linkInfo.getBoundedVariable().toString();
			metadataProperties = vdmMetadata.get(metadataKey);
			if (linkType == LinkType.SDP) {
				if (!metadataProperties.get(1).equals("const")) {
					errors.add(new LinkError(linkInfo.getLine(), "Link "
							+ linkId + " does not refer to a value"));
				}
			}

		}

	}

	private LinkType getLinkType(String linkId) {

		if (links == null)
			return null;

		if (links.getInputs().contains(linkId))
			return LinkType.Input;

		if (links.getOutputs().contains(linkId))
			return LinkType.Output;

		if (links.getEvents().contains(linkId))
			return LinkType.Event;

		if (links.getSharedDesignParameters().contains(linkId)) {
			return LinkType.SDP;
		}

		return null;
	}


	public List<LinkError> getErrors() {
		return errors;
	}

}
