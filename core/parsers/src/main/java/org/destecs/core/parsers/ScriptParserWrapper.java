package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeNodeStream;
import org.destecs.core.dcl.CymbolAST;
import org.destecs.core.dcl.Script;
import org.destecs.core.dcl.SymbolTable;
import org.destecs.core.parsers.dcl.ScriptLexer;
import org.destecs.core.parsers.dcl.ScriptParser;
import org.destecs.core.parsers.dcl.Treewalker;

public class ScriptParserWrapper extends ParserWrapper<Script>
{
////	/** An adaptor that tells ANTLR to build CymbolAST nodes */
    public static TreeAdaptor CymbolAdaptor = new CommonTreeAdaptor() {
        public Object create(Token token) {
            return new CymbolAST(token);
        }
        public Object dupNode(Object t) {
            if ( t==null ) {
                return null;
            }
            return create(((CymbolAST)t).token);
        }

//        public Object errorNode(TokenStream input,
//                                Token start,
//                                Token stop,
//                                RecognitionException e)
//        {
//            return new CymbolErrorNode(input,start,stop,e);
//        }
    };

	protected Script  internalParse(File source, CharStream data)
			throws IOException
	{
		super.lexer = new ScriptLexer(data);
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		ScriptParser thisParser = new ScriptParser(tokens);
		parser = thisParser;
		
		
		final SymbolTable symtab = new SymbolTable();
		
		((ScriptLexer)lexer).enableErrorMessageCollection(true);
		thisParser.enableErrorMessageCollection(true);
			
		
		try
		{
			
//			DclParser.compilationUnit_return ast = thisParser.compilationUnit(symtab);				
			ScriptParser.compilationUnit_return ast = thisParser.compilationUnit(symtab);			
		    CommonTree comtree = (CommonTree) ast.getTree();
		 
			// CREATE TREE NODE STREAM FOR TREE PARSERS
			TreeNodeStream nodes = new CommonTreeNodeStream(comtree);
			
			Treewalker typeComp = new Treewalker(nodes, symtab);//should symtab also be an input here?
		
			typeComp.program();
			
				        
			if (((ScriptLexer)lexer).hasExceptions())
			{
//				System.out.println("Dcl Lexer hasExceptions");
				
				List<RecognitionException> exps = ((ScriptLexer)lexer).getExceptions();
				addErrorsLexer(source, exps);
				return null;
			}

			if (thisParser.hasExceptions())
			{
//				System.out.println("Script Parser has Exceptions --check the grammar");
				
				List<RecognitionException> exps = thisParser.getExceptions();
				addErrorsParser(source, exps);
			}
			if (typeComp.hasExceptions())
			{
				List<RecognitionException> exps = typeComp.getExceptions();
				addErrorsParser(source, exps);
			}
	    	else
			{
//				System.out.println("return AST");
				return thisParser.getScript();
			}
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, parser.getTokenNames())));
		}
		return null;
	}
}
