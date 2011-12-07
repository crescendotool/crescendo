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
package org.destecs.ide.core.internal.builder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.destecs.core.contract.Contract;
import org.destecs.core.contract.IVariable;
import org.destecs.core.vdmlink.LinkInfo;
import org.destecs.core.vdmlink.Links;
import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.metadata.DeMetadataChecker;
import org.destecs.ide.core.metadata.LinkError;
import org.destecs.ide.core.resources.DestecsModel;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.core.utility.FileUtility;
import org.destecs.ide.core.utility.ParserUtil;
import org.destecs.ide.core.utility.ParserUtil.IAddErrorHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class IncrementalProjectBuilder extends
		org.eclipse.core.resources.IncrementalProjectBuilder
{

	private IAddErrorHandler errorHandler = new IAddErrorHandler() {

		public void addMarker(IFile file, String message, int lineNumber,
				int columnNumber, int severity, String content)
		{
//			FileUtility.addMarker(file, message, lineNumber, columnNumber,
//					severity, content);
			if(severity == IMarker.SEVERITY_ERROR)
			{
				addError(file, lineNumber, message);
			}else if (severity == IMarker.SEVERITY_WARNING)
			{
				addWarning(file, lineNumber, message);
			}
		}
	};

	@Override
	protected IProject[] build(int kind,
			@SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor)
			throws CoreException
	{
		IResourceDelta delta = getDelta(getProject());
		if (!getProject().hasNature(IDestecsCoreConstants.NATURE)
				|| (delta != null && delta.getAffectedChildren().length == 0))
		{
			return null;
		}

		// System.out.println("Project: "+getProject().getName()+"-"+getDelta(getProject()).getAffectedChildren().length);

		boolean isOk = true;
		boolean isChecked = true;

		IDestecsProject project = (IDestecsProject) getProject().getAdapter(
				IDestecsProject.class);
		final DestecsModel model = project.getModel();
		try
		{
			//Check contract parser
			if (!project.getContractFile().exists())
			{
				model.setContract(null);
				isOk = false;
				return null;
			}
			FileUtility.deleteMarker(project.getContractFile(),
					IMarker.PROBLEM, IDestecsCoreConstants.PLUGIN_ID);
			Contract contract = null;
			try
			{
				contract = ParserUtil.getContract(project, errorHandler);
			} catch (IOException e)
			{
				addError(project.getContractFile(), 0,
						"Internal error: Failed to parse");
				return new IProject[0];
			}
			
			
			//check vdmlink parser
			if (!project.getVdmLinkFile().exists())
			{
				model.setLinks(null);
				isOk = false;
				return null;
			}

			FileUtility.deleteMarker(project.getVdmLinkFile(), IMarker.PROBLEM,
					IDestecsCoreConstants.PLUGIN_ID);
			Links vdmlinks = null;
			try
			{
				vdmlinks = ParserUtil.getVdmLinks(project, errorHandler);
			} catch (IOException e)
			{
				addError(project.getVdmLinkFile(), 0,
						"Internal error: Failed to parse");
				return new IProject[0];
			}
			
			if(vdmlinks ==null || contract ==null)
			{
				return new IProject[0];
			}

			//type checking
			if (!typeCheck(project.getContractFile(), contract))
			{
				model.setContract(null);
				isOk = false;
				return null;
			}
			model.setContract(contract);

		
			if (!typeCheckContract(project.getContractFile(), vdmlinks,
					contract))
			{
				model.setLinks(null);
				isOk = false;
				return null;
			}

			typeCheckLinks(project.getVdmLinkFile(), vdmlinks, contract);

			model.setLinks(vdmlinks);

			DeMetadataChecker checker = new DeMetadataChecker(project, vdmlinks);
			checker.checkLinks();
			if (checker.hasErrors())
			{
				for (LinkError error : checker.getErrors())
				{
					addError(project.getVdmLinkFile(), error.getLine() + 1,
							error.getReason());
				}
			}

			// //TODO: The check below has some issues with the build order
			// DeMetadata deMetadata = new DeMetadata(vdmlinks,project);
			// deMetadata.checkLinks();
			// for (String err : deMetadata.getErrorMsgs()) {
			// addError(project.getVdmLinkFile(), err);
			// }

		} catch (Exception e)
		{
			isOk = false;
			isChecked = false;
			e.printStackTrace();
			// TODO build with errors, set project state to build and error
		}

		model.setChecked(isChecked);
		model.setOk(isOk);

		return null;
	}

	/**
	 * Checks the link against the contract file and adds error markers to the
	 * link file
	 * 
	 * @param file
	 *            this file is the link
	 * @param vdmlinks
	 * @param contract
	 * @return
	 */
	private boolean typeCheckLinks(IFile file, Links vdmlinks, Contract contract)
	{
		boolean failed = false;

		// check outputs
		for (LinkInfo var : vdmlinks.getSharedDesignParameters().values())// )
		{
			if (!hasVariable(var.getIdentifier(),
					contract.getSharedDesignParameters()))
			{
				addWarning(file, var.getLine() + 1, "Shared design parameter ["
						+ var.getIdentifier() + "] not present in the contract");
			}
		}

		// check outputs
		for (LinkInfo var : vdmlinks.getOutputs().values())
		{
			if (!hasVariable(var.getIdentifier(),
					contract.getControlledVariables()))
			{
				addWarning(file, var.getLine() + 1,
						"Output variable [" + var.getIdentifier()
								+ "] not present in the contract");
			}
		}

		// check inputs
		for (LinkInfo var : vdmlinks.getInputs().values())
		{
			if (!hasVariable(var.getIdentifier(),
					contract.getMonitoredVariables()))
			{
				addWarning(file, var.getLine() + 1,
						"Input variable [" + var.getIdentifier()
								+ "] not present in the contract");
			}
		}

		// check events
		for (LinkInfo event : vdmlinks.getEvents().values())
		{
			if (!contract.getEvents().contains(event.getIdentifier()))
			{
				addWarning(file, event.getLine() + 1, "Unlinked event: "
						+ event.getIdentifier());
			}
		}

		return !failed;
	}

	private boolean hasVariable(String identifier,
			List<IVariable> sharedDesignParameters)
	{
		for (IVariable iVariable : sharedDesignParameters)
		{
			if (iVariable.getName().equals(identifier))
				return true;
		}

		return false;
	}

	/**
	 * Checks the contract against the link file and adds error markers to the
	 * contract file
	 * 
	 * @param file
	 *            this file is the contract
	 * @param vdmlinks
	 * @param contract
	 * @return
	 */
	private boolean typeCheckContract(IFile file, Links vdmlinks,
			Contract contract)
	{
		boolean failed = false;

		// check outputs
		for (IVariable var : contract.getSharedDesignParameters())
		{
			if (!vdmlinks.getSharedDesignParameters().keySet()
					.contains(var.getName()))
			{
				addError(file, var.getLine() + 1,
						"Unlinked shared design parameter: " + var);
				failed = true;
			} else if (!vdmlinks.getLinks().containsKey(var.getName()))
			{
				addError(file, var.getLine() + 1,
						"Unlinked  shared design parameter link: " + var);
				failed = true;
			}
		}

		// check outputs
		for (IVariable var : contract.getControlledVariables())
		{
			if (!vdmlinks.getOutputs().keySet().contains(var.getName()))
			{
				addError(file, var.getLine() + 1,
						"Unlinked controlled variable: " + var);
				failed = true;
			} else if (!vdmlinks.getLinks().containsKey(var.getName()))
			{
				addError(file, var.getLine() + 1, "Unlinked variable link: "
						+ var);
				failed = true;
			}

		}

		// check inputs
		for (IVariable var : contract.getMonitoredVariables())
		{
			if (!vdmlinks.getInputs().keySet().contains(var.getName()))
			{
				addError(file, 0, "Unlinked monitored variable: " + var);
				failed = true;
			} else if (!vdmlinks.getLinks().containsKey(var.getName()))
			{
				addError(file, 0, "Unlinked monitored variable link: " + var);
				failed = true;
			}

		}

		// check events
		for (String event : contract.getEvents())
		{
			if (!vdmlinks.getEvents().keySet().contains(event))
			{
				addError(file, 0, "Unlinked event: " + event);
				failed = true;
			} else if (!vdmlinks.getLinks().containsKey(event))
			{
				addError(file, 0, "Unlinked event link: " + event);
				failed = true;
			}
		}

		return !failed;
	}

	private boolean typeCheck(IFile file, Contract contract)
	{
		List<String> names = new Vector<String>();
		boolean failed = false;
		for (IVariable var : contract.getVariables())
		{
			if (names.contains(var.getName()))
			{
				FileUtility.addMarker(file, "Dublicate variable name: " + var,
						0, IMarker.SEVERITY_ERROR);
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
				FileUtility.addMarker(file,
						"Type error in variable expected 'true' or 'false' in: "
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
				FileUtility.addMarker(file,
						"Type error in variable expected 'true' or 'false' in: "
								+ variable, 0, IMarker.SEVERITY_ERROR);
				return false;
			}
			break;

		}
		return true;

	}

	protected void addWarning(IFile file, int line, String message)
	{
		FileUtility.addMarker(file, message, line, IMarker.SEVERITY_WARNING);
	}

	protected void addError(IFile file, int line, String message)
	{
		FileUtility.addMarker(file, message, line, IMarker.SEVERITY_ERROR);
	}
}
