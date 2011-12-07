package org.destecs.ide.core.utility;

import java.io.IOException;

import org.destecs.core.contract.Contract;
import org.destecs.core.parsers.ContractParserWrapper;
import org.destecs.core.parsers.IError;
import org.destecs.core.parsers.ParserWrapper;
import org.destecs.core.parsers.VdmLinkParserWrapper;
import org.destecs.core.vdmlink.Links;
import org.destecs.ide.core.resources.IDestecsProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

public class ParserUtil
{
	public static interface IAddErrorHandler
	{
		void addMarker(IFile file, String message, int lineNumber,
				int columnNumber, int severity, String content);

	}

	/**
	 * @param project
	 * @param errorHandler
	 *            may be null
	 * @return
	 * @throws Exception
	 */
	public static Contract getContract(IDestecsProject project,
			IAddErrorHandler errorHandler) throws Exception
	{
		ContractParserWrapper contractParser = new ContractParserWrapper();
		Contract contract = (Contract) parse(contractParser,
				project.getContractFile(), errorHandler);
		return contract;
	}

	/**
	 * 
	 * @param project
	 * @param errorHandler
	 *            may be null
	 * @return
	 * @throws Exception
	 */
	public static Links getVdmLinks(IDestecsProject project,
			IAddErrorHandler errorHandler) throws Exception
	{
		VdmLinkParserWrapper vdmLinkParser = new VdmLinkParserWrapper();
		Links vdmlinks = (Links) parse(vdmLinkParser, project.getVdmLinkFile(),
				errorHandler);
		return vdmlinks;
	}

	protected static Object parse(
			@SuppressWarnings("rawtypes") ParserWrapper parser, IFile file,
			IAddErrorHandler errorHandler) throws IOException
	{
		Object result = parser.parse(file.getLocation().toFile());

		if (file == null || !file.exists() || !file.isAccessible())
			return result;

		if (parser.hasErrors() && errorHandler != null)
		{
			for (Object err : parser.getErrors())
			{
				IError e = (IError) err;
				errorHandler.addMarker(file, e.getMessage(), e.getLine() + 1, e
						.getCharPositionInLine(), IMarker.SEVERITY_ERROR,
						FileUtility
								.getCharContent(FileUtility.getContent(file))
								.toString());
			}
			return null;
		}
		return result;
	}
}
