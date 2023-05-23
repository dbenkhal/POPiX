package Boolean;

import java.util.Map;
import Expression.AbstractClass;
import Util.Environment;

public class LessEqual extends AbstractClassB {
	AbstractClass left;
	AbstractClass right;

	public LessEqual(AbstractClass lt, AbstractClass rt) {
		left = lt;
		right = rt;
		lab = AbstractClass.newLab();
	}

	public Boolean evaluate(Environment env) {
		return left.evaluate(env).LESSEQUAL(right.evaluate(env));
	}

	public String symbol() {
		return "<=";
	}

	public String toString() {
		return left.toString() + symbol() + right.toString();
	}

	public String toStringLab() {
		return left.toStringLab() + "|" + lab + "|" + symbol() + "|" + lab + "|" + right.toStringLab() + "|" + lab
				+ "|";
	}

	public String toStringRes(Map<String, Integer> env) {
		return left.toStringRes(env) + symbol() + right.toString();
	}

	public String toStringFix(Map<String, Integer> env) {
		return left.toStringFix(env) + symbol() + right.toStringFix(env);
	}

	public Map<String, String> Generate_constraints(Map<String, String> varEnv) {
		left.Generate_constraints(varEnv);
		right.Generate_constraints(varEnv);

		return varEnv;
	}

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv) {
		left.Generate_constraints_Backward(varEnv);
		right.Generate_constraints_Backward(varEnv);
		return varEnv;

	}

	public String toStringPython(Map<String, Integer> env) {
		return left.toStringPython(env) + symbol() + right.toStringPython(env);

	}

	public String toStringOriginal() {
		return left.toStringOriginal() + symbol() + right.toStringOriginal();

	}

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv) {
		left.Generate_Constraints_GLPK(varEnv);
		right.Generate_Constraints_GLPK(varEnv);
		return varEnv;

	}

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		left.Generate_Constraints_GLPK_Prime(varEnv);
		right.Generate_Constraints_GLPK_Prime(varEnv);
		return varEnv;

	}

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv) {
		left.Generate_Constraints_AC(varEnv);
		right.Generate_Constraints_AC(varEnv);
		return varEnv;
	}

}
