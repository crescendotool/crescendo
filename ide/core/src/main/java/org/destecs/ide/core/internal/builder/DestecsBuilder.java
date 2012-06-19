package org.destecs.ide.core.internal.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

		//check duplicates on all parts of the link
		checkDuplicates(vdmlinks.getSharedDesignParametersList(),vdmlinks.getSharedDesignParameters(),file);
		checkDuplicates(vdmlinks.getOutputsList(),vdmlinks.getOutputs(),file);
		checkDuplicates(vdmlinks.getInputsList(),vdmlinks.getInputs(),file);
		checkDuplicates(vdmlinks.getEventsList(),vdmlinks.getEvents(),file);
		
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

		return true;
	}


	private static void checkDuplicates(List<String> list, Map<String, LinkInfo> map, IFile file)
	{
		List<String> identifiersList = new ArrayList<String>();
		
		for (String id : list)
		{
			identifiersList.add(id);
		}
		
		Set<String> identifiersSet = new HashSet<String>(identifiersList);
		if(identifiersSet.size() < identifiersList.size())
		{
			for (String string : identifiersSet)
			{
				identifiersList.remove(string);
			}
			
			for (String string : identifiersList)
			{
				LinkInfo linkInfo = map.get(string);
				
				addError(file, linkInfo.getLine()+1, "Duplicated identifier - " + linkInfo.getIdentifier());
			}
		}
		
		
		
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

		
		setDuplicationWarnings(contract,file);
		setMatrixDimensionWarnings(contract,file);
		
		
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


	private static void setMatrixDimensionWarnings(Contract contract, IFile file) {
		for (IVariable var : contract.getVariables()) {
			checkNotAllowedDimentions(var,file);
		}
		
		for (IVariable var  : contract.getSharedDesignParameters()) {
			checkNotAllowedDimentions(var,file);
		}
		
	}

	private static void checkNotAllowedDimentions(IVariable var, IFile file) {
		List<Integer> dimensions  = var.getDimensions();
		for (int i = 0; i < dimensions.size(); i++) {
			if(dimensions.get(i) <= 0)
			{
				addError(file, var.getLine()+1, "Cannot have zero or negative dimentions in array/matrix \"" + var.getName() + "\"");
			}			
		}
		
		if(dimensions.size() > 1)
		{
			if(dimensions.get(dimensions.size()-1) == 1)
			{
				addError(file, var.getLine()+1, "Last dimension of matrix \"" +  var.getName() +"\" should not be 1.");
			}
		}
		
	}

	private static void setDuplicationWarnings(Contract contract, IFile file)
	{
		
		List<String> allIdentifiersList = new ArrayList<String>();
		
		allIdentifiersList.addAll(getIdentifiers(contract.getControlledVariables()));
		allIdentifiersList.addAll(getIdentifiers(contract.getMonitoredVariables()));
		allIdentifiersList.addAll(getIdentifiers(contract.getSharedDesignParameters()));
		allIdentifiersList.addAll(contract.getEvents());
		
		
		Set<String> allIdentifiersSet = new HashSet<String>(allIdentifiersList);
		if(allIdentifiersSet.size() < allIdentifiersList.size())
		{
			for (String string : allIdentifiersSet)
			{
				allIdentifiersList.remove(string);
			}
			
			markDuplication(allIdentifiersList,contract.getControlledVariables(),file);
			markDuplication(allIdentifiersList,contract.getMonitoredVariables(),file);
			markDuplication(allIdentifiersList,contract.getSharedDesignParameters(),file);
			markDuplication(allIdentifiersList,contract.getEventsWithLineNumbers(),file);
			
		}
		
	}

	private static void markDuplication(List<String> allIdentifiersList,
			List<IVariable> variables, IFile file)
	{
		for (IVariable iVariable : variables)
		{
			if(allIdentifiersList.contains(iVariable.getName()))
			{
				addError(file, iVariable.getLine()+1, "Duplicated identifier - " + iVariable.getName());
			}
		}
		
	}

	private static Collection<? extends String> getIdentifiers(
			List<IVariable> controlledVariables)
	{
		List<String> result = new ArrayList<String>();
		for (IVariable iVariable : controlledVariables)
		{
			result.add(iVariable.getName());
		}
		
		return result;
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
				//failed = failed || !typeCheck(file, var);
			}
		}
		return !failed;
	}

//	private static boolean typeCheck(IFile file, IVariable variable)
//	{
//		switch (variable.getDataType())
//		{
//		case bool:
//			try
//			{
//				Boolean.parseBoolean(variable.getValue().toString());
//			} catch (Exception e)
//			{
//				FileUtility.addMarker(file,
//						"Type error in variable expected 'true' or 'false' in: "
//								+ variable, 0, IMarker.SEVERITY_ERROR);
//				return false;
//			}
//			break;
//		case real:
//			try
//			{
//				Double.parseDouble(variable.getValue().toString());
//			} catch (Exception e)
//			{
//				FileUtility.addMarker(file,
//						"Type error in variable expected 'true' or 'false' in: "
//								+ variable, 0, IMarker.SEVERITY_ERROR);
//				return false;
//			}
//			break;
//
//		}
//		return true;
//
//	}

	protected static void addWarning(IFile file, int line, String message)
	{
		FileUtility.addMarker(file, message, line, IMarker.SEVERITY_WARNING);
	}

	protected static void addError(IFile file, int line, String message)
	{
		FileUtility.addMarker(file, message, line, IMarker.SEVERITY_ERROR);
	}
}
