package Statement;

import Util.*;
import java.util.*;
import Boolean.AbstractClassB;
import Constraint.*;
import Expression.AbstractClass;

public class While extends AbstractClassS {

	AbstractClassB cond;
	StatementList body;
	ArrayList<Constraints> while_list = new ArrayList<Constraints>();
	ArrayList<Constraints> while_list_AC = new ArrayList<Constraints>();

	public While(AbstractClassB condd, StatementList bodyy) {
		cond = condd;
		body = bodyy;
		lab = AbstractClass.newLab();

	}

	public AbstractClassB condition() {
		return cond;

	}

	public StatementList body() {
		return body;

	}

	public Environment evaluate(Environment env) {
		while (condition().evaluate(env)) {
			env = body().evaluate(env);

		}
		return env;

	}

	public String toString(int indent) {
		indent = 1;
		String s1 = condition().toString();
		String s2 = body().toString(indent + 2);
		return ("While ( " + s1 + " )" + "{\n" + s2 + " } ");

	}

	public String toStringLab(int indent) {
		indent = 1;
		String s1 = condition().toStringLab();
		String s2 = body().toStringLab(indent + 2);
		return ("while (" + s1 + " |" + lab + " |" + ") {\n" + s2 + " |" + lab + " |" + " } ");
	}

	public String toStringRes(int indent, Map<String, Integer> env) {
		String s1 = condition().toString();
		String s2 = body().toStringRes(indent + 2, env);
		return (spaces(indent) + "while (" + s1.toString() + ") {\n" + s2 + " } ");
	}

	public String toStringFix(int indent, Map<String, Integer> env) {
		return "while(" + cond + "){" + body.toStringFix(indent, env) + "};";

	}

	public String symbol() {
		return null;

	}

	public Map<String, String> Generate_constraints(Map<String, String> varEnv) {
		Map<String, String> varEnv3 = body.Generate_constraints(varEnv);
		Set<String> varSet = varEnv.keySet();
		Iterator<String> it = varSet.iterator();
		while (it.hasNext()) {
			String id = it.next();
			String id_avant = "accf_lab" + varEnv.get(id);
			String id_apres = "accf_lab" + varEnv3.get(id);
			Constraints eq = new Constraints(id_avant, new CstrId(id_apres), ">=");
			while_list.add(eq);
		}
		ConstraintsSet.allCst.addAll(while_list);
		return varEnv3;
	}

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv) {
		Map<String, String> varEnv2 = cond.Generate_constraints_Backward(varEnv);
		Map<String, String> varEnv3 = body.Generate_constraints_Backward(varEnv2);
		Set<String> varSet = varEnv2.keySet();
		Iterator<String> it = varSet.iterator();
		while (it.hasNext()) {
			String id = it.next();
			String id_avant = "accb_lab" + varEnv2.get(id);
			String id_apres = "accb_lab" + varEnv3.get(id);
			Constraints eq = new Constraints(id_avant, new CstrId(id_apres), "<=");
			while_list.add(eq);
		}
		ConstraintsSet.allCst.addAll(while_list);
		return varEnv3;

	}

	public String toStringOriginal(int tab) {
		String s1 = condition().toString();
		String s2 = body().toStringOriginal(tab + 1);
		return (ntab(tab) + "while ( " + s1 + "):\n" + s2);
	}

	public String toStringPython(int tab, Map<String, Integer> env) {
		String s1 = condition().toString();
		String s2 = body().toStringPython(tab + 1, env);
		return (ntab(tab) + "while ( " + s1 + "):\n" + s2);
	}

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv) {
		Map<String, String> varEnv3 = body.Generate_Constraints_GLPK(varEnv);
		Set<String> varSet = varEnv.keySet();
		Iterator<String> it = varSet.iterator();
		while (it.hasNext()) {
			String id = it.next();
			String id_avant = "accb" + "_lab" + varEnv.get(id);
			AbstractClass.z3ConstInt.add(id_avant);
			String id_apres = "accb" + "_lab" + varEnv3.get(id);
			AbstractClass.z3ConstInt.add(id_apres);
			String id_endwhile = "accb" + "_lab" + lab;
			AbstractClass.z3ConstInt.add(id_endwhile);
			Constraints while1 = new Constraints(id_avant, new CstrId(id_endwhile), ">=");
			while_list.add(while1);
			Constraints while2 = new Constraints(id_apres, new CstrId(id_endwhile), ">=");
			while_list.add(while2);
			varEnv.put(id, lab + "");
		}
		ConstraintsSet.allCst.addAll(while_list);
		return varEnv;
	}

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		Map<String, String> varEnv3 = body.Generate_Constraints_GLPK_Prime(varEnv);
		Set<String> varSet = varEnv.keySet();
		Iterator<String> it = varSet.iterator();
		while (it.hasNext()) {
			String id = it.next();
			String id_avant = "acce" + "_lab" + varEnv.get(id);
			AbstractClass.z3ConstInt.add(id_avant);
			String id_apres = "acce" + "_lab" + varEnv3.get(id);
			AbstractClass.z3ConstInt.add(id_apres);
			String id_endwhile = "acce" + "_lab" + lab;
			AbstractClass.z3ConstInt.add(id_endwhile);
			Constraints while1 = new Constraints(id_avant, new CstrId(id_endwhile), ">=");
			while_list.add(while1);
			Constraints while2 = new Constraints(id_apres, new CstrId(id_endwhile), ">=");
			while_list.add(while2);
			varEnv.put(id, lab + "");
		}
		ConstraintsSet.allCst.addAll(while_list);
		return varEnv;
	}

	public void sameLabel() {
		if (AbstractClass.sameLab != 0) {
			body.sameLabel();
		}
	}

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv) {
		Map<String, String> varEnv3 = body.Generate_Constraints_AC(varEnv);
		Set<String> varSet = varEnv.keySet();
		Iterator<String> it = varSet.iterator();
		while (it.hasNext()) {
			String id = it.next();
			String id_avant = "ulp" + "_lab" + varEnv.get(id);
			AbstractClass.z3ConstInt.add(id_avant);
			String id_apres = "ulp" + "_lab" + varEnv3.get(id);
			AbstractClass.z3ConstInt.add(id_apres);
			String id_endwhile = "ulp" + "_lab" + lab;
			AbstractClass.z3ConstInt.add(id_endwhile);
			Constraints while1 = new Constraints(id_avant, new CstrId(id_endwhile), ">=");
			while_list_AC.add(while1);
			Constraints while2 = new Constraints(id_apres, new CstrId(id_endwhile), ">=");
			while_list_AC.add(while2);
			varEnv.put(id, lab + "");
		}
		ConstraintsSet.allCst.addAll(while_list_AC);
		return varEnv;

	}

}