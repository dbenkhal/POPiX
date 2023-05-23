package Expression;

import java.util.*;
import Constraint.*;
import Number.*;
import Util.Environment;

public class Sqrt extends AbstractClass {

	AbstractClass expr;
	Environment env;
	ArrayList<Constraints> csqrt = new ArrayList<Constraints>();
	ArrayList<Constraints> csqrtAC = new ArrayList<Constraints>();

	Numbers res;

	public Sqrt(AbstractClass exp) {

		expr = exp;
		lab = newLab();

	}

	public Numbers evaluate(Environment env) {
		res = new DoubleNumbers(Math.sqrt((expr.evaluate(env).getDoubleValue())));
		return res;

	}

	public void Generate_constraints(Map<String, String> varEnv) {
		String accf = "accf_lab" + expr.lab;
		z3ConstInt.add(accf);
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		String ulp = "ulp" + expr.lab;
		z3ConstInt.add(ulp);
		String ulpll = "ulpSup_lab" + lab;
		z3ConstInt.add(ulpll);
		String ulpeL1 = "ulpe_lab" + expr.lab;
		AbstractClass.z3ConstInt.add(ulpeL1);
		expr.Generate_constraints(varEnv);
		String accres = "accf_lab" + lab;
		z3ConstInt.add(accres);
		/** Constrainte **/
		Constraints cst = new Constraints(accres, new CstrId(accf), "=");
		csqrt.add(cst);
		/*** ulp de lerreur **/
		String ulpesqrt = "ulpe_lab" + lab;
		z3ConstInt.add(ulpesqrt);
		String ulperr = "ulpe_lab" + expr.getLab();
		z3ConstInt.add(ulperr);
		ExpressionConstraint ulperreur = new CstrId(ulperr);
		Constraints ulpErr = new Constraints(ulpesqrt, ulperreur, "<=");
		csqrt.add(ulpErr);
		/* acc<accf */
		Constraints ineq1 = new Constraints(acc, new CstrId(accres), "<=");
		csqrt.add(ineq1);
		ConstraintsSet.allCst.addAll(csqrt);

	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {
		String accb = "accb_lab" + expr.lab;
		String accres = "accb_lab" + lab;
		z3ConstInt.add(accb);
		z3ConstInt.add(accres);
		expr.Generate_constraints_Backward(varEnv);
		Constraints cst = new Constraints(accres, new CstrId(accb), "=");
		csqrt.add(cst);
		/* accb<acc */
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		Constraints ineq2 = new Constraints(accres, new CstrId(acc), "<=");
		csqrt.add(ineq2);
		ConstraintsSet.allCst.addAll(csqrt);

	}

	public String toString() {
		return "";
	}

	public String toStringLab() {
		return "sqrt(" + getRacine().toStringLab() + ")|" + lab + "|";
	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		if (val <= 52) {
			AbstractClass.numberSwitch++;
		}
		if (fixedMode != 0) {
			return "sqrt(" + expr.toStringRes(env) + ")|" + res.ufp() + "," + val + "|";
		} else
			return "sqrt(" + expr.toStringRes(env) + ")|" + val + "|";
	}

	public String toStringFix(Map<String, Integer> env) {
		// Integer f = env.get(AbstractClass.prefix+"lab" + lab);
		// Integer f1 = env.get(AbstractClass.prefix+"lab" + expr.lab);
		// Integer val = getFixPrec(env);
		// Integer valLeft = expr.getFixPrec(env);
		// String s = "fx_xtox("+expr.toStringFix(env)+","+valLeft+","+val+")";
		// return "fx_sqrtx(" + s + "," + f+ ")";

		Integer f = env.get(AbstractClass.prefix + "lab" + lab);
		Integer f1 = env.get(AbstractClass.prefix + "lab" + expr.lab);
		String s = "fx_xtox(" + expr.toStringFix(env) + "," + f1 + "," + f + ")";
		String ss = "POPxxx = " + s + ";\n";
		String sss = "fx_addx(fx_itox(1," + f + "),fx_divx(fx_subx(POPxxx,fx_itox(1," + f + ")" + ")," + "fx_dtox(2,"
				+ f + ")," + f + "));";
		return ss + sss;

	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}

	public String symbol() {
		return "";
	}

	public AbstractClass getRacine() {
		return expr;
	}

	public Intervalle getInterv() {
		return null;
	}

	public String toStringOriginal() {
		return "gmpy2.sqrt(" + expr.toStringOriginal() + ")";
	}

	public String toStringPython(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		return "gmpy2.sqrt(" + expr.toStringPython(env) + ")";
	}

	public String toStringInt() {
		return null;
	}

	public void Generate_Constraints_GLPK(Map<String, String> varEnv) {
		int const_prec;
		String accd = "accb" + "_lab" + lab;
		AbstractClass.z3ConstInt.add(accd);
		String accdExpr = "accb" + "_lab" + expr.getLab();
		z3ConstInt.add(accdExpr);
		expr.Generate_Constraints_GLPK(varEnv);
		if (AbstractClass.fixedMode == 1) {
			const_prec = 2;
		} else {
			const_prec = 0;
		}
		ExpressionConstraint minus = new CstrPlus(new CstrId(accd), new CstrConst(const_prec));
		Constraints cst = new Constraints(accdExpr, minus, ">=");
		csqrt.add(cst);
		ConstraintsSet.allCst.addAll(csqrt);

	}

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		String acce = "acce" + "_lab" + lab;
		z3ConstInt.add(acce);
		String accdExpr = "acce" + "_lab" + expr.lab;
		z3ConstInt.add(accdExpr);
		expr.Generate_Constraints_GLPK_Prime(varEnv);
		Constraints cst = new Constraints(acce, new CstrId(accdExpr), ">=");
		csqrt.add(cst);
		ConstraintsSet.allCst.addAll(csqrt);
	}

	public void sameLabel() {
		if (sameLab != 0) {
			expr.lab = lab;
			expr.sameLabel();

		}
	}

	public void Generate_Constraints_AC(Map<String, String> varEnv) {
		expr.Generate_Constraints_AC(varEnv);
		String ulp = "ulp_lab" + lab;
		AbstractClass.z3ConstInt.add(ulp);
		Constraints add_ACL = new Constraints(ulp, new CstrId("ulp_lab" + expr.lab), "<=");
		csqrtAC.add(add_ACL);
		ConstraintsSet.allCst.addAll(csqrtAC);
	}
}