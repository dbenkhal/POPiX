package Statement;

import java.nio.*;
import java.util.Map;
import Expression.AbstractClass;
import Expression.Expression;

public abstract class AbstractClassS implements StateExpression {

	AbstractClass left;
	AbstractClass right;
	public int lab;
	Expression exp;
	String id;
	public static int nbTab = 0;
	public static String require_var_name;

	public String spaces(int spaces) {
		return CharBuffer.allocate(spaces).toString().replace('\0', ' ');
	}

	public AbstractClassS(Expression e, String i) {
		exp = e;
		id = i;

	}

	public Expression getLeft() {
		return left;

	}

	public Expression getRight() {
		return right;

	}

	public AbstractClassS() {
	}

	public abstract String symbol();

	public Expression getexp() {
		return exp;

	}

	public String getid() {
		return id;

	}

	public String toString(int i) {
		return getid().toString() + symbol() + exp.toString();

	}

	public String toStringLab(int i) {
		return getid().toString() + symbol() + "|" + lab + "|" + exp.toString() + "|" + lab + "|";

	}

	public String toStringRes(int i, Map<String, Integer> env) {
		return getid().toString() + symbol() + "|" + lab + "|" + exp.toString() + "|" + lab + "|";

	}

	public abstract String toStringOriginal(int tab);

	public abstract String toStringPython(int tab, Map<String, Integer> env);

	public String ntab(int n) {
		String s = "";
		for (int i = 0; i < n; i++) {
			s = s + '\t';
		}
		return s;
	};

}
