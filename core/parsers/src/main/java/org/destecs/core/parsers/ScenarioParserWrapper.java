package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.parsers.scenario.ScenarioLexer;
import org.destecs.core.parsers.scenario.ScenarioParser;
import org.destecs.core.scenario.Scenario;

public class ScenarioParserWrapper extends ParserWrapper<Scenario>
{
			public Scenario parse(File source) throws IOException
	{
		ANTLRFileStream input = new ANTLRFileStream(source.getAbsolutePath());
		ScenarioLexer lexer = new ScenarioLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ScenarioParser thisParser = new ScenarioParser(tokens);
		parser=thisParser;
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
				return thisParser.getScenario();
			}
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, thisParser.getTokenNames())));
		}
		return null;
	}

	public Scenario parse(File source, String data) throws IOException
	{
		ANTLRStringStream input = new ANTLRStringStream(data);
		ScenarioLexer lexer = new ScenarioLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ScenarioParser thisParser = new ScenarioParser(tokens);
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
				return thisParser.getScenario();
			}
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, thisParser.getTokenNames())));
		}
		return null;
	}
}
