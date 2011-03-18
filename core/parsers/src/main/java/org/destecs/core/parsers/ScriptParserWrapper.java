package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.dcl.Dcl;
import org.destecs.core.parsers.dcl.DclLexer;
import org.destecs.core.parsers.dcl.DclParser;


public class ScriptParserWrapper extends ParserWrapper<DclParser.toplevelStatement_return>
{
	protected DclParser.toplevelStatement_return internalParse(File source, CharStream data)
			throws IOException
	{
		super.lexer = new DclLexer(data);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		DclParser thisParser = new DclParser(tokens);
		parser = thisParser;

		((DclLexer)lexer).enableErrorMessageCollection(true);
		thisParser.enableErrorMessageCollection(true);
		try
		{
			DclParser.toplevelStatement_return ast = thisParser.toplevelStatement();

			if (((DclLexer)lexer).hasExceptions())
			{
				List<RecognitionException> exps = ((DclLexer)lexer).getExceptions();
				addErrorsLexer(source, exps);
				return null;
			}

			if (thisParser.hasExceptions())
			{

				List<RecognitionException> exps = thisParser.getExceptions();
				addErrorsParser(source, exps);
			} else
			{
				return ast;
			}
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, parser.getTokenNames())));
		}
		return null;
	}
}
