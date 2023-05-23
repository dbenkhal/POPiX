// Generated from Grammaire.g4 by ANTLR 4.7.2

import Expression.*;
import Statement.*;
import Boolean.*;
import Util.*;
import Number.*;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GrammaireLexer extends Lexer {
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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"INTERVALLE", "CONSTF", "CONSTI", "RANGERANDOMFLOAT", "REQUIREACCURACY", 
			"DIV", "MULT", "MINUS", "PLUS", "VIRG", "TRUE", "FALSE", "GT", "GE", 
			"LT", "LE", "EQ", "NEQ", "NOT", "PV", "ASSIGN", "IF", "ELSE", "WHILE", 
			"FOR", "LPAREN", "RPAREN", "LBRACKET", "INF", "SUP", "RBRACKET", "NEWLINE", 
			"END", "WS", "COMMENT", "LINE_COMMENT", "IDENTIFIER"
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

	public GrammaireLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Grammaire.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\'\u0103\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\3\2\6\2O\n\2\r\2\16\2P\3\2\3\2\6\2"+
		"U\n\2\r\2\16\2V\3\2\3\2\6\2[\n\2\r\2\16\2\\\3\2\3\2\6\2a\n\2\r\2\16\2"+
		"b\3\3\6\3f\n\3\r\3\16\3g\3\3\3\3\6\3l\n\3\r\3\16\3m\3\4\6\4q\n\4\r\4\16"+
		"\4r\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\f\3"+
		"\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\21\3\21"+
		"\3\21\3\22\3\22\3\22\3\23\3\23\3\23\3\23\5\23\u00ae\n\23\3\24\3\24\3\25"+
		"\3\25\3\26\3\26\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36"+
		"\3\36\3\37\3\37\3 \3 \3!\6!\u00d5\n!\r!\16!\u00d6\3!\3!\3\"\3\"\3#\6#"+
		"\u00de\n#\r#\16#\u00df\3#\3#\3$\3$\3$\3$\7$\u00e8\n$\f$\16$\u00eb\13$"+
		"\3$\3$\3$\3$\3$\3%\3%\3%\3%\7%\u00f6\n%\f%\16%\u00f9\13%\3%\3%\3&\3&\7"+
		"&\u00ff\n&\f&\16&\u0102\13&\3\u00e9\2\'\3\3\5\4\7\5\t\6\13\7\r\b\17\t"+
		"\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27"+
		"-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'\3\2\6\4\2\f"+
		"\f\17\17\4\2\13\13\"\"\4\2C\\c|\5\2\62;C\\c|\2\u010f\2\3\3\2\2\2\2\5\3"+
		"\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2"+
		"\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3"+
		"\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'"+
		"\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63"+
		"\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2"+
		"?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3"+
		"\2\2\2\3N\3\2\2\2\5e\3\2\2\2\7p\3\2\2\2\tt\3\2\2\2\13v\3\2\2\2\r\u0087"+
		"\3\2\2\2\17\u0089\3\2\2\2\21\u008b\3\2\2\2\23\u008d\3\2\2\2\25\u008f\3"+
		"\2\2\2\27\u0091\3\2\2\2\31\u0096\3\2\2\2\33\u009c\3\2\2\2\35\u009e\3\2"+
		"\2\2\37\u00a1\3\2\2\2!\u00a3\3\2\2\2#\u00a6\3\2\2\2%\u00ad\3\2\2\2\'\u00af"+
		"\3\2\2\2)\u00b1\3\2\2\2+\u00b3\3\2\2\2-\u00b5\3\2\2\2/\u00b8\3\2\2\2\61"+
		"\u00bd\3\2\2\2\63\u00c3\3\2\2\2\65\u00c7\3\2\2\2\67\u00c9\3\2\2\29\u00cb"+
		"\3\2\2\2;\u00cd\3\2\2\2=\u00cf\3\2\2\2?\u00d1\3\2\2\2A\u00d4\3\2\2\2C"+
		"\u00da\3\2\2\2E\u00dd\3\2\2\2G\u00e3\3\2\2\2I\u00f1\3\2\2\2K\u00fc\3\2"+
		"\2\2MO\4\62;\2NM\3\2\2\2OP\3\2\2\2PN\3\2\2\2PQ\3\2\2\2QR\3\2\2\2RT\7\60"+
		"\2\2SU\4\62;\2TS\3\2\2\2UV\3\2\2\2VT\3\2\2\2VW\3\2\2\2WX\3\2\2\2XZ\7."+
		"\2\2Y[\4\62;\2ZY\3\2\2\2[\\\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2]^\3\2\2\2^`"+
		"\7\60\2\2_a\4\62;\2`_\3\2\2\2ab\3\2\2\2b`\3\2\2\2bc\3\2\2\2c\4\3\2\2\2"+
		"df\4\62;\2ed\3\2\2\2fg\3\2\2\2ge\3\2\2\2gh\3\2\2\2hi\3\2\2\2ik\7\60\2"+
		"\2jl\4\62;\2kj\3\2\2\2lm\3\2\2\2mk\3\2\2\2mn\3\2\2\2n\6\3\2\2\2oq\4\62"+
		";\2po\3\2\2\2qr\3\2\2\2rp\3\2\2\2rs\3\2\2\2s\b\3\2\2\2tu\4\62;\2u\n\3"+
		"\2\2\2vw\7t\2\2wx\7g\2\2xy\7s\2\2yz\7w\2\2z{\7k\2\2{|\7t\2\2|}\7g\2\2"+
		"}~\7a\2\2~\177\7c\2\2\177\u0080\7e\2\2\u0080\u0081\7e\2\2\u0081\u0082"+
		"\7w\2\2\u0082\u0083\7t\2\2\u0083\u0084\7c\2\2\u0084\u0085\7e\2\2\u0085"+
		"\u0086\7{\2\2\u0086\f\3\2\2\2\u0087\u0088\7\61\2\2\u0088\16\3\2\2\2\u0089"+
		"\u008a\7,\2\2\u008a\20\3\2\2\2\u008b\u008c\7/\2\2\u008c\22\3\2\2\2\u008d"+
		"\u008e\7-\2\2\u008e\24\3\2\2\2\u008f\u0090\7.\2\2\u0090\26\3\2\2\2\u0091"+
		"\u0092\7v\2\2\u0092\u0093\7t\2\2\u0093\u0094\7w\2\2\u0094\u0095\7g\2\2"+
		"\u0095\30\3\2\2\2\u0096\u0097\7h\2\2\u0097\u0098\7c\2\2\u0098\u0099\7"+
		"n\2\2\u0099\u009a\7u\2\2\u009a\u009b\7g\2\2\u009b\32\3\2\2\2\u009c\u009d"+
		"\7@\2\2\u009d\34\3\2\2\2\u009e\u009f\7@\2\2\u009f\u00a0\7?\2\2\u00a0\36"+
		"\3\2\2\2\u00a1\u00a2\7>\2\2\u00a2 \3\2\2\2\u00a3\u00a4\7>\2\2\u00a4\u00a5"+
		"\7?\2\2\u00a5\"\3\2\2\2\u00a6\u00a7\7?\2\2\u00a7\u00a8\7?\2\2\u00a8$\3"+
		"\2\2\2\u00a9\u00aa\7#\2\2\u00aa\u00ae\7?\2\2\u00ab\u00ac\7>\2\2\u00ac"+
		"\u00ae\7@\2\2\u00ad\u00a9\3\2\2\2\u00ad\u00ab\3\2\2\2\u00ae&\3\2\2\2\u00af"+
		"\u00b0\7#\2\2\u00b0(\3\2\2\2\u00b1\u00b2\7=\2\2\u00b2*\3\2\2\2\u00b3\u00b4"+
		"\7?\2\2\u00b4,\3\2\2\2\u00b5\u00b6\7k\2\2\u00b6\u00b7\7h\2\2\u00b7.\3"+
		"\2\2\2\u00b8\u00b9\7g\2\2\u00b9\u00ba\7n\2\2\u00ba\u00bb\7u\2\2\u00bb"+
		"\u00bc\7g\2\2\u00bc\60\3\2\2\2\u00bd\u00be\7y\2\2\u00be\u00bf\7j\2\2\u00bf"+
		"\u00c0\7k\2\2\u00c0\u00c1\7n\2\2\u00c1\u00c2\7g\2\2\u00c2\62\3\2\2\2\u00c3"+
		"\u00c4\7h\2\2\u00c4\u00c5\7q\2\2\u00c5\u00c6\7t\2\2\u00c6\64\3\2\2\2\u00c7"+
		"\u00c8\7*\2\2\u00c8\66\3\2\2\2\u00c9\u00ca\7+\2\2\u00ca8\3\2\2\2\u00cb"+
		"\u00cc\7}\2\2\u00cc:\3\2\2\2\u00cd\u00ce\7]\2\2\u00ce<\3\2\2\2\u00cf\u00d0"+
		"\7_\2\2\u00d0>\3\2\2\2\u00d1\u00d2\7\177\2\2\u00d2@\3\2\2\2\u00d3\u00d5"+
		"\t\2\2\2\u00d4\u00d3\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d6"+
		"\u00d7\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d9\b!\2\2\u00d9B\3\2\2\2\u00da"+
		"\u00db\7\2\2\3\u00dbD\3\2\2\2\u00dc\u00de\t\3\2\2\u00dd\u00dc\3\2\2\2"+
		"\u00de\u00df\3\2\2\2\u00df\u00dd\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0\u00e1"+
		"\3\2\2\2\u00e1\u00e2\b#\2\2\u00e2F\3\2\2\2\u00e3\u00e4\7\61\2\2\u00e4"+
		"\u00e5\7,\2\2\u00e5\u00e9\3\2\2\2\u00e6\u00e8\13\2\2\2\u00e7\u00e6\3\2"+
		"\2\2\u00e8\u00eb\3\2\2\2\u00e9\u00ea\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea"+
		"\u00ec\3\2\2\2\u00eb\u00e9\3\2\2\2\u00ec\u00ed\7,\2\2\u00ed\u00ee\7\61"+
		"\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00f0\b$\2\2\u00f0H\3\2\2\2\u00f1\u00f2"+
		"\7\61\2\2\u00f2\u00f3\7\61\2\2\u00f3\u00f7\3\2\2\2\u00f4\u00f6\n\2\2\2"+
		"\u00f5\u00f4\3\2\2\2\u00f6\u00f9\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f7\u00f8"+
		"\3\2\2\2\u00f8\u00fa\3\2\2\2\u00f9\u00f7\3\2\2\2\u00fa\u00fb\b%\2\2\u00fb"+
		"J\3\2\2\2\u00fc\u0100\t\4\2\2\u00fd\u00ff\t\5\2\2\u00fe\u00fd\3\2\2\2"+
		"\u00ff\u0102\3\2\2\2\u0100\u00fe\3\2\2\2\u0100\u0101\3\2\2\2\u0101L\3"+
		"\2\2\2\u0102\u0100\3\2\2\2\20\2PV\\bgmr\u00ad\u00d6\u00df\u00e9\u00f7"+
		"\u0100\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}