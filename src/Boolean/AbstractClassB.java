package Boolean;

import java.util.Map;
import Expression.Expression;

public abstract class AbstractClassB implements BoolExpression {

	public Expression left, right;
	public int lab;

	public AbstractClassB(Expression lt, Expression rt) {
		left = lt;
		right = rt;
	}

	public AbstractClassB() {
		super();
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	public abstract String symbol();

	public String toString() {
		return getLeft() + symbol() + getRight();
	}

	public String toStringLab() {
		return getLeft() + "| lab" + lab + "|" + symbol() + "| lab" + lab + "|" + getRight() + "| lab" + lab + "|";
	}

	public String toStringRes(Map<String, Integer> env) {
		return left.toStringRes(env) + symbol() + right.toString();
	}

	public abstract String toStringPython(Map<String, Integer> env);

	public abstract String toStringOriginal();
}