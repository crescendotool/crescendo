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

package org.destecs.script.ast.expressions.binop;


import org.destecs.script.ast.analysis.intf.IAnalysis;
import java.util.Map;
import java.lang.Boolean;
import org.destecs.script.ast.expressions.binop.AAndBinop;
import org.destecs.script.ast.analysis.intf.IQuestion;
import org.destecs.script.ast.node.INode;
import java.lang.String;
import org.destecs.script.ast.analysis.intf.IAnswer;
import org.destecs.script.ast.expressions.binop.EBinop;
import org.destecs.script.ast.analysis.intf.IQuestionAnswer;
import java.util.HashMap;
import org.destecs.script.ast.expressions.binop.PBinopBase;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class AAndBinop extends PBinopBase
{
	private static final long serialVersionUID = 1L;


	/**
	 * Creates a new {@link AAndBinop} node with no children.
	 */
	public AAndBinop()
	{

	}




	/**
	 * Creates a deep clone of this {@link AAndBinop} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link AAndBinop} node
	 */
	public AAndBinop clone(Map<INode,INode> oldToNewMap)
	{
		AAndBinop node = new AAndBinop(
		);
		oldToNewMap.put(this, node);
		return node;
	}



	public String toString()
	{
		return super.toString();
	}


	/**
	 * Returns the {@link EBinop} corresponding to the
	 * type of this {@link EBinop} node.
	 * @return the {@link EBinop} for this node
	 */
	@Override
	public EBinop kindPBinop()
	{
		return EBinop.AND;
	}


	/**
	 * Returns a deep clone of this {@link AAndBinop} node.
	 * @return a deep clone of this {@link AAndBinop} node
	 */
	public AAndBinop clone()
	{
		return new AAndBinop(
		);
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof AAndBinop)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link AAndBinop} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link AAndBinop} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link AAndBinop} node
	 */
	public void removeChild(INode child)
	{
		throw new RuntimeException("Not a child.");
	}


	/**
	 * Creates a map of all field names and their value
	 * @param includeInheritedFields if true all inherited fields are included
	 * @return a a map of names to values of all fields
	 */
	@Override
	public Map<String,Object> getChildren(Boolean includeInheritedFields)
	{
		Map<String,Object> fields = new HashMap<String,Object>();
		if(includeInheritedFields)
		{
			fields.putAll(super.getChildren(includeInheritedFields));
		}
		return fields;
	}


	/**
	* Calls the {@link IAnalysis#caseAAndBinop(AAndBinop)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link AAndBinop} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis) throws Throwable
	{
		analysis.caseAAndBinop(this);
	}


	/**
	* Calls the {@link IAnswer#caseAAndBinop(AAndBinop)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link AAndBinop} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller) throws Throwable
	{
		return caller.caseAAndBinop(this);
	}


	/**
	* Calls the {@link IQuestion#caseAAndBinop(AAndBinop, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link AAndBinop} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) throws Throwable
	{
		caller.caseAAndBinop(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseAAndBinop(AAndBinop, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link AAndBinop} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) throws Throwable
	{
		return caller.caseAAndBinop(this, question);
	}



}
