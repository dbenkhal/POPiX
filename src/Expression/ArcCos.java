package Expression;

import java.util.*;
import Constraint.*;
import Number.*;
import Util.Environment;

public class ArcCos extends AbstractClass {

	AbstractClass expr;
	Environment env;
	Numbers res;
	ArrayList<Constraints> cacos = new ArrayList<Constraints>();
	ArrayList<Constraints> cacosAC = new ArrayList<Constraints>();

	public ArcCos(AbstractClass exp) {
		expr = exp;
		lab = newLab();

	}

	public Numbers evaluate(Environment env) {
		res = new DoubleNumbers(Math.acos((expr.evaluate(env).getDoubleValue())));
		return res;
	}

	public void Generate_constraints(Map<String, String> varEnv) {
		expr.Generate_constraints(varEnv);
		/*** contrainte acc(ArcCos) = p - 3 ****/
		String accf = "accf_lab" + expr.getLab();
		z3ConstInt.add(accf);
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		String accres = "accf_lab" + lab;
		z3ConstInt.add(accres);
		ExpressionConstraint minus = new CstrPlus(new CstrId(accf), new CstrConst(prec_trig));
		Constraints cst = new Constraints(accres, minus, "=");
		cacos.add(cst);
		/* acc<accf */
		Constraints ineq1 = new Constraints(acc, new CstrId(accres), "<=");
		cacos.add(ineq1);
		/*** ulp de lerreur **/
		String ulpeacos = "ulpe_lab" + lab;
		z3ConstInt.add(ulpeacos);
		String ulperr = "ulpe_lab" + expr.getLab();
		z3ConstInt.add(ulperr);
		ExpressionConstraint ulperreur = new CstrId(ulperr);
		Constraints ulpErr = new Constraints(ulpeacos, ulperreur, "<=");
		cacos.add(ulpErr);
		ConstraintsSet.allCst.addAll(cacos);

	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {
		String accb = "accb_lab" + expr.lab;
		z3ConstInt.add(accb);
		String accres = "accb_lab" + lab;
		z3ConstInt.add(accres);
		expr.Generate_constraints_Backward(varEnv);
		ExpressionConstraint plus = new CstrMinus(new CstrId(accb), new CstrConst(prec_trig));
		Constraints cst = new Constraints(accres, plus, "=");
		cacos.add(cst);
		/* accb<acc */
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		Constraints ineq2 = new Constraints(accres, new CstrId(acc), "<=");
		cacos.add(ineq2);
		ConstraintsSet.allCst.addAll(cacos);

	}

	public String symbol() {
		return " ";
	}

	public String toStringLab() {
		return "acos(" + expr.toStringLab() + ")|" + lab + "|";
	}

	public AbstractClass getAcos() {
		return expr;
	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get(AbstractClass.prefix + "lab" + lab);
		if (val <= 52) {
			AbstractClass.numberSwitch++;
		}
		;
		if (fixedMode != 0) {
			return "acos(" + expr.toStringRes(env) + ")|" + res.ufp() + "," + val + "|";
		} else
			return "acos(" + expr.toStringRes(env) + ")|" + val + "|";
	}

	public String toStringFix(Map<String, Integer> env) {
		Integer f = env.get(AbstractClass.prefix + "lab" + lab);
		Integer f1 = env.get(AbstractClass.prefix + "lab" + expr.lab);
		Integer val = getFixPrec(env);
		Integer valLeft = expr.getFixPrec(env);
		String s = "fx_xtox(" + expr.toStringFix(env) + "," + valLeft + "," + val + ")";
		// String s = "fx_xtox("+expr.toStringFix(env)+","+f1+","+f+")";
		// return "fx_cosx(" + s + "," + f+ ")";
		String ss = "POPxxx = " + s + ";\n";
		String sss = "fx_subx(fx_divx(3.141592653589,fx_dtox(2," + f + ")," + f + "), POPxxx);";
		return ss + sss;
	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}

	public Intervalle getInterv() {
		return null;
	}

	public String toStringOriginal() {
		return "gmpy2.acos(" + expr.toStringOriginal() + ")";
	}

	public String toStringPython(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		return "gmpy2.acos(" + expr.toStringPython(env) + ")";
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
		cacos.add(cst);
		ConstraintsSet.allCst.addAll(cacos);
	}

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		String acce = "acce" + "_lab" + lab;
		z3ConstInt.add(acce);
		Constraints cst = new Constraints(acce, new CstrConst(prec), ">=");
		cacos.add(cst);
		ConstraintsSet.allCst.addAll(cacos);
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
		cacosAC.add(add_ACL);
		ConstraintsSet.allCst.addAll(cacosAC);
	}

}
