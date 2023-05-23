package Constraint;

import java.util.*;
import org.gnu.glpk.*;
import Number.*;

public class CstrConst extends ExpressionConstraint {

	int i;
	Numbers x;

	public CstrConst(int u) {
		i = u;
		x = new DoubleNumbers(u);
		lab = newLabCstr();

	}

	public CstrConst(Numbers x2) {
		x = x2;
		lab = newLabCstr();

	}

	public String toString() {
		return i + "";
	}

	public String print() {
		if (i >= 0)
			return i + "";
		else
			return ("(- 0 " + Math.abs(i) + ")");
	}

	public int evaluate(Map<String, Integer> env) {
		return x.getIntValue();
	};

	public ExpressionConstraint setPolicies(String id) {
		return this;
	}

	public Numbers getCoef() {
		return x;
	}

	public void genLPConst() {
		double val;
		if (x == null) {
			val = (double) i;
		} else {
			val = x.getDoubleValue();
		}
		GLPK.glp_set_row_name(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, this.toString());
		GLPK.glp_set_row_bnds(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, GLPKConstants.GLP_FX, val, val);
		GLPK.intArray_setitem(ConstraintsSet.ind, 1, lab2VarNum(lab));
		GLPK.doubleArray_setitem(ConstraintsSet.val, 1, 1.);
		GLPK.glp_set_mat_row(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, 1, ConstraintsSet.ind,
				ConstraintsSet.val);
		ConstraintsSet.lpConstrCounter++;
	}

	public boolean isCstrConstant() {
		return true;
	}

	public Numbers getCstrConstant() {
		return x;
	}

	public void simGenLPConst() {
		ConstraintsSet.lpConstrCounter++;

	}

}
