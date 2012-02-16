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

package org.destecs.script.ast.expressions;


import org.destecs.script.ast.expressions.PExp;
import org.destecs.script.ast.expressions.SSingleExp;
import java.util.Map;
import org.destecs.script.ast.expressions.ESingleExp;
import org.destecs.script.ast.node.INode;
import java.lang.String;
import org.destecs.script.ast.expressions.EExp;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public interface SSingleExp extends PExp
{	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link SSingleExpBase} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link SSingleExpBase} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link SSingleExpBase} node
	 */
	public void removeChild(INode child);
	/**
	 * Creates a deep clone of this {@link SSingleExpBase} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link SSingleExpBase} node
	 */
	public abstract SSingleExp clone(Map<INode,INode> oldToNewMap);
	/**
	 * Returns a deep clone of this {@link SSingleExpBase} node.
	 * @return a deep clone of this {@link SSingleExpBase} node
	 */
	public abstract SSingleExp clone();

	public String toString();
	/**
	 * Returns the {@link ESingleExp} corresponding to the
	 * type of this {@link ESingleExp} node.
	 * @return the {@link ESingleExp} for this node
	 */
	public abstract ESingleExp kindSSingleExp();
	/**
	 * Returns the {@link EExp} corresponding to the
	 * type of this {@link EExp} node.
	 * @return the {@link EExp} for this node
	 */
	public EExp kindPExp();

}