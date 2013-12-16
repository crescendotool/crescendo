package org.destecs.ide.core.internal.builder;

import java.util.List;

import org.destecs.ide.core.DestecsCorePlugin;
import org.destecs.ide.core.IDestecsCoreConstants;
import org.destecs.ide.core.resources.DestecsModel;
import org.destecs.ide.core.resources.IDestecsProject;
import org.destecs.ide.core.utility.FileUtility;
import org.destecs.ide.core.utility.ParserUtil;
import org.destecs.ide.core.utility.ParserUtil.IAddErrorHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

public class ScriptBuilder
{
	private static IAddErrorHandler errorHandler = new IAddErrorHandler()
	{

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
		try
		{
			DestecsModel model = project.getModel();
			model.setScriptOk(true);
			List<IFile> scenarios = project.getScenarioFiles();
			for (IFile iFile : scenarios)
			{
				try
				{
					if (iFile.getName().endsWith(".script"))
					{
//						FileUtility.deleteMarker(iFile, IMarker.PROBLEM, IDestecsCoreConstants.PLUGIN_ID);
//						if(ParserUtil.getScenario(project, iFile, errorHandler)==null)
//						{
//							model.setScriptOk(false);
//						}
//					} else if (iFile.getName().endsWith(".script2"))
//					{
						FileUtility.deleteMarker(iFile, IMarker.PROBLEM, IDestecsCoreConstants.PLUGIN_ID);
						if(ParserUtil.getScript(project, iFile, errorHandler)==null)
						{
							model.setScriptOk(false);
						}
					}
				} catch (Exception e)
				{
					DestecsCorePlugin.log("Failure in script parser", e);
				}
			}

		} catch (Exception e)
		{
			DestecsCorePlugin.log("Failed to find scenario files", e);
		}

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
