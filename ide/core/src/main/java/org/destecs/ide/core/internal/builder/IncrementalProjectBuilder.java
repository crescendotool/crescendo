package org.destecs.ide.core.internal.builder;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.core.contract.Contract;
import org.destecs.core.contract.IVariable;
import org.destecs.core.contract.Variable;
import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.parsers.IError;
import org.destecs.core.parsers.ParserWrapper;
import org.destecs.core.parsers.VdmLinkParserWrapper;
import org.destecs.core.vdmlink.Links;

import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.metadata.DeMetadata;
import org.destecs.ide.core.metadata.DeMetadataChecker;
import org.destecs.ide.core.metadata.LinkError;
import org.destecs.ide.core.resources.DestecsModel;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.core.utility.FileUtility;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class IncrementalProjectBuilder extends
		org.eclipse.core.resources.IncrementalProjectBuilder
{

	@Override
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
			throws CoreException
	{
		IResourceDelta delta = getDelta(getProject());
		if (!getProject().hasNature(IDestecsCoreConstants.NATURE) || (delta!=null && delta.getAffectedChildren().length==0))
		{
			return null;
		}
		
//		System.out.println("Project: "+getProject().getName()+"-"+getDelta(getProject()).getAffectedChildren().length);

		boolean isOk = true;
		boolean isChecked = true;
		
		IDestecsProject project = (IDestecsProject) getProject().getAdapter(IDestecsProject.class);
		final DestecsModel model = project.getModel();
		try
		{
			if (!project.getContractFile().exists())
			{
				model.setContract(null);
				isOk = false;
				return null;
			}
			ContractParserWrapper contractParser = new ContractParserWrapper();
			Contract contract = (Contract) parse(contractParser, project.getContractFile());

			if (!typeCheck(project.getContractFile(), contract))
			{
				model.setContract(null);
				isOk = false;
				return null;
			}
			model.setContract(contract);

			if (!project.getVdmLinkFile().exists())
			{
				model.setLinks(null);
				isOk = false;
				return null;
			}
			VdmLinkParserWrapper vdmLinkParser = new VdmLinkParserWrapper();
			Links vdmlinks = (Links) parse(vdmLinkParser, project.getVdmLinkFile());

 			if (!typeCheck(project.getVdmLinkFile(), vdmlinks, contract))
			{
				model.setLinks(null);
				isOk = false;
				return null;
			}

			model.setLinks(vdmlinks);

			DeMetadataChecker checker = new DeMetadataChecker(project,vdmlinks);
			checker.checkLinks();
			if(checker.hasErrors())
			{
				for (LinkError error : checker.getErrors()) {
					addError(project.getVdmLinkFile(), error.getLine() + 1, error.getReason());
				}
			}
			
			
//			//TODO: The check below has some issues with the build order
//			DeMetadata deMetadata = new DeMetadata(vdmlinks,project);
//			deMetadata.checkLinks();
//			for (String err : deMetadata.getErrorMsgs()) {
//				addError(project.getVdmLinkFile(), err);
//			}
			
		} catch (Exception e)
		{
			isOk = false;
			isChecked = false;
			e.printStackTrace();
			//TODO build with errors, set project state to build and error
		}

		model.setChecked(isChecked);
		model.setOk(isOk);
		
		return null;
	}

	private boolean typeCheck(IFile file, Links vdmlinks, Contract contract)
	{
		boolean faild = false;
		List<String> tmp = new Vector<String>();

		// check outputs
		tmp.addAll(vdmlinks.getOutputs().keySet());
		for (IVariable var : contract.getControlledVariables())
		{
			if (!vdmlinks.getOutputs().keySet().contains(var.getName()))
			{
				addError(file, 0, "Missing-output controlled variable: " + var);
				faild = true;
			}

			if (!vdmlinks.getLinks().containsKey(var.getName()))
			{
				addError(file,0,  "Missing-output controlled variable link: "
						+ var);
				faild = true;
			}

			if (tmp.contains(var.getName()))
			{
				tmp.remove(var.getName());
			}
		}

		if (tmp.size() > 0)
		{
			addError(file, 0, "Too many outputs defined; no usage found for: "
					+ tmp);
			faild = true;
		}
		tmp.clear();

		// check inputs
		tmp.addAll(vdmlinks.getInputs().keySet());
		for (IVariable var : contract.getMonitoredVariables())
		{
			if (!vdmlinks.getInputs().keySet().contains(var.getName()))
			{
				addError(file,0, "Missing-input monitored variable: " + var);
				faild = true;
			}
			if (!vdmlinks.getLinks().containsKey(var.getName()))
			{
				addError(file,0, "Missing-input monitored variable link: " + var);
				faild = true;
			}

			if (tmp.contains(var.getName()))
			{
				tmp.remove(var.getName());
			}
		}

		if (tmp.size() > 0)
		{
			addError(file,0, "Too many inputs defined; no usage found for: "
					+ tmp);
			faild = true;
		}
		tmp.clear();

		// check events
		tmp.addAll(vdmlinks.getEvents().keySet());
		for (String event : contract.getEvents())
		{
			if (!vdmlinks.getEvents().keySet().contains(event))
			{
				addError(file,0, "Missing-event: " + event);
				faild = true;
			}

			if (!vdmlinks.getLinks().containsKey(event))
			{
				addError(file,0, "Missing-event link: " + event);
				faild = true;
			}

			if (tmp.contains(event))
			{
				tmp.remove(event);
			}
		}

		if (tmp.size() > 0)
		{
			addError(file,0, "Too many events defined; no usage found for: "
					+ tmp);
			faild = true;
		}
		tmp.clear();

		return !faild;
	}

	private boolean typeCheck(IFile file, Contract contract)
	{
		List<String> names = new Vector<String>();
		boolean failed = false;
		for (IVariable var : contract.getVariables())
		{
			if (names.contains(var.getName()))
			{
				FileUtility.addMarker(file, "Dublicate variable name: " + var, 0, IMarker.SEVERITY_ERROR);
				failed = true;
			} else
			{
				names.add(var.getName());
				failed = failed || !typeCheck(file, var);
			}
		}
		return !failed;
	}

	private boolean typeCheck(IFile file, IVariable variable)
	{
		switch (variable.getDataType())
		{
			case bool:
				try
				{
					Boolean.parseBoolean(variable.getValue().toString());
				} catch (Exception e)
				{
					FileUtility.addMarker(file, "Type error in variable expected 'true' or 'false' in: "
							+ variable, 0, IMarker.SEVERITY_ERROR);
					return false;
				}
				break;
			case real:
				try
				{
					Double.parseDouble(variable.getValue().toString());
				} catch (Exception e)
				{
					FileUtility.addMarker(file, "Type error in variable expected 'true' or 'false' in: "
							+ variable, 0, IMarker.SEVERITY_ERROR);
					return false;
				}
				break;

		}
		return true;

	}

	protected void addError(IFile file, int line, String message)
	{
		FileUtility.addMarker(file, message, line, IMarker.SEVERITY_ERROR);
	}

	protected Object parse(@SuppressWarnings("rawtypes") ParserWrapper parser, IFile file) throws Exception
	{
		Object result = parser.parse(file.getLocation().toFile());

		if (file == null || !file.exists() || !file.isAccessible())
			return result;

		FileUtility.deleteMarker(file, IMarker.PROBLEM, IDestecsCoreConstants.PLUGIN_ID);
		if (parser.hasErrors())
		{
			for (Object err : parser.getErrors())
			{
				IError e = (IError) err;
//				System.out.println(e);
				FileUtility.addMarker(file, e.getMessage(), e.getLine(), e.getCharPositionInLine(), IMarker.SEVERITY_ERROR, FileUtility.getCharContent(FileUtility.getContent(file)).toString());
			}
			throw new Exception("Parse errors in " + file);
		} else
		{
			return result;
		}
	}

}
