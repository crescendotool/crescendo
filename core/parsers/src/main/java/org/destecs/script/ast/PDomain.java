/*******************************************************************************
* Copyright (c) 2009, 2011 Overture Team and others.
*
* Overture is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Overture is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Overture.  If not, see <http://www.gnu.org/licenses/>.
*
* The Overture Tool web-site: http://overturetool.org/
*******************************************************************************/

/* This file was generated by ASTcreator (http://www.lausdahl.com/). */

package org.destecs.script.ast;


import java.util.Map;
import org.destecs.script.ast.PDomain;
import org.destecs.script.ast.node.INode;
import java.lang.String;
import org.destecs.script.ast.node.NodeEnum;
import org.destecs.script.ast.EDomain;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public interface PDomain extends INode
{	/**
	 * Returns a deep clone of this {@link PDomainBase} node.
	 * @return a deep clone of this {@link PDomainBase} node
	 */
	public abstract PDomain clone();
	/**
	 * Returns the {@link NodeEnum} corresponding to the
	 * type of this {@link INode} node.
	 * @return the {@link NodeEnum} for this node
	 */
	public NodeEnum kindNode();
	/**
	 * Creates a deep clone of this {@link PDomainBase} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link PDomainBase} node
	 */
	public abstract PDomain clone(Map<INode,INode> oldToNewMap);

	public String toString();
	/**
	 * Returns the {@link EDomain} corresponding to the
	 * type of this {@link EDomain} node.
	 * @return the {@link EDomain} for this node
	 */
	public abstract EDomain kindPDomain();
	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link PDomainBase} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link PDomainBase} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link PDomainBase} node
	 */
	public void removeChild(INode child);

}