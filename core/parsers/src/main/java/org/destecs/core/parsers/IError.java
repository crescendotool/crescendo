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
package org.destecs.core.parsers;

import java.io.File;

public interface IError
{
	/**
	 * get the file of which the error occurred
	 * @return the file where the error occurred
	 */
	File getFile();

	/**
	 * Get the line of which the error was discovered
	 * @return the line of the error
	 */
	int getLine();

	/**
	 * Get the char position in the line where the error was discovered
	 * @return char pos in line
	 */
	int getCharPositionInLine();
	
	/**
	 * Get a description of the error encountered by the parser
	 * @return the message
	 */
	String getMessage();
}
