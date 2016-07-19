package org.opentosca.planbuilder.helpers.xpath;

// Generated from xpath.g4 by ANTLR 4.5.3
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link xpathParser}.
 */
public interface xpathListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link xpathParser#main}.
	 * @param ctx the parse tree
	 */
	void enterMain(xpathParser.MainContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#main}.
	 * @param ctx the parse tree
	 */
	void exitMain(xpathParser.MainContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#locationPath}.
	 * @param ctx the parse tree
	 */
	void enterLocationPath(xpathParser.LocationPathContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#locationPath}.
	 * @param ctx the parse tree
	 */
	void exitLocationPath(xpathParser.LocationPathContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#absoluteLocationPathNoroot}.
	 * @param ctx the parse tree
	 */
	void enterAbsoluteLocationPathNoroot(xpathParser.AbsoluteLocationPathNorootContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#absoluteLocationPathNoroot}.
	 * @param ctx the parse tree
	 */
	void exitAbsoluteLocationPathNoroot(xpathParser.AbsoluteLocationPathNorootContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#relativeLocationPath}.
	 * @param ctx the parse tree
	 */
	void enterRelativeLocationPath(xpathParser.RelativeLocationPathContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#relativeLocationPath}.
	 * @param ctx the parse tree
	 */
	void exitRelativeLocationPath(xpathParser.RelativeLocationPathContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#step}.
	 * @param ctx the parse tree
	 */
	void enterStep(xpathParser.StepContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#step}.
	 * @param ctx the parse tree
	 */
	void exitStep(xpathParser.StepContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#axisSpecifier}.
	 * @param ctx the parse tree
	 */
	void enterAxisSpecifier(xpathParser.AxisSpecifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#axisSpecifier}.
	 * @param ctx the parse tree
	 */
	void exitAxisSpecifier(xpathParser.AxisSpecifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#nodeTest}.
	 * @param ctx the parse tree
	 */
	void enterNodeTest(xpathParser.NodeTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#nodeTest}.
	 * @param ctx the parse tree
	 */
	void exitNodeTest(xpathParser.NodeTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterPredicate(xpathParser.PredicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitPredicate(xpathParser.PredicateContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#abbreviatedStep}.
	 * @param ctx the parse tree
	 */
	void enterAbbreviatedStep(xpathParser.AbbreviatedStepContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#abbreviatedStep}.
	 * @param ctx the parse tree
	 */
	void exitAbbreviatedStep(xpathParser.AbbreviatedStepContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(xpathParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(xpathParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(xpathParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(xpathParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(xpathParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(xpathParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#unionExprNoRoot}.
	 * @param ctx the parse tree
	 */
	void enterUnionExprNoRoot(xpathParser.UnionExprNoRootContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#unionExprNoRoot}.
	 * @param ctx the parse tree
	 */
	void exitUnionExprNoRoot(xpathParser.UnionExprNoRootContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#pathExprNoRoot}.
	 * @param ctx the parse tree
	 */
	void enterPathExprNoRoot(xpathParser.PathExprNoRootContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#pathExprNoRoot}.
	 * @param ctx the parse tree
	 */
	void exitPathExprNoRoot(xpathParser.PathExprNoRootContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#filterExpr}.
	 * @param ctx the parse tree
	 */
	void enterFilterExpr(xpathParser.FilterExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#filterExpr}.
	 * @param ctx the parse tree
	 */
	void exitFilterExpr(xpathParser.FilterExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterOrExpr(xpathParser.OrExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitOrExpr(xpathParser.OrExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void enterAndExpr(xpathParser.AndExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void exitAndExpr(xpathParser.AndExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#equalityExpr}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpr(xpathParser.EqualityExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#equalityExpr}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpr(xpathParser.EqualityExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpr(xpathParser.RelationalExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpr(xpathParser.RelationalExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#additiveExpr}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpr(xpathParser.AdditiveExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#additiveExpr}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpr(xpathParser.AdditiveExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpr(xpathParser.MultiplicativeExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#multiplicativeExpr}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpr(xpathParser.MultiplicativeExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#unaryExprNoRoot}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExprNoRoot(xpathParser.UnaryExprNoRootContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#unaryExprNoRoot}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExprNoRoot(xpathParser.UnaryExprNoRootContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#qName}.
	 * @param ctx the parse tree
	 */
	void enterQName(xpathParser.QNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#qName}.
	 * @param ctx the parse tree
	 */
	void exitQName(xpathParser.QNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#functionName}.
	 * @param ctx the parse tree
	 */
	void enterFunctionName(xpathParser.FunctionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#functionName}.
	 * @param ctx the parse tree
	 */
	void exitFunctionName(xpathParser.FunctionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#variableReference}.
	 * @param ctx the parse tree
	 */
	void enterVariableReference(xpathParser.VariableReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#variableReference}.
	 * @param ctx the parse tree
	 */
	void exitVariableReference(xpathParser.VariableReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void enterNameTest(xpathParser.NameTestContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#nameTest}.
	 * @param ctx the parse tree
	 */
	void exitNameTest(xpathParser.NameTestContext ctx);
	/**
	 * Enter a parse tree produced by {@link xpathParser#nCName}.
	 * @param ctx the parse tree
	 */
	void enterNCName(xpathParser.NCNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link xpathParser#nCName}.
	 * @param ctx the parse tree
	 */
	void exitNCName(xpathParser.NCNameContext ctx);
}