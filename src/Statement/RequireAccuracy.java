package Statement;

import java.util.*;
import Constraint.*;
import Expression.AbstractClass;
import Util.Environment;

public class RequireAccuracy extends AbstractClassS {
	String name;
	int prec;
	ArrayList<Constraints> liste = new ArrayList<Constraints>();
	ArrayList<Constraints> liste_AC = new ArrayList<Constraints>();

	public RequireAccuracy(String nom, int p) {
		name = nom;
		prec = p;
		lab = AbstractClass.newLab();
		require_var_name = nom;
		AbstractClass.fixFinalPrec = p;
	}

	public Environment evaluate(Environment env) {
		// System.out.println("@require_nsb|" + lab + "|:" + name + "=" +
		// env.getValue(name));
		AbstractClass.floatTAFFORes.add(env.getValue(name).getDoubleValue());

		return env;

	}

	public Map<String, String> Generate_constraints(Map<String, String> varEnv) {
		String accf = "accf_lab" + lab;
		String acc = "acc_lab" + lab;
		varEnv.put(name, lab + "");
		AbstractClass.z3ConstInt.add(accf);
		AbstractClass.z3ConstInt.add(acc);
		return varEnv;
	}

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv) {
		String accbx = "accb_lab" + varEnv.get(name);
		AbstractClass.z3ConstInt.add(accbx);
		CstrConst pp = new CstrConst(prec);
		Constraints cf = new Constraints(accbx, pp, ">=");
		liste.add(cf);
		String accbx2 = "accb_lab" + lab;
		AbstractClass.z3ConstInt.add(accbx2);
		CstrConst pp2 = new CstrConst(prec);
		Constraints cf2 = new Constraints(accbx2, pp2, ">=");
		liste.add(cf2);
		/* accb<acc */
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		Constraints ineq2 = new Constraints(accbx, new CstrId(acc), "<=");
		Constraints ineq3 = new Constraints(accbx2, new CstrId(acc), "<=");
		liste.add(ineq2);
		liste.add(ineq3);
		ConstraintsSet.allCst.addAll(liste);
		varEnv.put(id, lab + "");
		return varEnv;
	}

	public String symbol() {
		return "";
	}

	public String toStringLab(int indent) {
		return "require_accuracy(" + name + "," + prec + ")|" + lab + "|";
	}

	public String toStringRes(int indent, Map<String, Integer> env) {
		Integer val = env.get(AbstractClass.prefix + "lab" + lab);
		return spaces(indent) + "require_accuracy(" + name + "," + prec + ")";
	}

	public String toStringFix(int indent, Map<String, Integer> env) {
		return "";
	}

	public String toStringOriginal(int tab) {
		return ntab(tab) + "print(\"" + name + " = \" + str(" + name + ")  )\n" + ntab(tab) + ntab(tab) + "# Variable "
				+ name + " has an accuracy of " + prec + " bits.";
	}

	public String toStringPython(int tab, Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		return ntab(tab) + "print(\"" + name + " = \" + str(" + name + "))\n" + ntab(tab) + "# Variable " + name
				+ " has an accuracy of " + prec + " bits.";

	}

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv) {
		String accd = "accb" + "_lab" + varEnv.get(name);
		AbstractClass.z3ConstInt.add(accd);
		CstrConst p = new CstrConst(prec);
		Constraints cst = new Constraints(accd, p, ">=");
		liste.add(cst);
		ConstraintsSet.allCst.addAll(liste);
		return varEnv;
	}

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		return varEnv;

	}

	public void sameLabel() {
	}

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv) {
		String ulp = "ulp" + "_lab" + varEnv.get(name);
		AbstractClass.z3ConstInt.add(ulp);
		CstrConst p = new CstrConst(prec);
		Constraints cst = new Constraints(ulp, p, ">=");
		liste_AC.add(cst);
		ConstraintsSet.allCst.addAll(liste_AC);
		return varEnv;
	}

}