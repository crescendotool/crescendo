package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.parsers.vdmlink.VdmLinkLexer;
import org.destecs.core.parsers.vdmlink.VdmLinkParser;
import org.destecs.core.vdmlink.Links;

public class VdmLinkParserWrapper extends ParserWrapper<Links>
{
	protected Links internalParse(File source, CharStream data)
			throws IOException
	{
		super.lexer = new VdmLinkLexer(data);
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		VdmLinkParser thisParser = new VdmLinkParser(tokens);
		parser = thisParser;

		((VdmLinkLexer)lexer).enableErrorMessageCollection(true);
		thisParser.enableErrorMessageCollection(true);
		try
		{
			thisParser.start();

			if (((VdmLinkLexer)lexer).hasExceptions())
			{
				List<RecognitionException> exps = thisParser.getExceptions();
				addErrors(source, exps);
				return null;
			}

			if (thisParser.hasExceptions())
			{

				List<RecognitionException> exps = thisParser.getExceptions();
				addErrors(source, exps);
			} else
			{
				return thisParser.getLinks();
			}
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, parser.getTokenNames())));
		}
		return null;
	}
}
