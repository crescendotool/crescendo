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
  ASSIGN=':=';
  EXPR; 
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
    : toplevelStatement* 
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
  :WHEN expression DO
    statement *
   (AFTER 
    statement)?
    ->^(WHEN expression DO 
      statement *
       ^(AFTER
       statement))
  ;
  
statement
  : assignStatement
  | blockStatement 
  | revertStatement
  | printStatement
  | errorStatement
  | warnStatement
  | QUIT
  ;

assignStatement 
  : identifier ':=' expression
    -> ^(':=' identifier expression)
  ;

blockStatement
  : '(' statement (';' statement)* (';')? ')'  
  ;
  
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
  : singletonexpression
  | unaryexpression
  | binaryexpression
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
  

type 
    :   REAL  
    |   INT
    |   BOOL 
    |   TIME   
    ; 
 
STRING
  :
   '"'
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

