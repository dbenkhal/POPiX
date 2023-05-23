package Statement;

import java.util.*;
import Constraint.*;
import Expression.AbstractClass;
import Number.*;
import Util.Environment;

public class AssignTabElement extends AbstractClassS {

	public String id;
	public AbstractClass exp;
	Numbers xmin, xmax;
	Intervalle IntervalleValue;
	AbstractClass indice;
	ArrayList<Constraints> list = new ArrayList<Constraints>();
	ArrayList<Constraints> list_AC = new ArrayList<Constraints>();

	public AssignTabElement(AbstractClass e, String i, AbstractClass ind) {
		exp = e;
		id = i;
		indice = ind;
		lab = AbstractClass.newLab();
		AbstractClass.acc2Id.put("accb_lab" + lab, id);
		AbstractClass.acc2Id.put("acce_lab" + lab, id);
	}

	public int getLab() {
		return lab;

	}

	public AbstractClass getexp() {
		return exp;

	}

	public String getid() {
		return id;

	}

	public Environment evaluate(Environment env) {
		Numbers num = indice.evaluate(env);
		int n = num.getIntValue();
		PopArray t = (PopArray) env.getValue(id);
		Numbers res = exp.evaluate(env);
		t.setElmt(n, res);
		env.set(id, t);
		int indWidth = indice.xmax.subtract(indice.xmin).add(new IntNumbers(1)).getIntValue();
		AbstractClass.statEnv.put("accb_lab" + lab, indWidth);
		return env;

	}

	public String symbol() {
		return " = ";

	}

	public String toString(int indent) {
		return getid().toString() + symbol() + getexp().toString();

	}

	public String toStringLab(int indent) {
		return getid().toString() + "[" + indice.toString() + "]" + "|" + lab + "|" + symbol() + getexp().toString();
	}

	public String toStringRes(int indent, Map<String, Integer> env) {
		Integer val = env.get(AbstractClass.acc2Id.get("accb_lab" + lab));
		return spaces(indent) + getid().toString() + "[" + indice.toStringInt() + "]" + "|" + val + "|" + symbol()
				+ getexp().toStringRes(env);

	}

	public String toStringFix(int indent, Map<String, Integer> env) {
		Integer val = env.get(AbstractClass.acc2Id.get(AbstractClass.prefix + "lab" + lab));
		return getid().toString() + "[" + indice.toStringInt() + "]" + symbol() + getexp().toStringFix(env) + ";";

	}

	public Map<String, String> Generate_constraints(Map<String, String> varEnv) {
		String accfx = "accf_lab" + lab;
		String accx = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(accx);
		AbstractClass.z3ConstInt.add(accfx);
		String accf_exp = "accf_lab" + Integer.toString(exp.lab);
		AbstractClass.z3ConstInt.add(accf_exp);
		getexp().Generate_constraints(varEnv);
		varEnv.put(id, lab + "");
		CstrId accf_exp2 = new CstrId(accf_exp);
		Constraints cf = new Constraints(accfx, accf_exp2, "=");
		list.add(cf);
		/*** ulp de lerreur **/
		String ulpeid = "ulpe_lab" + lab;
		AbstractClass.z3ConstInt.add(ulpeid);
		String ulperr = "ulpe_lab" + exp.getLab();
		AbstractClass.z3ConstInt.add(ulperr);
		ExpressionConstraint ulperreur = new CstrId(ulperr);
		Constraints ulpErr = new Constraints(ulpeid, ulperreur, "<=");
		list.add(ulpErr);
		/* acc<accf */
		Constraints ineq1 = new Constraints(accx, new CstrId(accfx), "<=");
		list.add(ineq1);
		ConstraintsSet.allCst.addAll(list);
		return varEnv;
	}

