package Expression;

import java.util.*;
import Constraint.*;
import Number.*;
import Util.Environment;

public class Add extends AbstractClass {

	AbstractClass left;
	AbstractClass right;
	ArrayList<Constraints> cplus = new ArrayList<Constraints>();
	ArrayList<Constraints> cplusAC = new ArrayList<Constraints>();
	ArrayList<ConstraintsSet> clogic = new ArrayList<ConstraintsSet>();
	Intervalle intervalleValue = new Intervalle(new DoubleNumbers(Double.POSITIVE_INFINITY),
			new DoubleNumbers(Double.NEGATIVE_INFINITY));
	Numbers res;

	public Add(AbstractClass l, AbstractClass r) {
		left = l;
		right = r;
		lab = newLab();
		nbVarSupplement++;
		AbstractClass.opLabList.add(lab + "");
	}

	public Add(AbstractClass l, AbstractClass r, int opPrec) {
		left = l;
		right = r;
		absCancelPrec = opPrec;
		lab = newLab();
		nbVarSupplement++;
		AbstractClass.opLabList.add(lab + "");
	}

	public Numbers evaluate(Environment env) {
		res = left.evaluate(env).add(right.evaluate(env));
		intervalleValue.join(res);
		xmin = intervalleValue.xmin;
		xmax = intervalleValue.xmax;
		return res;
	}

	public void Generate_constraints(Map<String, String> varEnv) {
		String accplus = "acc_lab" + lab;
		z3ConstInt.add(accplus);
		left.Generate_constraints(varEnv);
		right.Generate_constraints(varEnv);

		/* rlsup */
		String rl = "rSup_lab" + lab;
		z3ConstInt.add(rl);
		String tmp = "ufpLSup_lab" + lab;
		z3ConstInt.add(tmp);
		String tmp2 = "max_lab" + lab;
		z3ConstInt.add(tmp2);
		CstrConst ufpL1Sup = new CstrConst(left.xmax.ufp());
		CstrId accfL1Sup = new CstrId("accf_lab" + left.lab);
		z3ConstInt.add(accfL1Sup.toString());
		CstrConst ufpL2Sup = new CstrConst(right.xmax.ufp());
		CstrId accfL2Sup = new CstrId("accf_lab" + right.lab);
		z3ConstInt.add(accfL2Sup.toString());
		Numbers tmp2_c = new FloatNumbers(intervalleValue.xmax.ufp());
		ExpressionConstraint abc = new CstrMinus(new CstrConst(tmp2_c),
				new CstrMax(new CstrMinus(ufpL1Sup, accfL1Sup), new CstrMinus(ufpL2Sup, accfL2Sup)));
		Constraints rlSup = new Constraints(rl, abc, "=");
		cplus.add(rlSup);
		/* r_inf */
		String rl2 = "rInf_lab" + lab;
		z3ConstInt.add(rl2);
		String tmp3 = "ufpLInf_lab" + lab;
		z3ConstInt.add(tmp3);
		String tmp4 = "min_lab" + lab;
		z3ConstInt.add(tmp4);
		Numbers tmp3_c = new FloatNumbers(intervalleValue.xmin.ufp());
		CstrConst ufpl1_inf = new CstrConst(left.xmin.ufp());
		CstrId accf1_inf = new CstrId("accf_lab" + left.lab);
		z3ConstInt.add(accf1_inf.toString());
		CstrConst ufpl2_inf = new CstrConst(right.xmin.ufp());
		CstrId accf2_inf = new CstrId("accf_lab" + right.lab);
		z3ConstInt.add(accf2_inf.toString());
		ExpressionConstraint abc2 = new CstrMinus(new CstrConst(tmp3_c),
				new CstrMax(new CstrMinus(ufpl1_inf, accf1_inf), new CstrMinus(ufpl2_inf, accf2_inf)));
		Constraints rl_inf = new Constraints(rl2, abc2, "=");
		cplus.add(rl_inf);
		/* ulpe_left */
		String ulpeLeft = "ulpe_lab" + left.lab;
		AbstractClass.z3ConstInt.add(ulpeLeft);
		/* ulpe_right */
		String ulpeRight = "ulpe_lab" + right.lab;
		AbstractClass.z3ConstInt.add(ulpeRight);
		/* iotaGt new */
		String IotaGt = "IotaGt_lab" + lab;
		z3ConstInt.add(IotaGt);
		String ulpeL1 = "ulpe_lab" + left.xmax.lab;
		z3ConstInt.add(ulpeL1);
		String ulpeL2 = "ulpe_lab" + right.xmax.lab;
		z3ConstInt.add(ulpeL2);
		ExpressionConstraint right_ufperr = new CstrConst(right.xmax.ufpErr());
		ExpressionConstraint left_ufperr = new CstrConst(left.xmax.ufpErr());
		ExpressionConstraint cst_ulp1 = new CstrIota(new CstrId(ulpeL1), right_ufperr);
		ExpressionConstraint cst_ulp2 = new CstrIota(new CstrId(ulpeL2), left_ufperr);
		Constraints iota_add = new Constraints(IotaGt, new CstrMax(cst_ulp1, cst_ulp2), "=");
		cplus.add(iota_add); // commenter car Unsat
		/* iotaGT_inf new */
		String IotaInf = "IotaInf_lab" + lab;
		z3ConstInt.add(IotaInf);
		String ulpeL1_inf = "ulpe_lab" + left.xmin.lab;
		z3ConstInt.add(ulpeL1_inf);
		String ulpeL2_inf = "ulpe_lab" + right.xmax.lab;
		z3ConstInt.add(ulpeL2_inf);
		ExpressionConstraint right_ufperr_inf = new CstrConst(right.xmin.ufpErr());
		ExpressionConstraint left_ufperr_inf = new CstrConst(left.xmin.ufpErr());
		ExpressionConstraint cst_ulpInf1 = new CstrIota(new CstrId(ulpeL1_inf), right_ufperr_inf);
		ExpressionConstraint cst_ulpInf2 = new CstrIota(new CstrId(ulpeL2_inf), left_ufperr_inf);
		Constraints iota_inf = new Constraints(IotaInf, new CstrMax(cst_ulpInf1, cst_ulpInf2), "=");
		cplus.add(iota_inf); // commenter car Unsat
		/* Acc_f */
		String accf = "accf_lab" + lab;
		z3ConstInt.add(accf);
		ExpressionConstraint min_c = new CstrMin(new CstrMinus(new CstrId(rl2), new CstrId(IotaInf)),
				new CstrMinus(new CstrId(rl), new CstrId(IotaGt)));
		Constraints accff = new Constraints(accf, min_c, "=");
		cplus.add(accff);
		/* ulpe_Res */
		String ulpeRes = "ulpe_lab" + lab;
		AbstractClass.z3ConstInt.add(ulpeRes);
		ExpressionConstraint ulpErrAdd = new CstrMin(new CstrId(ulpeLeft), new CstrId(ulpeRight));
		Constraints ulpErrAddCst = new Constraints(ulpeRes, ulpErrAdd, "<=");
		cplus.add(ulpErrAddCst);
		/** nbe resultat ***/
		String nbe = "nbe_lab" + lab;
		z3ConstInt.add(nbe);
		ExpressionConstraint nbits_erreur = new CstrMinus(new CstrConst(intervalleValue.ufpErr()), new CstrId(ulpeRes));
		Constraints nbee = new Constraints(nbe, nbits_erreur, "=");
		cplus.add(nbee);
		/* acc<accf */
		Constraints ineq1 = new Constraints(accplus, new CstrId(accf), "<=");
		cplus.add(ineq1);
		ConstraintsSet.allCst.addAll(cplus);
	}

