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


import java.util.Map;
import org.destecs.script.ast.analysis.intf.IAnalysis;
import java.lang.Boolean;
import org.destecs.script.ast.expressions.AUnaryExp;
import java.lang.String;
import org.destecs.script.ast.analysis.intf.IAnswer;
import java.util.HashMap;
import org.destecs.script.ast.expressions.PExp;
import org.destecs.script.ast.expressions.unop.PUnop;
import org.destecs.script.ast.analysis.intf.IQuestion;
import org.destecs.script.ast.node.INode;
import org.destecs.script.ast.expressions.PExpBase;
import org.destecs.script.ast.expressions.EExp;
import org.destecs.script.ast.analysis.intf.IQuestionAnswer;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class AUnaryExp extends PExpBase
{
	private static final long serialVersionUID = 1L;

	private PUnop _operator;
	private PExp _exp;


	/**
	 * Creates a new {@link AUnaryExp} node with no children.
	 */
	public AUnaryExp()
	{

	}


	/**
	* Creates a new {@code AUnaryExp} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param operator_ the {@link PUnop} node for the {@code operator} child of this {@link AUnaryExp} node
	* @param exp_ the {@link PExp} node for the {@code exp} child of this {@link AUnaryExp} node
	*/
	public AUnaryExp(PUnop operator_, PExp exp_)
	{
		super();
		this.setOperator(operator_);
		this.setExp(exp_);

	}


	/**
	 * Returns the {@link EExp} corresponding to the
	 * type of this {@link EExp} node.
	 * @return the {@link EExp} for this node
	 */
	@Override
	public EExp kindPExp()
	{
		return EExp.UNARY;
	}


	/**
	 * Returns a deep clone of this {@link AUnaryExp} node.
	 * @return a deep clone of this {@link AUnaryExp} node
	 */
	public AUnaryExp clone()
	{
		return new AUnaryExp(
			cloneNode(_operator),
			cloneNode(_exp)
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
		fields.put("_operator",this._operator);
		fields.put("_exp",this._exp);
		return fields;
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link AUnaryExp} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link AUnaryExp} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link AUnaryExp} node
	 */
	public void removeChild(INode child)
	{
		if (this._operator == child) {
			this._operator = null;
			return;
		}

		if (this._exp == child) {
			this._exp = null;
			return;
		}

		throw new RuntimeException("Not a child.");
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof AUnaryExp)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Creates a deep clone of this {@link AUnaryExp} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link AUnaryExp} node
	 */
	public AUnaryExp clone(Map<INode,INode> oldToNewMap)
	{
		AUnaryExp node = new AUnaryExp(
			cloneNode(_operator, oldToNewMap),
			cloneNode(_exp, oldToNewMap)
		);
		oldToNewMap.put(this, node);
		return node;
	}



	public String toString()
	{
		return (_operator!=null?_operator.toString():this.getClass().getSimpleName())+ (_exp!=null?_exp.toString():this.getClass().getSimpleName());
	}


	/**
	 * Sets the {@code _operator} child of this {@link AUnaryExp} node.
	 * @param value the new {@code _operator} child of this {@link AUnaryExp} node
	*/
	public void setOperator(PUnop value)
	{
		if (this._operator != null) {
			this._operator.parent(null);
		}
		if (value != null) {
			if (value.parent() != null) {
				value.parent().removeChild(value);
		}
			value.parent(this);
		}
		this._operator = value;

	}


	/**
	 * @return the {@link PUnop} node which is the {@code _operator} child of this {@link AUnaryExp} node
	*/
	public PUnop getOperator()
	{
		return this._operator;
	}


	/**
	 * Sets the {@code _exp} child of this {@link AUnaryExp} node.
	 * @param value the new {@code _exp} child of this {@link AUnaryExp} node
	*/
	public void setExp(PExp value)
	{
		if (this._exp != null) {
			this._exp.parent(null);
		}
		if (value != null) {
			if (value.parent() != null) {
				value.parent().removeChild(value);
		}
			value.parent(this);
		}
		this._exp = value;

	}


	/**
	 * @return the {@link PExp} node which is the {@code _exp} child of this {@link AUnaryExp} node
	*/
	public PExp getExp()
	{
		return this._exp;
	}


	/**
	* Calls the {@link IAnalysis#caseAUnaryExp(AUnaryExp)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link AUnaryExp} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis) throws Throwable
	{
		analysis.caseAUnaryExp(this);
	}


	/**
	* Calls the {@link IAnswer#caseAUnaryExp(AUnaryExp)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link AUnaryExp} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller) throws Throwable
	{
		return caller.caseAUnaryExp(this);
	}


	/**
	* Calls the {@link IQuestion#caseAUnaryExp(AUnaryExp, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link AUnaryExp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) throws Throwable
	{
		caller.caseAUnaryExp(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseAUnaryExp(AUnaryExp, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link AUnaryExp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) throws Throwable
	{
		return caller.caseAUnaryExp(this, question);
	}



}
