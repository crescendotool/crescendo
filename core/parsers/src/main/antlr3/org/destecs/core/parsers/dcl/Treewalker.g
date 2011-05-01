tree grammar Treewalker;

options {
  language = Java;
  tokenVocab = Dcl;
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
  ;

statement
  : assignStatement 
  ;

assignStatement 
  : ^(ASSIGN identifier expression)
  {if($identifier.type!=$expression.type) 
  System.out.println("At Line: " + $ASSIGN.getLine()+ ",  type doesn't match! Identifier type is: "+$identifier.type.getName()+",while expression type is: " + $expression.type.getName());
  }
  ;
  
expression returns[Type type]
  : singletonexpression {$type=$singletonexpression.type;}
  | unaryexpression{$type=$unaryexpression.type;}
  | binaryexpression {$type=$binaryexpression.type;}
  ;
    
singletonexpression returns[Type type]
  : booleanliteral {$type= SymbolTable._boolean;}
  | numericliteral {$type= SymbolTable._int;}
  | timeliteral    {$type= SymbolTable._TIME;}
  | TIME           {$type= SymbolTable._TIME;}
  | identifier     {$type= $identifier.type;}
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
  :  ^(EXPR expression){$type= $expression.type;}
  ;
  
unaryoperator
  : 'add'
  | 'minus'
  | 'abs'
  | 'floor'
  | 'ceil'
  ;
  
binaryexpression returns[Type type]
  : singletonexpression ^(EXPR expression)
  {if ($singletonexpression.type==$expression.type) 
      $type=$singletonexpression.type;
   else{
   System.out.println( "At Line: " + $EXPR.getLine()+ ", binary operator type doesn't match! lhs type is: "+$singletonexpression.type.getName()+",while rhs type is: " + $expression.type.getName());
//    System.out.println( "type doesn't match!");
   }   
  }
   
//  {$identifier.type==$expression.type }
//  {$singletonexpression.type==$expression.type;}
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

type returns [Type type]
    :   REAL  
       {$type= SymbolTable._real;}
    |   INT
       {$type= SymbolTable._int;} 
    |   BOOL 
       {$type= SymbolTable._boolean;}
    |   TIME   
       {$type= SymbolTable._TIME;}
    ; 
