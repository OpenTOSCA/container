package org.opentosca.planbuilder.helpers.xpath;

// Generated from xpath.g4 by ANTLR 4.5.3
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class xpathParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, NodeType=9, 
		Number=10, AxisName=11, PATHSEP=12, ABRPATH=13, LPAR=14, RPAR=15, LBRAC=16, 
		RBRAC=17, MINUS=18, PLUS=19, DOT=20, MUL=21, DOTDOT=22, AT=23, COMMA=24, 
		PIPE=25, LESS=26, MORE_=27, LE=28, GE=29, COLON=30, CC=31, APOS=32, QUOT=33, 
		Literal=34, Whitespace=35, NCName=36;
	public static final int
		RULE_main = 0, RULE_locationPath = 1, RULE_absoluteLocationPathNoroot = 2, 
		RULE_relativeLocationPath = 3, RULE_step = 4, RULE_axisSpecifier = 5, 
		RULE_nodeTest = 6, RULE_predicate = 7, RULE_abbreviatedStep = 8, RULE_expr = 9, 
		RULE_primaryExpr = 10, RULE_functionCall = 11, RULE_unionExprNoRoot = 12, 
		RULE_pathExprNoRoot = 13, RULE_filterExpr = 14, RULE_orExpr = 15, RULE_andExpr = 16, 
		RULE_equalityExpr = 17, RULE_relationalExpr = 18, RULE_additiveExpr = 19, 
		RULE_multiplicativeExpr = 20, RULE_unaryExprNoRoot = 21, RULE_qName = 22, 
		RULE_functionName = 23, RULE_variableReference = 24, RULE_nameTest = 25, 
		RULE_nCName = 26;
	public static final String[] ruleNames = {
		"main", "locationPath", "absoluteLocationPathNoroot", "relativeLocationPath", 
		"step", "axisSpecifier", "nodeTest", "predicate", "abbreviatedStep", "expr", 
		"primaryExpr", "functionCall", "unionExprNoRoot", "pathExprNoRoot", "filterExpr", 
		"orExpr", "andExpr", "equalityExpr", "relationalExpr", "additiveExpr", 
		"multiplicativeExpr", "unaryExprNoRoot", "qName", "functionName", "variableReference", 
		"nameTest", "nCName"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'processing-instruction'", "'or'", "'and'", "'='", "'!='", "'div'", 
		"'mod'", "'$'", null, null, null, "'/'", "'//'", "'('", "')'", "'['", 
		"']'", "'-'", "'+'", "'.'", "'*'", "'..'", "'@'", "','", "'|'", "'<'", 
		"'>'", "'<='", "'>='", "':'", "'::'", "'''", "'\"'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, "NodeType", "Number", 
		"AxisName", "PATHSEP", "ABRPATH", "LPAR", "RPAR", "LBRAC", "RBRAC", "MINUS", 
		"PLUS", "DOT", "MUL", "DOTDOT", "AT", "COMMA", "PIPE", "LESS", "MORE_", 
		"LE", "GE", "COLON", "CC", "APOS", "QUOT", "Literal", "Whitespace", "NCName"
	};
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
	public String getGrammarFileName() { return "xpath.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public xpathParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class MainContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public MainContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_main; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterMain(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitMain(this);
		}
	}

	public final MainContext main() throws RecognitionException {
		MainContext _localctx = new MainContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_main);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			expr();
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

	public static class LocationPathContext extends ParserRuleContext {
		public RelativeLocationPathContext relativeLocationPath() {
			return getRuleContext(RelativeLocationPathContext.class,0);
		}
		public AbsoluteLocationPathNorootContext absoluteLocationPathNoroot() {
			return getRuleContext(AbsoluteLocationPathNorootContext.class,0);
		}
		public LocationPathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_locationPath; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterLocationPath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitLocationPath(this);
		}
	}

	public final LocationPathContext locationPath() throws RecognitionException {
		LocationPathContext _localctx = new LocationPathContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_locationPath);
		try {
			setState(58);
			switch (_input.LA(1)) {
			case T__0:
			case NodeType:
			case AxisName:
			case DOT:
			case MUL:
			case DOTDOT:
			case AT:
			case NCName:
				enterOuterAlt(_localctx, 1);
				{
				setState(56);
				relativeLocationPath();
				}
				break;
			case PATHSEP:
			case ABRPATH:
				enterOuterAlt(_localctx, 2);
				{
				setState(57);
				absoluteLocationPathNoroot();
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

	public static class AbsoluteLocationPathNorootContext extends ParserRuleContext {
		public RelativeLocationPathContext relativeLocationPath() {
			return getRuleContext(RelativeLocationPathContext.class,0);
		}
		public AbsoluteLocationPathNorootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_absoluteLocationPathNoroot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterAbsoluteLocationPathNoroot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitAbsoluteLocationPathNoroot(this);
		}
	}

	public final AbsoluteLocationPathNorootContext absoluteLocationPathNoroot() throws RecognitionException {
		AbsoluteLocationPathNorootContext _localctx = new AbsoluteLocationPathNorootContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_absoluteLocationPathNoroot);
		try {
			setState(64);
			switch (_input.LA(1)) {
			case PATHSEP:
				enterOuterAlt(_localctx, 1);
				{
				setState(60);
				match(PATHSEP);
				setState(61);
				relativeLocationPath();
				}
				break;
			case ABRPATH:
				enterOuterAlt(_localctx, 2);
				{
				setState(62);
				match(ABRPATH);
				setState(63);
				relativeLocationPath();
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

	public static class RelativeLocationPathContext extends ParserRuleContext {
		public List<StepContext> step() {
			return getRuleContexts(StepContext.class);
		}
		public StepContext step(int i) {
			return getRuleContext(StepContext.class,i);
		}
		public RelativeLocationPathContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relativeLocationPath; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterRelativeLocationPath(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitRelativeLocationPath(this);
		}
	}

	public final RelativeLocationPathContext relativeLocationPath() throws RecognitionException {
		RelativeLocationPathContext _localctx = new RelativeLocationPathContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_relativeLocationPath);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(66);
			step();
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PATHSEP || _la==ABRPATH) {
				{
				{
				setState(67);
				_la = _input.LA(1);
				if ( !(_la==PATHSEP || _la==ABRPATH) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(68);
				step();
				}
				}
				setState(73);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class StepContext extends ParserRuleContext {
		public AxisSpecifierContext axisSpecifier() {
			return getRuleContext(AxisSpecifierContext.class,0);
		}
		public NodeTestContext nodeTest() {
			return getRuleContext(NodeTestContext.class,0);
		}
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public AbbreviatedStepContext abbreviatedStep() {
			return getRuleContext(AbbreviatedStepContext.class,0);
		}
		public StepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_step; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterStep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitStep(this);
		}
	}

	public final StepContext step() throws RecognitionException {
		StepContext _localctx = new StepContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_step);
		int _la;
		try {
			setState(83);
			switch (_input.LA(1)) {
			case T__0:
			case NodeType:
			case AxisName:
			case MUL:
			case AT:
			case NCName:
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				axisSpecifier();
				setState(75);
				nodeTest();
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==LBRAC) {
					{
					{
					setState(76);
					predicate();
					}
					}
					setState(81);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case DOT:
			case DOTDOT:
				enterOuterAlt(_localctx, 2);
				{
				setState(82);
				abbreviatedStep();
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

	public static class AxisSpecifierContext extends ParserRuleContext {
		public TerminalNode AxisName() { return getToken(xpathParser.AxisName, 0); }
		public AxisSpecifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_axisSpecifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterAxisSpecifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitAxisSpecifier(this);
		}
	}

	public final AxisSpecifierContext axisSpecifier() throws RecognitionException {
		AxisSpecifierContext _localctx = new AxisSpecifierContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_axisSpecifier);
		int _la;
		try {
			setState(90);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(85);
				match(AxisName);
				setState(86);
				match(CC);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(88);
				_la = _input.LA(1);
				if (_la==AT) {
					{
					setState(87);
					match(AT);
					}
				}

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

	public static class NodeTestContext extends ParserRuleContext {
		public NameTestContext nameTest() {
			return getRuleContext(NameTestContext.class,0);
		}
		public TerminalNode NodeType() { return getToken(xpathParser.NodeType, 0); }
		public TerminalNode Literal() { return getToken(xpathParser.Literal, 0); }
		public NodeTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nodeTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterNodeTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitNodeTest(this);
		}
	}

	public final NodeTestContext nodeTest() throws RecognitionException {
		NodeTestContext _localctx = new NodeTestContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_nodeTest);
		try {
			setState(100);
			switch (_input.LA(1)) {
			case AxisName:
			case MUL:
			case NCName:
				enterOuterAlt(_localctx, 1);
				{
				setState(92);
				nameTest();
				}
				break;
			case NodeType:
				enterOuterAlt(_localctx, 2);
				{
				setState(93);
				match(NodeType);
				setState(94);
				match(LPAR);
				setState(95);
				match(RPAR);
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 3);
				{
				setState(96);
				match(T__0);
				setState(97);
				match(LPAR);
				setState(98);
				match(Literal);
				setState(99);
				match(RPAR);
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

	public static class PredicateContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public PredicateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_predicate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterPredicate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitPredicate(this);
		}
	}

	public final PredicateContext predicate() throws RecognitionException {
		PredicateContext _localctx = new PredicateContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_predicate);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			match(LBRAC);
			setState(103);
			expr();
			setState(104);
			match(RBRAC);
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

	public static class AbbreviatedStepContext extends ParserRuleContext {
		public AbbreviatedStepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_abbreviatedStep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterAbbreviatedStep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitAbbreviatedStep(this);
		}
	}

	public final AbbreviatedStepContext abbreviatedStep() throws RecognitionException {
		AbbreviatedStepContext _localctx = new AbbreviatedStepContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_abbreviatedStep);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
			_la = _input.LA(1);
			if ( !(_la==DOT || _la==DOTDOT) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
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

	public static class ExprContext extends ParserRuleContext {
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitExpr(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108);
			orExpr();
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

	public static class PrimaryExprContext extends ParserRuleContext {
		public VariableReferenceContext variableReference() {
			return getRuleContext(VariableReferenceContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode Literal() { return getToken(xpathParser.Literal, 0); }
		public TerminalNode Number() { return getToken(xpathParser.Number, 0); }
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public PrimaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterPrimaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitPrimaryExpr(this);
		}
	}

	public final PrimaryExprContext primaryExpr() throws RecognitionException {
		PrimaryExprContext _localctx = new PrimaryExprContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_primaryExpr);
		try {
			setState(118);
			switch (_input.LA(1)) {
			case T__7:
				enterOuterAlt(_localctx, 1);
				{
				setState(110);
				variableReference();
				}
				break;
			case LPAR:
				enterOuterAlt(_localctx, 2);
				{
				setState(111);
				match(LPAR);
				setState(112);
				expr();
				setState(113);
				match(RPAR);
				}
				break;
			case Literal:
				enterOuterAlt(_localctx, 3);
				{
				setState(115);
				match(Literal);
				}
				break;
			case Number:
				enterOuterAlt(_localctx, 4);
				{
				setState(116);
				match(Number);
				}
				break;
			case AxisName:
			case NCName:
				enterOuterAlt(_localctx, 5);
				{
				setState(117);
				functionCall();
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

	public static class FunctionCallContext extends ParserRuleContext {
		public FunctionNameContext functionName() {
			return getRuleContext(FunctionNameContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitFunctionCall(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_functionCall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			functionName();
			setState(121);
			match(LPAR);
			setState(130);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__7) | (1L << NodeType) | (1L << Number) | (1L << AxisName) | (1L << PATHSEP) | (1L << ABRPATH) | (1L << LPAR) | (1L << MINUS) | (1L << DOT) | (1L << MUL) | (1L << DOTDOT) | (1L << AT) | (1L << Literal) | (1L << NCName))) != 0)) {
				{
				setState(122);
				expr();
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(123);
					match(COMMA);
					setState(124);
					expr();
					}
					}
					setState(129);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(132);
			match(RPAR);
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

	public static class UnionExprNoRootContext extends ParserRuleContext {
		public PathExprNoRootContext pathExprNoRoot() {
			return getRuleContext(PathExprNoRootContext.class,0);
		}
		public UnionExprNoRootContext unionExprNoRoot() {
			return getRuleContext(UnionExprNoRootContext.class,0);
		}
		public UnionExprNoRootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unionExprNoRoot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterUnionExprNoRoot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitUnionExprNoRoot(this);
		}
	}

	public final UnionExprNoRootContext unionExprNoRoot() throws RecognitionException {
		UnionExprNoRootContext _localctx = new UnionExprNoRootContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_unionExprNoRoot);
		int _la;
		try {
			setState(142);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(134);
				pathExprNoRoot();
				setState(137);
				_la = _input.LA(1);
				if (_la==PIPE) {
					{
					setState(135);
					match(PIPE);
					setState(136);
					unionExprNoRoot();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(139);
				match(PATHSEP);
				setState(140);
				match(PIPE);
				setState(141);
				unionExprNoRoot();
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

	public static class PathExprNoRootContext extends ParserRuleContext {
		public LocationPathContext locationPath() {
			return getRuleContext(LocationPathContext.class,0);
		}
		public FilterExprContext filterExpr() {
			return getRuleContext(FilterExprContext.class,0);
		}
		public RelativeLocationPathContext relativeLocationPath() {
			return getRuleContext(RelativeLocationPathContext.class,0);
		}
		public PathExprNoRootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pathExprNoRoot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterPathExprNoRoot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitPathExprNoRoot(this);
		}
	}

	public final PathExprNoRootContext pathExprNoRoot() throws RecognitionException {
		PathExprNoRootContext _localctx = new PathExprNoRootContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_pathExprNoRoot);
		int _la;
		try {
			setState(150);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(144);
				locationPath();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(145);
				filterExpr();
				setState(148);
				_la = _input.LA(1);
				if (_la==PATHSEP || _la==ABRPATH) {
					{
					setState(146);
					_la = _input.LA(1);
					if ( !(_la==PATHSEP || _la==ABRPATH) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					setState(147);
					relativeLocationPath();
					}
				}

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

	public static class FilterExprContext extends ParserRuleContext {
		public PrimaryExprContext primaryExpr() {
			return getRuleContext(PrimaryExprContext.class,0);
		}
		public List<PredicateContext> predicate() {
			return getRuleContexts(PredicateContext.class);
		}
		public PredicateContext predicate(int i) {
			return getRuleContext(PredicateContext.class,i);
		}
		public FilterExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_filterExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterFilterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitFilterExpr(this);
		}
	}

	public final FilterExprContext filterExpr() throws RecognitionException {
		FilterExprContext _localctx = new FilterExprContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_filterExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(152);
			primaryExpr();
			setState(156);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRAC) {
				{
				{
				setState(153);
				predicate();
				}
				}
				setState(158);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class OrExprContext extends ParserRuleContext {
		public List<AndExprContext> andExpr() {
			return getRuleContexts(AndExprContext.class);
		}
		public AndExprContext andExpr(int i) {
			return getRuleContext(AndExprContext.class,i);
		}
		public OrExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterOrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitOrExpr(this);
		}
	}

	public final OrExprContext orExpr() throws RecognitionException {
		OrExprContext _localctx = new OrExprContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_orExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			andExpr();
			setState(164);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(160);
				match(T__1);
				setState(161);
				andExpr();
				}
				}
				setState(166);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class AndExprContext extends ParserRuleContext {
		public List<EqualityExprContext> equalityExpr() {
			return getRuleContexts(EqualityExprContext.class);
		}
		public EqualityExprContext equalityExpr(int i) {
			return getRuleContext(EqualityExprContext.class,i);
		}
		public AndExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_andExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterAndExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitAndExpr(this);
		}
	}

	public final AndExprContext andExpr() throws RecognitionException {
		AndExprContext _localctx = new AndExprContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_andExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			equalityExpr();
			setState(172);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(168);
				match(T__2);
				setState(169);
				equalityExpr();
				}
				}
				setState(174);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class EqualityExprContext extends ParserRuleContext {
		public List<RelationalExprContext> relationalExpr() {
			return getRuleContexts(RelationalExprContext.class);
		}
		public RelationalExprContext relationalExpr(int i) {
			return getRuleContext(RelationalExprContext.class,i);
		}
		public EqualityExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_equalityExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterEqualityExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitEqualityExpr(this);
		}
	}

	public final EqualityExprContext equalityExpr() throws RecognitionException {
		EqualityExprContext _localctx = new EqualityExprContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_equalityExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(175);
			relationalExpr();
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3 || _la==T__4) {
				{
				{
				setState(176);
				_la = _input.LA(1);
				if ( !(_la==T__3 || _la==T__4) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(177);
				relationalExpr();
				}
				}
				setState(182);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class RelationalExprContext extends ParserRuleContext {
		public List<AdditiveExprContext> additiveExpr() {
			return getRuleContexts(AdditiveExprContext.class);
		}
		public AdditiveExprContext additiveExpr(int i) {
			return getRuleContext(AdditiveExprContext.class,i);
		}
		public RelationalExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationalExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterRelationalExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitRelationalExpr(this);
		}
	}

	public final RelationalExprContext relationalExpr() throws RecognitionException {
		RelationalExprContext _localctx = new RelationalExprContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_relationalExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			additiveExpr();
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LESS) | (1L << MORE_) | (1L << LE) | (1L << GE))) != 0)) {
				{
				{
				setState(184);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LESS) | (1L << MORE_) | (1L << LE) | (1L << GE))) != 0)) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(185);
				additiveExpr();
				}
				}
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class AdditiveExprContext extends ParserRuleContext {
		public List<MultiplicativeExprContext> multiplicativeExpr() {
			return getRuleContexts(MultiplicativeExprContext.class);
		}
		public MultiplicativeExprContext multiplicativeExpr(int i) {
			return getRuleContext(MultiplicativeExprContext.class,i);
		}
		public AdditiveExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_additiveExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterAdditiveExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitAdditiveExpr(this);
		}
	}

	public final AdditiveExprContext additiveExpr() throws RecognitionException {
		AdditiveExprContext _localctx = new AdditiveExprContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_additiveExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			multiplicativeExpr();
			setState(196);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==MINUS || _la==PLUS) {
				{
				{
				setState(192);
				_la = _input.LA(1);
				if ( !(_la==MINUS || _la==PLUS) ) {
				_errHandler.recoverInline(this);
				} else {
					consume();
				}
				setState(193);
				multiplicativeExpr();
				}
				}
				setState(198);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
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

	public static class MultiplicativeExprContext extends ParserRuleContext {
		public UnaryExprNoRootContext unaryExprNoRoot() {
			return getRuleContext(UnaryExprNoRootContext.class,0);
		}
		public MultiplicativeExprContext multiplicativeExpr() {
			return getRuleContext(MultiplicativeExprContext.class,0);
		}
		public MultiplicativeExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multiplicativeExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterMultiplicativeExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitMultiplicativeExpr(this);
		}
	}

	public final MultiplicativeExprContext multiplicativeExpr() throws RecognitionException {
		MultiplicativeExprContext _localctx = new MultiplicativeExprContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_multiplicativeExpr);
		int _la;
		try {
			setState(209);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(199);
				unaryExprNoRoot();
				setState(202);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__6) | (1L << MUL))) != 0)) {
					{
					setState(200);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__6) | (1L << MUL))) != 0)) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					setState(201);
					multiplicativeExpr();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(204);
				match(PATHSEP);
				setState(207);
				_la = _input.LA(1);
				if (_la==T__5 || _la==T__6) {
					{
					setState(205);
					_la = _input.LA(1);
					if ( !(_la==T__5 || _la==T__6) ) {
					_errHandler.recoverInline(this);
					} else {
						consume();
					}
					setState(206);
					multiplicativeExpr();
					}
				}

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

	public static class UnaryExprNoRootContext extends ParserRuleContext {
		public UnionExprNoRootContext unionExprNoRoot() {
			return getRuleContext(UnionExprNoRootContext.class,0);
		}
		public UnaryExprNoRootContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExprNoRoot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterUnaryExprNoRoot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitUnaryExprNoRoot(this);
		}
	}

	public final UnaryExprNoRootContext unaryExprNoRoot() throws RecognitionException {
		UnaryExprNoRootContext _localctx = new UnaryExprNoRootContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_unaryExprNoRoot);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==MINUS) {
				{
				{
				setState(211);
				match(MINUS);
				}
				}
				setState(216);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(217);
			unionExprNoRoot();
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

	public static class QNameContext extends ParserRuleContext {
		public List<NCNameContext> nCName() {
			return getRuleContexts(NCNameContext.class);
		}
		public NCNameContext nCName(int i) {
			return getRuleContext(NCNameContext.class,i);
		}
		public QNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterQName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitQName(this);
		}
	}

	public final QNameContext qName() throws RecognitionException {
		QNameContext _localctx = new QNameContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_qName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			nCName();
			setState(222);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(220);
				match(COLON);
				setState(221);
				nCName();
				}
			}

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

	public static class FunctionNameContext extends ParserRuleContext {
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public FunctionNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterFunctionName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitFunctionName(this);
		}
	}

	public final FunctionNameContext functionName() throws RecognitionException {
		FunctionNameContext _localctx = new FunctionNameContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_functionName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			qName();
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

	public static class VariableReferenceContext extends ParserRuleContext {
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public VariableReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableReference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterVariableReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitVariableReference(this);
		}
	}

	public final VariableReferenceContext variableReference() throws RecognitionException {
		VariableReferenceContext _localctx = new VariableReferenceContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_variableReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
			match(T__7);
			setState(227);
			qName();
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

	public static class NameTestContext extends ParserRuleContext {
		public NCNameContext nCName() {
			return getRuleContext(NCNameContext.class,0);
		}
		public QNameContext qName() {
			return getRuleContext(QNameContext.class,0);
		}
		public NameTestContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nameTest; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterNameTest(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitNameTest(this);
		}
	}

	public final NameTestContext nameTest() throws RecognitionException {
		NameTestContext _localctx = new NameTestContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_nameTest);
		try {
			setState(235);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(229);
				match(MUL);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(230);
				nCName();
				setState(231);
				match(COLON);
				setState(232);
				match(MUL);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(234);
				qName();
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

	public static class NCNameContext extends ParserRuleContext {
		public TerminalNode NCName() { return getToken(xpathParser.NCName, 0); }
		public TerminalNode AxisName() { return getToken(xpathParser.AxisName, 0); }
		public NCNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nCName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).enterNCName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof xpathListener ) ((xpathListener)listener).exitNCName(this);
		}
	}

	public final NCNameContext nCName() throws RecognitionException {
		NCNameContext _localctx = new NCNameContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_nCName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(237);
			_la = _input.LA(1);
			if ( !(_la==AxisName || _la==NCName) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3&\u00f2\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\3\3\3\5\3=\n\3\3\4\3\4\3\4\3"+
		"\4\5\4C\n\4\3\5\3\5\3\5\7\5H\n\5\f\5\16\5K\13\5\3\6\3\6\3\6\7\6P\n\6\f"+
		"\6\16\6S\13\6\3\6\5\6V\n\6\3\7\3\7\3\7\5\7[\n\7\5\7]\n\7\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\b\3\b\5\bg\n\b\3\t\3\t\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\5\fy\n\f\3\r\3\r\3\r\3\r\3\r\7\r\u0080\n\r\f"+
		"\r\16\r\u0083\13\r\5\r\u0085\n\r\3\r\3\r\3\16\3\16\3\16\5\16\u008c\n\16"+
		"\3\16\3\16\3\16\5\16\u0091\n\16\3\17\3\17\3\17\3\17\5\17\u0097\n\17\5"+
		"\17\u0099\n\17\3\20\3\20\7\20\u009d\n\20\f\20\16\20\u00a0\13\20\3\21\3"+
		"\21\3\21\7\21\u00a5\n\21\f\21\16\21\u00a8\13\21\3\22\3\22\3\22\7\22\u00ad"+
		"\n\22\f\22\16\22\u00b0\13\22\3\23\3\23\3\23\7\23\u00b5\n\23\f\23\16\23"+
		"\u00b8\13\23\3\24\3\24\3\24\7\24\u00bd\n\24\f\24\16\24\u00c0\13\24\3\25"+
		"\3\25\3\25\7\25\u00c5\n\25\f\25\16\25\u00c8\13\25\3\26\3\26\3\26\5\26"+
		"\u00cd\n\26\3\26\3\26\3\26\5\26\u00d2\n\26\5\26\u00d4\n\26\3\27\7\27\u00d7"+
		"\n\27\f\27\16\27\u00da\13\27\3\27\3\27\3\30\3\30\3\30\5\30\u00e1\n\30"+
		"\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u00ee\n\33"+
		"\3\34\3\34\3\34\2\2\35\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,"+
		".\60\62\64\66\2\n\3\2\16\17\4\2\26\26\30\30\3\2\6\7\3\2\34\37\3\2\24\25"+
		"\4\2\b\t\27\27\3\2\b\t\4\2\r\r&&\u00f6\28\3\2\2\2\4<\3\2\2\2\6B\3\2\2"+
		"\2\bD\3\2\2\2\nU\3\2\2\2\f\\\3\2\2\2\16f\3\2\2\2\20h\3\2\2\2\22l\3\2\2"+
		"\2\24n\3\2\2\2\26x\3\2\2\2\30z\3\2\2\2\32\u0090\3\2\2\2\34\u0098\3\2\2"+
		"\2\36\u009a\3\2\2\2 \u00a1\3\2\2\2\"\u00a9\3\2\2\2$\u00b1\3\2\2\2&\u00b9"+
		"\3\2\2\2(\u00c1\3\2\2\2*\u00d3\3\2\2\2,\u00d8\3\2\2\2.\u00dd\3\2\2\2\60"+
		"\u00e2\3\2\2\2\62\u00e4\3\2\2\2\64\u00ed\3\2\2\2\66\u00ef\3\2\2\289\5"+
		"\24\13\29\3\3\2\2\2:=\5\b\5\2;=\5\6\4\2<:\3\2\2\2<;\3\2\2\2=\5\3\2\2\2"+
		">?\7\16\2\2?C\5\b\5\2@A\7\17\2\2AC\5\b\5\2B>\3\2\2\2B@\3\2\2\2C\7\3\2"+
		"\2\2DI\5\n\6\2EF\t\2\2\2FH\5\n\6\2GE\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2"+
		"\2\2J\t\3\2\2\2KI\3\2\2\2LM\5\f\7\2MQ\5\16\b\2NP\5\20\t\2ON\3\2\2\2PS"+
		"\3\2\2\2QO\3\2\2\2QR\3\2\2\2RV\3\2\2\2SQ\3\2\2\2TV\5\22\n\2UL\3\2\2\2"+
		"UT\3\2\2\2V\13\3\2\2\2WX\7\r\2\2X]\7!\2\2Y[\7\31\2\2ZY\3\2\2\2Z[\3\2\2"+
		"\2[]\3\2\2\2\\W\3\2\2\2\\Z\3\2\2\2]\r\3\2\2\2^g\5\64\33\2_`\7\13\2\2`"+
		"a\7\20\2\2ag\7\21\2\2bc\7\3\2\2cd\7\20\2\2de\7$\2\2eg\7\21\2\2f^\3\2\2"+
		"\2f_\3\2\2\2fb\3\2\2\2g\17\3\2\2\2hi\7\22\2\2ij\5\24\13\2jk\7\23\2\2k"+
		"\21\3\2\2\2lm\t\3\2\2m\23\3\2\2\2no\5 \21\2o\25\3\2\2\2py\5\62\32\2qr"+
		"\7\20\2\2rs\5\24\13\2st\7\21\2\2ty\3\2\2\2uy\7$\2\2vy\7\f\2\2wy\5\30\r"+
		"\2xp\3\2\2\2xq\3\2\2\2xu\3\2\2\2xv\3\2\2\2xw\3\2\2\2y\27\3\2\2\2z{\5\60"+
		"\31\2{\u0084\7\20\2\2|\u0081\5\24\13\2}~\7\32\2\2~\u0080\5\24\13\2\177"+
		"}\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082"+
		"\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0084|\3\2\2\2\u0084\u0085\3\2\2\2"+
		"\u0085\u0086\3\2\2\2\u0086\u0087\7\21\2\2\u0087\31\3\2\2\2\u0088\u008b"+
		"\5\34\17\2\u0089\u008a\7\33\2\2\u008a\u008c\5\32\16\2\u008b\u0089\3\2"+
		"\2\2\u008b\u008c\3\2\2\2\u008c\u0091\3\2\2\2\u008d\u008e\7\16\2\2\u008e"+
		"\u008f\7\33\2\2\u008f\u0091\5\32\16\2\u0090\u0088\3\2\2\2\u0090\u008d"+
		"\3\2\2\2\u0091\33\3\2\2\2\u0092\u0099\5\4\3\2\u0093\u0096\5\36\20\2\u0094"+
		"\u0095\t\2\2\2\u0095\u0097\5\b\5\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2"+
		"\2\2\u0097\u0099\3\2\2\2\u0098\u0092\3\2\2\2\u0098\u0093\3\2\2\2\u0099"+
		"\35\3\2\2\2\u009a\u009e\5\26\f\2\u009b\u009d\5\20\t\2\u009c\u009b\3\2"+
		"\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f"+
		"\37\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\u00a6\5\"\22\2\u00a2\u00a3\7\4\2"+
		"\2\u00a3\u00a5\5\"\22\2\u00a4\u00a2\3\2\2\2\u00a5\u00a8\3\2\2\2\u00a6"+
		"\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7!\3\2\2\2\u00a8\u00a6\3\2\2\2"+
		"\u00a9\u00ae\5$\23\2\u00aa\u00ab\7\5\2\2\u00ab\u00ad\5$\23\2\u00ac\u00aa"+
		"\3\2\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af"+
		"#\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u00b6\5&\24\2\u00b2\u00b3\t\4\2\2"+
		"\u00b3\u00b5\5&\24\2\u00b4\u00b2\3\2\2\2\u00b5\u00b8\3\2\2\2\u00b6\u00b4"+
		"\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7%\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b9"+
		"\u00be\5(\25\2\u00ba\u00bb\t\5\2\2\u00bb\u00bd\5(\25\2\u00bc\u00ba\3\2"+
		"\2\2\u00bd\u00c0\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf"+
		"\'\3\2\2\2\u00c0\u00be\3\2\2\2\u00c1\u00c6\5*\26\2\u00c2\u00c3\t\6\2\2"+
		"\u00c3\u00c5\5*\26\2\u00c4\u00c2\3\2\2\2\u00c5\u00c8\3\2\2\2\u00c6\u00c4"+
		"\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7)\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c9"+
		"\u00cc\5,\27\2\u00ca\u00cb\t\7\2\2\u00cb\u00cd\5*\26\2\u00cc\u00ca\3\2"+
		"\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00d4\3\2\2\2\u00ce\u00d1\7\16\2\2\u00cf"+
		"\u00d0\t\b\2\2\u00d0\u00d2\5*\26\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2"+
		"\2\2\u00d2\u00d4\3\2\2\2\u00d3\u00c9\3\2\2\2\u00d3\u00ce\3\2\2\2\u00d4"+
		"+\3\2\2\2\u00d5\u00d7\7\24\2\2\u00d6\u00d5\3\2\2\2\u00d7\u00da\3\2\2\2"+
		"\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2\2\2\u00d9\u00db\3\2\2\2\u00da\u00d8"+
		"\3\2\2\2\u00db\u00dc\5\32\16\2\u00dc-\3\2\2\2\u00dd\u00e0\5\66\34\2\u00de"+
		"\u00df\7 \2\2\u00df\u00e1\5\66\34\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3"+
		"\2\2\2\u00e1/\3\2\2\2\u00e2\u00e3\5.\30\2\u00e3\61\3\2\2\2\u00e4\u00e5"+
		"\7\n\2\2\u00e5\u00e6\5.\30\2\u00e6\63\3\2\2\2\u00e7\u00ee\7\27\2\2\u00e8"+
		"\u00e9\5\66\34\2\u00e9\u00ea\7 \2\2\u00ea\u00eb\7\27\2\2\u00eb\u00ee\3"+
		"\2\2\2\u00ec\u00ee\5.\30\2\u00ed\u00e7\3\2\2\2\u00ed\u00e8\3\2\2\2\u00ed"+
		"\u00ec\3\2\2\2\u00ee\65\3\2\2\2\u00ef\u00f0\t\t\2\2\u00f0\67\3\2\2\2\35"+
		"<BIQUZ\\fx\u0081\u0084\u008b\u0090\u0096\u0098\u009e\u00a6\u00ae\u00b6"+
		"\u00be\u00c6\u00cc\u00d1\u00d3\u00d8\u00e0\u00ed";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}