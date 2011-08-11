package org.destecs.core.dcl;
/***
 * Excerpted from "Language Implementation Patterns",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpdsl for more book information.
***/
import java.util.*;

import org.antlr.runtime.tree.CommonTree;
public class SymbolTable implements Scope { // single-scope symtab
	   public static final int tUSER = 0; // user-defined type
	    public static final int tBOOLEAN = 1;
	    public static final int tINT = 2;
	    public static final int tREAL = 3;
	    public static final int tTIME = 4;

	    public static final BuiltInTypeSymbol _boolean =
	        new BuiltInTypeSymbol("boolean", tBOOLEAN);
	    public static final BuiltInTypeSymbol _int =
	        new BuiltInTypeSymbol("int", tINT);
	    public static final BuiltInTypeSymbol _real =
	        new BuiltInTypeSymbol("real", tREAL);
	    public static final BuiltInTypeSymbol _TIME =
	        new BuiltInTypeSymbol("TIME", tTIME);

    public Map<String, Symbol> symbols = new HashMap<String, Symbol>();
    public static final Type[] indexToType = {
        null, _boolean, _real, _int, _TIME
    };

    public SymbolTable() { initTypeSystem(); }
    protected void initTypeSystem() {
        for (Type t : indexToType) {
            if ( t!=null ) define((BuiltInTypeSymbol)t);
        }
    }

    // Satisfy Scope interface
    public String getScopeName() { return "global"; }
    public Scope getEnclosingScope() { return null; }
    public void define(Symbol sym) {symbols.put(sym.name, sym); }
    public Symbol resolve(String name) { return symbols.get(name); }

    public String toString() { return getScopeName()+":"+symbols; }
    
//    public Type compare (CommonTree lhs,CommonTree rhs, String Operator)
//    {
//    	int tlhs = lhs.;
//    	String op = Operator;
//    	Type type = (Type) "true";
//    	return type;
//    }
}
