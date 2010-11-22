package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.parsers.sdp.SdpLexer;
import org.destecs.core.parsers.sdp.SdpParser;

public class SdpParserWrapper extends ParserWrapper<HashMap<String, Object>>
{
	@SuppressWarnings("unchecked")
	protected HashMap<String, Object> internalParse(File source, CharStream data)
			throws IOException
	{
		super.lexer = new SdpLexer(data);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		SdpParser thisParser = new SdpParser(tokens);
		parser = thisParser;

		((SdpLexer)lexer).enableErrorMessageCollection(true);
		thisParser.enableErrorMessageCollection(true);
		try
		{
			thisParser.start();

			if (((SdpLexer)lexer).hasExceptions())
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
				return thisParser.getSdps();
			}
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, parser.getTokenNames())));
		}
		return null;
	}
}
