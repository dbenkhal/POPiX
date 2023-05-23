package Constraint;

import java.util.*;
import Number.*;

public class CstrIota extends ExpressionConstraint {

	ExpressionConstraint cleft;
	ExpressionConstraint cright;

	public CstrIota(ExpressionConstraint cl, ExpressionConstraint cr) {
		cleft = cl;
		cright = cr;
		lab = newLabCstr();

	}

	public String toString() {
		return "iota(" + cleft.toString() + "," + cright.toString() + ")";
	}

	public String print() {
		return ("(ite" + "(> " + cleft.print() + " " + cright.print() + ") 0 1)");

	}

	public int evaluate(Map<String, Integer> env) {
		return 0;

	}

	public void genLPConst() {

	}

	public boolean isCstrConstant() {
		return false;
	}

	public Numbers getCstrConstant() {
		return null;
	}

	public void simGenLPConst() {

	}

}
