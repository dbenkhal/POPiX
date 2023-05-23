// Generated from Grammaire.g4 by ANTLR 4.7.2

import Expression.*;
import Statement.*;
import Boolean.*;
import Util.*;
import Number.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GrammaireParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INTERVALLE=1, CONSTF=2, CONSTI=3, RANGERANDOMFLOAT=4, REQUIREACCURACY=5, 
		DIV=6, MULT=7, MINUS=8, PLUS=9, VIRG=10, TRUE=11, FALSE=12, GT=13, GE=14, 
		LT=15, LE=16, EQ=17, NEQ=18, NOT=19, PV=20, ASSIGN=21, IF=22, ELSE=23, 
		WHILE=24, FOR=25, LPAREN=26, RPAREN=27, LBRACKET=28, INF=29, SUP=30, RBRACKET=31, 
		NEWLINE=32, END=33, WS=34, COMMENT=35, LINE_COMMENT=36, IDENTIFIER=37;
	public static final int
		RULE_expression = 0, RULE_baseexpr = 1, RULE_statementList = 2, RULE_statement = 3, 
		RULE_boolexp = 4;
	private static String[] makeRuleNames() {
		return new String[] {
			"expression", "baseexpr", "statementList", "statement", "boolexp"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, "'require_accuracy'", "'/'", "'*'", "'-'", 
			"'+'", "','", "'true'", "'false'", "'>'", "'>='", "'<'", "'<='", "'=='", 
			null, "'!'", "';'", "'='", "'if'", "'else'", "'while'", "'for'", "'('", 
			"')'", "'{'", "'['", "']'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "INTERVALLE", "CONSTF", "CONSTI", "RANGERANDOMFLOAT", "REQUIREACCURACY", 
			"DIV", "MULT", "MINUS", "PLUS", "VIRG", "TRUE", "FALSE", "GT", "GE", 
			"LT", "LE", "EQ", "NEQ", "NOT", "PV", "ASSIGN", "IF", "ELSE", "WHILE", 
			"FOR", "LPAREN", "RPAREN", "LBRACKET", "INF", "SUP", "RBRACKET", "NEWLINE", 
			"END", "WS", "COMMENT", "LINE_COMMENT", "IDENTIFIER"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Grammaire.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


	AbstractClass left;
	AbstractClass right;
	float b;
	int i;
	int id;
	int lab;	
	Numbers xmin, xmax;
	String nom;	
	AbstractClassB cond;
	StatementList thenBlock;
	StatementList elseBlock;
	StatementList body;
	AbstractClass prec ;
	Environment env;
	public GrammaireParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExpressionContext extends ParserRuleContext {
		public AbstractClass result;
		public BaseexprContext left;
		public ExpressionContext right;
		public BaseexprContext baseexpr() {
			return getRuleContext(BaseexprContext.class,0);
		}
		public TerminalNode PLUS() { return getToken(GrammaireParser.PLUS, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode MINUS() { return getToken(GrammaireParser.MINUS, 0); }
		public TerminalNode MULT() { return getToken(GrammaireParser.MULT, 0); }
		public TerminalNode DIV() { return getToken(GrammaireParser.DIV, 0); }
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GrammaireListener ) ((GrammaireListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GrammaireListener ) ((GrammaireListener)listener).exitExpression(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expression);
		try {
			setState(33);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(10);
				((ExpressionContext)_localctx).left = baseexpr();
				 
						 System.out.println("left expression ");
						 	System.out.println(((ExpressionContext)_localctx).result = ((ExpressionContext)_localctx).left.result);
						 	 
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(13);
				((ExpressionContext)_localctx).left = baseexpr();
				setState(14);
				match(PLUS);
				setState(15);
				((ExpressionContext)_localctx).right = expression();
				 
							 assert(((ExpressionContext)_localctx).left.result != null);  
							 assert(((ExpressionContext)_localctx).right.result != null);
						
							AbstractClass.newLabDecrease();
							((ExpressionContext)_localctx).result =  new Add(((ExpressionContext)_localctx).left.result,((ExpressionContext)_localctx).right.result);
							
															
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(18);
				((ExpressionContext)_localctx).left = baseexpr();
				setState(19);
				match(MINUS);
				setState(20);
				((ExpressionContext)_localctx).right = expression();

							 assert(((ExpressionContext)_localctx).left.result != null);  
							 assert(((ExpressionContext)_localctx).right.result != null); 
							 AbstractClass.newLabDecrease();
							((ExpressionContext)_localctx).result =  new Subtract(((ExpressionContext)_localctx).left.result,((ExpressionContext)_localctx).right.result);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(23);
				((ExpressionContext)_localctx).left = baseexpr();
				setState(24);
				match(MULT);
				setState(25);
				((ExpressionContext)_localctx).right = expression();

							 assert(((ExpressionContext)_localctx).left.result != null);  
							 assert(((ExpressionContext)_localctx).right.result != null); 
							 AbstractClass.newLabDecrease();
							((ExpressionContext)_localctx).result =  new Multiply(((ExpressionContext)_localctx).left.result, ((ExpressionContext)_localctx).right.result);
							
									 		                    
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(28);
				((ExpressionContext)_localctx).left = baseexpr();
				setState(29);
				match(DIV);
				setState(30);
				((ExpressionContext)_localctx).right = expression();

							 assert(((ExpressionContext)_localctx).left.result != null);  
							 assert(((ExpressionContext)_localctx).right.result != null); 
							 AbstractClass.newLabDecrease();
							((ExpressionContext)_localctx).result =  new Divide(((ExpressionContext)_localctx).left.result, ((ExpressionContext)_localctx).right.result);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BaseexprContext extends ParserRuleContext {
		public AbstractClass result;
		public Token id;
		public Token CONSTF;
		public Token CONSTI;
		public BaseexprContext xmin;
		public BaseexprContext xmax;
		public TerminalNode IDENTIFIER() { return getToken(GrammaireParser.IDENTIFIER, 0); }
		public TerminalNode CONSTF() { return getToken(GrammaireParser.CONSTF, 0); }
		public TerminalNode CONSTI() { return getToken(GrammaireParser.CONSTI, 0); }
		public TerminalNode INF() { return getToken(GrammaireParser.INF, 0); }
		public TerminalNode VIRG() { return getToken(GrammaireParser.VIRG, 0); }
		public TerminalNode SUP() { return getToken(GrammaireParser.SUP, 0); }
		public List<BaseexprContext> baseexpr() {
			return getRuleContexts(BaseexprContext.class);
		}
		public BaseexprContext baseexpr(int i) {
			return getRuleContext(BaseexprContext.class,i);
		}
		public BaseexprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_baseexpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GrammaireListener ) ((GrammaireListener)listener).enterBaseexpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GrammaireListener ) ((GrammaireListener)listener).exitBaseexpr(this);
		}
	}

	public final BaseexprContext baseexpr() throws RecognitionException {
		BaseexprContext _localctx = new BaseexprContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_baseexpr);
		try {
			setState(48);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(35);
				((BaseexprContext)_localctx).id = match(IDENTIFIER);
				}
				 ((BaseexprContext)_localctx).result =  new Variable( ((BaseexprContext)_localctx).id.getText());
				}
				break;
			case CONSTF:
				enterOuterAlt(_localctx, 2);
				{
				setState(37);
				((BaseexprContext)_localctx).CONSTF = match(CONSTF);
				 
					lab = AbstractClass.newLab();    	 	
				    	 	System.out.println("float number = "+(((BaseexprContext)_localctx).CONSTF!=null?((BaseexprContext)_localctx).CONSTF.getText():null));
				  			Numbers r = new FloatNumbers(Float.parseFloat((((BaseexprContext)_localctx).CONSTF!=null?((BaseexprContext)_localctx).CONSTF.getText():null)),lab);
				    		 ((BaseexprContext)_localctx).result =   new Intervalle(r, r,lab);
				    
				}
				break;
			case CONSTI:
				enterOuterAlt(_localctx, 3);
				{
				setState(39);
				((BaseexprContext)_localctx).CONSTI = match(CONSTI);
				 
				     	
				    	 System.out.println("int number = "+(((BaseexprContext)_localctx).CONSTI!=null?((BaseexprContext)_localctx).CONSTI.getText():null));
				         System.out.println();
				         lab=AbstractClass.newLab();
				         Numbers ii = new IntNumbers(Integer.parseInt((((BaseexprContext)_localctx).CONSTI!=null?((BaseexprContext)_localctx).CONSTI.getText():null)),lab);
				         System.out.println(ii.getIntValue());
				          ((BaseexprContext)_localctx).result =   new Intervalle(ii, ii,lab);
				    
				}
				break;
			case INF:
				enterOuterAlt(_localctx, 4);
				{
				setState(41);
				match(INF);
				setState(42);
				((BaseexprContext)_localctx).xmin = baseexpr();
				setState(43);
				match(VIRG);
				setState(44);
				((BaseexprContext)_localctx).xmax = baseexpr();
				setState(45);
				match(SUP);

				    	
				    	System.out.println("intervalle");
				    	AbstractClass.newLabDecrease2();
				    	lab =AbstractClass.newLab();
				    	Numbers xmin = new FloatNumbers(Float.parseFloat((((BaseexprContext)_localctx).xmin!=null?_input.getText(((BaseexprContext)_localctx).xmin.start,((BaseexprContext)_localctx).xmin.stop):null)),lab);
				    	Numbers xmax = new FloatNumbers(Float.parseFloat((((BaseexprContext)_localctx).xmax!=null?_input.getText(((BaseexprContext)_localctx).xmax.start,((BaseexprContext)_localctx).xmax.stop):null)),lab);    
				        assert(((BaseexprContext)_localctx).xmin.result != null);  
					    assert(((BaseexprContext)_localctx).xmax.result != null);    
				       ((BaseexprContext)_localctx).result =   new Intervalle(xmin, xmax,lab);
				    
				    	
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementListContext extends ParserRuleContext {
		public StatementList st;
		public StatementContext s1;
		public StatementListContext s2;
		public TerminalNode PV() { return getToken(GrammaireParser.PV, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public StatementListContext statementList() {
			return getRuleContext(StatementListContext.class,0);
		}
		public StatementListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statementList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GrammaireListener ) ((GrammaireListener)listener).enterStatementList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GrammaireListener ) ((GrammaireListener)listener).exitStatementList(this);
		}
	}

	public final StatementListContext statementList() throws RecognitionException {
		StatementListContext _localctx = new StatementListContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_statementList);
		try {
			setState(59);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(50);
				((StatementListContext)_localctx).s1 = statement();
				}
				setState(51);
				match(PV);
				{
				setState(52);
				((StatementListContext)_localctx).s2 = statementList();
				}

				    
				    	
				    	((StatementListContext)_localctx).st =  new StatementList(((StatementListContext)_localctx).s1.result,((StatementListContext)_localctx).s2.st);
				    
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(55);
				((StatementListContext)_localctx).s1 = statement();
				}
				setState(56);
				match(PV);

				    	    	
				    	((StatementListContext)_localctx).st =  new StatementList(((StatementListContext)_localctx).s1.result);
				    
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public AbstractClassS result;
		public Token id;
		public ExpressionContext right;
		public BoolexpContext condition;
		public StatementListContext thenBlock;
		public StatementListContext elseBlock;
		public StatementListContext body;
		public ExpressionContext begin;
		public ExpressionContext end;
		public Token name;
		public BaseexprContext prec;
		public TerminalNode ASSIGN() { return getToken(GrammaireParser.ASSIGN, 0); }
		public TerminalNode IDENTIFIER() { return getToken(GrammaireParser.IDENTIFIER, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode IF() { return getToken(GrammaireParser.IF, 0); }
		public TerminalNode LPAREN() { return getToken(GrammaireParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(GrammaireParser.RPAREN, 0); }
		public List<TerminalNode> LBRACKET() { return getTokens(GrammaireParser.LBRACKET); }
		public TerminalNode LBRACKET(int i) {
			return getToken(GrammaireParser.LBRACKET, i);
		}
		public List<TerminalNode> RBRACKET() { return getTokens(GrammaireParser.RBRACKET); }
		public TerminalNode RBRACKET(int i) {
			return getToken(GrammaireParser.RBRACKET, i);
		}
		public BoolexpContext boolexp() {
			return getRuleContext(BoolexpContext.class,0);
		}
		public List<StatementListContext> statementList() {
			return getRuleContexts(StatementListContext.class);
		}
		public StatementListContext statementList(int i) {
			return getRuleContext(StatementListContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(GrammaireParser.ELSE, 0); }
		public TerminalNode WHILE() { return getToken(GrammaireParser.WHILE, 0); }
		public TerminalNode FOR() { return getToken(GrammaireParser.FOR, 0); }
		public List<TerminalNode> PV() { return getTokens(GrammaireParser.PV); }
		public TerminalNode PV(int i) {
			return getToken(GrammaireParser.PV, i);
		}
		public TerminalNode REQUIREACCURACY() { return getToken(GrammaireParser.REQUIREACCURACY, 0); }
		public TerminalNode VIRG() { return getToken(GrammaireParser.VIRG, 0); }
		public BaseexprContext baseexpr() {
			return getRuleContext(BaseexprContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GrammaireListener ) ((GrammaireListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GrammaireListener ) ((GrammaireListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_statement);
		int _la;
		try {
			setState(113);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(61);
				((StatementContext)_localctx).id = match(IDENTIFIER);
				setState(62);
				match(ASSIGN);
				setState(63);
				((StatementContext)_localctx).right = expression();
				}
				((StatementContext)_localctx).result =  new Assign(((StatementContext)_localctx).right.result,(((StatementContext)_localctx).id!=null?((StatementContext)_localctx).id.getText():null)); 
				}
				break;
			case IF:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(67);
				match(IF);
				setState(68);
				match(LPAREN);
				setState(69);
				((StatementContext)_localctx).condition = boolexp();
				setState(70);
				match(RPAREN);
				setState(71);
				match(LBRACKET);
				setState(72);
				((StatementContext)_localctx).thenBlock = statementList();
				setState(73);
				match(RBRACKET);
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(74);
					match(ELSE);
					setState(75);
					match(LBRACKET);
					setState(76);
					((StatementContext)_localctx).elseBlock = statementList();
					setState(77);
					match(RBRACKET);
					}
				}

				}

				      		
				      		  assert((((StatementContext)_localctx).condition!=null?_input.getText(((StatementContext)_localctx).condition.start,((StatementContext)_localctx).condition.stop):null) != null);
				              assert((((StatementContext)_localctx).thenBlock!=null?_input.getText(((StatementContext)_localctx).thenBlock.start,((StatementContext)_localctx).thenBlock.stop):null) != null);
				              assert((((StatementContext)_localctx).elseBlock!=null?_input.getText(((StatementContext)_localctx).elseBlock.start,((StatementContext)_localctx).elseBlock.stop):null) != null);
				             
				      		((StatementContext)_localctx).result =  new IfThenElse(((StatementContext)_localctx).condition.result, ((StatementContext)_localctx).thenBlock.st, ((StatementContext)_localctx).elseBlock.st);
				      	
				}
				break;
			case WHILE:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(83);
				match(WHILE);
				setState(84);
				match(LPAREN);
				setState(85);
				((StatementContext)_localctx).condition = boolexp();
				setState(86);
				match(RPAREN);
				setState(87);
				match(LBRACKET);
				setState(88);
				((StatementContext)_localctx).body = statementList();
				setState(89);
				match(RBRACKET);
				}
				     	 				
				   
				     ((StatementContext)_localctx).result =  new While(((StatementContext)_localctx).condition.result, ((StatementContext)_localctx).body.st);
				}
				break;
			case FOR:
				enterOuterAlt(_localctx, 4);
				{
				{
				setState(93);
				match(FOR);
				setState(94);
				match(LPAREN);
				setState(95);
				((StatementContext)_localctx).begin = expression();
				setState(96);
				match(PV);
				setState(97);
				((StatementContext)_localctx).end = expression();
				setState(98);
				match(PV);
				setState(99);
				((StatementContext)_localctx).body = statementList();
				setState(100);
				match(RPAREN);
				}
				    	 				
				    ((StatementContext)_localctx).result =  new For(((StatementContext)_localctx).begin.result,((StatementContext)_localctx).end.result ,((StatementContext)_localctx).body.st);
				}
				break;
			case REQUIREACCURACY:
				enterOuterAlt(_localctx, 5);
				{
				{
				setState(104);
				match(REQUIREACCURACY);
				setState(105);
				match(LPAREN);
				setState(106);
				((StatementContext)_localctx).name = match(IDENTIFIER);
				setState(107);
				match(VIRG);
				setState(108);
				((StatementContext)_localctx).prec = baseexpr();
				setState(109);
				match(RPAREN);
				}

				     
				     	Intervalle i= prec.getInterv();
				        int pp = i.getInf().getIntValue();       	
				        System.out.println("********"+pp);    	 				
				  	  ((StatementContext)_localctx).result =  new RequireAccuracy((((StatementContext)_localctx).name!=null?((StatementContext)_localctx).name.getText():null), pp); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BoolexpContext extends ParserRuleContext {
		public AbstractClassB result;
		public BaseexprContext left;
		public ExpressionContext right;
		public TerminalNode EQ() { return getToken(GrammaireParser.EQ, 0); }
		public BaseexprContext baseexpr() {
			return getRuleContext(BaseexprContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode GT() { return getToken(GrammaireParser.GT, 0); }
		public TerminalNode GE() { return getToken(GrammaireParser.GE, 0); }
		public TerminalNode LT() { return getToken(GrammaireParser.LT, 0); }
		public TerminalNode LE() { return getToken(GrammaireParser.LE, 0); }
		public BoolexpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolexp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof GrammaireListener ) ((GrammaireListener)listener).enterBoolexp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof GrammaireListener ) ((GrammaireListener)listener).exitBoolexp(this);
		}
	}

	public final BoolexpContext boolexp() throws RecognitionException {
		BoolexpContext _localctx = new BoolexpContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_boolexp);
		try {
			setState(145);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(115);
				((BoolexpContext)_localctx).left = baseexpr();
				setState(116);
				match(EQ);
				setState(117);
				((BoolexpContext)_localctx).right = expression();
				}

				          	 assert(((BoolexpContext)_localctx).left.result != null);  
							 assert(((BoolexpContext)_localctx).right.result != null);   
					
							 ((BoolexpContext)_localctx).result =  new Equal(((BoolexpContext)_localctx).left.result, ((BoolexpContext)_localctx).right.result);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(121);
				((BoolexpContext)_localctx).left = baseexpr();
				setState(122);
				match(GT);
				setState(123);
				((BoolexpContext)_localctx).right = expression();
				}

							 assert(((BoolexpContext)_localctx).left.result != null);  
							 assert(((BoolexpContext)_localctx).right.result != null);   
							 
							((BoolexpContext)_localctx).result =  new Greater(((BoolexpContext)_localctx).left.result, ((BoolexpContext)_localctx).right.result);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(127);
				((BoolexpContext)_localctx).left = baseexpr();
				setState(128);
				match(GE);
				setState(129);
				((BoolexpContext)_localctx).right = expression();
				}
				 
							 assert(((BoolexpContext)_localctx).left.result != null);  
							 assert(((BoolexpContext)_localctx).right.result != null);   
							 
							((BoolexpContext)_localctx).result =  new GreaterEqual(((BoolexpContext)_localctx).left.result,  ((BoolexpContext)_localctx).right.result);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				{
				setState(133);
				((BoolexpContext)_localctx).left = baseexpr();
				setState(134);
				match(LT);
				setState(135);
				((BoolexpContext)_localctx).right = expression();
				}

							 assert(((BoolexpContext)_localctx).left.result != null);  
							 assert(((BoolexpContext)_localctx).right.result != null);  
							  
							((BoolexpContext)_localctx).result = new Less(((BoolexpContext)_localctx).left.result, ((BoolexpContext)_localctx).right.result);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				{
				setState(139);
				((BoolexpContext)_localctx).left = baseexpr();
				setState(140);
				match(LE);
				setState(141);
				((BoolexpContext)_localctx).right = expression();
				}

					
					    	 assert(((BoolexpContext)_localctx).left.result != null);  
							 assert(((BoolexpContext)_localctx).right.result != null);  
							 
					    	((BoolexpContext)_localctx).result =  new LessEqual(left, right);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\'\u0096\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2$\n\2\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\63\n\3\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\5\4>\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5R\n\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5t\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\5\6\u0094\n\6\3\6\2\2\7\2\4\6\b\n\2\2\2\u00a1\2"+
		"#\3\2\2\2\4\62\3\2\2\2\6=\3\2\2\2\bs\3\2\2\2\n\u0093\3\2\2\2\f\r\5\4\3"+
		"\2\r\16\b\2\1\2\16$\3\2\2\2\17\20\5\4\3\2\20\21\7\13\2\2\21\22\5\2\2\2"+
		"\22\23\b\2\1\2\23$\3\2\2\2\24\25\5\4\3\2\25\26\7\n\2\2\26\27\5\2\2\2\27"+
		"\30\b\2\1\2\30$\3\2\2\2\31\32\5\4\3\2\32\33\7\t\2\2\33\34\5\2\2\2\34\35"+
		"\b\2\1\2\35$\3\2\2\2\36\37\5\4\3\2\37 \7\b\2\2 !\5\2\2\2!\"\b\2\1\2\""+
		"$\3\2\2\2#\f\3\2\2\2#\17\3\2\2\2#\24\3\2\2\2#\31\3\2\2\2#\36\3\2\2\2$"+
		"\3\3\2\2\2%&\7\'\2\2&\63\b\3\1\2\'(\7\4\2\2(\63\b\3\1\2)*\7\5\2\2*\63"+
		"\b\3\1\2+,\7\37\2\2,-\5\4\3\2-.\7\f\2\2./\5\4\3\2/\60\7 \2\2\60\61\b\3"+
		"\1\2\61\63\3\2\2\2\62%\3\2\2\2\62\'\3\2\2\2\62)\3\2\2\2\62+\3\2\2\2\63"+
		"\5\3\2\2\2\64\65\5\b\5\2\65\66\7\26\2\2\66\67\5\6\4\2\678\b\4\1\28>\3"+
		"\2\2\29:\5\b\5\2:;\7\26\2\2;<\b\4\1\2<>\3\2\2\2=\64\3\2\2\2=9\3\2\2\2"+
		">\7\3\2\2\2?@\7\'\2\2@A\7\27\2\2AB\5\2\2\2BC\3\2\2\2CD\b\5\1\2Dt\3\2\2"+
		"\2EF\7\30\2\2FG\7\34\2\2GH\5\n\6\2HI\7\35\2\2IJ\7\36\2\2JK\5\6\4\2KQ\7"+
		"!\2\2LM\7\31\2\2MN\7\36\2\2NO\5\6\4\2OP\7!\2\2PR\3\2\2\2QL\3\2\2\2QR\3"+
		"\2\2\2RS\3\2\2\2ST\b\5\1\2Tt\3\2\2\2UV\7\32\2\2VW\7\34\2\2WX\5\n\6\2X"+
		"Y\7\35\2\2YZ\7\36\2\2Z[\5\6\4\2[\\\7!\2\2\\]\3\2\2\2]^\b\5\1\2^t\3\2\2"+
		"\2_`\7\33\2\2`a\7\34\2\2ab\5\2\2\2bc\7\26\2\2cd\5\2\2\2de\7\26\2\2ef\5"+
		"\6\4\2fg\7\35\2\2gh\3\2\2\2hi\b\5\1\2it\3\2\2\2jk\7\7\2\2kl\7\34\2\2l"+
		"m\7\'\2\2mn\7\f\2\2no\5\4\3\2op\7\35\2\2pq\3\2\2\2qr\b\5\1\2rt\3\2\2\2"+
		"s?\3\2\2\2sE\3\2\2\2sU\3\2\2\2s_\3\2\2\2sj\3\2\2\2t\t\3\2\2\2uv\5\4\3"+
		"\2vw\7\23\2\2wx\5\2\2\2xy\3\2\2\2yz\b\6\1\2z\u0094\3\2\2\2{|\5\4\3\2|"+
		"}\7\17\2\2}~\5\2\2\2~\177\3\2\2\2\177\u0080\b\6\1\2\u0080\u0094\3\2\2"+
		"\2\u0081\u0082\5\4\3\2\u0082\u0083\7\20\2\2\u0083\u0084\5\2\2\2\u0084"+
		"\u0085\3\2\2\2\u0085\u0086\b\6\1\2\u0086\u0094\3\2\2\2\u0087\u0088\5\4"+
		"\3\2\u0088\u0089\7\21\2\2\u0089\u008a\5\2\2\2\u008a\u008b\3\2\2\2\u008b"+
		"\u008c\b\6\1\2\u008c\u0094\3\2\2\2\u008d\u008e\5\4\3\2\u008e\u008f\7\22"+
		"\2\2\u008f\u0090\5\2\2\2\u0090\u0091\3\2\2\2\u0091\u0092\b\6\1\2\u0092"+
		"\u0094\3\2\2\2\u0093u\3\2\2\2\u0093{\3\2\2\2\u0093\u0081\3\2\2\2\u0093"+
		"\u0087\3\2\2\2\u0093\u008d\3\2\2\2\u0094\13\3\2\2\2\b#\62=Qs\u0093";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}