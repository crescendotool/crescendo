grammar Scenario;

options {
  language = Java;
}


tokens{
  CT = 'CT';
  DT = 'DT';
  ASSIGN = ':=';
}

@header{
package org.destecs.core.parsers.scenario;

import org.destecs.core.scenario.Action;
import org.destecs.core.scenario.Scenario;
import org.destecs.core.scenario.ScenarioFactory;
}

@lexer::header{
package org.destecs.core.parsers.scenario;
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


@members{
 private boolean mMessageCollectionEnabled = false;
    private boolean mHasErrors = false;
    private ScenarioFactory scenario = new ScenarioFactory();
    private List<String> mMessages;
    private List<RecognitionException> mExceptions = new ArrayList<RecognitionException>();

    public Scenario getScenario()
    {
        return scenario.getScenario();
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


COMMENT
    :   '--' ~('\n'|'\r')* '\r'? '\n'? {$channel=HIDDEN;}
    |   '//' ~('\n'|'\r')* '\r'? '\n'? {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;
    
ID  : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;
    
fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

start : action* EOF
      ;
      
action 
      : t=FLOAT simType '.' ID ASSIGN v=FLOAT ';'
      { scenario.addAction(
        new Action(
          Double.parseDouble($t.text),
          Action.Simulator.valueOf($simType.text),
          $ID.text,
          Double.parseDouble($v.text)
          )
        );
      }
      ; 

simType 
      : DT
      | CT
      ;
