package org.destecs.core.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

import org.destecs.core.dcl.CymbolAST;
import org.destecs.core.dcl.Dcl;

import org.destecs.core.dcl.SymbolTable;
import org.destecs.core.parsers.dcl.DclLexer;
import org.destecs.core.parsers.dcl.DclParser;
import org.destecs.core.parsers.dcl.DclParser.compilationUnit_return;
import org.destecs.core.parsers.dcl.Treewalker;
//import org.destecs.core.parsers.dcl.Def;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeAdaptor;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;

public class ScriptParserWrapper extends ParserWrapper<DclParser.compilationUnit_return>
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

	protected DclParser.compilationUnit_return internalParse(File source, CharStream data)
			throws IOException
	{
		super.lexer = new DclLexer(data);
		final CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		DclParser thisParser = new DclParser(tokens);
		parser = thisParser;
					
		SymbolTable symtab = new SymbolTable();
		
		((DclLexer)lexer).enableErrorMessageCollection(true);
		thisParser.enableErrorMessageCollection(true);
			
		
		try
		{
			thisParser.setTreeAdaptor(CymbolAdaptor);  
//			thisParser.compilationUnit(symtab);
			System.out.println("parser ends " );
			
//			DclParser.toplevelStatement_return ast = thisParser.toplevelStatement();
			DclParser.compilationUnit_return ast = thisParser.compilationUnit(symtab);
			
			
			
			
			System.out.println("parse again " );
		
			
			// create CymbolAST nodes
//		    CommonTree comtree = (CommonTree) ast.getTree();
			CymbolAST comtree = (CymbolAST) ast.getTree();
		    System.out.println("create the tree " );
		    
//			// CREATE TREE NODE STREAM FOR TREE PARSERS
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(comtree);
			nodes.setTokenStream(tokens); // where to find tokens				    
			nodes.setTreeAdaptor(CymbolAdaptor);			
			
			System.out.println("globals: " + symtab.symbols);

		    ////			// DEFINE SYMBOLS
//			Def def = new Def(nodes,symtab); // pass symtab to walker
//			def.downup(comtree); // trigger define actions upon certain subtrees
////			
////			// RESOLVE SYMBOLS, COMPUTE EXPRESSION TYPES
			nodes.reset();
			Treewalker typeComp = new Treewalker(nodes, symtab);
			System.out.println ("Tree:" + comtree.toStringTree() );	
			typeComp.downup(comtree); // trigger resolve/type computation actions
////			typeComp.irexpression();
//			System.out.println ( comtree.toStringTree() );
			

	        // WALK TREE TO DUMP SUBTREE TYPES
	        TreeVisitor v = new TreeVisitor(new CommonTreeAdaptor());
	        TreeVisitorAction actions = new TreeVisitorAction() {
	            public Object pre(Object t) { return t; }
	            public Object post(Object t)  {
	            	showTypes((CymbolAST)t, tokens);
	                return t;
	            }
				private void showTypes(CymbolAST t, CommonTokenStream tokens) {
					 if ( t.evalType!=null && t.getType()!=DclParser.EXPR ) {
						 	
						    System.out.println("Name: " + t.symbol.toString());
						 	System.out.println("what type:" + t.evalType + "; the node is:" + t.toString());
						    System.out.println("parser type: " + DclParser.EXPR);
			                System.out.printf("%-17s",
			                                  tokens.toString(t.getTokenStartIndex(),
			                                                  t.getTokenStopIndex()));
			                String ts = t.evalType.toString();
			                System.out.printf(" type %-8s\n", ts);
			            }
					
				}
	        };
//	        
	        v.visit(comtree, actions); // walk in postorder, showing types
			
	
			//maybe don't need
//			   if ( comtree.evalType != null && comtree.getType()!=DclParser.EXPR ) {
//			CymbolAST t = (CymbolAST)comtree;
////			   if ( t.getType()!=DclParser.EXPR ) {
//				    System.out.println("what type:" + t.evalType + "; the node is:" + t.toString());
//				    System.out.println("parser type: " + DclParser.EXPR);
//		            System.out.printf("%-17s",
//		                              tokens.toString(comtree.getTokenStartIndex(),
//		                            		  		  comtree.getTokenStopIndex()));
//		            String ts = t.evalType.toString(); // toString() part can't be used, otherwise there will be error
//		            System.out.printf(" type %-8s\n", ts);
//		            System.out.println("Everything is okay");
////		        }
//	        
	     
	        
//	        System.out.println("final tree: " + comtree.toStringTree());
	        //END
		        
			if (((DclLexer)lexer).hasExceptions())
			{
				System.out.println("Dcl Lexer hasExceptions");
				
				List<RecognitionException> exps = ((DclLexer)lexer).getExceptions();
				addErrorsLexer(source, exps);
				return null;
			}

			if (thisParser.hasExceptions())
			{
				System.out.println("Script Parser has Exceptions --check the grammar");
				
				List<RecognitionException> exps = thisParser.getExceptions();
				addErrorsParser(source, exps);
			} else
			{
				System.out.println("return AST");
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
