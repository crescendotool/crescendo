grammar Dcl;

options {
  language = Java;
  output = AST;
  ASTLabelType= CommonTree;
//   ASTLabelType = CymbolAST;
}

tokens{
  WHEN = 'when';  
  FOR = 'for';
  INCLUDE = 'include';
  TIME = 'time';
  DO = 'do';
  CT = 'ct';
  DE = 'de';
  TRUE = 'true';
  FALSE = 'false';
  PRINT ='print';
  AFTER = 'after';
  QUIT = 'quit';
  REVERT = 'revert';
  ERROR = 'error';  
  WARN = 'warn';
  REAL ='real';
  INT = 'int';
  BOOL = 'boolean';
// new part 
  METHOD_DECL; // function definition
  ARG_DECL;    // parameter
  BLOCK;
  VAR_DECL;
  FIELD_DECL;
  CALL;
  ELIST;       // expression list
  EXPR;      // root of an expression
  UNARY_MINUS;
  UNARY_NOT;
  INDEX;
}
 
@header {
package org.destecs.core.parsers.dcl;

import org.antlr.runtime.Token;

import org.destecs.core.dcl.CymbolAST;
//import org.destecs.core.dcl.ClassSymbol;
//import org.destecs.core.dcl.BaseScope;
import org.destecs.core.dcl.BuiltInTypeSymbol;
import org.destecs.core.dcl.Scope;
//import org.destecs.core.dcl.GlobalScope;
//import org.destecs.core.dcl.SymbolTree;
//import org.destecs.core.dcl.ScopedSymbol;
import org.destecs.core.dcl.SymbolTable;
import org.destecs.core.dcl.Type;
import org.destecs.core.dcl.Symbol;
import org.destecs.core.dcl.VariableSymbol;

}

@lexer::header{  
package org.destecs.core.parsers.dcl;
} 

@lexer::members{
    private boolean mMessageCollectionEnabled = false;
    private boolean mHasErrors = false;
    
    private List<String> mMessages;
    private List<RecognitionException> mExceptions = new ArrayList<RecognitionException>();
      
    public boolean hasExceptions()
    {
        return mExceptions.size() > 0;
    }

    public List<RecognitionException> getExceptions()
    {
        return mExceptions;
    }

    public String getErrorMessage(RecognitionException e, String[] tokenNames)
    {
        String msg = super.getErrorMessage(e, tokenNames);
        mExceptions.add(e);
        return msg;
    }

    /**
     *  Switches error message collection on or of.
     *
     *  The standard destination for parser error messages is <code>System.err</code>.
     *  However, if <code>true</code> gets passed to this method this default
     *  behaviour will be switched off and all error messages will be collected
     *  instead of written to anywhere.
     *
     *  The default value is <code>false</code>.
     *
     *  @param pNewState  <code>true</code> if error messages should be collected.
     */
    public void enableErrorMessageCollection(boolean pNewState) {
        mMessageCollectionEnabled = pNewState;
        if (mMessages == null && mMessageCollectionEnabled) {
            mMessages = new ArrayList<String>();
        }
    }
    
    /**
     *  Collects an error message or passes the error message to <code>
     *  super.emitErrorMessage(...)</code>.
     *
     *  The actual behaviour depends on whether collecting error messages
     *  has been enabled or not.
     *
     *  @param pMessage  The error message.
     */
     @Override
    public void emitErrorMessage(String pMessage) {
        if (mMessageCollectionEnabled) {
            mMessages.add(pMessage);
        } else {
            super.emitErrorMessage(pMessage);
        }
    }
    
    /**
     *  Returns collected error messages.
     *
     *  @return  A list holding collected error messages or <code>null</code> if
     *           collecting error messages hasn't been enabled. Of course, this
     *           list may be empty if no error message has been emited.
     */
    public List<String> getMessages() {
        return mMessages;
    }
    
    /**
     *  Tells if parsing a Java source has caused any error messages.
     *
     *  @return  <code>true</code> if parsing a Java source has caused at least one error message.
     */
    public boolean hasErrors() {
        return mHasErrors;
    }
} 