	public Map<String, String> Generate_constraints_Backward(Map<String, String> varEnv) {
		String accbx = "accb_lab" + lab;
		AbstractClass.z3ConstInt.add(accbx);
		String accb_exp = "accb_lab" + Integer.toString(exp.lab);
		AbstractClass.z3ConstInt.add(accb_exp);
		getexp().Generate_constraints_Backward(varEnv);
		CstrId accb_exp2 = new CstrId(accb_exp);
		Constraints cf2 = new Constraints(accbx, accb_exp2, "<=");
		list.add(cf2);
		String accbnewx = "accb_lab" + varEnv.get(id);
		CstrId accbnewxId = new CstrId(accbnewx);
		Constraints cf3 = new Constraints(accbx, accbnewxId, ">=");
		list.add(cf3);
		/* accb<acc */
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		Constraints ineq2 = new Constraints(accbx, new CstrId(acc), "<=");
		list.add(ineq2);
		ConstraintsSet.allCst.addAll(list);
		varEnv.put(id, lab + "");
		return varEnv;

	}

	public String toStringOriginal(int tab) {
		String ind2 = "xmpz(" + indice.toStringInt() + ")";
		return ntab(tab) + getid().toString() + "[" + ind2 + "]" + symbol() + getexp().toStringOriginal();
	}

	public String toStringPython(int tab, Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		String ind2 = "xmpz(" + indice.toStringInt() + ")";
		return ntab(tab) + getid().toString() + "[" + ind2 + "]" + symbol() + getexp().toStringPython(env);
	}

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv) {
		String accx = AbstractClass.acc2Id.get("accb_lab" + lab);
		AbstractClass.z3ConstInt.add(accx);
		String accd = "accb_lab" + lab;
		AbstractClass.z3ConstInt.add(accd);
		String accdExpr = "accb_lab" + Integer.toString(exp.lab);
		AbstractClass.z3ConstInt.add(accdExpr);
		exp.Generate_Constraints_GLPK(varEnv);
		varEnv.put(id, lab + "");
		AbstractClass.acc2Id.put("accb_lab" + lab, id);
		Constraints cst = new Constraints(accdExpr, new CstrId(accx), ">=");
		Constraints cst2 = new Constraints(accx, new CstrId(accd), ">=");
		list.add(cst);
		list.add(cst2);
		ConstraintsSet.allCst.addAll(list);
		return varEnv;
	}

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		String accx = AbstractClass.acc2Id.get("acce_lab" + lab);
		AbstractClass.z3ConstInt.add(accx);
		String accd = "acce_lab" + lab;
		AbstractClass.z3ConstInt.add(accd);
		String accdExpr = "acce_lab" + Integer.toString(exp.lab);
		AbstractClass.z3ConstInt.add(accdExpr);
		exp.Generate_Constraints_GLPK(varEnv);
		varEnv.put(id, lab + "");
		AbstractClass.acc2Id.put("acce_lab" + lab, id);
		Constraints cst = new Constraints(accdExpr, new CstrId(accx), ">=");
		Constraints cst2 = new Constraints(accx, new CstrId(accd), ">=");
		list.add(cst);
		list.add(cst2);
		ConstraintsSet.allCst.addAll(list);
		return varEnv;
	}

	public void sameLabel() {
		if (AbstractClass.sameLab != 0) {
			exp.sameLabel();
		}
	}

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv) {
		String ulp_2 = AbstractClass.acc2Id.get("accb_lab" + lab);
		AbstractClass.z3ConstInt.add(ulp_2);
		String ulp = "ulp_lab" + lab;
		AbstractClass.z3ConstInt.add(ulp);
		String ulpExpr = "ulp_lab" + Integer.toString(exp.lab);
		AbstractClass.z3ConstInt.add(ulpExpr);
		exp.Generate_Constraints_AC(varEnv);
		varEnv.put(id, lab + "");
		AbstractClass.acc2Id.put("ulp_lab" + lab, id);
		Constraints cst = new Constraints(ulpExpr, new CstrId(ulp_2), ">=");
		Constraints cst2 = new Constraints(ulp_2, new CstrId(ulp), ">=");
		list_AC.add(cst);
		list_AC.add(cst2);
		ConstraintsSet.allCst.addAll(list_AC);
		return varEnv;

	}

}
