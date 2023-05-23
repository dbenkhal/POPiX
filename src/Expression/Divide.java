package Expression;

import java.util.ArrayList;
import java.util.Map;
import Constraint.*;
import Number.*;
import Util.Environment;

public class Divide extends AbstractClass {

	int id;
	AbstractClass left;
	AbstractClass right;
	ArrayList<Constraints> cdiv = new ArrayList<Constraints>();
	ArrayList<Constraints> cdivAC = new ArrayList<Constraints>();
	Intervalle intervalleValue = new Intervalle(new DoubleNumbers(Double.POSITIVE_INFINITY),
			new DoubleNumbers(Double.NEGATIVE_INFINITY));
	Numbers res;

	public Divide(AbstractClass lt, AbstractClass rt) {
		left = lt;
		right = rt;
		lab = newLab();
		nbVarSupplement++;
		AbstractClass.opLabList.add(lab + "");
	}

	public Divide(AbstractClass lt, AbstractClass rt, int opPrec) {
		left = lt;
		right = rt;
		absCancelPrec = opPrec;
		lab = newLab();
		nbVarSupplement++;
		AbstractClass.opLabList.add(lab + "");
	}

	public Numbers evaluate(Environment env) {
		res = left.evaluate(env).divide(right.evaluate(env));
		intervalleValue.join(res);
		xmin = intervalleValue.xmin;
		xmax = intervalleValue.xmax;
		return res;

	}

	public String symbol() {
		return " /";

	}

	public String toString() {
		return left.toString() + symbol() + right.toString();

	}

	public String toStringLab() {
		return left.toStringLab() + symbol() + "|" + lab + "| " + right.toStringLab();

	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		if (fixedMode != 0) {
			return left.toStringRes(env) + symbol() + "|" + res.ufp() + "," + val + "|" + right.toStringRes(env);
		} else
			return left.toStringRes(env) + symbol() + "|" + val + "|" + right.toStringRes(env);
	}

	public String toStringFix(Map<String, Integer> env) {
		int val, frac;
		int f, f1, f2;
		// f1 = env.get(AbstractClass.prefix + "lab" + left.lab);
		// f2 = env.get(AbstractClass.prefix + "lab" + right.lab);
		// f = env.get(AbstractClass.prefix + "lab" + lab);
		f = getFixPrec(env);
		f1 = left.getFixPrec(env);
		f2 = right.getFixPrec(env);
		frac = f - f1 + f2; // see doc of fx_divx
		String sl = left.toStringFix(env);
		String sr = right.toStringFix(env);
		String s = "fx_divx(" + sl + "," + sr + "," + frac + ")";
		return s;

		// Integer val = getFixPrec(env);
		// String sl = "fx_xtox("+left.toStringFix(env)+","+f1+","+val+")";
		// String sr = "fx_xtox("+right.toStringFix(env)+","+f2+","+val+")";

	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}

