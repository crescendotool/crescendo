package org.destecs.core.parsers;

import java.io.File;
import java.util.List;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.contract.Contract;
import org.destecs.core.contract.ContractFactory;
import org.destecs.core.parsers.contract.contractLexer;
import org.destecs.core.parsers.contract.contractParser;

public class ContractParserWrapper extends contractParser
{
	public Contract parse(File file)
	{
		ANTLRFileStream input = new ANTLRFileStream(file.getAbsolutePath());
		contractLexer lexer = new contractLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		contractParser parser = new contractParser(tokens);
		parser.enableErrorMessageCollection(true);

		try
		{
			parser.contract();
			if (parser.hasExceptions())
			{
				List<RecognitionException> exps = parser.getExceptions();
				System.out.println("Exceptions");
				for (RecognitionException recognitionException : exps)
				{
					System.out.println(parser.getErrorHeader(recognitionException));
				}
			} else
			{
				ContractFactory c = parser.getContract();
				return c.getContract();
			}
		} catch (RecognitionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
