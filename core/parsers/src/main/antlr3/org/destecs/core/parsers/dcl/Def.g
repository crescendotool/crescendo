// START: header
tree grammar Def;
options {
  tokenVocab = Dcl;
  ASTLabelType = CymbolAST;
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
import org.destecs.core.dcl.CymbolAST;
import org.destecs.core.dcl.VariableSymbol;

//import org.destecs.core.dcl.ArrayType;
import org.destecs.core.dcl.Symbol;
}


@members {
    SymbolTable symtab;
//    Scope currentScope;
//    MethodSymbol currentMethod;
    public Def(TreeNodeStream input, SymbolTable symtab) {
        this(input);
        this.symtab = symtab;
//        currentScope = symtab.globals;
    }
}
// END: header


//// START: var
//varDeclaration // global, parameter, or local variable
//    :   ^((FIELD_DECL|VAR_DECL|ARG_DECL) type ID .?)
//        {
//        //System.out.println("line "+$ID.getLine()+": def "+$ID.text);
//        VariableSymbol vs = new VariableSymbol($ID.text,$type.type);
//        vs.def = $ID;            // track AST location of def's ID
//        $ID.symbol = vs;         // track in AST
//        currentScope.define(vs);
//        }
//    ;
//// END: field

///** Not included in tree pattern matching directly.  Needed by declarations */
type returns [Type type]
	:	^('[]' typeElement)	
//	{$type = new ArrayType($typeElement.type);}
	|	typeElement 	    {$type = $typeElement.type;}
	;

typeElement returns [Type type]
//@init {CymbolAST t = (CymbolAST)input.LT(1);}
//@after {
//    t.symbol = currentScope.resolve(t.getText()); // return Type
//    t.scope = currentScope;
//    $type = (Type)t.symbol;
//}
    :   'real'
    |   'int'
    |   TIME
    |   'boolean'
    ;

