package Constraint;

import java.util.*;
import Number.*;

public class CstrMin4 extends ExpressionConstraint {

	int activeConstr;
	ExpressionConstraint constr1, constr2, constr3, constr4;

	public CstrMin4(ExpressionConstraint e1, ExpressionConstraint e2, ExpressionConstraint e3,
			ExpressionConstraint e4) {

		constr1 = e1;
		constr2 = e2;
		constr3 = e3;
		constr4 = e4;
		activeConstr = 1;
		lab = newLabCstr();

	}

	public String toString() {
		return "min(" + constr1.toString() + "," + constr2.toString() + "," + constr3.toString() + ","
				+ constr4.toString() + ")";

	}

	public String print() {
		return "not yet implemented";
	}

	public int evaluate(Map<String, Integer> env) {
		int v1 = constr1.evaluate(env);
		int v2 = constr2.evaluate(env);
		int v3 = constr3.evaluate(env);
		int v4 = constr4.evaluate(env);

		return Integer.min(Integer.min(v1, v2), Integer.min(v3, v4));
	};

	public boolean isCstrConstant() {
		return false;
	}

	public Numbers getCstrConstant() {
		return null;
	}

	public void genLPConst() {
		if (activeConstr == 1)
			constr1.genLPConst();
		else if (activeConstr == 2)
			constr2.genLPConst();
		else if (activeConstr == 3)
			constr3.genLPConst();
		else
			constr4.genLPConst();
	}

	public void simGenLPConst() {
		constr1.simGenLPConst();
		constr2.simGenLPConst();
		constr3.simGenLPConst();
		constr4.simGenLPConst();
	}

	ExpressionConstraint getActiveConstr() {
		ExpressionConstraint res = constr1;
		switch (activeConstr) {
		case 1: {
			res = constr1;
			break;
		}
		case 2: {
			res = constr2;
			break;
		}
		case 3: {
			res = constr3;
			break;
		}
		case 4: {
			res = constr4;
			break;
		}
		default: {
			System.out.println("Should not happen in getActiveConst.");
			System.exit(0);
		}
		}
		return res;
	}

	public void changePolicy(HashMap<String, Integer> GLPKSolEnv, int obj) {
		ExpressionConstraint c = getActiveConstr();
		int valC = c.evaluate(GLPKSolEnv);
		while (valC != obj) {
			activeConstr = activeConstr + 1;
			if (activeConstr == 5)
				activeConstr = 1;
			c = getActiveConstr();
			valC = c.evaluate(GLPKSolEnv);
		}
	}

}
