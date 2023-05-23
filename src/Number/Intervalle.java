package Number;

import java.util.*;
import Constraint.*;
import Expression.AbstractClass;
import Util.Environment;

public class Intervalle extends AbstractClass {
	ArrayList<Constraints> cinterv = new ArrayList<Constraints>();
	ArrayList<Constraints> cintervAC = new ArrayList<Constraints>();

	public Intervalle(Numbers xmi, Numbers xma) {
		xmin = xmi;
		xmax = xma;
		x = new DoubleNumbers(0.0);
		lab = newLab();

	}

	public Intervalle(Numbers xmi, Numbers xma, int l) {
		xmin = xmi;
		xmax = xma;
		x = new DoubleNumbers((double) ((xmi.getDoubleValue() + xma.getDoubleValue()) / 2.0));
		lab = l;

	}

	public Intervalle(Numbers xmi, Numbers xma, int opPrec, int l) {
		xmin = xmi;
		xmax = xma;
		xmin.absCancelPrec = opPrec;
		xmax.absCancelPrec = opPrec;
		absCancelPrec = opPrec;
		x = new DoubleNumbers((double) ((xmi.getDoubleValue() + xma.getDoubleValue()) / 2.0));
		lab = l;

	}

	public Numbers abs(Numbers x) {
		Numbers y = new DoubleNumbers(-1.0);
		if (x.getDoubleValue() < 0.0)
			return x.multiply(y);
		else
			return x;

	}

	public int max(int xmin, int xmax) {
		if (xmin > xmax)
			return xmin;
		else
			return xmax;

	}

	public int min(int xmin, int xmax) {
		if (xmin < xmax)
			return xmin;
		else
			return xmax;

	}

	public static Numbers min(Numbers a, Numbers b) {
		if (a.LESS(b))
			return a;
		else
			return b;

	}

	public static Numbers max(Numbers a, Numbers b) {
		if (a.GREATER(b))
			return a;
		else
			return b;

	}

	public int ufp() {
		return max(xmin.ufp(), xmax.ufp());

	}

	public int ufpErr() {
		return max(xmin.ufpErr(), xmax.ufpErr());
	}

	public int ulp() {
		return min(xmin.ulp(), xmax.ulp());

	}

	public void join(Numbers xx) {
		if (xx.getDoubleValue() < xmin.getDoubleValue()) {
			xmin = xx;
		}
		if (xx.getDoubleValue() > xmax.getDoubleValue()) {
			xmax = xx;
		}
		x = xx;

	}

	public Numbers evaluate(Environment env) {
		Numbers i = new DoubleNumbers(0);
		x = i.Rand(xmin, xmax);
		return x;

	}

	public Numbers getSup() {
		if (xmin.getDoubleValue() > xmax.getDoubleValue())
			return xmin;
		else
			return xmax;

	}

	public Numbers getInf() {
		if (xmin.getDoubleValue() < xmax.getDoubleValue())
			return xmin;
		else
			return xmax;

	}

	public void Generate_constraints(Map<String, String> varEnv) {
		String accf = "accf_lab" + lab;
		String acc = "acc_lab" + lab;
		AbstractClass.z3ConstInt.add(accf);
		AbstractClass.z3ConstInt.add(acc);
		CstrConst p = new CstrConst(prec);
		Constraints cst = new Constraints(accf, p, "<=");
		cinterv.add(cst);
		/*** nbe ***/
		String nbe = "nbe_lab" + lab;
		AbstractClass.z3ConstInt.add(nbe);
		Constraints cst2 = new Constraints(nbe, new CstrConst(1), "=");
		cinterv.add(cst2);
		/** ulp **/
		String ulpeL1 = "ulpe_lab" + lab;
		AbstractClass.z3ConstInt.add(ulpeL1);
		String ulp = "ulp_lab" + lab;
		AbstractClass.z3ConstInt.add(ulp);
		ExpressionConstraint ulpl = new CstrMinus(new CstrConst(this.ufp()), new CstrConst(prec));
		Constraints cst1 = new Constraints(ulp, ulpl, "=");
		cinterv.add(cst1);
		Constraints cstulpe = new Constraints(ulpeL1, ulpl, "<=");
		cinterv.add(cstulpe);
		/* acc<accf */
		Constraints ineq1 = new Constraints(acc, new CstrId(accf), "<=");
		cinterv.add(ineq1);
		ConstraintsSet.allCst.addAll(cinterv);

	}

