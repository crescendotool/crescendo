tree grammar Treewalker;

options {
  language = Java;
  tokenVocab = Dcl;
  ASTLabelType = CymbolAST;
//  ASTLabelType = CommonTree;
  filter = true;
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

// START: root
bottomup // match subexpressions innermost to outermost
    :   exprRoot // only match the start of expressions (root EXPR)
    ;

exprRoot // invoke type computation rule after matching EXPR
    :   ^(EXPR expression) { $EXPR.evalType = $expression.type ;System.out.println("EXPR TYPE ="+ $EXPR.evalType);} // annotate AST
    ;
// END: root


expression returns [Type type]
@after { $start.evalType  = $type; } // do after any alternative
    :   'true'      {
    $type = SymbolTable._boolean;
    System.out.println("expression type IS boolean");}
    |   'false'      {
    $type = SymbolTable._boolean;   
    System.out.println("expression type IS boolean");}
    |   INTEGER     {
    $type = SymbolTable._int; 
       System.out.println("expression type IS int");}
    |   'TIME'      {
    $type = SymbolTable._TIME; 
      System.out.println("expression type IS TIME");}
//    |   'when'      {$type = symtab.unot($irexpression.start);   System.out.print("******Just test******");}
//    |   'when'      {$type = symtab.irexpression($irexpression.start);System.out.print("******Just test******");}
    ;

// START: binaryOps
//binaryOps returns [Type type]
//@after { $start.evalType = $type; }
//    :   ^(bop a=irexpression  b=irexpression )   {$type=symtab.bop($a.start, $b.start);}
//    |   ^(relop a=irexpression  b=irexpression ) {$type=symtab.relop($a.start, $b.start);}
//    |   ^(eqop a=irexpression  b=irexpression )  {$type=symtab.eqop($a.start, $b.start);}
//    ;
// END: binaryOps

// START: arrayRef
//arrayRef returns [Type type]
//    :   ^(INDEX ID irexpression)
//        {
//        $type = symtab.arrayIndex($ID, $irexpression.start);
//        $start.evalType = $type; // save computed type
//        }
//    ;
// END: arrayRef

// START: call
//call returns [Type type]
//@init {List args = new ArrayList();}
//    :   ^(CALL ID ^(ELIST (irexpression {args.add($irexpression.start);})*))
//        {
//        $type = symtab.call($ID, args);
//        $start.evalType = $type;
//        }
//    ;
// END: call

// START: member
//member returns [Type type]
//    :   ^('.' irexpression ID)           // match expr.ID subtrees
//        { // $expr.start is root of tree matched by expr rule
//        $type = symtab.member($irexpression.start, $ID); 
//        $start.evalType = $type; // save computed type
//        }
//    ;
// END: member

bop :   '+' | '-' | '*' | '/' ;

relop:  '<' | '>' | '<=' | '>=' ;

eqop:   '!=' | '==' ;
