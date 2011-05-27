package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.parsers.subs.SubsLexer;
import org.destecs.core.parsers.subs.SubsParser;

public class SubsParserWrapper extends ParserWrapper<SubsParser.start_return>
{
	protected SubsParser.start_return internalParse(File source, CharStream data)
			throws IOException
	{
		super.lexer = new SubsLexer(data);
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		SubsParser thisParser = new SubsParser(tokens);
		parser = thisParser;

		((SubsLexer) lexer).enableErrorMessageCollection(true);
		thisParser.enableErrorMessageCollection(true);
		try
		{
			SubsParser.start_return result = thisParser.start();

			if (((SubsLexer) lexer).hasExceptions())
			{
				List<RecognitionException> exps = ((SubsLexer) lexer).getExceptions();
				addErrorsLexer(source, exps);
				return null;
			}

			if (thisParser.hasExceptions())
			{

				List<RecognitionException> exps = thisParser.getExceptions();
				addErrorsParser(source, exps);
			} else
			{
				return result;
			}
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, parser.getTokenNames())));
		}
		return null;
	}
}
