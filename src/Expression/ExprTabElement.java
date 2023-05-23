package Expression;

import java.util.*;
import Constraint.*;
import Number.*;
import Util.Environment;

public class ExprTabElement extends AbstractClass {
	String name;
	AbstractClass indice;
	Numbers res;
	ArrayList<Constraints> varconst = new ArrayList<Constraints>();
	Intervalle IntervalleValue = new Intervalle(new DoubleNumbers(Double.POSITIVE_INFINITY),
			new DoubleNumbers(Double.NEGATIVE_INFINITY));
	Environment env = new Environment();

	public ExprTabElement(String nom, AbstractClass i) {
		name = nom;
		indice = i;
		lab = newLab();
		AbstractClass.acc2Id.put("accb_lab" + lab, nom);

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
		return name + "[" + indice.toStringInt() + "]";
	}

	public String toStringLab() {
		return name + "[" + indice.toStringLab() + "]" + "|" + lab + "|";
	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		String se = indice.toStringInt();
		if (fixedMode != 0) {
			return name + "[" + se + "]" + "|" + res.ufp() + "," + val + "| ";
		} else
			return name + "[" + se + "]" + "|" + val + "| ";
	}

	public String toStringFix(Map<String, Integer> env) {

		Integer val = env.get(AbstractClass.prefix + "lab" + lab);
		String se = indice.toStringInt();
		return name + "[" + se + "]";

	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}

	public Numbers evaluate(Environment env) {
		Numbers ind = indice.evaluate(env);
		Numbers tab = env.getValue(name);
		int i = ind.getIntValue();
		res = tab.getElmt(i);
		IntervalleValue.join(res);
		xmin = IntervalleValue.xmin;
		xmax = IntervalleValue.xmax;
		int indWidth = indice.xmax.subtract(indice.xmin).add(new IntNumbers(1)).getIntValue();
		statEnv.put("accb_lab" + lab, indWidth);
		return res;

	}

	public Numbers min(Numbers a, Numbers b) {
		System.out.println("arrays have no min");
		return null;

	}

	public Numbers max(Numbers c, Numbers d) {
		System.out.println("arrays have no max");
		return null;

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
		/* contrainte accf **/
		Constraints var = new Constraints(accf, accfX, "<=");
		varconst.add(var);
		/** ulp erreur **/
		String ulpe = "ulpe_lab" + varEnv.get(name);
		z3ConstInt.add(ulpe);
		String ulpevar = "ulpe_lab" + lab;
		z3ConstInt.add(ulpevar);
		ExpressionConstraint ulperreur = new CstrId(ulpe);
		Constraints ulpErr = new Constraints(ulpevar, ulperreur, "=");
		varconst.add(ulpErr);
		/* acc<accf */
		Constraints ineq1 = new Constraints(accx, new CstrId(accf), "<=");
		varconst.add(ineq1);
		ConstraintsSet.allCst.addAll(varconst);
	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {
		String x = "accb_lab" + varEnv.get(name);
		CstrId accbX = new CstrId(x);
		String accb = new String("accb_lab" + lab);
		z3ConstInt.add(accb);
		Constraints var = new Constraints(accb, accbX, "=");
		varconst.add(var);
		/* accb<acc */
		String acc = "acc_lab" + lab;
		z3ConstInt.add(acc);
		Constraints ineq3 = new Constraints(accb, new CstrId(acc), "<=");
		varconst.add(ineq3);
		ConstraintsSet.allCst.addAll(varconst);

	}

	public Intervalle getInterv() {
		return null;
	}

	public String toStringOriginal() {
		String ind2 = "xmpz(" + indice.toStringInt() + ")";
		return "mpfr(" + name + "[" + ind2 + "]," + precMpfr + ")";
	}

	public String toStringPython(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		String ind2 = "xmpz(" + indice.toStringInt() + ")";
		return "mpfr(" + name + "[" + ind2 + "]," + val + ")";
	}

	public void Generate_Constraints_GLPK(Map<String, String> varEnv) {
		String accd = "accb" + "_lab" + lab;
		CstrId accdId = new CstrId(accd);
		AbstractClass.z3ConstInt.add(accd);
		String accdEnv = "accb" + "_lab" + varEnv.get(name);
		AbstractClass.z3ConstInt.add(accdEnv);
		z3ConstInt.add(accdEnv);
		Constraints var = new Constraints(accdEnv, accdId, ">=");
		varconst.add(var);
		ConstraintsSet.allCst.addAll(varconst);

	}

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		String acce = "acce" + "_lab" + lab;
		AbstractClass.z3ConstInt.add(acce);
		String acceEnv = "acce" + "_lab" + varEnv.get(name);
		z3ConstInt.add(acceEnv);
		Constraints var = new Constraints(acceEnv, new CstrId(acce), ">=");
		varconst.add(var);
		ConstraintsSet.allCst.addAll(varconst);

	}

	public void sameLabel() {
	}

	@Override
	public void Generate_Constraints_AC(Map<String, String> varEnv) {
		// TODO Auto-generated method stub

	}

}
