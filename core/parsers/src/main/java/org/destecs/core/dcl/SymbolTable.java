package org.destecs.core.dcl;
/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
import java.util.HashMap;
import java.util.Map;
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
