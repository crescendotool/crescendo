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

package org.destecs.script.ast.statement;


import java.util.Map;
import org.destecs.script.ast.analysis.intf.IAnalysis;
import java.lang.Boolean;
import org.destecs.script.ast.statement.PStmBase;
import java.lang.String;
import org.destecs.script.ast.statement.EStm;
import org.destecs.script.ast.analysis.intf.IAnswer;
import java.util.HashMap;
import org.destecs.script.ast.statement.ARevertStm;
import org.destecs.script.ast.analysis.intf.IQuestion;
import org.destecs.script.ast.node.INode;
import org.destecs.script.ast.expressions.AIdentifierSingleExp;
import org.destecs.script.ast.analysis.intf.IQuestionAnswer;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class ARevertStm extends PStmBase
{
	private static final long serialVersionUID = 1L;

	private AIdentifierSingleExp _identifier;


	/**
	 * Creates a new {@link ARevertStm} node with no children.
	 */
	public ARevertStm()
	{

	}


	/**
	* Creates a new {@code ARevertStm} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param identifier_ the {@link AIdentifierSingleExp} node for the {@code identifier} child of this {@link ARevertStm} node
	*/
	public ARevertStm(AIdentifierSingleExp identifier_)
	{
		super();
		this.setIdentifier(identifier_);

	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof ARevertStm)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Creates a deep clone of this {@link ARevertStm} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link ARevertStm} node
	 */
	public ARevertStm clone(Map<INode,INode> oldToNewMap)
	{
		ARevertStm node = new ARevertStm(
			cloneNode(_identifier, oldToNewMap)
		);
		oldToNewMap.put(this, node);
		return node;
	}


	/**
	 * Returns a deep clone of this {@link ARevertStm} node.
	 * @return a deep clone of this {@link ARevertStm} node
	 */
	public ARevertStm clone()
	{
		return new ARevertStm(
			cloneNode(_identifier)
		);
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
		fields.put("_identifier",this._identifier);
		return fields;
	}



	public String toString()
	{
		return (_identifier!=null?_identifier.toString():this.getClass().getSimpleName());
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link ARevertStm} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link ARevertStm} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link ARevertStm} node
	 */
	public void removeChild(INode child)
	{
		if (this._identifier == child) {
			this._identifier = null;
			return;
		}

		throw new RuntimeException("Not a child.");
	}


	/**
	 * Returns the {@link EStm} corresponding to the
	 * type of this {@link EStm} node.
	 * @return the {@link EStm} for this node
	 */
	@Override
	public EStm kindPStm()
	{
		return EStm.REVERT;
	}


	/**
	 * Sets the {@code _identifier} child of this {@link ARevertStm} node.
	 * @param value the new {@code _identifier} child of this {@link ARevertStm} node
	*/
	public void setIdentifier(AIdentifierSingleExp value)
	{
		if (this._identifier != null) {
			this._identifier.parent(null);
		}
		if (value != null) {
			if (value.parent() != null) {
				value.parent().removeChild(value);
		}
			value.parent(this);
		}
		this._identifier = value;

	}


	/**
	 * @return the {@link AIdentifierSingleExp} node which is the {@code _identifier} child of this {@link ARevertStm} node
	*/
	public AIdentifierSingleExp getIdentifier()
	{
		return this._identifier;
	}


	/**
	* Calls the {@link IAnalysis#caseARevertStm(ARevertStm)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link ARevertStm} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis) throws Throwable
	{
		analysis.caseARevertStm(this);
	}


	/**
	* Calls the {@link IAnswer#caseARevertStm(ARevertStm)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link ARevertStm} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller) throws Throwable
	{
		return caller.caseARevertStm(this);
	}


	/**
	* Calls the {@link IQuestion#caseARevertStm(ARevertStm, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link ARevertStm} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) throws Throwable
	{
		caller.caseARevertStm(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseARevertStm(ARevertStm, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link ARevertStm} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) throws Throwable
	{
		return caller.caseARevertStm(this, question);
	}



}
