package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.contract.Contract;
import org.destecs.core.parsers.contract.ContractLexer;
import org.destecs.core.parsers.contract.ContractParser;

public class ContractParserWrapper 
{
	
	ContractParser parser =null;
	
	public Contract parse(File file) throws IOException
	{
		ANTLRFileStream input = new ANTLRFileStream(file.getAbsolutePath());
		ContractLexer lexer = new ContractLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		parser = new ContractParser(tokens);
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
				return parser.getContract();
			}
		} catch (RecognitionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean hasErrors()
	{
		if(parser!=null)
		{
			return parser.hasExceptions();
		}
		return false;
	}
}
