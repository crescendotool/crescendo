grammar Contract;

options{
	language=Java;
}

tokens {
	CONTRACT = 'contract';
	DESIGN_PARAMETER = 'shared_design_parameter';
	SDP = 'sdp';
	MONITORED = 'monitored';
	CONTROLLED = 'controlled';
	REAL = 'real';
	BOOL = 'bool';
	EVENT = 'event';
	END = 'end';
	ASSIGN = ':=';
	MATRIX = 'matrix';
}

@header {
package org.destecs.core.parsers.contract;

import org.destecs.core.contract.Contract;
import org.destecs.core.contract.Variable;
import org.destecs.core.contract.Variable.VariableType;
import org.destecs.core.contract.Variable.DataType;
import org.destecs.core.contract.ContractFactory;
import org.destecs.core.contract.ArrayVariable;
import java.util.Vector;

}

@lexer::header{  
package org.destecs.core.parsers.contract;
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
    private ContractFactory contract = new ContractFactory();
    private List<String> mMessages;
    private List<RecognitionException> mExceptions = new ArrayList<RecognitionException>();

    public Contract getContract()
    {
        return contract.getContract();
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

BOOL_VAL 
	: 'true'
	| 'false'
	;

COMMENT
    :   '--' ~('\n'|'\r')* '\r'? '\n'? {$channel=HIDDEN;}
    |   '//' ~('\n'|'\r')* '\r'? '\n'? {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;
	
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;
    

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
    


contract 
//	: CONTRACT a=ID body* END ID EOF 
//	{ contract.setName($a.getText()); }
	: body* EOF 
	;
	
body 
	: parameters 
	| variables
	| events
	;

parameters 
	: sharedDesignParamater t=type ID ';' 
	 { contract.addVariable(new Variable($ID.text,VariableType.SharedDesignParameter,DataType.valueOf(t),null,$ID.getLine())); }
	;


sharedDesignParamater
  : 'design_parameter'
  | 'sdp'
  ;
  
variables 
	: k=kind t=type ID ASSIGN v=value ';' 
	 {contract.addVariable(new Variable($ID.text,k,DataType.valueOf(t),v,$ID.getLine()));}
	| k=kind MATRIX ID sizes=arrayspec ';'
	 {contract.addVariable(new ArrayVariable($ID.text,k,null,sizes,$ID.getLine()));}
	;

arrayspec returns [List<Integer> sizes]
@init{
  sizes = new Vector<Integer>();
  }
  : '[' a=INT {$sizes.add(new Integer($a.text));}  (',' b=INT {$sizes.add(new Integer($b.text));})* ']'
  ;

events 

  : EVENT ID ';' 
  {contract.addEvent($ID.text);}
  
	;	

type returns [String type]
  : REAL {$type = $REAL.getText();}
	| BOOL {$type = $BOOL.getText();}
	;

value returns [Object value] 
	: FLOAT {$value = Float.parseFloat($FLOAT.getText());}
	| BOOL_VAL {$value = Boolean.parseBoolean($BOOL_VAL.getText());}
	;

kind returns [VariableType kind]
	: MONITORED {$kind = VariableType.Monitored;}
	| CONTROLLED {$kind = VariableType.Controlled;}
	;