@members {

SymbolTable symtab;

private boolean mMessageCollectionEnabled = false;
    private boolean mHasErrors = false;
    private List<String> mMessages;
    private List<RecognitionException> mExceptions = new ArrayList<RecognitionException>();
  
    public boolean hasExceptions()
    {
        return mExceptions.size() > 0;
    }

    public List<RecognitionException> getExceptions()
    {
        return mExceptions;
    }

    public String getErrorMessage(RecognitionException e, String[] tokenNames)
    {
        String msg = super.getErrorMessage(e, tokenNames);
        mExceptions.add(e);
        return msg;
    }

    /**
     *  Switches error message collection on or of.
     *
     *  The standard destination for parser error messages is <code>System.err</code>.
     *  However, if <code>true</code> gets passed to this method this default
     *  behaviour will be switched off and all error messages will be collected
     *  instead of written to anywhere.
     *
     *  The default value is <code>false</code>.
     *
     *  @param pNewState  <code>true</code> if error messages should be collected.
     */
    public void enableErrorMessageCollection(boolean pNewState) {
        mMessageCollectionEnabled = pNewState;
        if (mMessages == null && mMessageCollectionEnabled) {
            mMessages = new ArrayList<String>();
        }
    }
    
    /**
     *  Collects an error message or passes the error message to <code>
     *  super.emitErrorMessage(...)</code>.
     *
     *  The actual behaviour depends on whether collecting error messages
     *  has been enabled or not.
     *
     *  @param pMessage  The error message.
     */
     @Override
    public void emitErrorMessage(String pMessage) {
        if (mMessageCollectionEnabled) {
            mMessages.add(pMessage);
        } else {
            super.emitErrorMessage(pMessage);
        }
    }
    
    /**
     *  Returns collected error messages.
     *
     *  @return  A list holding collected error messages or <code>null</code> if
     *           collecting error messages hasn't been enabled. Of course, this
     *           list may be empty if no error message has been emited.
     */
    public List<String> getMessages() {
        return mMessages;
    }
    
    /**
     *  Tells if parsing a Java source has caused any error messages.
     *
     *  @return  <code>true</code> if parsing a Java source has caused at least one error message.
     */
    public boolean hasErrors() {
        return mHasErrors;
    }
   
   
}
compilationUnit[SymbolTable symtab]
@init {this.symtab = symtab;}       // set the parser's field
    : toplevelStatement* {System.out.println("Compilation Unit ends");}
//    assignStatement+ // recognize at least one variable declaration
    ; 

toplevelStatement
  : includeStatement
  | whenStatement
  ;

includeStatement
  : INCLUDE STRING
    -> ^(INCLUDE STRING )
  ; 

whenStatement
  :WHEN {System.out.println("whenStatement");} expression{System.out.println("expression");}  DO
    statement *
   (AFTER 
    statement)?
    ->^(expression WHEN DO
    statement *
    ^(AFTER 
    statement)?)
  ;
  
statement
  : assignStatement {System.out.println("assign Statement");}  
  | blockStatement 
  | revertStatement
  | printStatement
  | errorStatement
  | warnStatement
  | QUIT
  ;

assignStatement 
  : identifier ':=' expression
//  {
//      System.out.println("line  "+$IDENT.getLine()+":  def  "+$IDENT.text);
//      VariableSymbol  vs  =  new  VariableSymbol($IDENT.text,$type.tsym);
//      symtab.define(vs);
//  } 
    -> ^(EXPR identifier expression)
  ;
  
  
//assignStatement 
//  :type IDENT  ':=' expression
//   {System.out.println("THIS IS" + $identifier.type );}
//  {
//			System.out.println("line  "+$IDENT.getLine()+":  def  "+$IDENT.text);
//			VariableSymbol  vs  =  new  VariableSymbol($IDENT.text,$type.tsym);
//			symtab.define(vs);
//			System.out.println("here ");
//  }  
//  ;
  
  
  
//varDeclaration
//:      type  ID  ('=' expression)?  ';'
////  E.g.,  "int  i  =  2;",  "int  i;"
//{
//System.out.println("line  "+$ID.getLine()+":  def  "+$ID.text);
//VariableSymbol  vs  =  new  VariableSymbol($ID.text,$type.tsym);
//symtab.define(vs);
//}
//;

blockStatement
  : '(' statement (';' statement)* (';')? ')'  
  -> ^( statement '('  (';' statement)* (';')? ')')  
  ;
// To Check 
// multi-alternatives complains ???
  
revertStatement
  : REVERT identifier
  -> ^(REVERT identifier)
  ; 

printStatement
  : PRINT STRING
  -> ^(PRINT STRING)
  ;

errorStatement
  : ERROR STRING 
  -> ^(ERROR STRING )
  ;

warnStatement
  : WARN STRING
  -> ^(WARN STRING)
  ;
  
expression
  : irexpression
  -> ^(EXPR irexpression)
  ;

irexpression 
  : singletonexpression
  | unaryexpression
  | binaryexpression
