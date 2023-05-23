package Constraint;

import java.util.*;
import Expression.AbstractClass;
import Statement.CreateArray;
import org.gnu.glpk.*;

public abstract class ConstraintsSet {

	String id;
	static public int globalLabCstr = 0;
	ExpressionConstraint ec;
	String symbol;
	static public glp_smcp parm;
	static public SWIGTYPE_p_int ind;
	static public SWIGTYPE_p_double val;
	static int[][] matrix = null;
	public static Map<String, Integer> varNumEnv = new HashMap<String, Integer>();
	public static Map<String, Integer> lpvar2numEnv = new HashMap<String, Integer>(
			AbstractClass.z3ConstInt.size() + AbstractClass.nbVarSupplement + Constraints.globalLabCstr + 1);
	static public glp_prob lp;
	static public int lpConstrCounter = 1;
	public static ArrayList<ConstraintsSet> allCst = new ArrayList<ConstraintsSet>();
	public static Map<String, ArrayList<ExpressionConstraint>> pol = new HashMap<String, ArrayList<ExpressionConstraint>>();

	public abstract String toString();

	public abstract String print();

	public abstract String getId();

	public abstract void setEc(ExpressionConstraint ec);

	public abstract ExpressionConstraint getEc();

	public abstract String symbol();

	public abstract void genLPConst();

	public abstract void simGenLPConst();

