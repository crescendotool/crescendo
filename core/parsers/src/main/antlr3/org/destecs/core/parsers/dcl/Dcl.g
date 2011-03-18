grammar Dcl;

options {
  language = Java;
  output = AST;
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
}
 
@header {
package org.destecs.core.parsers.dcl;

import org.destecs.core.dcl.Dcl;
import org.destecs.core.dcl.Action;
import org.destecs.core.dcl.ScriptFactory;
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
private boolean mMessageCollectionEnabled = false;
    private boolean mHasErrors = false;
    private ScriptFactory dcl = new ScriptFactory();
    private List<String> mMessages;
    private List<RecognitionException> mExceptions = new ArrayList<RecognitionException>();

    public Dcl getScript()
    {
        return dcl.getScript();
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


toplevelStatement
  : includeStatement
  | whenStatement
  ;

includeStatement
  : INCLUDE STRING
  ; 
  
whenStatement
  :WHEN expression DO
    statement *
   (AFTER 
    statement)?';'
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
  : identifier ':=' expression ';'
  ;

blockStatement
  : '(' statement ( (';' statement)* (';')? (')')+ ) ';'
  ;
  
revertStatement
  : REVERT identifier ';'
  ;

printStatement
  : PRINT STRING ';'
  ;

errorStatement
  : ERROR STRING ';'
  ;

warnStatement
  : WARN STRING ';'
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
  :(domain)? IDENT
  ;

domain
  : DE | CT
  ;
  
unaryexpression
  : unaryoperator expression
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

