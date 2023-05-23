package Statement;

import java.util.Map;
import Boolean.*;
import Expression.*;
import Number.IntNumbers;
import Util.Environment;

public class For extends AbstractClassS {

	public String id;
	AbstractClass beg, end;
	StatementList SL;

	public For(AbstractClass begin, AbstractClass e, StatementList sL) {
		beg = begin;
		end = e;
		SL = sL;
		lab = AbstractClass.newLab();
	}

	public Environment evaluate(Environment env) {
		AbstractClassS A = new Assign(beg, id);
		AbstractClassS I = new Assign(new Add(new Variable(id), new IntNumbers(1)), id);
		Environment env2 = A.evaluate(env);
		AbstractClassB B = new LessEqual(new Variable(id), (AbstractClass) end);
		while (B.evaluate(env2)) {
			env2 = SL.evaluate(env2);
			env2 = I.evaluate(env2);
		}
		return env2;

	}

	public String toString(int indent) {
		return this.toString();

	}

	public String toStringLab(int indent) {
		return this.toString() + " #" + lab + " #";
	}

	public String toStringFix(int indent, Map<String, Integer> env) {
		return "for(" + id + "=" + beg + ";" + id + "<" + end + ";" + id + "++){" + SL.toStringFix(indent, env) + "};";

	}

	public String symbol() {
		return null;

	}

	public Map<String, String> Generate_constraints(Map<String, String> varEnv) {
		return varEnv;
	}

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv) {
		return varEnv;
	}

	public String toStringOriginal(int tab) {
		return null;
	}

	public String toStringPython(int tab, Map<String, Integer> env) {
		return null;
	}

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv) {
		return varEnv;
	}

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		return varEnv;
	}

	public void sameLabel() {
	}

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv) {
		return varEnv;
	}

}
