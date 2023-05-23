package Statement;

import java.util.*;
import Constraint.*;
import Expression.AbstractClass;
import Number.*;
import Util.Environment;

public class Assign extends AbstractClassS {

	public String id;
	public AbstractClass exp;
	Numbers xmin, xmax;
	Intervalle intervalleValue = new Intervalle(new DoubleNumbers(Double.POSITIVE_INFINITY),
			new DoubleNumbers(Double.NEGATIVE_INFINITY));
	ArrayList<Constraints> list = new ArrayList<Constraints>();
	ArrayList<Constraints> list_AC = new ArrayList<Constraints>();

	public Assign(AbstractClass e, String i) {
		exp = e;
		id = i;
		lab = AbstractClass.newLab();
		if (AbstractClass.progForAnalysis == true) {
			AbstractClass.accVars.add("accb_lab" + lab);
			AbstractClass.acc2Id.put("accb_lab" + lab, id);
			AbstractClass.outputProgVarList.add(id);
		}
	}

	public Assign(Numbers e, String i) {
		exp = e;
		id = i;
		lab = AbstractClass.newLab();
		if (AbstractClass.progForAnalysis == true) {
			AbstractClass.accVars.add("accb_lab" + lab);
			AbstractClass.acc2Id.put("accb_lab" + lab, id);
			AbstractClass.outputProgVarList.add(id);
		}

	}

	public int getLab() {
		return this.lab;

	}

	public AbstractClass getexp() {
		return exp;

	}

	public String getid() {
		return id;

	}

	public Environment evaluate(Environment env) {
		Numbers num = getexp().evaluate(env);
		env.set(id, num);
		intervalleValue.join(num);
		xmin = intervalleValue.xmin;
		xmax = intervalleValue.xmax;
		return env;
	}

	public String symbol() {
		return " = ";
	}

	public String toString(int indent) {
		return getid().toString() + symbol() + getexp().toString();
	}

	public String toStringLab(int indent) {
		return getid().toString() + "|" + lab + "|" + symbol() + getexp().toStringLab();
	}

	public String toStringRes(int indent, Map<String, Integer> env) {
		Integer val = env.get(AbstractClass.prefix + "lab" + lab);
		AbstractClass.declaredIdPrec.put(getid() + lab, val);

		return spaces(indent) + getid().toString() + "|" + val + "|" + symbol() + getexp().toStringRes(env);
	}

	public Map<String, String> Generate_constraints(Map<String, String> varEnv) {
		String accfx = "accf_lab" + lab;
		String accx = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(accx);
		AbstractClass.accVarsAssignedOnly.add(accx);
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
		System.out.println("Backward assign chain");
		System.out.println(cf2 + "\n" + cf3);
		ConstraintsSet.allCst.addAll(list);
		return varEnv;

	}

	public String toStringOriginal(int tab) {
		return ntab(tab) + getid().toString() + symbol() + getexp().toStringOriginal();
	}

	public String toStringPython(int tab, Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		return ntab(tab) + getid().toString() + symbol() + getexp().toStringPython(env);
	}

	public String toStringFix(int indent, Map<String, Integer> env) {
		AbstractClass.outputProgVarList.add(id);

		String s = id + " = " + exp.toStringFix(env) + ";";
		AbstractClass.fixVarEnv.put(getid(), getid() + lab);
		return s;
	}

