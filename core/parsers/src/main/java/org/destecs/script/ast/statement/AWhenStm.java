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


import java.util.LinkedList;
import org.destecs.script.ast.analysis.intf.IAnalysis;
import java.util.Map;
import java.lang.Boolean;
import java.util.List;
import org.destecs.script.ast.statement.PStmBase;
import org.destecs.script.ast.expressions.ATimeSingleExp;
import org.destecs.script.ast.statement.EStm;
import java.lang.String;
import org.destecs.script.ast.node.NodeList;
import org.destecs.script.ast.analysis.intf.IAnswer;
import org.destecs.script.ast.statement.PStm;
import java.util.HashMap;
import org.destecs.script.ast.expressions.PExp;
import org.destecs.script.ast.statement.AWhenStm;
import org.destecs.script.ast.statement.ARevertStm;
import org.destecs.script.ast.analysis.intf.IQuestion;
import org.destecs.script.ast.node.INode;
import org.destecs.script.ast.analysis.intf.IQuestionAnswer;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public class AWhenStm extends PStmBase
{
	private static final long serialVersionUID = 1L;

	private PExp _test;
	private NodeList<PStm> _then = new NodeList<PStm>(this);
	private NodeList<ARevertStm> _after = new NodeList<ARevertStm>(this);
	private ATimeSingleExp _for;

	/**
	* Creates a new {@code AWhenStm} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param test_ the {@link PExp} node for the {@code test} child of this {@link AWhenStm} node
	* @param then_ the {@link NodeList} node for the {@code then} child of this {@link AWhenStm} node
	* @param after_ the {@link NodeList} node for the {@code after} child of this {@link AWhenStm} node
	* @param for_ the {@link ATimeSingleExp} node for the {@code for} child of this {@link AWhenStm} node
	*/
	public AWhenStm(PExp test_, List<? extends PStm> then_, List<? extends ARevertStm> after_, ATimeSingleExp for_)
	{
		super();
		this.setTest(test_);
		this.setThen(then_);
		this.setAfter(after_);
		this.setFor(for_);

	}



	/**
	 * Creates a new {@link AWhenStm} node with no children.
	 */
	public AWhenStm()
	{

	}


	/**
	 * Creates a deep clone of this {@link AWhenStm} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link AWhenStm} node
	 */
	public AWhenStm clone(Map<INode,INode> oldToNewMap)
	{
		AWhenStm node = new AWhenStm(
			cloneNode(_test, oldToNewMap),
			cloneList(_then, oldToNewMap),
			cloneList(_after, oldToNewMap),
			cloneNode(_for, oldToNewMap)
		);
		oldToNewMap.put(this, node);
		return node;
	}


	/**
	 * Returns the {@link EStm} corresponding to the
	 * type of this {@link EStm} node.
	 * @return the {@link EStm} for this node
	 */
	@Override
	public EStm kindPStm()
	{
		return EStm.WHEN;
	}


	/**
	 * Removes the {@link INode} {@code child} as a child of this {@link AWhenStm} node.
	 * Do not call this method with any graph fields of this node. This will cause any child's
	 * with the same reference to be removed unintentionally or {@link RuntimeException}will be thrown.
	 * @param child the child node to be removed from this {@link AWhenStm} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link AWhenStm} node
	 */
	public void removeChild(INode child)
	{
		if (this._test == child) {
			this._test = null;
			return;
		}

		if (this._then.remove(child)) {
				return;
		}
		if (this._after.remove(child)) {
				return;
		}
		if (this._for == child) {
			this._for = null;
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
		if (o != null && o instanceof AWhenStm)		{
			 return toString().equals(o.toString());
		}
		return false;
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
		fields.put("_test",this._test);
		fields.put("_then",this._then);
		fields.put("_after",this._after);
		fields.put("_for",this._for);
		return fields;
	}


	/**
	 * Returns a deep clone of this {@link AWhenStm} node.
	 * @return a deep clone of this {@link AWhenStm} node
	 */
	public AWhenStm clone()
	{
		return new AWhenStm(
			cloneNode(_test),
			cloneList(_then),
			cloneList(_after),
			cloneNode(_for)
		);
	}



	public String toString()
	{
		return (_test!=null?_test.toString():this.getClass().getSimpleName())+ (_then!=null?_then.toString():this.getClass().getSimpleName())+ (_after!=null?_after.toString():this.getClass().getSimpleName())+ (_for!=null?_for.toString():this.getClass().getSimpleName());
	}


	/**
	 * Sets the {@code _test} child of this {@link AWhenStm} node.
	 * @param value the new {@code _test} child of this {@link AWhenStm} node
	*/
	public void setTest(PExp value)
	{
		if (this._test != null) {
			this._test.parent(null);
		}
		if (value != null) {
			if (value.parent() != null) {
				value.parent().removeChild(value);
		}
			value.parent(this);
		}
		this._test = value;

	}


	/**
	 * @return the {@link PExp} node which is the {@code _test} child of this {@link AWhenStm} node
	*/
	public PExp getTest()
	{
		return this._test;
	}


	/**
	 * Sets the {@code _then} child of this {@link AWhenStm} node.
	 * @param value the new {@code _then} child of this {@link AWhenStm} node
	*/
	public void setThen(List<? extends PStm> value)
	{
		if (this._then.equals(value)) {
			return;
		}
		this._then.clear();
		if (value != null) {
			this._then.addAll(value);
		}

	}


	/**
	 * @return the {@link LinkedList} node which is the {@code _then} child of this {@link AWhenStm} node
	*/
	public LinkedList<PStm> getThen()
	{
		return this._then;
	}


	/**
	 * Sets the {@code _after} child of this {@link AWhenStm} node.
	 * @param value the new {@code _after} child of this {@link AWhenStm} node
	*/
	public void setAfter(List<? extends ARevertStm> value)
	{
		if (this._after.equals(value)) {
			return;
		}
		this._after.clear();
		if (value != null) {
			this._after.addAll(value);
		}

	}


	/**
	 * @return the {@link LinkedList} node which is the {@code _after} child of this {@link AWhenStm} node
	*/
	public LinkedList<ARevertStm> getAfter()
	{
		return this._after;
	}


	/**
	 * Sets the {@code _for} child of this {@link AWhenStm} node.
	 * @param value the new {@code _for} child of this {@link AWhenStm} node
	*/
	public void setFor(ATimeSingleExp value)
	{
		if (this._for != null) {
			this._for.parent(null);
		}
		if (value != null) {
			if (value.parent() != null) {
				value.parent().removeChild(value);
		}
			value.parent(this);
		}
		this._for = value;

	}


	/**
	 * @return the {@link ATimeSingleExp} node which is the {@code _for} child of this {@link AWhenStm} node
	*/
	public ATimeSingleExp getFor()
	{
		return this._for;
	}


	/**
	* Calls the {@link IAnalysis#caseAWhenStm(AWhenStm)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link AWhenStm} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis) throws Throwable
	{
		analysis.caseAWhenStm(this);
	}


	/**
	* Calls the {@link IAnswer#caseAWhenStm(AWhenStm)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link AWhenStm} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller) throws Throwable
	{
		return caller.caseAWhenStm(this);
	}


	/**
	* Calls the {@link IQuestion#caseAWhenStm(AWhenStm, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link AWhenStm} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) throws Throwable
	{
		caller.caseAWhenStm(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseAWhenStm(AWhenStm, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link AWhenStm} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) throws Throwable
	{
		return caller.caseAWhenStm(this, question);
	}



}
