package org.destecs.ide.core.internal.builder;

import java.util.List;
import java.util.Vector;

import org.destecs.core.contract.Contract;
import org.destecs.core.contract.IVariable;
import org.destecs.core.vdmlink.LinkInfo;
import org.destecs.core.vdmlink.Links;
import org.destecs.ide.core.DestecsCorePlugin;
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

public class DestecsBuilder
{
	private static IAddErrorHandler errorHandler = new IAddErrorHandler() {

		public void addMarker(IFile file, String message, int lineNumber,
				int columnNumber, int severity, String content)
		{
			if (severity == IMarker.SEVERITY_ERROR)
			{
				addError(file, lineNumber, message);
			} else if (severity == IMarker.SEVERITY_WARNING)
			{
				addWarning(file, lineNumber, message);
			}
		}
	};

	public static void build(IDestecsProject project)
	{
		final DestecsModel model = project.getModel();
		try
		{
			model.setOk(true);
			// Check contract parser
			parseContract(project);

			// check vdmlink parser
			parseVdmLinks(project);

			if (model.getLinks() == null || model.getContract() == null)
			{
				model.setChecked(false);
				model.setOk(false);
				return;
			}

			// type checking
			if (!typeCheck(project.getContractFile(), model.getContract()))
			{
				model.setContract(null);
				model.setChecked(false);
				model.setOk(false);
				return;
			}

			if (!typeCheckContract(project.getContractFile(), model.getLinks(),
					model.getContract()))
			{
				model.setLinks(null);
				model.setChecked(false);
				model.setOk(false);
				return;
			}

			typeCheckLinks(project.getVdmLinkFile(), model.getLinks(),
					model.getContract());

			DeMetadataChecker checker = new DeMetadataChecker(project,
					model.getLinks());
			checker.checkLinks();
			if (checker.hasErrors())
			{
				for (LinkError error : checker.getErrors())
				{
					addError(project.getVdmLinkFile(), error.getLine() + 1,
							error.getReason());
				}
				model.setOk(false);
				model.setChecked(true);
			}


		} catch (Exception e)
		{
			model.setOk(false);
			model.setChecked(true);
			e.printStackTrace();
			DestecsCorePlugin.log("Error in Destecs builder for project: "+project, e);
		}


	}

	/**
	 * Re-parses the vdmlinks and clears all problem markers for the vdm link
	 * file
	 * 
	 * @param project
	 */
	private static void parseVdmLinks(IDestecsProject project)
	{
		DestecsModel model = project.getModel();
		if (!project.getVdmLinkFile().exists())
		{
			model.setLinks(null);
			model.setOk(false);
		} else
		{

			FileUtility.deleteMarker(project.getVdmLinkFile(), IMarker.PROBLEM,
					IDestecsCoreConstants.PLUGIN_ID);
			try
			{
				model.setLinks(ParserUtil.getVdmLinks(project, errorHandler));
			} catch (Exception e)
			{
				addError(project.getVdmLinkFile(), 0,
						"Internal error: Failed to parse");
				model.setLinks(null);
				model.setOk(false);
			}
		}
	}

	/**
	 * Re-parses the contract and clears all problem markers for the contract
	 * file
	 * 
	 * @param project
	 */
	private static void parseContract(IDestecsProject project)
	{
		DestecsModel model = project.getModel();
		if (!project.getContractFile().exists())
		{
			model.setContract(null);
			model.setOk(false);
		} else
		{

			FileUtility.deleteMarker(project.getContractFile(),
					IMarker.PROBLEM, IDestecsCoreConstants.PLUGIN_ID);
			try
			{
				model.setContract(ParserUtil.getContract(project, errorHandler));
			} catch (Exception e)
			{
				addError(project.getContractFile(), 0,
						"Internal error: Failed to parse");
				model.setContract(null);
				model.setOk(false);
			}
		}
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
	private static boolean typeCheckLinks(IFile file, Links vdmlinks,
			Contract contract)
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

	private static boolean hasVariable(String identifier,
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
	private static boolean typeCheckContract(IFile file, Links vdmlinks,
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
				addError(file, var.getLine() + 1,
						"Unlinked monitored variable: " + var);
				failed = true;
			} else if (!vdmlinks.getLinks().containsKey(var.getName()))
			{
				addError(file, var.getLine() + 1,
						"Unlinked monitored variable link: " + var);
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

	private static boolean typeCheck(IFile file, Contract contract)
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

	private static boolean typeCheck(IFile file, IVariable variable)
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

	protected static void addWarning(IFile file, int line, String message)
	{
		FileUtility.addMarker(file, message, line, IMarker.SEVERITY_WARNING);
	}

	protected static void addError(IFile file, int line, String message)
	{
		FileUtility.addMarker(file, message, line, IMarker.SEVERITY_ERROR);
	}
}
