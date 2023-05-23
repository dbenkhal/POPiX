package Statement;

import Util.Environment;
import java.util.*;

import Expression.AbstractClass;

public class StatementList extends AbstractClassS {

	public List<StateExpression> list;

	public int size() {
		return list.size();

	}

	public StateExpression get(int i) {
		return list.get(i);

	}

	public StatementList(StateExpression st) {
		list = new ArrayList<StateExpression>();
		list.add(st);

	}

	public StatementList(AbstractClassS st, StatementList stl) {
		list = new ArrayList<StateExpression>();
		list.add(st);
		int i = 0;
		while (i < stl.size()) {
			list.add(stl.get(i));
			i++;
		}

	}

	public void add(StateExpression command) {
		list.add(0, command);
	}

	public Environment evaluate(Environment env) {
		int i = 0;
		while (i < list.size()) {
			env = list.get(i).evaluate(env);
			i++;
		}
		return env;

	}

	public String toString(int indent) {
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			s = s + list.get(i).toString(indent);
		}
		return s;

	}

	public String toStringLab(int indent) {
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			s = s + list.get(i).toStringLab(i) + ";\n";
		}
		return s;
	}

	public String toStringRes(int indent, Map<String, Integer> env) {
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			s = s + list.get(i).toStringRes(indent, env) + ";\n";
		}
		return s;
	}

	public String toStringFix(int indent, Map<String, Integer> env) {
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			s = s + list.get(i).toStringFix(indent, env) + "\n";
		}
		return s;
	}

	public String symbol() {
		return null;

	}

	public Map<String, String> Generate_constraints(Map<String, String> varEnv) {
		Iterator<StateExpression> iterator = list.iterator();
		while (iterator.hasNext()) {
			StateExpression s = iterator.next();
			varEnv = s.Generate_constraints(varEnv);
		}
		return varEnv;
	}

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv) {
		Iterator<StateExpression> iterator = list.iterator();
		while (iterator.hasNext()) {
			StateExpression s = iterator.next();
			varEnv = s.Generate_constraints_Backward(varEnv);
		}
		return varEnv;

	}

	public String toStringOriginal(int tab) {
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			s = s + list.get(i).toStringOriginal(tab) + "\n";
		}
		return s;

	}

	public String toStringPython(int tab, Map<String, Integer> env) {
		String s = "";
		for (int i = 0; i < list.size(); i++) {
			s = s + list.get(i).toStringPython(tab, env) + "\n";
		}
		return s;

	}

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv) {
		Iterator<StateExpression> iterator = list.iterator();
		while (iterator.hasNext()) {
			StateExpression s = iterator.next();
			varEnv = s.Generate_Constraints_GLPK(varEnv);
		}
		return varEnv;

	}

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		Iterator<StateExpression> iterator = list.iterator();
		while (iterator.hasNext()) {
			StateExpression s = iterator.next();
			varEnv = s.Generate_Constraints_GLPK_Prime(varEnv);
		}
		return varEnv;

	}

	public void sameLabel() {
		if (AbstractClass.sameLab != 0) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).sameLabel();
			}
		}

	}

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv) {
		Iterator<StateExpression> iterator = list.iterator();
		while (iterator.hasNext()) {
			StateExpression s = iterator.next();
			varEnv = s.Generate_Constraints_AC(varEnv);
		}
		return varEnv;

	}
}