	public Map<String, String> Generate_Constraints_GLPK(Map<String, String> varEnv) {
		String accd = "accb_lab" + lab;
		AbstractClass.z3ConstInt.add(accd);
		String accdExpr = "accb" + "_lab" + Integer.toString(exp.lab);
		AbstractClass.z3ConstInt.add(accdExpr);
		exp.Generate_Constraints_GLPK(varEnv);
		varEnv.put(id, lab + "");
		AbstractClass.ufpEnv.put(lab + "", new DoubleNumbers(Math.max(xmax.ufp(), xmin.ufp())));
		// ArrayList<String> vl = AbstractClass.var2AllItsLabs.get(id);
		// vl.add(vl.size(), lab + "");
		// AbstractClass.var2AllItsLabs.put(id, vl);

		Constraints cst = new Constraints(accdExpr, new CstrId(accd), ">=");
		list.add(cst);

		// for costFuncion 1 (minimize max format)
		if (AbstractClass.costFunction == 1) {
			// Constraints cst2 = new Constraints(AbstractClass.costFunction1MaxAcc, new
			// CstrId(accd), ">=");
			Constraints cst2 = new Constraints(AbstractClass.costFunction1MaxAcc, new CstrId(accdExpr), ">=");

			list.add(cst2);
		}
		;

		ConstraintsSet.allCst.addAll(list);

		return varEnv;
	}

	public Map<String, String> Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {

		String id0;
		if (varEnv.containsKey(id)) {
			id0 = varEnv.get(id);
		} else {
			id0 = "" + lab;
		}
		String accd = "accb_lab" + lab;
		AbstractClass.z3ConstInt.add(accd);
		AbstractClass.ufpEnv.put(lab + "", new DoubleNumbers(Math.max(xmax.ufp(), xmin.ufp())));

		String acce = "acce" + "_lab" + lab;
		AbstractClass.z3ConstInt.add(acce);
		String acceExpr = "acce" + "_lab" + Integer.toString(exp.lab);
		AbstractClass.z3ConstInt.add(acceExpr);
		exp.Generate_Constraints_GLPK_Prime(varEnv);
		varEnv.put(id, lab + "");
		Constraints cst = new Constraints(acceExpr, new CstrId(acce), ">=");
		list.add(cst);

		if ((AbstractClass.costFunction == 2) & (id0 != "undefined")/* &(AbstractClass.ufpEnv.get(id0)!=null) */) { // minimize
			// #
			// casts
			/*
			 * String w = "accb_lab_w" + lab; System.out.println("----->"+w);
			 * AbstractClass.z3ConstInt.add(w); if (AbstractClass.ufpEnv.get(id0)==null)
			 * System.out.println(":::--(((( "+id0); CstrConst ufpL0 = new
			 * CstrConst(AbstractClass.ufpEnv.get(id0).getIntValue()); CstrConst ufpL1 = new
			 * CstrConst((Math.max(xmax.ufp(), xmin.ufp())));
			 */
			/*
			 * Constraints wCstr = new Constraints(w, new CstrMinus(new CstrConst(1), new
			 * CstrXi(new CstrPlus(ufpL0, new CstrId("accb_lab"+id0)), new CstrPlus(ufpL1,
			 * new CstrId(accd)))), ">=" );
			 */
			AbstractClass.z3ConstInt.add("accb_lab" + id0);
			Constraints wCstr = new Constraints("accb_lab" + id0, new CstrId(accd), ">=");
			// list.add(wCstr);
			Constraints wCstr2 = new Constraints("accb_lab" + id0, new CstrId(accd), "<=");
			list.add(wCstr2);

		}

		ConstraintsSet.allCst.addAll(list);
		return varEnv;
	}

	public void sameLabel() {
		if (AbstractClass.sameLab != 0) {
			exp.sameLabel();
		}
	}

	public Map<String, String> Generate_Constraints_AC(Map<String, String> varEnv) {
		String ulp = "ulp_lab" + lab;
		AbstractClass.z3ConstInt.add(ulp);
		String ulpExpr = "ulp" + "_lab" + Integer.toString(exp.lab);
		AbstractClass.z3ConstInt.add(ulpExpr);
		exp.Generate_Constraints_AC(varEnv);
		varEnv.put(id, lab + "");
		AbstractClass.ufpEnv.put(lab + "", new DoubleNumbers(Math.max(xmax.ufp(), xmin.ufp())));
		Constraints cst = new Constraints(ulpExpr, new CstrId(ulp), ">=");
		list_AC.add(cst);
		ConstraintsSet.allCst.addAll(list_AC);
		return varEnv;
	}

}