package Expression;

import java.util.*;
import Constraint.*;
import Number.*;
import Util.Environment;

public class Tan extends AbstractClass {

	AbstractClass expr;
	Environment env;
	Numbers res;
	ArrayList<Constraints> ctan = new ArrayList<Constraints>();
	ArrayList<Constraints> ctanAC = new ArrayList<Constraints>();

	public Tan(AbstractClass exp) {
		expr = exp;
		lab = newLab();

	}

	public Numbers evaluate(Environment env) {
		res = new DoubleNumbers(Math.tan((expr.evaluate(env).getDoubleValue())));
		return res;
	}

	public void Generate_constraints(Map<String, String> varEnv) {
		expr.Generate_constraints(varEnv);
		String accf = "accf_lab" + expr.getLab();
		z3ConstInt.add(accf);
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		String accres = "accf_lab" + lab;
		z3ConstInt.add(accres);
		ExpressionConstraint minus = new CstrPlus(new CstrId(accf), new CstrConst(prec_trig));
		Constraints cst = new Constraints(accres, minus, "=");
		ctan.add(cst);
		/*** ulp de lerreur **/
		String ulpetan = "ulpe_lab" + lab;
		z3ConstInt.add(ulpetan);
		String ulperr = "ulpe_lab" + expr.getLab();
		z3ConstInt.add(ulperr);
		ExpressionConstraint ulperreur = new CstrId(ulperr);
		Constraints ulpErr = new Constraints(ulpetan, ulperreur, "<=");
		ctan.add(ulpErr);
		/* acc<accf */
		Constraints ineq1 = new Constraints(acc, new CstrId(accres), "<=");
		ctan.add(ineq1);
		ConstraintsSet.allCst.addAll(ctan);

	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {
		String accb = "accb_lab" + expr.lab;
		z3ConstInt.add(accb);
		String accres = "accb_lab" + lab;
		z3ConstInt.add(accres);
		expr.Generate_constraints_Backward(varEnv);
		ExpressionConstraint plus = new CstrMinus(new CstrId(accb), new CstrConst(prec_trig));
		Constraints cst = new Constraints(accres, plus, "=");
		ctan.add(cst);
		/* accb<acc */
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		Constraints ineq2 = new Constraints(accres, new CstrId(acc), "<=");
		ctan.add(ineq2);
		ConstraintsSet.allCst.addAll(ctan);

	}

	public String symbol() {
		return " ";
	}

	public String toStringLab() {
		return "tan(" + expr.toStringLab() + ")|" + lab + "|";
	}

	public AbstractClass gettan() {
		return expr;
	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get(AbstractClass.prefix + "lab" + lab);
		if (val <= 52) {
			AbstractClass.numberSwitch++;
		}
		if (fixedMode != 0) {
			return "tan(" + expr.toStringRes(env) + ")|" + res.ufp() + "," + val + "|";
		} else
			return "tan(" + expr.toStringRes(env) + ")|" + val + "|";

	}

	public Intervalle getInterv() {
		return null;
	}

	public String toStringOriginal() {
		return "gmpy2.tan(" + expr.toStringOriginal() + ")";
	}

	public String toStringPython(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		return "gmpy2.tan(" + expr.toStringPython(env) + ")";
	}

	public String toStringFix(Map<String, Integer> env) {
		Integer f = env.get(AbstractClass.prefix + "lab" + lab);
		Integer f1 = env.get(AbstractClass.prefix + "lab" + expr.lab);
		String s = "fx_xtox(" + expr.toStringFix(env) + "," + f1 + "," + f + ")";
		// return "fx_tanx(" + s + "," + f+ ")";
		String ss = "POPxxx = " + s + ";\n";
		String sss = "fx_addx(POPxxx,fx_divx(fx_mulx(POPxxx,fx_mulx(POPxxx,POPxxx," + f + ")," + f + ")," + "fx_dtox(3,"
				+ f + ")," + f + "));" + " //" + "fx_tanx(" + s + "," + f + ")";
		return ss + sss;

	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
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
			const_prec = 3;
		} else {
			const_prec = prec_trig;
		}
		ExpressionConstraint minus = new CstrPlus(new CstrId(accd), new CstrConst(const_prec));
		Constraints cst = new Constraints(accdExpr, minus, ">=");
		ctan.add(cst);
		ConstraintsSet.allCst.addAll(ctan);
	}

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		String acce = "acce" + "_lab" + lab;
		z3ConstInt.add(acce);
		Constraints cst = new Constraints(acce, new CstrConst(prec), ">=");
		ctan.add(cst);
		ConstraintsSet.allCst.addAll(ctan);
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
		ctanAC.add(add_ACL);
		ConstraintsSet.allCst.addAll(ctanAC);
	}
}
