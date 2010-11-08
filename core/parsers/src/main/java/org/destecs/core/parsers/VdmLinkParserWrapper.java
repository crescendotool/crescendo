package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.parsers.vdmlink.VdmLinkLexer;
import org.destecs.core.parsers.vdmlink.VdmLinkParser;
import org.destecs.core.vdmLink.Links;

public class VdmLinkParserWrapper extends ParserWrapper<Links>
{
	public Links parse(File source) throws IOException
	{
		ANTLRFileStream input = new ANTLRFileStream(source.getAbsolutePath());
		VdmLinkLexer lexer = new VdmLinkLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		VdmLinkParser thisParser = new VdmLinkParser(tokens);
		parser = thisParser;
		thisParser.enableErrorMessageCollection(true);

		try
		{
			thisParser.start();
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

	public Links parse(File source, String data) throws IOException
	{
		ANTLRFileStream input = new ANTLRFileStream(source.getAbsolutePath());
		VdmLinkLexer lexer = new VdmLinkLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		VdmLinkParser thisParser = new VdmLinkParser(tokens);
		parser = thisParser;
		thisParser.enableErrorMessageCollection(true);

		try
		{
			thisParser.start();
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
