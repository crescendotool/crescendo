package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.contract.Contract;
import org.destecs.core.parsers.contract.ContractLexer;
import org.destecs.core.parsers.contract.ContractParser;

public class ContractParserWrapper extends ParserWrapper<Contract>
{
	protected Contract internalParse(File source, CharStream data) throws IOException
	{
		ContractLexer lexer = new ContractLexer(data);
					CommonTokenStream tokens = new CommonTokenStream(lexer);
		ContractParser thisParser = new ContractParser(tokens);
		parser = thisParser;
		
		lexer.enableErrorMessageCollection(true);		
		thisParser.enableErrorMessageCollection(true);
		try
		{
			thisParser.contract();
			
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
				return thisParser.getContract();
			}
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, parser.getTokenNames())));
		}
		return null;
	}
}