//  -> ^(EXPR (singletonexpression|unaryexpression|binaryexpression))
  ;

    
singletonexpression
  : booleanliteral
  | numericliteral
  | timeliteral
  | TIME
  | identifier 
  ;
  
booleanliteral
  : TRUE | FALSE
  ;
  
numericliteral
  : numeral ('.' numeral )?  (exponent)?
  ;

timeliteral
  : 'Time:' numericliteral ('{')? (timeunit) ('}')?
// TODO, needs to lookahead
  ;

timeunit
  : ('microseconds'  | 'us')
  | ('milliseconds'  | 'ms')
  | ('second'        | 's' )
  | ('minutes'       | 'm' )
  | ('hours'         | 'h' )
  ;

exponent
  :('E'|'e') ('+'|'-')? numeral
  ;

numeral
  : INTEGER
  ;

identifier 
  : (domain)? type IDENT 
  { System.out.println("line  "+$IDENT.getLine()+":  def  "+$IDENT.text);
    VariableSymbol  vs  =  new  VariableSymbol($IDENT.text,$type.tsym);
    symtab.define(vs);
//    System.out.println("line "+$IDENT.getLine()+": ref to "+ symtab.resolve($IDENT.text));
   }
//  {$type.setType(INDEX);}
  ;

domain
  : DE | CT
  ;
  
unaryexpression
  : unaryoperator expression
  ->  ^(EXPR expression)
  ;
  
unaryoperator
  : 'add'
  | 'minus'
  | 'abs'
  | 'floor'
  | 'ceil'
  ;
  
binaryexpression
  : singletonexpression binaryoperator expression
  -> singletonexpression
   ^(EXPR expression)
  ;

binaryoperator
  : '+'
  | '-'
  | '*'
  | '/'
  | 'div'
  | 'mod'
  | '<'
  | '<='
  | '>'
  | '>='
  | '='
  | '<>'
  | 'or'
  | 'and'
  | '=>'
  | '<=>'
  | 'for'
  ;
  
//type: primitiveType
//    | 'struct' IDENT -> IDENT
//  ;

//primitiveType
//  :   'float'
//    | 'int'
//    | 'char'
//    | 'boolean'
//    | 'void'
//    ;
type returns [Type tsym]
//@after { // $start is the first tree node matched by this rule
////    System.out.println("line "+$start.getLine()+": ref "+$tsym.getName());
//    System.out.println(": ref "+$tsym.getName());
//}
    :   REAL    {System.out.println("real here!!!");if(symtab == null) System.out.println("null!"); $tsym= (Type)symtab.resolve("real");System.out.println("real end!!!");}
    |   INT     {$tsym = (Type)symtab.resolve("int");} 
    |   BOOL    {$tsym = (Type)symtab.resolve("boolean");}
    |   TIME    {$tsym = (Type)symtab.resolve("TIME");}
    ; 



//type returns [Type type]
//  :   REAL {$type = $REAL.getName();System.out.println("THIS IS" + $REAL.getName() );}
//    | INT  {$type = $INT.getName(); System.out.println("THIS IS" + $INT.getText() );}
//    | BOOL {$type = $BOOL.getName();System.out.println("THIS IS" + $BOOL.getText() );}
//    | TIME {$type = $TIME.getName();System.out.println("THIS IS" + $TIME.getText() );}
//    ;    


//type returns [String type]
//  : REAL {$type = $REAL.getText();System.out.println("THIS IS" + $REAL.getText() );}
//  | BOOL {$type = $BOOL.getText();System.out.println("THIS IS" + $BOOL.getText() );}
//  | INT  {$type = $INT.getText(); System.out.println("THIS IS" + $INT.getText() );}
//  | TIME {$type = $TIME.getText();System.out.println("THIS IS" + $TIME.getText() );}
//  ;  






STRING
  : '"'
    { StringBuilder b = new StringBuilder(); }
    ( '"' '"'       { b.appendCodePoint('"');}
    | c=~('"'|'\r'|'\n')  { b.appendCodePoint(c);}
    )*
    '"'
   ;
  
FORMATTEDSTRING
  : STRING 
  ('%' IDENT (',' IDENT )?)?
  ;

fragment LETTER : ('a'..'z' | 'A'..'Z') ;
fragment DIGIT : '0'..'9';
INTEGER : DIGIT+ ;
IDENT: LETTER (LETTER | DIGIT)*;
WS : (' ' | '\t' | '\n' | '\r' | '\f')+ {$channel = HIDDEN;};
COMMENT : '//' .* ('\n'|'\r') {$channel = HIDDEN;};