	/* les contraintes en backward */

	public void Generate_constraints_Backward(Map<String, String> varEnv) {
		left.Generate_constraints_Backward(varEnv);
		right.Generate_constraints_Backward(varEnv);
		/* sl_sup */
		String sl1_sup = "slSup_lab" + right.lab;
		z3ConstInt.add(sl1_sup);
		CstrConst ufpsl1Sup = new CstrConst(left.xmax.ufp());
		CstrConst ufpL = new CstrConst(intervalleValue.xmax.ufp());
		// Numbers tmp2_c = new FloatNumbers(left.xmax.add(right.xmax).getFloatValue());
		CstrId accbl = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accbl.toString());
		// ExpressionConstraint ec22 = new CstrMinus(new CstrConst(tmp2_c.ufp()),
		// accbl);
		ExpressionConstraint ec22 = new CstrMinus(ufpL, accbl);
		Constraints sl1_supc = new Constraints(sl1_sup, new CstrMinus(ufpsl1Sup, ec22), "=");
		cplus.add(sl1_supc);
		/* slinf */
		String sl1_inf = "slInf_lab" + right.lab;
		z3ConstInt.add(sl1_inf);
		CstrConst ufpsl1Inf = new CstrConst(left.xmin.ufp());
		CstrConst ufpL2 = new CstrConst(intervalleValue.xmin.ufp());
		Numbers tmp3_c = new FloatNumbers(left.xmin.add(right.xmin).getFloatValue());
		CstrId accbl2 = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accbl2.toString());
		// ExpressionConstraint ec2252 = new CstrMinus(new CstrConst(tmp3_c.ufp()),
		// accbl2);
		ExpressionConstraint ec2252 = new CstrMinus(ufpL2, accbl2);
		Constraints sl1_infc = new Constraints(sl1_inf, new CstrMinus(ufpsl1Inf, ec2252), "=");
		cplus.add(sl1_infc);
		/* accb */
		String accb = "accb_lab" + right.lab;
		z3ConstInt.add(accb);
		// CstrId accbll = new CstrId(accb + right.lab);
		ExpressionConstraint max = new CstrMax(new CstrId(sl1_inf), new CstrId(sl1_sup));
		Constraints accbb = new Constraints(accb, max, ">=");
		cplus.add(accbb);
		/* accb<acc */
		String accplus = "acc_lab" + lab;
		z3ConstInt.add(accplus);
		Constraints ineq2 = new Constraints(accb, new CstrId(accplus), "<=");
		cplus.add(ineq2);
		/******************************************************************************/
		String sl1_supp = "slSup_lab" + left.lab;
		z3ConstInt.add(sl1_supp);
		CstrConst ufpsl1Sup2 = new CstrConst(right.xmax.ufp());
		CstrConst ufpL4 = new CstrConst(intervalleValue.xmax.ufp());
		CstrId accbl4 = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accbl.toString());
		ExpressionConstraint ec24 = new CstrMinus(ufpL4, accbl4);
		Constraints sl1_sup7 = new Constraints(sl1_supp, new CstrMinus(ufpsl1Sup2, ec24), "=");
		cplus.add(sl1_sup7);
		String sl1_inf22 = "slInf_lab" + left.lab;
		z3ConstInt.add(sl1_inf22);
		CstrConst ufpsl1Inf45 = new CstrConst(right.xmin.ufp());
		CstrConst ufpL25 = new CstrConst(intervalleValue.xmin.ufp());
		CstrId accbl26 = new CstrId("accb_lab" + lab);
		z3ConstInt.add(accbl2.toString());
		ExpressionConstraint ec25 = new CstrMinus(ufpL25, accbl26);
		Constraints sl1_inf3 = new Constraints(sl1_inf22, new CstrMinus(ufpsl1Inf45, ec25), "=");
		cplus.add(sl1_inf3);
		/* accb */
		String accb1 = "accb_lab" + left.lab;
		z3ConstInt.add(accb1);
		ExpressionConstraint max2 = new CstrMax(new CstrId(sl1_inf22), new CstrId(sl1_supp));
		Constraints accbL2 = new Constraints(accb1, max2, ">=");
		cplus.add(accbL2);
		/* accb<acc */
		Constraints ineq3 = new Constraints(accb1, new CstrId(accplus), "<=");
		cplus.add(ineq3);
		ConstraintsSet.allCst.addAll(cplus);

		/* AND / OR */
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

	public String symbol() {
		return " +";

	}

	public String toString() {
		return left.toString() + symbol() + right.toString();

	}

	public String toStringInt() {
		return left.toStringInt() + symbol() + right.toStringInt();

	}

	public String toStringLab() {
		return left.toStringLab() + symbol() + "|" + lab + "| " + right.toStringLab();

	}

	public String toStringRes(Map<String, Integer> env) {
		Integer val = env.get("accb_lab" + lab);
		if (fixedMode != 0) {
			return left.toStringRes(env) + symbol() + "|" + res.ufp() + "," + val + "| " + right.toStringRes(env);
		} else
			return left.toStringRes(env) + symbol() + "|" + val + "| " + right.toStringRes(env);
	}

	public Intervalle getInterv() {
		return null;
	}

	public String toStringOriginal() {
		String sl = left.toStringOriginal();
		String sr = right.toStringOriginal();
		String s = "mpfr(" + sl + "+" + sr + "," + precMpfr + ")";
		return s;
	}

	public String toStringPython(Map<String, Integer> env) {
		// Integer val = env.get(lab + "");
		Integer val = env.get("accb_lab" + lab);
		String sl = left.toStringPython(env);
		String sr = right.toStringPython(env);
		String s = "mpfr(" + sl + "+" + sr + "," + val + ")";
		return s;
	}

	public String toStringFix(Map<String, Integer> env) {
		Integer val = getFixPrec(env);
		Integer valLeft = left.getFixPrec(env);
		Integer valRight = right.getFixPrec(env);
		String sl, sr;
		if (valLeft != val)
			sl = "fx_xtox(" + left.toStringFix(env) + "," + valLeft + "," + val + ")";
		else
			sl = left.toStringFix(env);

		if (valRight != val)
			sr = "fx_xtox(" + right.toStringFix(env) + "," + valRight + "," + val + ")";
		else
			sr = right.toStringFix(env);
		String s = "fx_addx(" + sl + "," + sr + ")";
		return s;

	}

	public int getFixPrec(Map<String, Integer> env) {
		return env.get(AbstractClass.prefix + "lab" + lab);
	}

	public void Generate_Constraints_GLPK(Map<String, String> varEnv) {

		left.Generate_Constraints_GLPK(varEnv);
		right.Generate_Constraints_GLPK(varEnv);
		String accd = "accb" + "_lab" + lab;
		AbstractClass.z3ConstInt.add(accd);
		String accdLeft = "accb" + "_lab" + left.lab;
		AbstractClass.z3ConstInt.add(accdLeft);
		String accdRight = "accb" + "_lab" + right.lab;
		AbstractClass.z3ConstInt.add(accdRight);
		CstrConst ufpLeft = new CstrConst((Math.max(left.xmax.ufp(), left.xmin.ufp())));
		CstrConst ufpRight = new CstrConst((Math.max(right.xmax.ufp(), right.xmin.ufp())));
		CstrConst ufp = new CstrConst((Math.max(intervalleValue.xmax.ufp(), intervalleValue.xmin.ufp())));
		String iota = "iota" + lab;
		z3ConstInt.add(iota);
		if (iotaMode == 0) {
			Constraints iotaCstr = new Constraints(iota, new CstrConst(1), ">=");
			cplus.add(iotaCstr);
		}
		ExpressionConstraint aux1 = new CstrPlus(new CstrId(accd), ufpLeft);
		ExpressionConstraint aux2 = new CstrMinus(new CstrId(iota), ufp);
		Constraints accdL = new Constraints(accdLeft, new CstrPlus(aux1, aux2), ">=");
		cplus.add(accdL);
		ExpressionConstraint aux3 = new CstrPlus(new CstrId(accd), ufpRight);
		ExpressionConstraint aux4 = new CstrMinus(new CstrId(iota), ufp);
		Constraints accdR = new Constraints(accdRight, new CstrPlus(aux3, aux4), ">=");
		cplus.add(accdR);
		ConstraintsSet.allCst.addAll(cplus);

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
		CstrConst ufpLeft = new CstrConst((Math.max(left.xmax.ufp(), left.xmin.ufp())));
		CstrConst ufpRight = new CstrConst((Math.max(right.xmax.ufp(), right.xmin.ufp())));
		CstrConst ufp = new CstrConst((Math.max(intervalleValue.xmax.ufp(), intervalleValue.xmin.ufp())));
		String iota = "iota" + lab;
		z3ConstInt.add(iota);
		Constraints iotaCstr = new Constraints(iota,
				new CstrXi(
						new CstrPlus(new CstrMinus(ufpRight, ufpLeft),
								new CstrPlus(new CstrId(accdLeft), new CstrId(acceLeft))),
						new CstrPlus(new CstrMinus(ufpLeft, ufpRight),
								new CstrPlus(new CstrId(accdRight), new CstrId(acceRight)))),
				">=");
		cplus.add(iotaCstr);
		Constraints cstr1 = new Constraints(acce, new CstrId(acceLeft), ">=");
		cplus.add(cstr1);
		Constraints cstr2 = new Constraints(acce, new CstrId(acceRight), ">=");
		cplus.add(cstr2);
		ExpressionConstraint aux1 = new CstrMinus(ufpLeft, ufpRight);
		ExpressionConstraint aux2 = new CstrMinus(new CstrId(accdRight), new CstrId(accdLeft));
		Constraints cstr3 = new Constraints(acce,
				new CstrPlus(new CstrPlus(aux1, aux2), new CstrPlus(new CstrId(acceRight), new CstrId(iota))), ">=");
		cplus.add(cstr3);
		ExpressionConstraint aux3 = new CstrMinus(ufpRight, ufpLeft);
		ExpressionConstraint aux4 = new CstrMinus(new CstrId(accdLeft), new CstrId(accdRight));
		Constraints cstr4 = new Constraints(acce,
				new CstrPlus(new CstrPlus(aux3, aux4), new CstrPlus(new CstrId(acceLeft), new CstrId(iota))), ">=");
		cplus.add(cstr4);
		ConstraintsSet.allCst.addAll(cplus);

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
		cplusAC.add(add_ACL);
		Constraints add_ACR = new Constraints(ulp, new CstrId("ulp_lab" + right.lab), "<=");
		cplusAC.add(add_ACR);
		CstrConst p = new CstrConst(res.ufp() - absCancelPrec + 1);
		Constraints cst = new Constraints(ulp, p, "<=");
		cplusAC.add(cst);
		ConstraintsSet.allCst.addAll(cplusAC);

	}

}