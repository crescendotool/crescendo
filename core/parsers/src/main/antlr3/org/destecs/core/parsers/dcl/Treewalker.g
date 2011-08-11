tree grammar Treewalker;

options {
//  k=1;
  language = Java;
  tokenVocab = Script;
//  ASTLabelType = CymbolAST;
  ASTLabelType = CommonTree;
//  filter = true;
}

 
@header {
package org.destecs.core.parsers.dcl;

import org.destecs.core.dcl.Action;
import org.destecs.core.dcl.ScriptFactory;
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
import org.destecs.core.dcl.Script;
import org.destecs.core.dcl.VariableSymbol;

//import org.destecs.core.dcl.ArrayType;
import org.destecs.core.dcl.Symbol;

}

@members {

    SymbolTable symtab;
    public Treewalker(TreeNodeStream input, SymbolTable symtab) {
        this(input);
        this.symtab = symtab;       
    }
    
//    private ScriptFactory script = new ScriptFactory();
//    public Script getScript()
//    {
//        return script.getScript();
//    }
    
//    Action action;
//    public void ScriptParser(TokenStream input, Action action) {
//       this(input);
//       this.action = action;
//   }
     
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
       
program // match subexpressions innermost to outermost
    : whenStatement
       |assignStatement
    ;
whenStatement
  :^(WHEN expression DO 
      statement *
       ^(AFTER 
       statement))
       //think about the alternatives 
  ;

statement
  : assignStatement 
  ;

assignStatement 
  : ^(ASSIGN identifier expression)
//  {if($identifier.type!=$expression.type) 
//   System.out.println("At Line: " + $ASSIGN.getLine()+ ",  type doesn't match! Identifier type is: "+$identifier.type.getName()+",while expression type is: " + $expression.type.getName());
//  }
    {
    if($identifier.type == $expression.type)
     System.out.println("checked");
    ;}
  ;
  
expression returns[Type type]
  : singletonexpression {$type = $singletonexpression.type;}
  | unaryexpression{$type = $unaryexpression.type;}
  | binaryexpression {$type = $binaryexpression.type;}
  ;
    
singletonexpression returns[Type type]
  : booleanliteral {$type = SymbolTable._boolean;}
  | numericliteral {$type = SymbolTable._int;}
  | timeliteral    {$type = SymbolTable._TIME;}
  | TIME           {$type = SymbolTable._TIME;}
  | identifier     {$type = $identifier.type;}
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

identifier returns [Type type]
  : (domain)? type IDENT 
  {   
     $type= $type.type;
   }
  ;

domain
  : DE | CT
  ;
  
unaryexpression returns [Type type]
  :  ^(EXPR unaryoperator expression){$type = $expression.type;}
  ;
  
unaryoperator
  : 'add'
  | 'minus'
  | 'abs'
  | 'floor'
  | 'ceil'
  ;
  
binaryexpression returns[Type type]
  : singletonexpression ^(EXPR binaryoperator expression)
  {
	  if ($singletonexpression.type == $expression.type) 
	      {
	         $type = $expression.type;
	      }
//	   else{
//	         System.out.println( "At Line: " + $EXPR.getLine()+ ", binary operator type doesn't match! lhs type is: "+$singletonexpression.type.getName()+",while rhs type is: " + $expression.type.getName());
//	   	   }   
  }   
  ;

binaryoperator 
  : boolOps 
  | numericOps 
  ;
 
boolOps
  : '<'
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
//  {}
  ;
 
numericOps 
  : '+'
  | '-'
  | '*'
  | '/'
  | 'div'
  | 'mod'
//  {}
  ;
  
type returns [Type type]
    :   REAL  
       {$type = SymbolTable._real;}
    |   INT
       {$type = SymbolTable._int;} 
    |   BOOL 
       {$type = SymbolTable._boolean;}
    |   TIME   
       {$type = SymbolTable._TIME;}
    ; 
