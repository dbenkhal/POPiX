package Expression;

import java.util.*;
import Constraint.*;
import Number.*;
import Util.Environment;

public class Abs extends AbstractClass {

	Numbers valeur;
	Numbers res;
	ArrayList<Constraints> cconst = new ArrayList<Constraints>();
	ArrayList<Constraints> cconstAC = new ArrayList<Constraints>();
	Intervalle intervalleValue = new Intervalle(new DoubleNumbers(Double.POSITIVE_INFINITY),
			new DoubleNumbers(Double.NEGATIVE_INFINITY));

	public Abs(Numbers val) {
		valeur = val;
		lab = newLab();
		absCancelPrec = valeur.absCancelPrec;
		nbVarSupplement++;

	}

	public Numbers evaluate(Environment env) {
		xmax = res = valeur;
		if (valeur.LESS(new IntNumbers(0))) {
			valeur.multiply(new IntNumbers(-1));
		}
		return valeur;

	}

	public String symbol() {
		return "" + valeur;
	}

	public String toString() {
		return "" + valeur;
	}

	public String toStringInt() {
		return "" + valeur.getIntValue();
	}

	public String toStringLab() {
		return valeur + "|" + lab + "| ";
	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get(AbstractClass.prefix + "lab" + lab);
		if (fixedMode != 0) {
			return valeur.toStringRes(env) + "|" + valeur.ufp() + "," + val + "|";
		} else
			return valeur.toStringRes(env) + "|" + val + "|";
	}

	public void Generate_constraints(Map<String, String> varEnv) {
		String accf = "accf_lab" + lab;
		AbstractClass.z3ConstInt.add(accf);
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		String ulp = "ulp" + lab;
		AbstractClass.z3ConstInt.add(ulp);
		String ulperr = "ulpe_lab" + lab;
		AbstractClass.z3ConstInt.add(ulperr);
		/*** nbe ***/
		String nbe = "nbe_lab" + lab;
		AbstractClass.z3ConstInt.add(nbe);
		Constraints cst2 = new Constraints(nbe, new CstrConst(1), "=");
		cconst.add(cst2);
		/** ulp **/
		CstrConst p = new CstrConst(prec);
		Numbers zero = new DoubleNumbers(0.0);
		String ufp = String.valueOf(zero.ufp());
		AbstractClass.z3ConstInt.add(ufp);
		String ullp = String.valueOf(zero.ulp());
		AbstractClass.z3ConstInt.add(ullp);
		/** ulp de la cst **//** xmax ? **/
		ExpressionConstraint ulpl = new CstrConst(intervalleValue.xmax.ulp());
		Constraints cst1 = new Constraints(ulp, ulpl, "<=");
		cconst.add(cst1);
		/** contrainte accf **/
		Constraints cst = new Constraints(accf, p, "=");
		cconst.add(cst);
		/** contrainte ulperreur */
		ExpressionConstraint ulperreur = new CstrConst(intervalleValue.xmax.ulpErrCst());
		Constraints cstulpe = new Constraints(ulperr, ulperreur, "<=");
		cconst.add(cstulpe);
		/* acc<accf */
		Constraints ineq1 = new Constraints(acc, new CstrId(accf), "<=");
		cconst.add(ineq1);

		ConstraintsSet.allCst.addAll(cconst);
	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {
		String accb = "accb_lab" + lab;
		AbstractClass.z3ConstInt.add(accb);
		/* accb<acc */
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(acc);
		Constraints ineq2 = new Constraints(accb, new CstrId(acc), "<=");
		cconst.add(ineq2);
	}

	public Intervalle getInterv() {
		return null;
	}

	public String toStringOriginal() {
		String s = "mpfr(" + valeur + "," + precMpfr + ")";
		return s;
	}

	public String toStringPython(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		String s = "mpfr(" + valeur + "," + val + ")";
		return s;
	}

	public void Generate_Constraints_GLPK(Map<String, String> varEnv) {
		String accd = "accb_lab" + lab;
		AbstractClass.z3ConstInt.add(accd);
		CstrConst p = new CstrConst(prec);
		Constraints cst = new Constraints(accd, p, "<="); // <= ou >= ou rien ????
		cconst.add(cst);
		// ConstraintsSet.allCst.addAll(cconst);

	}

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		String acce = "acce_lab" + lab;
		AbstractClass.z3ConstInt.add(acce);
		Constraints cst = new Constraints(acce, new CstrConst(0), ">=");
		cconst.add(cst);
		ConstraintsSet.allCst.addAll(cconst);

	}

	public void sameLabel() {
	}

	public void Generate_Constraints_AC(Map<String, String> varEnv) {
		Integer ufp = valeur.getufp();
		String unitLP = "ulp_lab" + lab;
		AbstractClass.z3ConstInt.add(unitLP);
		CstrConst p = new CstrConst(ufp - absCancelPrec);
		Constraints cst = new Constraints(unitLP, p, "<=");
		cconstAC.add(cst);
		ConstraintsSet.allCst.addAll(cconstAC);

	}

	public String toStringFix(Map<String, Integer> env) {
		return null;
	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}

}