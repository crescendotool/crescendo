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

package org.destecs.script.ast.analysis.intf;


import org.destecs.script.ast.expressions.AMsTimeunit;
import org.destecs.script.ast.expressions.binop.AModBinop;
import java.lang.Boolean;
import org.destecs.script.ast.statement.AQuitStm;
import java.lang.Long;
import org.destecs.script.ast.preprocessing.AScriptInclude;
import org.destecs.script.ast.types.AIntType;
import org.destecs.script.ast.expressions.ASTimeunit;
import org.destecs.script.ast.expressions.ABoolSingleExp;
import java.lang.Double;
import org.destecs.script.ast.statement.AErrorMessageStm;
import org.destecs.script.ast.expressions.binop.ADivideBinop;
import org.destecs.script.ast.statement.APrintMessageStm;
import java.io.Serializable;
import org.destecs.script.ast.statement.AAssignStm;
import org.destecs.script.ast.expressions.binop.APlusBinop;
import org.destecs.script.ast.expressions.binop.ADifferentBinop;
import org.destecs.script.ast.ACtDomain;
import org.destecs.script.ast.expressions.AHTimeunit;
import org.destecs.script.ast.types.ATimeType;
import org.destecs.script.ast.analysis.intf.IAnalysis;
import org.destecs.script.ast.statement.AOnceStm;
import org.destecs.script.ast.expressions.binop.AEqualBinop;
import org.destecs.script.ast.expressions.binop.AGreaterThanBinop;
import java.lang.Integer;
import org.destecs.script.ast.expressions.binop.AMultiplyBinop;
import org.destecs.script.ast.statement.AWarnMessageStm;
import org.destecs.script.ast.expressions.AIdentifierSingleExp;
import org.destecs.script.ast.expressions.binop.AMinusBinop;
import org.destecs.script.ast.expressions.AMTimeunit;
import org.destecs.script.ast.expressions.ABinaryExp;
import org.destecs.script.ast.expressions.AUnaryExp;
import org.destecs.script.ast.expressions.AUsTimeunit;
import org.destecs.script.ast.expressions.unop.ACeilUnop;
import org.destecs.script.ast.ADeDomain;
import org.destecs.script.ast.expressions.binop.AOrBinop;
import org.destecs.script.ast.expressions.ANumericalSingleExp;
import org.destecs.script.ast.node.tokens.TInt;
import org.destecs.script.ast.expressions.unop.AMinusUnop;
import org.destecs.script.ast.expressions.binop.AGreaterEqualBinop;
import org.destecs.script.ast.expressions.binop.ADivBinop;
import org.destecs.script.ast.statement.AWhenStm;
import org.destecs.script.ast.expressions.binop.AAndBinop;
import org.destecs.script.ast.types.ARealType;
import org.destecs.script.ast.expressions.unop.AFloorUnop;
import org.destecs.script.ast.expressions.ASystemTimeSingleExp;
import org.destecs.script.ast.expressions.binop.AEquivBinop;
import java.lang.String;
import org.destecs.script.ast.expressions.ATimeSingleExp;
import org.destecs.script.ast.expressions.unop.AAbsUnop;
import org.destecs.script.ast.expressions.binop.ALessEqualBinop;
import org.destecs.script.ast.expressions.binop.ALessThanBinop;
import org.destecs.script.ast.statement.ARevertStm;
import org.destecs.script.ast.types.ABoolType;
import org.destecs.script.ast.expressions.binop.AImpliesBinop;
import org.destecs.script.ast.expressions.unop.AAddUnop;
import org.destecs.script.ast.node.IToken;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public interface IQuestionAnswer<Q, A> extends Serializable
{	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public A caseTInt(TInt node, Q question) throws Throwable;
	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public A caseDouble(Double node, Q question) throws Throwable;
	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public A caseBoolean(Boolean node, Q question) throws Throwable;
	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public A caseInteger(Integer node, Q question) throws Throwable;
	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public A caseString(String node, Q question) throws Throwable;
	/**
	* Called by the {@link IToken} node from {@link IToken#apply(IAnalysis)}.
	* @param node the calling {@link IToken} node
	*/
	public A caseLong(Long node, Q question) throws Throwable;
	/**
	* Called by the {@link AAddUnop} node from {@link AAddUnop#apply(IAnalysis)}.
	* @param node the calling {@link AAddUnop} node
	*/
	public A caseAAddUnop(AAddUnop node, Q question) throws Throwable;
	/**
	* Called by the {@link AMinusUnop} node from {@link AMinusUnop#apply(IAnalysis)}.
	* @param node the calling {@link AMinusUnop} node
	*/
	public A caseAMinusUnop(AMinusUnop node, Q question) throws Throwable;
	/**
	* Called by the {@link AAbsUnop} node from {@link AAbsUnop#apply(IAnalysis)}.
	* @param node the calling {@link AAbsUnop} node
	*/
	public A caseAAbsUnop(AAbsUnop node, Q question) throws Throwable;
	/**
	* Called by the {@link AFloorUnop} node from {@link AFloorUnop#apply(IAnalysis)}.
	* @param node the calling {@link AFloorUnop} node
	*/
	public A caseAFloorUnop(AFloorUnop node, Q question) throws Throwable;
	/**
	* Called by the {@link ACeilUnop} node from {@link ACeilUnop#apply(IAnalysis)}.
	* @param node the calling {@link ACeilUnop} node
	*/
	public A caseACeilUnop(ACeilUnop node, Q question) throws Throwable;
	/**
	* Called by the {@link APlusBinop} node from {@link APlusBinop#apply(IAnalysis)}.
	* @param node the calling {@link APlusBinop} node
	*/
	public A caseAPlusBinop(APlusBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link AMinusBinop} node from {@link AMinusBinop#apply(IAnalysis)}.
	* @param node the calling {@link AMinusBinop} node
	*/
	public A caseAMinusBinop(AMinusBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link AMultiplyBinop} node from {@link AMultiplyBinop#apply(IAnalysis)}.
	* @param node the calling {@link AMultiplyBinop} node
	*/
	public A caseAMultiplyBinop(AMultiplyBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link ADivideBinop} node from {@link ADivideBinop#apply(IAnalysis)}.
	* @param node the calling {@link ADivideBinop} node
	*/
	public A caseADivideBinop(ADivideBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link ADivBinop} node from {@link ADivBinop#apply(IAnalysis)}.
	* @param node the calling {@link ADivBinop} node
	*/
	public A caseADivBinop(ADivBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link AModBinop} node from {@link AModBinop#apply(IAnalysis)}.
	* @param node the calling {@link AModBinop} node
	*/
	public A caseAModBinop(AModBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link ALessThanBinop} node from {@link ALessThanBinop#apply(IAnalysis)}.
	* @param node the calling {@link ALessThanBinop} node
	*/
	public A caseALessThanBinop(ALessThanBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link ALessEqualBinop} node from {@link ALessEqualBinop#apply(IAnalysis)}.
	* @param node the calling {@link ALessEqualBinop} node
	*/
	public A caseALessEqualBinop(ALessEqualBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link AGreaterThanBinop} node from {@link AGreaterThanBinop#apply(IAnalysis)}.
	* @param node the calling {@link AGreaterThanBinop} node
	*/
	public A caseAGreaterThanBinop(AGreaterThanBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link AGreaterEqualBinop} node from {@link AGreaterEqualBinop#apply(IAnalysis)}.
	* @param node the calling {@link AGreaterEqualBinop} node
	*/
	public A caseAGreaterEqualBinop(AGreaterEqualBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link AEqualBinop} node from {@link AEqualBinop#apply(IAnalysis)}.
	* @param node the calling {@link AEqualBinop} node
	*/
	public A caseAEqualBinop(AEqualBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link ADifferentBinop} node from {@link ADifferentBinop#apply(IAnalysis)}.
	* @param node the calling {@link ADifferentBinop} node
	*/
	public A caseADifferentBinop(ADifferentBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link AOrBinop} node from {@link AOrBinop#apply(IAnalysis)}.
	* @param node the calling {@link AOrBinop} node
	*/
	public A caseAOrBinop(AOrBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link AAndBinop} node from {@link AAndBinop#apply(IAnalysis)}.
	* @param node the calling {@link AAndBinop} node
	*/
	public A caseAAndBinop(AAndBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link AImpliesBinop} node from {@link AImpliesBinop#apply(IAnalysis)}.
	* @param node the calling {@link AImpliesBinop} node
	*/
	public A caseAImpliesBinop(AImpliesBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link AEquivBinop} node from {@link AEquivBinop#apply(IAnalysis)}.
	* @param node the calling {@link AEquivBinop} node
	*/
	public A caseAEquivBinop(AEquivBinop node, Q question) throws Throwable;
	/**
	* Called by the {@link ADeDomain} node from {@link ADeDomain#apply(IAnalysis)}.
	* @param node the calling {@link ADeDomain} node
	*/
	public A caseADeDomain(ADeDomain node, Q question) throws Throwable;
	/**
	* Called by the {@link ACtDomain} node from {@link ACtDomain#apply(IAnalysis)}.
	* @param node the calling {@link ACtDomain} node
	*/
	public A caseACtDomain(ACtDomain node, Q question) throws Throwable;
	/**
	* Called by the {@link AUnaryExp} node from {@link AUnaryExp#apply(IAnalysis)}.
	* @param node the calling {@link AUnaryExp} node
	*/
	public A caseAUnaryExp(AUnaryExp node, Q question) throws Throwable;
	/**
	* Called by the {@link ABinaryExp} node from {@link ABinaryExp#apply(IAnalysis)}.
	* @param node the calling {@link ABinaryExp} node
	*/
	public A caseABinaryExp(ABinaryExp node, Q question) throws Throwable;
	/**
	* Called by the {@link ABoolSingleExp} node from {@link ABoolSingleExp#apply(IAnalysis)}.
	* @param node the calling {@link ABoolSingleExp} node
	*/
	public A caseABoolSingleExp(ABoolSingleExp node, Q question) throws Throwable;
	/**
	* Called by the {@link ANumericalSingleExp} node from {@link ANumericalSingleExp#apply(IAnalysis)}.
	* @param node the calling {@link ANumericalSingleExp} node
	*/
	public A caseANumericalSingleExp(ANumericalSingleExp node, Q question) throws Throwable;
	/**
	* Called by the {@link ATimeSingleExp} node from {@link ATimeSingleExp#apply(IAnalysis)}.
	* @param node the calling {@link ATimeSingleExp} node
	*/
	public A caseATimeSingleExp(ATimeSingleExp node, Q question) throws Throwable;
	/**
	* Called by the {@link AIdentifierSingleExp} node from {@link AIdentifierSingleExp#apply(IAnalysis)}.
	* @param node the calling {@link AIdentifierSingleExp} node
	*/
	public A caseAIdentifierSingleExp(AIdentifierSingleExp node, Q question) throws Throwable;
	/**
	* Called by the {@link ASystemTimeSingleExp} node from {@link ASystemTimeSingleExp#apply(IAnalysis)}.
	* @param node the calling {@link ASystemTimeSingleExp} node
	*/
	public A caseASystemTimeSingleExp(ASystemTimeSingleExp node, Q question) throws Throwable;
	/**
	* Called by the {@link AUsTimeunit} node from {@link AUsTimeunit#apply(IAnalysis)}.
	* @param node the calling {@link AUsTimeunit} node
	*/
	public A caseAUsTimeunit(AUsTimeunit node, Q question) throws Throwable;
	/**
	* Called by the {@link AMsTimeunit} node from {@link AMsTimeunit#apply(IAnalysis)}.
	* @param node the calling {@link AMsTimeunit} node
	*/
	public A caseAMsTimeunit(AMsTimeunit node, Q question) throws Throwable;
	/**
	* Called by the {@link ASTimeunit} node from {@link ASTimeunit#apply(IAnalysis)}.
	* @param node the calling {@link ASTimeunit} node
	*/
	public A caseASTimeunit(ASTimeunit node, Q question) throws Throwable;
	/**
	* Called by the {@link AMTimeunit} node from {@link AMTimeunit#apply(IAnalysis)}.
	* @param node the calling {@link AMTimeunit} node
	*/
	public A caseAMTimeunit(AMTimeunit node, Q question) throws Throwable;
	/**
	* Called by the {@link AHTimeunit} node from {@link AHTimeunit#apply(IAnalysis)}.
	* @param node the calling {@link AHTimeunit} node
	*/
	public A caseAHTimeunit(AHTimeunit node, Q question) throws Throwable;
	/**
	* Called by the {@link AWhenStm} node from {@link AWhenStm#apply(IAnalysis)}.
	* @param node the calling {@link AWhenStm} node
	*/
	public A caseAWhenStm(AWhenStm node, Q question) throws Throwable;
	/**
	* Called by the {@link AOnceStm} node from {@link AOnceStm#apply(IAnalysis)}.
	* @param node the calling {@link AOnceStm} node
	*/
	public A caseAOnceStm(AOnceStm node, Q question) throws Throwable;
	/**
	* Called by the {@link AAssignStm} node from {@link AAssignStm#apply(IAnalysis)}.
	* @param node the calling {@link AAssignStm} node
	*/
	public A caseAAssignStm(AAssignStm node, Q question) throws Throwable;
	/**
	* Called by the {@link ARevertStm} node from {@link ARevertStm#apply(IAnalysis)}.
	* @param node the calling {@link ARevertStm} node
	*/
	public A caseARevertStm(ARevertStm node, Q question) throws Throwable;
	/**
	* Called by the {@link AQuitStm} node from {@link AQuitStm#apply(IAnalysis)}.
	* @param node the calling {@link AQuitStm} node
	*/
	public A caseAQuitStm(AQuitStm node, Q question) throws Throwable;
	/**
	* Called by the {@link APrintMessageStm} node from {@link APrintMessageStm#apply(IAnalysis)}.
	* @param node the calling {@link APrintMessageStm} node
	*/
	public A caseAPrintMessageStm(APrintMessageStm node, Q question) throws Throwable;
	/**
	* Called by the {@link AErrorMessageStm} node from {@link AErrorMessageStm#apply(IAnalysis)}.
	* @param node the calling {@link AErrorMessageStm} node
	*/
	public A caseAErrorMessageStm(AErrorMessageStm node, Q question) throws Throwable;
	/**
	* Called by the {@link AWarnMessageStm} node from {@link AWarnMessageStm#apply(IAnalysis)}.
	* @param node the calling {@link AWarnMessageStm} node
	*/
	public A caseAWarnMessageStm(AWarnMessageStm node, Q question) throws Throwable;
	/**
	* Called by the {@link ARealType} node from {@link ARealType#apply(IAnalysis)}.
	* @param node the calling {@link ARealType} node
	*/
	public A caseARealType(ARealType node, Q question) throws Throwable;
	/**
	* Called by the {@link AIntType} node from {@link AIntType#apply(IAnalysis)}.
	* @param node the calling {@link AIntType} node
	*/
	public A caseAIntType(AIntType node, Q question) throws Throwable;
	/**
	* Called by the {@link ABoolType} node from {@link ABoolType#apply(IAnalysis)}.
	* @param node the calling {@link ABoolType} node
	*/
	public A caseABoolType(ABoolType node, Q question) throws Throwable;
	/**
	* Called by the {@link ATimeType} node from {@link ATimeType#apply(IAnalysis)}.
	* @param node the calling {@link ATimeType} node
	*/
	public A caseATimeType(ATimeType node, Q question) throws Throwable;
	/**
	* Called by the {@link AScriptInclude} node from {@link AScriptInclude#apply(IAnalysis)}.
	* @param node the calling {@link AScriptInclude} node
	*/
	public A caseAScriptInclude(AScriptInclude node, Q question) throws Throwable;

}
