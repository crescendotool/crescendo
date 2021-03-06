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

package org.destecs.script.ast.types;


import org.destecs.script.ast.analysis.intf.IAnalysis;
import java.util.Map;
import java.lang.Boolean;
import org.destecs.script.ast.types.PTypeBase;
import org.destecs.script.ast.types.ABoolType;
import org.destecs.script.ast.analysis.intf.IQuestion;
import org.destecs.script.ast.types.EType;
import org.destecs.script.ast.node.INode;
import java.lang.String;
import org.destecs.script.ast.analysis.intf.IAnswer;
import org.destecs.script.ast.analysis.intf.IQuestionAnswer;
import java.util.HashMap;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class ABoolType extends PTypeBase
{
	private static final long serialVersionUID = 1L;


	/**
	 * Creates a new {@link ABoolType} node with no children.
	 */
	public ABoolType()
	{

	}




	/**
	 * Creates a deep clone of this {@link ABoolType} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link ABoolType} node
	 */
	public ABoolType clone(Map<INode,INode> oldToNewMap)
	{
		ABoolType node = new ABoolType(
		);
		oldToNewMap.put(this, node);
		return node;
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
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof ABoolType)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link ABoolType} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link ABoolType} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link ABoolType} node
	 */
	public void removeChild(INode child)
	{
		throw new RuntimeException("Not a child.");
	}



	public String toString()
	{
		return super.toString();
	}


	/**
	 * Returns a deep clone of this {@link ABoolType} node.
	 * @return a deep clone of this {@link ABoolType} node
	 */
	public ABoolType clone()
	{
		return new ABoolType(
		);
	}


	/**
	 * Returns the {@link EType} corresponding to the
	 * type of this {@link EType} node.
	 * @return the {@link EType} for this node
	 */
	@Override
	public EType kindPType()
	{
		return EType.BOOL;
	}


	/**
	* Calls the {@link IAnalysis#caseABoolType(ABoolType)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link ABoolType} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis) throws Throwable
	{
		analysis.caseABoolType(this);
	}


	/**
	* Calls the {@link IAnswer#caseABoolType(ABoolType)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link ABoolType} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller) throws Throwable
	{
		return caller.caseABoolType(this);
	}


	/**
	* Calls the {@link IQuestion#caseABoolType(ABoolType, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link ABoolType} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) throws Throwable
	{
		caller.caseABoolType(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseABoolType(ABoolType, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link ABoolType} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) throws Throwable
	{
		return caller.caseABoolType(this, question);
	}



}
