package Constraint;

import java.util.*;
import org.gnu.glpk.*;
import Number.*;

public class CstrDivide extends ExpressionConstraint {

	ExpressionConstraint cleft;
	ExpressionConstraint cright;

	public CstrDivide(ExpressionConstraint cl, ExpressionConstraint cr) {
		cleft = cl;
		cright = cr;
		lab = newLabCstr();

	}

	public String toString() {
		return cleft.toString() + "/" + cright.toString();
	}

	public String print() {
		return ("(/ " + cleft.print() + " " + cright.print() + ")");
	}

	public int evaluate(Map<String, Integer> env) {
		int v1 = cleft.evaluate(env);
		int v2 = cright.evaluate(env);
		return v1 / v2;
	};

	public void genLPConst() {
		GLPK.glp_set_row_name(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, this.toString());
		GLPK.glp_set_row_bnds(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, GLPKConstants.GLP_FX, 0, 0);
		if (cleft.isCstrConstant()) {
			GLPK.intArray_setitem(ConstraintsSet.ind, 1, lab2VarNum(cright.lab));
			GLPK.intArray_setitem(ConstraintsSet.ind, 2, lab2VarNum(this.lab));
			GLPK.doubleArray_setitem(ConstraintsSet.val, 1,
					new IntNumbers(-1).multiply(cleft.getCstrConstant()).getDoubleValue());
			GLPK.doubleArray_setitem(ConstraintsSet.val, 2, 1.);
			GLPK.glp_set_mat_row(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, 2, ConstraintsSet.ind,
					ConstraintsSet.val);
			ConstraintsSet.lpConstrCounter++;
			cright.genLPConst();
		} else {
			if (!cright.isCstrConstant()) {
				System.exit(0);
			}
		}
		GLPK.intArray_setitem(ConstraintsSet.ind, 1, lab2VarNum(cleft.lab));
		GLPK.intArray_setitem(ConstraintsSet.ind, 2, lab2VarNum(this.lab));
		GLPK.doubleArray_setitem(ConstraintsSet.val, 1,
				new IntNumbers(-1).multiply(cright.getCstrConstant()).getDoubleValue());
		GLPK.doubleArray_setitem(ConstraintsSet.val, 2, 1.);
		GLPK.glp_set_mat_row(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, 2, ConstraintsSet.ind,
				ConstraintsSet.val);
		ConstraintsSet.lpConstrCounter++;
		cleft.genLPConst();

	}

	public boolean isCstrConstant() {
		return false;
	}

	public Numbers getCstrConstant() {
		return null;
	}

	public void simGenLPConst() {
		cleft.simGenLPConst();
		cright.simGenLPConst();
		ConstraintsSet.lpConstrCounter++;

	}

}
