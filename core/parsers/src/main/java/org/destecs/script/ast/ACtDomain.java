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


import org.destecs.script.ast.ACtDomain;
import org.destecs.script.ast.analysis.intf.IAnalysis;
import java.util.Map;
import org.destecs.script.ast.PDomainBase;
import java.lang.Boolean;
import org.destecs.script.ast.analysis.intf.IQuestion;
import org.destecs.script.ast.node.INode;
import java.lang.String;
import org.destecs.script.ast.analysis.intf.IAnswer;
import org.destecs.script.ast.EDomain;
import org.destecs.script.ast.analysis.intf.IQuestionAnswer;
import java.util.HashMap;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class ACtDomain extends PDomainBase
{
	private static final long serialVersionUID = 1L;


	/**
	 * Creates a new {@link ACtDomain} node with no children.
	 */
	public ACtDomain()
	{

	}




	/**
	 * Returns the {@link EDomain} corresponding to the
	 * type of this {@link EDomain} node.
	 * @return the {@link EDomain} for this node
	 */
	@Override
	public EDomain kindPDomain()
	{
		return EDomain.CT;
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link ACtDomain} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link ACtDomain} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link ACtDomain} node
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



	public String toString()
	{
		return super.toString();
	}


	/**
	 * Creates a deep clone of this {@link ACtDomain} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link ACtDomain} node
	 */
	public ACtDomain clone(Map<INode,INode> oldToNewMap)
	{
		ACtDomain node = new ACtDomain(
		);
		oldToNewMap.put(this, node);
		return node;
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof ACtDomain)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Returns a deep clone of this {@link ACtDomain} node.
	 * @return a deep clone of this {@link ACtDomain} node
	 */
	public ACtDomain clone()
	{
		return new ACtDomain(
		);
	}


	/**
	* Calls the {@link IAnalysis#caseACtDomain(ACtDomain)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link ACtDomain} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis) throws Throwable
	{
		analysis.caseACtDomain(this);
	}


	/**
	* Calls the {@link IAnswer#caseACtDomain(ACtDomain)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link ACtDomain} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller) throws Throwable
	{
		return caller.caseACtDomain(this);
	}


	/**
	* Calls the {@link IQuestion#caseACtDomain(ACtDomain, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link ACtDomain} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) throws Throwable
	{
		caller.caseACtDomain(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseACtDomain(ACtDomain, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link ACtDomain} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) throws Throwable
	{
		return caller.caseACtDomain(this, question);
	}



}
