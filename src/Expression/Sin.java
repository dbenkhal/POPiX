package Expression;

import java.util.*;
import Constraint.*;
import Number.*;
import Util.Environment;

public class Sin extends AbstractClass {

	AbstractClass expr;
	Environment env;
	Numbers res;
	ArrayList<Constraints> csin = new ArrayList<Constraints>();
	ArrayList<Constraints> csinAC = new ArrayList<Constraints>();

	public Sin(AbstractClass exp) {
		expr = exp;
		lab = newLab();

	}

	public Sin(AbstractClass exp, int opPrec) {
		expr = exp;
		lab = newLab();
		absCancelPrec = opPrec;

	}

	public Numbers evaluate(Environment env) {
		res = new DoubleNumbers(Math.sin((expr.evaluate(env).getDoubleValue())));
		// res = new DoubleNumbers(fx_sin_phase((expr.evaluate(env).getDoubleValue())));
		return res;
	}

	public void Generate_constraints(Map<String, String> varEnv) {
		expr.Generate_constraints(varEnv);
		/*** contrainte acc(sin) = p - 3 ****/
		String accf = "accf_lab" + expr.getLab();
		z3ConstInt.add(accf);
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		String accres = "accf_lab" + lab;
		z3ConstInt.add(accres);
		ExpressionConstraint minus = new CstrPlus(new CstrId(accf), new CstrConst(prec_trig));
		Constraints cst = new Constraints(accres, minus, "=");
		csin.add(cst);
		/*** ulp de lerreur **/
		String ulpesin = "ulpe_lab" + lab;
		z3ConstInt.add(ulpesin);
		String ulperr = "ulpe_lab" + expr.getLab();
		z3ConstInt.add(ulperr);
		ExpressionConstraint ulperreur = new CstrId(ulperr);
		Constraints ulpErr = new Constraints(ulpesin, ulperreur, "<=");
		csin.add(ulpErr);
		/* acc<accf */
		Constraints ineq1 = new Constraints(acc, new CstrId(accres), "<=");
		csin.add(ineq1);
		ConstraintsSet.allCst.addAll(csin);

	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {
		String accb = "accb_lab" + expr.lab;
		z3ConstInt.add(accb);
		String accres = "accb_lab" + lab;
		z3ConstInt.add(accres);
		expr.Generate_constraints_Backward(varEnv);
		ExpressionConstraint plus = new CstrMinus(new CstrId(accb), new CstrConst(prec_trig));
		Constraints cst = new Constraints(accres, plus, "=");
		csin.add(cst);
		/* accb<acc */
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		Constraints ineq2 = new Constraints(accres, new CstrId(acc), "<=");
		csin.add(ineq2);
		ConstraintsSet.allCst.addAll(csin);

	}

	public String symbol() {
		return " ";
	}

	public String toStringLab() {
		return "sin(" + expr.toStringLab() + ")|" + lab + "|";
	}

	public AbstractClass getSinus() {
		return expr;
	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		if (val <= 52) {
			AbstractClass.numberSwitch++;
		}
		;
		if (fixedMode != 0) {
			return "sin(" + expr.toStringRes(env) + ")|" + res.ufp() + "," + val + "|";
		} else
			return "sin(" + expr.toStringRes(env) + ")|" + val + "|";
	}

	public Intervalle getInterv() {
		return null;
	}

	public String toStringOriginal() {
		return "gmpy2.sin(" + expr.toStringOriginal() + ")";
	}

	public String toStringPython(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		return "gmpy2.sin(" + expr.toStringPython(env) + ")";
	}

	public String toStringFix(Map<String, Integer> env) {
		Integer f = env.get(AbstractClass.prefix + "lab" + lab);
		Integer f1 = env.get(AbstractClass.prefix + "lab" + expr.lab);
		Integer val = getFixPrec(env);
		Integer valLeft = expr.getFixPrec(env);
		String s = "fx_xtox(" + expr.toStringFix(env) + "," + valLeft + "," + val + ")";
		// String s = "fx_xtox("+expr.toStringFix(env)+","+f1+","+f+")";
		// return "fx_sinx(" + s + "," + f+ ")";
		String ss = " POPxxx = " + s + ";\n";
		String sss = "fx_subx(POPxxx,fx_divx(fx_mulx(POPxxx,fx_mulx(POPxxx,POPxxx," + f + ")," + f + ")," + "fx_dtox(6,"
				+ f + ")," + f + "));" + " //" + "fx_sinx(" + s + "," + f + ")";
		return ss + sss;
	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}

	public String toStringInt() {
		return null;
	}

	public void Generate_Constraints_GLPK(Map<String, String> varEnv) {
		String accd = "accb" + "_lab" + lab;
		AbstractClass.z3ConstInt.add(accd);
		int const_prec;
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
		csin.add(cst);
		ConstraintsSet.allCst.addAll(csin);
	}

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		String acce = "acce" + "_lab" + lab;
		z3ConstInt.add(acce);
		Constraints cst = new Constraints(acce, new CstrConst(prec), ">=");
		csin.add(cst);
		ConstraintsSet.allCst.addAll(csin);
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
		csinAC.add(add_ACL);
		CstrConst p = new CstrConst(res.ufp() - absCancelPrec + 1);
		Constraints cst = new Constraints(ulp, p, "<=");
		csinAC.add(cst);
		ConstraintsSet.allCst.addAll(csinAC);

	}

	double fx_sin_phase(double uval) {
		double res = 0;
		double fpart = uval / (2 * Math.PI);

		if ((uval >= 0.0) & (uval < Math.PI / 2.0))
			res = 4.0 * fpart;

		if ((uval >= Math.PI) & (uval < 3.0 * Math.PI / 2.0))
			res = -4.0 * fpart;

		if ((uval >= Math.PI / 2.0) & (uval < Math.PI))
			res = 1.0 - 4.0 * fpart;

		if ((uval >= 3.0 * Math.PI / 2.0) & (uval < 2.0 * Math.PI))
			res = -1.0 + 4.0 * fpart;
		return res;
	}
}
