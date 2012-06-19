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
import org.destecs.script.ast.expressions.ABinaryExp;
import java.lang.Boolean;
import java.lang.String;
import org.destecs.script.ast.analysis.intf.IAnswer;
import java.util.HashMap;
import org.destecs.script.ast.expressions.PExp;
import org.destecs.script.ast.analysis.intf.IQuestion;
import org.destecs.script.ast.node.INode;
import org.destecs.script.ast.expressions.PExpBase;
import org.destecs.script.ast.expressions.EExp;
import org.destecs.script.ast.expressions.binop.PBinop;
import org.destecs.script.ast.analysis.intf.IQuestionAnswer;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class ABinaryExp extends PExpBase
{
	private static final long serialVersionUID = 1L;

	private PExp _left;
	private PBinop _operator;
	private PExp _right;


	/**
	 * Creates a new {@link ABinaryExp} node with no children.
	 */
	public ABinaryExp()
	{

	}


	/**
	* Creates a new {@code ABinaryExp} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param left_ the {@link PExp} node for the {@code left} child of this {@link ABinaryExp} node
	* @param operator_ the {@link PBinop} node for the {@code operator} child of this {@link ABinaryExp} node
	* @param right_ the {@link PExp} node for the {@code right} child of this {@link ABinaryExp} node
	*/
	public ABinaryExp(PExp left_, PBinop operator_, PExp right_)
	{
		super();
		this.setLeft(left_);
		this.setOperator(operator_);
		this.setRight(right_);

	}


	/**
	 * Returns the {@link EExp} corresponding to the
	 * type of this {@link EExp} node.
	 * @return the {@link EExp} for this node
	 */
	@Override
	public EExp kindPExp()
	{
		return EExp.BINARY;
	}


	/**
	 * Returns a deep clone of this {@link ABinaryExp} node.
	 * @return a deep clone of this {@link ABinaryExp} node
	 */
	public ABinaryExp clone()
	{
		return new ABinaryExp(
			cloneNode(_left),
			cloneNode(_operator),
			cloneNode(_right)
		);
	}



	public String toString()
	{
		return (_left!=null?_left.toString():this.getClass().getSimpleName())+ (_operator!=null?_operator.toString():this.getClass().getSimpleName())+ (_right!=null?_right.toString():this.getClass().getSimpleName());
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof ABinaryExp)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Creates a deep clone of this {@link ABinaryExp} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link ABinaryExp} node
	 */
	public ABinaryExp clone(Map<INode,INode> oldToNewMap)
	{
		ABinaryExp node = new ABinaryExp(
			cloneNode(_left, oldToNewMap),
			cloneNode(_operator, oldToNewMap),
			cloneNode(_right, oldToNewMap)
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
		fields.put("_left",this._left);
		fields.put("_operator",this._operator);
		fields.put("_right",this._right);
		return fields;
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link ABinaryExp} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link ABinaryExp} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link ABinaryExp} node
	 */
	public void removeChild(INode child)
	{
		if (this._left == child) {
			this._left = null;
			return;
		}

		if (this._operator == child) {
			this._operator = null;
			return;
		}

		if (this._right == child) {
			this._right = null;
			return;
		}

		throw new RuntimeException("Not a child.");
	}


	/**
	 * Sets the {@code _left} child of this {@link ABinaryExp} node.
	 * @param value the new {@code _left} child of this {@link ABinaryExp} node
	*/
	public void setLeft(PExp value)
	{
		if (this._left != null) {
			this._left.parent(null);
		}
		if (value != null) {
			if (value.parent() != null) {
				value.parent().removeChild(value);
		}
			value.parent(this);
		}
		this._left = value;

	}


	/**
	 * @return the {@link PExp} node which is the {@code _left} child of this {@link ABinaryExp} node
	*/
	public PExp getLeft()
	{
		return this._left;
	}


	/**
	 * Sets the {@code _operator} child of this {@link ABinaryExp} node.
	 * @param value the new {@code _operator} child of this {@link ABinaryExp} node
	*/
	public void setOperator(PBinop value)
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
	 * @return the {@link PBinop} node which is the {@code _operator} child of this {@link ABinaryExp} node
	*/
	public PBinop getOperator()
	{
		return this._operator;
	}


	/**
	 * Sets the {@code _right} child of this {@link ABinaryExp} node.
	 * @param value the new {@code _right} child of this {@link ABinaryExp} node
	*/
	public void setRight(PExp value)
	{
		if (this._right != null) {
			this._right.parent(null);
		}
		if (value != null) {
			if (value.parent() != null) {
				value.parent().removeChild(value);
		}
			value.parent(this);
		}
		this._right = value;

	}


	/**
	 * @return the {@link PExp} node which is the {@code _right} child of this {@link ABinaryExp} node
	*/
	public PExp getRight()
	{
		return this._right;
	}


	/**
	* Calls the {@link IAnalysis#caseABinaryExp(ABinaryExp)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link ABinaryExp} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis) throws Throwable
	{
		analysis.caseABinaryExp(this);
	}


	/**
	* Calls the {@link IAnswer#caseABinaryExp(ABinaryExp)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link ABinaryExp} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller) throws Throwable
	{
		return caller.caseABinaryExp(this);
	}


	/**
	* Calls the {@link IQuestion#caseABinaryExp(ABinaryExp, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link ABinaryExp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) throws Throwable
	{
		caller.caseABinaryExp(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseABinaryExp(ABinaryExp, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link ABinaryExp} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) throws Throwable
	{
		return caller.caseABinaryExp(this, question);
	}



}
