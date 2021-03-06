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

package org.destecs.script.ast.node.tokens;


import org.destecs.script.ast.node.tokens.TInt;
import org.destecs.script.ast.analysis.intf.IAnalysis;
import java.util.Map;
import java.lang.Boolean;
import org.destecs.script.ast.analysis.intf.IQuestion;
import org.destecs.script.ast.node.INode;
import java.lang.String;
import org.destecs.script.ast.analysis.intf.IAnswer;
import org.destecs.script.ast.node.Token;
import org.destecs.script.ast.analysis.intf.IQuestionAnswer;
import java.util.HashMap;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public final class TInt extends Token
{
	private static final long serialVersionUID = 1L;

	private String _text;


	/**
	* Creates a new {@code TInt} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param text_ the {@link String} node for the {@code text} child of this {@link TInt} node
	*/
	public TInt(String text_)
	{
		super();
		this.setText(text_);

	}



	public TInt()
	{
		_text = "int";
	}


	/**
	 * Creates a deep clone of this {@link TInt} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link TInt} node
	 */
	public TInt clone(Map<INode,INode> oldToNewMap)
	{
		TInt token = new TInt( getText());
		oldToNewMap.put(this, token);
		return token;
	}



	public String toString()
	{
		return (_text!=null?_text.toString():this.getClass().getSimpleName());
	}


	/**
	 * Returns a deep clone of this {@link TInt} node.
	 * @return a deep clone of this {@link TInt} node
	 */
	public TInt clone()
	{
		return new TInt( getText());
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
		fields.put("_text",this._text);
		return fields;
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof TInt)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Sets the {@code _text} child of this {@link TInt} node.
	 * @param value the new {@code _text} child of this {@link TInt} node
	*/
	public void setText(String value)
	{
		this._text = value;
	}


	/**
	 * @return the {@link String} node which is the {@code _text} child of this {@link TInt} node
	*/
	public String getText()
	{
		return this._text;
	}


	/**
	* Calls the {@link IAnalysis#caseTInt(TInt)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link TInt} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis) throws Throwable
	{
		analysis.caseTInt(this);
	}


	/**
	* Calls the {@link IAnswer#caseTInt(TInt)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link TInt} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller) throws Throwable
	{
		return caller.caseTInt(this);
	}


	/**
	* Calls the {@link IQuestion#caseTInt(TInt, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link TInt} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) throws Throwable
	{
		caller.caseTInt(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseTInt(TInt, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link TInt} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) throws Throwable
	{
		return caller.caseTInt(this, question);
	}



}