	public String symbol() {
		return null;

	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {
		String accb = "accb_lab" + lab;
		AbstractClass.z3ConstInt.add(accb);
		/* accb<acc */
		String acc = "acc_lab" + lab;
		z3ConstInt.add(acc);
		Constraints ineq2 = new Constraints(accb, new CstrId(acc), "<=");
		cinterv.add(ineq2);
	}

	public String toString() {
		return x.toString();

	}

	public String toStringInt() {
		return x.toStringInt();

	}

	public String toStringLab() {
		return "[" + xmin.toStringLab() + "," + xmax.toStringLab() + "]" + "|" + lab + "|";
	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		String a = xmin.toString();
		String b = xmax.toString();
		String res;
		if (a.compareTo(b) == 0)
			res = a;
		else
			res = "[" + a + "," + b + "]";
		if (fixedMode != 0) {
			return res + "|" + x.ufp() + "," + val + "|";
		} else
			return res + "|" + val + "|";
	}

	public String toStringFix(Map<String, Integer> env) {
		Integer val = env.get(AbstractClass.prefix + "lab" + lab);
		String a = xmin.toStringFix(env);
		String b = xmax.toStringFix(env);
		String res;
		if (a.compareTo(b) == 0)
			res = a;
		else
			res = "[" + a + "," + b + "]";
		return res;
	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}

	public Intervalle getInterv() {
		return this;
	}

	public Intervalle meet(Intervalle i2) {
		return new Intervalle(max(xmin, i2.xmin), min(xmax, i2.xmax));

	}

	public String toStringOriginal() {
		Numbers res = ((xmax.add(xmin)).divide(new DoubleNumbers(2)));
		return "mpfr(" + (res.getDoubleValue()) + "," + precMpfr + ")";
	}

	public String toStringPython(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		Numbers res = ((xmax.add(xmin)).divide(new DoubleNumbers(2)));
		return "mpfr(" + (res.getDoubleValue()) + "," + val + ")";

	}

	/* add, multiply, subtract Intervalle */
	public Intervalle add(Intervalle i1, Intervalle i2) {
		Intervalle res = new Intervalle(xmin, xmax);
		res.xmin = i1.xmin.add(i2.xmin);
		res.xmax = i1.xmin.add(i2.xmax);
		return res;
	}

	public Intervalle subtract(Intervalle i1, Intervalle i2) {
		Intervalle res = new Intervalle(xmin, xmax);
		res.xmin = i1.xmin.subtract(i2.xmin);
		res.xmax = i1.xmin.subtract(i2.xmax);
		return res;
	}

	public static Numbers min4(Numbers a, Numbers b, Numbers c, Numbers d) {
		Numbers min1 = min(a, b);
		Numbers min2 = min(c, d);
		return min(min1, min2);
	}

	public static Numbers max4(Numbers a, Numbers b, Numbers c, Numbers d) {
		Numbers max1 = max(a, b);
		Numbers max2 = max(c, d);
		return max(max1, max2);

	}

	public Intervalle multiply(Intervalle i1, Intervalle i2) {
		Intervalle res = new Intervalle(xmin, xmax);
		res.xmin = min4(i1.xmin.multiply(i2.xmin), i1.xmin.multiply(i2.xmax), i1.xmax.multiply(i2.xmin),
				i1.xmax.multiply(i2.xmax));
		res.xmax = max4(i1.xmin.multiply(i2.xmin), i2.xmin.multiply(i2.xmax), i1.xmax.multiply(i2.xmin),
				i1.xmax.multiply(i2.xmax));
		return res;
	}

	public double getDoubleValue() {
		return (double) (xmax.getDoubleValue() - xmin.getDoubleValue()) / 2.0;
	}

	public void Generate_Constraints_GLPK(Map<String, String> varEnv) {
		String accd = "accb_lab" + lab;
		AbstractClass.z3ConstInt.add(accd);
		CstrConst p = new CstrConst(prec);
		Constraints cst = new Constraints(accd, p, ">=");
		// cinterv.add(cst);

	}

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		String acce = "acce_lab" + lab;
		AbstractClass.z3ConstInt.add(acce);
		Constraints cst = new Constraints(acce, new CstrConst(0), ">=");
		cinterv.add(cst);
		ConstraintsSet.allCst.addAll(cinterv);
	}

	public void sameLabel() {
		xmin.lab = lab;
		xmax.lab = lab;
	}

	public void Generate_Constraints_AC(Map<String, String> varEnv) {
		Integer u = ufp();
		String ulp = "ulp_lab" + lab;
		AbstractClass.z3ConstInt.add(ulp);
		CstrConst p = new CstrConst(u - absCancelPrec + 1);
		Constraints cst = new Constraints(ulp, p, "<="); // <= ou >= ou rien ????
		cintervAC.add(cst);
		ConstraintsSet.allCst.addAll(cintervAC);
	}

}