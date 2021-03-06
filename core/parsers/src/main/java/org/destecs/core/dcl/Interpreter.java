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
package org.destecs.core.dcl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.Token;

public class Interpreter {
    public InterpreterListener listener = // default response to messages
        new InterpreterListener() {
            public void info(String msg) { System.out.println(msg); }
            public void error(String msg) { System.err.println(msg); }
            public void error(String msg, Exception e) {
                error(msg); e.printStackTrace(System.err);
            }
            public void error(String msg, Token t) {
                error("line "+t.getLine()+": "+msg);
            }
        };

    Map<String, Object> globals = new HashMap<String, Object>();
    Map<String, Table> tables = new HashMap<String, Table>();

//    public void interp(InputStream input) throws RecognitionException, IOException {
//        QLexer lex = new QLexer(new ANTLRInputStream(input));
//        CommonTokenStream tokens = new CommonTokenStream(lex);
//        QParser parser = new QParser(tokens, this);
//        parser.program();
//        // System.out.println(tables);
//    }

    public void createTable(String name,
                            String primaryKey,
                            List<Token> columns)
    {
        Table table = new Table(name, primaryKey);
        for (Token t : columns) table.addColumn(t.getText());
        tables.put(name, table);
    }

    public void insertInto(String name, Row row) {
        Table t = tables.get(name);
        if ( t==null ) { listener.error("No such table "+name); return; }
        t.add(row);
    }

    public Object select(String name, List<Token> columns) {
        Table table = tables.get(name);
        ResultSet rs = new ResultSet();
        for (Row r : table.rows.values()) rs.add( r.getColumns(columns) );
        return rs;
    }

    public Object select(String name, List<Token> columns, String key, Object value) {
        Table table = tables.get(name);
        ResultSet rs = new ResultSet();
        if ( key.equals(table.getPrimaryKey()) ) {
            List<Object> selectedColumnData =
                table.rows.get(value).getColumns(columns);
            if ( selectedColumnData.size()==1 ) return selectedColumnData.get(0);
            rs.add( selectedColumnData );
            return rs;
        }
        // key isn't the primary key; walk linearly to find all rows satisfying
        for (Row r : table.rows.values()) {
            if ( r.values.get(key).equals(value) ) {
                rs.add( r.getColumns(columns) );
            }
        }
        return rs;
    }

    public void store(String name, Object o) { globals.put(name, o); }

    public Object load(String name) { return globals.get(name); }

    public void print(Object o) {
        if ( o instanceof ResultSet ) { // result set?
            ResultSet rs = (ResultSet)o;
            for (List<Object> r : rs.results) {
                for (int i = 0; i<r.size(); i++) {
                    if ( i>0 ) System.out.print(", ");
                    System.out.print(r.get(i));
                }
                System.out.println();
            }
        }
        else {
            System.out.println(o.toString());
        }
    }
}
