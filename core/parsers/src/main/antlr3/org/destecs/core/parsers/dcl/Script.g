grammar Script;

options {
//  k=2;
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
import org.destecs.core.dcl.ScriptFactory;
import org.destecs.core.dcl.Script;
import org.destecs.core.dcl.Action;
//import org.destecs.core.dcl.Action.Condition;
import org.destecs.core.dcl.Interpreter;
//import org.destecs.core.simulationengine.SimulationEngine.Simulator;

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
	 
	 Action action;
   public ScriptParser(TokenStream input, Action action) {
       this(input);
       this.action = action;
   }
   
   Interpreter interp;
   public ScriptParser(TokenStream input, Interpreter interp) {
       this(input);
       this.interp = interp;
   }

    private boolean mMessageCollectionEnabled = false;
    private boolean mHasErrors = false;
    private ScriptFactory script = new ScriptFactory();
    private List<String> mMessages;
    private List<RecognitionException> mExceptions = new ArrayList<RecognitionException>();

    public Script getScript()
    {
        return script.getScript();
    }
  
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
//    {
//      script.addAction(action);    // script here is scriptfactory, the first element of the queue won't be null
//     }
    ; 

//***Statements start***
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
       statement)?) //replace by ->
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
  : 
//  identifier ':=' expression
//   {script.addAction(action);
//    action = new Action( $singletonexpression.value.toString(),
//            Double.valueOf($expression.value.toString()).doubleValue()            
//            );
//   }  
    domain type IDENT ':=' expression
    {
//      if (script.actions.size()!= 0){
        action = new Action($domain.value.toString(), $IDENT.text.toString(),
                Double.valueOf($expression.value.toString()).doubleValue()            
                );
        script.addAction(action);
//      }
//	    else{
//		    action = new Action($domain.value.toString(), $IDENT.text.toString(),
//		            Double.valueOf($expression.value.toString()).doubleValue()            
//		            );
//		    
//	    }
    }
//    -> ^(':=' identifier expression)
      -> ^(':=' domain type IDENT  expression)
  ;

blockStatement  
  : '(' statement (';' statement)* (';')? ')'  
//  {script.addAction(action);} //TO-DO
  ;
  
revertStatement
  : REVERT identifier
  -> ^(REVERT identifier)
  ; 

printStatement
  : PRINT STRING {interp.print($STRING.text);}
  -> ^(PRINT STRING)
  ;

errorStatement
  : ERROR STRING {interp.print($STRING.text);}
  -> ^(ERROR STRING)
  ;

warnStatement
  : WARN STRING {interp.print($STRING.text);}
  -> ^(WARN STRING)
  ;
//***Statements end***


//***Expression start***
expression returns [Object value]
  : singletonexpression {$value = $singletonexpression.value ;}
  | unaryexpression     {$value = $unaryexpression.value ;}
  | binaryexpression    {$value = $binaryexpression.value ;}
//  {script.addAction(action);}  
  ;
    
unaryexpression returns [Object value]
  : unaryoperator expression
  // TO-DO
  ->  ^(EXPR unaryoperator expression)
  ;
  
unaryoperator
  : 'add'
  | 'minus'
  | 'abs'
  | 'floor'
  | 'ceil'
  ;
  
binaryexpression returns [Object value]
  : 
//    singletonexpression binaryoperator expression
//   {        
//         $value = $singletonexpression.value;
//    }      
    singletonexpression binaryoperator expression
    {
      $value = $expression.value;
      action = new Action($singletonexpression.value.toString(),
                Double.valueOf($expression.value.toString()).doubleValue()            
                );    
      script.addAction(action);  
    }
  -> singletonexpression
   ^(EXPR binaryoperator expression)
  ;
  

singletonexpression returns [Object value]
  : booleanliteral {$value = $booleanliteral.value;}
  | numericliteral {$value = Double.parseDouble($numericliteral.text);}
  | timeliteral    {$value = $timeliteral.value;}
  | TIME           {$value = $TIME.text;}
  | identifier     {$value = $identifier.value;}// for the binaryexpression
  ;

//***Expression start***
  
booleanliteral returns [Object value]
  : TRUE  {$value = $TRUE.text ;}
  | FALSE {$value = $FALSE.text ;}
  ;
  
numericliteral
  : numeral ('.' numeral )?  (exponent)?
  ;

timeliteral returns [Object value]
  :  numericliteral ('{')? (timeunit) ('}')?
//  'Time:' numericliteral ('{')? (timeunit) ('}')?
// TODO, needs to lookahead
  {$value = Double.parseDouble($numericliteral.text);
  }
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

identifier returns [Object value]
  : 
  (domain)? type IDENT
//  domain type IDENT
//  {
//////  new Action.Name($ident.text);
////    action.Name($IDENT.text);  
//      id = $IDENT.text;
//  }
    {$value = $IDENT.text;}
  ;

domain returns [Object value]
  : DE {$value = $DE.text;}
  | CT {$value = $CT.text;}
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
  | '=' // equals
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

//ident:
//    IDENT
//    ;
 
STRING
  :
//  '"'
//  ( '"' '"'
//  | ~('"'|'\r'|'\n')
//  )*
//  '"'
//  ;
  
    '"'
    .*
    '"'
    ;
    



  
FORMATTEDSTRING
  : STRING 
  ('%' IDENT (',' IDENT)?)?
  ;

fragment LETTER : ('a'..'z' | 'A'..'Z') ;
fragment DIGIT : '0'..'9';
INTEGER : DIGIT+ ;
IDENT: LETTER (LETTER | DIGIT)*;
WS : (' ' | '\t' | '\n' | '\r' | '\f')+ {$channel = HIDDEN;};
COMMENT : '//' .* ('\n'|'\r') {$channel = HIDDEN;};

