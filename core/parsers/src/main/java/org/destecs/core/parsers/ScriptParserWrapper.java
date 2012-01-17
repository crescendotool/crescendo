/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.destecs.core.parsers.dcl.ScriptLexer;
import org.destecs.core.parsers.dcl.ScriptParser;
import org.destecs.script.ast.node.INode;

public class ScriptParserWrapper extends ParserWrapper<List<INode>>
{

	protected List<INode> internalParse(File source, CharStream data)
			throws IOException
	{
		super.lexer = new ScriptLexer(data);
		final CommonTokenStream tokens = new CommonTokenStream(lexer);

		ScriptParser thisParser = new ScriptParser(tokens);
		parser = thisParser;

		((ScriptLexer) lexer).enableErrorMessageCollection(true);
		thisParser.enableErrorMessageCollection(true);

		try
		{
			List<INode> toplevel = thisParser.root();

			if (((ScriptLexer) lexer).hasExceptions())
			{
				List<RecognitionException> exps = ((ScriptLexer) lexer).getExceptions();
				addErrorsLexer(source, exps);
				return null;
			}

			if (thisParser.hasExceptions())
			{
				List<RecognitionException> exps = thisParser.getExceptions();
				addErrorsParser(source, exps);
			}

			return toplevel;
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, parser.getTokenNames())));
		}
		return null;
	}
}