	public static void initGLPK() {

		if (AbstractClass.costFunction == 1) {
			String accd = AbstractClass.costFunction1MaxAcc;
			CstrConst p = new CstrConst(29);
			Constraints cst = new Constraints(accd, p, "<=");
			ArrayList<Constraints> liste = new ArrayList<Constraints>();
			liste.add(cst);
			ConstraintsSet.allCst.addAll(liste);
		}
		;

		// init and create problem
		int i = 1;
		int precMin;
//		if (AbstractClass.anaMode == 0) {
//			precMin = -1024;
//			AbstractClass.prec=1024;
//		} else {
		precMin = 0;
		// }
		AbstractClass.accBCount = 0;
		AbstractClass.z3ConstInt.add(AbstractClass.costFunction1MaxAcc);
		AbstractClass.arraySize2 = new HashMap<String, Integer>(CreateArray.arraySize);
		AbstractClass.arraySize3 = new HashMap<String, Integer>(CreateArray.arraySize);
		lp = GLPK.glp_create_prob();
		GLPK.glp_set_prob_name(lp, " IP Problem");
		// init variables/columns
		int tt = AbstractClass.z3ConstInt.size() + AbstractClass.nbVarSupplement + Constraints.allCst.size() * 2
				+ Constraints.globalLabCstr + 5;
		GLPK.glp_add_cols(lp, tt);
		AbstractClass.nbGLPKVars = AbstractClass.z3ConstInt.size();
		Iterator<String> it = AbstractClass.z3ConstInt.iterator();
		while (it.hasNext()) {
			String str = it.next();
			GLPK.glp_set_col_name(lp, i, str);
			GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_CV);
			// GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_IV);
			GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_DB, precMin, AbstractClass.prec);
			lpvar2numEnv.put(str, i);
			i++;
		}

		int i0 = i;
		while (i < tt) {
			GLPK.glp_set_col_name(lp, i, "lab" + (i - i0));
			// GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_CV); continuous variable
			GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_IV);
			GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_DB, -AbstractClass.prec, AbstractClass.prec);
			i++;
		}
		// Allocate memory
		ind = GLPK.new_intArray(tt);
		val = GLPK.new_doubleArray(tt);
		// Create rows
		int oldl = Constraints.globalLabCstr;
		Iterator<ConstraintsSet> iterator = Constraints.allCst.iterator();
		while (iterator.hasNext()) {
			ConstraintsSet cc = iterator.next();
			cc.simGenLPConst();
		}
		Constraints.globalLabCstr = oldl;
		GLPK.glp_add_rows(lp, ConstraintsSet.lpConstrCounter);
		AbstractClass.nbGLPKRows = Constraints.allCst.size();
		ConstraintsSet.lpConstrCounter = 1;
	}

	public Map<String, Integer> solveIPOnce() {
		initGLPK();
		int oldglobalLabCstr = globalLabCstr;
		Iterator<ConstraintsSet> iterator = Constraints.allCst.iterator();
		while (iterator.hasNext()) {
			ConstraintsSet cc = iterator.next();
			cc.genLPConst();
		}
		// Free memory
		GLPK.delete_intArray(ind);
		GLPK.delete_doubleArray(val);
		// Define objective
		GLPK.glp_set_obj_name(lp, "nbBits");
		int i = 1;
		Iterator<String> iterator2 = AbstractClass.z3ConstInt.iterator();

//		if (AbstractClass.anaMode == 0) {
//			GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);
//			while (iterator2.hasNext()) {
//				String s = iterator2.next();
//				if (s.startsWith("ulp_")) {
//					AbstractClass.accBCount++;
//					if (AbstractClass.arraySize3.containsKey(AbstractClass.acc2Id.get(s))) {
//						int n = AbstractClass.arraySize3.get(AbstractClass.acc2Id.get(s));
//						AbstractClass.arraySize3.put(AbstractClass.acc2Id.get(s), 0);
//						GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum(s), n);
//					} else {
//						GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum(s), 1.);
//					}
//					i++;
//				}
//			}
//
//		} else {

		if (AbstractClass.costFunction == 0) {
			GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);
		} else {
			GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);
		}

		if (AbstractClass.costFunction == 0) { // assigned variables
			while (iterator2.hasNext()) {
				String s = iterator2.next();
				if (s.startsWith("accb_")) {
					AbstractClass.accBCount++;
					if (AbstractClass.arraySize3.containsKey(AbstractClass.acc2Id.get(s))) {
						int n = AbstractClass.arraySize3.get(AbstractClass.acc2Id.get(s));
						AbstractClass.arraySize3.put(AbstractClass.acc2Id.get(s), 0);
						GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum(s), n);
					} else {
						GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum(s), 1.);
					}
					i++;
				}
			}
		} else if (AbstractClass.costFunction == 1) { // minimize max accb
			while (iterator2.hasNext()) {
				String s = iterator2.next();
				if (s.startsWith("accb_")) {
					AbstractClass.accBCount++;
					if (AbstractClass.arraySize3.containsKey(AbstractClass.acc2Id.get(s))) {
						int n = AbstractClass.arraySize3.get(AbstractClass.acc2Id.get(s));
						AbstractClass.arraySize3.put(AbstractClass.acc2Id.get(s), 0);
						GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum(s), n);
					} else {
						GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum(s), -1.);
					}
					i++;
				}
			}
		} else if (AbstractClass.costFunction == 2) { // # of casts
			while (iterator2.hasNext()) {
				String s = iterator2.next();
				if (s.startsWith("w")) {
					AbstractClass.accBCount++;
					if (AbstractClass.arraySize3.containsKey(AbstractClass.acc2Id.get(s))) {
						int n = AbstractClass.arraySize3.get(AbstractClass.acc2Id.get(s));
						AbstractClass.arraySize3.put(AbstractClass.acc2Id.get(s), 0);
						GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum(s), n);
					} else {
						GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum(s), -1.);
					}
					i++;
				}
			}
		} else if (AbstractClass.costFunction == 3) { // size of op
			Iterator<String> iterator3 = AbstractClass.opLabList.iterator();
			while (iterator3.hasNext()) {
				String s = iterator3.next();
				GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum("accb_lab" + s), -1.);
				i++;
			}
		} else if (AbstractClass.costFunction == 4) { // acc of assigned var
			Iterator<String> iterator3 = AbstractClass.accVars.iterator();
			while (iterator3.hasNext()) {
				String s = iterator3.next();
				AbstractClass.accBCount++;
				if (AbstractClass.arraySize3.containsKey(AbstractClass.acc2Id.get(s))) {
					int n = AbstractClass.arraySize3.get(AbstractClass.acc2Id.get(s));
					AbstractClass.arraySize3.put(AbstractClass.acc2Id.get(s), 0);
					GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum(s), -n);
				} else {
					GLPK.glp_set_obj_coef(lp, ExpressionConstraint.varName2VarNum(s), -1.);
				}
				i++;
			}
		} else {
			System.out.println("bad cost function!");
			System.exit(0);
		}

		// Solve model
		System.out.println("Starting GLPK...");

		parm = new glp_smcp();
		GLPK.glp_init_smcp(parm);
		int ret = GLPK.glp_simplex(lp, parm);

		Map<String, Integer> res = new HashMap<String, Integer>();
		if (ret == 0) {
			GLPK.glp_write_lp(lp, null, "glpk.pb");
			res = write_lp_solution(lp);
			AbstractClass.resultEnv = write_lp_solution2(lp);
		} else {
			System.out.println("GLPK: The problem could not be solved");
		}

		// Free memory
		globalLabCstr = oldglobalLabCstr;
		GLPK.glp_delete_prob(lp);
		return res;
	}

	public Map<String, Integer> write_lp_solution(glp_prob lp) {
		int i;
		int n;
		Map<String, Integer> GLPKSolEnv = new HashMap<String, Integer>();
		String name;
		int val;
		name = GLPK.glp_get_obj_name(lp);
		val = (int) GLPK.glp_get_obj_val(lp);
		GLPKSolEnv.put(name, val);
		n = GLPK.glp_get_num_cols(lp);
		for (i = 1; i <= n; i++) {
			name = GLPK.glp_get_col_name(lp, i);
			val = (int) GLPK.glp_get_col_prim(lp, i);
			GLPKSolEnv.put(name, val);

		}
		return GLPKSolEnv;
	}

	static Map<String, Integer> write_lp_solution2(glp_prob lp) {
		int i;
		int n;
		String name;
		int val;
		name = GLPK.glp_get_obj_name(lp);
		val = (int) GLPK.glp_get_obj_val(lp);
		n = GLPK.glp_get_num_cols(lp);
		for (i = 1; i <= n; i++) {
			name = GLPK.glp_get_col_name(lp, i);
			val = (int) GLPK.glp_get_col_prim(lp, i);
			AbstractClass.resultEnv.put(name, val);
		}
		return AbstractClass.resultEnv;
	}

	public boolean areEqual(Map<String, Integer> env) {
		boolean b = true;
		Iterator<ConstraintsSet> iterator = Constraints.allCst.iterator();
		while (b & iterator.hasNext()) {
			ConstraintsSet cc = iterator.next();
			b = ((Constraints) cc).isEqual(env);
		}
		return b;

	}

	public boolean myEquals(Map<String, Integer> env1, Map<String, Integer> env2) {
		Iterator<String> iterator = env1.keySet().iterator();
		String k = iterator.next();
		k = iterator.next();

		while (iterator.hasNext()) {
			if ((k != null) && (k.startsWith("accb_lab") || k.startsWith("w"))) {
				if (env1.get(k) == env2.get(k)) {
					k = iterator.next();
				} else
					return false;
			} else {
				k = iterator.next();
			}
		}
		return true;
	}

	public void solveIP() {
		int cpt = 0;
		Map<String, Integer> oldGLPKSolEnv = new HashMap<String, Integer>();
		Map<String, Integer> GLPKSolEnv = new HashMap<String, Integer>();
		GLPKSolEnv = solveIPOnce();
		boolean ae = this.areEqual(GLPKSolEnv); // x=f(x) ?
		boolean me = myEquals(GLPKSolEnv, oldGLPKSolEnv); // old = new env?
		boolean stop = ae | me | (AbstractClass.iotaMode == 0);
		int nbIt = 0;
		while (!stop) {
			oldGLPKSolEnv = new HashMap<String, Integer>(GLPKSolEnv);
			changePolicies(GLPKSolEnv);
			GLPKSolEnv = solveIPOnce();
			cpt++;
			ae = this.areEqual(GLPKSolEnv); // x=f(x) ?
			me = myEquals(GLPKSolEnv, oldGLPKSolEnv); // old = new env?
			stop = ae | me;
			if (nbIt++ >= 20)
				stop = true;
		}
		int nbBits = 0;

		/*******************************
		 * 
		 * Iterator<String> it2Vars; if (costFun == 0) { it2Vars = accVars.iterator(); }
		 * else { it2Vars = AbstractClass.accVarsAssignedOnly.iterator(); } while
		 * (it2Vars.hasNext()) { String itn = it2Vars.next();
		 * 
		 *******************************/

		Iterator<String> iterator2 = AbstractClass.accVars.iterator();
		int nbAccB = 0;
		int nbBitsIEEE754 = 0;
		int fp16 = 0, fp32 = 0, fp64 = 0, fp128 = 0, fpxx = 0;
		ArrayList<String> accVars2 = new ArrayList<String>();
		int valMaxCF1 = 0;

		Map<String, Integer> uniformEnv = new HashMap<String, Integer>();

//		if (AbstractClass.anaMode == 0) {
//			AbstractClass.prefix = new String("ulp_");
//
//		} else {
		AbstractClass.prefix = new String("accb_");
		// }
		while (iterator2.hasNext()) {
			String s = iterator2.next();
			if (s.startsWith(AbstractClass.prefix)) {

				int nn = 0;
				if (AbstractClass.arraySize2.containsKey(AbstractClass.acc2Id.get(s))) {
					nn = AbstractClass.arraySize2.get(AbstractClass.acc2Id.get(s));
					AbstractClass.arraySize2.put(AbstractClass.acc2Id.get(s), 0);
				} else {
					nn = 1;
				}
				nbAccB++;
				int val = AbstractClass.resultEnv.get(s);

				if (AbstractClass.uniformMode == 1) {
					String ss = AbstractClass.acc2Id.get(s);
					if (uniformEnv.containsKey(ss)) {
						int v = uniformEnv.get(ss);
						if (val > v) {
							uniformEnv.put(ss, val);
						} else
							val = v;
					} else {
						uniformEnv.put(ss, val);
					}
				}

				nbBits += val * nn;
				if (val > valMaxCF1)
					valMaxCF1 = val;
				if (!accVars2.contains(s)) {
					accVars2.add(s);
					if ((val <= 11) && (val > 0)) {
						fp16 += nn;
						nbBitsIEEE754 += 11 * nn;
					} else if ((val <= 24) && (val > 0)) {
						fp32 += nn;
						nbBitsIEEE754 += 24 * nn;
					} else if ((val <= 53) && (val > 0)) {
						fp64 += nn;
						nbBitsIEEE754 += 53 * nn;
					} else if ((val <= 113) && (val > 0)) {
						fp128 += nn;
						nbBitsIEEE754 += 113 * nn;
					} else if ((val > 113)) {
						fpxx += nn;
						nbBitsIEEE754 += val;
					}
					;

				}

			}
		}

		// for cost function 3
		int totalBitsOp = 0;
		Iterator<String> iterator3 = AbstractClass.opLabList.iterator();
		while (iterator3.hasNext()) {
			String s = iterator3.next();
			if (AbstractClass.arraySize2.containsKey("accb_lab" + s)) {
				totalBitsOp += AbstractClass.resultEnv.get(AbstractClass.prefix + "lab" + s);
			}
		}

		// for cost function 2
		int nbCasts = 0;
		if (AbstractClass.costFunction == 2) {
			Iterator<String> iterator4 = AbstractClass.z3ConstInt.iterator();
			while (iterator4.hasNext()) {
				String s = iterator4.next();
				if (s.startsWith("accb_lab_w")) {
					if (AbstractClass.resultEnv.get(s) != 0) {
						nbCasts++;
					}
					;
					System.out.println("s  =  " + AbstractClass.resultEnv.get(s));
				}
			}
		}

		// *******************************************************
		// affichage resultats
		// *******************************************************
		System.out.println("Number of policy iterations                               : " + cpt);
		System.out.println("Number of GLPK variables                                  : " + AbstractClass.nbGLPKVars);
		System.out.println("Number of GLPK constraints                                : " + AbstractClass.nbGLPKRows);
		System.out.println("Total number of bits before optimization                  : " + nbAccB * 53);
		System.out.println("Total number of bits after optimization (bit level)       : " + nbBits + " "
				+ (int) (100.0 - (nbBits * 100.0) / (nbAccB * 53)) + " %");
		System.out.println("Total number of bits after optimization (IEEE754 formats) : " + nbBitsIEEE754 + " "
				+ (int) (100.0 - (nbBitsIEEE754 * 100.0) / (nbAccB * 53)) + " %");
		System.out.println("Largest accuracy after optimization                       : " + valMaxCF1);
		System.out.println("Number of FP16                                            : " + fp16);
		System.out.println("Number of FP32                                            : " + fp32);
		System.out.println("Number of FP64                                            : " + fp64);
		System.out.println("Number of FP128                                           : " + fp128);
		System.out.println("Number of FPXX                                            : " + fpxx);
		System.out.println(
				"Number of bits for operators before optimization          : " + (AbstractClass.opLabList.size() * 53));
		System.out.println("Number of bits for operators after optimization           : " + totalBitsOp + " "
				+ (int) (100.0 - totalBitsOp * 100. / (AbstractClass.opLabList.size() * 53)) + "%");
		if (AbstractClass.costFunction == 2) {
			System.out.println("Number of casts                                           : " + nbCasts);
		}
	}

	public void changePolicies(Map<String, Integer> env) {
		Iterator<ConstraintsSet> iterator = Constraints.allCst.iterator();
		while (iterator.hasNext()) {
			ConstraintsSet cc = iterator.next();
			if (!((Constraints) cc).isEqual(env)) {
				((Constraints) cc).changePolicy(env);
			}
		}

	}

}