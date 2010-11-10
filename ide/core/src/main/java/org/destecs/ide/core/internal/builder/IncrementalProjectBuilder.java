package org.destecs.ide.core.internal.builder;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.core.contract.Contract;
import org.destecs.core.contract.Variable;
import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.parsers.IError;
import org.destecs.core.parsers.ParserWrapper;
import org.destecs.core.parsers.VdmLinkParserWrapper;
import org.destecs.core.vdmlink.Links;

import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.core.utility.FileUtility;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class IncrementalProjectBuilder extends
		org.eclipse.core.resources.IncrementalProjectBuilder
{

	@SuppressWarnings("unchecked")
	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException
	{
		if (!getProject().hasNature(IDestecsCoreConstants.NATURE))
		{
			return null;
		}

		IDestecsProject project = (IDestecsProject) getProject().getAdapter(IDestecsProject.class);

		try
		{
			ContractParserWrapper contractParser = new ContractParserWrapper();
			Contract contract = (Contract) parse(contractParser, project.getContractFile());

			if (!typeCheck(project.getContractFile(), contract))
			{
				return null;
			}

			VdmLinkParserWrapper vdmLinkParser = new VdmLinkParserWrapper();
			Links vdmlinks = (Links) parse(vdmLinkParser, project.getVdmLinkFile());

			if (!typeCheck(project.getVdmLinkFile(), vdmlinks, contract))
			{
				return null;
			}

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return null;
	}

	private boolean typeCheck(IFile file, Links vdmlinks, Contract contract)
	{
		boolean faild = false;
		List<String> tmp = new Vector<String>();

		// check outputs
		tmp.addAll(vdmlinks.getOutputs());
		for (Variable var : contract.getControlledVariables())
		{
			if (!vdmlinks.getOutputs().contains(var.name))
			{
				addError(file, "Missing-output controlled variable: " + var);
				faild = true;
			}

			if (!vdmlinks.getLinks().containsKey(var.name))
			{
				addError(file, "Missing-output controlled variable link: "
						+ var);
				faild = true;
			}

			if (tmp.contains(var.name))
			{
				tmp.remove(var.name);
			}
		}

		if (tmp.size() > 0)
		{
			addError(file, "Too many outputs defined; no usage found for: "
					+ tmp);
			faild = true;
		}
		tmp.clear();

		// check inputs
		tmp.addAll(vdmlinks.getInputs());
		for (Variable var : contract.getMonitoredVariables())
		{
			if (!vdmlinks.getInputs().contains(var.name))
			{
				addError(file, "Missing-input monitored variable: " + var);
				faild = true;
			}
			if (!vdmlinks.getLinks().containsKey(var.name))
			{
				addError(file, "Missing-input monitored variable link: " + var);
				faild = true;
			}

			if (tmp.contains(var.name))
			{
				tmp.remove(var.name);
			}
		}

		if (tmp.size() > 0)
		{
			addError(file, "Too many inputs defined; no usage found for: "
					+ tmp);
			faild = true;
		}
		tmp.clear();

		// check events
		tmp.addAll(vdmlinks.getEvents());
		for (String event : contract.getEvents())
		{
			if (!vdmlinks.getEvents().contains(event))
			{
				addError(file, "Missing-event: " + event);
				faild = true;
			}

			if (!vdmlinks.getLinks().containsKey(event))
			{
				addError(file, "Missing-event link: " + event);
				faild = true;
			}

			if (tmp.contains(event))
			{
				tmp.remove(event);
			}
		}

		if (tmp.size() > 0)
		{
			addError(file, "Too many events defined; no usage found for: "
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
		for (Variable var : contract.getVariables())
		{
			if (names.contains(var.name))
			{
				FileUtility.addMarker(file, "Dublicate variable name: " + var, 0, IMarker.SEVERITY_ERROR);
				failed = true;
			} else
			{
				names.add(var.name);
				failed = failed || !typeCheck(file, var);
			}
		}
		return !failed;
	}

	private boolean typeCheck(IFile file, Variable variable)
	{
		switch (variable.dataType)
		{
			case bool:
				try
				{
					Boolean.parseBoolean(variable.value.toString());
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
					Double.parseDouble(variable.value.toString());
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

	protected void addError(IFile file, String message)
	{
		FileUtility.addMarker(file, message, 0, IMarker.SEVERITY_ERROR);
	}

	@SuppressWarnings("unchecked")
	protected Object parse(ParserWrapper parser, IFile file) throws Exception
	{
		FileUtility.deleteMarker(file, IMarker.PROBLEM, IDestecsCoreConstants.PLUGIN_ID);

		Object result = parser.parse(file.getLocation().toFile());

		if (parser.hasErrors())
		{
			for (Object err : parser.getErrors())
			{
				IError e = (IError) err;
				System.out.println(e);
				FileUtility.addMarker(file, e.getMessage(), e.getLine(), e.getCharPositionInLine(), IMarker.SEVERITY_ERROR, FileUtility.getCharContent(FileUtility.getContent(file)).toString());
			}
			throw new Exception("Parse errors in " + file);
		} else
		{
			return result;
		}
	}

}
