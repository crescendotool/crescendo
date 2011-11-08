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

import java.util.*;

public class Table {
    public static final Object nil = new Object() {
        public String toString() { return ""; }
    };
    
    String name;
    LinkedHashMap<Object,Row> rows = new LinkedHashMap<Object,Row>();
    List<String> columns = new ArrayList<String>();

    public Table(String name, String primaryKey) {
        this.name = name;
        addColumn(primaryKey);
    }

    public void addColumn(String name) {
        columns.add(name);
    }

    public void add(Row r) {
        String primaryKey = getPrimaryKey();
        Object primaryKeyValue = r.values.get(primaryKey);
        rows.put(primaryKeyValue, r);
    }

    public String getPrimaryKey() {
        return columns.get(0);
    }

    @Override
    public String toString() {
        return "Table{" +
               "name='" + name + '\'' +
               ", rows=" + rows +
               ", columns=" + columns +
               '}';
    }
}
