package Constraint;

import java.util.*;
import org.gnu.glpk.*;
import Number.*;

public class CstrId extends ExpressionConstraint {

	String name;

	public CstrId(String nom) {
		name = nom;
		lab = newLabCstr();
	}

	public String toString() {
		return name;

	}

	public String print() {
		return name;
	}

	public int evaluate(Map<String, Integer> env) {
		int v3;
		v3 = env.get(name);
		return v3;

	}

	public int getVarNumber() {
		return ConstraintsSet.varNumEnv.get(id);
	}

	public void genLPConst() {
		GLPK.glp_set_row_name(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, this.toString());
		GLPK.glp_set_row_bnds(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, GLPKConstants.GLP_FX, 0, 0);
		GLPK.intArray_setitem(ConstraintsSet.ind, 1, lab2VarNum(lab));
		GLPK.intArray_setitem(ConstraintsSet.ind, 2, varName2VarNum(name));
		GLPK.doubleArray_setitem(ConstraintsSet.val, 1, -1);
		GLPK.doubleArray_setitem(ConstraintsSet.val, 2, 1);
		GLPK.glp_set_mat_row(ConstraintsSet.lp, ConstraintsSet.lpConstrCounter, 2, ConstraintsSet.ind,
				ConstraintsSet.val);
		ConstraintsSet.lpConstrCounter++;
	}

	public boolean isCstrConstant() {
		return false;
	}

	public Numbers getCstrConstant() {
		return null;
	}

	public void simGenLPConst() {
		ConstraintsSet.lpConstrCounter += 2;

	}

}
