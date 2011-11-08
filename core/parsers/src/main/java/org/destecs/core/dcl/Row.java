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

import org.antlr.runtime.Token;

import java.util.*;

public class Row {
    LinkedHashMap<String,Object> values = new LinkedHashMap<String, Object>();

    public Row(List<String> columns) {
        for (String c : columns) values.put(c, Table.nil);
    }

    public List<Object> getColumns() {
        List<Object> row = new ArrayList<Object>();
        for (Object o : values.values()) row.add(o);
        return row;
    }

    public List<Object> getColumns(List<Token> columns) {
        List<Object> row = new ArrayList<Object>();
        for (Token t : columns) row.add(values.get(t.getText()));
        return row;
    }

    public void set(String col, Object value) { values.put(col, value); }

    public String toString() {
        return values.toString();
    }
}