package Constraint;

import java.util.*;
import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import Number.Numbers;

public class CstrXi extends ExpressionConstraint {

	// min(max(left,0),max(right,0),1)
	Boolean policyFixed = false;
	ExpressionConstraint left;
	ExpressionConstraint right;
	int activeConstr;

	public CstrXi(ExpressionConstraint lt, ExpressionConstraint rt) {
		left = lt;
		right = rt;
		lab = newLabCstr();
		activeConstr = 1;
	}

	public String toString() {
		return "xi(" + left.toString() + "," + right.toString() + ")";

	}

	public String print() {
		return ("(xi (< " + left.print() + " " + right.print() + ") " + left.print() + " " + right.print() + ")");

	}

	public int evaluate(Map<String, Integer> env) {
		int l = left.evaluate(env);
		int r = right.evaluate(env);
		int res = Integer.min(Integer.min(Integer.max(l, 0), Integer.max(r, 0)), 1);
		return res;
	}

	public int evaluatePol(Map<String, Integer> env) {
		switch (activeConstr) {
		case 1: {
			return 0;
		}
		case 2: {
			return 1;
		}
		case 3: {
			return left.evaluate(env);
		}
		default: {
			return right.evaluate(env);
		}
		}
	}

	public boolean isCstrConstant() {
		return false;
	}

	public Numbers getCstrConstant() {
		return null;
	}

	public void genLPConst() {
		switch (activeConstr) {
		case 1: {
			GLPK.glp_set_row_name(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, this.toString());
			GLPK.glp_set_row_bnds(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, GLPKConstants.GLP_FX, 0, 0);
			GLPK.intArray_setitem(ConstraintsSet.ind, 1, lab2VarNum(lab));
			GLPK.doubleArray_setitem(ConstraintsSet.val, 1, 1.);
			GLPK.glp_set_mat_row(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, 1, ConstraintsSet.ind,
					ConstraintsSet.val);
			ConstraintsSet.lpConstrCounter++;
			break;
		}
		case 2: {
			GLPK.glp_set_row_name(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, this.toString());
			GLPK.glp_set_row_bnds(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, GLPKConstants.GLP_FX, 1, 1);
			GLPK.intArray_setitem(ConstraintsSet.ind, 1, lab2VarNum(lab));
			GLPK.doubleArray_setitem(ConstraintsSet.val, 1, 1.);
			GLPK.glp_set_mat_row(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, 1, ConstraintsSet.ind,
					ConstraintsSet.val);
			ConstraintsSet.lpConstrCounter++;
			break;
		}
		case 3: {
			GLPK.glp_set_row_name(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, this.toString());
			GLPK.glp_set_row_bnds(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, GLPKConstants.GLP_FX, 0, 0);
			GLPK.intArray_setitem(ConstraintsSet.ind, 1, lab2VarNum(left.lab));
			GLPK.intArray_setitem(ConstraintsSet.ind, 2, lab2VarNum(this.lab));
			GLPK.doubleArray_setitem(ConstraintsSet.val, 1, -1.);
			GLPK.doubleArray_setitem(ConstraintsSet.val, 2, 1.);
			GLPK.glp_set_mat_row(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, 2, ConstraintsSet.ind,
					ConstraintsSet.val);
			ConstraintsSet.lpConstrCounter++;
			left.genLPConst();
			break;
		}
		default: {
			GLPK.glp_set_row_name(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, this.toString());
			GLPK.glp_set_row_bnds(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, GLPKConstants.GLP_FX, 0, 0);
			GLPK.intArray_setitem(ConstraintsSet.ind, 1, lab2VarNum(right.lab));
			GLPK.intArray_setitem(ConstraintsSet.ind, 2, lab2VarNum(this.lab));
			GLPK.doubleArray_setitem(ConstraintsSet.val, 1, -1.);
			GLPK.doubleArray_setitem(ConstraintsSet.val, 2, 1.);
			GLPK.glp_set_mat_row(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, 2, ConstraintsSet.ind,
					ConstraintsSet.val);
			ConstraintsSet.lpConstrCounter++;
			right.genLPConst();
			break;
		}
		}

	}

	public void simGenLPConst() {
		switch (activeConstr) {
		case 1: {
			ConstraintsSet.lpConstrCounter++;
		}
		case 2: {
			ConstraintsSet.lpConstrCounter++;
		}
		case 3: {
			ConstraintsSet.lpConstrCounter++;
			left.simGenLPConst();
		}
		default: {
			ConstraintsSet.lpConstrCounter++;
			right.simGenLPConst();
		}
		}
	}

	public void changePolicy(Map<String, Integer> GLPKSolEnv, int obj) {
		int valEc = evaluatePol(GLPKSolEnv);
		while (valEc != obj) {
			activeConstr = (activeConstr + 1);
			valEc = evaluate(GLPKSolEnv);
		}
		policyFixed = true;
	}

}