	public void Generate_constraints(Map<String, String> varEnv) {
		String accmult = "acc_lab" + lab;
		z3ConstInt.add(accmult);
		left.Generate_constraints(varEnv);
		right.Generate_constraints(varEnv);
		/*** r1 ***/
		String r1 = "r1_lab" + lab;
		z3ConstInt.add(r1);
		String l1_sup = "ufp_l1_sup_lab" + lab;
		z3ConstInt.add(l1_sup);
		String l2_sup = "ufp_l2_sup_lab" + lab;
		z3ConstInt.add(l2_sup);
		String max_r = "max_lab" + lab;
		z3ConstInt.add(max_r);
		CstrConst ufpl1_sup = new CstrConst(left.xmax.ufp());
		CstrId accfl1 = new CstrId("accf_lab" + left.lab);
		z3ConstInt.add(accfl1.toString());
		CstrConst ufpl2_sup = new CstrConst(-right.xmin.ufp());
		CstrId accfl2 = new CstrId("accf_lab" + right.lab);
		z3ConstInt.add(accfl2.toString());
		Numbers div = new FloatNumbers(left.xmax.divide(right.xmax).getFloatValue());
		ExpressionConstraint r1_l = new CstrMinus(new CstrConst(div.ufp()),
				new CstrMax(new CstrMinus(new CstrPlus(ufpl1_sup, ufpl2_sup), accfl1),
						new CstrMinus(new CstrPlus(ufpl1_sup, ufpl2_sup), accfl2)));
		Constraints R1 = new Constraints(r1, r1_l, "=");
		cdiv.add(R1);
		/*** r2 ***/
		String r2 = "r2_lab" + lab;
		z3ConstInt.add(r2);
		String l1_inf = "ufpl1_inf_lab" + lab;
		z3ConstInt.add(l1_inf);
		String l2_inf = "ufpl2_inf_lab" + lab;
		z3ConstInt.add(l2_inf);
		CstrConst ufpl1_inf = new CstrConst(left.xmin.ufp());
		CstrConst ufpl2_inf = new CstrConst(-right.xmax.ufp());
		Numbers div2 = new FloatNumbers(left.xmin.divide(right.xmin).getFloatValue());
		ExpressionConstraint r2_l = new CstrMinus(new CstrConst(div2.ufp()),
				new CstrMax(new CstrMinus(new CstrPlus(ufpl1_inf, ufpl2_inf), accfl1),
						new CstrMinus(new CstrPlus(ufpl1_inf, ufpl2_inf), accfl2)));
		Constraints R2 = new Constraints(r2, r2_l, "=");
		cdiv.add(R2);
		/***** r3 ******/
		String r3 = "r3_lab" + lab;
		z3ConstInt.add(r3);
		Numbers div3 = new FloatNumbers(left.xmin.divide(right.xmax).getFloatValue());
		ExpressionConstraint r3_l = new CstrMinus(new CstrConst(div3.ufp()),
				new CstrMax(new CstrMinus(new CstrPlus(ufpl1_inf, ufpl2_sup), accfl1),
						new CstrMinus(new CstrPlus(ufpl1_inf, ufpl2_sup), accfl2)));
		Constraints R3 = new Constraints(r3, r3_l, "=");
		cdiv.add(R3);

		/****** r4 ******/
		String r4 = "r4_lab" + lab;
		z3ConstInt.add(r4);
		Numbers div4 = new FloatNumbers(left.xmax.divide(right.xmin).getFloatValue());
		ExpressionConstraint r4_l = new CstrMinus(new CstrConst(Math.abs(div4.ufp())),
				new CstrMax(new CstrMinus(new CstrPlus(ufpl1_sup, ufpl2_inf), accfl1),
						new CstrMinus(new CstrPlus(ufpl1_sup, ufpl2_inf), accfl2)));
		Constraints R4 = new Constraints(r4, r4_l, "=");
		cdiv.add(R4);
		/** iota new **/
		/* ulpe_left */
		String ulpeLeft = "ulpe_lab" + left.lab;
		AbstractClass.z3ConstInt.add(ulpeLeft);
		/* ulpe_right */
		String ulpeRight = "ulpe_lab" + right.lab;
		AbstractClass.z3ConstInt.add(ulpeRight);
		/* iotaGt new */
		String IotaGt = "IotaGt_lab" + lab;
		z3ConstInt.add(IotaGt);
		String ulpeL1 = "ulpe_lab" + left.xmin.lab;
		z3ConstInt.add(ulpeL1);
		String ulpeL2 = "ulpe_lab" + right.xmin.lab;
		z3ConstInt.add(ulpeL2);
		ExpressionConstraint right_ufperr = new CstrConst(-right.xmax.ufpErr());
		ExpressionConstraint left_ufperr = new CstrConst(left.xmax.ufpErr());
		ExpressionConstraint cst_ulp1 = new CstrIota(new CstrId(ulpeL1), right_ufperr);
		ExpressionConstraint cst_ulp2 = new CstrIota(new CstrId(ulpeL2), left_ufperr);
		Constraints iota_div = new Constraints(IotaGt, new CstrPlus(cst_ulp1, cst_ulp2), "=");
		cdiv.add(iota_div);
		/* ulpe_Res */
		String ulpeRes = "ulpe_lab" + lab;
		AbstractClass.z3ConstInt.add(ulpeRes);
		ExpressionConstraint ulpErrAdd = new CstrConst(intervalleValue.xmax.ufpErr());
		Constraints ulpErrAddCst = new Constraints(ulpeRes, ulpErrAdd, "<=");
		cdiv.add(ulpErrAddCst);
		/*** accF ***/
		String accF = "accf_lab" + lab;
		z3ConstInt.add(accF);
		ExpressionConstraint min1 = new CstrMin4(new CstrMinus(new CstrId(r1), new CstrId(IotaGt)),
				new CstrMinus(new CstrId(r2), new CstrId(IotaGt)), new CstrMinus(new CstrId(r3), new CstrId(IotaGt)),
				new CstrMinus(new CstrId(r4), new CstrId(IotaGt)));
		Constraints accfor = new Constraints(accF, min1, "<=");
		cdiv.add(accfor);
		/** nbe ***/
		String nbe = "nbe_lab" + lab;
		z3ConstInt.add(nbe);
		ExpressionConstraint ab = new CstrMinus(
				new CstrMinus(new CstrConst(intervalleValue.xmax.ufp()), new CstrId(accF)), new CstrId(ulpeRes));
		Constraints nbee = new Constraints(nbe, ab, "=");
		cdiv.add(nbee);
		/* acc<accf */
		Constraints ineq1 = new Constraints(accmult, new CstrId(accF), "<=");
		cdiv.add(ineq1);
		ConstraintsSet.allCst.addAll(cdiv);

	}

