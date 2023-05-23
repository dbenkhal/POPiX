package Constraint;

import java.util.*;
import Number.*;
import org.gnu.glpk.*;

public class CstrLte extends ExpressionConstraint {

	ExpressionConstraint cleft;
	ExpressionConstraint cright;

	public CstrLte(ExpressionConstraint cl, ExpressionConstraint cr) {
		cleft = cl;
		cright = cr;
		lab = newLabCstr();

	}

	public String toString() {
		return "(<=" + cleft.toString() + "," + cright.toString() + ")";
	}

	public String print() {
		return ("(<= " + cleft.print() + " " + cright.print() + ")");

	}

	public int evaluate(Map<String, Integer> env) {
		return 0;

	}

	public void genLPConst() {
		GLPK.glp_set_row_name(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, this.toString());
		GLPK.glp_set_row_bnds(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, GLPKConstants.GLP_DB, 0,
				Double.MAX_VALUE);
		GLPK.intArray_setitem(ConstraintsSet.ind, 1, lab2VarNum(cleft.lab));
		GLPK.intArray_setitem(ConstraintsSet.ind, 2, lab2VarNum(cright.lab));
		GLPK.doubleArray_setitem(ConstraintsSet.val, 1, -1);
		GLPK.doubleArray_setitem(ConstraintsSet.val, 2, 1);
		GLPK.glp_set_mat_row(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, 2, ConstraintsSet.ind,
				ConstraintsSet.val);
		ConstraintsSet.lpConstrCounter++;
		cleft.genLPConst();
		cright.genLPConst();

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