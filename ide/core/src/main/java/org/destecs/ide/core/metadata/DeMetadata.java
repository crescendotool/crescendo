package org.destecs.ide.core.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.destecs.core.vdmlink.Links;
import org.destecs.core.vdmlink.StringPair;
import org.destecs.ide.core.resources.IDestecsProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class DeMetadata {

	private Map<String, List<String>> vdmMetadata = new Hashtable<String, List<String>>();
	private Links links = null;
	private IDestecsProject project;
	private List<String> errorMsgs = new ArrayList<String>();
	private String systemClass = null;

	public DeMetadata(Links links, IDestecsProject project) {
		this.links = links;
		this.project = project;
	}

	private void loadVdmMetadata(IDestecsProject p) throws IOException,
			CoreException {

		Properties props = new Properties();
		IFile file = p.getVdmModelFolder().getFile(".metadata");

		if (file.exists()) {
			props.load(file.getContents());

			for (Entry<Object, Object> entry : props.entrySet()) {
				vdmMetadata.put(entry.getKey().toString(),
						Arrays.asList(entry.getValue().toString().split(",")));
			}
		}

	}

	public synchronized void checkLinks() {
		try {
			loadVdmMetadata(project);
			findAndClearSystem();
			matchLinksAndMetaData();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void findAndClearSystem() {

		for (String key : vdmMetadata.keySet()) {
			List<String> clas = vdmMetadata.get(key);

			if (clas.size() > 0) {
				String className = clas.get(0);
				if (className.equals("system")) {
					systemClass = key + ".";
				}
			}
		}

		for (String key : new HashSet<String>(vdmMetadata.keySet())) {

			List<String> clas = vdmMetadata.get(key);
			if (clas.size() > 0 && !clas.get(0).equals("real")) {
				vdmMetadata.remove(key);
			} else {
				if (key.startsWith(systemClass)) {
					vdmMetadata.remove(key);
					String res = key.replace(systemClass, "");
					vdmMetadata.put(res, clas);
				}
			}
		}

		printMetadata();

	}

	private void matchLinksAndMetaData() {

		for (String key : links.getLinks().keySet()) {
			StringPair p = links.getLinks().get(key);

			if (!vdmMetadata.containsKey(p.toString())) {
				System.out.println(p.toString()
						+ " not present in the metadata");
				errorMsgs
						.add(p.toString()
								+ " does not exist in the VDM model or it is not at real number");
			}
		}
	}

	public List<String> getErrorMsgs() {
		return errorMsgs;
	}

	private void printMetadata() {
		System.out.println("---- PRINTING TRIMMED METADATA ----");
		for (String key : vdmMetadata.keySet()) {
			System.out.println(key + ": " + vdmMetadata.get(key));
		}
	}

}
