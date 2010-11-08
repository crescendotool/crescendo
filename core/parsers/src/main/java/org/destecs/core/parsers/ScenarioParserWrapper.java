package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.parsers.scenario.ScenarioLexer;
import org.destecs.core.parsers.scenario.ScenarioParser;
import org.destecs.core.scenario.Scenario;

public class ScenarioParserWrapper extends ParserWrapper<Scenario>
{
	protected Scenario internalParse(File source, CharStream data) throws IOException
	{
		ScenarioLexer lexer = new ScenarioLexer(data);
					CommonTokenStream tokens = new CommonTokenStream(lexer);
					ScenarioParser thisParser = new ScenarioParser(tokens);
		parser = thisParser;
		
		lexer.enableErrorMessageCollection(true);		
		thisParser.enableErrorMessageCollection(true);
		try
		{
			thisParser.start();
			
			if(lexer.hasExceptions())
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
				return thisParser.getScenario();
			}
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, parser.getTokenNames())));
		}
		return null;
	}
}
