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


import org.destecs.script.ast.expressions.PTimeunitBase;
import org.destecs.script.ast.analysis.intf.IAnalysis;
import java.util.Map;
import org.destecs.script.ast.analysis.intf.IQuestion;
import org.destecs.script.ast.expressions.ETimeunit;
import org.destecs.script.ast.node.INode;
import java.lang.String;
import org.destecs.script.ast.expressions.ASTimeunit;
import org.destecs.script.ast.analysis.intf.IAnswer;
import org.destecs.script.ast.analysis.intf.IQuestionAnswer;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class ASTimeunit extends PTimeunitBase
{
	private static final long serialVersionUID = 1L;


	/**
	 * Creates a new {@link ASTimeunit} node with no children.
	 */
	public ASTimeunit()
	{

	}






	/**
	 * Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o) {
	if (o != null && o instanceof ASTimeunit)
	 return toString().equals(o.toString());
	return false; }
	
	/**
	 * Creates a deep clone of this {@link ASTimeunit} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link ASTimeunit} node
	 */
	public ASTimeunit clone(Map<INode,INode> oldToNewMap)
	{
		ASTimeunit node = new ASTimeunit(
		);
		oldToNewMap.put(this, node);
		return node;
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link ASTimeunit} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link ASTimeunit} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link ASTimeunit} node
	 */
	public void removeChild(INode child)
	{
		throw new RuntimeException("Not a child.");
	}


	/**
	 * Returns the {@link ETimeunit} corresponding to the
	 * type of this {@link ETimeunit} node.
	 * @return the {@link ETimeunit} for this node
	 */
	@Override
	public ETimeunit kindPTimeunit()
	{
		return ETimeunit.S;
	}


	/**
	 * Returns a deep clone of this {@link ASTimeunit} node.
	 * @return a deep clone of this {@link ASTimeunit} node
	 */
	public ASTimeunit clone()
	{
		return new ASTimeunit(
		);
	}



	public String toString()
	{
		return super.toString();
	}


	/**
	* Calls the {@link IAnalysis#caseASTimeunit(ASTimeunit)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link ASTimeunit} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis)
	{
		analysis.caseASTimeunit(this);
	}


	/**
	* Calls the {@link IAnswer#caseASTimeunit(ASTimeunit)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link ASTimeunit} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller)
	{
		return caller.caseASTimeunit(this);
	}


	/**
	* Calls the {@link IQuestion#caseASTimeunit(ASTimeunit, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link ASTimeunit} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
	{
		caller.caseASTimeunit(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseASTimeunit(ASTimeunit, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link ASTimeunit} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
	{
		return caller.caseASTimeunit(this, question);
	}



}