	public void Generate_constraints_Backward(Map<String, String> varEnv) {
		left.Generate_constraints_Backward(varEnv);
		right.Generate_constraints_Backward(varEnv);
		String accmult = "acc_lab" + lab;
		z3ConstInt.add(accmult);
		/** s1 **/
		String s1 = "s1_lab" + left.lab;
		z3ConstInt.add(s1);
		CstrConst ufpls1 = new CstrConst(left.xmax.ufp());
		CstrConst ufpls2 = new CstrConst(-right.xmin.ufp());
		Numbers l_sup = new FloatNumbers(left.xmax.divide(right.xmax).getFloatValue());
		CstrConst ufplsup = new CstrConst(l_sup.ufp());
		CstrId accbl = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accbl.toString());
		CstrId accfl2 = new CstrId("accf_lab" + right.lab);
		z3ConstInt.add(accfl2.toString());
		ExpressionConstraint s = new CstrMinus(ufpls1,
				new CstrMax(new CstrMinus(new CstrPlus(new CstrMinus(ufplsup, ufpls2), new CstrConst(1)), accbl),
						new CstrMinus(new CstrMinus(ufplsup, ufpls2), accfl2)));
		Constraints S1 = new Constraints(s1, s, "=");
		cdiv.add(S1);
		/** s2 **/
		String s2 = "s2_lab" + left.lab;
		z3ConstInt.add(s2);
		CstrConst ufpsls1 = new CstrConst(left.xmin.ufp());
		CstrConst ufplsinf = new CstrConst(-right.xmax.ufp());
		/** changement **/
		Numbers l_inf = new FloatNumbers(left.xmin.divide(right.xmin).getFloatValue());
		CstrConst ufpsls2 = new CstrConst(l_inf.ufp());
		CstrId accbl2 = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accbl2.toString());
		CstrId accfl22 = new CstrId("accf_lab" + right.lab);
		z3ConstInt.add(accfl22.toString());
		ExpressionConstraint sa = new CstrMinus(ufpsls1,
				new CstrMax(new CstrMinus(new CstrPlus(new CstrMinus(ufpsls2, ufplsinf), new CstrConst(1)), accbl2),
						new CstrMinus(new CstrMinus(ufpsls2, ufplsinf), accfl22)));
		Constraints S2 = new Constraints(s2, sa, "=");
		cdiv.add(S2);
		/** s3 **/
		String s3 = "s3_lab" + left.lab;
		z3ConstInt.add(s3);
		CstrConst ufpsls3 = new CstrConst(left.xmin.ufp());
		CstrConst ufplssup = new CstrConst(-right.xmin.ufp());
		CstrId accbls3 = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accbls3.toString());
		CstrId accfls3 = new CstrId("accf_lab" + right.lab);
		z3ConstInt.add(accfls3.toString());
		ExpressionConstraint saa = new CstrMinus(ufpsls3,
				new CstrMax(new CstrMinus(new CstrPlus(new CstrMinus(ufpsls2, ufplssup), new CstrConst(1)), accbls3),
						new CstrMinus(new CstrMinus(ufpsls2, ufplssup), accfls3)));
		Constraints S3 = new Constraints(s3, saa, "=");
		cdiv.add(S3);
		/** s4 **/
		String s4 = "s4_lab" + left.lab;
		z3ConstInt.add(s4);
		CstrConst ufpls12 = new CstrConst(left.xmax.ufp());
		CstrConst ufplssupp = new CstrConst(-right.xmax.ufp());
		CstrId accb = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accb.toString());
		CstrId accs4 = new CstrId("accf_lab" + right.lab);
		z3ConstInt.add(accs4.toString());
		ExpressionConstraint saaa = new CstrMinus(ufpls12,
				new CstrMax(new CstrMinus(new CstrPlus(new CstrMinus(ufplsup, ufplssupp), new CstrConst(1)), accb),
						new CstrMinus(new CstrMinus(ufplsup, ufplssupp), accs4)));
		Constraints S4 = new Constraints(s4, saaa, "=");
		cdiv.add(S4);
		/** accB **/
		String accB = "accb_lab" + left.lab;
		z3ConstInt.add(accB);
		ExpressionConstraint max_b = new CstrMax4(new CstrId(s1), new CstrId(s2), new CstrId(s3), new CstrId(s4));
		Constraints accbak = new Constraints(accB, max_b, ">=");
		cdiv.add(accbak);
		/* accb<acc */
		String acc = "acc_lab" + lab;
		z3ConstInt.add(acc);
		Constraints ineq3 = new Constraints(accB, new CstrId(acc), "<=");
		cdiv.add(ineq3);
		ConstraintsSet.allCst.addAll(cdiv);
		/***********************************************************************************************/
		/** s1 **/
		String s1l2 = "s1l2_lab" + right.lab;
		z3ConstInt.add(s1l2);
		CstrConst ufpls1l2 = new CstrConst(-right.xmin.ufp());
		CstrConst ufpls2l1 = new CstrConst(left.xmax.ufp());
		CstrId accbll = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accbll.toString());
		CstrId accfl1 = new CstrId("accf_lab" + left.lab);
		z3ConstInt.add(accfl1.toString());
		ExpressionConstraint sl2 = new CstrMinus(ufpls1l2,
				new CstrMax(new CstrMinus(new CstrPlus(new CstrMinus(ufplsup, ufpls2l1), new CstrConst(1)), accbll),
						new CstrMinus(new CstrMinus(ufplsup, ufpls2l1), accfl1)));
		Constraints S1l2 = new Constraints(s1l2, sl2, "=");
		cdiv.add(S1l2);
		/** s2 **/
		String s2l2 = "s2l2_lab" + right.lab;
		z3ConstInt.add(s2l2);
		CstrConst ufpsls1l2 = new CstrConst(-right.xmax.ufp());
		CstrConst ufplsinfl2 = new CstrConst(left.xmin.ufp());
		CstrId accblll = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accblll.toString());
		ExpressionConstraint sal2 = new CstrMinus(ufpsls1l2,
				new CstrMax(new CstrMinus(new CstrPlus(new CstrMinus(ufpsls2, ufplsinfl2), new CstrConst(1)), accblll),
						new CstrMinus(new CstrMinus(ufpsls2, ufplsinfl2), accfl1)));
		Constraints S2l2 = new Constraints(s2l2, sal2, "=");
		cdiv.add(S2l2);
		/** s3 **/
		String s3l2 = "s3l2_lab" + right.lab;
		z3ConstInt.add(s3l2);
		CstrConst ufpl3 = new CstrConst(-right.xmax.ufp());
		CstrConst ufpl1sup = new CstrConst(left.xmax.ufp());
		CstrId accbll3 = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accbll3.toString());
		CstrId accfl3 = new CstrId("accf_lab" + left.lab);
		z3ConstInt.add(accfl3.toString());
		ExpressionConstraint s33 = new CstrMinus(ufpl3,
				new CstrMax(new CstrMinus(new CstrPlus(new CstrMinus(ufpsls2, ufpl1sup), new CstrConst(1)), accbll3),
						new CstrMinus(new CstrMinus(ufpsls2, ufpl1sup), accfl3)));
		Constraints Slb = new Constraints(s3l2, s33, "=");
		cdiv.add(Slb);
		/** s4 **/
		String s4l2 = "s4l2_lab" + right.lab;
		z3ConstInt.add(s4l2);
		CstrConst ufpl4 = new CstrConst(-right.xmin.ufp());
		CstrConst ufpl1inf = new CstrConst(left.xmin.ufp());
		CstrId accbl4 = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accbl4.toString());
		CstrId accfl4 = new CstrId("accf_lab" + left.lab);
		z3ConstInt.add(accfl4.toString());
		ExpressionConstraint s44 = new CstrMinus(ufpl4,
				new CstrMax(new CstrMinus(new CstrPlus(new CstrMinus(ufplsup, ufpl1inf), new CstrConst(1)), accbl4),
						new CstrMinus(new CstrMinus(ufplsup, ufpl1inf), accfl4)));
		Constraints Slbb = new Constraints(s4l2, s44, "=");
		cdiv.add(Slbb);
		/** accB **/
		String accB2 = "accb_lab" + right.lab;
		z3ConstInt.add(accB2);
		ExpressionConstraint max_b2 = new CstrMax4(new CstrId(s1l2), new CstrId(s2l2), new CstrId(s3l2),
				new CstrId(s4l2));
		Constraints accbak2 = new Constraints(accB2, max_b2, ">=");
		cdiv.add(accbak2);
		/* accb<acc */
		Constraints ineq4 = new Constraints(accB2, new CstrId(acc), "<=");
		cdiv.add(ineq4);
		ConstraintsSet.allCst.addAll(cdiv);
		/**** and /or */

		/*
		 * CstrId accfLeft = new CstrId("accf_lab" + left.lab);
		 * z3ConstInt.add(accfLeft.toString()); String accLeft = "acc_lab" + left.lab;
		 * z3ConstInt.add(accLeft); CstrId accbLeft = new CstrId("accb_lab" + left.lab);
		 * z3ConstInt.add(accbLeft.toString());
		 * 
		 * CstrId accfRight = new CstrId("accf_lab" + right.lab);
		 * z3ConstInt.add(accfRight.toString()); CstrId accbRight = new
		 * CstrId("accb_lab" + right.lab); z3ConstInt.add(accbRight.toString()); String
		 * accRight = "acc_lab" + right.lab; z3ConstInt.add(accRight);
		 * 
		 * CstrAnd and1 = new CstrAnd(new CstrLte(new CstrId(accLeft), accfLeft), new
		 * CstrGeq(new CstrId(accRight), accbRight)); CstrAnd and2 = new CstrAnd(new
		 * CstrLte(new CstrId(accRight), accfRight), new CstrGeq(new CstrId(accLeft),
		 * accbLeft)); CstrOr or = new CstrOr(and1, and2); clogic.add(or);
		 * ConstraintsSet.allCst.addAll(clogic);
		 */

		/* MIN-MAX CONSTRAINTS FOR POLICY ITERATIONS */
		String accfLeft = "accf_lab" + left.lab;
		z3ConstInt.add(accfLeft.toString());
		String accLeft = "acc_lab" + left.lab;
		z3ConstInt.add(accLeft);
		String accbLeft = "accb_lab" + left.lab;
		z3ConstInt.add(accbLeft.toString());
		String accfRight = "accf_lab" + right.lab;
		z3ConstInt.add(accfRight.toString());
		String accbRight = "accb_lab" + right.lab;
		z3ConstInt.add(accbRight.toString());
		String accRight = "acc_lab" + right.lab;
		z3ConstInt.add(accRight);
		String aux = "varIntermediaire" + lab;
		z3ConstInt.add(aux);
		Constraints Cstr1 = new Constraints(aux, new CstrMax(new CstrId(accLeft), new CstrId(accRight)), ">=");
		Constraints Cstr2 = new Constraints(aux, new CstrMin(new CstrId(accfLeft), new CstrId(accfRight)), ">=");
		clogic.add(Cstr1);
		clogic.add(Cstr2);
		String aux2 = "varIntermediaire" + lab;
		z3ConstInt.add(aux2);
		Constraints Cstr3 = new Constraints(aux2, new CstrMax(new CstrId(accLeft), new CstrId(accLeft)), ">=");
		clogic.add(Cstr3);
		Constraints Cstr4 = new Constraints(aux2,
				new CstrMin(new CstrId(accfLeft), new CstrTimes(new CstrId(accbLeft), new CstrConst(-1))), ">=");
		clogic.add(Cstr4);
		String aux3 = "varIntermediaire" + lab;
		z3ConstInt.add(aux3);
		Constraints Cstr5 = new Constraints(aux3, new CstrMax(new CstrId(accRight), new CstrId(accRight)), ">=");
		clogic.add(Cstr5);
		Constraints Cstr6 = new Constraints(aux3,
				new CstrMin(new CstrId(accbRight), new CstrTimes(new CstrId(accfRight), new CstrConst(-1))), ">=");
		clogic.add(Cstr6);

		String aux4 = "varIntermediaire" + lab;
		z3ConstInt.add(aux4);
		Constraints Cstr7 = new Constraints(aux4, new CstrMax(new CstrId(accbRight), new CstrId(accbLeft)), ">=");
		clogic.add(Cstr7);
		Constraints Cstr8 = new Constraints(aux4, new CstrMin(new CstrId(accLeft), new CstrId(accRight)), ">=");
		clogic.add(Cstr8);

		ConstraintsSet.allCst.addAll(clogic);
	}

	public Intervalle getInterv() {
		return null;
	}

	public String toStringOriginal() {
		String sl = left.toStringOriginal();
		String sr = right.toStringOriginal();
		String s = "mpfr(" + sl + "/" + sr + "," + precMpfr + ")";
		return s;
	}

	public String toStringPython(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		String sl = left.toStringPython(env);
		String sr = right.toStringPython(env);
		String s = "mpfr(" + sl + "/" + sr + "," + val + ")";
		return s;
	}

	public String toStringInt() {
		return left.toStringInt() + symbol() + right.toStringInt();

	}

	public void Generate_Constraints_GLPK(Map<String, String> varEnv) {
		left.Generate_Constraints_GLPK(varEnv);
		right.Generate_Constraints_GLPK(varEnv);
		String accd = "accb" + "_lab" + lab;
		z3ConstInt.add(accd);
		String accdLeft = "accb" + "_lab" + left.lab;
		AbstractClass.z3ConstInt.add(accdLeft);
		String accdRight = "accb" + "_lab" + right.lab;
		z3ConstInt.add(accdRight);
		String iota = "iota" + lab;
		z3ConstInt.add(iota);
		if (iotaMode == 0) {
			Constraints iotaCstr = new Constraints(iota, new CstrConst(1), ">=");
			cdiv.add(iotaCstr);
		}
		Constraints accdL = new Constraints(accdLeft,
				new CstrMinus(new CstrPlus(new CstrId(accd), new CstrId(iota)), new CstrConst(1)), ">=");
		cdiv.add(accdL);
		Constraints accdR = new Constraints(accdRight,
				new CstrMinus(new CstrPlus(new CstrId(accd), new CstrId(iota)), new CstrConst(1)), ">=");
		cdiv.add(accdR);
		ConstraintsSet.allCst.addAll(cdiv);

	}

	public void Generate_Constraints_GLPK_Prime(Map<String, String> varEnv) {
		left.Generate_Constraints_GLPK_Prime(varEnv);
		right.Generate_Constraints_GLPK_Prime(varEnv);
		String accd = "accb" + "_lab" + lab;
		z3ConstInt.add(accd);
		String accdLeft = "accb" + "_lab" + left.lab;
		z3ConstInt.add(accdLeft);
		String accdRight = "accb" + "_lab" + right.lab;
		z3ConstInt.add(accdRight);
		String acce = "acce" + "_lab" + lab;
		z3ConstInt.add(acce);
		String acceLeft = "acce" + "_lab" + left.lab;
		z3ConstInt.add(acceLeft);
		String acceRight = "acce" + "_lab" + right.lab;
		z3ConstInt.add(acceRight);
		ExpressionConstraint aux1 = new CstrPlus(new CstrPlus(new CstrId(accdLeft), new CstrId(acceLeft)),
				new CstrId(acceRight));
		Constraints cstr1 = new Constraints(acce, new CstrMinus(aux1, new CstrConst(2)), ">=");
		cdiv.add(cstr1);
		ExpressionConstraint aux2 = new CstrPlus(new CstrPlus(new CstrId(accdRight), new CstrId(acceRight)),
				new CstrId(acceLeft));
		Constraints cstr2 = new Constraints(acce, new CstrMinus(aux2, new CstrConst(2)), ">=");
		cdiv.add(cstr2);
		ConstraintsSet.allCst.addAll(cdiv);

	}

	public void sameLabel() {
		if (sameLab != 0) {
			left.lab = lab;
			right.lab = lab;
			left.sameLabel();
			right.sameLabel();
		}

	}

	public void Generate_Constraints_AC(Map<String, String> varEnv) {
		left.Generate_Constraints_AC(varEnv);
		right.Generate_Constraints_AC(varEnv);
		String ulp = "ulp_lab" + lab;
		AbstractClass.z3ConstInt.add(ulp);
		Constraints add_ACL = new Constraints(ulp, new CstrId("ulp_lab" + left.lab), "<=");
		cdivAC.add(add_ACL);
		Constraints add_ACR = new Constraints(ulp, new CstrId("ulp_lab" + right.lab), "<=");
		cdivAC.add(add_ACR);
		ConstraintsSet.allCst.addAll(cdivAC);

	}

}