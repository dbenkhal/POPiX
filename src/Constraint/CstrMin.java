package Constraint;

import java.util.*;
import Number.*;

public class CstrMin extends ExpressionConstraint {

	ExpressionConstraint left;
	ExpressionConstraint right;
	int activeConstr;

	public CstrMin(ExpressionConstraint lt, ExpressionConstraint rt) {
		left = lt;
		right = rt;
		lab = newLabCstr();
		activeConstr = 1;
	}

	public String toString() {
		return "min(" + left.toString() + "," + right.toString() + ")";

	}

	public String print() {
		return ("(ite (< " + left.print() + " " + right.print() + ") " + left.print() + " " + right.print() + ")");

	}

	public int evaluate(Map<String, Integer> env) {
		int v1 = left.evaluate(env);
		int v2 = right.evaluate(env);
		return Integer.min(v1, v2);
	};

	public boolean isCstrConstant() {
		return false;
	}

	public Numbers getCstrConstant() {
		return null;
	}

	public void genLPConst() {
		if (activeConstr == 1)
			left.genLPConst();
		else
			right.genLPConst();
	}

	public void simGenLPConst() {
		left.simGenLPConst();
		right.simGenLPConst();

	}

	public void changePolicy(HashMap<String, Integer> GLPKSolEnv, int obj) {
		activeConstr = (activeConstr + 1);
	}

}
