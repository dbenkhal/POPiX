package Expression;

import Constraint.*;
import Number.*;
import java.util.*;
import Util.Environment;

public class Variable extends AbstractClass {

	String name;
	Numbers res;
	ArrayList<Constraints> varconst = new ArrayList<Constraints>();
	ArrayList<Constraints> varconstAC = new ArrayList<Constraints>();
	Intervalle IntervalleValue = new Intervalle(new DoubleNumbers(Double.POSITIVE_INFINITY),
			new DoubleNumbers(Double.NEGATIVE_INFINITY));
	Environment env = new Environment();

	public Variable(String nom) {
		this.name = nom;
		lab = newLab();
		AbstractClass.acc2Id.put("accb_lab" + lab, nom);
		AbstractClass.outputProgVarList.add(name);

	}

	public String getName() {
		return name;

	}

	public void setName(String name) {
		this.name = name;

	}

	public String toString() {
		return name;
	}

	public String toStringInt() {
		return name;
	}

	public String toStringLab() {
		return name + "|" + lab + "|";
	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		if (fixedMode != 0) {
			return name + "|" + res.ufp() + "," + val + "|";
		} else
			return name + "|" + val + "|";
	}

	public Numbers evaluate(Environment env) {
		res = env.getValue(name);
		IntervalleValue.join(res);
		xmin = IntervalleValue.xmin;
		xmax = IntervalleValue.xmax;
		return res;

	}

	public Numbers min(Numbers a, Numbers b) {
		if (a.getDoubleValue() < b.getDoubleValue())
			return a;
		else
			return b;
	}

	public Numbers max(Numbers c, Numbers d) {
		if (c.LESS(d))
			return c;
		else
			return d;
	}

	public String symbol() {
		return " ";

	}

	public void Generate_constraints(Map<String, String> varEnv) {
		String x = "accf_lab" + varEnv.get(name);
		CstrId accfX = new CstrId(x);
		String accf = new String("accf_lab" + lab);
		z3ConstInt.add(accf);
		String accx = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(accx);
		Constraints var = new Constraints(accf, accfX, "<=");
		varconst.add(var);
		String ulpe = "ulpe_lab" + varEnv.get(name);
		z3ConstInt.add(ulpe);
		String ulpevar = "ulpe_lab" + lab;
		z3ConstInt.add(ulpevar);
		ExpressionConstraint ulperreur = new CstrId(ulpe);
		Constraints ulpErr = new Constraints(ulpevar, ulperreur, "=");
		varconst.add(ulpErr);
		Constraints ineq1 = new Constraints(accx, new CstrId(accf), "<=");
		varconst.add(ineq1);
		ConstraintsSet.allCst.addAll(varconst);
	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {
		String x = "accb_lab" + varEnv.get(name);
		CstrId accbX = new CstrId(x);
		String accb = new String("accb_lab" + lab);
		z3ConstInt.add(accb);
		Constraints var = new Constraints(accb, accbX, "<=");
		varconst.add(var);
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		Constraints ineq2 = new Constraints(accb, new CstrId(acc), "<=");
		varconst.add(ineq2);
		ConstraintsSet.allCst.addAll(varconst);
	}

	public Intervalle getInterv() {
		return null;
	}

	public String toStringOriginal() {
		String s = "mpfr(" + name + "," + precMpfr + ")";
		return s;
	}

	public String toStringPython(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		String s = "mpfr(" + name + "," + val + ")";
		return s;
	}

	public void Generate_Constraints_GLPK(Map<String, String> varEnv) {
		String accd = "accb" + "_lab" + lab;
		CstrId accdId = new CstrId(accd);
		AbstractClass.z3ConstInt.add(accd);
		String accdEnv = "accb" + "_lab" + varEnv.get(name);
		z3ConstInt.add(accdEnv);
		Constraints var = new Constraints(accdEnv, accdId, ">=");
		varconst.add(var);
		ConstraintsSet.allCst.addAll(varconst);

	}

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {

		String id0;
		if (varEnv.containsKey(name)) {
			id0 = varEnv.get(name);
		} else {
			id0 = "" + lab;
		}

		String accd = "acce" + "_lab" + lab;
		CstrId accdId = new CstrId(accd);
		AbstractClass.z3ConstInt.add(accd);
		String accdEnv = "acce" + "_lab" + varEnv.get(name);
		z3ConstInt.add(accdEnv);
		Constraints var = new Constraints(accdEnv, accdId, ">=");
		varconst.add(var);

		if ((AbstractClass.costFunction == 2) & (id0 != "undefined")) {
			AbstractClass.z3ConstInt.add("accb_lab" + id0);
			Constraints wCstr = new Constraints("accb_lab" + id0, new CstrId(accd), ">=");
			// varconst.add(wCstr);
			Constraints wCstr2 = new Constraints("accb_lab" + id0, new CstrId(accd), "<=");
			varconst.add(wCstr2);

		}

		ConstraintsSet.allCst.addAll(varconst);

	}

	public void sameLabel() {

	}

	public void Generate_Constraints_AC(Map<String, String> varEnv) {
		String ulp = "ulp_lab" + lab;
		AbstractClass.z3ConstInt.add(ulp);
		String x = "ulp_lab" + varEnv.get(name);
		CstrId ulpX = new CstrId(x);
		Constraints cst = new Constraints(ulp, ulpX, "<=");
		varconstAC.add(cst);
		ConstraintsSet.allCst.addAll(varconstAC);

	}

	public String toStringFix(Map<String, Integer> env) {
		return name;
	}

	public int getFixPrec(Map<String, Integer> env) {
		return declaredIdPrec.get(AbstractClass.fixVarEnv.get(name));
	}
}
