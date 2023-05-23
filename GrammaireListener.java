// Generated from Grammaire.g4 by ANTLR 4.7.2

import Expression.*;
import Statement.*;
import Boolean.*;
import Util.*;
import Number.*;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GrammaireParser}.
 */
public interface GrammaireListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GrammaireParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(GrammaireParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammaireParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(GrammaireParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammaireParser#baseexpr}.
	 * @param ctx the parse tree
	 */
	void enterBaseexpr(GrammaireParser.BaseexprContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammaireParser#baseexpr}.
	 * @param ctx the parse tree
	 */
	void exitBaseexpr(GrammaireParser.BaseexprContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammaireParser#statementList}.
	 * @param ctx the parse tree
	 */
	void enterStatementList(GrammaireParser.StatementListContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammaireParser#statementList}.
	 * @param ctx the parse tree
	 */
	void exitStatementList(GrammaireParser.StatementListContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammaireParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(GrammaireParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammaireParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(GrammaireParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammaireParser#boolexp}.
	 * @param ctx the parse tree
	 */
	void enterBoolexp(GrammaireParser.BoolexpContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammaireParser#boolexp}.
	 * @param ctx the parse tree
	 */
	void exitBoolexp(GrammaireParser.BoolexpContext ctx);
}