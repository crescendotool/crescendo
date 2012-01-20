grammar Script;

options {
//  k=2;
  language = Java;
//  output = AST;
//  ASTLabelType= CommonTree;
//   ASTLabelType = CymbolAST;
}

tokens{
  WHEN = 'when';  
//  FOR = 'for';
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
  //EXPR;
  
  PLUS = '+';
  MINUS = '-';
  MULTIPLU =  '*';
  DIVIDE = '/' ;
  DIV = 'div' ;
  MOD = 'mod' ;
  LESSS = '<' ;
  LESSEQUAL= '<=' ;
  GREATER = '>';
  GREATEREQUAL= '>=' ;
  EQUAL = '=';
  DIFFERENT = '<>';
  OR = 'or' ;
  AND =  'and' ;
  IMPLIES = '=>' ;
  EQUIV= '<=>' ;
  FOR= 'for'; 
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
import org.destecs.script.ast.*;
import org.destecs.script.ast.node.*;
import org.destecs.script.ast.expressions.*;
import org.destecs.script.ast.expressions.unop.*;
import org.destecs.script.ast.expressions.binop.*;
import org.destecs.script.ast.statement.*;
import org.destecs.script.ast.types.*;
import org.destecs.script.ast.preprocessing.*;
import java.util.Vector;
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
root returns [List<INode> nodes]
 @init
    {
      $nodes = new Vector<INode>();
    }
  : (toplevelStatement {$nodes.add($toplevelStatement.value);})* EOF
  ;

//***Statements start***
toplevelStatement returns [INode value]
  : includeStatement ';' {$value = $includeStatement.value;}
  | whenStatement ';' {$value = $whenStatement.value;}
  ;

includeStatement returns [AScriptInclude value]
  : INCLUDE STRING
{
  $value = new AScriptInclude($STRING.text);
}
  ; 

whenStatement returns [AWhenStm value]
  :WHEN expression DO
    '('  statementList ')'
   (AFTER '(' revertStatementList ')')?  
    {
      $value = new AWhenStm($expression.value,$statementList.valueList,$revertStatementList.valueList);
    }       
  ;
statementList returns [List<PStm> valueList]
 @init
    {
      $valueList = new Vector<PStm>();
    }
  : (statement ';'{$valueList.add($statement.value);})*
  ;
  
  
revertStatementList returns [List<ARevertStm> valueList]
 @init
    {
      $valueList = new Vector<ARevertStm>();
    }
  : (revertStatement ';' {$valueList.add($revertStatement.value);})*
  ;  
  
  
  
statement returns [PStm value]
  : assignStatement {$value = $assignStatement.value;}
  | revertStatement {$value = $revertStatement.value;}
  | printStatement {$value = $printStatement.value;}
  | errorStatement {$value = $errorStatement.value;}
  | warnStatement {$value = $warnStatement.value;}
  | QUIT {$value = new AQuitStm();}
  ;

assignStatement returns [AAssignStm value]
  : 
    domain type IDENT ':=' expression
  {
    $value = new AAssignStm($domain.value,$IDENT.text,$expression.value);
  }
  ;

revertStatement returns [ARevertStm value]
  : REVERT identifier
  {
    $value = new ARevertStm($identifier.text);
  }
  ; 

printStatement returns [APrintMessageStm value]
  : PRINT STRING 
  {
    $value = new APrintMessageStm($STRING.getText());
  }
  ;

errorStatement returns [AErrorMessageStm value]
  : ERROR STRING 
  {
    $value = new AErrorMessageStm($STRING.getText());
  }
  ;

warnStatement returns [AWarnMessageStm value]
  : WARN STRING 
  {
    $value = new AWarnMessageStm($STRING.getText());
  }
  ;
//***Statements end***


//***Expression start***
expression returns [PExp value]
  : expression0 {$value = $expression0.value ;}
  ;

expression0 returns [PExp value]
  : left=expression1 (expressionOp right=expression0)? 
  { if($right.value==null) 
      { $value = $left.value;}
      else{$value = new ABinaryExp($left.value, $expressionOp.value, $right.value);}
    }
  ;
  
expressionOp returns [PBinop value]
  : AND     {$value = new AAndBinop();}
  | OR      {$value = new AOrBinop();}//TODO sub rule or to make the precedence correct.
  | IMPLIES {$value = new AImpliesBinop();}
  | EQUIV   {$value = new AEquivBinop();}
  ;
  
expression1 returns [PExp value]
    : left=expression2 (expression1Op right=expression2)? 
    { if($right.value==null) 
      { $value = $left.value;}
      else{$value = new ABinaryExp($left.value, $expression1Op.value, $right.value);}
    }
  ;
  
expression1Op returns [PBinop value]
  : LESSS         {$value = new ALessThanBinop();}
  | LESSEQUAL     {$value = new ALessEqualBinop();}
  | GREATER       {$value = new AMoreThanBinop();}
  | GREATEREQUAL  {$value = new AMoreEqualBinop();}
  | EQUAL         {$value = new AEqualBinop();}
  | DIFFERENT     {$value = new ADifferentBinop();}
  ;
  
expression2 returns [PExp value]
  : left=expressionAtom expression2Op right=expression2 {$value = new ABinaryExp($left.value, $expression2Op.value, $right.value);}
  | unaryoperator unexp=expression2 {$value =new AUnaryExp($unaryoperator.value,$unexp.value);}
  | expressionAtom {$value = $expressionAtom.value;}
  ;

expression2Op returns [PBinop value]
  : PLUS      {$value = new APlusBinop();}
  | MINUS     {$value = new AMinusBinop();}
  | MULTIPLU  {$value = new AMultiplyBinop();}
  | DIVIDE    {$value = new ADivideBinop();}
  | MOD       {$value = new AModBinop();}
  | DIV       {$value = new ADivBinop();}
  ;  
 
//Do not delete - the unaryexpression has a side effect on the parser that makes the expression2 work.
unaryexpression returns [AUnaryExp value]
  : unaryoperator expression
  {$value = new AUnaryExp($unaryoperator.value,$expression.value);}
  ;
  
unaryoperator returns [PUnop value]
  : 'add'   {$value = new AAddUnop();}
  | 'minus' {$value = new AMinusUnop();}
  | 'abs'   {$value = new AAbsUnop();}
  | 'floor' {$value = new AFloorUnop();}
  | 'ceil'  {$value = new ACeilUnop();}
  ;
  
expressionAtom returns [SSingleExp value]
  : booleanliteral {$value = new ABoolSingleExp( $booleanliteral.value);}
  | numericliteral {$value = new ANumericalSingleExp(Double.parseDouble($numericliteral.text));}
  | timeliteral    {$value = $timeliteral.value;}
  | TIME           {$value = new ASystemTimeSingleExp();}
  | identifier     {$value = $identifier.value;}// for the binaryexpression
  ;
  
//***Expression start***
  
booleanliteral returns [Boolean value]
  : TRUE  {$value = true ;}
  | FALSE {$value = false;}
  ;
  
numericliteral
  : numeral ('.' numeral )?  (exponent)?
  ;

timeliteral returns [ATimeSingleExp value]
  :  numericliteral ('{')? (timeunit) ('}')?
  {$value = new ATimeSingleExp(Double.parseDouble($numericliteral.text), $timeunit.value);
  }
  ;

timeunit returns [PTimeunit value]
  : ('microseconds'  | 'us' {$value = new AUsTimeunit();}) 
  | ('milliseconds'  | 'ms') {$value = new AMsTimeunit();}
  | ('second'        | 's' ) {$value = new ASTimeunit();}
  | ('minutes'       | 'm' ) {$value = new AMTimeunit();}
  | ('hours'         | 'h' ) {$value = new AHTimeunit();}
  ;

exponent
  :('E'|'e') ('+'|'-')? numeral
  ;

numeral
  : INTEGER
  ;

identifier returns [AIdentifierSingleExp value]
  : (domain)? type IDENT
    {$value = new AIdentifierSingleExp($domain.value, $type.value,$IDENT.text);}
  ;

domain returns [PDomain value]
  : DE {$value = new ADeDomain();}
  | CT {$value = new ACtDomain();}
  ;

type returns[PType value]
    :   REAL  {$value = new ARealType();}
    |   INT   {$value = new AIntType();}
    |   BOOL  {$value = new ABoolType();}
    |   TIME  {$value = new ATimeType();}
    ; 

 
STRING : '"' .* '"';
    
fragment LETTER : ('a'..'z' | 'A'..'Z') ;
fragment DIGIT : '0'..'9';
INTEGER : DIGIT+ ;
IDENT: LETTER (LETTER | DIGIT)*;
WS : (' ' | '\t' | '\n' | '\r' | '\f')+ {$channel = HIDDEN;};
COMMENT
    :   '--' ~('\n'|'\r')* '\r'? '\n'? {$channel=HIDDEN;}
    |   '//' ~('\n'|'\r')* '\r'? '\n'? {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;
