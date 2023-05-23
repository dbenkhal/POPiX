package Statement;

import java.util.*;
import Expression.*;
import Number.*;
import Util.Environment;

public class CreateArray extends AbstractClassS {
	int n;
	String t;
	public static Map<String, Integer> arraySize = new HashMap<String, Integer>();

	public CreateArray(String tab, int taille) {
		t = tab;
		n = taille;
		lab = AbstractClass.newLab();
		nbTab++;
		arraySize.put(t, n);
	}

	public int getN() {
		return n;
	}

	public String getT() {
		return t;
	}

	public Environment evaluate(Environment env) {
		PopArray tab = new PopArray(n, new DoubleNumbers(0.0));
		env.set(t, tab);
		return env;
	}

	public Map<String, String> Generate_constraints(Map<String, String> varEnv) {
		return varEnv;
	}

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv) {
		return varEnv;
	}

	public String symbol() {
		return "";
	}

	public String toStringLab(int indent) {
		return "create_array(" + t + "," + n + ")|" + lab + "|";
	}

	public String toStringRes(int indent, Map<String, Integer> env) {
		return "create_array(" + t + "," + (int) n + ")";
	}

	public String toStringOriginal(int tab) {
		return ntab(tab) + t + " = [0.0 for aaaapython in range(" + n + ")]";
	}

	public String toStringPython(int tab, Map<String, Integer> env) {
		return ntab(tab) + t + " = [0.0 for aaaapython in range(" + n + ")]";
	}

	public String toStringFix(int indent, Map<String, Integer> env) {

		return "fixed_t " + id + "[" + n + "];";
	}

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv) {
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
